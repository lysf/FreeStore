package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * 商品代金券合作游戏实体
 * Created by pancl on 14-4-30.
 */
public class MyVoucherGoodsCooperModel extends BaseDataModel {
    protected List<ModelItem> itemList;

    @JSONField(name = "data")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "data")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem implements Parcelable {
        private String appId;
        private String appName;
        private String appIcon;
        private String url;

        public String getAppIcon() {
            return appIcon;
        }

        @JSONField(name = "goods_app_icon")
        public void setAppIcon(String appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppId() {
            return appId;
        }

        @JSONField(name = "goods_app_id")
        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppName() {
            return appName;
        }

        @JSONField(name = "goods_app_name")
        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getUrl() {
            return url;
        }

        @JSONField(name = "url")
        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.appId);
            dest.writeString(this.appName);
            dest.writeString(this.appIcon);
            dest.writeString(this.url);
        }

        public ModelItem() {
        }

        protected ModelItem(Parcel in) {
            this.appId = in.readString();
            this.appName = in.readString();
            this.appIcon = in.readString();
            this.url = in.readString();
        }

        public static final Parcelable.Creator<ModelItem> CREATOR = new Parcelable.Creator<ModelItem>() {
            public ModelItem createFromParcel(Parcel source) {
                return new ModelItem(source);
            }

            public ModelItem[] newArray(int size) {
                return new ModelItem[size];
            }
        };
    }
}
