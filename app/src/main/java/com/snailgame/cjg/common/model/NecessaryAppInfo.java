package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 装机必备
 * Created by lic
 */
public class NecessaryAppInfo {
    private int nAppId;
    private String sAppName;
    private int iCategoryId;
    private String sCategoryName;
    private String cIcon;
    private long iSize;
    private String sAppDesc;
    private String cFlowFree;
    private String cAppType;
    private String iVersionCode;
    private String cVersionName;
    private String cPackage;
    private String cDownloadUrl;
    private String cMd5;
    private String cPosterIcon;
    private String cPosterPic;
    private String cOs;
    private boolean isSelected = true;//装机必备中的程序被选中状态，默认选中

    @JSONField(name = "nAppId")
    public int getnAppId() {
        return nAppId;
    }

    @JSONField(name = "sAppName")
    public String getsAppName() {
        return sAppName;
    }

    @JSONField(name = "iCategoryId")
    public int getiCategoryId() {
        return iCategoryId;
    }

    @JSONField(name = "sCategoryName")
    public String getsCategoryName() {
        return sCategoryName;
    }

    @JSONField(name = "cIcon")
    public String getcIcon() {
        return cIcon;
    }

    @JSONField(name = "iSize")
    public long getiSize() {
        return iSize;
    }

    @JSONField(name = "sAppDesc")
    public String getsAppDesc() {
        return sAppDesc;
    }

    @JSONField(name = "cFlowFree")
    public String getcFlowFree() {
        return cFlowFree;
    }

    @JSONField(name = "cAppType")
    public String getcAppType() {
        return cAppType;
    }

    @JSONField(name = "iVersionCode")
    public String getiVersionCode() {
        return iVersionCode;
    }

    @JSONField(name = "cVersionName")
    public String getcVersionName() {
        return cVersionName;
    }

    @JSONField(name = "cPackage")
    public String getcPackage() {
        return cPackage;
    }

    @JSONField(name = "cDownloadUrl")
    public String getcDownloadUrl() {
        return cDownloadUrl;
    }

    @JSONField(name = "cMd5")
    public String getcMd5() {
        return cMd5;
    }

    @JSONField(name = "cPosterIcon")
    public String getcPosterIcon() {
        return cPosterIcon;
    }

    @JSONField(name = "cPosterPic")
    public String getcPosterPic() {
        return cPosterPic;
    }

    @JSONField(name = "cOs")
    public String getcOs() {
        return cOs;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name = "sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name = "iCategoryId")
    public void setiCategoryId(int iCategoryId) {
        this.iCategoryId = iCategoryId;
    }

    @JSONField(name = "sCategoryName")
    public void setsCategoryName(String sCategoryName) {
        this.sCategoryName = sCategoryName;
    }

    @JSONField(name = "cIcon")
    public void setcIcon(String cIcon) {
        this.cIcon = cIcon;
    }

    @JSONField(name = "iSize")
    public void setiSize(long iSize) {
        this.iSize = iSize;
    }

    @JSONField(name = "sAppDesc")
    public void setsAppDesc(String sAppDesc) {
        this.sAppDesc = sAppDesc;
    }

    @JSONField(name = "cFlowFree")
    public void setcFlowFree(String cFlowFree) {
        this.cFlowFree = cFlowFree;
    }

    @JSONField(name = "cAppType")
    public void setcAppType(String cAppType) {
        this.cAppType = cAppType;
    }

    @JSONField(name = "iVersionCode")
    public void setiVersionCode(String iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name = "cVersionName")
    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    @JSONField(name = "cPackage")
    public void setcPackage(String cPackage) {
        this.cPackage = cPackage;
    }

    @JSONField(name = "cDownloadUrl")
    public void setcDownloadUrl(String cDownloadUrl) {
        this.cDownloadUrl = cDownloadUrl;
    }

    @JSONField(name = "cMd5")
    public void setcMd5(String cMd5) {
        this.cMd5 = cMd5;
    }

    @JSONField(name = "cPosterIcon")
    public void setcPosterIcon(String cPosterIcon) {
        this.cPosterIcon = cPosterIcon;
    }

    @JSONField(name = "cPosterPic")
    public void setcPosterPic(String cPosterPic) {
        this.cPosterPic = cPosterPic;
    }

    @JSONField(name = "cOs")
    public void setcOs(String cOs) {
        this.cOs = cOs;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
