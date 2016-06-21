package com.snailgame.cjg.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.snailgame.cjg.download.DownloadHelper;

import lombok.Getter;
import lombok.Setter;


/**
 * 用户应用类
 */
@Getter
@Setter
public class AppInfo extends BaseAppInfo implements Parcelable {
    private String[] appPermissionArray = {};//应用拥有的全部权限
    private long lastUpdateTime;//最后安装时间
    private long lastUploadTime;//应用最近一次更新版本的时间

    private String downloadCount;//应用被下载次数
    private int iPrice;//应用价格
    private int level;//应用评分等级

    private String downloadSpeed = DownloadHelper.DEFAULT_DOWNLOAD_SPEED;//下载速度
    private long downloadedSize;//已下载大小
    private double downloadedPercent;//已下载进度
    private int downloadState;//应用下载状态
    private long downloadTotalSize;//下载文件的大小

    private boolean isChecked = false;//当前应用是否被选中

    private String localAppVersion;//本地应用版本号，如：1.0.1
    private int localAppVersionCode;//本地应用版本码，如：3

    private long apkDownloadId;//应用在下载表的ID
    private String localUri;//APK本地位置
    private String downloadHint;//下载文件uri地址
    private long dataSize; //本地大小
    private int from;//应用来源
    private int isUpdate;
    private int isPatch; //0 无差分 1 可差分

    protected String diffUrl;
    protected long diffSize;
    protected String diffMd5;

    private String originCFlowFree;
    private String installedApkVersionName;
    private String cGameStatus;
    private boolean isDownloading;
    private int[] route;

    //用于区别是游戏还是应用 11来源应用 3 9来源游戏 20来源弹窗
    private int cMainType;

    private int upgradeIgnoreCode; //忽略更新的 最低版本
    private String upgradeDesc;
    private boolean isShowExpand = false; //升级信息内容是否展开
    private boolean isDownloadPatch = false; //是否正在下载Patch包
    private boolean isInDownloadDB = false; //是否在下载表中
    //排行专用
    private float commonLevel; //软件评分
    private String rssId; // itunes应用ID

    private String cIconLabel;  // 图标角标

    private int totalIntsallNum; // 总得安装量

    private boolean hasAppointment = false;  //是否已预约

    private String appointmentStatus;      //这个字段表示 1标识显示预约， 0-下载


    private String testingStatus;// 公测状态
    private String delTestTime; //删除测试数据时间

    public String getRssId() {
        return rssId;
    }

    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public float getCommonLevel() {
        return commonLevel;
    }

    public void setCommonLevel(float commonLevel) {
        this.commonLevel = commonLevel;
    }

    @Override
    public int getcMainType() {
        return cMainType;
    }

    public AppInfo() {
        //do nothing
    }


    public AppInfo(BaseAppInfo info) {
        appId = info.getAppId();
        appName = info.getAppName();
        icon = info.getIcon();
        apkUrl = info.getApkUrl();
        apkSize = info.getApkSize();
        versionName = info.getVersionName();
        versionCode = info.getVersionCode();
        pkgName = info.getPkgName();
        cFlowFree = info.getcFlowFree();
        md5 = info.getMd5();
        sAppDesc = info.getsAppDesc();
        sInfo = info.getsInfo();
        cType = info.getcType();
        cPicUrl = info.getcPicUrl();
        cMark = info.getcMark();
        iRefId = info.getiRefId();
        cHtmlUrl = info.getcHtmlUrl();
        iCategoryId = info.getiCategoryId();
        cMainType = info.getcMainType();
        cAppType = info.getcAppType();
        iFreeArea = info.getiFreeArea();
        cIconLabel = info.getcIconLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        AppInfo appInfo = (AppInfo) o;
        if (versionCode != appInfo.versionCode) return false;
        return apkUrl != null ? apkUrl.equals(appInfo.apkUrl) : appInfo.apkUrl == null;
    }

    @Override
    public int hashCode() {
        int result = versionName != null ? versionName.hashCode() : 0;
        result = 31 * result + versionCode;
        result = 31 * result + (apkUrl != null ? apkUrl.hashCode() : 0);
        return result;
    }


    /**
     * FreeState "1111"
     * 1 : YES 0:NO
     * DOWNLOAD/PLAY/UPGRADE/FREEAREA
     */
    public static final String FREE_DOWNLOAD_PLAY_UPGRADE = "1110";
    public static final String FREE_DOWNLOAD_PLAY_UPGRADE_AREA = "1111";
    public static final String FREE_DOWNLOAD_PLAY = "1100";
    public static final String FREE_DOWNLOAD_PLAY_AREA = "1101";
    public static final String FREE_DOWNLOAD_UPGRADE = "1010";
    public static final String FREE_DOWNLOAD_UPGRADE_AREA = "1011";
    public static final String FREE_DOWNLOAD = "1000";
    public static final String FREE_DOWNLOAD_AREA = "1001";
    public static final String FREE_PLAY_UPGRADE = "0110";
    public static final String FREE_PLAY_UPGRADE_AREA = "0111";
    public static final String FREE_UPGRADE = "0010";
    public static final String FREE_UPGRADE_AREA = "0011";
    public static final String FREE_PLAY = "0100";
    public static final String FREE_PLAY_AREA = "0101";
    public static final String FREE_NULL = "0000";
    public static final String FREE_AREA = "0001";

    public static boolean isFree(String freeFlow) {
        if (freeFlow == null)
            return false;

        if (freeFlow.length() < 4)
            return false;

        if (freeFlow.length() > 4)
            freeFlow = freeFlow.substring(0, 4);

        switch (freeFlow) {
            case FREE_DOWNLOAD_PLAY_UPGRADE:
            case FREE_DOWNLOAD_PLAY_UPGRADE_AREA:
            case FREE_DOWNLOAD_PLAY:
            case FREE_DOWNLOAD_PLAY_AREA:
            case FREE_DOWNLOAD_UPGRADE:
            case FREE_DOWNLOAD_UPGRADE_AREA:
            case FREE_DOWNLOAD:
            case FREE_DOWNLOAD_AREA:
            case FREE_PLAY_UPGRADE:
            case FREE_PLAY_UPGRADE_AREA:
            case FREE_UPGRADE:
            case FREE_UPGRADE_AREA:
            case FREE_PLAY:
            case FREE_PLAY_AREA:
                return true;
            case FREE_AREA:
            case FREE_NULL:
            default:
                return false;
        }
    }

    public static boolean isDownloadFree(String freeFlow) {
        if (freeFlow == null)
            return false;

        if (freeFlow.length() < 4)
            return false;

        if (freeFlow.length() > 4)
            freeFlow = freeFlow.substring(0, 4);

        switch (freeFlow) {
            case FREE_DOWNLOAD_PLAY_UPGRADE:
            case FREE_DOWNLOAD_PLAY_UPGRADE_AREA:
            case FREE_DOWNLOAD_PLAY:
            case FREE_DOWNLOAD_PLAY_AREA:
            case FREE_DOWNLOAD_UPGRADE:
            case FREE_DOWNLOAD_UPGRADE_AREA:
            case FREE_DOWNLOAD:
            case FREE_DOWNLOAD_AREA:
                return true;
            case FREE_PLAY_UPGRADE:
            case FREE_PLAY_UPGRADE_AREA:
            case FREE_UPGRADE:
            case FREE_UPGRADE_AREA:
            case FREE_PLAY:
            case FREE_PLAY_AREA:
            case FREE_AREA:
            case FREE_NULL:
            default:
                return false;
        }
    }

    public static boolean isFreeArea(String freeFlow) {
        if (freeFlow == null)
            return false;

        if (freeFlow.length() != 4)
            return false;

        switch (freeFlow) {
            case FREE_DOWNLOAD_PLAY_UPGRADE_AREA:
            case FREE_DOWNLOAD_PLAY_AREA:
            case FREE_DOWNLOAD_UPGRADE_AREA:
            case FREE_DOWNLOAD_AREA:
                return true;
            case FREE_DOWNLOAD_PLAY_UPGRADE:
            case FREE_DOWNLOAD_PLAY:
            case FREE_DOWNLOAD_UPGRADE:
            case FREE_DOWNLOAD:
            case FREE_PLAY_UPGRADE:
            case FREE_PLAY_UPGRADE_AREA:
            case FREE_UPGRADE:
            case FREE_UPGRADE_AREA:
            case FREE_PLAY:
            case FREE_PLAY_AREA:
            case FREE_AREA:
            case FREE_NULL:
            default:
                return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.appPermissionArray);
        dest.writeLong(this.lastUpdateTime);
        dest.writeLong(this.lastUploadTime);
        dest.writeString(this.downloadCount);
        dest.writeInt(this.iPrice);
        dest.writeInt(this.level);
        dest.writeString(this.downloadSpeed);
        dest.writeLong(this.downloadedSize);
        dest.writeDouble(this.downloadedPercent);
        dest.writeInt(this.downloadState);
        dest.writeLong(this.downloadTotalSize);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.localAppVersion);
        dest.writeInt(this.localAppVersionCode);
        dest.writeLong(this.apkDownloadId);
        dest.writeString(this.localUri);
        dest.writeString(this.downloadHint);
        dest.writeLong(this.dataSize);
        dest.writeInt(this.from);
        dest.writeInt(this.isUpdate);
        dest.writeInt(this.isPatch);
        dest.writeString(this.diffUrl);
        dest.writeLong(this.diffSize);
        dest.writeString(this.diffMd5);
        dest.writeString(this.originCFlowFree);
        dest.writeString(this.installedApkVersionName);
        dest.writeString(this.cGameStatus);
        dest.writeByte(isDownloading ? (byte) 1 : (byte) 0);
        dest.writeInt(this.appId);
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
        dest.writeString(this.sInfo);
        dest.writeString(this.cType);
        dest.writeString(this.cPicUrl);
        dest.writeString(this.cMark);
        dest.writeInt(this.iRefId);
        dest.writeString(this.cHtmlUrl);
        dest.writeInt(this.iCategoryId);
        dest.writeIntArray(this.route);
        dest.writeString(this.cAppType);
        dest.writeInt(this.iFreeArea);
        dest.writeString(this.upgradeDesc);
        dest.writeInt(this.upgradeIgnoreCode);
        dest.writeString(this.cIconLabel);
        dest.writeInt(this.totalIntsallNum);
        dest.writeByte(hasAppointment ? (byte) 1 : (byte) 0);
        dest.writeString(appointmentStatus);
        dest.writeString(testingStatus);
        dest.writeString(delTestTime);
    }

    private AppInfo(Parcel in) {
        this.appPermissionArray = in.createStringArray();
        this.lastUpdateTime = in.readLong();
        this.lastUploadTime = in.readLong();
        this.downloadCount = in.readString();
        this.iPrice = in.readInt();
        this.level = in.readInt();
        this.downloadSpeed = in.readString();
        this.downloadedSize = in.readLong();
        this.downloadedPercent = in.readDouble();
        this.downloadState = in.readInt();
        this.downloadTotalSize = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.localAppVersion = in.readString();
        this.localAppVersionCode = in.readInt();
        this.apkDownloadId = in.readLong();
        this.localUri = in.readString();
        this.downloadHint = in.readString();
        this.dataSize = in.readLong();
        this.from = in.readInt();
        this.isUpdate = in.readInt();
        this.isPatch = in.readInt();
        this.diffUrl = in.readString();
        this.diffSize = in.readLong();
        this.diffMd5 = in.readString();
        this.originCFlowFree = in.readString();
        this.installedApkVersionName = in.readString();
        this.cGameStatus = in.readString();
        this.isDownloading = in.readByte() != 0;
        this.appId = in.readInt();
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
        this.sInfo = in.readString();
        this.cType = in.readString();
        this.cPicUrl = in.readString();
        this.cMark = in.readString();
        this.iRefId = in.readInt();
        this.cHtmlUrl = in.readString();
        this.iCategoryId = in.readInt();
        this.route = in.createIntArray();
        this.cAppType = in.readString();
        this.iFreeArea = in.readInt();
        this.upgradeDesc = in.readString();
        this.upgradeIgnoreCode = in.readInt();
        this.cIconLabel = in.readString();
        this.totalIntsallNum = in.readInt();
        this.hasAppointment = in.readByte() != 0;
        this.appointmentStatus = in.readString();
        this.testingStatus = in.readString();
        this.delTestTime = in.readString();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}

