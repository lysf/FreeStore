package com.snailgame.cjg.manage.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xixh on 14-7-9.
 */
public class TreasureBoxInfoModel extends BaseDataModel {
    public List<TreasureBoxInfo> treasureBoxInfos = new ArrayList<TreasureBoxInfo>();
    public PageInfo pageInfo;

    @JSONField(name = "list")
    public List<TreasureBoxInfo> getTreasureBoxInfos() {
        return treasureBoxInfos;
    }

    @JSONField(name = "list")
    public void setTreasureBoxInfos(List<TreasureBoxInfo> treasureBoxInfos) {
        this.treasureBoxInfos = treasureBoxInfos;
    }
}
