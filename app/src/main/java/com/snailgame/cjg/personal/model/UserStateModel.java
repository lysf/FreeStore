package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * 个人中心统计数据
 * Created by TAJ_C on 2015/10/14.
 */
public class UserStateModel extends BaseDataModel {
    public ModelItem modelItem;

    @JSONField(name = "item")
    public ModelItem getModelItem() {
        return modelItem;
    }

    @JSONField(name = "item")
    public void setModelItem(ModelItem modelItem) {
        this.modelItem = modelItem;
    }


    public static class ModelItem {
        private int unCompletedTaskNum; //未完成的任务数量
        private int userSpreeNum; // 领取的礼包数量
        private int userDownloadNum;// 下载的应用数量
        private int notReceiveCompletedTaskNum;//完成未领取的任务数量

        public int getUnCompletedTaskNum() {
            return unCompletedTaskNum;
        }

        public void setUnCompletedTaskNum(int unCompletedTaskNum) {
            this.unCompletedTaskNum = unCompletedTaskNum;
        }

        public int getUserSpreeNum() {
            return userSpreeNum;
        }

        public void setUserSpreeNum(int userSpreeNum) {
            this.userSpreeNum = userSpreeNum;
        }

        public int getUserDownloadNum() {
            return userDownloadNum;
        }

        public void setUserDownloadNum(int userDownloadNum) {
            this.userDownloadNum = userDownloadNum;
        }

        public int getNotReceiveCompletedTaskNum() {
            return notReceiveCompletedTaskNum;
        }

        public void setNotReceiveCompletedTaskNum(int notReceiveCompletedTaskNum) {
            this.notReceiveCompletedTaskNum = notReceiveCompletedTaskNum;
        }
    }

}
