package com.snailgame.cjg.common.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.downloadmanager.AutoCheckUpgradableGameServiceUtil;
import com.snailgame.cjg.event.ServiceStopEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.news.util.NewsChannelUtil;
import com.snailgame.cjg.settings.AutoUpdateMyselfServiceUtil;
import com.snailgame.cjg.settings.SilenceUpdateServicesUtil;
import com.snailgame.cjg.statistics.TrafficStatisticServiceUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.service.RemindUsrServiceUtil;
import com.squareup.otto.Subscribe;


/**
 * Created by TAJ_C on 2015/7/14.
 */
public class SnailFreeStoreService extends Service {
    public static final int TYPE_AUTO_CHECK_UPGRADABLE_GAME = 1;  //检测 应用内应用升级的
    public static final int TYPE_AUTO_UPDATE_MYSELF = 2;   //自更新
    public static final int TYPE_TRAFFIC_STATISTIC = 3;    //流量统计
    public static final int TYPE_SILENCE_UPDATE = 4;    //静默更新
    public static final int TYPE_REMIND_USER = 5;  //  一段时间未使用后提醒用户使用
    public static final int TYPE_DOWNLOAD_OBSERVER = 6; //下载更新
    public static final int TYPE_GET_NEWS_CHANNEL = 7; //获取资讯频道列表

    public static Intent newIntent(Context context, int serviceType) {
        Intent intent = newIntent(context);
        intent.putExtra(AppConstants.KEY_SERVICE_TYPE, serviceType);
        return intent;
    }


    public static Intent newIntent(Context context, int serviceType, int updateType) {
        Intent intent = newIntent(context, serviceType);
        intent.putExtra(AppConstants.UPDATE_TYPE, updateType);
        return intent;
    }

    public static Intent newIntent(Context context, int serviceType, int updateType, UpdateModel.ModelItem modelItem) {
        Intent intent = newIntent(context, serviceType, updateType);
        intent.putExtra(AppConstants.KEY_UPDATE_MODE, modelItem);
        return intent;
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SnailFreeStoreService.class);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainThreadBus.getInstance().register(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int serviceType = intent.getIntExtra(AppConstants.KEY_SERVICE_TYPE, -1);
            int updateType = intent.getIntExtra(AppConstants.UPDATE_TYPE, -1);
            UpdateModel.ModelItem model = (UpdateModel.ModelItem) intent.getSerializableExtra(AppConstants.KEY_UPDATE_MODE);
            start(serviceType, updateType, model);
        }

        return START_STICKY;
    }

    @Subscribe
    public void stopServiceEvent(ServiceStopEvent event) {
        int type = event.getServiceType();
        stop(type);
    }

    private void start(int serviceType, int updateType, UpdateModel.ModelItem modelItem) {
        switch (serviceType) {
            case TYPE_AUTO_CHECK_UPGRADABLE_GAME:
                AutoCheckUpgradableGameServiceUtil.getInstance(this).start(updateType);
                break;
            case TYPE_AUTO_UPDATE_MYSELF:
                AutoUpdateMyselfServiceUtil.getInstance(this).start(updateType);
                break;

            case TYPE_TRAFFIC_STATISTIC:
                TrafficStatisticServiceUtil.getInstance(this).start();
                break;

            case TYPE_SILENCE_UPDATE:
                SilenceUpdateServicesUtil.getInstance(this).start(modelItem);
                break;

            case TYPE_REMIND_USER:
                RemindUsrServiceUtil.getInstance(this).start();
                break;

            case TYPE_DOWNLOAD_OBSERVER:
                DownloadObserverServiceUtil.getInstance(this);
                break;

            case TYPE_GET_NEWS_CHANNEL:
                NewsChannelUtil.getInstance().getNewsChannelData(this);
            default:
                break;
        }
    }

    private void stop(int type) {
        switch (type) {
            case TYPE_AUTO_CHECK_UPGRADABLE_GAME:
                AutoCheckUpgradableGameServiceUtil.getInstance(this).onDestroy();
                break;
            case TYPE_AUTO_UPDATE_MYSELF:
                AutoUpdateMyselfServiceUtil.getInstance(this).onDestroy();
                break;
            case TYPE_TRAFFIC_STATISTIC:
                TrafficStatisticServiceUtil.getInstance(this).onDestroy();
                break;
            case TYPE_SILENCE_UPDATE:
                SilenceUpdateServicesUtil.getInstance(this).onDestroy();
                break;
            case TYPE_REMIND_USER:
                RemindUsrServiceUtil.getInstance(this).onDestroy();
                break;

            case TYPE_DOWNLOAD_OBSERVER:
                DownloadObserverServiceUtil.getInstance(this).onDestroy();
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
        stop(TYPE_AUTO_CHECK_UPGRADABLE_GAME);
        stop(TYPE_AUTO_UPDATE_MYSELF);
        stop(TYPE_SILENCE_UPDATE);
        stop(TYPE_TRAFFIC_STATISTIC);
        stop(TYPE_REMIND_USER);
        stop(TYPE_DOWNLOAD_OBSERVER);
        stop(TYPE_GET_NEWS_CHANNEL);
    }


}
