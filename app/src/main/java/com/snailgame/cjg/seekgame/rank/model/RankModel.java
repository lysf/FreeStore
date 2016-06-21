package com.snailgame.cjg.seekgame.rank.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxy on 2015/3/23.
 */
public class RankModel extends BaseDataModel implements Parcelable {
    private Root root;

    public Root getRoot() {
        return root;
    }

    @JSONField(name = "root")
    public void setRoot(Root root) {
        this.root = root;
    }

    public static class Root implements Parcelable {

        private String country;
        private List<RankAppInfo> appLists = new ArrayList<>();
        private String mediaType; // 媒体类型
        private String feedType; // 排行类型
        private String updatedTime;//更新时间
        private String genre; // 分类
        private int configId; // 主键ID

        public String getUpdatedTime() {
            return updatedTime;
        }

        @JSONField(name = "updatedTime")
        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public String getCountry() {
            return country;
        }

        @JSONField(name = "cCountry")
        public void setCountry(String country) {
            this.country = country;
        }

        public List<RankAppInfo> getAppLists() {
            return appLists;
        }

        @JSONField(name = "app")
        public void setAppLists(List<RankAppInfo> appLists) {
            this.appLists = appLists;
        }

        public String getMediaType() {
            return mediaType;
        }

        @JSONField(name = "SMediaType")
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public String getFeedType() {
            return feedType;
        }

        @JSONField(name = "SFeedType")
        public void setFeedType(String feedType) {
            this.feedType = feedType;
        }

        public String getGenre() {
            return genre;
        }

        @JSONField(name = "CGenre")
        public void setGenre(String genre) {
            this.genre = genre;
        }

        public int getConfigId() {
            return configId;
        }

        @JSONField(name = "configId")
        public void setConfigId(int configId) {
            this.configId = configId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.country);
            dest.writeTypedList(appLists);
            dest.writeString(this.mediaType);
            dest.writeString(this.feedType);
            dest.writeString(this.genre);
            dest.writeInt(this.configId);
        }

        public Root() {
        }

        private Root(Parcel in) {
            this.country = in.readString();
            in.readTypedList(appLists, RankAppInfo.CREATOR);
            this.mediaType = in.readString();
            this.feedType = in.readString();
            this.genre = in.readString();
            this.configId = in.readInt();
        }

        public static final Creator<Root> CREATOR = new Creator<Root>() {
            public Root createFromParcel(Parcel source) {
                return new Root(source);
            }

            public Root[] newArray(int size) {
                return new Root[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.root, flags);
    }

    public RankModel() {
    }

    private RankModel(Parcel in) {
        this.root = in.readParcelable(Root.class.getClassLoader());
    }

    public static final Parcelable.Creator<RankModel> CREATOR = new Parcelable.Creator<RankModel>() {
        public RankModel createFromParcel(Parcel source) {
            return new RankModel(source);
        }

        public RankModel[] newArray(int size) {
            return new RankModel[size];
        }
    };
}
