package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 搜索 -> 游戏应用Model
 * Created by shenzaih on 13-12-16.
 */
public class SearchInfo {
    private int nAppId;//游戏id
    private String sAppName;//游戏名称
    private long iSize;//游戏大小
    private String cIcon; // 游戏icon url
    private String cDownloadUrl;//下载地址
    private int iVersionCode;//游戏版本
    private String cPackage; //应用包名
    private String cFlowFree;//是否免流量
    private String cVersionName;//版本名称
    private String cMd5;
    private String cStatus ;
    private String sAppDesc;//应用描述
    private String cAppType;

    @JSONField(name = "cStatus")
    public String getcStatus() {
        return cStatus;
    }

    @JSONField(name = "cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    @JSONField(name="cVersionName")
    public String getcVersionName() {
        return cVersionName;
    }

    @JSONField(name="cVersionName")
    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    @JSONField(name="nAppId")
    public int getnAppId() {
        return nAppId;
    }

    @JSONField(name="nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name="sAppName")
    public String getsAppName() {
        return sAppName;
    }

    @JSONField(name="sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name="iSize")
    public long getiSize() {
        return iSize;
    }

    @JSONField(name="iSize")
    public void setiSize(long iSize) {
        this.iSize = iSize;
    }

    @JSONField(name="cIcon")
    public String getcIcon() {
        return cIcon;
    }

    @JSONField(name="cIcon")
    public void setcIcon(String cIcon) {
        this.cIcon = cIcon;
    }

    @JSONField(name="cDownloadUrl")
    public String getcDownloadUrl() {
        return cDownloadUrl;
    }

    @JSONField(name="cDownloadUrl")
    public void setcDownloadUrl(String cDownloadUrl) {
        this.cDownloadUrl = cDownloadUrl;
    }

    @JSONField(name="iVersionCode")
    public int getiVersionCode() {
        return iVersionCode;
    }

    @JSONField(name="iVersionCode")
    public void setiVersionCode(int iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name="cPackage")
    public String getcPackage() {
        return cPackage;
    }

    @JSONField(name="cPackage")
    public void setcPackage(String cPackage) {
        this.cPackage = cPackage;
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
    public String getcMd5() {
        return cMd5;
    }

    @JSONField(name = "cMd5")
    public void setcMd5(String cMd5) {
        this.cMd5 = cMd5;
    }
    
    @JSONField(name = "sAppDesc")
    public String getsAppDesc() {
        return sAppDesc;
    }

    @JSONField(name = "sAppDesc")
    public void setsAppDesc(String sAppDesc) {
        this.sAppDesc = sAppDesc;
    }

    @JSONField(name = "cAppType")
    public String getcAppType() {
        return cAppType;
    }

    @JSONField(name = "cAppType")
    public void setcAppType(String cAppType) {
        this.cAppType = cAppType;
    }
}
