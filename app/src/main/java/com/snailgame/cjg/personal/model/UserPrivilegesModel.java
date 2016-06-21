package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * 用户特权实体
 * Created by pancl on 15-5-7.
 */
public class UserPrivilegesModel extends BaseDataModel {

    protected List<ModelItem> itemList;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem implements Parcelable {

        public static final CharSequence NEED_NOTICE = "1";

        private boolean opened;//是否开通
        private String cStatus;//状态(0-无效，1-有效)
        private String sDesc;//描述
        private String dUpdate;//更新时间
        private String dCreate;//生成时间
        private int iPlatformId;//1通用 其余参考平台定义
        private String cType;//特权类型(0-免卡;1-免流量)
        private String cConfig;//标准配置格式{voucher:[{"id":"1000"},{"id":"1001"}]}
        private int iPrivilegeId;//特权ID
        private String cRefId;//关联ID
        private String sPrivilegeName;//特权名称
        private String cGrayIcon;//未点亮图标
        private String cLightIcon;//已点亮图标
        private String cGrayHint;//未点亮提示
        private String cLightHint;//已点亮提示
        private String cNotice;//特权是否提示(0-不需，1-需要)
        private String cNoticeInfo;//通知信息{"type": 12,"url":"","pageId":"","pageContent":"","imgUrl":""}
        private String cOpenInfo;//开通信息{"type": 12,"url":"","pageId":""}

        @JSONField(name = "opened")
        public boolean isOpened() {
            return opened;
        }

        @JSONField(name = "opened")
        public void setOpened(boolean opened) {
            this.opened = opened;
        }

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

        @JSONField(name = "iPlatformId")
        public int getiPlatformId() {
            return iPlatformId;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "cType")
        public String getcType() {
            return cType;
        }

        @JSONField(name = "cType")
        public void setcType(String cType) {
            this.cType = cType;
        }

        @JSONField(name = "cConfig")
        public String getcConfig() {
            return cConfig;
        }

        @JSONField(name = "cConfig")
        public void setcConfig(String cConfig) {
            this.cConfig = cConfig;
        }

        @JSONField(name = "iPrivilegeId")
        public int getiPrivilegeId() {
            return iPrivilegeId;
        }

        @JSONField(name = "iPrivilegeId")
        public void setiPrivilegeId(int iPrivilegeId) {
            this.iPrivilegeId = iPrivilegeId;
        }

        @JSONField(name = "cRefId")
        public String getcRefId() {
            return cRefId;
        }

        @JSONField(name = "cRefId")
        public void setcRefId(String cRefId) {
            this.cRefId = cRefId;
        }

        @JSONField(name = "sPrivilegeName")
        public String getsPrivilegeName() {
            return sPrivilegeName;
        }

        @JSONField(name = "sPrivilegeName")
        public void setsPrivilegeName(String sPrivilegeName) {
            this.sPrivilegeName = sPrivilegeName;
        }

        @JSONField(name = "cGrayIcon")
        public String getcGrayIcon() {
            return cGrayIcon;
        }

        @JSONField(name = "cGrayIcon")
        public void setcGrayIcon(String cGrayIcon) {
            this.cGrayIcon = cGrayIcon;
        }

        @JSONField(name = "cLightIcon")
        public String getcLightIcon() {
            return cLightIcon;
        }

        @JSONField(name = "cLightIcon")
        public void setcLightIcon(String cLightIcon) {
            this.cLightIcon = cLightIcon;
        }

        @JSONField(name = "cGrayHint")
        public String getcGrayHint() {
            return cGrayHint;
        }

        @JSONField(name = "cGrayHint")
        public void setcGrayHint(String cGrayHint) {
            this.cGrayHint = cGrayHint;
        }

        @JSONField(name = "cLightHint")
        public String getcLightHint() {
            return cLightHint;
        }

        @JSONField(name = "cLightHint")
        public void setcLightHint(String cLightHint) {
            this.cLightHint = cLightHint;
        }

        @JSONField(name = "cNotice")
        public String getcNotice() {
            return cNotice;
        }

        @JSONField(name = "cNotice")
        public void setcNotice(String cNotice) {
            this.cNotice = cNotice;
        }

        @JSONField(name = "cNoticeInfo")
        public String getcNoticeInfo() {
            return cNoticeInfo;
        }

        @JSONField(name = "cNoticeInfo")
        public void setcNoticeInfo(String cNoticeInfo) {
            this.cNoticeInfo = cNoticeInfo;
        }

        @JSONField(name = "cOpenInfo")
        public String getcOpenInfo() {
            return cOpenInfo;
        }

        @JSONField(name = "cOpenInfo")
        public void setcOpenInfo(String cOpenInfo) {
            this.cOpenInfo = cOpenInfo;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(opened ? (byte) 1 : (byte) 0);
            dest.writeString(this.cStatus);
            dest.writeString(this.sDesc);
            dest.writeString(this.dUpdate);
            dest.writeString(this.dCreate);
            dest.writeInt(this.iPlatformId);
            dest.writeString(this.cType);
            dest.writeString(this.cConfig);
            dest.writeInt(this.iPrivilegeId);
            dest.writeString(this.cRefId);
            dest.writeString(this.sPrivilegeName);
            dest.writeString(this.cGrayIcon);
            dest.writeString(this.cLightIcon);
            dest.writeString(this.cGrayHint);
            dest.writeString(this.cLightHint);
            dest.writeString(this.cNotice);
            dest.writeString(this.cNoticeInfo);
            dest.writeString(this.cOpenInfo);
        }

        public ModelItem() {
        }

        private ModelItem(Parcel in) {
            this.opened = in.readByte() != 0;
            this.cStatus = in.readString();
            this.sDesc = in.readString();
            this.dUpdate = in.readString();
            this.dCreate = in.readString();
            this.iPlatformId = in.readInt();
            this.cType = in.readString();
            this.cConfig = in.readString();
            this.iPrivilegeId = in.readInt();
            this.cRefId = in.readString();
            this.sPrivilegeName = in.readString();
            this.cGrayIcon = in.readString();
            this.cLightIcon = in.readString();
            this.cGrayHint = in.readString();
            this.cLightHint = in.readString();
            this.cNotice = in.readString();
            this.cNoticeInfo = in.readString();
            this.cOpenInfo = in.readString();
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
