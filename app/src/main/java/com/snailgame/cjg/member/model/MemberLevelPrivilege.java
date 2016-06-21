package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lic on 2015/12/28.
 * 会员等级特权
 */
public class MemberLevelPrivilege {

    String sPrivilegeName;// 特权名称
    String dCreate;// 生成时间
    String sDesc;// 描述
    String cConfig;// 标准配置格式
    String dUpdate;// 更新时间
    int iPrivilegeId;// 特权ID
    String cType;// 特权类型

    @JSONField(name = "sPrivilegeName")
    public String getsPrivilegeName() {
        return sPrivilegeName;
    }

    @JSONField(name = "dCreate")
    public String getdCreate() {
        return dCreate;
    }

    @JSONField(name = "sDesc")
    public String getsDesc() {
        return sDesc;
    }

    @JSONField(name = "cConfig")
    public String getcConfig() {
        return cConfig;
    }

    @JSONField(name = "dUpdate")
    public String getdUpdate() {
        return dUpdate;
    }

    @JSONField(name = "iPrivilegeId")
    public int getiPrivilegeId() {
        return iPrivilegeId;
    }

    @JSONField(name = "cType")
    public String getcType() {
        return cType;
    }

    @JSONField(name = "sPrivilegeName")
    public void setsPrivilegeName(String sPrivilegeName) {
        this.sPrivilegeName = sPrivilegeName;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "sDesc")
    public void setsDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    @JSONField(name = "cConfig")
    public void setcConfig(String cConfig) {
        this.cConfig = cConfig;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "iPrivilegeId")
    public void setiPrivilegeId(int iPrivilegeId) {
        this.iPrivilegeId = iPrivilegeId;
    }

    @JSONField(name = "cType")
    public void setcType(String cType) {
        this.cType = cType;
    }
}
