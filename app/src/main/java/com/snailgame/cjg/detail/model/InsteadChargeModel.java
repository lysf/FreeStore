package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * Created by sunxy on 2015/10/15.
 */
public class InsteadChargeModel extends BaseDataModel {
    List<InsteadCharge>insteadChargeList;
    @JSONField(name = "list")
    public List<InsteadCharge> getInsteadChargeList() {
        return insteadChargeList;
    }
    @JSONField(name = "list")
    public void setInsteadChargeList(List<InsteadCharge> insteadChargeList) {
        this.insteadChargeList = insteadChargeList;
    }
}
