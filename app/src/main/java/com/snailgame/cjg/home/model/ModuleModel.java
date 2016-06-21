package com.snailgame.cjg.home.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by sunxy on 2014/9/18.
 */
public class ModuleModel {

    private int iId; //主键
    private String dCreate;
    private int cType;//类型:1-广告位;2-快速入口;3-弹窗;4-内容栏
    private int iOrder;//顺序
    private String cDelFlag;
    private String cStatus;
    private String dUpdate;
    private int iPlatformId;
    private String cSource;//1-自定义 2-礼包 3-游戏推荐 4-合集 5-活动 6-攻略 7-资讯 8-论坛 9-游戏 10-积分墙 11-应用
    private String sTitle;//栏目标题
    private String sSubtitle;//副标题
    private String  sImageUrl;//图片地址
    private String  sExtend;//扩展字段 json格式
    private String cTemplateId;//模板ID
    private String cHeadlineShow;//是否显示栏目IOS专用
    private String sIconUrl;//icon地址
    private String  sPinUrl;//边角链接路径

    private ArrayList<ContentModel> childs;//

    public static final int TYPE_BANNER = 1;
   // 2-快速入口  5-快速入口代充值   8//加入测新游和 软件  9-快速入口带资讯  10 -快速入口 朋友
    public static final int TYPE_QUICK_ENTER = 10;
    public static final int TYPE_POPUP = 3;
    public static final int TYPE_CONTENT = 4;


    //对应模板3-10
    public static final String TEMPLATE_THREE = "3";
    public static final String TEMPLATE_FOUR = "4";
    public static final String TEMPLATE_FIVE = "5";
    public static final String TEMPLATE_SIX = "6";
    public static final String TEMPLATE_SEVEN = "7";
    public static final String TEMPLATE_EIGHT = "8";
    public static final String TEMPLATE_NINE = "9";
    public static final String TEMPLATE_TEN = "10";
    public static final String TEMPLATE_ELEVEN = "11";
    public static final String TEMPLATE_TWL = "12";
    public static final String TEMPLATE_THIRTEEN = "13";
    public static final String TEMPLATE_FOURTEEN = "14";
    public static final String TEMPLATE_FIFTEEN = "15";

    public static final String TEMPLATE_SEVENTEEN = "17";
    public static final String TEMPLATE_EIGHTEEM = "18";

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

    public int getiPlatformId() {
        return iPlatformId;
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

    public String getcHeadlineShow() {
        return cHeadlineShow;
    }

    public String getsIconUrl() {
        return sIconUrl;
    }

    public String getsPinUrl() {
        return sPinUrl;
    }

    public ArrayList<ContentModel> getChilds() {
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

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
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

    @JSONField(name = "cHeadlineShow")
    public void setcHeadlineShow(String cHeadlineShow) {
        this.cHeadlineShow = cHeadlineShow;
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
    public void setChilds(ArrayList<ContentModel> childs) {
        this.childs = childs;
    }
}
