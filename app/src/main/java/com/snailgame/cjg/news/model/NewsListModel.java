package com.snailgame.cjg.news.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/12.
 */
@Setter
@Getter
public class NewsListModel extends BaseDataModel {

    private ModelItem item;

    @Getter
    @Setter
    public static class ModelItem {
        private PageInfo page;

        private List<DataBean> data;

        @Getter
        @Setter
        public static class DataBean implements Parcelable {
            private String dCreate;
            private String cTraceId;
            private int nChannelId;
            private String sTitle;
            private String cUrl;
            private String nArticleId;
            private List<ImagesBean> images;

            private List<NewsIgnoreModel> noInterestTags;

            private String cStyleType; //样式"1":一个标题；"2":一个标题+一个小图片；"3":一个标题+3个小图片；"4":一个标题+一个大图片

            private boolean isRead;  //是否看过

            @JSONField(name = "cItemType")
            private String itemType = "1"; //资讯类型: 1:为普通资讯 ; 2:为推广..
            @JSONField(name = "sTags")
            private String tags; //推广的标签

            public DataBean() {
            }

            protected DataBean(Parcel in) {
                dCreate = in.readString();
                cTraceId = in.readString();
                nChannelId = in.readInt();
                sTitle = in.readString();
                cUrl = in.readString();
                nArticleId = in.readString();
                cStyleType = in.readString();
                isRead = in.readByte() != 0;
                itemType = in.readString();
                tags = in.readString();
            }

            public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
                @Override
                public DataBean createFromParcel(Parcel in) {
                    return new DataBean(in);
                }

                @Override
                public DataBean[] newArray(int size) {
                    return new DataBean[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(dCreate);
                dest.writeString(cTraceId);
                dest.writeInt(nChannelId);
                dest.writeString(sTitle);
                dest.writeString(cUrl);
                dest.writeString(nArticleId);
                dest.writeString(cStyleType);
                dest.writeByte((byte) (isRead ? 1 : 0));
                dest.writeString(itemType);
                dest.writeString(tags);
            }

            @Getter
            @Setter
            public static class ImagesBean {
                private int nHeight;
                private String cSrcUrl;
                private int nOrder;
                private int nWidth;
                private String sDesc;
                private String sTitle;
                private String cUrl;
                private String nArticleId;
                private int nThumbnailsOrder;
                private String cType;
                private String cPicId;

            }


        }
    }


}
