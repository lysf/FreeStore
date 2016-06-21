package com.snailgame.cjg.downloadmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.InsteadChargeHelper;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.NotificationUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.ResUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: shenzaih
 * To change this template use File | Settings | File Templates.
 */
public class AutoCheckUpgradableGameServiceUtil implements MyGameDaoHelper.MyGameCallback {
    private long delayTime;
    private static final int REPEAT_ALARM = 1;
    private static final int REPEAT_ALARM_NOTIFICATION = 2;
    private static final int NOTIFICATION_ALL_APP_ID = 288;
    private static final int NOTIFICATION_REQUEST_COED_NORMAL = 0;
    private static final int NOTIFICATION_REQUEST_COED_DELETE = 1;
    private List<AppInfo> infoList;
    private boolean isCancleNotify = false;//通知栏是否已被取消的标志位
    private NotificationManager mnotiManager;

    private AsyncTask queryTask;
    private Context mContext;
    private static AutoCheckUpgradableGameServiceUtil instance;

    public static AutoCheckUpgradableGameServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new AutoCheckUpgradableGameServiceUtil(context);
        }
        return instance;
    }

    public AutoCheckUpgradableGameServiceUtil(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        delayTime = PersistentVar.getInstance().getSystemConfig().getGameUpdateIntervel();
        mnotiManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        cancelCheckUpdateNotifyAlarm();
        startCheckUpdateAlarm(AppConstants.UPDATE_INTERVAL);
    }


    public void start(int updateType) {

        //如果配置自更新 时间变换 则重新生成Alarm
        if (delayTime != PersistentVar.getInstance().getSystemConfig().getGameUpdateIntervel()) {
            delayTime = PersistentVar.getInstance().getSystemConfig().getGameUpdateIntervel();
            startNotifyAlarm();
        } else {
            switch (updateType) {
                case AppConstants.UPDATE_REQUEST_START:
                    startNotifyAlarm();
                    break;
                case AppConstants.UPDATE_REQUESTDATA:
                    MyGameDaoHelper.getUpgradeApk(mContext);
                    InsteadChargeHelper.getInsteadCharge();//刷新代充数据与检查更新保持同步
                    break;
                case AppConstants.UPDATE_CHECK_NOTIFICATION:
                    queryTask = MyGameDaoHelper.queryForAppInfoInThread(mContext, this);
                    break;
                case AppConstants.ONE_KEY_UPDATE:
                    oneKeyUpdate();
                    break;
                case AppConstants.CANCLE_NOTIFY:
                    isCancleNotify = true;
                    break;
                default:
                    break;
            }
        }

    }

    private void startNotifyAlarm() {
        cancelCheckUpdateNotifyAlarm();
        if (SharedPreferencesUtil.getInstance().isUpdate()) {
            startCheckUpdateNotifyAlarm(delayTime);
        }
    }

    public void onDestroy() {
        if (queryTask != null) {
            queryTask.cancel(true);
        }
        cancelCheckUpdateAlarm();
        cancelCheckUpdateNotifyAlarm();
        instance = null;
    }

    private void showDefaultNotification(List<AppInfo> infoList) {
        isCancleNotify = false;
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setWhen(System.currentTimeMillis())
                .setTicker(ResUtil.getString(R.string.update_notification_title_free))
                .setAutoCancel(true);
        NotificationUtils.setIcon(mContext, builder);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.notify_update_layout);
        builder.setContent(remoteViews);
        //设置一键下载的按钮的点击事件
        PendingIntent intent = PendingIntent.getService(mContext,
                NOTIFICATION_REQUEST_COED_NORMAL,
                SnailFreeStoreService.newIntent(mContext,
                        SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME,
                        AppConstants.ONE_KEY_UPDATE), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_update, intent);
        //手机大于5.0显示的icon为小图标
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(
                    ResUtil.getResources(), R.drawable.notification));
        } else {
            remoteViews.setImageViewBitmap(R.id.icon, BitmapFactory.decodeResource(
                    ResUtil.getResources(), R.drawable.notification));
        }
        //手机小于2.3则不显示一键下载的按钮
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
            remoteViews.setViewVisibility(R.id.btn_update, View.GONE);
        } else {
            remoteViews.setViewVisibility(R.id.btn_update, View.VISIBLE);
        }
        int roundSize;
        //大于5个应用可以更新则显示...
        if (infoList.size() > 5) {
            remoteViews.setViewVisibility(R.id.notify_more_tv, View.VISIBLE);
            roundSize = 5;
        } else {
            remoteViews.setViewVisibility(R.id.notify_more_tv, View.GONE);
            roundSize = infoList.size();
        }
        long appTotalSize = 0;
        for (AppInfo appInfo : infoList) {
            appTotalSize += appInfo.getApkSize();
        }
        remoteViews.setTextViewText(R.id.content_tv, GameManageUtil.getSpannableStringBuilder(mContext, infoList.size(), appTotalSize));
        //设置通知栏点击的intent
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_COED_NORMAL,
                GameManageActivity.newIntent(mContext, true), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //设置通知栏被用户手动取消的intent，比如滑动取消和一键清除通知
        builder.setDeleteIntent(PendingIntent.getService(mContext,
                NOTIFICATION_REQUEST_COED_DELETE,
                SnailFreeStoreService.newIntent(mContext,
                        SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME,
                        AppConstants.CANCLE_NOTIFY),
                PendingIntent.FLAG_UPDATE_CURRENT));
        Notification notify = builder.build();
        builder.setContent(remoteViews);
        for (int i = 0; i < roundSize; i++) {
            AppInfo appInfo = infoList.get(i);
            switch (i) {
                case 0:
                    getUpdateAppIcon(appInfo.getIcon(), remoteViews, R.id.notify_img_1, notify);
                    break;
                case 1:
                    getUpdateAppIcon(appInfo.getIcon(), remoteViews, R.id.notify_img_2, notify);
                    break;
                case 2:
                    getUpdateAppIcon(appInfo.getIcon(), remoteViews, R.id.notify_img_3, notify);
                    break;
                case 3:
                    getUpdateAppIcon(appInfo.getIcon(), remoteViews, R.id.notify_img_4, notify);
                    break;
                case 4:
                    getUpdateAppIcon(appInfo.getIcon(), remoteViews, R.id.notify_img_5, notify);
                    break;
            }
        }
        mnotiManager.notify(NOTIFICATION_ALL_APP_ID, notify);
    }

    /**
     * 通过网络来获取可更新应用的图标
     */
    private void getUpdateAppIcon(String iconUrl, final RemoteViews remoteViews, final int imageViewId, final Notification notify) {
        BitmapManager.showImg(iconUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null && !isCancleNotify) {
                            remoteViews.setImageViewBitmap(imageViewId, response.getBitmap());
                            remoteViews.setViewVisibility(imageViewId, View.VISIBLE);
                            mnotiManager.notify(NOTIFICATION_ALL_APP_ID, notify);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }

    /**
     * 根据回调的可更新应用列表来进统一更新
     */
    public void oneKeyUpdate() {
        isCancleNotify = true;
        NotificationManager mnotiManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.cancel(NOTIFICATION_ALL_APP_ID);
        collapseStatusBar();
        if (NetworkUtils.isWifiEnabled(mContext)) {    // WIFI连接，直接下载
            doDownload();
        } else if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) { // 用户登录且用户为免流量用户，则直接下载
            doDownload();
        } else {
            if (SharedPreferencesUtil.getInstance().isDoNotAlertAnyMore1()) {
                doDownload();
            } else {
                mContext.startActivity(UpdateInNotificationActivity.newIntent(mContext, (ArrayList) infoList));
            }
        }

    }

    private void doDownload() {
        if (infoList != null) {
            for (AppInfo appInfo : infoList) {
                DownloadHelper.startDownload(mContext, appInfo);
            }
        }
        MainThreadBus.getInstance().post(new UpdateChangeEvent());
    }

    /**
     * 通过反射收起通知栏
     */
    private void collapseStatusBar() {
        try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            Object service = mContext.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method collapse;
            if (currentapiVersion <= 16) {
                collapse = statusbarManager.getMethod("collapse");
            } else {
                collapse = statusbarManager.getMethod("collapsePanels");
            }
            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * **********************************************************************************************************
     * 启动向服务器请求当前需要更新应用列表任务的定时器
     *
     * @param interval
     */
    private void startCheckUpdateAlarm(long interval) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME,
                AppConstants.UPDATE_REQUESTDATA);
        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, pi);
    }


    /**
     * **********************************************************************************************************
     * 启动查询当前数据库内需要更新的应用的任务定时器
     *
     * @param interval
     */
    private void startCheckUpdateNotifyAlarm(long interval) {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME, AppConstants.UPDATE_CHECK_NOTIFICATION);
        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM_NOTIFICATION,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, pi);

    }


    private void cancelCheckUpdateAlarm() {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME,
                AppConstants.UPDATE_REQUESTDATA);
        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    private void cancelCheckUpdateNotifyAlarm() {
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent timerIntent = SnailFreeStoreService.newIntent(mContext,
                SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME, AppConstants.UPDATE_CHECK_NOTIFICATION);
        PendingIntent pi = PendingIntent.getService(mContext, REPEAT_ALARM_NOTIFICATION,
                timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    /**
     * *********************************************************************************************************
     */

    @Override
    public void Callback(List<AppInfo> appInfos) {
        List<AppInfo> infoList = GameManageUtil.getUpdateInfos(mContext, appInfos, false);
        this.infoList = infoList;
        //当免商店在前台不显示通知
        if (infoList.size() > 0 && !ComUtil.isApplicationShowing(AppConstants.APP_PACKAGE_NAME)) {
            showDefaultNotification(infoList);
        }

    }
}