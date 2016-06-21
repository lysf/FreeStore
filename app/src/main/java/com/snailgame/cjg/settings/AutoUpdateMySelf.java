package com.snailgame.cjg.settings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.NotificationUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoUpdateMySelf implements UpdateUtil.CallBack {

    private Context mContext;

    public AutoUpdateMySelf(Context activity) {
        mContext = activity;
    }

    @Override
    public void onCompleted(UpdateModel model) {
        if (model == null || !model.getMsg().equals("OK") || model.itemModel == null) return;
        if (model.itemModel.getbUpdate()) {
            AppInfo appInfo = new AppInfo();
            appInfo.setApkUrl(model.itemModel.getcApkUrl());
            appInfo.setAppName(model.itemModel.getsName());
            appInfo.setPkgName(FreeStoreApp.getContext().getPackageName());
            appInfo.setIcon(model.itemModel.getcIcon());
            appInfo.setVersionCode(Integer.parseInt(model.itemModel.getnVersionCode()));
            appInfo.setAppId(Integer.parseInt(model.itemModel.getnAppId()));
            appInfo.setVersionName(model.itemModel.getcVersion());
            appInfo.setMd5(model.itemModel.getcMd5Code());
            appInfo.setApkSize(Integer.parseInt(model.itemModel.getcSize()));
            appInfo.setsAppDesc(model.itemModel.getsDesc());
            appInfo.setIsUpdate(1);
            appInfo.setIsPatch(0);
            showDefaultNotification(mContext, appInfo, model);
        }
    }

    private void showDefaultNotification(Context context, AppInfo appInfo, UpdateModel model) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setTicker(context.getString(R.string.update_notification_title_appstore))
                .setContentTitle(context.getString(R.string.update_notification_title_appstore))
                .setContentText(context.getString(R.string.click_to_upgrade))
                .setAutoCancel(true);
        NotificationUtils.setIcon(context, builder);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notify_normal_layout);
        remoteViews.setTextViewText(R.id.title_tv, context.getString(R.string.update_notification_title_appstore));
        remoteViews.setTextViewText(R.id.content_tv, context.getString(R.string.click_to_upgrade));
        DateFormat df = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.time_tv, df.format(new Date()));
        builder.setContent(remoteViews);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, UpdateDialogActivity.newIntent(context, true, appInfo, model.itemModel.getsDesc(), Intent.FLAG_ACTIVITY_NEW_TASK, false), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager mnotiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(289, builder.build());
    }
}
