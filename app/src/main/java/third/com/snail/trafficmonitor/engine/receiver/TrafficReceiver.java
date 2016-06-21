package third.com.snail.trafficmonitor.engine.receiver;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;

import com.snailgame.cjg.R;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.manager.TimeTickAlarmManager;
import third.com.snail.trafficmonitor.engine.service.TrafficMonitor;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by lic on 2014/12/15.
 * 所有APP的广播接收类
 */
public class TrafficReceiver extends BroadcastReceiver {
    private final String TAG = TrafficReceiver.class.getSimpleName();
    private App app = new App();
    private boolean tellService = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogWrapper.d("Action: " + action);
        if (!EngineEnvironment.isTrafficEnable()) return;
        switch (action) {
            //网络切换
            case ConnectivityManager.CONNECTIVITY_ACTION:
                Intent netChangeIntent = new Intent(context, TrafficMonitor.class);
                netChangeIntent.setAction(TrafficMonitor.ACTION_NETWORK_CHANGED);
                netChangeIntent.addFlags(4);
                context.startService(netChangeIntent);
                break;
            //关机
            case Intent.ACTION_SHUTDOWN:
            case "com.htc.intent.action.QUICKBOOT_POWEROFF":
                Intent stopMeasure = new Intent(context, TrafficMonitor.class);
                stopMeasure.setAction(TrafficMonitor.ACTION_DEVICE_SHUTDOWN);
                stopMeasure.addFlags(6);
                context.startService(stopMeasure);
                break;
            //应用变动
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REPLACED:
            case Intent.ACTION_PACKAGE_REMOVED:
                String packageName = intent.getDataString().split(":")[1];
                LogWrapper.d(TAG, "action:" + action + " packageName:" + packageName);
                boolean isReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);//intent中带有的是否是更新app的字段
                LogWrapper.d(TAG, "isReplace" + isReplace);

                if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                    if (isReplace) return;
                    getInternetUninstallAppInfo(context, packageName);
                } else {
                    try {
                        getInternetInstallAppInfo(context, packageName, isReplace);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                sendIntent(context);
                break;
            //开机
            case Intent.ACTION_BOOT_COMPLETED:
            case "com.htc.intent.action.QUICKBOOT_POWERON":
                Intent bootIntent = new Intent(context, TrafficMonitor.class);
                bootIntent.setAction(TrafficMonitor.ACTION_BOOT_INIT);
                bootIntent.addFlags(3);
                context.startService(bootIntent);
                break;
            //打点
            case TimeTickAlarmManager.ACTION: //"com.snail.trafficmonitor.engine.TimeTick"
                PowerManager.WakeLock wakeLock = ((PowerManager) context
                        .getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                wakeLock.acquire();
                initTimeTickAlarmManager(context);
                Intent timeTickIntent = new Intent(context, TrafficMonitor.class);
                timeTickIntent.setAction(TrafficMonitor.ACTION_TIME_TICK);
                timeTickIntent.addFlags(7);
                context.startService(timeTickIntent);
                LogWrapper.d(TAG, "TimeTick " + new Date().toString());
                wakeLock.release();
                break;
            case "android.provider.Telephony.SECRET_CODE":
                String code = intent.getData().getHost();
                LogWrapper.e(TAG, "SecretCode: " + code);
                if (code.equals(EngineEnvironment.DEBUG_SECRET_CODE)) {
                    Intent debug = new Intent(Intent.ACTION_VIEW);
                    debug.setDataAndType(Uri.parse("snail://com.snail.trafficmonitor"), "monitor/debug");
                    debug.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(debug);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            default:
                LogWrapper.e(TAG, "BroadCast in default");
                break;
        }
    }

    /**
     * 每次收到整点打点广播后，需要重新设置下一次整点打点广播，但是在4.4下不需要，因为设置了循环的alarm，但是不精准，
     * 4.4以上则可以使用精确时间的alarm。
     *
     * @param context*
     */
    private void initTimeTickAlarmManager(Context context) {
        TimeTickAlarmManager.tryRegister(context);
    }

    public void sendIntent(Context context) {
        if (!tellService) {
            return;
        }

        Intent serviceIntent = new Intent(context, TrafficMonitor.class);
        if (app.getUid() != 0) {
            serviceIntent.setAction(TrafficMonitor.ACTION_APP_ADD);
        } else {
            serviceIntent.setAction(TrafficMonitor.ACTION_APP_REMOVE);
        }
        Bundle arg = new Bundle();
        arg.putParcelable("data", app);
        serviceIntent.putExtras(arg);
        serviceIntent.addFlags(5);
        context.startService(serviceIntent);
    }

    /**
     * @param context
     * @param packageName
     * @param isReplace   判断应用是否有网络权限
     */
    public void getInternetInstallAppInfo(Context context, String packageName, boolean isReplace) throws SQLException {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> list = manager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : list) {
            String[] permissions = info.requestedPermissions;
            String pName = info.packageName;
            if (pName.equals(packageName)) {
                if (permissions == null) {
                    return;
                }
                boolean monitor = false;
                for (String p : permissions) {
                    if (p.equals(Manifest.permission.INTERNET)) {
                        monitor = true;
                        tellService = true;
                        break;
                    }
                }
                app.setAppName(info.applicationInfo.loadLabel(manager).toString());
                app.setPackageName(info.packageName);
                app.setVersionCode(info.versionCode);
                app.setVersionName(info.versionName);
                app.setUid(info.applicationInfo.uid);

                if (isReplace) {
                    AppDao appDao = new AppDao(context);
                    List<App> exist = appDao.query(App.getMasterKey(),
                            packageName);
                    if (exist != null && exist.size() > 0) {
                        App prev = exist.get(0);
                        app.setMonitoring(prev.isMonitoring());
                        app.setDisplay(prev.isDisplay());
                        app.setWifiAccess(prev.isWifiAccess());
                        app.setMobileAccess(prev.isMobileAccess());
                    }
                } else {
                    app.setMonitoring(monitor);
                    // liukai added @20141124 begin
                    app.setDisplay(true);
                    app.setWifiAccess(true);
                    app.setMobileAccess(true);
                    // liukai added @20141124 end
                }
            }
        }
    }

    public void getInternetUninstallAppInfo(Context context, String packageName) {
        LogWrapper.d(TAG, "getInternetUninstallAppInfo");
        tellService = true;

        //TODO 所有的卸载都会走这里，即使没有网络权限的app
        app.setPackageName(packageName);
        app.setAppName(context.getResources().getString(R.string.have_deleted));
    }
}
