package com.snailgame.cjg.settings;

import android.content.Context;

import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.ServiceStopEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;

/**
 * Created by shenzaih on 2014/5/12.
 */
public class SnailFreeStoreServiceUtil {
    /**
     * 初始化商店本身的自更新
     */
    public static void initMyselfUpdate(Context context) {
        int serviceSwitch = PersistentVar.getInstance().getSystemConfig().getAppStoreUpdateTimeSwitch();
        if (serviceSwitch == 1) {
            context.startService(SnailFreeStoreService.newIntent(context, SnailFreeStoreService.TYPE_AUTO_UPDATE_MYSELF));
        }
    }


    public static void initGameUpdate(Context context) {
        boolean isShowUpdateMessage = SharedPreferencesUtil.getInstance().isUpdate();
        int serviceSwitch = PersistentVar.getInstance().getSystemConfig().getGameUpdateTimeSwitch();
        if (isShowUpdateMessage && serviceSwitch == 1) {
            GameManageUtil.startCheckUpdateService(context);
        }
    }

    public static void initBBSReLogin(Context context) {
        if (LoginSDKUtil.isLogined(context) && !ComUtil.isServiceRunning(context, AutoReLoginBBsService.class.getName())) {
            context.startService(AutoReLoginBBsService.newIntent(context));
        }
    }

    public static void initTrafficStatistic(Context context) {
        context.startService(SnailFreeStoreService.newIntent(context, SnailFreeStoreService.TYPE_TRAFFIC_STATISTIC));
    }

    public static void initRemindUser(Context context) {
        if (PersistentVar.getInstance().getSystemConfig().getAppRemindTimeSwitch() == 1) {
            context.startService(SnailFreeStoreService.newIntent(context,SnailFreeStoreService.TYPE_REMIND_USER));
        }
    }



    public static void initSnailFreeStoreService(Context context) {
        initGameUpdate(context);
        initMyselfUpdate(context);
        initBBSReLogin(context);
        initTrafficStatistic(context);
        MainThreadBus.getInstance().post(new ServiceStopEvent(SnailFreeStoreService.TYPE_REMIND_USER));

        if (LoginSDKUtil.isLogined(context)) {
            context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_UPDATE_USR_INFO));
        }

        context.startService(SnailFreeStoreService.newIntent(context, SnailFreeStoreService.TYPE_GET_NEWS_CHANNEL));
    }
}
