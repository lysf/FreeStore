package com.snailgame.cjg.communication.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by lic on 2015/1/22.
 * 通讯里模块的对象
 */
public class CommunicationChildModel {

    public static final String TYPE_BANNER = "1";//广告位
    public static final String TYPE_QUICK_ENTER = "2";//快速入口
    public static final String TYPE_THREE_VERTICAL_AD = "3";//三竖栏广告位
    public static final String TYPE_HOT_CARD_= "4";//热门免卡
    public static final String TYPE_HOT_TARIFF_PACKAGE= "5";//热门资费包
    private int iId; //主键
    private String dCreate;
    private int cType;//类型:1-广告位;2-快速入口;3-弹窗;4-内容栏
    private int iOrder;//顺序
    private String cSource;//
    private String dUpdate;
    private String cDelFlag;
    private String sImageUrl;//图片地址
    private String sTitle;//栏目标题
    private String cStatus;
    private String sExtend;//扩展字段 json格式
    private String sSubtitle;//副标题
    private String cTemplateId;//模板ID
    private String sIconUrl;//icon地址
    private String sPinUrl;//边角链接路径
    private String sPinText;//边角链接文本
    private String cHeadlineShow;//是否显示标题栏 0-不显示 1-显示
    private String sPinIcon;//边角链接图标
    private ArrayList<CommunicationChildContentModel> childs;//

    public int getiId() {
        return iId;
    }

    public String getdCreate() {
        return dCreate;
    }

    public int getcType() {
        return cType;
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

    public String getsTitle() {
        return sTitle;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public String getsImageUrl() {
        return sImageUrl;
    }

    public String getsExtend() {
        return sExtend;
    }

    public String getcTemplateId() {
        return cTemplateId;
    }


    public String getsIconUrl() {
        return sIconUrl;
    }

    public String getsPinUrl() {
        return sPinUrl;
    }

    public String getcHeadlineShow() {
        return cHeadlineShow;
    }

    public String getsPinIcon() {
        return sPinIcon;
    }

    public ArrayList<CommunicationChildContentModel> getChilds() {
        return childs;
    }

    @JSONField(name = "iId")
    public void setiId(int iId) {
        this.iId = iId;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "cType")
    public void setcType(int cType) {
        this.cType = cType;
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

    @JSONField(name = "sPinText")
    public void setsPinText(String sPinText) {
        this.sPinText = sPinText;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
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

    @JSONField(name = "sImageUrl")
    public void setsImageUrl(String sImageUrl) {
        this.sImageUrl = sImageUrl;
    }

    @JSONField(name = "sExtend")
    public void setsExtend(String sExtend) {
        this.sExtend = sExtend;
    }

    @JSONField(name = "cTemplateId")
    public void setcTemplateId(String cTemplateId) {
        this.cTemplateId = cTemplateId;
    }

    @JSONField(name = "sIconUrl")
    public void setsIconUrl(String sIconUrl) {
        this.sIconUrl = sIconUrl;
    }

    @JSONField(name = "sPinUrl")
    public void setsPinUrl(String sPinUrl) {
        this.sPinUrl = sPinUrl;
    }

    @JSONField(name = "childs")
    public void setChilds(ArrayList<CommunicationChildContentModel> childs) {
        this.childs = childs;
    }

    @JSONField(name = "sPinText")
    public String getsPinText() {
        return sPinText;
    }

    @JSONField(name = "cHeadlineShow")
    public void setcHeadlineShow(String cHeadlineShow) {
        this.cHeadlineShow = cHeadlineShow;
    }

    @JSONField(name = "sPinIcon")
    public void setsPinIcon(String sPinIcon) {
        this.sPinIcon = sPinIcon;
    }
}
