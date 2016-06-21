package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenping1 on 2014/9/17.
 */
@DatabaseTable(tableName = "traffic_static")
public class TrafficStaticInfo {

    public final static String COLUMN_ID = BaseColumns._ID;
    public static final String COL_NETWORK_TYPE = "network_type";
    public static final String COL_ACTION_TYPE = "action_type";
    public static final String COL_PHONE_NUMBER = "phone_number";
    public static final String COL_VENDOR = "vendor";
    public static final String COL_GPS_PROVINCE = "gps_province";
    public static final String COL_CHANNEL_ID = "channel_id";
    public static final String COL_FLOW = "flow";
    public static final String COL_PLATFORM = "platform";
    public static final String COL_IMEI = "device_id";
    public static final String COL_IMSI = "imsi";
    public static final String COL_TIME = "time";
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COL_NETWORK_TYPE)
    private int cNetworkType;      // 网络类型 0=无线网， 1=2G网络， 2=3G移动， 3=3G联通，4=3G电信， 5=4G
    @DatabaseField(columnName = COL_ACTION_TYPE)
    private String cActionType;    //统计类型  0-全部 1-下载
    @DatabaseField(columnName = COL_PHONE_NUMBER)
    private String nPhoneNum;
    @DatabaseField(columnName = COL_VENDOR)
    private String cVendor;     //运营商
    @DatabaseField(columnName = COL_GPS_PROVINCE)
    private String sGPSProvince; //GPS 省份
    @DatabaseField(columnName = COL_CHANNEL_ID)
    private String cChennelId;    // 渠道
    @DatabaseField(columnName = COL_FLOW)
    private long nFlow;   //流量大小
    @DatabaseField(columnName = COL_PLATFORM)
    private int nPlatform;
    @DatabaseField(columnName = COL_IMEI)
    private String cImei;    //设备号
    private String startTime;
    @DatabaseField(columnName = COL_TIME)
    private String endTime;
    @DatabaseField(columnName = COL_IMSI)
    private String cImsi;

    public int getcNetworkType() {
        return cNetworkType;
    }

    public void setcNetworkType(int cNetworkType) {
        this.cNetworkType = cNetworkType;
    }

    public int getnPlatform() {
        return nPlatform;
    }

    public void setnPlatform(int nPlatform) {
        this.nPlatform = nPlatform;
    }

    public String getcActionType() {
        return cActionType;
    }

    public void setcActionType(String cActionType) {
        this.cActionType = cActionType;
    }

    public String getnPhoneNum() {
        return nPhoneNum;
    }

    public void setnPhoneNum(String nPhoneNum) {
        this.nPhoneNum = nPhoneNum;
    }

    public String getcVendor() {
        return cVendor;
    }

    public void setcVendor(String cVendor) {
        this.cVendor = cVendor;
    }

    public String getsGPSProvince() {
        return sGPSProvince;
    }

    public void setsGPSProvince(String sGPSProvince) {
        this.sGPSProvince = sGPSProvince;
    }

    public String getcChennelId() {
        return cChennelId;
    }

    public void setcChennelId(String cChennelId) {
        this.cChennelId = cChennelId;
    }

    public long getnFlow() {
        return nFlow;
    }

    public void setnFlow(long nFlow) {
        this.nFlow = nFlow;
    }

    public String getcImei() {
        return cImei;
    }

    public void setcImei(String cImei) {
        this.cImei = cImei;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getcImsi() {
        return cImsi;
    }

    public void setcImsi(String cImsi) {
        this.cImsi = cImsi;
    }

}
