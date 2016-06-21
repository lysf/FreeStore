package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by sunxy on 2015/10/15.
 */
public class InsteadCharge {
    private int nAppId; // 应用ID
    private String cTag; // 角标签
    private int iOrder; // 排序
    private String sDesc; // 备注
    private String sGuideTitle; // 代充引导标题
    private String sGuideSubtitle; // 代充引导副标题
    private String cGuideIcon; // 代充引导图标
    private String sGuideButton; // 代充引导按钮
    private String cStatus; // 0 失效 1 有效
//    private Timestamp DUpdate; // 更新时间
//    private Timestamp DCreate; // 创建时间
    private int iPlatformId; // 平台id

    public int getnAppId() {
        return nAppId;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    public String getcTag() {
        return cTag;
    }

    @JSONField(name = "cTag")
    public void setcTag(String cTag) {
        this.cTag = cTag;
    }

    public int getiOrder() {
        return iOrder;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(int iOrder) {
        this.iOrder = iOrder;
    }

    public String getsDesc() {
        return sDesc;
    }

    @JSONField(name = "sDesc")
    public void setsDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public String getsGuideTitle() {
        return sGuideTitle;
    }

    @JSONField(name = "sGuideTitle")
    public void setsGuideTitle(String sGuideTitle) {
        this.sGuideTitle = sGuideTitle;
    }

    public String getsGuideSubtitle() {
        return sGuideSubtitle;
    }

    @JSONField(name = "sGuideSubtitle")
    public void setsGuideSubtitle(String sGuideSubtitle) {
        this.sGuideSubtitle = sGuideSubtitle;
    }

    public String getcGuideIcon() {
        return cGuideIcon;
    }

    @JSONField(name = "cGuideIcon")
    public void setcGuideIcon(String cGuideIcon) {
        this.cGuideIcon = cGuideIcon;
    }

    public String getsGuideButton() {
        return sGuideButton;
    }

    @JSONField(name = "sGuideButton")
    public void setsGuideButton(String sGuideButton) {
        this.sGuideButton = sGuideButton;
    }

    public String getcStatus() {
        return cStatus;
    }

    @JSONField(name = "cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    public int getiPlatformId() {
        return iPlatformId;
    }

    @JSONField(name = "iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
    }

}
