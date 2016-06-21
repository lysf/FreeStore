package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


/**
 * Created by yftx on 7/19/14.
 */
public class UpdateModel extends BaseDataModel implements Serializable{
    @JSONField(name = "item")
    public ModelItem itemModel;

    public static final class ModelItem implements Serializable{
        String cSize;
        String cIcon;
        String cMd5Code;
        String cApkUrl;
        String nApkId;
        String sDesc;
        String cForceUpdate;
        String cVersion;
        String nAppId;
        String nVersionCode;
        String sName;
        Boolean bUpdate;

        public String getcSize() {
            return cSize;
        }

        public String getcIcon() {
            return cIcon;
        }

        public String getcMd5Code() {
            return cMd5Code;
        }

        public String getcApkUrl() {
            return cApkUrl;
        }

        public String getnApkId() {
            return nApkId;
        }

        public String getsDesc() {
            return sDesc;
        }

        public String getcForceUpdate() {
            return cForceUpdate;
        }

        public String getcVersion() {
            return cVersion;
        }

        public String getnAppId() {
            return nAppId;
        }

        public String getnVersionCode() {
            return nVersionCode;
        }

        public String getsName() {
            return sName;
        }

        public Boolean getbUpdate() {
            return bUpdate;
        }

        @JSONField(name = "cSize")
        public void setcSize(String cSize) {
            this.cSize = cSize;
        }

        @JSONField(name = "cIcon")
        public void setcIcon(String cIcon) {
            this.cIcon = cIcon;
        }

        @JSONField(name = "cMd5Code")
        public void setcMd5Code(String cMd5Code) {
            this.cMd5Code = cMd5Code;
        }

        @JSONField(name = "cApkUrl")
        public void setcApkUrl(String cApkUrl) {
            this.cApkUrl = cApkUrl;
        }

        @JSONField(name = "nApkId")
        public void setnApkId(String nApkId) {
            this.nApkId = nApkId;
        }

        @JSONField(name = "sDesc")
        public void setsDesc(String sDesc) {
            this.sDesc = sDesc;
        }

        @JSONField(name = "cForceUpdate")
        public void setcForceUpdate(String cForceUpdate) {
            this.cForceUpdate = cForceUpdate;
        }

        @JSONField(name = "cVersion")
        public void setcVersion(String cVersion) {
            this.cVersion = cVersion;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(String nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name = "nVersionCode")
        public void setnVersionCode(String nVersionCode) {
            this.nVersionCode = nVersionCode;
        }

        @JSONField(name = "sName")
        public void setsName(String sName) {
            this.sName = sName;
        }

        @JSONField(name = "bUpdate")
        public void setbUpdate(Boolean bUpdate) {
            this.bUpdate = bUpdate;
        }

    }
}
