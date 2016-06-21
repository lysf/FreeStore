package com.snailgame.cjg.download;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.fastdev.util.LogUtils;

/**
 * Created by liuzl on 14-3-24.
 */
public class InstallService extends IntentService {
    public InstallService() {
        super("InstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
        String fileDest = intent.getStringExtra(DownloadManager.DOWNLOAD_FILE_DEST);
        String pkgName = intent.getStringExtra(DownloadManager.DOWNLOAD_PKG_NAME);
        String notifyTitle = intent.getStringExtra(DownloadManager.DOWNLOAD_NOTIFY_TITLE);
        if (id == 0L
                || TextUtils.isEmpty(fileDest)
                || TextUtils.isEmpty(pkgName)
                || TextUtils.isEmpty(notifyTitle)) {

            LogUtils.e("Fail to auto install apk, id=" + id + ", fileDest=" + fileDest
                    + ", pkgName=" + pkgName + ", notifyTitle=" + notifyTitle);
            return;
        }

        //免商店 更新在 updateProgressDialogActivity 处理
        if (pkgName.equals(AppConstants.APP_PACKAGE_NAME)){
            return;
        }

        ApkInstaller apkInstaller = new ApkInstaller(this, id, fileDest, pkgName);
        apkInstaller.execute();
    }
}