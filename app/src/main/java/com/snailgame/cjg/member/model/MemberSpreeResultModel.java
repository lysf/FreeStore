package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by TAJ_C on 2015/12/28.
 */
public class MemberSpreeResultModel extends BaseDataModel{

    @JSONField(name = "item")
    private String item;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
