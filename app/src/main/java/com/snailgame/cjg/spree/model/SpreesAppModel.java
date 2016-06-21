package com.snailgame.cjg.spree.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;
import com.snailgame.cjg.global.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 礼包和app
 * Created by TAJ_C on 2015/5/5.
 */
public class SpreesAppModel extends BaseDataModel implements Parcelable {

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

    public static final class ModelItem implements Parcelable {

        //应用信息
        long appId;
        String appName;
        String icon;
        String apkUrl;
        long apkSize;
        String versionName;
        int versionCode;
        String pkgName;
        String cFlowFree;
        String md5;
        String sAppDesc;
        String cAppType = AppConstants.VALUE_TYPE_GAME;      // 主类型: 1,游戏; 2, 应用; 3, 皮肤

        //礼包列表

        ArrayList<SpreeGiftInfo> spreeGiftInfoList = new ArrayList<>();
        boolean isShowExpand = false; //升级信息内容是否展开

        @JSONField(name = "nAppId")
        public long getAppId() {
            return appId;
        }

        @JSONField(name = "nAppId")
        public void setAppId(long appId) {
            this.appId = appId;
        }

        @JSONField(name = "sAppName")
        public String getAppName() {
            return appName;
        }

        @JSONField(name = "sAppName")
        public void setAppName(String appName) {
            this.appName = appName;
        }

        @JSONField(name = "cIcon")
        public String getIcon() {
            return icon;
        }

        @JSONField(name = "cIcon")
        public void setIcon(String icon) {
            this.icon = icon;
        }

        @JSONField(name = "cDownloadUrl")
        public String getApkUrl() {
            return apkUrl;
        }

        @JSONField(name = "cDownloadUrl")
        public void setApkUrl(String apkUrl) {
            if (apkUrl != null)
                this.apkUrl = apkUrl.trim();
        }

        @JSONField(name = "iSize")
        public long getApkSize() {
            return apkSize;
        }

        @JSONField(name = "iSize")
        public void setApkSize(long apkSize) {
            this.apkSize = apkSize;
        }

        @JSONField(name = "cVersionName")
        public String getVersionName() {
            return versionName;
        }

        @JSONField(name = "cVersionName")
        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        @JSONField(name = "iVersionCode")
        public int getVersionCode() {
            return versionCode;
        }

        @JSONField(name = "iVersionCode")
        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        @JSONField(name = "cPackage")
        public String getPkgName() {
            return pkgName;
        }

        @JSONField(name = "cPackage")
        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }

        @JSONField(name = "cFlowFree")
        public String getcFlowFree() {
            return cFlowFree;
        }

        @JSONField(name = "cFlowFree")
        public void setcFlowFree(String cFlowFree) {
            this.cFlowFree = cFlowFree;
        }


        @JSONField(name = "cMd5")
        public String getMd5() {
            return md5;
        }

        @JSONField(name = "cMd5")
        public void setMd5(String md5) {
            this.md5 = md5;
        }

        @JSONField(name = "sAppDesc")
        public String getsAppDesc() {
            return sAppDesc;
        }

        @JSONField(name = "sAppDesc")
        public void setsAppDesc(String sAppDesc) {
            this.sAppDesc = sAppDesc;
        }

        @JSONField(name = "cAppType")
        public String getcAppType() {
            return cAppType;
        }

        @JSONField(name = "cAppType")
        public void setcAppType(String cAppType) {
            this.cAppType = cAppType;
        }

        @JSONField(name = "vList")
        public ArrayList<SpreeGiftInfo> getSpreeGiftInfoList() {
            return spreeGiftInfoList;
        }

        @JSONField(name = "vList")
        public void setSpreeGiftInfoList(ArrayList<SpreeGiftInfo> spreeGiftInfoList) {
            this.spreeGiftInfoList = spreeGiftInfoList;
        }


        public boolean isShowExpand() {
            return isShowExpand;
        }

        public void setShowExpand(boolean isShowExpand) {
            this.isShowExpand = isShowExpand;
        }

        public ModelItem() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.appId);
            dest.writeString(this.appName);
            dest.writeString(this.icon);
            dest.writeString(this.apkUrl);
            dest.writeLong(this.apkSize);
            dest.writeString(this.versionName);
            dest.writeInt(this.versionCode);
            dest.writeString(this.pkgName);
            dest.writeString(this.cFlowFree);
            dest.writeString(this.md5);
            dest.writeString(this.sAppDesc);
            dest.writeString(this.cAppType);
            dest.writeTypedList(spreeGiftInfoList);
        }

        private ModelItem(Parcel in) {
            this.appId = in.readLong();
            this.appName = in.readString();
            this.icon = in.readString();
            this.apkUrl = in.readString();
            this.apkSize = in.readLong();
            this.versionName = in.readString();
            this.versionCode = in.readInt();
            this.pkgName = in.readString();
            this.cFlowFree = in.readString();
            this.md5 = in.readString();
            this.sAppDesc = in.readString();
            this.cAppType = in.readString();
            in.readTypedList(spreeGiftInfoList, SpreeGiftInfo.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.itemList == null)
            this.itemList = new ArrayList<ModelItem>();

        dest.writeParcelableArray(this.itemList.toArray(new ModelItem[this.itemList.size()]), flags);

        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    public SpreesAppModel() {
    }

    private SpreesAppModel(Parcel in) {
        Parcelable[] list = in.readParcelableArray(ModelItem.class.getClassLoader());
        if (list == null) {
            this.itemList = new ArrayList<ModelItem>();
        } else {
            this.itemList = new ArrayList(Arrays.asList(list));
        }

        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Parcelable.Creator<SpreesAppModel> CREATOR = new Parcelable.Creator<SpreesAppModel>() {
        public SpreesAppModel createFromParcel(Parcel source) {
            return new SpreesAppModel(source);
        }

        public SpreesAppModel[] newArray(int size) {
            return new SpreesAppModel[size];
        }
    };
}
