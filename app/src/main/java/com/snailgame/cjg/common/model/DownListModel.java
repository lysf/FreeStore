package com.snailgame.cjg.common.model;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * User: sunxy
 * Date: 14-2-22
 * Time: 下午3:30
 */
public class DownListModel extends BaseDataModel{

    public List<BaseAppInfo> gameList;

    @JSONField(name = "list")
    public List<BaseAppInfo> getGameList() {
        return gameList;
    }
    @JSONField(name = "list")
    public void setGameList(List<BaseAppInfo> gameList) {
        this.gameList = gameList;
    }
}
