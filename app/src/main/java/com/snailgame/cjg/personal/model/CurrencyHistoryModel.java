package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;

/**
 * 兔兔币充值记录
 * Created by TAJ_C on 2015/4/20.
 */
public class CurrencyHistoryModel extends BaseDataModel {
    protected ArrayList<ModelItem> itemList;
    protected PageInfo pageInfo;


    @JSONField(name = "list")
    public ArrayList<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(ArrayList<ModelItem> itemList) {
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


    public static class ModelItem implements Parcelable {
        private String cStatus;// 状态 1=成功，2=正在处理中，其它错误码
        private String sDesc;// 订单描述
        private String dUpdate;// 更新时间
        private String dCreate;// 创建时间
        private int nAppId;// 应用ID
        private String cType;// 订单类型 1：消费，2：赠送，3：计费充值
        private int iMoney;// 订单金额
        private int nUserId;// 用户ID
        private String cSource;// 来源
        private String cRefId;// 关联订单ID
        private int nRecordId;// 记录ID

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

        @JSONField(name = "dUpdate")
        public String getdUpdate() {
            return dUpdate;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
        }

        @JSONField(name = "dCreate")
        public String getdCreate() {
            return dCreate;
        }

        @JSONField(name = "dCreate")
        public void setdCreate(String dCreate) {
            this.dCreate = dCreate;
        }

        @JSONField(name = "nAppId")
        public int getnAppId() {
            return nAppId;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(int nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name = "cType")
        public String getcType() {
            return cType;
        }

        @JSONField(name = "cType")
        public void setcType(String cType) {
            this.cType = cType;
        }


        @JSONField(name = "cSource")
        public String getcSource() {
            return cSource;
        }

        @JSONField(name = "cSource")
        public void setcSource(String cSource) {
            this.cSource = cSource;
        }

        @JSONField(name = "cRefId")
        public String getcRefId() {
            return cRefId;
        }

        @JSONField(name = "cRefId")
        public void setcRefId(String cRefId) {
            this.cRefId = cRefId;
        }

        @JSONField(name = "iMoney")
        public int getiMoney() {
            return iMoney;
        }

        @JSONField(name = "iMoney")
        public void setiMoney(int iMoney) {
            this.iMoney = iMoney;
        }

        @JSONField(name = "nUserId")
        public int getnUserId() {
            return nUserId;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(int nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "nRecordId")
        public int getnRecordId() {
            return nRecordId;
        }

        @JSONField(name = "nRecordId")
        public void setnRecordId(int nRecordId) {
            this.nRecordId = nRecordId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cStatus);
            dest.writeString(this.sDesc);
            dest.writeString(this.dUpdate);
            dest.writeString(this.dCreate);
            dest.writeInt(this.nAppId);
            dest.writeString(this.cType);
            dest.writeInt(this.iMoney);
            dest.writeInt(this.nUserId);
            dest.writeString(this.cSource);
            dest.writeString(this.cRefId);
            dest.writeInt(this.nRecordId);
        }

        public ModelItem() {
        }

        private ModelItem(Parcel in) {
            this.cStatus = in.readString();
            this.sDesc = in.readString();
            this.dUpdate = in.readString();
            this.dCreate = in.readString();
            this.nAppId = in.readInt();
            this.cType = in.readString();
            this.iMoney = in.readInt();
            this.nUserId = in.readInt();
            this.cSource = in.readString();
            this.cRefId = in.readString();
            this.nRecordId = in.readInt();
        }

        public static final Parcelable.Creator<ModelItem> CREATOR = new Parcelable.Creator<ModelItem>() {
            public ModelItem createFromParcel(Parcel source) {
                return new ModelItem(source);
            }

            public ModelItem[] newArray(int size) {
                return new ModelItem[size];
            }
        };
    }
}
