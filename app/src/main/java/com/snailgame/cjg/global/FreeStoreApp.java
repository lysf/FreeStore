package com.snailgame.cjg.global;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.snailgame.cjg.BuildConfig;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.GameSdkDataUtil;
import com.snailgame.cjg.util.HostUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.FastDevApplication;
import com.snailgame.fastdev.util.LogUtils;
import com.squareup.leakcanary.LeakCanary;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;

import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

public class FreeStoreApp extends FastDevApplication {
    public static int statusOfUsr = AppConstants.USR_STATUS_IDLE; //获取用户信息状态

    /**
     * 解决Dex超出方法数的限制问题
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // 流量代理需要配置的key及var
    // private String key = "132524";
    // private String var = "1aa7ed73bec94b1371c89eca8515e3be395a68bc";

    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        LogUtils.setDebug(BuildConfig.DEBUG, "FreeStore");
        initStatistics();

        // 免商店统计
        LoginSDKUtil.setChannelID(this, String.valueOf(ChannelUtil.getChannelID()));
        LoginSDKUtil.setDelays(3, 60);

        GlobalVar.getInstance().generateUserAgent(mContext);
        initProxy();


        // 命令使用初始化
        CommandHelper.init();
        // 流量统计初始化
        EngineEnvironment.getInstance(this).initEnvironments();

        // 检测SDK所用数据
        GameSdkDataUtil.checkSdkData();
        classLoader();

        startDownloadObserverService();

        // fix bug: UserManager.mContext leak 
        initUserManager();
    }


    /**
     * fix bug :java.lang.NoClassDefFoundError: android.os.AsyncTask
     */
    private void classLoader() {
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable ignore) {
        }
    }

    private void startDownloadObserverService() {
        startService(SnailFreeStoreService.newIntent(this, SnailFreeStoreService.TYPE_DOWNLOAD_OBSERVER));
    }

    /**
     * 第三方统计配置
     */
    private void initStatistics() {
        // 友盟统计
        AnalyticsConfig.setChannel(ChannelUtil.getChannelID());
        AnalyticsConfig.setAppkey(this, "53017a3156240b325b1c0fd3");

        //禁止默认Acitivity 统计方式
        MobclickAgent.openActivityDurationTrack(false);

        // 云测崩溃大师初始化
        TestinAgent.init(this, "ae52a1f1bb84c29040b72d3a1ecafc4b", String.valueOf(ChannelUtil.getChannelID()));
        // for testin test
//        TestinAgent.setLocalDebug(true);
        TestinAgent.setUserInfo(TextUtils.isEmpty(PhoneUtil.getIMEICode(this)) ? "-" : PhoneUtil.getIMEICode(this));

    }

    /**
     * 开启流量代理
     */
    private void initProxy() {
        HostUtil.replaceHost();
    }


    /**
     * fix bug: UserManager.mContext leak
     */
    private void initUserManager() {
        try {
            Method method = Class.forName("android.os.UserManager").getMethod(
                    "get", Context.class);
            method.invoke(null, FreeStoreApp.getContext());
        } catch (Exception e) {
        }
    }

}
