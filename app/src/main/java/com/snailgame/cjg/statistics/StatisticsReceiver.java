package com.snailgame.cjg.statistics;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by
 * liuzl on 14-3-19.
 * 统计功能使用的监听启动完成的广播
 */
public class StatisticsReceiver extends BroadcastReceiver {
    //1000*秒
    private static final int PERIOD = 1000 * 60 * 10;
   // private static final int PERIOD = 1000 * 5 ;

    public void onReceive(Context context, Intent intent) {
        scheduleAlarms(context);
    }

    public static void scheduleAlarms(Context ctxt) {
        AlarmManager mgr =
                (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(ctxt, 0, LivenessService.newIntent(ctxt), 0);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + PERIOD, PERIOD, pi);
    }



}
