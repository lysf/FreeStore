package com.snailgame.cjg.seekgame.recommend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseAppInfo;

/**
 * Created by taj on 14-2-17.
 */
public class RecommendInfo extends BaseAppInfo implements Parcelable {
    private int iBannerId;
    private String cPostion;
    private long nParamId;
    private String sPayDesc;
    private String sExtends;

    public RecommendInfo() {

    }


    public RecommendInfo(BaseAppInfo info) {
        appId = info.getAppId();
        appName = info.getAppName();
        icon = info.getIcon();
        apkUrl = info.getApkUrl();
        apkSize = info.getApkSize();
        versionName = info.getVersionName();
        versionCode = info.getVersionCode();
        pkgName = info.getPkgName();
        cFlowFree = info.getcFlowFree();
        md5 = info.getMd5();
        sAppDesc = info.getsAppDesc();
        sInfo = info.getsInfo();
        cType = info.getcType();
        cPicUrl = info.getcPicUrl();
        cMark = info.getcMark();
        iRefId = info.getiRefId();
        cHtmlUrl = info.getcHtmlUrl();
        cAppType = info.getcAppType();
    }

    @JSONField(name = "iBannerId")
    public int getiBannerId() {
        return iBannerId;
    }

    @JSONField(name = "iBannerId")
    public void setiBannerId(int iBannerId) {
        this.iBannerId = iBannerId;
    }

    @JSONField(name = "cPostion")
    public String getcPostion() {
        return cPostion;
    }

    @JSONField(name = "cPostion")
    public void setcPostion(String cPostion) {
        this.cPostion = cPostion;
    }

    @JSONField(name = "nParamId")
    public long getnParamId() {
        return nParamId;
    }

    @JSONField(name = "nParamId")
    public void setnParamId(long nParamId) {
        this.nParamId = nParamId;
    }

    public int getiCategoryId() {
        return iCategoryId;
    }

    @JSONField(name = "iCategoryId")
    public void setiCategoryId(int iCategoryId) {
        this.iCategoryId = iCategoryId;
    }

    @JSONField(name = "sPayDesc")
    public String getsPayDesc() {
        return sPayDesc;
    }

    @JSONField(name = "sPayDesc")
    public void setsPayDesc(String sPayDesc) {
        this.sPayDesc = sPayDesc;
    }

    @JSONField(name = "sExtends")
    public String getsExtends() {
        return sExtends;
    }

    @JSONField(name = "sExtends")
    public void setsExtends(String sExtends) {
        this.sExtends = sExtends;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iBannerId);
        dest.writeString(this.cPostion);
        dest.writeLong(this.nParamId);
        dest.writeInt(this.iCategoryId);
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
        dest.writeString(this.sPayDesc);
        dest.writeString(this.cAppType);
        dest.writeString(this.sExtends);
    }

    private RecommendInfo(Parcel in) {
        this.iBannerId = in.readInt();
        this.cPostion = in.readString();
        this.nParamId = in.readLong();
        this.iCategoryId = in.readInt();
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
        this.sPayDesc = in.readString();
        this.cAppType = in.readString();
        this.sExtends = in.readString();
    }

    public static final Creator<RecommendInfo> CREATOR = new Creator<RecommendInfo>() {
        public RecommendInfo createFromParcel(Parcel source) {
            return new RecommendInfo(source);
        }

        public RecommendInfo[] newArray(int size) {
            return new RecommendInfo[size];
        }
    };
}
