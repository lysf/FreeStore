package third.com.snail.trafficmonitor.engine.util.process;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.snailgame.cjg.global.GlobalVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lic on 2014/12/08
 * 删选收到一些系统广播的程序
 */
public class ImportantProcessUtil {
    private static final int flags = PackageManager.GET_DISABLED_COMPONENTS;
    private static final String smsaction = "android.provider.Telephony.SMS_RECEIVED";
    private static final String goingcallaction = "android.intent.action.NEW_OUTGOING_CALL";
    private static final String shortcutaction = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    //AppwidgetProvider接收APPWIDGET_UPDATE广播 此为appwidget唯一要声明的广播，（据我理解应该就是判断是不是widget）
    private static final String appwidget = "android.appwidget.action.APPWIDGET_UPDATE";

    public static void initprocess(Context c) {
        List<String> listMainValue;
        List<String> listAppWidgetValue;
        List<ResolveInfo> listMain;
        List<ResolveInfo> listAppWidget;

        PackageManager pm = c.getApplicationContext().getPackageManager();

        //时区改变的广播
        Intent timeZoneIntent = new Intent(Intent.ACTION_TIMEZONE_CHANGED);
        listAppWidget = pm.queryBroadcastReceivers(timeZoneIntent, flags);

        //改变时间广播
        Intent timeIntent = new Intent(Intent.ACTION_TIME_CHANGED);
        listAppWidget.addAll(pm.queryBroadcastReceivers(timeIntent, flags));

        //时间改变的话，每分钟发射广播
        Intent timeTickIntent = new Intent(Intent.ACTION_TIME_TICK);
        listAppWidget.addAll(pm.queryBroadcastReceivers(timeTickIntent, flags));

        //改变日期广播
        Intent dateIntent = new Intent(Intent.ACTION_DATE_CHANGED);
        listAppWidget.addAll(pm.queryBroadcastReceivers(dateIntent, flags));

        //app更新广播
        Intent appwidgetintent = new Intent(appwidget);
        listAppWidget.addAll(pm.queryBroadcastReceivers(appwidgetintent, flags));



        //在顶层的应用
        Intent mainintent = new Intent(Intent.ACTION_MAIN);
        mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
        listMain = pm.queryBroadcastReceivers(mainintent, flags);

        Intent shortcut = new Intent(shortcutaction);
        listMain.addAll(pm.queryBroadcastReceivers(shortcut, flags));

        //接收短信广播
        Intent smsintent = new Intent(smsaction);
        listMain.addAll(pm.queryBroadcastReceivers(smsintent, flags));

        //去电广播
        Intent goingcall = new Intent(goingcallaction);
        listMain.addAll(pm.queryBroadcastReceivers(goingcall, flags));

        Intent changewall;
        changewall = new Intent(Intent.ACTION_WALLPAPER_CHANGED);
        listMain.addAll(pm.queryBroadcastReceivers(changewall, flags));

        //电源连接广播
        Intent powerconnection = new Intent(Intent.ACTION_POWER_CONNECTED);
        listMain.addAll(pm.queryBroadcastReceivers(powerconnection, flags));

        //content provider改变广播
        Intent content = new Intent(Intent.ACTION_PROVIDER_CHANGED);
        listMain.addAll(pm.queryBroadcastReceivers(content, flags));

        //关机广播
        Intent shutdown = new Intent(Intent.ACTION_SHUTDOWN);
        listMain.addAll(pm.queryBroadcastReceivers(shutdown, flags));

        //G-talk建立连接广播
        Intent gtalk = new Intent(Intent.ACTION_GTALK_SERVICE_CONNECTED);
        listMain.addAll(pm.queryBroadcastReceivers(gtalk, flags));

        //设备进入休眠状态
        Intent screenoff = new Intent(Intent.ACTION_SCREEN_OFF);
        listMain.addAll(pm.queryBroadcastReceivers(screenoff, flags));

        //输入法改变
        Intent inputmethod = new Intent(Intent.ACTION_INPUT_METHOD_CHANGED);
        listMain.addAll(pm.queryBroadcastReceivers(inputmethod, flags));

        //用户主动清除程序数据
        Intent packagedata = new Intent(Intent.ACTION_PACKAGE_DATA_CLEARED);
        listMain.addAll(pm.queryBroadcastReceivers(packagedata, flags));

        //设备重启
        Intent reboot = new Intent(Intent.ACTION_REBOOT);
        listMain.addAll(pm.queryBroadcastReceivers(reboot, flags));

        //来电广播
        Intent call = new Intent(Intent.ACTION_CALL);
        listMain.addAll(pm.queryIntentActivities(call, flags));

        //launcher
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_HOME);
        listMain.addAll(pm.queryIntentActivities(launcher, flags));


        listMainValue = new ArrayList<>();
        listAppWidgetValue = new ArrayList<>();

        for (int j = 0; j < listMain.size(); j++) {
            listMainValue.add(listMain.get(j).activityInfo.processName);
        }

        for (int m = 0; m < listAppWidget.size(); m++) {
            listAppWidgetValue.add(listAppWidget.get(m).activityInfo.processName);
        }
        GlobalVar.getInstance().setListAppWidgetValue(listAppWidgetValue);
        GlobalVar.getInstance().setListMainValue(listMainValue);
    }
}
