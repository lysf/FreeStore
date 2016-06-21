package com.snailgame.cjg.settings;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.LoginSDKUtil;

/**
 * 防止BBS 登录失效，故25分钟 重新登录一下
 * Created by TAJ_C on 2014/12/23.
 */
public class AutoReLoginBBsService extends IntentService {
    private static final String TAG = "AutoReLoginBBsService";

    private static final int REPEAT_ALARM = 6;
    private static long delayTime;

    public AutoReLoginBBsService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AutoReLoginBBsService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (delayTime != AppConstants.RELOGIN_BBS_TIME) {
            delayTime = AppConstants.RELOGIN_BBS_TIME;
            startReLoginAlarm(delayTime);
        }
        //重新登录
        LoginSDKUtil.snailBBsLogin();
    }

    private void startReLoginAlarm(long interval) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent timerIntent = AutoReLoginBBsService.newIntent(this);
        PendingIntent pi = PendingIntent.getService(this, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis() + interval;
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, pi);
    }

    public static void cancelReLoginAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent timerIntent = AutoReLoginBBsService.newIntent(context);

        PendingIntent pi = PendingIntent.getService(context, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }
}
