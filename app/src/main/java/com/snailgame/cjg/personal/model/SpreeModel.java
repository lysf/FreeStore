package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class SpreeModel extends BaseDataModel implements Parcelable {
    public static final String EXCHANGE_TYPE_INTEGRAL="0",EXCHANGE_TYPE_TUTU="1",EXCHANGE_TYPE_FREE="2",EXCHANGE_TYPE_OTHER="3";//                  兑换类型  0:积分兑换  1:兔兔币兑换   2:免费 3：其他
    protected ArrayList<SpreeGiftInfo> itemList;
    protected PageInfo pageInfo;

    @JSONField(name = "list")
    public ArrayList<SpreeGiftInfo> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(ArrayList<SpreeGiftInfo> itemList) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.itemList == null)
            this.itemList = new ArrayList<SpreeGiftInfo>();

        dest.writeParcelableArray(this.itemList.toArray(new SpreeGiftInfo[this.itemList.size()]), flags);

        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    public SpreeModel() {
    }

    private SpreeModel(Parcel in) {
        Parcelable[] list = in.readParcelableArray(SpreeGiftInfo.class.getClassLoader());
        if (list == null) {
            this.itemList = new ArrayList<SpreeGiftInfo>();
        } else {
            this.itemList = new ArrayList(Arrays.asList(list));
        }

        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<SpreeModel> CREATOR = new Creator<SpreeModel>() {
        public SpreeModel createFromParcel(Parcel source) {
            return new SpreeModel(source);
        }

        public SpreeModel[] newArray(int size) {
            return new SpreeModel[size];
        }
    };
}
