package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by sunxy on 14-7-8.
 */
public class FreeGameModel {

    public List<FreeGameItem> infos = new ArrayList<FreeGameItem>();

    @JSONField(name = "list")
    public List<FreeGameItem> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(List<FreeGameItem> infos) {
        this.infos = infos;
    }
}
