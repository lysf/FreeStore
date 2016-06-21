package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by lic on 2015/12/28.
 * 会员信息
 */
public class MemberInfoModel {
    String currentPoint; // 当前会员点
    List<MemberLevelPrivilege> currentMemberLevelPrivileges;// 当前会员等级特权
    MemberLevel nextMemberLevel;// 下一级会员等级信息
    List<MemberLevelPrivilege> nextMemberLevelPrivileges;//下一级会员等级特权
    MemberLevel currentlevel;// 当前会员等级信息

    @JSONField(name = "currentPoint")
    public String getCurrentPoint() {
        return currentPoint;
    }

    @JSONField(name = "currentLevelPrivileges")
    public List<MemberLevelPrivilege> getCurrentMemberLevelPrivileges() {
        return currentMemberLevelPrivileges;
    }

    @JSONField(name = "nextLevel")
    public MemberLevel getNextMemberLevel() {
        return nextMemberLevel;
    }

    @JSONField(name = "nextLevelPrivileges")
    public List<MemberLevelPrivilege> getNextMemberLevelPrivileges() {
        return nextMemberLevelPrivileges;
    }

    @JSONField(name = "currentlevel")
    public MemberLevel getCurrentlevel() {
        return currentlevel;
    }

    @JSONField(name = "currentPoint")
    public void setCurrentPoint(String currentPoint) {
        this.currentPoint = currentPoint;
    }

    @JSONField(name = "currentLevelPrivileges")
    public void setCurrentMemberLevelPrivileges(List<MemberLevelPrivilege> currentMemberLevelPrivileges) {
        this.currentMemberLevelPrivileges = currentMemberLevelPrivileges;
    }

    @JSONField(name = "nextLevel")
    public void setNextMemberLevel(MemberLevel nextMemberLevel) {
        this.nextMemberLevel = nextMemberLevel;
    }

    @JSONField(name = "nextLevelPrivileges")
    public void setNextMemberLevelPrivileges(List<MemberLevelPrivilege> nextMemberLevelPrivileges) {
        this.nextMemberLevelPrivileges = nextMemberLevelPrivileges;
    }

    @JSONField(name = "currentlevel")
    public void setCurrentlevel(MemberLevel currentlevel) {
        this.currentlevel = currentlevel;
    }

}
