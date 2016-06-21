package com.snailgame.cjg.common.model;

import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.JsonUrl;

import java.io.Serializable;

/**
 * Created by TAJ_C on 2016/2/25.
 */
public class SystemConfig implements Serializable {

    //配置中的提醒应用更新间隔
    private long gameUpdateIntervel = 0L;
    //配置中的提醒应用更新状态(1.开启，0.关闭)
    private int gameUpdateTimeSwitch = 1;

    //免商店更新提醒更新的间隔
    private long appStoreUpdateTime = 0L;
    //配置中的提醒应用更新状态(1.开启，0.关闭)
    private int appStoreUpdateTimeSwitch = 1;

    //提醒用户使用免商店的时间间隔he
    private int appRemindTime = 48;
    //提醒用户使用免商店的状态(1.开启，0.关闭)
    private int appRemindTimeSwitch = 0;
    //分享免商店的时候的分享URL地址
    private String shareUrl;
    //分享免商店的时候的分享标题
    private String shareTitle;
    //欢迎页面广告的地址
    private String splashUrl;
    //欢迎页面是否开启广告
    private int splashSwitch = 0;

    //欢迎页面的广告对应的是哪种，如果是活动点击后就进入活动界面，如果是应用就点击进入应用详情界面
    private String splashImageAppId;

    //广告页面的展现时间
    private Long splashStayTime = 2000L;

    //弹窗配置信息
    //未登录首次下载声明
    private String downloadNotLogin;

    //暂停免卡功能
    private int stopFreeCardFunc;
    //暂停免卡功能原因
    private String stopFreeCardDes;
    //隐藏免卡功能的渠道号
    private String hideFreeCardChannelIds;
    //隐藏免卡功能的版本号
    private String scoreFreeCardStopVersion;

    //论坛墙模块功能是否停用
    private int stopForumFunc;
    //论坛墙模块功能停用原因
    private String stopForumDes;
    //论坛墙模块功能停用渠道号
    private String hideForumChannelIds;
    //论坛墙模块功能停用版本号
    private String scoreForumStopVersion;

    //退出按钮的描述
    private String existDialogDes;

    //首页弹窗次数
    private int popupTimes = 5;

    // 酷玩开关
    private boolean coolPlayLock = false;

    //首页中免商店点击返回是否显示退出dialog
    private int showExistDialog;

    private String bbsUrl;//论坛Url
    private String freeCardUrl;//免卡URL
    private String coolPlayUrl;//酷玩URL
    private String myOrderUrl;//我的订单url
    private String orderAddressUrl;//收货地址url
    private String shoppingCarUrl;//购物车url
    private String heyue_treaty_url;//合约机协议


    //代金券广告
    private int voucherAdStstus;
    private String voucherAdPageId;
    private String voucherAdPageTitle;
    private String voucherAdType;
    private String voucherAdUrl;
    private String voucherAdImgUrl;

    //个人中心等级说明
    private String userLevelInfoUrl;
    //app排行榜的搜索地区的国家列表
    private String countryLists;

    //当前使用的皮肤配置信息
    private String skinPackages;

    //充话费 地址
    private String rechargeCurrencyUrl;
    //充游戏 地址
    private String rechargeGameUrl;
    //充流量 地址
    private String rechargeFlowUrl;

    private String flowPrivilegeUrl;
    //蜗牛代金卷 显示开关
    private boolean voucherWoniuSwitch = false;
    //蜗牛代金卷 标题
    private String voucherWoniuTitle;
    //大家都在搜
    private String hotSearch;
    //畅享宝配置
    private boolean cxbStatus;
    private String cxbIconUrl;
    private String cxbTitle;
    private String cxbPackageName;
    private int cxbAppId;
    //会员介绍
    private String memberIntroduce;

    public long getGameUpdateIntervel() {
        if (gameUpdateIntervel == 0L)
            return AppConstants.UPDATE_INTERVAL_NOTIFICATION;
        return gameUpdateIntervel;
    }

    public void setGameUpdateIntervel(long gameUpdateIntervel) {
        this.gameUpdateIntervel = gameUpdateIntervel;
    }

    public int getGameUpdateTimeSwitch() {
        return gameUpdateTimeSwitch;
    }

    public void setGameUpdateTimeSwitch(int gameUpdateTimeSwitch) {
        this.gameUpdateTimeSwitch = gameUpdateTimeSwitch;
    }

    public long getAppStoreUpdateTime() {
        if (appStoreUpdateTime == 0L)
            return AppConstants.UPDATE_INTERVAL_MYSHELF;
        return appStoreUpdateTime;
    }

    public void setAppStoreUpdateTime(long appStoreUpdateTime) {
        this.appStoreUpdateTime = appStoreUpdateTime;
    }

    public int getAppStoreUpdateTimeSwitch() {
        return appStoreUpdateTimeSwitch;
    }

    public void setAppStoreUpdateTimeSwitch(int appStoreUpdateTimeSwitch) {
        this.appStoreUpdateTimeSwitch = appStoreUpdateTimeSwitch;
    }

    public int getAppRemindTime() {
        return appRemindTime;
    }

    public void setAppRemindTime(int appRemindTime) {
        this.appRemindTime = appRemindTime;
    }

    public int getAppRemindTimeSwitch() {
        return appRemindTimeSwitch;
    }

    public void setAppRemindTimeSwitch(int appRemindTimeSwitch) {
        this.appRemindTimeSwitch = appRemindTimeSwitch;
    }

    public String getShareUrl() {
        if (shareUrl == null || TextUtils.isEmpty(shareUrl))
            shareUrl = FreeStoreApp.getContext().getString(R.string.invite_share_url);
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareTitle() {
        if (shareTitle == null || TextUtils.isEmpty(shareTitle))
            shareTitle = FreeStoreApp.getContext().getString(R.string.invite_friend_title);
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getSplashUrl() {
        if (splashUrl == null)
            splashUrl = "";
        return splashUrl;
    }

    public void setSplashUrl(String splashUrl) {
        this.splashUrl = splashUrl;
    }

    public int getSplashSwitch() {
        return splashSwitch;
    }

    public void setSplashSwitch(int splashSwitch) {
        this.splashSwitch = splashSwitch;
    }

    public String getSplashImageAppId() {
        if (splashImageAppId == null)
            splashImageAppId = "";
        return splashImageAppId;
    }

    public void setSplashImageAppId(String splashImageAppId) {
        this.splashImageAppId = splashImageAppId;
    }

    public Long getSplashStayTime() {
        return splashStayTime;
    }

    public void setSplashStayTime(Long splashStayTime) {
        this.splashStayTime = splashStayTime;
    }

    public String getDownloadNotLogin() {
        return downloadNotLogin;
    }

    public void setDownloadNotLogin(String downloadNotLogin) {
        this.downloadNotLogin = downloadNotLogin;
    }

    public int getStopFreeCardFunc() {
        return stopFreeCardFunc;
    }

    public void setStopFreeCardFunc(int stopFreeCardFunc) {
        this.stopFreeCardFunc = stopFreeCardFunc;
    }

    public String getStopFreeCardDes() {
        return stopFreeCardDes;
    }

    public void setStopFreeCardDes(String stopFreeCardDes) {
        this.stopFreeCardDes = stopFreeCardDes;
    }

    public String getHideFreeCardChannelIds() {
        if (hideFreeCardChannelIds == null)
            hideFreeCardChannelIds = "";
        return hideFreeCardChannelIds;
    }

    public void setHideFreeCardChannelIds(String hideFreeCardChannelIds) {
        this.hideFreeCardChannelIds = hideFreeCardChannelIds;
    }

    public String getScoreFreeCardStopVersion() {
        if (scoreFreeCardStopVersion == null)
            scoreFreeCardStopVersion = "";
        return scoreFreeCardStopVersion;
    }

    public void setScoreFreeCardStopVersion(String scoreFreeCardStopVersion) {
        this.scoreFreeCardStopVersion = scoreFreeCardStopVersion;
    }

    public int getStopForumFunc() {
        return stopForumFunc;
    }

    public void setStopForumFunc(int stopForumFunc) {
        this.stopForumFunc = stopForumFunc;
    }

    public String getStopForumDes() {
        if (stopForumDes == null)
            stopForumDes = "";
        return stopForumDes;
    }

    public void setStopForumDes(String stopForumDes) {
        this.stopForumDes = stopForumDes;
    }

    public String getHideForumChannelIds() {
        if (hideForumChannelIds == null)
            hideForumChannelIds = "";
        return hideForumChannelIds;
    }

    public void setHideForumChannelIds(String hideForumChannelIds) {
        this.hideForumChannelIds = hideForumChannelIds;
    }

    public String getScoreForumStopVersion() {
        return scoreForumStopVersion;
    }

    public void setScoreForumStopVersion(String scoreForumStopVersion) {
        this.scoreForumStopVersion = scoreForumStopVersion;
    }

    public String getExistDialogDes() {
        if (existDialogDes == null || TextUtils.isEmpty(existDialogDes))
            existDialogDes = FreeStoreApp.getContext().getString(R.string.double_click_exit);
        return existDialogDes;
    }

    public void setExistDialogDes(String existDialogDes) {
        this.existDialogDes = existDialogDes;
    }

    public int getPopupTimes() {
        return popupTimes;
    }

    public void setPopupTimes(int popupTimes) {
        this.popupTimes = popupTimes;
    }


    public boolean isCoolPlayLock() {
        return coolPlayLock;
    }

    public void setCoolPlayLock(boolean coolPlayLock) {
        this.coolPlayLock = coolPlayLock;
    }

    public int getShowExistDialog() {
        return showExistDialog;
    }

    public void setShowExistDialog(int showExistDialog) {
        this.showExistDialog = showExistDialog;
    }

    public String getBbsUrl() {
        if (bbsUrl == null || TextUtils.isEmpty(bbsUrl))
            return JsonUrl.getJsonUrl().JSON_URL_BBS;
        return bbsUrl;
    }

    public void setBbsUrl(String bbsUrl) {
        this.bbsUrl = bbsUrl;
    }

    public String getFreeCardUrl() {
        if (freeCardUrl == null)
            return "";
        return freeCardUrl;
    }

    public void setFreeCardUrl(String freeCardUrl) {
        this.freeCardUrl = freeCardUrl;
    }

    public String getCoolPlayUrl() {
        if (coolPlayUrl == null)
            return "";
        return coolPlayUrl;
    }

    public void setCoolPlayUrl(String coolPlayUrl) {
        this.coolPlayUrl = coolPlayUrl;
    }

    public String getMyOrderUrl() {
        if (myOrderUrl == null)
            return "";
        return myOrderUrl;
    }

    public void setMyOrderUrl(String myOrderUrl) {
        this.myOrderUrl = myOrderUrl;
    }

    public String getOrderAddressUrl() {
        if (orderAddressUrl == null)
            return "";
        return orderAddressUrl;
    }

    public void setOrderAddressUrl(String orderAddressUrl) {
        this.orderAddressUrl = orderAddressUrl;
    }

    public String getShoppingCarUrl() {
        if (shoppingCarUrl == null)
            return "";
        return shoppingCarUrl;
    }

    public void setShoppingCarUrl(String shoppingCarUrl) {
        this.shoppingCarUrl = shoppingCarUrl;
    }

    public int getVoucherAdStstus() {
        return voucherAdStstus;
    }

    public void setVoucherAdStstus(int voucherAdStstus) {
        this.voucherAdStstus = voucherAdStstus;
    }

    public String getVoucherAdPageId() {

        return voucherAdPageId == null ? "" : voucherAdPageId;
    }

    public void setVoucherAdPageId(String voucherAdPageId) {
        this.voucherAdPageId = voucherAdPageId;
    }

    public String getVoucherAdPageTitle() {
        return voucherAdPageTitle == null ? "" : voucherAdPageTitle;
    }

    public void setVoucherAdPageTitle(String voucherAdPageTitle) {
        this.voucherAdPageTitle = voucherAdPageTitle;
    }

    public String getVoucherAdType() {
        return voucherAdType;
    }

    public void setVoucherAdType(String voucherAdType) {
        this.voucherAdType = voucherAdType;
    }

    public String getVoucherAdUrl() {
        return voucherAdUrl;
    }

    public void setVoucherAdUrl(String voucherAdUrl) {
        this.voucherAdUrl = voucherAdUrl;
    }

    public String getVoucherAdImgUrl() {
        return voucherAdImgUrl;
    }

    public void setVoucherAdImgUrl(String voucherAdImgUrl) {
        this.voucherAdImgUrl = voucherAdImgUrl;
    }

    public String getUserLevelInfoUrl() {
        if (TextUtils.isEmpty(userLevelInfoUrl))
            return JsonUrl.getJsonUrl().JSON_URL_USER_LEVER_V2;
        return userLevelInfoUrl;
    }

    public void setUserLevelInfoUrl(String userLevelInfoUrl) {
        this.userLevelInfoUrl = userLevelInfoUrl;
    }

    public String getCountryLists() {
        return countryLists;
    }

    public void setCountryLists(String countryLists) {
        this.countryLists = countryLists;
    }

    public String getSkinPackages() {
        return skinPackages;
    }

    public void setSkinPackages(String skinPackages) {
        this.skinPackages = skinPackages;
    }

    public String getRechargeCurrencyUrl() {
        if (TextUtils.isEmpty(rechargeCurrencyUrl)) {
            return JsonUrl.getJsonUrl().JSON_URL_RECHARGE_CURRENCY;
        }
        return rechargeCurrencyUrl;
    }

    public void setRechargeCurrencyUrl(String rechargeCurrencyUrl) {
        this.rechargeCurrencyUrl = rechargeCurrencyUrl;
    }

    public String getRechargeGameUrl() {
        if (TextUtils.isEmpty(rechargeGameUrl)) {
            return JsonUrl.getJsonUrl().JSON_URL_RECHARGE_GAME;
        }
        return rechargeGameUrl;
    }

    public void setRechargeGameUrl(String rechargeGameUrl) {
        this.rechargeGameUrl = rechargeGameUrl;
    }

    public String getRechargeFlowUrl() {
        if (TextUtils.isEmpty(rechargeFlowUrl)) {
            return JsonUrl.getJsonUrl().JSON_URL_RECHARGE_FLOW;
        }
        return rechargeFlowUrl;
    }

    public void setRechargeFlowUrl(String rechargeFlowUrl) {
        this.rechargeFlowUrl = rechargeFlowUrl;
    }

    public String getFlowPrivilegeUrl() {
        if (TextUtils.isEmpty(flowPrivilegeUrl)) {
            return JsonUrl.getJsonUrl().JSON_URL_FLOW_FREE;
        }
        return flowPrivilegeUrl;
    }

    public void setFlowPrivilegeUrl(String flowPrivilegeUrl) {
        this.flowPrivilegeUrl = flowPrivilegeUrl;
    }

    public boolean isVoucherWoniuSwitch() {
        return voucherWoniuSwitch;
    }

    public void setVoucherWoniuSwitch(boolean voucherWoniuSwitch) {
        this.voucherWoniuSwitch = voucherWoniuSwitch;
    }

    public String getVoucherWoniuTitle() {
        return voucherWoniuTitle;
    }

    public void setVoucherWoniuTitle(String voucherWoniuTitle) {
        this.voucherWoniuTitle = voucherWoniuTitle;
    }

    public String getHotSearch() {
        return hotSearch;
    }

    public void setHotSearch(String hotSearch) {
        this.hotSearch = hotSearch;
    }


    public boolean isCxbStatus() {
        return cxbStatus;
    }

    public void setCxbStatus(boolean cxbStatus) {
        this.cxbStatus = cxbStatus;
    }

    public String getCxbIconUrl() {
        return cxbIconUrl;
    }

    public void setCxbIconUrl(String cxbIconUrl) {
        this.cxbIconUrl = cxbIconUrl;
    }

    public String getCxbTitle() {
        return cxbTitle;
    }

    public void setCxbTitle(String cxbTitle) {
        this.cxbTitle = cxbTitle;
    }

    public String getCxbPackageName() {
        return cxbPackageName;
    }

    public void setCxbPackageName(String cxbPackageName) {
        this.cxbPackageName = cxbPackageName;
    }

    public int getCxbAppId() {
        return cxbAppId;
    }

    public void setCxbAppId(int cxbAppId) {
        this.cxbAppId = cxbAppId;
    }

    public String getMemberIntroduce() {
        if (TextUtils.isEmpty(memberIntroduce))
            return JsonUrl.getJsonUrl().JSON_URL_MEMBER_INTRODUCE;
        return memberIntroduce;
    }

    public void setMemberIntroduce(String memberIntroduce) {
        this.memberIntroduce = memberIntroduce;
    }

    public String getHeyue_treaty_url() {
        if (TextUtils.isEmpty(heyue_treaty_url))
            return JsonUrl.getJsonUrl().JSON_URL_HEYUE_TREATY;
        return heyue_treaty_url;
    }

    public void setHeyue_treaty_url(String heyue_treaty_url) {
        this.heyue_treaty_url = heyue_treaty_url;
    }
}
