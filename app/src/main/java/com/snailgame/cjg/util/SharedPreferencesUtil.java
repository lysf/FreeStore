package com.snailgame.cjg.util;

import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * 将通用SharedPreferences 存取都由此类处理
 * Created by TAJ_C on 2014/10/16.
 */
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil sharedPreferencesUtil;
    private SharedPreferencesHelper helper;

    //用户相关
    private boolean isFlowFree;
    private boolean isSnailFlowFree;
    private String nickName;
    private String phoneNumber;
    private String point;
    private String replaceDomain;
    private boolean isSnailPhoneNumber;
    private String flowFreeStart;
    private String flowEnd;
    private String flowSize;
    private boolean isCxbPhoneNumber;
    private boolean isAlertGrantRoot;
    private boolean isAutoInstall;
    private boolean isSuperuser;
    private boolean isUseFlowDownLoad;
    private boolean isWifiOnly;
    private boolean isContractMachine;//合约机

    private boolean isAutoDelApp;
    private boolean isOpenPush;
    private boolean isUpdate;

    private String lastLoginID;
    private int popupTimes;
    private String popupRecommentID;
    private String area;
    private String notificationID;
    private String lastPackage;

    private boolean isDoNotAlertAnyMore1;
    private boolean isSilenceUpdate;  //静默更新

    //机器相关
    private boolean isFirstReboot;
    private boolean isFirstIn;
    private boolean isNeedCreateDeskGameShortcut;
    private boolean bindCid;

    //搜索历史 JSON格式
    private String searchHistoryList;
    // 渠道号
    private String channelID;
    //每日抽奖，是否免费抽奖的状态
    private int scratchStatus;

    //GameSDK 游戏id列表
    private String sdkGameId;

    //最后一次清理内存得到的分数
    private int lastScore;
    //最后一次清理内存得到的内存
    private long lastMemory;
    //热门搜索
    private String hotSearch;
    //首页的最后一次的刷新时间
    private long lastRefreshTime;

    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil();
        }
        return sharedPreferencesUtil;
    }

    public SharedPreferencesUtil() {
        helper = SharedPreferencesHelper.getInstance();
        isFirstReboot = helper.getValue(AppConstants.KEY_FIRST_REBOOT, true);
        isNeedCreateDeskGameShortcut = helper.getValue(AppConstants.KEY_NEED_CREATE_DESK_GAME_SHORTCUT, true);
        isFirstIn = helper.getValue(AppConstants.KEY_FIRST_IN, true);
        isFlowFree = helper.getValue(AppConstants.FLOW_IS_FREE, false);
        isSnailFlowFree = helper.getValue(AppConstants.FLOW_IS_FREE_SNAIL, false);
        flowFreeStart = helper.getValue(AppConstants.FLOW_FREE_START, "");
        flowEnd = helper.getValue(AppConstants.FLOW_FREE_END, "");
        flowSize = helper.getValue(AppConstants.FLOW_FREE_SIZE, "");
        isContractMachine = helper.getValue(AppConstants.IS_CONTRACT_MACHINE, false);
        isSnailPhoneNumber = helper.getValue(AppConstants.IS_SNAIL_PHONE_NUMBER, false);
        isCxbPhoneNumber = helper.getValue(AppConstants.IS_CXB_PHONE_NUMBER, false);
        nickName = helper.getValue(AppConstants.NICK_NAME, "");
        phoneNumber = helper.getValue(AppConstants.PHONE_NUMBER, "");
        bindCid = helper.getValue(AppConstants.BIND_CID, false);

        point = helper.getValue(AppConstants.REWARD_SCORE, "0");
        isAlertGrantRoot = helper.getValue(AppConstants.ALERT_GRANT_ROOT, true);

        isAutoInstall = helper.getValue(AppConstants.SETTING_AUTO_INSTALL, false);
        isSuperuser = helper.getValue(AppConstants.KEY_IS_SUPERUSER, false);
        isUseFlowDownLoad = helper.getValue(AppConstants.USE_FLOW_DOWNLOAD, false);
        isWifiOnly = helper.getValue(AppConstants.SETTING_WIFI_ONLY, false);
        isAutoDelApp = helper.getValue(AppConstants.SETTING_AUTO_DELETE_APP, true);
        replaceDomain = helper.getValue(AppConstants.KEY_REPLACE_DOMAIN, "");

        lastLoginID = helper.getValue(AppConstants.LAST_LOGIN_ID, "");
        popupTimes = helper.getValue(AppConstants.POPUP_TIMES, 0);

        area = helper.getValue(AppConstants.AREA, "x");
        popupRecommentID = helper.getValue(AppConstants.POPUP_RECOMMENT_ID, "-1");
        isOpenPush = helper.getValue(AppConstants.SETTING_OPEN_PUSH, true);

        notificationID = helper.getValue(AppConstants.NOTIFICATION_IDS, "");
        isUpdate = helper.getValue(AppConstants.SETTING_UPDATE, true);
        lastPackage = helper.getValue(AppConstants.KEY_LAST_PACKAGE, "");

        searchHistoryList = helper.getValue(AppConstants.SEARCH_HISTORY_LIST, "[]");

        isDoNotAlertAnyMore1 = helper.getValue(AppConstants.DO_NOT_ALERT_ANY_MORE_1, false);

        channelID = helper.getValue(AppConstants.CHANNEL_ID, "");
        isSilenceUpdate = helper.getValue(AppConstants.KEY_SILENCE_UPDATE, false);
        scratchStatus = helper.getValue(AppConstants.SCRATCH_STATUS, 0);

        sdkGameId = helper.getValue(AppConstants.SDK_GAME_ID, "");
        lastScore = helper.getValue(AppConstants.LAST_SCORE, 0);
        lastMemory = helper.getValue(AppConstants.LAST_MEMORY, (long) 0);
        hotSearch = helper.getValue(AppConstants.KEY_RANDOM_HOT_SEARCH, "");
        lastRefreshTime = helper.getValue(AppConstants.LAST_REFRESH_TIME, (long) 0);
    }


    public String getSdkGameId() {
        return sdkGameId;
    }

    public void setSdkGameId(String sdkGameId) {
        this.sdkGameId = sdkGameId;
        setConfig(AppConstants.SDK_GAME_ID, sdkGameId);
    }

    public boolean isFlowFree() {
        return isFlowFree;
    }

    public void setFlowFree(boolean isFree) {
        this.isFlowFree = isFree;
        setConfig(AppConstants.FLOW_IS_FREE, isFree);
    }

    public boolean isSnailFlowFree() {
        return isSnailFlowFree;
    }

    public void setSnailFlowFree(boolean isFree) {
        this.isSnailFlowFree = isFree;
        setConfig(AppConstants.FLOW_IS_FREE_SNAIL, isFree);
    }


    public String getFlowFreeStart() {
        return flowFreeStart;
    }

    public void setFlowFreeStart(String flowFreeStart) {
        this.flowFreeStart = flowFreeStart;
        setConfig(AppConstants.FLOW_FREE_START, flowFreeStart);
    }

    public String getFlowFreeEnd() {
        return flowEnd;
    }

    public void setFlowFreeEnd(String end) {
        this.flowEnd = end;
        setConfig(AppConstants.FLOW_FREE_END, end);
    }

    public String getFlowFreeSize() {
        return flowSize;
    }

    public void setFlowFreeSize(String size) {
        this.flowSize = size;
        setConfig(AppConstants.FLOW_FREE_SIZE, size);
    }

    public int getVoucherNum() {
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext()))
            return helper.getValue(AppConstants.USER_VOUCHER_NUM + LoginSDKUtil.getLoginUin(FreeStoreApp.getContext()), 0);
        else
            return 0;

    }

    public void setVoucherNum(int voucherNum) {
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext()))
            setConfig(AppConstants.USER_VOUCHER_NUM + LoginSDKUtil.getLoginUin(FreeStoreApp.getContext()), voucherNum);
    }

    public boolean isFirstReboot() {
        return isFirstReboot;
    }

    public void setFirstReboot(boolean isFirstReboot) {
        this.isFirstReboot = isFirstReboot;
        setConfig(AppConstants.KEY_FIRST_REBOOT, isFirstReboot);
    }

    public boolean isFirstIn() {
        return isFirstIn;
    }

    public void setFirstIn(boolean isFirstIn) {
        this.isFirstIn = isFirstIn;
        setConfig(AppConstants.KEY_FIRST_IN, isFirstIn);
    }


    public boolean isNeedCreateDeskGameShortcut() {
        return isNeedCreateDeskGameShortcut;
    }

    public void setisNeedCreateDeskGameShortcut(boolean isNeedCreateDeskGameShortcut) {
        this.isNeedCreateDeskGameShortcut = isNeedCreateDeskGameShortcut;
        setConfig(AppConstants.KEY_NEED_CREATE_DESK_GAME_SHORTCUT, isNeedCreateDeskGameShortcut);
    }

    public boolean isSnailPhoneNumber() {
        return isSnailPhoneNumber;
    }

    public void setSnailPhoneNumber(boolean isSnailPhoneNumber) {
        this.isSnailPhoneNumber = isSnailPhoneNumber;
        setConfig(AppConstants.IS_SNAIL_PHONE_NUMBER, isSnailPhoneNumber);
    }

    public boolean isContractMachine() {
        return isContractMachine;
    }

    public void setContractMachine(boolean contractMachine) {
        isContractMachine = contractMachine;
        setConfig(AppConstants.IS_CONTRACT_MACHINE, contractMachine);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        setConfig(AppConstants.NICK_NAME, nickName);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setConfig(AppConstants.PHONE_NUMBER, phoneNumber);
    }

    public boolean isCxbPhoneNumber() {
        return isCxbPhoneNumber;
    }

    public void setCxbPhoneNumber(boolean isCxbPhoneNumber) {
        this.isCxbPhoneNumber = isCxbPhoneNumber;
        setConfig(AppConstants.IS_CXB_PHONE_NUMBER, isCxbPhoneNumber);
    }

    public boolean isBindCid() {
        return bindCid;
    }

    public void setBindCid(boolean bindCid) {
        this.bindCid = bindCid;
        setConfig(AppConstants.BIND_CID, bindCid);
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
        setConfig(AppConstants.REWARD_SCORE, point);
    }


    public boolean isAlertGrantRoot() {
        return isAlertGrantRoot;
    }

    public void setAlertGrantRoot(boolean isAlertGrantRoot) {
        this.isAlertGrantRoot = isAlertGrantRoot;
        setConfig(AppConstants.ALERT_GRANT_ROOT, isAlertGrantRoot);
    }

    public boolean isAutoInstall() {
        return isAutoInstall;
    }

    public void setAutoInstall(boolean isAutoInstall) {
        this.isAutoInstall = isAutoInstall;
        setConfig(AppConstants.SETTING_AUTO_INSTALL, isAutoInstall);
    }


    public boolean isUseFlowDownLoad() {
        return isUseFlowDownLoad;
    }

    public void setUseFlowDownLoad(boolean isUseFlowDownLoad) {
        this.isUseFlowDownLoad = isUseFlowDownLoad;
        setConfig(AppConstants.USE_FLOW_DOWNLOAD, isUseFlowDownLoad);
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(boolean isSuperuser) {
        this.isSuperuser = isSuperuser;
        setConfig(AppConstants.KEY_IS_SUPERUSER, isSuperuser);
    }


    public boolean isWifiOnly() {
        return isWifiOnly;
    }

    public void setWifiOnly(boolean isWifiOnly) {
        this.isWifiOnly = isWifiOnly;
        setConfig(AppConstants.SETTING_WIFI_ONLY, isWifiOnly);
    }

    public boolean isAutoDelApp() {
        return isAutoDelApp;
    }

    public void setAutoDelApp(boolean isAutoDelApp) {
        this.isAutoDelApp = isAutoDelApp;
        setConfig(AppConstants.SETTING_AUTO_DELETE_APP, isAutoDelApp);
    }

    public String getReplaceDomain() {
        return replaceDomain;
    }

    public void setReplaceDomain(String replaceDonain) {
        this.replaceDomain = replaceDonain;
        setConfig(AppConstants.KEY_REPLACE_DOMAIN, replaceDonain);
    }

    public String getLastLoginID() {
        return lastLoginID;
    }

    public void removeLastLoginID() {
        removeConfig(AppConstants.LAST_LOGIN_ID);
    }

    public void setLastLoginID(String lastLoginID) {
        this.lastLoginID = lastLoginID;
        setConfig(AppConstants.LAST_LOGIN_ID, lastLoginID);
    }

    public int getPopupTimes() {
        return popupTimes;
    }

    public void setPopupTimes(int popupTimes) {
        this.popupTimes = popupTimes;
        setConfig(AppConstants.POPUP_TIMES, popupTimes);
    }

    public String getPopupRecommentID() {
        return popupRecommentID;
    }

    public void setPopupRecommentID(String popupRecommentID) {
        this.popupRecommentID = popupRecommentID;
        setConfig(AppConstants.POPUP_RECOMMENT_ID, popupRecommentID);
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
        setConfig(AppConstants.AREA, area);
    }

    public boolean isOpenPush() {
        return isOpenPush;
    }

    public void setOpenPush(boolean isOpenPush) {
        this.isOpenPush = isOpenPush;
        setConfig(AppConstants.SETTING_OPEN_PUSH, isOpenPush);
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
        setConfig(AppConstants.NOTIFICATION_ID, notificationID);
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
        setConfig(AppConstants.SETTING_UPDATE, isUpdate);
    }

    public String getLastPackage() {
        return lastPackage;
    }

    public void setLastPackage(String lastPackage) {
        this.lastPackage = lastPackage;
        setConfig(AppConstants.KEY_LAST_PACKAGE, lastPackage);
    }

    public boolean isDoNotAlertAnyMore1() {
        return isDoNotAlertAnyMore1;
    }

    public void setDoNotAlertAnyMore1(boolean isDoNotAlertAnyMore1) {
        this.isDoNotAlertAnyMore1 = isDoNotAlertAnyMore1;
        setConfig(AppConstants.DO_NOT_ALERT_ANY_MORE_1, isDoNotAlertAnyMore1);
    }

    public void setSearchHistoryList(String searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
        setConfig(AppConstants.SEARCH_HISTORY_LIST, searchHistoryList);
    }

    public String getSearchHistoryList() {
        return searchHistoryList;
    }


    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
        setConfig(AppConstants.CHANNEL_ID, channelID);
    }

    public boolean shouldShowGuide() {
        return helper.getValue(AppConstants.SHOW_SHOW_GUIDE, true);
    }

    public void guideShowed() {
        setShouldGuideShow(false);
    }

    public void setShouldGuideShow(boolean isShowed) {
        setConfig(AppConstants.SHOW_SHOW_GUIDE, isShowed);
    }

    public boolean isSilenceUpdate() {
        return isSilenceUpdate;
    }

    public void setSilenceUpdate(boolean isSilenceUpdate) {
        this.isSilenceUpdate = isSilenceUpdate;
        setConfig(AppConstants.KEY_SILENCE_UPDATE, isSilenceUpdate);
    }


    public int getScratchStatus() {
        return scratchStatus;
    }

    public void setScratchStatus(int scratchStatus) {
        this.scratchStatus = scratchStatus;
        setConfig(AppConstants.SCRATCH_STATUS, popupTimes);
    }

    private void setConfig(String key, boolean value) {
        helper.putValue(key, value);
        helper.applyValue();
    }

    private void setConfig(String key, int value) {
        helper.putValue(key, value);
        helper.applyValue();
    }

    private void setConfig(String key, long value) {
        helper.putValue(key, value);
        helper.applyValue();
    }

    private void setConfig(String key, String value) {
        helper.putValue(key, value);
        helper.applyValue();
    }

    public void removeConfig(String key) {
        helper.removeItem(key);
        helper.applyValue();
    }

    public int getLastScore() {
        return lastScore;
    }

    public long getLastMemory() {
        return lastMemory;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
        setConfig(AppConstants.LAST_SCORE, lastScore);
    }

    public void setLastMemory(long lastMemory) {
        this.lastMemory = lastMemory;
        setConfig(AppConstants.LAST_MEMORY, lastMemory);
    }

    public void setHotSearch(String hot_search) {
        this.hotSearch = hot_search;
        setConfig(AppConstants.KEY_RANDOM_HOT_SEARCH, hot_search);
    }

    public String getHotSearch() {
        return hotSearch;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
        setConfig(AppConstants.LAST_REFRESH_TIME, lastRefreshTime);
    }

}

