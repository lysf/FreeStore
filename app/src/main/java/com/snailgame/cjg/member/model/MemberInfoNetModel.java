package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * 会员信息网络数据
 * Created by lic on 2015/12/21.
 */
public class MemberInfoNetModel extends BaseDataModel {

    String val;
    MemberInfoModel memberInfoModel;

    @JSONField(name = "val")
    public String getVal() {
        return val;
    }

    @JSONField(name = "item")
    public MemberInfoModel getMemberInfoModel() {
        return memberInfoModel;
    }

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }

    @JSONField(name = "item")
    public void setMemberInfoModel(MemberInfoModel memberInfoModel) {
        this.memberInfoModel = memberInfoModel;
    }
}
