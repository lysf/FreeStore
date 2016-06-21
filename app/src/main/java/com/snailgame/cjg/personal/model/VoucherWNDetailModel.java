package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * 蜗牛代金券明细实体
 * Created by pancl on 2015/4/29.
 */
public class VoucherWNDetailModel extends BaseDataModel {
    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static final class ModelItem {
        private String desc;//消费描述信息
        private String consumeTime;//消费时间
        private int money;//消费金额

        public int getMoney() {
            return money;
        }

        @JSONField(name = "money")
        public void setMoney(int money) {
            this.money = money;
        }

        public String getConsumeTime() {
            return consumeTime;
        }

        @JSONField(name = "consumeTime")
        public void setConsumeTime(String consumeTime) {
            this.consumeTime = consumeTime;
        }

        public String getDesc() {
            return desc;
        }

        @JSONField(name = "desc")
        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
