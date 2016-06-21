package com.snailgame.cjg.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PackageInfoUtil {
    private static String TAG = PackageInfoUtil.class.getSimpleName();

    /**
     * 获取应用第一次安装的时间和大小
     * 如果安装时间为空，再获取应用最后一次修改的时间
     *
     * @return
     */
    public static void getAppInstallTimeAndSize(Context context, PackageInfo pInfo, AppInfo aInfo) {

        // API level 9 and above have the "firstInstallTime" field.
        // Check for it with reflection and return if present.
        long installTime = 0;
        /**
         * 注意：PackageInfo.firstInstallTime方法始终返回为零
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            installTime = pInfo.firstInstallTime;
        }

        File apkFile = new File(pInfo.applicationInfo.sourceDir);

        if (0 == installTime) {
            installTime = apkFile.exists() ? apkFile.lastModified() : 0;
        }
        aInfo.setLastUpdateTime(installTime);
        aInfo.setDataSize(apkFile.length());
    }

    public static boolean deleteApkFromDiskByUri(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtils.w("filePath is empty or null.");
            return false;
        }
        File file = new File(URI.create(filePath));
        if (file.exists()) {
            if (file.delete()) {
                return true;
            }
        }
        return false;
    }

    public static PackageInfo getPackageInfoByName(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        if (context != null && pkgName != null) {
            try {
                //getPackageInfo第二个参数传0表示只获取该包名下的ApplicationInfo信息，
                //传-1表示获取AndroidManifest中配置的所有内容
                packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            } catch (Exception ignore) {
            }
        }
        return packageInfo;
    }

    /**
     * 读取已安装的应用列表
     *
     * @return
     */
    public static ArrayList<InstallGameInfo> getAllInstalledGames(Context context) {
        ArrayList<InstallGameInfo> gameLists = new ArrayList<>();
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            int length = packageInfos.size();
            for (int i = 0; i < length; i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                InstallGameInfo tmpInfo = new InstallGameInfo();
                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                tmpInfo.setPackageName(packageInfo.packageName);
                tmpInfo.setVersionName(packageInfo.versionName);
                tmpInfo.setVersionCode(packageInfo.versionCode);

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    gameLists.add(tmpInfo);
                }
            }
        }
        return gameLists;
    }

}
