package com.snailgame.cjg.common.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.global.AppConstants;


/**
 * 应用信息的基本实体类
 * 继承它的子类有: 应用列表
 * 推荐页面
 * Created by taj on 14-2-19.
 */
public class BaseAppInfo implements Parcelable {
    protected int appId;
    protected String appName;
    protected String icon;
    protected String apkUrl;
    protected long apkSize;
    protected String versionName;
    protected int versionCode;
    protected String pkgName;
    protected String cFlowFree;
    protected String md5;
    protected String sAppDesc;
    protected String sInfo;
    protected String cType;
    protected String cPicUrl;
    protected String cPicSmall;
    protected String cMark;
    protected int iRefId;

    protected String cHtmlUrl;
    protected int iCategoryId;
    protected String cAppType = AppConstants.VALUE_TYPE_GAME;      // 主类型: 1,游戏; 2, 应用; 3, 皮肤
    protected int iFreeArea;

    String cIconLabel;  // 图标角标
    // 游戏
    public static final String APP_TYPE_GAME = "1";
    // 应用软件
    public static final String APP_TYPE_SOFT = "2";
    //用于区别是游戏还是应用 11来源应用 3 9来源游戏
    private int cMainType;

    public int getcMainType() {
        return cMainType;
    }

    public void setcMainType(int cMainType) {
        this.cMainType = cMainType;
    }


    @JSONField(name = "nAppId")
    public int getAppId() {
        return appId;
    }

    @JSONField(name = "nAppId")
    public void setAppId(int appId) {
        this.appId = appId;
    }

    @JSONField(name = "sAppName")
    public String getAppName() {
        return appName;
    }

    @JSONField(name = "sAppName")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @JSONField(name = "cIcon")
    public String getIcon() {
        return icon;
    }

    @JSONField(name = "cIcon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JSONField(name = "cDownloadUrl")
    public String getApkUrl() {
        return apkUrl;
    }

    @JSONField(name = "cDownloadUrl")
    public void setApkUrl(String apkUrl) {
        if (apkUrl != null)
            this.apkUrl = apkUrl.trim();
    }

    @JSONField(name = "iSize")
    public long getApkSize() {
        return apkSize;
    }

    @JSONField(name = "iSize")
    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    @JSONField(name = "cVersionName")
    public String getVersionName() {
        return versionName;
    }

    @JSONField(name = "cVersionName")
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @JSONField(name = "iVersionCode")
    public int getVersionCode() {
        return versionCode;
    }

    @JSONField(name = "iVersionCode")
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @JSONField(name = "cPackage")
    public String getPkgName() {
        return pkgName;
    }

    @JSONField(name = "cPackage")
    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    @JSONField(name = "cFlowFree")
    public String getcFlowFree() {
        return cFlowFree;
    }

    @JSONField(name = "cFlowFree")
    public void setcFlowFree(String cFlowFree) {
        this.cFlowFree = cFlowFree;
    }


    @JSONField(name = "cMd5")
    public String getMd5() {
        return md5;
    }

    @JSONField(name = "cMd5")
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Nullable
    @JSONField(name = "sAppDesc")
    public String getsAppDesc() {
        return sAppDesc;
    }

    @Nullable
    @JSONField(name = "sAppDesc")
    public void setsAppDesc(String sAppDesc) {
        this.sAppDesc = sAppDesc;
    }

    @Nullable
    @JSONField(name = "sInfo")
    public String getsInfo() {
        return sInfo;
    }

    @Nullable
    @JSONField(name = "sInfo")
    public void setsInfo(String sInfo) {
        this.sInfo = sInfo;
    }

    @Nullable
    @JSONField(name = "cType")
    public String getcType() {
        return cType;
    }

    @Nullable
    @JSONField(name = "cType")
    public void setcType(String cType) {
        this.cType = cType;
    }

    @Nullable
    @JSONField(name = "cPicUrl")
    public String getcPicUrl() {
        return cPicUrl;
    }

    @Nullable
    @JSONField(name = "cPicUrl")
    public void setcPicUrl(String cPicUrl) {
        this.cPicUrl = cPicUrl;
    }

    @Nullable
    @JSONField(name = "cPicSmall")
    public String getcPicSmall() {
        return cPicSmall;
    }

    @Nullable
    @JSONField(name = "cPicSmall")
    public void setcPicSmall(String cPicSmall) {
        this.cPicSmall = cPicSmall;
    }

    @Nullable
    @JSONField(name = "cMark")
    public String getcMark() {
        return cMark;
    }

    @Nullable
    @JSONField(name = "cMark")
    public void setcMark(String cMark) {
        this.cMark = cMark;
    }

    @Nullable
    @JSONField(name = "iRefId")
    public int getiRefId() {
        return iRefId;
    }

    @Nullable
    @JSONField(name = "iRefId")
    public void setiRefId(int iRefId) {
        this.iRefId = iRefId;
    }

    @Nullable
    @JSONField(name = "cHtmlUrl")
    public String getcHtmlUrl() {
        return cHtmlUrl;
    }

    @Nullable
    @JSONField(name = "cHtmlUrl")
    public void setcHtmlUrl(String cHtmlUrl) {
        this.cHtmlUrl = cHtmlUrl;
    }

    @Nullable
    @JSONField(name = "iCategoryId")
    public int getiCategoryId() {
        return iCategoryId;
    }

    @Nullable
    @JSONField(name = "iCategoryId")
    public void setiCategoryId(int iCategoryId) {
        this.iCategoryId = iCategoryId;
    }


    public String getcAppType() {
        return cAppType;
    }

    @Nullable
    @JSONField(name = "cAppType")
    public void setcAppType(String cAppType) {
        this.cAppType = cAppType;
    }

    public int getiFreeArea() {
        return iFreeArea;
    }

    public void setiFreeArea(int iFreeArea) {
        this.iFreeArea = iFreeArea;
    }

    @Nullable
    @JSONField(name = "cTagPic")
    public String getcIconLabel() {
        return cIconLabel;
    }

    @Nullable
    @JSONField(name = "cTagPic")
    public void setcIconLabel(String cIconLabel) {
        this.cIconLabel = cIconLabel;
    }


    public BaseAppInfo() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appId);
        dest.writeString(this.appName);
        dest.writeString(this.icon);
        dest.writeString(this.apkUrl);
        dest.writeLong(this.apkSize);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.pkgName);
        dest.writeString(this.cFlowFree);
        dest.writeString(this.md5);
        dest.writeString(this.sAppDesc);
        dest.writeString(this.sInfo);
        dest.writeString(this.cType);
        dest.writeString(this.cPicUrl);
        dest.writeString(this.cMark);
        dest.writeInt(this.iRefId);
        dest.writeString(this.cHtmlUrl);
        dest.writeInt(this.iCategoryId);
        dest.writeString(this.cAppType);
        dest.writeInt(this.iFreeArea);
        dest.writeString(this.cIconLabel);
    }

    private BaseAppInfo(Parcel in) {
        this.appId = in.readInt();
        this.appName = in.readString();
        this.icon = in.readString();
        this.apkUrl = in.readString();
        this.apkSize = in.readLong();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.pkgName = in.readString();
        this.cFlowFree = in.readString();
        this.md5 = in.readString();
        this.sAppDesc = in.readString();
        this.sInfo = in.readString();
        this.cType = in.readString();
        this.cPicUrl = in.readString();
        this.cMark = in.readString();
        this.iRefId = in.readInt();
        this.cHtmlUrl = in.readString();
        this.iCategoryId = in.readInt();
        this.cAppType = in.readString();
        this.iFreeArea = in.readInt();
        this.cIconLabel = in.readString();
    }

    public static final Creator<BaseAppInfo> CREATOR = new Creator<BaseAppInfo>() {
        public BaseAppInfo createFromParcel(Parcel source) {
            return new BaseAppInfo(source);
        }

        public BaseAppInfo[] newArray(int size) {
            return new BaseAppInfo[size];
        }
    };
}
