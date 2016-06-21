package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * 游戏代金券使用记录实体
 * Created by pancl on 2015/4/29.
 */
public class VoucherGameRecordModel {
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

    public static final class ModelItem {

        private String cChannel;// 平台渠道号
        private String sAppName;// 应用名称
        private String sVoucherName;// 代金券名称
        private String cImei;// 设备号
        private String nUserId;// 用户ID
        private String dCreate;// 使用时间
        private String nRecordId;// 记录ID
        private int iPlatformId;// 平台ID
        private String sDesc;// 使用描述
        private int iConsume;// 消费金额
        private int iVoucherId;//代金券ID
        private int nAppId;// 应用ID

        @JSONField(name = "cChannel")
        public String getcChannel() {
            return cChannel;
        }

        @JSONField(name = "cChannel")
        public void setcChannel(String cChannel) {
            this.cChannel = cChannel;
        }

        @JSONField(name = "sAppName")
        public String getsAppName() {
            return sAppName;
        }

        @JSONField(name = "sAppName")
        public void setsAppName(String sAppName) {
            this.sAppName = sAppName;
        }

        @JSONField(name = "sVoucherName")
        public String getsVoucherName() {
            return sVoucherName;
        }

        @JSONField(name = "sVoucherName")
        public void setsVoucherName(String sVoucherName) {
            this.sVoucherName = sVoucherName;
        }

        @JSONField(name = "cImei")
        public String getcImei() {
            return cImei;
        }

        @JSONField(name = "cImei")
        public void setcImei(String cImei) {
            this.cImei = cImei;
        }

        @JSONField(name = "nUserId")
        public String getnUserId() {
            return nUserId;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(String nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "dCreate")
        public String getdCreate() {
            return dCreate;
        }

        @JSONField(name = "dCreate")
        public void setdCreate(String dCreate) {
            this.dCreate = dCreate;
        }

        @JSONField(name = "nRecordId")
        public String getnRecordId() {
            return nRecordId;
        }

        @JSONField(name = "nRecordId")
        public void setnRecordId(String nRecordId) {
            this.nRecordId = nRecordId;
        }

        @JSONField(name = "iPlatformId")
        public int getiPlatformId() {
            return iPlatformId;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "sDesc")
        public String getsDesc() {
            return sDesc;
        }

        @JSONField(name = "sDesc")
        public void setsDesc(String sDesc) {
            this.sDesc = sDesc;
        }

        @JSONField(name = "iConsume")
        public int getiConsume() {
            return iConsume;
        }

        @JSONField(name = "iConsume")
        public void setiConsume(int iConsume) {
            this.iConsume = iConsume;
        }

        @JSONField(name = "iVoucherId")
        public int getiVoucherId() {
            return iVoucherId;
        }

        @JSONField(name = "iVoucherId")
        public void setiVoucherId(int iVoucherId) {
            this.iVoucherId = iVoucherId;
        }

        @JSONField(name = "nAppId")
        public int getnAppId() {
            return nAppId;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(int nAppId) {
            this.nAppId = nAppId;
        }
    }
}
