package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * 代金卷数量
 * Created by TAJ_C on 2015/6/25.
 */
public class VoucherModel extends BaseDataModel {

    ModelItem itemModel;

    @JSONField(name = "item")
    public ModelItem getItemModel() {
        return itemModel;
    }

    @JSONField(name = "item")
    public void setItemModel(ModelItem itemModel) {
        this.itemModel = itemModel;
    }


    public static class ModelItem {
        int freestoreVoucher;//免商店代金券
        int wnVoucher;//蜗牛券
        int kuwanVoucher;//酷玩代金券
        int totalVoucher;//总代金券数量

        @JSONField(name = "freestoreVoucher")
        public int getFreestoreVoucher() {
            return freestoreVoucher;
        }

        @JSONField(name = "freestoreVoucher")
        public void setFreestoreVoucher(int freestoreVoucher) {
            this.freestoreVoucher = freestoreVoucher;
        }

        @JSONField(name = "wnVoucher")
        public int getWnVoucher() {
            return wnVoucher;
        }

        @JSONField(name = "wnVoucher")
        public void setWnVoucher(int wnVoucher) {
            this.wnVoucher = wnVoucher;
        }

        @JSONField(name = "kuwanVoucher")
        public int getKuwanVoucher() {
            return kuwanVoucher;
        }

        @JSONField(name = "kuwanVoucher")
        public void setKuwanVoucher(int kuwanVoucher) {
            this.kuwanVoucher = kuwanVoucher;
        }

        @JSONField(name = "totalVoucher")
        public int getTotalVoucher() {
            return totalVoucher;
        }

        @JSONField(name = "totalVoucher")
        public void setTotalVoucher(int totalVoucher) {
            this.totalVoucher = totalVoucher;
        }
    }
}
