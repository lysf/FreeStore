package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by taj on 2014/11/18.
 */
public class CommentCommitModel extends BaseDataModel {
    public ModelItem itemModel;

    @JSONField(name = "item")
    public ModelItem getItemModel() {
        return itemModel;
    }

    @JSONField(name = "item")
    public void setItemModel(ModelItem itemModel) {
        this.itemModel = itemModel;
    }

    public static class ModelItem {
        int iScore;
        String dCreate;
        String sContent;
        String nUserId;
        int iPlatformId;
        String cStatus;
        String dUpdate;
        String nAppId;

        public int getiScore() {
            return iScore;
        }

        public String getdCreate() {
            return dCreate;
        }

        public String getsContent() {
            return sContent;
        }

        public String getnUserId() {
            return nUserId;
        }

        public int getiPlatformId() {
            return iPlatformId;
        }

        public String getcStatus() {
            return cStatus;
        }

        public String getdUpdate() {
            return dUpdate;
        }

        public String getnAppId() {
            return nAppId;
        }

        @JSONField(name = "iScore")
        public void setiScore(int iScore) {
            this.iScore = iScore;
        }

        @JSONField(name = "dCreate")
        public void setdCreate(String dCreate) {
            this.dCreate = dCreate;
        }

        @JSONField(name = "sContent")
        public void setsContent(String sContent) {
            this.sContent = sContent;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(String nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(String nAppId) {
            this.nAppId = nAppId;
        }
    }
}
