package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by TAJ_C on 2016/2/15.
 */
public class AppAppointmentStateModel extends BaseDataModel {
    @JSONField(name = "item")
    String item;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
