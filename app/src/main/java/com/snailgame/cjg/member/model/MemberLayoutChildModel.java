package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by lic on 2015/12/22.
 * 会员中心页面
 */
public class MemberLayoutChildModel {
    public static final String TYPE_COOL_PLAY = "8";//酷玩专购
    public static final String TYPE_DISCOUNT = "9";//优惠折扣
    public static final String TYPE_PRIVILEGE = "10";//会员特权
    public static final String TYPE_TWO_AD = "11";//两栏推荐位

    public static final String JUMP_TYPE_COOL_PLAY = "6";//酷玩专购
    public static final String JUMP_TYPE_DISCOUNT = "7";//优惠折扣

    private Integer iId; // 主键
    private String cTemplateId; // 模板ID 数据字典
    private String cType; // 类型:1-广告位;2-快速入口;3-弹窗;4-内容栏;
    private Integer iOrder; // 顺序 1开始
    private String cSource; // 数据来源 数据字典
    private String sTitle; // 栏目标题
    private String sSubtitle; // 栏目副标题
    private String sIconUrl; // icon地址
    private String sImageUrl; // 图片地址
    private String sExtend; // 扩展字段 json格式
    private String sCreate; // 创建时间

    private String sPinUrl; // 边角链接网址
    private String sPinText; // 边角链接文本
    private String sPinIcon; // 边角链接图标
    private String cHeadlineShow; // 是否显示栏目条

    private ArrayList<MemberLayoutChildContentModel> childs;//

    public ArrayList<MemberLayoutChildContentModel> getChilds() {
        return childs;
    }

    public Integer getiId() {
        return iId;
    }

    public String getcTemplateId() {
        return cTemplateId;
    }

    public String getcType() {
        return cType;
    }

    public Integer getiOrder() {
        return iOrder;
    }

    public String getcSource() {
        return cSource;
    }

    public String getsTitle() {
        return sTitle;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public String getsIconUrl() {
        return sIconUrl;
    }

    public String getsImageUrl() {
        return sImageUrl;
    }

    public String getsExtend() {
        return sExtend;
    }

    public String getsCreate() {
        return sCreate;
    }

    public String getsPinUrl() {
        return sPinUrl;
    }

    public String getsPinText() {
        return sPinText;
    }

    public String getsPinIcon() {
        return sPinIcon;
    }

    public String getcHeadlineShow() {
        return cHeadlineShow;
    }

    @JSONField(name = "iId")
    public void setiId(Integer iId) {
        this.iId = iId;
    }

    @JSONField(name = "cTemplateId")
    public void setcTemplateId(String cTemplateId) {
        this.cTemplateId = cTemplateId;
    }

    @JSONField(name = "cType")
    public void setcType(String cType) {
        this.cType = cType;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(Integer iOrder) {
        this.iOrder = iOrder;
    }

    @JSONField(name = "cSource")
    public void setcSource(String cSource) {
        this.cSource = cSource;
    }

    @JSONField(name = "sTitle")
    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    @JSONField(name = "sSubtitle")
    public void setsSubtitle(String sSubtitle) {
        this.sSubtitle = sSubtitle;
    }

    @JSONField(name = "sIconUrl")
    public void setsIconUrl(String sIconUrl) {
        this.sIconUrl = sIconUrl;
    }

    @JSONField(name = "sImageUrl")
    public void setsImageUrl(String sImageUrl) {
        this.sImageUrl = sImageUrl;
    }

    @JSONField(name = "sExtend")
    public void setsExtend(String sExtend) {
        this.sExtend = sExtend;
    }

    @JSONField(name = "sCreate")
    public void setsCreate(String sCreate) {
        this.sCreate = sCreate;
    }

    @JSONField(name = "sPinUrl")
    public void setsPinUrl(String sPinUrl) {
        this.sPinUrl = sPinUrl;
    }

    @JSONField(name = "sPinText")
    public void setsPinText(String sPinText) {
        this.sPinText = sPinText;
    }

    @JSONField(name = "sPinIcon")
    public void setsPinIcon(String sPinIcon) {
        this.sPinIcon = sPinIcon;
    }

    @JSONField(name = "cHeadlineShow")
    public void setcHeadlineShow(String cHeadlineShow) {
        this.cHeadlineShow = cHeadlineShow;
    }

    @JSONField(name = "childs")
    public void setChilds(ArrayList<MemberLayoutChildContentModel> childs) {
        this.childs = childs;
    }
}