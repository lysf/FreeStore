package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by lic on 15-7-20.
 * 皮肤表
 */
@DatabaseTable(tableName = "skin_table")
public class Skin {
    public final static String COLUMN_ID = BaseColumns._ID;
    public static final String COL_SKIN_VERSION = "skin_version";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";
    public static final String COL_SKIN_LOCAL_PATH = "skin_local_path";
    public static final String COL_APK_SIZE = "apkSize";
    public static final String COL_APK_PKG_NAME = "apkPkgName";
    public static final String COL_APK_VERSION_CODE = "apkVersionCode";
    public static final String COL_APK_URL = "apkUrl";//APK下载URL
    public static final String COL_APK_MD5 = "apkMd5";
    public static final String COL_APK_VERSION_NAME = "apkVersionName";
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COL_SKIN_VERSION)
    private String skinVersion;
    @DatabaseField(columnName = COL_START_TIME)
    private String startTime;
    @DatabaseField(columnName = COL_END_TIME)
    private String endTime;
    @DatabaseField(columnName = COL_SKIN_LOCAL_PATH)
    private String skinLocalPath;
    @DatabaseField(columnName = COL_APK_SIZE)
    private String apkSize;
    @DatabaseField(columnName = COL_APK_PKG_NAME)
    private String apkPackageName;
    @DatabaseField(columnName = COL_APK_VERSION_CODE)
    private String apkVersionCode;
    @DatabaseField(columnName = COL_APK_URL)
    private String apkUrl;
    @DatabaseField(columnName = COL_APK_MD5)
    private String apkMD5;
    @DatabaseField(columnName = COL_APK_VERSION_NAME)
    private String apkVersionName;

    public int getId() {
        return id;
    }

    public String getSkinVersion() {
        return skinVersion;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSkinLocalPath() {
        return skinLocalPath;
    }

    public String getApkSize() {
        return apkSize;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public String getApkVersionCode() {
        return apkVersionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getApkMD5() {
        return apkMD5;
    }

    public String getApkVersionName() {
        return apkVersionName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSkinVersion(String skinVersion) {
        this.skinVersion = skinVersion;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSkinLocalPath(String skinLocalPath) {
        this.skinLocalPath = skinLocalPath;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public void setApkVersionCode(String apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setApkMD5(String apkMD5) {
        this.apkMD5 = apkMD5;
    }

    public void setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
    }
}
