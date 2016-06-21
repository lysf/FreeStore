package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by lic on 15-7-20.
 * 用户已经安装的游戏表
 */
@DatabaseTable(tableName = "my_game_table")
public class MyGame {
    public final static String COLUMN_ID = BaseColumns._ID;
    public static final String COL_APK_ID = "apkId";
    public static final String COL_APK_LABEL = "apkLabel";
    public static final String COL_APK_SIZE = "apkSize";
    public static final String COL_APK_PKG_NAME = "apkPkgName";
    public static final String COL_APK_VERSION_CODE = "apkVersionCode";
    public static final String COL_APK_URL = "apkUrl";//APK下载URL
    public static final String COL_APK_MD5 = "apkMd5";
    public static final String COL_APK_VERSION_NAME = "apkVersionName";
    public static final String COL_ICON_URL = "iconUrl";
    public static final String COL_DOWNLOAD_ID = "downloadId";
    public static final String COL_DOWNLOAD_PERCENT = "downloadPercent";
    public static final String COL_DOWNLOAD_SIZE = "downloadSize";
    public static final String COL_DOWNLOAD_STATE = "downloadState";
    public static final String COL_DOWNLOAD_URI = "downloadUri";
    public static final String COL_DOWNLOAD_IS_UPDATE = "isUpdate";
    public static final String COL_DOWNLOAD_IS_PATCH = "isPatch";
    public static final String COL_UPGRADE_IGNORE_VERSION_CODE = "upgrade_ignore_code"; //忽略更新 -- 0不忽略 忽略小于 version code
    public static final String COL_APP_TYPE = "appType";// 应用类型 0 代表应用 1代表游戏  3.代表皮肤
    public static final String COL_UPGRADE_DESC = "upgrade_desc";
    public static final String COL_DOWNLOAD_FlOWFREE_STATE = "flowfree";
    public static final String COL_DIFF_URL = "diffUrl";//APK patch下载URL
    public static final String COL_DIFF_SIZE = "diffSize";
    public static final String COL_DIFF_MD5 = "diffMd5";
    public static final String COL_TOTAL_INSTALL_NUM = "totalInstallNum";//总得安装量
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COL_APK_ID, canBeNull = false)
    private int apkId;
    @DatabaseField(columnName = COL_APK_LABEL, canBeNull = false)
    private String apkLabel = "";
    @DatabaseField(columnName = COL_APK_SIZE, canBeNull = false)
    private String appSize = "";
    @DatabaseField(columnName = COL_APK_PKG_NAME, canBeNull = false, unique = true)
    private String packageName = "";
    @DatabaseField(columnName = COL_APK_VERSION_CODE)
    private int versionCode;
    @DatabaseField(columnName = COL_APK_URL)
    private String apkUrl = "";
    @DatabaseField(columnName = COL_APK_MD5)
    private String apkMD5 = "";
    @DatabaseField(columnName = COL_APK_VERSION_NAME)
    private String versionName = "";
    @DatabaseField(columnName = COL_ICON_URL)
    private String iconURL = "";
    @DatabaseField(columnName = COL_DOWNLOAD_ID)
    private long downloadId;
    @DatabaseField(columnName = COL_DOWNLOAD_PERCENT)
    private int downloadPercent;
    @DatabaseField(columnName = COL_DOWNLOAD_SIZE)
    private String downloadSize = "";
    @DatabaseField(columnName = COL_DOWNLOAD_STATE)
    private int downloadState;
    @DatabaseField(columnName = COL_DOWNLOAD_URI)
    private String downloadUri = "";
    @DatabaseField(columnName = COL_DOWNLOAD_IS_UPDATE)
    private int downloadIsUpdate;
    @DatabaseField(columnName = COL_DOWNLOAD_IS_PATCH)
    private int downloadIsPatch;
    @DatabaseField(columnName = COL_UPGRADE_IGNORE_VERSION_CODE)
    private int upgradeIgnoreVersionCode;
    @DatabaseField(columnName = COL_APP_TYPE)
    private String appType = "";
    @DatabaseField(columnName = COL_UPGRADE_DESC)
    private String upgradeDesc = "";
    @DatabaseField(columnName = COL_DOWNLOAD_FlOWFREE_STATE)
    private String downloadFlowFreeState = "";
    @DatabaseField(columnName = COL_DIFF_URL)
    private String diffUrl = "";
    @DatabaseField(columnName = COL_DIFF_SIZE)
    private String diffSize = "";
    @DatabaseField(columnName = COL_DIFF_MD5)
    private String diffMD5 = "";
    @DatabaseField(columnName = COL_TOTAL_INSTALL_NUM)
    private int totalInstallNum;

    public MyGame() {
    }

    public int getId() {
        return id;
    }

    public int getApkId() {
        return apkId;
    }

    public String getApkLabel() {
        return apkLabel;
    }

    public String getAppSize() {
        return appSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getApkMD5() {
        return apkMD5;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getIconURL() {
        return iconURL;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public int getDownloadPercent() {
        return downloadPercent;
    }

    public String getDownloadSize() {
        return downloadSize;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public int getDownloadIsUpdate() {
        return downloadIsUpdate;
    }

    public int getDownloadIsPatch() {
        return downloadIsPatch;
    }

    public int getUpgradeIgnoreVersionCode() {
        return upgradeIgnoreVersionCode;
    }

    public String getAppType() {
        return appType;
    }

    public String getUpgradeDesc() {
        return upgradeDesc;
    }

    public String getDownloadFlowFreeState() {
        return downloadFlowFreeState;
    }

    public String getDiffUrl() {
        return diffUrl;
    }

    public String getDiffSize() {
        return diffSize;
    }

    public String getDiffMD5() {
        return diffMD5;
    }

    public int getTotalInstallNum() {
        return totalInstallNum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setApkId(int apkId) {
        this.apkId = apkId;
    }

    public void setApkLabel(String apkLabel) {
        this.apkLabel = apkLabel;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setApkMD5(String apkMD5) {
        this.apkMD5 = apkMD5;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public void setDownloadPercent(int downloadPercent) {
        this.downloadPercent = downloadPercent;
    }

    public void setDownloadSize(String downloadSize) {
        this.downloadSize = downloadSize;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public void setDownloadIsUpdate(int downloadIsUpdate) {
        this.downloadIsUpdate = downloadIsUpdate;
    }

    public void setDownloadIsPatch(int downloadIsPatch) {
        this.downloadIsPatch = downloadIsPatch;
    }

    public void setUpgradeIgnoreVersionCode(int upgradeIgnoreVersionCode) {
        this.upgradeIgnoreVersionCode = upgradeIgnoreVersionCode;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setUpgradeDesc(String upgradeDesc) {
        this.upgradeDesc = upgradeDesc;
    }

    public void setDownloadFlowFreeState(String downloadFlowFreeState) {
        this.downloadFlowFreeState = downloadFlowFreeState;
    }

    public void setDiffUrl(String diffUrl) {
        this.diffUrl = diffUrl;
    }

    public void setDiffSize(String diffSize) {
        this.diffSize = diffSize;
    }

    public void setDiffMD5(String diffMD5) {
        this.diffMD5 = diffMD5;
    }

    public void setTotalInstallNum(int totalInstallNum) {
        this.totalInstallNum = totalInstallNum;
    }
}
