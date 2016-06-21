package com.snailgame.cjg.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by xiadi on 2014/6/10.
 */
public class PageInfo implements Parcelable {
    private int totalRowCount;
    private int pageRowCount;
    private int totalPageCount;
    private int requestPageNum;

    @JSONField(name = "iTotalRowCount")
    public int getTotalRowCount() {
        return totalRowCount;
    }
    @JSONField(name = "iTotalRowCount")
    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }
    @JSONField(name = "iPageRowCount")
    public int getPageRowCount() {
        return pageRowCount;
    }
    @JSONField(name = "iPageRowCount")
    public void setPageRowCount(int pageRowCount) {
        this.pageRowCount = pageRowCount;
    }
    @JSONField(name = "iTotalPageCount")
    public int getTotalPageCount() {
        return totalPageCount;
    }
    @JSONField(name = "iTotalPageCount")
    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }
    @JSONField(name = "iRequestPageNum")
    public int getRequestPageNum() {
        return requestPageNum;
    }
    @JSONField(name = "iRequestPageNum")
    public void setRequestPageNum(int requestPageNum) {
        this.requestPageNum = requestPageNum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalRowCount);
        dest.writeInt(this.pageRowCount);
        dest.writeInt(this.totalPageCount);
        dest.writeInt(this.requestPageNum);
    }

    public PageInfo() {
    }

    private PageInfo(Parcel in) {
        this.totalRowCount = in.readInt();
        this.pageRowCount = in.readInt();
        this.totalPageCount = in.readInt();
        this.requestPageNum = in.readInt();
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        public PageInfo createFromParcel(Parcel source) {
            return new PageInfo(source);
        }

        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}
