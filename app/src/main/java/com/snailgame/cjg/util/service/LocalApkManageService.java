package com.snailgame.cjg.util.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 13-8-28.
 */
public class LocalApkManageService {

    private static final String TAG = LocalApkManageService.class.getSimpleName();
    private static PackageManager mPackageManager = null;

    public static List<AppInfo> getInstalledAppList(Context context) {
        List<AppInfo> installedAppList = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        if (context == null) return installedAppList;

        mPackageManager = context.getPackageManager();
        if (mPackageManager == null) return installedAppList;

        List<PackageInfo> packageList = mPackageManager.getInstalledPackages(0);
        if (ListUtils.isEmpty(packageList)) return installedAppList;
        installedAppList.clear();
        for (PackageInfo packageInfo : packageList) {
            ApplicationInfo app = packageInfo.applicationInfo;
            //installedAppList.add(getAppInfoFromPackageInfo(context,packageInfo));
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0
                    || (app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                installedAppList.add(getAppInfoFromPackageInfo(context, packageInfo));
            } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                // 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                installedAppList.add(getAppInfoFromPackageInfo(context, packageInfo));
            }
        }

        // sorting the installed app list
        //Collections.sort(installedAppList, new AppInfoComparator(AppInfoOrderCriteria.ORDER_BY_NAME));
        return installedAppList;
    }


    private static AppInfo getAppInfoFromPackageInfo(Context context, PackageInfo packageInfo) {

        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (applicationInfo != null) {
            AppInfo appInfo = new AppInfo();

            CharSequence label = applicationInfo.loadLabel(mPackageManager);
            appInfo.setAppName(label == null ? "-" : label.toString());
//            Drawable icon = applicationInfo.loadIcon(mPackageManager);
//            Bitmap bitmap = icon == null ? null : ((BitmapDrawable) icon).getBitmap();
//            appInfo.setAppIcon(bitmap);

            appInfo.setLocalAppVersion(packageInfo.versionName);
            appInfo.setPkgName(applicationInfo.packageName);
            appInfo.setLocalAppVersionCode(packageInfo.versionCode);
            PackageInfoUtil.getAppInstallTimeAndSize(context, packageInfo, appInfo);
            return appInfo;
        }

        return null;
    }

    //list order criteria
    public enum AppInfoOrderCriteria {
        ORDER_BY_NAME, //order by app name
        ORDER_BY_SIZE, //order by app size
        ORDER_BY_TIME //order by app installed time
    }


}
