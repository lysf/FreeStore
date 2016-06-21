package com.snailgame.cjg.receiver.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by xixh on 2015/6/18.
 */
public class PointModel implements Parcelable {
    String msg;
    String val;


    @JSONField(name = "msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeString(this.val);
    }

    public PointModel() {
    }

    protected PointModel(Parcel in) {
        this.msg = in.readString();
        this.val = in.readString();
    }

    public static final Parcelable.Creator<PointModel> CREATOR = new Parcelable.Creator<PointModel>() {
        public PointModel createFromParcel(Parcel source) {
            return new PointModel(source);
        }

        public PointModel[] newArray(int size) {
            return new PointModel[size];
        }
    };
}
