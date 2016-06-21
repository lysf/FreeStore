package com.snailgame.cjg.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.snailgame.cjg.common.db.dao.TrafficStaticInfo;
import com.snailgame.cjg.common.db.daoHelper.TrafficStaticDaoHelper;

/**
 * 流量统计服务
 * Created by chenping1 on 2014/9/12.
 */
public class TrafficStatisticServiceUtil {


    private TrafficInfo trafficInfo;  //流量
    private TrafficStaticInfo trafficStaticInfo; //流量统计实体类
    private TrafficStaticDaoHelper helper;
    private long initialTraffic;

    private boolean isInitialStatics = true;
    private long traffic = -1;
    private String uid;


    //监测网络变化
    private BroadcastReceiver mNetworkChangedReceiver;

    //计时task， 计时时间到后 则发送数据到服务器
    Runnable task;

    //记录当前的网络，用于当 网络关闭情况下 统计能统计
    private int mCurrentNetworkType;

    private Handler handler = new Handler();
    public static int DELAY_TIME = 30 * 60 * 1000;


    private static TrafficStatisticServiceUtil instance;
    private Context context;

    public static TrafficStatisticServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TrafficStatisticServiceUtil(context);
        }
        return instance;
    }

    public TrafficStatisticServiceUtil(Context context) {
        this.context = context;
        init();
    }


    private void init() {
        initBroadcastReceiver();
        initTask();
        initData();
    }

    private void initData() {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_SERVICES);
            uid = String.valueOf(ai.uid);
            trafficInfo = new TrafficInfo(uid);
            mCurrentNetworkType = TrafficStatisticsUtil.getNetworkType();
            trafficStaticInfo = getTrafficStaticInfo(mCurrentNetworkType);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        helper = TrafficStaticDaoHelper.getInstance(context);
    }

    public void start(){
        handler.postDelayed(task, DELAY_TIME);
    }

    /**
     * 获取流量统计信息
     *
     * @param networkType
     * @return
     */
    private TrafficStaticInfo getTrafficStaticInfo(int networkType) {
        long traffic = getUseTraffic();
        if (traffic == -1) {
            traffic = 0;
        }
        return TrafficStatisticsUtil.getTrafficStaticInfo(networkType, traffic, TrafficStatisticsUtil.STATIC_ALL);
    }

    /**
     * 获取 消耗的流量, 单位 字节
     *
     * @return
     */
    private long getUseTraffic() {
        if (isInitialStatics) {

            initialTraffic = TrafficStatisticsUtil.getTraffic(Integer.valueOf(uid));
            //当设备不支持统计时 从"/proc/uid_stat/" + uid 文件读取
            if (-1 == initialTraffic) {
                initialTraffic = trafficInfo.getTrafficInfo();
            }
            isInitialStatics = false;

        } else {
            long lastestTraffic = TrafficStatisticsUtil.getTraffic(Integer.valueOf(uid));

            //当设备不支持统计时 从"/proc/uid_stat/" + uid 文件读取
            if (-1 == lastestTraffic) {
                lastestTraffic = trafficInfo.getTrafficInfo();
            }

            if (initialTraffic == -1) {
                traffic = -1;
            } else {
                traffic = lastestTraffic - initialTraffic;
            }
            initialTraffic = lastestTraffic;
        }
        return traffic;
    }


    private void initTask() {
        task = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(task, DELAY_TIME);

                //再统计一次流量
                trafficStaticInfo = getTrafficStaticInfo(mCurrentNetworkType);

                // 保存到发送的数据库中,  数据库内容有可能在网络切换 和 下载时候写入
                TrafficStatisticsUtil.saveDataToDB(trafficStaticInfo);

                // 上传数据
                helper.sendAndBackup();

                //数据库重置
                helper.delete();
            }
        };
    }

    private void initBroadcastReceiver() {
        mNetworkChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentType = -1;
                ConnectivityManager connMamager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                // 判断是否正在使用WIFI网络
                NetworkInfo wifiNetworkInfo = connMamager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiNetworkInfo != null && NetworkInfo.State.CONNECTED == wifiNetworkInfo.getState()) {
                    //如果之前不是wifi 网络则 计算之前网络流量 保存
                    if (trafficStaticInfo != null && trafficStaticInfo.getcNetworkType() != TrafficStatisticsUtil.NETWORK_TYPE_WIFI) {
                        currentType = TrafficStatisticsUtil.NETWORK_TYPE_WIFI;
                    }
                }

                // 判断是否正在使用GPRS网络
                NetworkInfo mobileNetworkInfo = connMamager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobileNetworkInfo != null && NetworkInfo.State.CONNECTED == mobileNetworkInfo.getState()) {
                    if (trafficStaticInfo != null && trafficStaticInfo.getcNetworkType() != TrafficStatisticsUtil.getNetworkType()) {
                        currentType = TrafficStatisticsUtil.getNetworkType();
                    }
                }
                //当网络变化之前的数据保存到数据库中
                if (currentType != -1) {
                    trafficStaticInfo = getTrafficStaticInfo(mCurrentNetworkType);
                    TrafficStatisticsUtil.saveDataToDB(trafficStaticInfo);

                    mCurrentNetworkType = currentType;
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkChangedReceiver, mFilter);
    }


    public void onDestroy() {
        if (mNetworkChangedReceiver != null) {
            context.unregisterReceiver(mNetworkChangedReceiver);
        }



        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        instance = null;
    }
}
