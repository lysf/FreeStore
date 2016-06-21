package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by xixh on 2015/1/27.
 */
public class AppDetailResp extends BaseDataModel {
    private AppDetailModel item;

    public AppDetailModel getItem() {
        return item;
    }

    @JSONField(name = "item")
    public void setItem(AppDetailModel item) {
        this.item = item;
    }
}
