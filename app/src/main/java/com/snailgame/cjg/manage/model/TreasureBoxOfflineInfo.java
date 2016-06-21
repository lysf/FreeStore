package com.snailgame.cjg.manage.model;

/**
 * 百宝箱没有获取到服务器数据默认的几个功能
 * Created by lic on 15-9-25.
 */
public class TreasureBoxOfflineInfo {

    public final static int TRAFFIC_STATISTICS_ID = 0;//流量统计
    public final static int TRAFFIC_CONTROL_ID = 1;//联网控制
    public final static int APP_MANAGE_ID = 2;//应用管理
    public final static int RECOMMEND_ID = 3;//推荐好友
    public final static int SETTING_ID = 4;//商店设置

    //标记点击的id
    public int id;
    //图片资源
    public int resId;
    //功能名称
    public String name;

    public TreasureBoxOfflineInfo(int id, int resId, String name) {
        this.id = id;
        this.resId = resId;
        this.name = name;

    }

}
