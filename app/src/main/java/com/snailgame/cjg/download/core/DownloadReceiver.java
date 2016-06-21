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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.InstallService;
import com.snailgame.cjg.downloadmanager.GameManageActivity;
import com.snailgame.cjg.event.NotifyRemoveEvent;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;

/**
 * Receives system broadcasts (boot, network connectivity)
 */
public class DownloadReceiver extends BroadcastReceiver {
    SystemFacade mSystemFacade = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (mSystemFacade == null) {
            mSystemFacade = new RealSystemFacade(context);
        }
        String action = intent.getAction();

        if (TextUtils.isEmpty(action)) return;

        switch (action) {
            //开机启动
            case Intent.ACTION_BOOT_COMPLETED:
                DownloadService.start(context);
                break;
            //网络状态切换 
            case ConnectivityManager.CONNECTIVITY_ACTION:
                ConnectivityManager manager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo == null) return;
                //判断当前网络是否可用，网络可用标准，1.链接 2.可用 3.wifi状态
                if (networkInfo.isConnected()
                        && networkInfo.isAvailable()
                        && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // 当链接为wifi状态时运行开启DownloadService
                    DownloadService.start(context);
                }
                break;
            case Constants.ACTION_LIST:
                handleNotificationBroadcast(context, intent);
                break;
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                int downloadResult = intent.getIntExtra(DownloadManager.DOWNLOAD_RESULT, -1);
                if (downloadResult == Downloads.STATUS_SUCCESS) {
                    installApk(context, intent);
                }
                break;
            case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                context.startActivity(GameManageActivity.newIntent(context, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case DownloadManager.ACTION_OPEN_APK:
                openApkAndCancelNotification(context, intent);
                break;
            case DownloadManager.ACTION_INSTALL_APK:
                installApkAndCancelNotification(context, intent);
                break;
        }
    }

    private void installApk(Context context, Intent intent) {
        Intent installIntent = new Intent(intent);
        installIntent.setAction(null);
        installIntent.setClass(context, InstallService.class);

        context.startService(installIntent);
    }

    /**
     * Handle any broadcast related to a system notification.
     */
    private void handleNotificationBroadcast(Context context, Intent intent) {
        sendNotificationClickedIntent(context);
    }

    /**
     * 点击通知栏信息后打开游戏管理界面
     */
    private void sendNotificationClickedIntent(Context context) {
        Intent appIntent;
        appIntent = new Intent(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        appIntent.setPackage(context.getPackageName());
        mSystemFacade.sendBroadcast(appIntent);
    }

    /**
     * Open the installed apk and remove the notification
     *
     * @param intent
     */
    private void openApkAndCancelNotification(Context context, Intent intent) {
        String appPkgName = intent.getStringExtra(DownloadManager.DOWNLOAD_PKG_NAME);
        if (!TextUtils.isEmpty(appPkgName)) {
            Intent openGameIntent = ComUtil.getLaunchIntentForPackage(context, appPkgName);
            if (openGameIntent != null) context.startActivity(openGameIntent);
        }

        long notifyId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
        if (notifyId != -1) {
            mSystemFacade.cancelNotification(notifyId);
            MainThreadBus.getInstance().post(new NotifyRemoveEvent(notifyId));
        }
    }

    /**
     * Open the install panel and remove the notification
     *
     * @param intent
     */
    private void installApkAndCancelNotification(Context context, Intent intent) {
        String apkHint = intent.getStringExtra(DownloadManager.DOWNLOAD_HINT);
        if (!TextUtils.isEmpty(apkHint)) {
            ApkInstaller.installApk(apkHint);
        }

        long notifyId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
        if (notifyId != -1) {
            mSystemFacade.cancelNotification(notifyId);
            MainThreadBus.getInstance().post(new NotifyRemoveEvent(notifyId));
        }
    }

}
