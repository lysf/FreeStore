package com.snailgame.cjg.event;

import com.snailgame.cjg.common.model.AppInfo;

import java.util.List;

/**
 * Created by lic on 2015/3/9.
 * 对my_game_table数据库的增删改查后发送的更新UI的广播
 */
public class MyGameDBChangeEvent extends BaseEvent {

    private List<AppInfo> appInfoList;

    public MyGameDBChangeEvent(List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
    }
    public List<AppInfo> getAppInfoList() {
        return appInfoList;
    }
}
