package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * Created by TAJ_C on 2015/11/12.
 */
public class ScoreGroupModel extends BaseDataModel {

    @JSONField(name = "val")
    protected boolean val;

    @JSONField(name = "list")
    protected List<ModelItem> itemList;

    public boolean isVal() {
        return val;
    }

    public void setVal(boolean val) {
        this.val = val;
    }

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public final static class ModelItem implements Parcelable{
        @JSONField(name = "cMonth")
        private String month; // 月份
        @JSONField(name = "iIntegral")
        private int integral; // 积分
        @JSONField(name = "dValidity")
        private String validity; // 有效期
        @JSONField(name = "dCreate")
        private String createTime; // 生成时间

        private boolean isExtend;

        public ModelItem() {
        }

        protected ModelItem(Parcel in) {
            month = in.readString();
            integral = in.readInt();
            validity = in.readString();
            createTime = in.readString();
            isExtend = in.readByte() != 0;
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

        public boolean isExtend() {
            return isExtend;
        }

        public void setIsExtend(boolean isExtend) {
            this.isExtend = isExtend;
        }
        public String getMonth() {
            return month;
        }
        public void setMonth(String month) {
            this.month = month;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public String getValidity() {
            return validity;
        }

        public void setValidity(String validity) {
            this.validity = validity;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(month);
            dest.writeInt(integral);
            dest.writeString(validity);
            dest.writeString(createTime);
            dest.writeByte((byte) (isExtend ? 1 : 0));
        }


    }
}
