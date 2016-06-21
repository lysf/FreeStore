package com.snailgame.cjg.statistics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.TrafficStaticInfo;
import com.snailgame.cjg.common.db.daoHelper.TrafficStaticDaoHelper;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.mobilesdk.SnailCommplatform;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by chenping1 on 2014/9/23.
 */
public class TrafficStatisticsUtil {
    public static final int NONE = -1;
    public static final int NETWORK_TYPE_WIFI = 0;
    public static final int NETWORK_TYPE_2G = 1;   //2G 网络
    public static final int NETWORK_TYPE_3G_CMCC = 2;  //3G 移动
    public static final int NETWORK_TYPE_3G_CUCC = 3;  //3G 联通
    public static final int NETWORK_TYPE_3G_CTCC = 4;   //3G 电信
    public static final int NETWORK_TYPE_4G = 5;   //4G


    public static final String STATIC_ALL = "0";
    public static final String STATIC_DOWNLOAD = "1";

    /**
     * 得到当前网络
     *
     * @return
     */
    public static int getNetworkType() {
        int type = NetworkUtils.getActiveNetworkType(FreeStoreApp.getContext());
        if (ConnectivityManager.TYPE_WIFI == type) {
            return NETWORK_TYPE_WIFI;
        } else if (ConnectivityManager.TYPE_MOBILE == type) {
            switch (NetworkUtils.getNetworkType(FreeStoreApp.getContext())) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NETWORK_TYPE_2G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return NETWORK_TYPE_3G_CUCC;

                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return NETWORK_TYPE_3G_CTCC;

                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    //这一部分网络由于不确定所属，暂先获得手机 运营商 来判断,可能有问题
                    TelephonyManager tm = (TelephonyManager) FreeStoreApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String iNumeric = tm.getSimOperator();
                    if (iNumeric.length() > 0) {
                        switch (iNumeric) {
                            case "46000":
                            case "46002":
                                return NETWORK_TYPE_3G_CMCC;
                            case "46001":
                                return NETWORK_TYPE_3G_CUCC;
                            case "46003":
                                return NETWORK_TYPE_3G_CTCC;
                            default:
                                return NONE;
                        }
                    }
                    return NONE;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NETWORK_TYPE_4G;
                default:
                    return NONE;
            }
        } else {
            return NONE;
        }

    }

    /**
     * 保存到 数据库中，计时时间 则发送 该数据所有内容
     * 在 数据库内容有可能在网络切换 和 下载时候写入
     *
     * @param info
     */
    public static void saveDataToDB(TrafficStaticInfo info) {
        if (null != info) {
            TrafficStaticDaoHelper helper = TrafficStaticDaoHelper.getInstance(FreeStoreApp.getContext());
            TrafficStaticInfo dbInfo = helper.query(info.getcNetworkType(), info.getcActionType());
            if (dbInfo != null) {
                //数据库 没有记录 则标志 action type 为DB_NO_DATA
                if (dbInfo.getcActionType().equals(TrafficStaticDaoHelper.DB_NO_DATA)) {
                    helper.insert(info);
                } else {
                    //如果数据库中有数据，则将保存的流量加上新增的流量
                    long size = dbInfo.getnFlow() + info.getnFlow();
                    info.setnFlow(size);
                    helper.update(info);
                }
            }
        }
    }

    /**
     * 获取 流量统计需要的信息
     *
     * @param networkType
     * @param traffic
     * @param actionType
     * @return
     */
    public static TrafficStaticInfo getTrafficStaticInfo(int networkType, long traffic, String actionType) {
        TrafficStaticInfo info = new TrafficStaticInfo();
        info.setcNetworkType(networkType);
        info.setcActionType(actionType);

        String phoneNum = ComUtil.readPhoneNumber(FreeStoreApp.getContext());
        if (phoneNum != null && phoneNum.length() > 11) {
            phoneNum = phoneNum.substring(phoneNum.length() - 11);
        }
        info.setnPhoneNum(phoneNum);
        info.setcVendor(TrafficStatisticsUtil.getMobileType());
        info.setsGPSProvince(ComUtil.getProvinceId("0"));
        info.setcChennelId(ChannelUtil.getChannelID());
        info.setnFlow(traffic);
        info.setnPlatform(0);
        info.setcImei(SnailCommplatform.getInstance().getCIMEI(FreeStoreApp.getContext()));
        info.setcImsi(PhoneUtil.getImsi(FreeStoreApp.getContext()));
        return info;
    }


    /**
     * 获得当前  手机运营商类型
     *
     * @param
     * @return
     */
    public static String getMobileType() {
        Context context = FreeStoreApp.getContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iNumeric = tm.getSimOperator();
        if (iNumeric != null && iNumeric.length() > 0) {
            switch (iNumeric) {
                case "46000":
                case "46002":
                case "46007":
                    return context.getString(R.string.static_cmcc);
                case "46001":
                case "46006":
                    return context.getString(R.string.static_cucc);
                case "46003":
                case "46005":
                    return context.getString(R.string.static_ctcc);
                default:
                    return "";
            }
        }
        return "";
    }


    public static String getOperators() {
        Context context = FreeStoreApp.getContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iNumeric = tm.getSimOperator();
        if (iNumeric != null && iNumeric.length() > 0) {
            switch (iNumeric) {
                case "46000":
                case "46002":
                case "46007":
                    return "2";
                case "46003":
                case "46005":
                    return "3";
                case "46001":
                case "46006":
                    return "4";
                default:
                    return "";
            }
        }
        return "-1";
    }

    /**
     * 发送 数据给服务器
     *
     * @param info
     */
    public static void send(TrafficStaticInfo info) {
        String url = JsonUrl.getJsonUrl().STATIC_TRAFFIC_URL + AccountUtil.getLoginParams()
                + "&cNetworkType=" + info.getcNetworkType()   // 网络类型 0=无线网， 1=2G网络， 2=3G移动， 3=3G联通，4=3G电信， 5=4G
                + "&cActionType=" + info.getcActionType()   //统计类型  0-全部 1-下载
                + "&nPhoneNum=" + info.getnPhoneNum()
                + "&cVendor=" + info.getcVendor()          //运营商
                + "&sGPSProvince=" + info.getsGPSProvince()  //GPS 省份
                + "&cChennelId=" + info.getcChennelId()    // 渠道
                + "&nFlow=" + info.getnFlow()              //流量大小
                + "&nPlatform=" + info.getnPlatform()       //版本号
                + "&cImei=" + info.getcImei()             //设备号
                + "&cStartTime=" + info.getStartTime()     //开始统计时间
                + "&cEndTime=" + info.getEndTime()         //结束统计时间
                + "&cImsi=" + info.getcImsi();               //IMSI

        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), url);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    public static String getStartTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis() - TrafficStatisticServiceUtil.DELAY_TIME);
        return formatter.format(curDate);
    }


    /**
     * 通过 trafficStats 获取流量
     *
     * @param uid
     * @return -1 表明设备不支持
     */
    public static long getTraffic(int uid) {
        long rx = TrafficStats.getUidRxBytes(uid);
        long tx = TrafficStats.getUidTxBytes(uid);

        if (rx == -1 || tx == -1) {
            return -1;
        } else {
            return rx + tx;
        }
    }

}