package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by TAJ_C on 2015/11/9.
 */
public class TaskReceiveModel extends BaseDataModel{
    @JSONField(name = "val")
    boolean isVal;

    public boolean isVal() {
        return isVal;
    }

    public void setIsVal(boolean isVal) {
        this.isVal = isVal;
    }
}
