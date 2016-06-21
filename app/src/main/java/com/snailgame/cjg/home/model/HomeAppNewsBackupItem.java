package com.snailgame.cjg.home.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by TAJ_C on 2015/5/12.
 */
public class HomeAppNewsBackupItem {
    String p1; //标识是否大图（1是0否）,
    String p2;  //标识 标签
    String p3;  //p3标识颜色

    @JSONField(name = "p1")
    public String getP1() {
        return p1;
    }

    @JSONField(name = "p1")
    public void setP1(String p1) {
        this.p1 = p1;
    }

    @JSONField(name = "p2")
    public String getP2() {
        return p2;
    }

    @JSONField(name = "p2")
    public void setP2(String p2) {
        this.p2 = p2;
    }

    @JSONField(name = "p3")
    public String getP3() {
        return p3;
    }

    @JSONField(name = "p3")
    public void setP3(String p3) {
        this.p3 = p3;
    }
}
