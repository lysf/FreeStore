package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by chenping1 on 2014/7/20.
 */
public class UserInfo implements Serializable {
    String spreeNum;             //礼包数量
    String nUserId;             //用户id

    boolean bBossAccount;        //是否为Boss帐号
    int iIntegral;              // 积分
    String cAccount;            // 用户账号
    boolean bFreeFlow;          // 是否免流量，true免流量，false不免流量 (运营商免)
    boolean bFreeFlowAllPlatform;   // 蜗牛免，true免流量，false不免流量
    String cPhoto;              // 用户头像
    String cOrientationDomain;  // 定向流量域名
    int iMoney;                 // 蜗牛币
    String userBirthday;  //年龄
    String cType;               // 免流量类型
    String sVendorArea;         //地区
    String cServiceName;        // 服务商名称
    String sNickName;           // 用户昵称
    String cPhone;              // 手机
    String cUrl;                // 订购页面


    //新增字段
    int iLevel; // 等级
    int iExp; // 经验值
    int iVip; // VIP
    int iCurrentLevelExp; // 当前等级经验值
    int iNextLevelExp;    // 下一等级经验值
    String dStart;       //免流量特权开通时间
    String dEnd;          // 免流量特权结束日期
    String cFlowSize;     // 免流量大小
    boolean bCxbNumber;
    String sHeadFrame;                // 头像边框
    String sSpecificMedal;                // 个性勋章


    int voucher; //代金卷数量

    private boolean cContractMachine = false;    //是否是合约机用户
    private boolean cSignDeal = false;           //是否签署合约协议


    public boolean iscContractMachine() {
        return cContractMachine;
    }

    public void setcContractMachine(boolean cContractMachine) {
        this.cContractMachine = cContractMachine;
    }

    public boolean iscSignDeal() {
        return cSignDeal;
    }

    public void setcSignDeal(boolean cSignDeal) {
        this.cSignDeal = cSignDeal;
    }

    public int getiCurrentLevelExp() {
        return iCurrentLevelExp;
    }

    @JSONField(name = "ICurrentLevelExp")
    public void setiCurrentLevelExp(int iCurrentLevelExp) {
        this.iCurrentLevelExp = iCurrentLevelExp;
    }

    public int getiNextLevelExp() {
        return iNextLevelExp;
    }

    @JSONField(name = "INextLevelExp")
    public void setiNextLevelExp(int iNextLevelExp) {
        this.iNextLevelExp = iNextLevelExp;
    }

    public int getiLevel() {
        return iLevel;
    }

    @JSONField(name = "ILevel")
    public void setiLevel(int iLevel) {
        this.iLevel = iLevel;
    }

    public int getiExp() {
        return iExp;
    }

    @JSONField(name = "IExp")
    public void setiExp(int iExp) {
        this.iExp = iExp;
    }

    public int getiVip() {
        return iVip;
    }

    @JSONField(name = "IVip")
    public void setiVip(int iVip) {
        this.iVip = iVip;
    }


    @JSONField(name = "spreeNum")
    public String getSpreeNum() {
        return spreeNum;
    }

    @JSONField(name = "spreeNum")
    public void setSpreeNum(String spreeNum) {
        this.spreeNum = spreeNum;
    }

    public String getnUserId() {
        return nUserId;
    }

    public boolean isbBossAccount() {
        return bBossAccount;
    }

    public int getiIntegral() {
        return iIntegral;
    }

    public String getcAccount() {
        return cAccount;
    }

    public boolean isbFreeFlow() {
        return bFreeFlow;
    }

    public String getcPhoto() {
        return cPhoto;
    }

    public String getcOrientationDomain() {
        return cOrientationDomain;
    }

    public int getiMoney() {
        return iMoney;
    }

    public String getcType() {
        return cType;
    }

    public String getsVendorArea() {
        return sVendorArea;
    }

    public String getcServiceName() {
        return cServiceName;
    }

    public String getsNickName() {
        return sNickName;
    }

    public String getcPhone() {
        return cPhone;
    }

    public String getcUrl() {
        return cUrl;
    }

    public boolean isbFreeFlowAllPlatform() {
        return bFreeFlowAllPlatform;
    }

    public String getdStart() {
        return dStart;
    }

    public String getdEnd() {
        return dEnd;
    }

    public String getcFlowSize() {
        return cFlowSize;
    }

    @JSONField(name = "nUserId")
    public void setnUserId(String nUserId) {
        this.nUserId = nUserId;
    }

    @JSONField(name = "bBossAccount")
    public void setbBossAccount(boolean bBossAccount) {
        this.bBossAccount = bBossAccount;
    }

    @JSONField(name = "iIntegral")
    public void setiIntegral(int iIntegral) {
        this.iIntegral = iIntegral;
    }

    @JSONField(name = "cAccount")
    public void setcAccount(String cAccount) {
        this.cAccount = cAccount;
    }

    @JSONField(name = "bFreeFlow")
    public void setbFreeFlow(boolean bFreeFlow) {
        this.bFreeFlow = bFreeFlow;
    }

    @JSONField(name = "cPhoto")
    public void setcPhoto(String cPhoto) {
        this.cPhoto = cPhoto;
    }

    @JSONField(name = "cOrientationDomain")
    public void setcOrientationDomain(String cOrientationDomain) {
        this.cOrientationDomain = cOrientationDomain;
    }

    @JSONField(name = "iMoney")
    public void setiMoney(int iMoney) {
        this.iMoney = iMoney;
    }

    @JSONField(name = "cType")
    public void setcType(String cType) {
        this.cType = cType;
    }

    @JSONField(name = "sVendorArea")
    public void setsVendorArea(String sVendorArea) {
        this.sVendorArea = sVendorArea;
    }

    @JSONField(name = "cServiceName")
    public void setcServiceName(String cServiceName) {
        this.cServiceName = cServiceName;
    }

    @JSONField(name = "sNickName")
    public void setsNickName(String sNickName) {
        this.sNickName = sNickName;
    }

    @JSONField(name = "cPhone")
    public void setcPhone(String cPhone) {
        this.cPhone = cPhone;
    }

    @JSONField(name = "nUserId")
    public void setcUrl(String cUrl) {
        this.cUrl = cUrl;
    }

    @JSONField(name = "bFreeFlowAllPlatform")
    public void setbFreeFlowAllPlatform(boolean bFreeFlowAllPlatform) {
        this.bFreeFlowAllPlatform = bFreeFlowAllPlatform;
    }

    @JSONField(name = "dEnd")
    public void setdEnd(String dEnd) {
        this.dEnd = dEnd;
    }


    @JSONField(name = "dStart")
    public void setdStart(String dStart) {
        this.dStart = dStart;
    }

    @JSONField(name = "cFlowSize")
    public void setcFlowSize(String cFlowSize) {
        this.cFlowSize = cFlowSize;
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
    }

    @JSONField(name = "sBirthday")
    public String getUserBirthday() {
        return userBirthday;
    }

    @JSONField(name = "sBirthday")
    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }
    
    public boolean isbCxbNumber() {
        return bCxbNumber;
    }

    public void setbCxbNumber(boolean bCxbNumber) {
        this.bCxbNumber = bCxbNumber;
    }

    public String getsSpecificMedal() {
        return sSpecificMedal;
    }

    public String getsHeadFrame() {
        return sHeadFrame;
    }

    @JSONField(name = "sHeadFrame")
    public void setsHeadFrame(String sHeadFrame) {
        this.sHeadFrame = sHeadFrame;
    }

    @JSONField(name = "sSpecificMedal")
    public void setsSpecificMedal(String sSpecificMedal) {
        this.sSpecificMedal = sSpecificMedal;
    }
}
