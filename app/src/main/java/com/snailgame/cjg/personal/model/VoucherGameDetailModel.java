package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * 代金券明细实体
 * Created by pancl on 2015/4/29.
 */
public class VoucherGameDetailModel extends BaseDataModel {
    protected ModelItem item;

    @JSONField(name = "item")
    public ModelItem getItem() {
        return item;
    }

    @JSONField(name = "item")
    public void setItem(ModelItem item) {
        this.item = item;
    }

    public static final class ModelItem {
        private String cStatus;//状态(0-未使用，1-已使用，2-已过期)
        private String dStart;//开始时间
        private String dEnd;//结束时间
        private String cIcon;//图片
        private String cSource;//来源(应用来源: 0,自研;1,联运;2,指定)
        private int iVoucherId;//代金券ID
        private String sVoucherName;//代金券名称
        private int iAmount;//面额
        private String cColor;//颜色
        private String nUserId;//用户ID
        private int iBalance;//余额
        private VoucherGameRecordModel records;//使用记录

        @JSONField(name = "cStatus")
        public String getcStatus() {
            return cStatus;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
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
        public VoucherGameRecordModel getRecords() {
            return records;
        }

        @JSONField(name = "records")
        public void setRecords(VoucherGameRecordModel records) {
            this.records = records;
        }

    }
}
