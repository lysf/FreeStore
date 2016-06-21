package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by sunxy on 14-7-8.
 */
public class FreeGameItem {
    int nAppId;
    String sAppName;
    String cMd5;
    String iVersionCode;
    String cPackage;
    String cDownloadUrl;
    String cVersionName;
    String cFlowFree;//-1代表不免

    public int getnAppId() {
        return nAppId;
    }

    public String getsAppName() {
        return sAppName;
    }

    public String getcMd5() {
        return cMd5;
    }

    public String getiVersionCode() {
        return iVersionCode;
    }

    public String getcPackage() {
        return cPackage;
    }

    public String getcDownloadUrl() {
        return cDownloadUrl;
    }

    public String getcVersionName() {
        return cVersionName;
    }

    public String getcFlowFree() {
        return cFlowFree;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name = "sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name = "cMd5")
    public void setcMd5(String cMd5) {
        this.cMd5 = cMd5;
    }

    @JSONField(name = "iVersionCode")
    public void setiVersionCode(String iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name = "cPackage")
    public void setcPackage(String cPackage) {
        this.cPackage = cPackage;
    }

    @JSONField(name = "cDownloadUrl")
    public void setcDownloadUrl(String cDownloadUrl) {
        this.cDownloadUrl = cDownloadUrl;
    }

    @JSONField(name = "cVersionName")
    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    @JSONField(name = "cFlowFree")
    public void setcFlowFree(String cFlowFree) {
        this.cFlowFree = cFlowFree;
    }
}
