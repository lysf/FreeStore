package com.snailgame.cjg.downloadmanager.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Uesr : MacSzh2013
 * Date : 14-2-22
 * Time : 下午2:30
 * Description :
 */
public class UpgradeAppListModel extends BaseDataModel {

    public List<ModelItem> itemList = new ArrayList<ModelItem>();

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem {
        String iVersionCode;
        String cMd5;
        int iSize;
        int nAppId;
        String cAppType;
        String sAppName;
        String cIcon;
        String cPackage;
        String cFlowFree;
        String cDownloadUrl;
        String cVersionName;
        String updateDesc;
        String cUpdate;
        String cPatch; // 0 无差分 1 可差分
        String cDiffUrl;
        String iDiffSize;
        String cDiffMd5;
        String cPreMd5; // 旧版本客户端的MD5--差异更新用
        int iTotalInstallNum;//累积安装量

        public String getiVersionCode() {
            return iVersionCode;
        }

        public String getcMd5() {
            return cMd5;
        }

        public int getiSize() {
            return iSize;
        }

        public int getnAppId() {
            return nAppId;
        }

        public String getcAppType() {
            return cAppType;
        }

        public String getsAppName() {
            return sAppName;
        }

        public String getcIcon() {
            return cIcon;
        }

        public String getcPackage() {
            return cPackage;
        }

        public String getcFlowFree() {
            return cFlowFree;
        }

        public String getcDownloadUrl() {
            return cDownloadUrl;
        }

        public String getcVersionName() {
            return cVersionName;
        }

        public String getUpdateDesc() {
            return updateDesc;
        }

        public String getcUpdate() {
            return cUpdate;
        }

        public String getcPatch() {
            return cPatch;
        }

        public String getcDiffUrl() {
            return cDiffUrl;
        }

        public int getiTotalInstallNum() {
            return iTotalInstallNum;
        }

        public int getiDiffSize() {
            try {
                return Integer.parseInt(iDiffSize);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public String getcDiffMd5() {
            return cDiffMd5;
        }

        @JSONField(name = "iVersionCode")
        public void setiVersionCode(String iVersionCode) {
            this.iVersionCode = iVersionCode;
        }

        @JSONField(name = "cMd5")
        public void setcMd5(String cMd5) {
            this.cMd5 = cMd5;
        }

        @JSONField(name = "iSize")
        public void setiSize(int iSize) {
            this.iSize = iSize;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(int nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name = "cAppType")
        public void setcAppType(String cAppType) {
            this.cAppType = cAppType;
        }

        @JSONField(name = "sAppName")
        public void setsAppName(String sAppName) {
            this.sAppName = sAppName;
        }

        @JSONField(name = "cIcon")
        public void setcIcon(String cIcon) {
            this.cIcon = cIcon;
        }

        @JSONField(name = "cPackage")
        public void setcPackage(String cPackage) {
            this.cPackage = cPackage;
        }

        @JSONField(name = "cFlowFree")
        public void setcFlowFree(String cFlowFree) {
            this.cFlowFree = cFlowFree;
        }

        @JSONField(name = "cDownloadUrl")
        public void setcDownloadUrl(String cDownloadUrl) {
            this.cDownloadUrl = cDownloadUrl;
        }

        @JSONField(name = "cVersionName")
        public void setcVersionName(String cVersionName) {
            this.cVersionName = cVersionName;
        }

        @JSONField(name = "sUpdateDesc")
        public void setUpdateDesc(String updateDesc) {
            this.updateDesc = updateDesc;
        }

        @JSONField(name = "cUpdate")
        public void setcUpdate(String cUpdate) {
            this.cUpdate = cUpdate;
        }

        @JSONField(name = "cPatch")
        public void setcPatch(String cPatch) {
            this.cPatch = cPatch;
        }

        @JSONField(name = "cDiffUrl")
        public void setcDiffUrl(String cDiffUrl) {
            this.cDiffUrl = cDiffUrl;
        }

        @JSONField(name = "iDiffSize")
        public void setiDiffSize(String iDiffSize) {
            this.iDiffSize = iDiffSize;
        }

        @JSONField(name = "cDiffMd5")
        public void setcDiffMd5(String cDiffMd5) {
            this.cDiffMd5 = cDiffMd5;
        }

        @JSONField(name = "iTotalInstallNum")
        public void setiTotalInstallNum(int iTotalInstallNum) {
            this.iTotalInstallNum = iTotalInstallNum;
        }

        public String getcPreMd5() {
            return cPreMd5;
        }

        public void setcPreMd5(String cPreMd5) {
            this.cPreMd5 = cPreMd5;
        }
    }
}
