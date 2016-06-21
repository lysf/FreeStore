package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by chenping1 on 2014/6/20.
 */
public class UserInfoModel extends BaseDataModel {
    public UserInfo itemModel;

    @JSONField(name = "item")
    public UserInfo getItemModel() {
        return itemModel;
    }

    @JSONField(name = "item")
    public void setItemModel(UserInfo itemModel) {
        this.itemModel = itemModel;
    }

}
