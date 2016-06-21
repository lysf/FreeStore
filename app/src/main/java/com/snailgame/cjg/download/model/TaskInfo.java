package com.snailgame.cjg.download.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * 下载信息抽象类
 */
@Getter
@Setter
public class TaskInfo implements Parcelable {

    private long taskId;
    private String apkUri;
    //private String iconUri;
    private String desc;
    private String apkLabel;
    private long apkTotalSize;
    private double taskPercent;
    private long downloadedSize;
    private int downloadState;
    private String apkLocalUri;
    //    private long lastUpdateTime;
    private String downloadHint;
    private int reason;

    private int appId;
    private String appLabel;
    private String appPkgName;
    private String appIconUrl;
    private String appVersionName;
    private int appVersionCode;
    private long bytesInWifi;
    private long bytesIn3G;
    private String flowFreeState;
    private String md5;
    private String appType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.taskId);
        dest.writeString(this.apkUri);
        dest.writeString(this.desc);
        dest.writeString(this.apkLabel);
        dest.writeLong(this.apkTotalSize);
        dest.writeDouble(this.taskPercent);
        dest.writeLong(this.downloadedSize);
        dest.writeInt(this.downloadState);
        dest.writeString(this.apkLocalUri);
        dest.writeString(this.downloadHint);
        dest.writeInt(this.reason);
        dest.writeInt(this.appId);
        dest.writeString(this.appLabel);
        dest.writeString(this.appPkgName);
        dest.writeString(this.appIconUrl);
        dest.writeString(this.appVersionName);
        dest.writeInt(this.appVersionCode);
        dest.writeLong(this.bytesInWifi);
        dest.writeLong(this.bytesIn3G);
        dest.writeString(this.flowFreeState);
        dest.writeString(this.md5);
        dest.writeString(this.appType);
    }

    public TaskInfo() {
    }

    private TaskInfo(Parcel in) {
        this.taskId = in.readLong();
        this.apkUri = in.readString();
        this.desc = in.readString();
        this.apkLabel = in.readString();
        this.apkTotalSize = in.readLong();
        this.taskPercent = in.readDouble();
        this.downloadedSize = in.readLong();
        this.downloadState = in.readInt();
        this.apkLocalUri = in.readString();
        this.downloadHint = in.readString();
        this.reason = in.readInt();
        this.appId = in.readInt();
        this.appLabel = in.readString();
        this.appPkgName = in.readString();
        this.appIconUrl = in.readString();
        this.appVersionName = in.readString();
        this.appVersionCode = in.readInt();
        this.bytesInWifi = in.readLong();
        this.bytesIn3G = in.readLong();
        this.flowFreeState = in.readString();
        this.md5 = in.readString();
        this.appType = in.readString();
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        public TaskInfo createFromParcel(Parcel source) {
            return new TaskInfo(source);
        }

        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };
}
