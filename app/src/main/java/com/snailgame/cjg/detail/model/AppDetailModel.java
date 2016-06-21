package com.snailgame.cjg.detail.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 应用详情
 * Created by taj on 14-2-19.
 */
public class AppDetailModel implements Parcelable {
    String sNotice;             //应用通知，富文本编辑
    String cAuditSource;        //审核源，1：开发者审核，2：爬虫审核，3：CMS上传审核
    String cSource;             //应用来源: 0,自研;1,联运;2,其他;3,爬取,4.CPS
    String cScreen;             //屏幕类型:1,竖屏; 2, 横屏；9：可适配
    String cPicScreen;          //截图屏幕类型:1,竖屏; 2, 横屏
    String sAppDesc;            //应用简介
    String sAppExtend;          //APP扩展，JSON格式，例：{spreeId:20}
    String cTags;               //应用标签，{标签ID:标签名称}
    String cAppType;            //主类型: 1,游戏; 2, 应用
    String cOs;                 //系统: 1,安卓; 2,IOS；3,WP
    int iCommentTimes;          //评论次数
    String nForumId;                //论坛版本ID
    int iShareTimes;            //分享次数
    String cPackage;            //应用包名
    String cServicePhone;       //客服电话 0512-67621007
    String dUpdate;             //更新时间
    int iSize;                  //文件大小
    String nScore;              //累计评分
    String cAppName;            //应用大写首字母
    String cIcon;               //游戏图标
    String sUpdateDesc;         //版本更新内容
    String cDownloadUrl;        //下载地址
    String cStatus;             //0 待上架 1上架 2 下架
    String cBbsUrl;             //论坛url
    int nAppId;                 //应用ID
    String cMd5;                //文件MD5
    String iVersionCode;        //当前版本号
    String cPicUrl;             //截图地址
    String sAppName;            //应用名称
    int iDownloadTimes;         //下载量
    String cApkPermission;      //Apk权限
    String dCreate;             //生成时间
    int iPlatformId;            //平台ID
    String cFlowFree;           //0:下载免 1:玩游戏免 2 下载免费,玩游戏免 10 其他
    int iCategoryId;            //分类ID
    String cVersionName;        //当前版本名称

    String cNatVideo;// 普清视频地址
    String cNatVideoPic;// 普清视频截图
    String cIconLabel;  // 图标角标

    String appointment; //是否预约
    String sBroadcast;  //小编广播

    public String getcNatVideo() {
        return cNatVideo;
    }

    @JSONField(name = "cNatVideo")
    public void setcNatVideo(String cNatVideo) {
        this.cNatVideo = cNatVideo;
    }

    public String getcNatVideoPic() {
        return cNatVideoPic;
    }

    @JSONField(name = "cNatVideoPic")
    public void setcNatVideoPic(String cNatVideoPic) {
        this.cNatVideoPic = cNatVideoPic;
    }


    @JSONField(name = "sNotice")
    public String getsNotice() {
        return sNotice;
    }

    @JSONField(name = "cAuditSource")
    public String getcAuditSource() {
        return cAuditSource;
    }

    @JSONField(name = "cSource")
    public String getcSource() {
        return cSource;
    }

    @JSONField(name = "cScreen")
    public String getcScreen() {
        return cScreen;
    }

    @JSONField(name = "cPicScreen")
    public String getcPicScreen() {
        return cPicScreen;
    }

    @JSONField(name = "sAppDesc")
    public String getsAppDesc() {
        return sAppDesc;
    }

    @JSONField(name = "sAppExtend")
    public String getsAppExtend() {
        return sAppExtend;
    }

    @JSONField(name = "cTags")
    public String getcTags() {
        return cTags;
    }

    @JSONField(name = "cAppType")
    public String getcAppType() {
        return cAppType;
    }

    @JSONField(name = "cOs")
    public String getcOs() {
        return cOs;
    }

    @JSONField(name = "iCommentTimes")
    public int getiCommentTimes() {
        return iCommentTimes;
    }

    @JSONField(name = "nForumId")
    public String getnFid() {
        return nForumId;
    }

    @JSONField(name = "iShareTimes")
    public int getiShareTimes() {
        return iShareTimes;
    }

    @JSONField(name = "cPackage")
    public String getcPackage() {
        return cPackage;
    }

    @JSONField(name = "cServicePhone")
    public String getcServicePhone() {
        return cServicePhone;
    }

    @JSONField(name = "dUpdate")
    public String getdUpdate() {
        return dUpdate;
    }

    @JSONField(name = "iSize")
    public int getiSize() {
        return iSize;
    }

    @JSONField(name = "nScore")
    public String getnScore() {
        return nScore;
    }

    @JSONField(name = "cAppName")
    public String getcAppName() {
        return cAppName;
    }

    @JSONField(name = "cIcon")
    public String getcIcon() {
        return cIcon;
    }

    @JSONField(name = "sUpdateDesc")
    public String getsUpdateDesc() {
        return sUpdateDesc;
    }

    @JSONField(name = "cDownloadUrl")
    public String getcDownloadUrl() {
        return cDownloadUrl;
    }

    @JSONField(name = "cStatus")
    public String getcStatus() {
        return cStatus;
    }

    @JSONField(name = "cBbsUrl")
    public String getcBbsUrl() {
        return cBbsUrl;
    }

    @JSONField(name = "nAppId")
    public int getnAppId() {
        return nAppId;
    }

    @JSONField(name = "cMd5")
    public String getcMd5() {
        return cMd5;
    }

    @JSONField(name = "iVersionCode")
    public String getiVersionCode() {
        return iVersionCode;
    }

    @JSONField(name = "cPicUrl")
    public String getcPicUrl() {
        return cPicUrl;
    }

    @JSONField(name = "sAppName")
    public String getsAppName() {
        return sAppName;
    }

    @JSONField(name = "iDownloadTimes")
    public int getiDownloadTimes() {
        return iDownloadTimes;
    }

    @JSONField(name = "cApkPermission")
    public String getcApkPermission() {
        return cApkPermission;
    }

    @JSONField(name = "dCreate")
    public String getdCreate() {
        return dCreate;
    }

    @JSONField(name = "iPlatformId")
    public int getiPlatformId() {
        return iPlatformId;
    }

    @JSONField(name = "cFlowFree")
    public String getcFlowFree() {
        return cFlowFree;
    }

    @JSONField(name = "iCategoryId")
    public int getiCategoryId() {
        return iCategoryId;
    }

    @JSONField(name = "cVersionName")
    public String getcVersionName() {
        return cVersionName;
    }

    @JSONField(name = "sNotice")
    public void setsNotice(String sNotice) {
        this.sNotice = sNotice;
    }

    @JSONField(name = "cAuditSource")
    public void setcAuditSource(String cAuditSource) {
        this.cAuditSource = cAuditSource;
    }

    @JSONField(name = "cSource")
    public void setcSource(String cSource) {
        this.cSource = cSource;
    }

    @JSONField(name = "cScreen")
    public void setcScreen(String cScreen) {
        this.cScreen = cScreen;
    }

    @JSONField(name = "cPicScreen")
    public void setcPicScreen(String cPicScreen) {
        this.cPicScreen = cPicScreen;
    }

    @JSONField(name = "sAppDesc")
    public void setsAppDesc(String sAppDesc) {
        this.sAppDesc = sAppDesc;
    }

    @JSONField(name = "sAppExtend")
    public void setsAppExtend(String sAppExtend) {
        this.sAppExtend = sAppExtend;
    }

    @JSONField(name = "cTags")
    public void setcTags(String cTags) {
        this.cTags = cTags;
    }

    @JSONField(name = "cAppType")
    public void setcAppType(String cAppType) {
        this.cAppType = cAppType;
    }

    @JSONField(name = "cOs")
    public void setcOs(String cOs) {
        this.cOs = cOs;
    }

    @JSONField(name = "iCommentTimes")
    public void setiCommentTimes(int iCommentTimes) {
        this.iCommentTimes = iCommentTimes;
    }

    @JSONField(name = "nForumId")
    public void setnFid(String nFid) {
        this.nForumId = nFid;
    }

    @JSONField(name = "iShareTimes")
    public void setiShareTimes(int iShareTimes) {
        this.iShareTimes = iShareTimes;
    }

    @JSONField(name = "cPackage")
    public void setcPackage(String cPackage) {
        this.cPackage = cPackage;
    }

    @JSONField(name = "cServicePhone")
    public void setcServicePhone(String cServicePhone) {
        this.cServicePhone = cServicePhone;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "iSize")
    public void setiSize(int iSize) {
        this.iSize = iSize;
    }

    @JSONField(name = "nScore")
    public void setnScore(String nScore) {
        this.nScore = nScore;
    }

    @JSONField(name = "cAppName")
    public void setcAppName(String cAppName) {
        this.cAppName = cAppName;
    }

    @JSONField(name = "cIcon")
    public void setcIcon(String cIcon) {
        this.cIcon = cIcon;
    }

    @JSONField(name = "sUpdateDesc")
    public void setsUpdateDesc(String sUpdateDesc) {
        this.sUpdateDesc = sUpdateDesc;
    }

    @JSONField(name = "cDownloadUrl")
    public void setcDownloadUrl(String cDownloadUrl) {
        this.cDownloadUrl = cDownloadUrl;
    }

    @JSONField(name = "cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    @JSONField(name = "cBbsUrl")
    public void setcBbsUrl(String cBbsUrl) {
        this.cBbsUrl = cBbsUrl;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(int nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name = "cMd5")
    public void setcMd5(String cMd5) {
        this.cMd5 = cMd5;
    }

    @JSONField(name = "iVersionCode")
    public void setiVersionCode(String iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name = "cPicUrl")
    public void setcPicUrl(String cPicUrl) {
        this.cPicUrl = cPicUrl;
    }

    @JSONField(name = "sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name = "iDownloadTimes")
    public void setiDownloadTimes(int iDownloadTimes) {
        this.iDownloadTimes = iDownloadTimes;
    }

    @JSONField(name = "cApkPermission")
    public void setcApkPermission(String cApkPermission) {
        this.cApkPermission = cApkPermission;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
    }

    @JSONField(name = "cFlowFree")
    public void setcFlowFree(String cFlowFree) {
        this.cFlowFree = cFlowFree;
    }

    @JSONField(name = "iCategoryId")
    public void setiCategoryId(int iCategoryId) {
        this.iCategoryId = iCategoryId;
    }

    @JSONField(name = "cVersionName")
    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    @JSONField(name = "cTagPic")
    public String getcIconLabel() {
        return cIconLabel;
    }

    @JSONField(name = "cTagPic")
    public void setcIconLabel(String cIconLabel) {
        this.cIconLabel = cIconLabel;
    }

    @JSONField(name = "cAppointment")
    public String getAppointment() {
        return appointment;
    }

    @JSONField(name = "cAppointment")
    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getsBroadcast() {
        return sBroadcast;
    }

    public void setsBroadcast(String sBroadcast) {
        this.sBroadcast = sBroadcast;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sNotice);
        dest.writeString(this.cAuditSource);
        dest.writeString(this.cSource);
        dest.writeString(this.cScreen);
        dest.writeString(this.cPicScreen);
        dest.writeString(this.sAppDesc);
        dest.writeString(this.sAppExtend);
        dest.writeString(this.cTags);
        dest.writeString(this.cAppType);
        dest.writeString(this.cOs);
        dest.writeInt(this.iCommentTimes);
        dest.writeString(this.nForumId);
        dest.writeInt(this.iShareTimes);
        dest.writeString(this.cPackage);
        dest.writeString(this.cServicePhone);
        dest.writeString(this.dUpdate);
        dest.writeInt(this.iSize);
        dest.writeString(this.nScore);
        dest.writeString(this.cAppName);
        dest.writeString(this.cIcon);
        dest.writeString(this.sUpdateDesc);
        dest.writeString(this.cDownloadUrl);
        dest.writeString(this.cStatus);
        dest.writeString(this.cBbsUrl);
        dest.writeInt(this.nAppId);
        dest.writeString(this.cMd5);
        dest.writeString(this.iVersionCode);
        dest.writeString(this.cPicUrl);
        dest.writeString(this.sAppName);
        dest.writeInt(this.iDownloadTimes);
        dest.writeString(this.cApkPermission);
        dest.writeString(this.dCreate);
        dest.writeInt(this.iPlatformId);
        dest.writeString(this.cFlowFree);
        dest.writeInt(this.iCategoryId);
        dest.writeString(this.cVersionName);
        dest.writeString(this.cIconLabel);
        dest.writeString(this.appointment);
        dest.writeString(this.sBroadcast);
    }

    public AppDetailModel() {
    }

    private AppDetailModel(Parcel in) {
        this.sNotice = in.readString();
        this.cAuditSource = in.readString();
        this.cSource = in.readString();
        this.cScreen = in.readString();
        this.cPicScreen = in.readString();
        this.sAppDesc = in.readString();
        this.sAppExtend = in.readString();
        this.cTags = in.readString();
        this.cAppType = in.readString();
        this.cOs = in.readString();
        this.iCommentTimes = in.readInt();
        this.nForumId = in.readString();
        this.iShareTimes = in.readInt();
        this.cPackage = in.readString();
        this.cServicePhone = in.readString();
        this.dUpdate = in.readString();
        this.iSize = in.readInt();
        this.nScore = in.readString();
        this.cAppName = in.readString();
        this.cIcon = in.readString();
        this.sUpdateDesc = in.readString();
        this.cDownloadUrl = in.readString();
        this.cStatus = in.readString();
        this.cBbsUrl = in.readString();
        this.nAppId = in.readInt();
        this.cMd5 = in.readString();
        this.iVersionCode = in.readString();
        this.cPicUrl = in.readString();
        this.sAppName = in.readString();
        this.iDownloadTimes = in.readInt();
        this.cApkPermission = in.readString();
        this.dCreate = in.readString();
        this.iPlatformId = in.readInt();
        this.cFlowFree = in.readString();
        this.iCategoryId = in.readInt();
        this.cVersionName = in.readString();
        this.cIconLabel = in.readString();
        this.appointment = in.readString();
        this.sBroadcast = in.readString();
    }

    public static final Creator<AppDetailModel> CREATOR = new Creator<AppDetailModel>() {
        public AppDetailModel createFromParcel(Parcel source) {
            return new AppDetailModel(source);
        }

        public AppDetailModel[] newArray(int size) {
            return new AppDetailModel[size];
        }
    };
}
