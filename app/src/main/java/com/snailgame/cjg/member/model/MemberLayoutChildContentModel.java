package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by lic on 2015/12/22.
 * 会员中心页面
 */
public class MemberLayoutChildContentModel {

    public final static String SOURCE_TYPE_PRIVILEGE = "5";
    private Integer iId; // 主键
    private Integer iHeadlineId; // 栏目主键
    private String sRefId; // 引用数据主键
    private String cSource; // 引用来源 数据字典, (同酷玩首页，另：5-会员特权)
    private Integer iOrder; // 顺序 1开始
    private String sTitle; // 主标题
    private String sSubtitle; // 副标题
    private String sImageUrl; // 图片地址
    private String sJumpUrl; // 点击跳转
    private String cJumpType; // 跳转类型
    private String sExtend; // 扩展字段: json格式
    private String dCreate; // 创建时间


    public Integer getiId() {
        return iId;
    }

    public Integer getiHeadlineId() {
        return iHeadlineId;
    }

    public String getsRefId() {
        return sRefId;
    }

    public String getcSource() {
        return cSource;
    }

    public Integer getiOrder() {
        return iOrder;
    }

    public String getsTitle() {
        return sTitle;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public String getsImageUrl() {
        return sImageUrl;
    }

    public String getsJumpUrl() {
        return sJumpUrl;
    }

    public String getcJumpType() {
        return cJumpType;
    }

    public String getsExtend() {
        return sExtend;
    }

    public String getdCreate() {
        return dCreate;
    }

    @JSONField(name = "iId")
    public void setiId(Integer iId) {
        this.iId = iId;
    }

    @JSONField(name = "iHeadlineId")
    public void setiHeadlineId(Integer iHeadlineId) {
        this.iHeadlineId = iHeadlineId;
    }

    @JSONField(name = "sRefId")
    public void setsRefId(String sRefId) {
        this.sRefId = sRefId;
    }

    @JSONField(name = "cSource")
    public void setcSource(String cSource) {
        this.cSource = cSource;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(Integer iOrder) {
        this.iOrder = iOrder;
    }

    @JSONField(name = "sTitle")
    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    @JSONField(name = "sSubtitle")
    public void setsSubtitle(String sSubtitle) {
        this.sSubtitle = sSubtitle;
    }

    @JSONField(name = "sImageUrl")
    public void setsImageUrl(String sImageUrl) {
        this.sImageUrl = sImageUrl;
    }

    @JSONField(name = "sJumpUrl")
    public void setsJumpUrl(String sJumpUrl) {
        this.sJumpUrl = sJumpUrl;
    }

    @JSONField(name = "cJumpType")
    public void setcJumpType(String cJumpType) {
        this.cJumpType = cJumpType;
    }

    @JSONField(name = "sExtend")
    public void setsExtend(String sExtend) {
        this.sExtend = sExtend;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }
}
