package third.com.snail.trafficmonitor.engine.util.process;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.AppListCustomData;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * Created by lic on 2014/12/08
 * 进程列表和程序列表的获取和进程杀死和程序卸载
 */
public class InastallAppUtil {
    Context context;
    SpManager spManager;
    boolean hasRoot;

    public InastallAppUtil(Context context) {
        this.context = context;
        spManager = SpManager.getInstance(context);
        hasRoot = CommandHelper.hasRoot();
    }


    /**
     * 获取所有安装的应用列表
     *
     * @return
     */
    public List<AppListCustomData> getInstallAppInfo() {
        List<AppListCustomData> mAppListData = new ArrayList<>();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> list = manager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        for (PackageInfo packageInfo : list) {
            LogWrapper.d("packageInfo.packageName" + packageInfo.packageName);
            AppListCustomData data = new AppListCustomData();
            data.setAppName(packageInfo.applicationInfo.loadLabel(manager).toString());
            data.setAppIcon(packageInfo.applicationInfo.loadIcon(manager));
            data.setPackageName(packageInfo.packageName);
            //判断是否为系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                data.setIsSystem(false);
                mAppListData.add(data);
            } else {
                //判断有没有root
                if (hasRoot && spManager.getBoolean(SpManager.SpData.OPEN_ROOT_ADVANCED_FUNCTION)) {
                    data.setIsSystem(true);
                    mAppListData.add(data);
                }
            }
        }
        return mAppListData;
    }

    /**
     * 获取系统应用列表
     *
     * @return
     */
    public List<AppListCustomData> getSystemInstallAppInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        //获取所有应用程序的安装路径的apk和包名
        // package:/system/priv-app/Dialer.apk=com.android.dialer类似这样的多条数据
        CommandHelper.runCmdAsRoot("pm list packages -f", stringBuilder);
        String[] strings = stringBuilder.toString().split("package:");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].contains("system")) {
                String[] strings1 = strings[i].split("=");
                String[] strings2 = strings1[0].split("/");
                map.put(strings1[1].trim(), strings2[3].trim());
            }
        }
        List<AppListCustomData> mAppListData = new ArrayList<>();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> list = manager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        for (PackageInfo packageInfo : list) {
            AppListCustomData data = new AppListCustomData();
            data.setAppName(packageInfo.applicationInfo.loadLabel(manager).toString());
            data.setAppIcon(packageInfo.applicationInfo.loadIcon(manager));
            data.setPackageName(packageInfo.packageName);
            //判断是否为系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                data.setIsSystem(true);
                data.setApkName(map.get(packageInfo.packageName));
                mAppListData.add(data);
            }
        }
        return mAppListData;
    }

    /**
     * 删除系统应用程序
     */
    public void removeSystemApp(String apkName) {
        StringBuilder rwStringBuilder = new StringBuilder();
        //增加system读写权限
        CommandHelper.runCmdAsRoot("mount -o remount,rw /system", rwStringBuilder);
        LogWrapper.d("rwStringBuilder" + rwStringBuilder.toString());
        StringBuilder removeStringBuilder = new StringBuilder();
        //移除系统应用
        CommandHelper.runCmdAsRoot("rm /system/app/" + apkName, removeStringBuilder);
        LogWrapper.d("removeStringBuilder" + removeStringBuilder.toString());
    }

    /**
     * 删除应用程序
     */
    public void removeInstallApp(String packageName) {
        if (hasRoot && spManager.getBoolean(SpManager.SpData.OPEN_ROOT_ADVANCED_FUNCTION)) {
            StringBuilder stringBuilder = new StringBuilder();
            CommandHelper.runCmdAsRoot("system/bin/pm uninstall " + packageName, stringBuilder);
        } else {
            Uri uri = Uri.fromParts("package",
                    packageName, null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            context.startActivity(intent);
        }
    }

}
