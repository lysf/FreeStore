package com.snailgame.cjg.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * Created with IntelliJ IDEA.
 * User: shenzaih
 * To change this template use File | Settings | File Templates.
 */
public class AutoUpdateMyselfServiceUtil {

    private static final int REPEAT_ALARM = 3;
    private static final String TAG = AutoUpdateMyselfServiceUtil.class.getSimpleName();
    private long delayTime;

    private Context mContext;
    private static AutoUpdateMyselfServiceUtil instance;

    public static AutoUpdateMyselfServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new AutoUpdateMyselfServiceUtil(context);
        }
        return instance;
    }

    public AutoUpdateMyselfServiceUtil(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        delayTime = PersistentVar.getInstance().getSystemConfig().getAppStoreUpdateTime();
        startCheckUpdateAlarm(delayTime);
    }

    public void start(int updateType) {
        //如果配置自更新 时间变换 则重新生成Alarm
        if (delayTime != PersistentVar.getInstance().getSystemConfig().getAppStoreUpdateTime()) {
            cancelCheckUpdateAlarm();
            delayTime = PersistentVar.getInstance().getSystemConfig().getAppStoreUpdateTime();
            startCheckUpdateAlarm(delayTime);
        } else {

            switch (updateType) {
                case AppConstants.UPDATE_REQUESTDATA:
                    UpdateUtil mUtil = new UpdateUtil();
                    AutoUpdateMySelf updateSelf = new AutoUpdateMySelf(mContext);
                    mUtil.checkUpdate(updateSelf, TAG);
                    break;
                default:
                    break;
            }
        }

    }

    public void onDestroy() {
        cancelCheckUpdateAlarm();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        instance = null;
    }

    private void startCheckUpdateAlarm(long interval) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_UPDATE_MYSELF, AppConstants.UPDATE_REQUESTDATA);
        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, pi);
    }

    private void cancelCheckUpdateAlarm() {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_UPDATE_MYSELF, 0);

        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }


}
