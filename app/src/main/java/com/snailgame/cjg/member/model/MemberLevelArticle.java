package com.snailgame.cjg.member.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by TAJ_C on 2015/12/21.
 */
public class MemberLevelArticle {
    @JSONField(name = "iOrder")
    int order; // 分类排序字段
    @JSONField(name = "cClassify")
    String classify; // 物品处理类别，例：cdkey|package|freeflow|novice|prize

    @JSONField(name = "iArticleId")
    int articeId; // 物品ID

    @JSONField(name = "cStatus")
    String status; // 状态: 0, 下架; 1, 上架

    @JSONField(name = "cConfig")
    String config; // 物品配置JSON，若类别为package则为标准奖励格式

    @JSONField(name = "cLogo")
    String logo; // 物品图片

    @JSONField(name = "sArticleName")
    String articleName; // 物品名称

    @JSONField(name = "nAppId")
    int appId; // 应用ID

    @JSONField(name = "cType")
    String type; // 物品类型 1：礼包、2：充值卡、3：物品、4：道具
    @JSONField(name = "cDelFlag")
    String delFlag; // 删除标识: 0,已删除;1,未删除

    @JSONField(name = "sAppName")
    String appName; // 应用名称


    @JSONField(name = "cHot")
    String isHot; // 是否热门:0,否;1,是


    @JSONField(name = "dCreate")
    String createTime; // 生成时间


    @JSONField(name = "dUpdate")
    String updateTime; // 更新时间


    @JSONField(name = "iPlatformId")
    int platformId; // 平台ID
    @JSONField(name = "sIntro")
    String intro; // 物品介绍

    @JSONField(name = "iAppOrder")
    int appOrder; // 按游戏排序


    @JSONField(name = "iTotalNum")
    int totalNum; // 物品总数， -1为不限

    @JSONField(name = "iGetNum")
    int getNum; // 每个帐号可获得数， -1为不限

    public int getArticeId() {
        return articeId;
    }

    public void setArticeId(int articeId) {
        this.articeId = articeId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getGetNum() {
        return getNum;
    }

    public void setGetNum(int getNum) {
        this.getNum = getNum;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public int getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(int appOrder) {
        this.appOrder = appOrder;
    }

}
