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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.snailgame.cjg.download.ConfigAutoInstallActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.event.SkinDownloadedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.settings.AutoInstallAccessibilityService;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.GameSdkDataUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * Stores information about an individual download.
 * 保存单个的下载任务的相关信息
 */
public class DownloadInfo {
    /**
     * The network is usable for the given download.
     */
    public static final int NETWORK_OK = 1;

    // the following NETWORK_* constants are used to indicates specfic reasons for disallowing a
    // download from using a network, since specific causes can require special handling
    /**
     * There is no network connectivity.
     */
    public static final int NETWORK_NO_CONNECTION = 2;
    /**
     * The download exceeds the maximum size for this network.
     */
    public static final int NETWORK_UNUSABLE_DUE_TO_SIZE = 3;
    /**
     * The download exceeds the recommended maximum size for this network, the user must confirm for
     * this download to proceed without WiFi.
     */
    public static final int NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE = 4;
    /**
     * The current connection is roaming, and the download can't proceed over a roaming connection.
     */
    public static final int NETWORK_CANNOT_USE_ROAMING = 5;
    /**
     * The app requesting the download specific that it can't use the current network connection.
     */
    public static final int NETWORK_TYPE_DISALLOWED_BY_REQUESTOR = 6;
    /**
     * Waiting for wifi.
     */
    public static final int NETWORK_WAIT_FOR_WIFI = 7;
    /**
     * For intents used to notify the user that a download exceeds a size threshold, if this extra
     * is true, WiFi is required for this download size; otherwise, it is only recommended.
     */
    public static final String EXTRA_IS_WIFI_REQUIRED = "isWifiRequired";
    public static final String SUFFIX_PATCHED_APK = ".patched.apk";
    public long mId;
    public String mUri;
    public String mHint;
    public String mFileName;
    public int mControl;
    public int mStatus;
    public long mLastMod;
    //根据该状态显示通知栏中item的不同状态
    public String mExtras;
    public long mTotalBytes;
    public long mCurrentBytes;
    public String mETag;
    public int mAllowedNetworkTypes;
    public String mTitle;
    public int mFuzz;
    public volatile boolean mHasActiveThread;
    public long mAppId;
    public long mBytesWifi;
    public long mBytes3G;
    public String md5;
    public String mAppPkgName;
    public String appIconUrl;
    public long mTotalBytesDefault;
    public boolean isFileDeleted = false;
    public transient int mActiveNetworkType = Constants.NET_WORK_TYPE_NONE;
    private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
    private SystemFacade mSystemFacade;
    private Context mContext;

    public int iFreeArea;
    public String cFreeFlow;
    public String cAppType;
    public int cPatchType;
    public String cDiffUrl;
    public long iDiffSize;
    public String cDiffMd5;

    private DownloadInfo(Context context, SystemFacade systemFacade) {
        mContext = context;
        mSystemFacade = systemFacade;
        mFuzz = Helpers.sRandom.nextInt(1001);
    }

    public Collection<Pair<String, String>> getHeaders() {
        return Collections.unmodifiableList(mRequestHeaders);
    }

    /**
     * 下载结束后发送广播通知进行安装，
     * 由于在多线程中调用该方法，
     * 并且第一次调用该方法的时候会判断用户是否root，并弹出弹框提示用户开启自动安装。
     * 所以需要多线程中调用该方法保持阻塞状态，
     * 提示框为ConfigAutoInstallActivity，该类为SingleInstance，
     * 会将所有打开该Activity的Intent保存起来。并在关闭该Activity时将这些intent以广播的形式发送出去。
     */
    public void sendIntentWhenDownloadCompleted() {
        //如果是皮肤，则不需要发送安装广播和弹框
        if (TextUtils.equals(cAppType, AppConstants.VALUE_TYPE_SKIN)) {
            if (mStatus == Downloads.STATUS_SUCCESS) {
                MainThreadBus.getInstance().post(new SkinDownloadedEvent(this.mFileName, this.mAppPkgName));
            }
            return;
        }
        if (mStatus == Downloads.STATUS_SUCCESS) {
            StaticsUtils.downloadSuccess(mContext, mAppId);
            // 游戏SDK检测应用数据是否需要保存
            GameSdkDataUtil.checkGameId(mContext, mAppId);
            //是否显示提示用户授予root权限
            boolean alertGrantRoot = SharedPreferencesUtil.getInstance().isAlertGrantRoot();
            boolean getRoot = SharedPreferencesUtil.getInstance().isSuperuser();
            //安装流程为
            //1.如果没有授予过root权限，并且未勾选不再提示授予root权限，并且手机root了。弹出提示框提示授予
            //2.如果已经授予root则使用root安装
            //3.如果未授予root则使用系统安装,系统安装不在此处处理在DownloadReceiver中处理
            //4.免商店自更新 不显示 alert
            //5.Much W3D 不显示alert
            if (!ApkInstaller.isSystemApp() && !getRoot && alertGrantRoot && CommandHelper.hasRoot() && (false == mAppPkgName.equals(AppConstants.APP_PACKAGE_NAME))) {
                showAlert(true);
                return;
            } else if (!ApkInstaller.isSystemApp() &&
                    false == CommandHelper.hasRoot() && alertGrantRoot &&
                    false == AutoInstallAccessibilityService.isAccessibilitySettingsOn(mContext) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                    (false == mAppPkgName.equals(AppConstants.APP_PACKAGE_NAME))) {
                showAlert(false);
                return;
            }
        } else {
            StaticsUtils.downloadError(mAppId);
        }

        Intent intent = buildDownloadCompleteIntent();
        mSystemFacade.sendBroadcast(intent);
    }

    private void showAlert(boolean isRootAutoInstall) {
        Intent intent = buildDownloadCompleteIntent();
        intent.putExtra(AppConstants.KEY_IS_ROOT_AUTO_INSTALL, isRootAutoInstall);
        intent.setClass(mContext, ConfigAutoInstallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    private Intent buildDownloadCompleteIntent() {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, mId);
        intent.putExtra(DownloadManager.GAME_ID, mAppId);
        intent.putExtra(DownloadManager.DOWNLOAD_RESULT, mStatus);
        intent.putExtra(DownloadManager.DOWNLOAD_FILE_DEST, mFileName);
        intent.putExtra(DownloadManager.DOWNLOAD_PKG_NAME, mAppPkgName);
        intent.putExtra(DownloadManager.DOWNLOAD_NOTIFY_TITLE, mTitle);
        return intent;
    }

    /**
     * Returns whether this download (which the download manager hasn't seen yet)
     * should be started.
     */
    protected boolean isReadyToStart() {
        if (mHasActiveThread) {
            // already running
            return false;
        }
        if (mControl == Downloads.CONTROL_PAUSED) {
            // the download is paused, so it's not going to start
            return false;
        }

        int activeNetworkType = Constants.NET_WORK_TYPE_NONE;
        try {
            if (mSystemFacade != null && mSystemFacade.getActiveNetworkType() != null) {
                activeNetworkType = mSystemFacade.getActiveNetworkType();
            }
        } catch (NullPointerException e) {

        }

        switch (mStatus) {
            case 0: // status hasn't been initialized yet, this is a new download
            case Downloads.STATUS_PENDING: // download is explicit marked as ready to start
            case Downloads.STATUS_RUNNING: // download interrupted (process killed etc) while
                // running, without a chance to update the database
                return !needToWaitForWifi(activeNetworkType);
            case Downloads.STATUS_PENDING_FOR_WIFI:
                return activeNetworkType == ConnectivityManager.TYPE_WIFI;
            case Downloads.STATUS_WAITING_FOR_NETWORK:
            case Downloads.STATUS_QUEUED_FOR_WIFI:
                if (mStatus == Downloads.STATUS_QUEUED_FOR_WIFI) {
                    return activeNetworkType == ConnectivityManager.TYPE_WIFI;
                }
                return checkCanUseNetwork() == NETWORK_OK;

            case Downloads.STATUS_WAITING_TO_RETRY:
                return true;
            case Downloads.STATUS_PAUSED_BY_APP:
                return mSystemFacade != null && mSystemFacade.getActiveNetworkType() != null && mSystemFacade.getActiveNetworkType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * Returns whether this download has a visible notification after
     * completion.
     */
    public boolean hasCompletionNotification() {
        if (!Downloads.isStatusCompleted(mStatus)) {
            return false;
        }
        return false;
    }

    /**
     * Returns whether this download is allowed to use the network.
     *
     * @return one of the NETWORK_* constants
     */
    public int checkCanUseNetwork() {
        Integer networkType = mSystemFacade.getActiveNetworkType();
        if (networkType == null) {
            return NETWORK_NO_CONNECTION;
        }
        if (needToWaitForWifi(networkType)) {
            return NETWORK_WAIT_FOR_WIFI;
        }
        return checkIsNetworkTypeAllowed(networkType);
    }

    /**
     * @return a non-localized string appropriate for logging corresponding to one of the
     * NETWORK_* constants.
     */
    public String getLogMessageForNetworkError(int networkError) {
        switch (networkError) {
            case NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE:
                return "download size exceeds recommended limit for mobile network";

            case NETWORK_UNUSABLE_DUE_TO_SIZE:
                return "download size exceeds limit for mobile network";

            case NETWORK_NO_CONNECTION:
                return "no network connection available";

            case NETWORK_CANNOT_USE_ROAMING:
                return "download cannot use the current network connection because it is roaming";

            case NETWORK_TYPE_DISALLOWED_BY_REQUESTOR:
                return "download was requested to not use the current network type";

            default:
                return "unknown error with network connectivity";
        }
    }

    /**
     * Check if this download can proceed over the given network type.
     *
     * @param networkType a constant from ConnectivityManager.TYPE_*.
     * @return one of the NETWORK_* constants
     */
    private int checkIsNetworkTypeAllowed(int networkType) {
        int flag = translateNetworkTypeToApiFlag(networkType);
        if ((flag & mAllowedNetworkTypes) == 0) {
            return NETWORK_TYPE_DISALLOWED_BY_REQUESTOR;
        }
        return NETWORK_OK;
    }

    /**
     * Translate a ConnectivityManager.TYPE_* constant to the corresponding
     * DownloadManager.Request.NETWORK_* bit flag.
     */
    private int translateNetworkTypeToApiFlag(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
                return DownloadManager.Request.NETWORK_MOBILE;

            case ConnectivityManager.TYPE_WIFI:
                return DownloadManager.Request.NETWORK_WIFI;

            default:
                return 0;
        }
    }


    synchronized void startIfReady(boolean isFirstCreated) {
        if (!isReadyToStart()) {
            return;
        }
        if (Constants.LOGV) {
            Log.v(Constants.TAG, "Service spawning thread to handle download " + mId);
        }
        if (mStatus != Downloads.STATUS_RUNNING) {
            // limit the max count for the download task list
            if (DownloadHelper.getApkCountByStatus(mContext, DownloadManager.STATUS_RUNNING) >= Downloads.MAX_DOWNLOAD_NUM) {
                mStatus = Downloads.STATUS_PENDING;
            } else {
                mStatus = Downloads.STATUS_RUNNING;
            }
            // update the status of the download task
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_STATUS, mStatus);
            mContext.getContentResolver().update(getAllDownloadsUri(), values, null, null);

            if (isFirstCreated && mCurrentBytes <= 0L) {
                sendIntentWhenFirstCreated();
            }
            return;
        }

        DownloadThread downloader = new DownloadThread(mContext, mSystemFacade, this);
        mHasActiveThread = true;
        mSystemFacade.startThread(downloader);
    }

    public Uri getAllDownloadsUri() {
        return ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, mId);
    }

    public void logVerboseInfo() {
        Log.v(Constants.TAG, "Service adding new entry");
        Log.v(Constants.TAG, "ID      : " + mId);
        Log.v(Constants.TAG, "URI     : " + ((mUri != null) ? "yes" : "no"));
        Log.v(Constants.TAG, "HINT    : " + mHint);
        Log.v(Constants.TAG, "FILENAME: " + mFileName);
        Log.v(Constants.TAG, "CONTROL : " + mControl);
        Log.v(Constants.TAG, "STATUS  : " + mStatus);
        Log.v(Constants.TAG, "LAST_MOD: " + mLastMod);
        Log.v(Constants.TAG, "TOTAL   : " + mTotalBytes);
        Log.v(Constants.TAG, "CURRENT : " + mCurrentBytes);
        Log.v(Constants.TAG, "ETAG    : " + mETag);
    }

    /**
     * Returns the amount of time (as measured from the "now" parameter)
     * at which a download will be active.
     * 0 = immediately - service should stick around to handle this download.
     * -1 = never - service can go away without ever waking up.
     * positive value - service must wake up in the future, as specified in ms from "now"
     */
    long nextAction() {
        if (Downloads.isStatusCompleted(mStatus)) {
            return -1;
        }
        if (mStatus != Downloads.STATUS_WAITING_TO_RETRY) {
            return 0;
        }
        return 0;
    }

    void notifyPauseDueToSize(boolean isWifiRequired) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(getAllDownloadsUri());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_IS_WIFI_REQUIRED, isWifiRequired);
        mContext.startActivity(intent);
    }

    /**
     * send an intent when a new download task first created
     */
    private void sendIntentWhenFirstCreated() {
        Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_START);
        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, mId);
        intent.putExtra(DownloadManager.GAME_ID, mAppId);
        mSystemFacade.sendBroadcast(intent);
    }

    /**
     * Check if need to wait for wifi if the network switch from wifi to mobile
     *
     * @param activeNetworkType
     * @return
     */
    private boolean needToWaitForWifi(int activeNetworkType) {
        boolean result = false;
        if (mActiveNetworkType != activeNetworkType) {
            if (activeNetworkType == Constants.NET_WORK_TYPE_NONE) {
                ContentValues values = new ContentValues();
                values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_QUEUED_FOR_WIFI);
                mContext.getContentResolver().update(getAllDownloadsUri(), values, null, null);
                result = true;
            }
        }
        mActiveNetworkType = activeNetworkType;
        return result;
    }

    public static class Reader {
        private Cursor mCursor;
        private CharArrayBuffer mOldChars;
        private CharArrayBuffer mNewChars;

        public Reader(Cursor cursor) {
            mCursor = cursor;
        }

        public DownloadInfo newDownloadInfo(Context context, SystemFacade systemFacade) {
            DownloadInfo info = new DownloadInfo(context, systemFacade);
            updateFromDatabase(info);
            return info;
        }

        public void updateFromDatabase(DownloadInfo info) {
            info.mId = getLong(Downloads._ID);
            info.mUri = getString(info.mUri, Downloads.COLUMN_URI);
            info.mHint = getString(info.mHint, Downloads.COLUMN_FILE_NAME_HINT);
            info.mFileName = getString(info.mFileName, Downloads._DATA);
            info.mStatus = getInt(Downloads.COLUMN_STATUS);
            info.mLastMod = getLong(Downloads.COLUMN_LAST_MODIFICATION);
            info.mExtras = getString(info.mExtras, Downloads.COLUMN_NOTIFICATION_EXTRAS);
            info.mTotalBytes = getLong(Downloads.COLUMN_TOTAL_BYTES);
            info.mCurrentBytes = getLong(Downloads.COLUMN_CURRENT_BYTES);
            info.mETag = getString(info.mETag, Constants.ETAG);
            info.mAllowedNetworkTypes = getInt(Downloads.COLUMN_ALLOWED_NETWORK_TYPES);
            info.mTitle = getString(info.mTitle, Downloads.COLUMN_TITLE);
            info.mAppId = getInt(Downloads.COLUMN_APP_ID);
            info.mBytesWifi = getInt(Downloads.COLUMN_BYTES_IN_WIFI);
            info.mBytes3G = getInt(Downloads.COLUMN_BYTES_IN_3G);
            info.md5 = getString(info.md5, Downloads.COLUMN_MD5);
            info.mAppPkgName = getString(info.mAppPkgName, Downloads.COLUMN_APP_PKG_NAME);
            info.mTotalBytesDefault = getLong(Downloads.COLUMN_TOTAL_BYTES_DEFAULT);
            info.iFreeArea = getInt(Downloads.COLUMN_FREE_AREA_STATE);
            info.cFreeFlow = getString(info.cFreeFlow, Downloads.COLUMN_FLOW_FREE_STATE_V2);
            info.cAppType = getString(info.cAppType, Downloads.COLUMN_APP_TYPE);
            info.appIconUrl = getString(info.appIconUrl, Downloads.COLUMN_APP_iCON_URL);
            info.cPatchType = getInt(Downloads.COLUMN_PATCH_TYPE);
            info.cDiffUrl = getString(info.cDiffUrl, Downloads.COLUMN_DIFF_URL);
            info.iDiffSize = getLong(Downloads.COLUMN_DIFF_SIZE);
            info.cDiffMd5 = getString(info.cDiffMd5, Downloads.COLUMN_DIFF_MD5);

            if (info.cPatchType == AppConstants.UPDATE_TYPE_PATCH) {
                info.mUri = info.cDiffUrl;
                info.md5 = info.cDiffMd5;
            }

            synchronized (this) {
                info.mControl = getInt(Downloads.COLUMN_CONTROL);
            }
        }

        /**
         * Returns a String that holds the current value of the column, optimizing for the case
         * where the value hasn't changed.
         */
        private String getString(String old, String column) {
            int index = mCursor.getColumnIndexOrThrow(column);
            if (old == null) {
                return mCursor.getString(index);
            }
            if (mNewChars == null) {
                mNewChars = new CharArrayBuffer(128);
            }
            mCursor.copyStringToBuffer(index, mNewChars);
            int length = mNewChars.sizeCopied;
            if (length != old.length()) {
                return new String(mNewChars.data, 0, length);
            }
            if (mOldChars == null || mOldChars.sizeCopied < length) {
                mOldChars = new CharArrayBuffer(length);
            }
            char[] oldArray = mOldChars.data;
            char[] newArray = mNewChars.data;
            old.getChars(0, length, oldArray, 0);
            for (int i = length - 1; i >= 0; --i) {
                if (oldArray[i] != newArray[i]) {
                    return new String(newArray, 0, length);
                }
            }
            return old;
        }

        private Integer getInt(String column) {
            return mCursor.getInt(mCursor.getColumnIndexOrThrow(column));
        }

        private Long getLong(String column) {
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(column));
        }
    }
}
