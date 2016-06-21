package com.snailgame.cjg.util.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.guide.SplashActivity;
import com.snailgame.cjg.util.NotificationUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenping1 on 2014/4/18.
 */
public class RemindUsrServiceUtil {

    private Handler mHandler = new Handler();
    private int mDelayTime;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showNotification();
            mHandler.postDelayed(runnable, mDelayTime * 60 * 60 * 1000);
        }
    };

    private Context mContext;
    private static RemindUsrServiceUtil instance;

    public static RemindUsrServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new RemindUsrServiceUtil(context);
        }

        return instance;
    }

    public RemindUsrServiceUtil(Context context) {
        this.mContext = context;
    }

    public void start() {
        mDelayTime = PersistentVar.getInstance().getSystemConfig().getAppRemindTime();
        mHandler.postDelayed(runnable, mDelayTime * 60 * 60 * 1000);
    }


    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setWhen(System.currentTimeMillis())
                .setTicker(mContext.getString(R.string.notification_remind_usr_title))
                .setContentTitle(mContext.getString(R.string.update_notification_title))
                .setContentText(mContext.getString(R.string.notification_remind_usr_content))
                .setAutoCancel(true);
        NotificationUtils.setIcon(mContext, builder);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.notify_normal_layout);
        remoteViews.setTextViewText(R.id.title_tv, mContext.getString(R.string.update_notification_title));
        remoteViews.setTextViewText(R.id.content_tv, mContext.getString(R.string.notification_remind_usr_content));
        DateFormat df = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.time_tv, df.format(new Date()));
        builder.setContent(remoteViews);
        Intent intent = SplashActivity.newIntent(mContext);
        intent.putExtra(AppConstants.IS_NOTIFICATION_TAG, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        NotificationManager mnotiManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(888, builder.build()); //888 是给次notification 随机赋的id,无特殊意义
    }

    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        instance = null;
    }

}
