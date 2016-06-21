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
 * 商品代金券实体
 * Created by pancl on 14-4-30.
 */
public class MyVoucherGoodsModel extends BaseDataModel {
    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;
    protected int iExpireNum;

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

    @JSONField(name = "iExpireNum")
    public int getiExpireNum() {
        return iExpireNum;
    }

    @JSONField(name = "iExpireNum")
    public void setiExpireNum(int iExpireNum) {
        this.iExpireNum = iExpireNum;
    }


    public static final class ModelItem implements Parcelable{
        private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private final static String OUT_DATE_FORMAT = "yyyy/M/d";

        private String cStatus;//状态(1-未用,2-已用,3-过期,4-收回,5-锁定)
        private String sDesc;//描述
        private String dStart;//起始日期，unix时间戳
        private String dEnd;//结束日期
        private String cIcon;//图片
        private int iVoucherId;//代金券ID
        private String sVoucherName;//代金券名称
        private int iAmountLimit;//使用限额
        private int iAmount;//面额

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

        @JSONField(name = "dStart")
        public String getdStart() {
            return dStart;
        }

        @JSONField(name = "dStart")
        public void setdStart(String dStart) {
            this.dStart = dStart;
        }

        @JSONField(name = "dEnd")
        public String getdEnd() {
            return dEnd;
        }

        @JSONField(name = "dEnd")
        public void setdEnd(String dEnd) {
            this.dEnd = dEnd;
        }

        @JSONField(name = "cIcon")
        public String getcIcon() {
            return cIcon;
        }

        @JSONField(name = "cIcon")
        public void setcIcon(String cIcon) {
            this.cIcon = cIcon;
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

        @JSONField(name = "iAmountLimit")
        public int getiAmountLimit() {
            return iAmountLimit;
        }

        @JSONField(name = "iAmountLimit")
        public void setiAmountLimit(int iAmountLimit) {
            this.iAmountLimit = iAmountLimit;
        }

        @JSONField(name = "iAmount")
        public int getiAmount() {
            return iAmount;
        }

        @JSONField(name = "iAmount")
        public void setiAmount(int iAmount) {
            this.iAmount = iAmount;
        }

        /**
         * 截止时间格式化
         * @return
         */
        public String getFormatteddEnd() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                return new SimpleDateFormat(OUT_DATE_FORMAT).format(simpleDateFormat.parse(this.dEnd));
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
                return simpleDateFormat.parse(this.dEnd);
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
            dest.writeString(this.dStart);
            dest.writeString(this.dEnd);
            dest.writeString(this.cIcon);
            dest.writeInt(this.iVoucherId);
            dest.writeString(this.sVoucherName);
            dest.writeInt(this.iAmountLimit);
            dest.writeInt(this.iAmount);
        }

        public ModelItem() {
        }

        private ModelItem(Parcel in) {
            this.cStatus = in.readString();
            this.sDesc = in.readString();
            this.dStart = in.readString();
            this.dEnd = in.readString();
            this.cIcon = in.readString();
            this.iVoucherId = in.readInt();
            this.sVoucherName = in.readString();
            this.iAmountLimit = in.readInt();
            this.iAmount = in.readInt();
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
