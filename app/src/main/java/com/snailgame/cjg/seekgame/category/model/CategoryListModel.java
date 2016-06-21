package com.snailgame.cjg.seekgame.category.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yftx
 * on 2/21/14.
 */
public class CategoryListModel extends BaseDataModel implements Parcelable {
    private ArrayList<ModelItem> infos = new ArrayList<ModelItem>();

    @JSONField(name = "list")
    public ArrayList<ModelItem> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(ArrayList<ModelItem> infos) {
        this.infos = infos;
    }


    public static final class ModelItem implements Parcelable {
        String dCreate; //
        String cDelFlag;
        String dUpdate;
        int iPlatformId;
        int iSortValue;
        String cPicUrl;         // 图片URL
        String sCategoryDesc;  // 集合描述
        int cCategoryType;  //类别分类: 1,类别（请求分类列表动态接口）; 2,TAG（请求标签列表动态接口）
        int iCategoryId;      // 分类ID
        String sCategoryName;    // 集合名称
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

        @JSONField(name = "sCategoryDesc")
        public String getsCategoryDesc() {
            return sCategoryDesc;
        }

        @JSONField(name = "sCategoryDesc")
        public void setsCategoryDesc(String sCategoryDesc) {
            this.sCategoryDesc = sCategoryDesc;
        }

        @JSONField(name = "cCategoryType")
        public int getcCategoryType() {
            return cCategoryType;
        }

        @JSONField(name = "cCategoryType")
        public void setcCategoryType(int cCategoryType) {
            this.cCategoryType = cCategoryType;
        }

        @JSONField(name = "sCategoryName")
        public String getsCategoryName() {
            return sCategoryName;
        }

        @JSONField(name = "sCategoryName")
        public void setsCategoryName(String sCategoryName) {
            this.sCategoryName = sCategoryName;
        }

        @JSONField(name = "iCategoryId")
        public int getiCategoryId() {
            return iCategoryId;
        }

        @JSONField(name = "iCategoryId")
        public void setiCategoryId(int iCategoryId) {
            this.iCategoryId = iCategoryId;
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
            dest.writeString(this.sCategoryDesc);
            dest.writeInt(this.cCategoryType);
            dest.writeInt(this.iCategoryId);
            dest.writeString(this.sCategoryName);
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
            this.sCategoryDesc = in.readString();
            this.cCategoryType = in.readInt();
            this.iCategoryId = in.readInt();
            this.sCategoryName = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.infos == null)
            this.infos = new ArrayList<ModelItem>();

        dest.writeParcelableArray(this.infos.toArray(new ModelItem[this.infos.size()]), flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    public CategoryListModel() {
    }

    private CategoryListModel(Parcel in) {
        Parcelable[] list = in.readParcelableArray(ModelItem.class.getClassLoader());
        if (list == null) {
            this.infos = new ArrayList<ModelItem>();
        } else {
            this.infos = new ArrayList(Arrays.asList(list));
        }

        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<CategoryListModel> CREATOR = new Creator<CategoryListModel>() {
        public CategoryListModel createFromParcel(Parcel source) {
            return new CategoryListModel(source);
        }

        public CategoryListModel[] newArray(int size) {
            return new CategoryListModel[size];
        }
    };
}
