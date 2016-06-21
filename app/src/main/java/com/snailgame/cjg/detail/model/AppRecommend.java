package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 应用推荐
 * Created by xixh on 14-12-25.
 */
public class AppRecommend {
    private int iPlatformId;
    private int iVersionCode;//游戏版本
    private int nAppId;//游戏id
    private String cRecommend;
    private int iCategoryId;
    private String sAppName;//游戏名称
    private String cIcon; // 游戏icon url
    private String cPackage; //应用包名
    private int iSize;
    private String cAppType;
    private String cMd5;
    private String cVersionName;
    private String cDownloadUrl;


    @JSONField(name = "nAppId")
    public int getnAppId() {
        return nAppId;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name = "sAppName")
    public String getsAppName() {
        return sAppName;
    }

    @JSONField(name = "sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name = "cIcon")
    public String getcIcon() {
        return cIcon;
    }

    @JSONField(name = "cIcon")
    public void setcIcon(String cIcon) {
        this.cIcon = cIcon;
    }

    @JSONField(name = "iVersionCode")
    public int getiVersionCode() {
        return iVersionCode;
    }

    @JSONField(name = "iVersionCode")
    public void setiVersionCode(int iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name = "cPackage")
    public String getcPackage() {
        return cPackage;
    }

    @JSONField(name = "cPackage")
    public void setcPackage(String cPackage) {
        this.cPackage = cPackage;
    }

    @JSONField(name = "iPlatformId")
    public int getiPlatformId() {
        return iPlatformId;
    }

    @JSONField(name = "cRecommend")
    public String getcRecommend() {
        return cRecommend;
    }

    @JSONField(name = "iCategoryId")
    public int getiCategoryId() {
        return iCategoryId;
    }

    @JSONField(name = "iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
    }

    @JSONField(name = "cRecommend")
    public void setcRecommend(String cRecommend) {
        this.cRecommend = cRecommend;
    }

    @JSONField(name = "iCategoryId")
    public void setiCategoryId(int iCategoryId) {
        this.iCategoryId = iCategoryId;
    }

    public int getiSize() {
        return iSize;
    }

    public String getcAppType() {
        return cAppType;
    }

    public String getcMd5() {
        return cMd5;
    }

    public String getcVersionName() {
        return cVersionName;
    }

    public String getcDownloadUrl() {
        return cDownloadUrl;
    }

    public void setiSize(int iSize) {
        this.iSize = iSize;
    }

    public void setcAppType(String cAppType) {
        this.cAppType = cAppType;
    }

    public void setcMd5(String cMd5) {
        this.cMd5 = cMd5;
    }

    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    public void setcDownloadUrl(String cDownloadUrl) {
        this.cDownloadUrl = cDownloadUrl;
    }
}
