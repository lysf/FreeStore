package com.snailgame.cjg.seekgame.rank.model;

import android.os.Parcel;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseAppInfo;

/**
 * Created by sunxy on 2015/3/24.
 */
public class RankAppInfo extends BaseAppInfo implements android.os.Parcelable {
    private String rssName; // itunes名称
    private String rssId; // itunes应用ID
    private String rssImg; // itunes图标
    private String refName; // 免商店应用名
    private float commonLevel; //免商店评分
    private int refId; // 免商店应用ID
    private String refDownLoadUrl; // 免商店下载地址
    private String platforms; //平台 逗号分隔，且前后逗号

    public String getRssName() {
        return rssName;
    }
    @JSONField(name = "rssName")
    public void setRssName(String rssName) {
        this.rssName = rssName;
    }

    public String getRssId() {
        return rssId;
    }
    @JSONField(name = "rssId")
    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public String getRssImg() {
        return rssImg;
    }
    @JSONField(name = "rssImg")
    public void setRssImg(String rssImg) {
        this.rssImg = rssImg;
    }

    public String getRefName() {
        return refName;
    }
    @JSONField(name = "sRefName")
    public void setRefName(String refName) {
        this.refName = refName;
    }

    public float getCommonLevel() {
        return commonLevel;
    }
    @JSONField(name = "fCommonLevel")
    public void setCommonLevel(float commonLevel) {
        this.commonLevel = commonLevel;
    }

    public int getRefId() {
        return refId;
    }
    @JSONField(name = "nRefId")
    public void setRefId(int refId) {
        this.refId = refId;
    }

    public String getRefDownLoadUrl() {
        return refDownLoadUrl;
    }
    @JSONField(name = "sRefDownLoadUrl")
    public void setRefDownLoadUrl(String refDownLoadUrl) {
        this.refDownLoadUrl = refDownLoadUrl;
    }

    public String getPlatforms() {
        return platforms;
    }
    @JSONField(name = "cPlatforms")
    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rssName);
        dest.writeString(this.rssId);
        dest.writeString(this.rssImg);
        dest.writeString(this.refName);
        dest.writeFloat(this.commonLevel);
        dest.writeInt(this.refId);
        dest.writeString(this.refDownLoadUrl);
        dest.writeString(this.platforms);

    }

    public RankAppInfo() {
    }

    private RankAppInfo(Parcel in) {
        this.rssName = in.readString();
        this.rssId = in.readString();
        this.rssImg = in.readString();
        this.refName = in.readString();
        this.commonLevel = in.readFloat();
        this.refId = in.readInt();
        this.refDownLoadUrl = in.readString();
        this.platforms = in.readString();

    }

    public static final Creator<RankAppInfo> CREATOR = new Creator<RankAppInfo>() {
        public RankAppInfo createFromParcel(Parcel source) {
            return new RankAppInfo(source);
        }

        public RankAppInfo[] newArray(int size) {
            return new RankAppInfo[size];
        }
    };
}
