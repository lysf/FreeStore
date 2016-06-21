package com.snailgame.cjg.desktop.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sunxy on 2014/9/2.
 */
public class InstallGameInfo implements Parcelable {
    public String appName = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Bitmap appIcon = null;
    public boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(appIcon!=null)
            appIcon.writeToParcel(dest, 0);
        dest.writeInt(versionCode);
        dest.writeString(versionName);

        dest.writeString(packageName);
        dest.writeString(appName);


    }

    public static final Creator<InstallGameInfo> CREATOR = new Creator<InstallGameInfo>() {
        @Override
        public InstallGameInfo createFromParcel(Parcel source) {
            InstallGameInfo installGameInfo = new InstallGameInfo();
            installGameInfo.setAppIcon(Bitmap.CREATOR.createFromParcel(source));
            installGameInfo.setVersionCode(source.readInt());
            installGameInfo.setVersionName(source.readString());
            installGameInfo.setPackageName(source.readString());
            installGameInfo.setAppName(source.readString());
            return installGameInfo;
        }

        @Override
        public InstallGameInfo[] newArray(int size) {
            return new InstallGameInfo[size];
        }
    };

}
