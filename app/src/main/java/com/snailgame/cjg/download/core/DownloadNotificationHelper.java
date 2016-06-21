/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snailgame.cjg.download.core;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.snailgame.cjg.R;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.NotificationUtils;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class handles the updating of the Notification Manager for the
 * cases where there is an ongoing download. Once the download is complete
 * (be it successful or unsuccessful) it is no longer the responsibility
 * of this component to show the download in the notification manager.
 */
public class DownloadNotificationHelper {

    private volatile Context mContext;
    private SystemFacade mSystemFacade;
    private Map<Long, NotificationCompat.Builder> builderMap
            = new HashMap<>();
    private static final long NOTIFICATION_PENDING_ID = 290;

    private List<String> mPendingList = new ArrayList<>();       // 下载等待中列表

    /**
     * Constructor
     *
     * @param ctx The context to use to obtain access to the Notification Service
     */
    DownloadNotificationHelper(Context ctx, SystemFacade systemFacade) {
        mContext = ctx;
        mSystemFacade = systemFacade;
    }

    public void updateWith(Collection<DownloadInfo> downloads, NotificationUtils.DownLoadNotifyIcon downLoadNotifyIcon) {
        synchronized (this) {
            updateWithLocked(downloads, downLoadNotifyIcon);
        }
    }

    /*
     * Update the notification ui.
     */
    private void updateWithLocked(Collection<DownloadInfo> downloads, NotificationUtils.DownLoadNotifyIcon downLoadNotifyIcon) {
        if (downloads.isEmpty()) return;

        DownloadInfo[] downloadInfoArr = new DownloadInfo[downloads.size()];
        downloads.toArray(downloadInfoArr);

        //check if the auto install function is available
        boolean isAutoInstallAvailable = ApkInstaller.isAutoInstallAvailable(mContext);

        // Add the notifications
        DownloadInfo downloadInfo;
        for (int index = 0; index < downloadInfoArr.length; index++) {
            downloadInfo = downloadInfoArr[index];
            if (downloadInfo == null) continue;

            if (downloadInfo.mAppPkgName.equals(AppConstants.APP_PACKAGE_NAME))
                continue;       // 免商店更新进度条不在通知栏显示

            if (TextUtils.equals(downloadInfo.cAppType, AppConstants.VALUE_TYPE_SKIN))
                continue;// 如果是皮肤，则不需要发送广播

            int tickerRes = -1;
            int contentTxtRes = -1;
            int oldStatus;
            int newStatus = downloadInfo.mStatus;
            try {
                oldStatus = Integer.parseInt(downloadInfo.mExtras);
            } catch (NumberFormatException e) {
                //
                oldStatus = -1;
            }

            if (newStatus != Downloads.STATUS_RUNNING) {
                if (oldStatus == newStatus) continue;
            }

            NotificationCompat.Builder builder = builderMap.get(downloadInfo.mId);

            if (newStatus == Downloads.STATUS_PENDING || newStatus == Downloads.STATUS_PENDING_FOR_WIFI) { // 等待中
                if (builder != null) {          // 删除
                    mSystemFacade.cancelNotification(downloadInfo.mId);
                    builderMap.remove(downloadInfo.mId);
                }

                if (mPendingList.contains(downloadInfo.mAppPkgName))
                    continue;

                mPendingList.add(downloadInfo.mAppPkgName);
                updatePendingNotify();
                continue;
            } else {
                if (mPendingList.contains(downloadInfo.mAppPkgName)) {
                    mPendingList.remove(downloadInfo.mAppPkgName);
                    updatePendingNotify();
                }
            }

            if (builder == null) {
                builder = new NotificationCompat.Builder(mContext);
                NotificationUtils.setIcon(mContext, builder);
                builder.setWhen(mSystemFacade.currentTimeMillis());
                builder.setContentTitle(downloadInfo.mTitle == null ? "-" : downloadInfo.mTitle);

                Intent intent = new Intent(DownloadManager.ACTION_NOTIFICATION_CLICKED);
                intent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
                intent.setData(downloadInfo.getAllDownloadsUri());
                builder.setContentIntent(PendingIntent.getBroadcast(mContext, (int) downloadInfo.mId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.notify_progress_layout);
                DateFormat df = new SimpleDateFormat("HH:mm");
                remoteViews.setTextViewText(R.id.time_tv, df.format(new Date()));
                remoteViews.setTextViewText(R.id.title_tv, downloadInfo.mTitle == null ? "-" : downloadInfo.mTitle);
                downLoadNotifyIcon.onLoadIcon(mContext, downloadInfo.appIconUrl, remoteViews);
                builder.setContent(remoteViews);
                builderMap.put(downloadInfo.mId, builder);
            }

            //如果正在下载中则设置通知不可取消
            if (newStatus == Downloads.STATUS_RUNNING)
                builder.setOngoing(true);
            else
                builder.setOngoing(false);

            boolean isAutoInstallCancel = false;
            switch (newStatus) {
                case Downloads.STATUS_RUNNING:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.VISIBLE);
                    if (Downloads.isStatusError(oldStatus)) {
                        tickerRes = R.string.notification_download_ticker_redownload;
                    } else {
                        if (oldStatus == Downloads.STATUS_PAUSED_BY_APP
                                || oldStatus == Downloads.STATUS_WAITING_TO_RETRY
                                || oldStatus == Downloads.STATUS_WAITING_FOR_NETWORK
                                || oldStatus == Downloads.STATUS_QUEUED_FOR_WIFI
                                || ((oldStatus == Downloads.STATUS_PENDING || oldStatus == Downloads.STATUS_PENDING_FOR_WIFI)
                                && downloadInfo.mCurrentBytes > 0L)) {
                            tickerRes = R.string.notification_download_ticker_continue;
                        } else if (oldStatus == -1 || oldStatus == Downloads.STATUS_PENDING || oldStatus == Downloads.STATUS_PENDING_FOR_WIFI) {
                            tickerRes = R.string.notification_download_ticker_start;
                        }
                    }
                    contentTxtRes = R.string.notification_download_state_running;
                    break;
                case Downloads.STATUS_PAUSED_BY_APP:
                case Downloads.STATUS_WAITING_TO_RETRY:
                case Downloads.STATUS_WAITING_FOR_NETWORK:
                case Downloads.STATUS_QUEUED_FOR_WIFI:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.VISIBLE);
                    tickerRes = R.string.notification_download_ticker_pause;
                    contentTxtRes = R.string.notification_download_state_pausing;
                    break;
                case Downloads.STATUS_SUCCESS:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.GONE);
                    if (!isAutoInstallAvailable && !ApkInstaller.isSystemApp()) {
                        tickerRes = R.string.notification_download_ticker_success;
                        contentTxtRes = R.string.notification_download_state_success;
                        //openInstallPanel(downloadInfo.mHint);
                        buildInstallNotification(downloadInfo, builder);
                    } else {
                        isAutoInstallCancel = oldStatus == Downloads.STATUS_INSTALLING
                                && PackageInfoUtil.getPackageInfoByName(mContext, downloadInfo.mAppPkgName) != null;
                        if (isAutoInstallCancel) {
                            tickerRes = R.string.notification_download_ticker_installed;
                            contentTxtRes = R.string.notification_download_state_installed;
                        } else {
                            tickerRes = -1;
                            contentTxtRes = -1;
                        }
                        buildInstalledCompletedNotification(downloadInfo, builder);
                    }
                    break;
                case Downloads.STATUS_INSTALLING:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.GONE);
                    tickerRes = R.string.notification_download_ticker_installing;
                    contentTxtRes = R.string.installing;
                    break;
                case Downloads.STATUS_PATCHING:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.GONE);
                    tickerRes = R.string.notification_download_ticker_patching;
                    contentTxtRes = R.string.patching;
                    break;
                default:
                    builder.build().contentView.setViewVisibility(R.id.progress, View.GONE);
//                    if (newStatus == Downloads.STATUS_FILE_ERROR) {
//                        /*
//                            手动删除APK， 不再提示下载文件未找到
//                         */
//                        //tickerRes = R.string.error_msg_download_file_not_found;
//                        return;
//                    } else {
                    tickerRes = R.string.notification_download_ticker_fail;
//                    }
                    contentTxtRes = R.string.notification_download_state_fail;
                    break;
            }
            if (tickerRes != -1) {
                builder.setTicker(String.format(ResUtil.getString(tickerRes),
                        downloadInfo.mTitle == null ? "-" : downloadInfo.mTitle));
            }
            if (contentTxtRes != -1) {
                builder.build().contentView.setTextViewText(R.id.content_tv, mContext.getString(contentTxtRes));
            }

            if (newStatus == Downloads.STATUS_PENDING || newStatus == Downloads.STATUS_PENDING_FOR_WIFI || newStatus == Downloads.STATUS_RUNNING) {
                long totalBytes = downloadInfo.mTotalBytes == -1 ? downloadInfo.mTotalBytesDefault : downloadInfo.mTotalBytes;
                if (newStatus == Downloads.STATUS_PENDING || newStatus == Downloads.STATUS_PENDING_FOR_WIFI) {
                    builder.build().contentView.setProgressBar(R.id.progress, 0, 0, true);
                } else {
                    builder.build().contentView.setProgressBar(R.id.progress, (int) totalBytes, (int) downloadInfo.mCurrentBytes, totalBytes == -1);
                }
                builder.build().contentView.setTextViewText(R.id.percent_tv, buildPercentageLabel(mContext, totalBytes, downloadInfo.mCurrentBytes));
            } else {
                builder.build().contentView.setTextViewText(R.id.percent_tv, "");
            }

            // send notification
            // Lars Vogel
            mSystemFacade.postNotification(downloadInfo.mId, builder.build());

            // update the status value in db when the download status has been changed
            if (oldStatus != newStatus) {
                ContentValues values = new ContentValues();
                values.put(Downloads.COLUMN_NOTIFICATION_EXTRAS, newStatus);
                mContext.getContentResolver().update(downloadInfo.getAllDownloadsUri(), values, null, null);
                downloadInfo.mExtras = String.valueOf(newStatus);
            }

            if (isAutoInstallAvailable && newStatus == Downloads.STATUS_SUCCESS && !isAutoInstallCancel) {
                mSystemFacade.cancelNotification(downloadInfo.mId);
                builderMap.remove(downloadInfo.mId);
            }
        }
    }

    /**
     * 更新通知栏 等待中 通知
     */
    private void updatePendingNotify() {
        if (mPendingList == null || mPendingList.size() == 0) {
            mSystemFacade.cancelNotification(NOTIFICATION_PENDING_ID);
            builderMap.remove(NOTIFICATION_PENDING_ID);
            return;
        }

        int pendingAppCount = mPendingList.size();
        NotificationCompat.Builder builder = builderMap.get(NOTIFICATION_PENDING_ID);

        if (builder == null) {
            builder = new NotificationCompat.Builder(mContext)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(mContext.getString(R.string.notification_pending_title, String.valueOf(pendingAppCount)));
            NotificationUtils.setIcon(mContext, builder);
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.notify_pending_layout);
            //手机大于5.0显示的icon为小图标
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                remoteViews.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(
                        ResUtil.getResources(), R.drawable.notification));
            } else {
                remoteViews.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(
                        ResUtil.getResources(), R.drawable.notification));
            }

            builder.setContent(remoteViews);
            //设置通知栏点击的intent
            Intent intent = new Intent(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            intent.setPackage(mContext.getPackageName());
            builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            builderMap.put(NOTIFICATION_PENDING_ID, builder);
        }
        builder.build().contentView.setTextViewText(R.id.title_tv, mContext.getString(R.string.notification_pending_title, String.valueOf(pendingAppCount)));
        mSystemFacade.postNotification(NOTIFICATION_PENDING_ID, builder.build());
    }

    private void buildInstalledCompletedNotification(DownloadInfo downloadInfo, NotificationCompat.Builder builder) {
        Intent openApkIntent = new Intent(DownloadManager.ACTION_OPEN_APK);
        openApkIntent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
        openApkIntent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadInfo.mId);
        openApkIntent.putExtra(DownloadManager.DOWNLOAD_PKG_NAME, downloadInfo.mAppPkgName);
        PendingIntent openApkPendingIntent = PendingIntent.getBroadcast(mContext, (int) downloadInfo.mId, openApkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(openApkPendingIntent);
    }

    private static String buildPercentageLabel(Context context, long totalBytes, long currentBytes) {
        if (totalBytes <= 0) {
            return null;
        } else {
            final int percent = (int) (100 * currentBytes / totalBytes);
            return context.getString(R.string.download_percent, percent);
        }
    }

    private void buildInstallNotification(DownloadInfo downloadInfo, NotificationCompat.Builder builder) {
        Intent installApkIntent = new Intent(DownloadManager.ACTION_INSTALL_APK);
        installApkIntent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
        installApkIntent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadInfo.mId);
        installApkIntent.putExtra(DownloadManager.DOWNLOAD_HINT, downloadInfo.mHint);
        PendingIntent installApkPendingIntent = PendingIntent.getBroadcast(mContext, (int) downloadInfo.mId, installApkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(installApkPendingIntent);
    }

    /**
     * 从pendingList中删除
     *
     * @param pkgName
     */
    public void removeFromPendingList(String pkgName) {
        synchronized (this) {
            if (mPendingList.contains(pkgName)) {
                mPendingList.remove(pkgName);
                updatePendingNotify();
            }
        }
    }

    /**
     * 从builderMap中删除
     *
     * @param notifyId
     */
    public void removeFromBuilderMap(long notifyId) {
        synchronized (this) {
            builderMap.remove(notifyId);
        }
    }
}
