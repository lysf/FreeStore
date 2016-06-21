package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * 蜗牛代金券接口实体
 * Created by pancl on 2015/6/23.
 */
public class MyVoucherWNModel extends BaseDataModel {
    private VoucherWNModel item;

    public VoucherWNModel getItem() {
        return item;
    }

    @JSONField(name = "item")
    public void setItem(VoucherWNModel item) {
        this.item = item;
    }
}
