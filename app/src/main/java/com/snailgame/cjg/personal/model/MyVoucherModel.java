package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 游戏代金券实体
 * Created by pancl on 14-4-30.
 */
public class MyVoucherModel extends BaseDataModel {

    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;
    protected String val;

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

    @JSONField(name = "val")
    public String getVal() {
        return val;
    }

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }


    public static final class ModelItem implements Parcelable {
        private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private final static String OUT_DATE_FORMAT = "yyyy/M/d";

        private String cStatus;//状态(0-未使用，1-已使用，2-已过期)
        private String sDesc;//描述
        private String sUsage; //使用说明
        private String dStart;//开始时间
        private String dEndTime;//结束时间
        private String cIcon;//图片
        private String cSource;//来源(应用来源: 0,自研;1,联运;2,指定)
        private int iVoucherId;//代金券ID
        private String sVoucherName;//代金券名称
        private int iAmount;//面额
        private String cColor;//颜色
        private String nUserId;//用户ID
        private int iBalance;//余额
        private String records;//使用记录

        @JSONField(name = "cStatus")
        public String getcStatus() {
            return cStatus;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
        }

        @JSONField(name = "sDesc")
        public String getsDesc() {
            return sDesc;
        }

        @JSONField(name = "sDesc")
        public void setsDesc(String sDesc) {
            this.sDesc = sDesc;
        }

        @JSONField(name = "sUsage")
        public String getsUsage() {
            return sUsage;
        }

        @JSONField(name = "sUsage")
        public void setsUsage(String sUsage) {
            this.sUsage = sUsage;
        }

        @JSONField(name = "dStart")
        public String getdStart() {
            return dStart;
        }

        @JSONField(name = "dStart")
        public void setdStart(String dStart) {
            this.dStart = dStart;
        }

        @JSONField(name = "dEndTime")
        public String getdEndTime() {
            return dEndTime;
        }

        @JSONField(name = "dEndTime")
        public void setdEndTime(String dEndTime) {
            this.dEndTime = dEndTime;
        }

        @JSONField(name = "cIcon")
        public String getcIcon() {
            return cIcon;
        }

        @JSONField(name = "cIcon")
        public void setcIcon(String cIcon) {
            this.cIcon = cIcon;
        }

        @JSONField(name = "cSource")
        public String getcSource() {
            return cSource;
        }

        @JSONField(name = "cSource")
        public void setcSource(String cSource) {
            this.cSource = cSource;
        }

        @JSONField(name = "iVoucherId")
        public int getiVoucherId() {
            return iVoucherId;
        }

        @JSONField(name = "iVoucherId")
        public void setiVoucherId(int iVoucherId) {
            this.iVoucherId = iVoucherId;
        }

        @JSONField(name = "sVoucherName")
        public String getsVoucherName() {
            return sVoucherName;
        }

        @JSONField(name = "sVoucherName")
        public void setsVoucherName(String sVoucherName) {
            this.sVoucherName = sVoucherName;
        }

        @JSONField(name = "iAmount")
        public int getiAmount() {
            return iAmount;
        }

        @JSONField(name = "iAmount")
        public void setiAmount(int iAmount) {
            this.iAmount = iAmount;
        }

        @JSONField(name = "cColor")
        public String getcColor() {
            return cColor;
        }

        @JSONField(name = "cColor")
        public void setcColor(String cColor) {
            this.cColor = cColor;
        }

        @JSONField(name = "nUserId")
        public String getnUserId() {
            return nUserId;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(String nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "iBalance")
        public int getiBalance() {
            return iBalance;
        }

        @JSONField(name = "iBalance")
        public void setiBalance(int iBalance) {
            this.iBalance = iBalance;
        }

        @JSONField(name = "records")
        public String getRecords() {
            return records;
        }

        @JSONField(name = "records")
        public void setRecords(String records) {
            this.records = records;
        }

        /**
         * 截止时间格式化
         * @return
         */
        public String getFormatteddEnd() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                return new SimpleDateFormat(OUT_DATE_FORMAT).format(simpleDateFormat.parse(this.dEndTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "";
        }

        /**
         * 截止时间格式化
         * @return
         */
        public Date getdEndDate() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                return simpleDateFormat.parse(this.dEndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cStatus);
            dest.writeString(this.sDesc);
            dest.writeString(this.sUsage);
            dest.writeString(this.dStart);
            dest.writeString(this.dEndTime);
            dest.writeString(this.cIcon);
            dest.writeString(this.cSource);
            dest.writeInt(this.iVoucherId);
            dest.writeString(this.sVoucherName);
            dest.writeInt(this.iAmount);
            dest.writeString(this.cColor);
            dest.writeString(this.nUserId);
            dest.writeInt(this.iBalance);
            dest.writeString(this.records);
        }

        public ModelItem() {
        }

        private ModelItem(Parcel in) {
            this.cStatus = in.readString();
            this.sDesc = in.readString();
            this.sUsage = in.readString();
            this.dStart = in.readString();
            this.dEndTime = in.readString();
            this.cIcon = in.readString();
            this.cSource = in.readString();
            this.iVoucherId = in.readInt();
            this.sVoucherName = in.readString();
            this.iAmount = in.readInt();
            this.cColor = in.readString();
            this.nUserId = in.readString();
            this.iBalance = in.readInt();
            this.records = in.readString();
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
}
