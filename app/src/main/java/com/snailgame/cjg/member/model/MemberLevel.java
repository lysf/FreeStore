package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lic on 2015/12/28.
 * 会员等级
 */
public class MemberLevel {
    String sLevelName;// 等级名称
    int iPointEnd;// 最大点数, 空为没有上限
    String dCreate;// 生成时间
    int iPointStart;// 最小点数
    String dUpdate;// 更新时间
    String cPrivileges; // 特权id, 多个逗号分隔
    int iLevelId; // 等级ID
    private String sIconLarger; // 大图
    private String sIconSmall; // 小图

    @JSONField(name = "sLevelName")
    public String getsLevelName() {
        return sLevelName;
    }

    @JSONField(name = "iPointEnd")
    public int getiPointEnd() {
        return iPointEnd;
    }

    @JSONField(name = "dCreate")
    public String getdCreate() {
        return dCreate;
    }

    @JSONField(name = "iPointStart")
    public int getiPointStart() {
        return iPointStart;
    }

    @JSONField(name = "dUpdate")
    public String getdUpdate() {
        return dUpdate;
    }

    @JSONField(name = "cPrivileges")
    public String getcPrivileges() {
        return cPrivileges;
    }

    @JSONField(name = "iLevelId")
    public int getiLevelId() {
        return iLevelId;
    }

    @JSONField(name = "sIconLarger")
    public String getsIconLarger() {
        return sIconLarger;
    }

    @JSONField(name = "sIconSmall")
    public String getsIconSmall() {
        return sIconSmall;
    }

    @JSONField(name = "sLevelName")
    public void setsLevelName(String sLevelName) {
        this.sLevelName = sLevelName;
    }

    @JSONField(name = "iPointEnd")
    public void setiPointEnd(int iPointEnd) {
        this.iPointEnd = iPointEnd;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "iPointStart")
    public void setiPointStart(int iPointStart) {
        this.iPointStart = iPointStart;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "cPrivileges")
    public void setcPrivileges(String cPrivileges) {
        this.cPrivileges = cPrivileges;
    }

    @JSONField(name = "iLevelId")
    public void setiLevelId(int iLevelId) {
        this.iLevelId = iLevelId;
    }

    @JSONField(name = "sIconLarger")
    public void setsIconLarger(String sIconLarger) {
        this.sIconLarger = sIconLarger;
    }

    @JSONField(name = "sIconSmall")
    public void setsIconSmall(String sIconSmall) {
        this.sIconSmall = sIconSmall;
    }
}
