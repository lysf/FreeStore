package com.snailgame.cjg.manage.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 百宝箱
 * Created by xixh on 14-7-9.
 */
public class TreasureBoxInfo {

    public final static int TRAFFIC_STATISTICS_ID = 1;//流量统计
    public final static int SCAN_ID = 2;//扫一扫
    public final static int WEB_ID = 3;//网页
    public final static int NOVICE_CARD_ID = 4;//新手卡兑换
    public final static int TRAFFIC_CONTROL_ID = 5;//联网控制
    public final static int SCORE_ID = 6;//赚话费
    public final static int APP_MANAGE_ID = 7;//应用管理
    public final static int RECOMMEND_ID = 8;//推荐好友
    public final static int SETTING_ID = 9;//商店设置
    private int iId; // 主键ID
    private String dCreate;
    private String sName; // 名称
    private String cType; // 类型:1,流量统计;2,二维码扫描;3待定
    private int iOrder; // 排序
    private String cDelFlag; // 删除标识: 0,已删除;1,未删除
    private String cIcon; // LOGO地址
    private String cStatus; // 状态: 0, 失效; 1, 有效
    private String dUpdate; // 更新时间
    private String cUrl; // 链接地址
    private String sExtension; // 扩展字段
    private int iPlatformId;
    
    @JSONField(name = "iId")
    public int getiId() {
        return iId;
    }

    @JSONField(name = "iId")
    public void setiId(int iId) {
        this.iId = iId;
    }

    @JSONField(name="sName")
    public String getsName() {
        return sName;
    }
    
    @JSONField(name="sName")
    public void setsName(String sName) {
        this.sName = sName;
    }

    @JSONField(name="cType")
    public String getcType() {
        return cType;
    }
    
    @JSONField(name="cType")
    public void setcType(String cType) {
        this.cType = cType;
    }
    
    @JSONField(name="iOrder")
    public int getiOrder() {
        return iOrder;
    }
    
    @JSONField(name="iOrder")
    public void setiOrder(int iOrder) {
        this.iOrder = iOrder;
    }
    
    @JSONField(name="cIcon")
    public String getcIcon() {
       return cIcon;
    }
    
    @JSONField(name="cIcon")
    public void setcIcon(String cIcon) {
        this.cIcon = cIcon;
    }
    
    @JSONField(name="dUpdate")
    public String getdUpdate() {
       return dUpdate;
    }
    
    @JSONField(name="dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }
    
    @JSONField(name="cStatus")
    public String getcStatus() {
        return cStatus;
    }
    
    @JSONField(name="cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }
    
    @JSONField(name="cDelFlag")
    public String getcDelFlag() {
        return cDelFlag;
    }
    
    @JSONField(name="cDelFlag")
    public void setcDelFlag(String cDelFlag) {
        this.cDelFlag = cDelFlag;
    }
    
    @JSONField(name="cUrl")
    public String getcUrl() {
        return cUrl;
    }
    
    @JSONField(name="cUrl")
    public void setcUrl(String cUrl) {
        this.cUrl = cUrl;
    }
    
    @JSONField(name="sExtension")
    public String getsExtension() {
        return sExtension;
    }
    
    @JSONField(name="sExtension")
    public void setsExtension(String sExtension) {
        this.sExtension = sExtension;
    }

    public String getdCreate() {
        return dCreate;
    }

    public int getiPlatformId() {
        return iPlatformId;
    }

    @JSONField(name="dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name="iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
    }
}
