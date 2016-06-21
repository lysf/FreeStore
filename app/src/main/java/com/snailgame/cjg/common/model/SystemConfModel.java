package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by sunxy on 14-3-28.
 */
public class SystemConfModel extends BaseDataModel{
    protected List<ModelItem> itemList;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }
    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem {
        String dCreate;
        String cType;
        String cDelFlag;
        String dUpdate;
        int iPlatformId;
        int cStatus; // 状态(1.开启，0.关闭)
        String sConf; // 配置信息
        String cFuncName; // 功能关键字(key)
        String sFuncName; // 功能名称(中文)

        @JSONField(name = "cStatus")
        public int getcStatus() {
            return cStatus;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(int cStatus) {
            this.cStatus = cStatus;
        }

        @JSONField(name = "sConf")
        public String getsConf() {
            return sConf;
        }

        @JSONField(name = "sConf")
        public void setsConf(String sConf) {
            this.sConf = sConf;
        }

        @JSONField(name = "cFuncName")
        public String getcFuncName() {
            return cFuncName;
        }

        @JSONField(name = "cFuncName")
        public void setcFuncName(String cFuncName) {
            this.cFuncName = cFuncName;
        }

        @JSONField(name = "sFuncName")
        public String getsFuncName() {
            return sFuncName;
        }

        @JSONField(name = "sFuncName")
        public void setsFuncName(String sFuncName) {
            this.sFuncName = sFuncName;
        }

        public String getdCreate() {
            return dCreate;
        }

        public String getcType() {
            return cType;
        }

        public String getcDelFlag() {
            return cDelFlag;
        }

        public String getdUpdate() {
            return dUpdate;
        }

        public int getiPlatformId() {
            return iPlatformId;
        }

        @JSONField(name = "dCreate")
        public void setdCreate(String dCreate) {
            this.dCreate = dCreate;
        }

        @JSONField(name = "cType")
        public void setcType(String cType) {
            this.cType = cType;
        }

        @JSONField(name = "cDelFlag")
        public void setcDelFlag(String cDelFlag) {
            this.cDelFlag = cDelFlag;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }
    }

}
