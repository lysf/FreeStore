package com.snailgame.cjg.spree.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 礼包
 * Created by TAJ_C on 2015/5/4.
 */
public class SpreeGiftInfo implements Parcelable {
    int iOrder;
    String cClassify;//类别
    int iArticleId;//物品id
    int integral;

    String convertType;
    String cStatus;//上下架状态
    String cConfig;//配置信息
    String cLogo;//logo
    long nAppId;
    String cType;//礼包类型
    String cDelFlag;
    String sAppName;
    boolean avaiable; //是否可以领取
    String cCdkey;
    String dCreate;
    String cHot;//
    int iPlatformId;//平台id
    String sIntro;//详情
    String dUpdate;
    String sArticleName;//物品名称
    int iTotalNum;//总数量
    int iUsedNum;//已用数量
    int iRemianNum; //剩余数量
    int iGetNum;//每个用户可以领取的数量
    int iTao;//已淘数量


    String content;
    String useMethod;
    String deadline;
    boolean isExpand = false;
    int topMargin = 0;

    @JSONField(name = "iIntegral")
    public int getIntegral() {
        return integral;
    }

    @JSONField(name = "iIntegral")
    public void setIntegral(int integral) {
        this.integral = integral;
    }

    @JSONField(name = "cConvertType")
    public String getConvertType() {
        return convertType;
    }

    @JSONField(name = "cConvertType")
    public void setConvertType(String convertType) {
        this.convertType = convertType;
    }


    @JSONField(name = "iUsedNum")
    public int getiUsedNum() {
        return iUsedNum;
    }

    @JSONField(name = "iUsedNum")
    public void setiUsedNum(int iUsedNum) {
        this.iUsedNum = iUsedNum;
    }

    @JSONField(name = "avaiable")
    public boolean isAvaiable() {
        return avaiable;
    }

    @JSONField(name = "avaiable")
    public void setAvaiable(boolean avaiable) {
        this.avaiable = avaiable;
    }

    @JSONField(name = "iRemianNum")
    public int getiRemianNum() {
        return iRemianNum;
    }

    @JSONField(name = "iRemianNum")
    public void setiRemianNum(int iRemianNum) {
        this.iRemianNum = iRemianNum;
    }

    @JSONField(name = "cStatus")
    public String getcStatus() {
        return cStatus;
    }

    @JSONField(name = "cStatus")
    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    @JSONField(name = "iPlatformId")
    public int getiPlatformId() {
        return iPlatformId;
    }

    @JSONField(name = "iPlatformId")
    public void setiPlatformId(int iPlatformId) {
        this.iPlatformId = iPlatformId;
    }

    @JSONField(name = "dUpdate")
    public String getdUpdate() {
        return dUpdate;
    }

    @JSONField(name = "dUpdate")
    public void setdUpdate(String dUpdate) {
        this.dUpdate = dUpdate;
    }

    @JSONField(name = "dCreate")
    public String getdCreate() {
        return dCreate;
    }

    @JSONField(name = "dCreate")
    public void setdCreate(String dCreate) {
        this.dCreate = dCreate;
    }

    @JSONField(name = "cDelFlag")
    public String getcDelFlag() {
        return cDelFlag;
    }

    @JSONField(name = "cDelFlag")
    public void setcDelFlag(String cDelFlag) {
        this.cDelFlag = cDelFlag;
    }

    @JSONField(name = "nAppId")
    public long getnAppId() {
        return nAppId;
    }

    @JSONField(name = "nAppId")
    public void setnAppId(long nAppId) {
        this.nAppId = nAppId;
    }

    @JSONField(name = "sAppName")
    public String getsAppName() {
        return sAppName;
    }

    @JSONField(name = "sAppName")
    public void setsAppName(String sAppName) {
        this.sAppName = sAppName;
    }

    @JSONField(name = "cType")
    public String getcType() {
        return cType;
    }

    @JSONField(name = "cType")
    public void setcType(String cType) {
        this.cType = cType;
    }

    @JSONField(name = "iArticleId")
    public int getiArticleId() {
        return iArticleId;
    }

    @JSONField(name = "iArticleId")
    public void setiArticleId(int iArticleId) {
        this.iArticleId = iArticleId;
    }

    @JSONField(name = "sArticleName")
    public String getsArticleName() {
        return sArticleName;
    }

    @JSONField(name = "sArticleName")
    public void setsArticleName(String sArticleName) {
        this.sArticleName = sArticleName;
    }

    @JSONField(name = "cLogo")
    public String getcLogo() {
        return cLogo;
    }

    @JSONField(name = "cLogo")
    public void setcLogo(String cLogo) {
        this.cLogo = cLogo;
    }

    @JSONField(name = "cClassify")
    public String getcClassify() {
        return cClassify;
    }

    @JSONField(name = "cClassify")
    public void setcClassify(String cClassify) {
        this.cClassify = cClassify;
    }

    @JSONField(name = "iTotalNum")
    public int getiTotalNum() {
        return iTotalNum;
    }

    @JSONField(name = "iTotalNum")
    public void setiTotalNum(int iTotalNum) {
        this.iTotalNum = iTotalNum;
    }

    @JSONField(name = "iGetNum")
    public int getiGetNum() {
        return iGetNum;
    }

    @JSONField(name = "iGetNum")
    public void setiGetNum(int iGetNum) {
        this.iGetNum = iGetNum;
    }

    @JSONField(name = "iTao")
    public int getiTao() {
        return iTao;
    }

    @JSONField(name = "iTao")
    public void setiTao(int iTao) {
        this.iTao = iTao;
    }

    @JSONField(name = "sIntro")
    public String getsIntro() {
        return sIntro;
    }

    @JSONField(name = "sIntro")
    public void setsIntro(String sIntro) {
        this.sIntro = sIntro;
    }

    @JSONField(name = "cConfig")
    public String getcConfig() {
        return cConfig;
    }

    @JSONField(name = "cConfig")
    public void setcConfig(String cConfig) {
        this.cConfig = cConfig;
    }

    @JSONField(name = "cHot")
    public String getcHot() {
        return cHot;
    }

    @JSONField(name = "cHot")
    public void setcHot(String cHot) {
        this.cHot = cHot;
    }

    @JSONField(name = "iOrder")
    public int getiOrder() {
        return iOrder;
    }

    @JSONField(name = "iOrder")
    public void setiOrder(int iOrder) {
        this.iOrder = iOrder;
    }

    @JSONField(name = "cCdkey")
    public String getcCdkey() {
        return cCdkey;
    }

    @JSONField(name = "cCdkey")
    public void setcCdkey(String cCdkey) {
        this.cCdkey = cCdkey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUseMethod() {
        return useMethod;
    }

    public void setUseMethod(String useMethod) {
        this.useMethod = useMethod;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }


    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }


    public SpreeGiftInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iOrder);
        dest.writeString(this.cClassify);
        dest.writeInt(this.iArticleId);
        dest.writeInt(this.integral);
        dest.writeString(this.convertType);
        dest.writeString(this.cStatus);
        dest.writeString(this.cConfig);
        dest.writeString(this.cLogo);
        dest.writeLong(this.nAppId);
        dest.writeString(this.cType);
        dest.writeString(this.cDelFlag);
        dest.writeString(this.sAppName);
        dest.writeByte(avaiable ? (byte) 1 : (byte) 0);
        dest.writeString(this.cCdkey);
        dest.writeString(this.dCreate);
        dest.writeString(this.cHot);
        dest.writeInt(this.iPlatformId);
        dest.writeString(this.sIntro);
        dest.writeString(this.dUpdate);
        dest.writeString(this.sArticleName);
        dest.writeInt(this.iTotalNum);
        dest.writeInt(this.iUsedNum);
        dest.writeInt(this.iRemianNum);
        dest.writeInt(this.iGetNum);
        dest.writeInt(this.iTao);
        dest.writeString(this.content);
        dest.writeString(this.useMethod);
        dest.writeString(this.deadline);
        dest.writeByte(isExpand ? (byte) 1 : (byte) 0);
        dest.writeInt(this.topMargin);
    }

    private SpreeGiftInfo(Parcel in) {
        this.iOrder = in.readInt();
        this.cClassify = in.readString();
        this.iArticleId = in.readInt();
        this.integral = in.readInt();
        this.convertType = in.readString();
        this.cStatus = in.readString();
        this.cConfig = in.readString();
        this.cLogo = in.readString();
        this.nAppId = in.readLong();
        this.cType = in.readString();
        this.cDelFlag = in.readString();
        this.sAppName = in.readString();
        this.avaiable = in.readByte() != 0;
        this.cCdkey = in.readString();
        this.dCreate = in.readString();
        this.cHot = in.readString();
        this.iPlatformId = in.readInt();
        this.sIntro = in.readString();
        this.dUpdate = in.readString();
        this.sArticleName = in.readString();
        this.iTotalNum = in.readInt();
        this.iUsedNum = in.readInt();
        this.iRemianNum = in.readInt();
        this.iGetNum = in.readInt();
        this.iTao = in.readInt();
        this.content = in.readString();
        this.useMethod = in.readString();
        this.deadline = in.readString();
        this.isExpand = in.readByte() != 0;
        this.topMargin = in.readInt();
    }

    public static final Creator<SpreeGiftInfo> CREATOR = new Creator<SpreeGiftInfo>() {
        public SpreeGiftInfo createFromParcel(Parcel source) {
            return new SpreeGiftInfo(source);
        }

        public SpreeGiftInfo[] newArray(int size) {
            return new SpreeGiftInfo[size];
        }
    };
}