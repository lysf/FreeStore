package com.snailgame.cjg.news.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 分类下应用列表
 * Created by TAJ_C on 2016/4/12.
 */
@Getter
@Setter
public class ChannelListModel extends BaseDataModel {

    @JSONField(name = "item")
    private List<ModelItem> item;

    @Getter
    @Setter
    public static class ModelItem implements Parcelable {
        @JSONField(name = "sChannelName")
        private String sChannelName;
        @JSONField(name = "iChannelId")
        private int channelId;

        private boolean isShow;

        public ModelItem() {
        }

        protected ModelItem(Parcel in) {
            sChannelName = in.readString();
            channelId = in.readInt();
            isShow = in.readByte() != 0;
        }

        public static final Creator<ModelItem> CREATOR = new Creator<ModelItem>() {
            @Override
            public ModelItem createFromParcel(Parcel in) {
                return new ModelItem(in);
            }

            @Override
            public ModelItem[] newArray(int size) {
                return new ModelItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sChannelName);
            dest.writeInt(channelId);
            dest.writeByte((byte) (isShow ? 1 : 0));
        }

    }
}
