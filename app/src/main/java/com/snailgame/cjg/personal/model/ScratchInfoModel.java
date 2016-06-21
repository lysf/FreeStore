package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by lic on 2014/6/20.
 */
public class ScratchInfoModel extends BaseDataModel {
    public ScratchInfo itemModel;

    @JSONField(name = "item")
    public ScratchInfo getItemModel() {
        return itemModel;
    }

    @JSONField(name = "item")
    public void setItemModel(ScratchInfo itemModel) {
        this.itemModel = itemModel;
    }


    public final static class ScratchInfo {
        private int score;
        private int status; //1-----免费抽奖  2-----消耗积分抽奖
        public static final int STATUS_FREE = 1; // 免费抽奖
        @JSONField(name = "iScore")
        public int getScore() {
            return score;
        }

        @JSONField(name = "iScore")
        public void setScore(int score) {
            this.score = score;
        }

        @JSONField(name = "cStatus")
        public int getStatus() {
            return status;
        }

        @JSONField(name = "cStatus")
        public void setStatus(int status) {
            this.status = status;

        }

    }

}
