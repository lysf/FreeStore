package com.snailgame.cjg.communication.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lic on 2015/1/22.
 * 通讯模块的对象中的内容的对象
 */
public class CommunicationChildContentModel {

    private int iId;//内容id
    private String dCreate;
    private int iOrder;//顺序
    private String cDelFlag;
    private String cStatus;
    private String dUpdate;
    private String dTime;
    private String cSource;//1-自定义 2-礼包 3-游戏推荐 4-合集 5-活动 6-攻略 7-资讯 8-论坛 9-游戏 10-积分墙 11-应用
    private int iHeadlineId;
    private String sRefId;//引用主键
    private String sTitle;//标题
    private String sSubtitle;//副标题
    private String sSummary;//简介
    private String sImageUrl;//图片地址
    private String sJumpUrl;//点击跳转
    private String sExtend;//引用数据扩展json参数，参照以前
    private String dStart;//开始时间
    private String dEnd;//结束时间
    private String cJumpType;//跳转类型-字典

    public int getiId() {
        return iId;
    }

    public String getdCreate() {
        return dCreate;
    }

    public int getiOrder() {
        return iOrder;
    }


    public String getcDelFlag() {
        return cDelFlag;
    }

    public String getcStatus() {
        return cStatus;
    }

    public String getdUpdate() {
        return dUpdate;
    }


    public String getcSource() {
        return cSource;
    }

    public int getiHeadlineId() {
        return iHeadlineId;
    }

    public String getsRefId() {
        return sRefId;
    }

    public String getsTitle() {
        return sTitle;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public String getsSummary() {
        return sSummary;
    }

    public String getsImageUrl() {
        return sImageUrl;
    }


    public String getsJumpUrl() {
        return sJumpUrl;
    }

    public String getsExtend() {
        return sExtend;
    }

    public String getdStart() {
        return dStart;
    }

    public String getdEnd() {
        return dEnd;
    }

    public String getcJumpType() {
        return cJumpType;
    }

    public String getdTime() {
        return dTime;
    }

    @JSONField(name = "iId")
    public void setiId(int iId) {
        this.iId = iId;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(int iOrder) {
        this.iOrder = iOrder;
    }


    @JSONField(name = "cDelFlag")
    public void setcDelFlag(String cDelFlag) {
        this.cDelFlag = cDelFlag;
    }

    @JSONField(name = "cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "cSource")
    public void setcSource(String cSource) {
        this.cSource = cSource;
    }

    @JSONField(name = "iHeadlineId")
    public void setiHeadlineId(int iHeadlineId) {
        this.iHeadlineId = iHeadlineId;
    }

    @JSONField(name = "sRefId")
    public void setsRefId(String sRefId) {
        this.sRefId = sRefId;
    }

    @JSONField(name = "sTitle")
    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    @JSONField(name = "sSubtitle")
    public void setsSubtitle(String sSubtitle) {
        this.sSubtitle = sSubtitle;
    }

    @JSONField(name = "sSummary")
    public void setsSummary(String sSummary) {
        this.sSummary = sSummary;
    }

    @JSONField(name = "sImageUrl")
    public void setsImageUrl(String sImageUrl) {
        this.sImageUrl = sImageUrl;
    }

    @JSONField(name = "sJumpUrl")
    public void setsJumpUrl(String sJumpUrl) {
        this.sJumpUrl = sJumpUrl;
    }

    @JSONField(name = "sExtend")
    public void setsExtend(String sExtend) {
        this.sExtend = sExtend;
    }

    @JSONField(name = "dStart")
    public void setdStart(String dStart) {
        this.dStart = dStart;
    }

    @JSONField(name = "dEnd")
    public void setdEnd(String dEnd) {
        this.dEnd = dEnd;
    }

    @JSONField(name = "cJumpType")
    public void setcJumpType(String cJumpType) {
        this.cJumpType = cJumpType;
    }

    @JSONField(name = "dTime")
    public void setdTime(String dTime) {
        this.dTime = dTime;
    }
}
