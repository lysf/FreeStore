package com.snailgame.cjg.member.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 会员中心 专享礼包
 * Created by TAJ_C on 2015/12/15.
 */
public class MemberArticle extends MemberLevelArticle implements Parcelable{

    @JSONField(name = "cCdkey")
    private String cdKey;
    @JSONField(name = "iUsedNum")
    private int usedNum;
    @JSONField(name = "iRemianNum")
    private int remianNum;
    @JSONField(name = "receive")
    boolean isReceive;

    String content;
    String useMethod;
    String deadline;

    public MemberArticle() {
    }

    protected MemberArticle(Parcel in) {
        cdKey = in.readString();
        usedNum = in.readInt();
        remianNum = in.readInt();
        isReceive = in.readByte() != 0;
        content = in.readString();
        useMethod = in.readString();
        deadline = in.readString();
    }

    public static final Creator<MemberArticle> CREATOR = new Creator<MemberArticle>() {
        @Override
        public MemberArticle createFromParcel(Parcel in) {
            return new MemberArticle(in);
        }

        @Override
        public MemberArticle[] newArray(int size) {
            return new MemberArticle[size];
        }
    };

    public String getCdKey() {
        return cdKey;
    }

    public void setCdKey(String cdKey) {
        this.cdKey = cdKey;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    public int getRemianNum() {
        return remianNum;
    }

    public void setRemianNum(int remianNum) {
        this.remianNum = remianNum;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setIsReceive(boolean isReceive) {
        this.isReceive = isReceive;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cdKey);
        dest.writeInt(usedNum);
        dest.writeInt(remianNum);
        dest.writeByte((byte) (isReceive ? 1 : 0));
        dest.writeString(content);
        dest.writeString(useMethod);
        dest.writeString(deadline);
    }
}
