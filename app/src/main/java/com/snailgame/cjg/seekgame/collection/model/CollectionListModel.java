package com.snailgame.cjg.seekgame.collection.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

 /**
  * 合集Model
 * Created by chenping1 on 2014/4/9.
 */
public class CollectionListModel extends BaseDataModel implements Parcelable {

    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static final class ModelItem implements Parcelable {
        String dCreate; //
        String cDelFlag;
        String dUpdate;
        int iPlatformId;
        int iSortValue;
        String cPicUrl;         // 图片URL
        String sCollectionDec;  // 集合描述
        int iCollectionId;      // 集合ID
        String sCollectionName;    // 集合名称
        String cAppType;

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

        @JSONField(name = "dUpdate")
        public String getdUpdate() {
            return dUpdate;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
        }

        @JSONField(name = "iPlatformId")
        public int getiPlatformId() {
            return iPlatformId;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "iSortValue")
        public int getiSortValue() {
            return iSortValue;
        }

        @JSONField(name = "iSortValue")
        public void setiSortValue(int iSortValue) {
            this.iSortValue = iSortValue;
        }

        @JSONField(name = "cPicUrl")
        public String getcPicUrl() {
            return cPicUrl;
        }

        @JSONField(name = "cPicUrl")
        public void setcPicUrl(String cPicUrl) {
            this.cPicUrl = cPicUrl;
        }

        @JSONField(name = "sCollectionDec")
        public String getsCollectionDec() {
            return sCollectionDec;
        }

        @JSONField(name = "sCollectionDec")
        public void setsCollectionDec(String sCollectionDec) {
            this.sCollectionDec = sCollectionDec;
        }

        @JSONField(name = "sCollectionName")
        public String getsCollectionName() {
            return sCollectionName;
        }

        @JSONField(name = "sCollectionName")
        public void setsCollectionName(String sCollectionName) {
            this.sCollectionName = sCollectionName;
        }

        @JSONField(name = "iCollectionId")
        public int getiCollectionId() {
            return iCollectionId;
        }

        @JSONField(name = "iCollectionId")
        public void setiCollectionId(int iCollectionId) {
            this.iCollectionId = iCollectionId;
        }

        @JSONField(name = "cAppType")
        public String getcAppType() {
            return cAppType;
        }

        @JSONField(name = "cAppType")
        public void setcAppType(String cAppType) {
            this.cAppType = cAppType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.dCreate);
            dest.writeString(this.cDelFlag);
            dest.writeString(this.dUpdate);
            dest.writeInt(this.iPlatformId);
            dest.writeInt(this.iSortValue);
            dest.writeString(this.cPicUrl);
            dest.writeString(this.sCollectionDec);
            dest.writeInt(this.iCollectionId);
            dest.writeString(this.sCollectionName);
            dest.writeString(this.cAppType);
        }

        public ModelItem() {
        }

        private ModelItem(Parcel in) {
            this.dCreate = in.readString();
            this.cDelFlag = in.readString();
            this.dUpdate = in.readString();
            this.iPlatformId = in.readInt();
            this.iSortValue = in.readInt();
            this.cPicUrl = in.readString();
            this.sCollectionDec = in.readString();
            this.iCollectionId = in.readInt();
            this.sCollectionName = in.readString();
            this.cAppType = in.readString();
        }

        public static final Creator<ModelItem> CREATOR = new Creator<ModelItem>() {
            public ModelItem createFromParcel(Parcel source) {
                return new ModelItem(source);
            }

            public ModelItem[] newArray(int size) {
                return new ModelItem[size];
            }
        };
    }

    public CollectionListModel() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (itemList == null)
            itemList = new ArrayList<ModelItem>();

        dest.writeParcelableArray(this.itemList.toArray(new ModelItem[this.itemList.size()]), flags);
        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    private CollectionListModel(Parcel in) {
        Parcelable[] list = in.readParcelableArray(ModelItem.class.getClassLoader());
        if (list == null) {
            this.itemList = new ArrayList<ModelItem>();
        } else {
            this.itemList = new ArrayList(Arrays.asList(list));
        }

        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<CollectionListModel> CREATOR = new Creator<CollectionListModel>() {
        public CollectionListModel createFromParcel(Parcel source) {
            return new CollectionListModel(source);
        }

        public CollectionListModel[] newArray(int size) {
            return new CollectionListModel[size];
        }
    };
}
