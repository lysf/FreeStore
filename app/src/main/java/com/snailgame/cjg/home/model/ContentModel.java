package com.snailgame.cjg.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by sunxy on 2014/9/18.
 */
public class ContentModel implements Parcelable {
    private int iId;//内容id
    private String dCreate;
    private int iOrder;//顺序
    private String sBackup;//自定义字段json,参数如p1,p2...
    private String sTarget;//发布对象 0-全部用户 1-170用户 其他-运营商_地区(运营商字典2:中国移动 3：中国电信 4：中国联通 地区字典如11000:北京 32000:江苏 整个如2,3_11000,32000 )
    private String cDelFlag;
    private String cStatus;
    private String dUpdate;
    private int iPlatformId;


    private String cSource;//1-自定义 2-礼包 3-游戏推荐 4-合集 5-活动 6-攻略 7-资讯 8-论坛 9-游戏 10-积分墙 11-应用
    private int iHeadlineId;
    private String sRefId;//引用主键
    private String sTitle;//标题
    private String sSubtitle;//副标题
    private String sSummary;//简介
    private String sImageUrl;//图片地址
    private String sImageBig;//--大图url (*** 例如 免商店4.0 首页广告图 ***)
    private String sJumpUrl;//点击跳转
    private String sExtend;//引用数据扩展json参数，参照以前
    private String dStart;//开始时间
    private String dEnd;//结束时间
    private String cJumpType;//跳转类型-字典
    private String cLayout;
    private String sOperators;
    private String sArea;
    private String p1;
    private String p2;
    private String p3;


    private String cChannel;

    private int commentNum;

    public String getcChannel() {
        return cChannel;
    }

    @JSONField(name = "cChannel")
    public void setcChannel(String cChannel) {
        this.cChannel = cChannel;
    }

    public int getiId() {
        return iId;
    }

    public String getdCreate() {
        return dCreate;
    }

    public int getiOrder() {
        return iOrder;
    }

    public String getsBackup() {
        return sBackup;
    }

    public String getsTarget() {
        return sTarget;
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

    public int getiHeadlineId() {
        return iHeadlineId;
    }

    public String getsRefId() {
        return sRefId;
    }

    public String getsTitle() {
        return sTitle;
    }

    public String getsSubtitle() {
        return sSubtitle;
    }

    public String getsSummary() {
        return sSummary;
    }

    public String getsImageUrl() {
        return sImageUrl;
    }

    public String getsImageBig() {
        return sImageBig;
    }

    public void setsImageBig(String sImageBig) {
        this.sImageBig = sImageBig;
    }
    public String getsJumpUrl() {
        return sJumpUrl;
    }

    public String getsExtend() {
        return sExtend;
    }

    public String getdStart() {
        return dStart;
    }

    public String getdEnd() {
        return dEnd;
    }

    public String getcJumpType() {
        return cJumpType;
    }

    public String getcLayout() {
        return cLayout;
    }

    public String getsOperators() {
        return sOperators;
    }

    public String getsArea() {
        return sArea;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getP3() {
        return p3;
    }

    @JSONField(name = "iId")
    public void setiId(int iId) {
        this.iId = iId;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(int iOrder) {
        this.iOrder = iOrder;
    }

    @JSONField(name = "sBackup")
    public void setsBackup(String sBackup) {
        this.sBackup = sBackup;
    }

    @JSONField(name = "sTarget")
    public void setsTarget(String sTarget) {
        this.sTarget = sTarget;
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

    @JSONField(name = "iHeadlineId")
    public void setiHeadlineId(int iHeadlineId) {
        this.iHeadlineId = iHeadlineId;
    }

    @JSONField(name = "sRefId")
    public void setsRefId(String sRefId) {
        this.sRefId = sRefId;
    }

    @JSONField(name = "sTitle")
    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    @JSONField(name = "sSubtitle")
    public void setsSubtitle(String sSubtitle) {
        this.sSubtitle = sSubtitle;
    }

    @JSONField(name = "sSummary")
    public void setsSummary(String sSummary) {
        this.sSummary = sSummary;
    }

    @JSONField(name = "sImageUrl")
    public void setsImageUrl(String sImageUrl) {
        this.sImageUrl = sImageUrl;
    }

    @JSONField(name = "sJumpUrl")
    public void setsJumpUrl(String sJumpUrl) {
        this.sJumpUrl = sJumpUrl;
    }

    @JSONField(name = "sExtend")
    public void setsExtend(String sExtend) {
        this.sExtend = sExtend;
    }

    @JSONField(name = "dStart")
    public void setdStart(String dStart) {
        this.dStart = dStart;
    }

    @JSONField(name = "dEnd")
    public void setdEnd(String dEnd) {
        this.dEnd = dEnd;
    }

    @JSONField(name = "cJumpType")
    public void setcJumpType(String cJumpType) {
        this.cJumpType = cJumpType;
    }

    @JSONField(name = "cLayout")
    public void setcLayout(String cLayout) {
        this.cLayout = cLayout;
    }

    @JSONField(name = "sOperators")
    public void setsOperators(String sOperators) {
        this.sOperators = sOperators;
    }

    @JSONField(name = "sArea")
    public void setsArea(String sArea) {
        this.sArea = sArea;
    }

    @JSONField(name = "p1")
    public void setP1(String p1) {
        this.p1 = p1;
    }

    @JSONField(name = "p2")
    public void setP2(String p2) {
        this.p2 = p2;
    }

    @JSONField(name = "p3")
    public void setP3(String p3) {
        this.p3 = p3;
    }


    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iId);
        dest.writeString(this.dCreate);
        dest.writeInt(this.iOrder);
        dest.writeString(this.sBackup);
        dest.writeString(this.sTarget);
        dest.writeString(this.cDelFlag);
        dest.writeString(this.cStatus);
        dest.writeString(this.dUpdate);
        dest.writeInt(this.iPlatformId);
        dest.writeString(this.cSource);
        dest.writeInt(this.iHeadlineId);
        dest.writeString(this.sRefId);
        dest.writeString(this.sTitle);
        dest.writeString(this.sSubtitle);
        dest.writeString(this.sSummary);
        dest.writeString(this.sImageUrl);
        dest.writeString(this.sJumpUrl);
        dest.writeString(this.sExtend);
        dest.writeString(this.dStart);
        dest.writeString(this.dEnd);
        dest.writeString(this.cJumpType);
        dest.writeString(this.cLayout);
        dest.writeString(this.sOperators);
        dest.writeString(this.sArea);
        dest.writeString(this.p1);
        dest.writeString(this.p2);
        dest.writeString(this.p3);
        dest.writeInt(this.commentNum);
    }

    public ContentModel() {
    }

    private ContentModel(Parcel in) {
        this.iId = in.readInt();
        this.dCreate = in.readString();
        this.iOrder = in.readInt();
        this.sBackup = in.readString();
        this.sTarget = in.readString();
        this.cDelFlag = in.readString();
        this.cStatus = in.readString();
        this.dUpdate = in.readString();
        this.iPlatformId = in.readInt();
        this.cSource = in.readString();
        this.iHeadlineId = in.readInt();
        this.sRefId = in.readString();
        this.sTitle = in.readString();
        this.sSubtitle = in.readString();
        this.sSummary = in.readString();
        this.sImageUrl = in.readString();
        this.sJumpUrl = in.readString();
        this.sExtend = in.readString();
        this.dStart = in.readString();
        this.dEnd = in.readString();
        this.cJumpType = in.readString();
        this.cLayout = in.readString();
        this.sOperators = in.readString();
        this.sArea = in.readString();
        this.p1 = in.readString();
        this.p2 = in.readString();
        this.p3 = in.readString();
        this.commentNum = in.readInt();
    }

    public static final Creator<ContentModel> CREATOR = new Creator<ContentModel>() {
        public ContentModel createFromParcel(Parcel source) {
            return new ContentModel(source);
        }

        public ContentModel[] newArray(int size) {
            return new ContentModel[size];
        }
    };
}
