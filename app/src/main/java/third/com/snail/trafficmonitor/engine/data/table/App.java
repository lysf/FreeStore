package third.com.snail.trafficmonitor.engine.data.table;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by kevin on 14-9-23.
 * <p/>
 * App table
 */
@DatabaseTable(tableName = "apps")
public class App implements Parcelable {
    private final String TAG = App.class.getSimpleName();
    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_UID = "uid";
    public final static String COLUMN_PACKAGE_NAME = "package_name";
    public final static String COLUMN_APP_NAME = "app_name";
    public final static String COLUMN_VERSION_CODE = "version_code";
    public final static String COLUMN_VERSION_NAME = "version_name";
    public final static String COLUMN_MONITORING = "monitoring";
    // liukai added @20141124 begin
    public final static String COLUMN_DISPLAY = "display"; //boolean
    public final static String COLUMN_ACCESS_WIFI = "access_wifi"; //boolean
    public final static String COLUMN_ACCESS_MOBILE = "access_mobile"; //boolean
    // liukai added @20141124 end
    /**
     * Master key is "uid", it's the only value.
     */
    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private int id;
    @DatabaseField
    private int uid;
    @DatabaseField(columnName = COLUMN_PACKAGE_NAME, canBeNull = false)
    private String packageName;
    @DatabaseField(columnName = COLUMN_APP_NAME, canBeNull = false)
    private String appName;
    @DatabaseField(columnName = COLUMN_VERSION_CODE)
    private int versionCode;
    @DatabaseField(columnName = COLUMN_VERSION_NAME)
    private String versionName;
    @DatabaseField(columnName = COLUMN_MONITORING, canBeNull = false)
    private boolean monitoring;
    // liukai added @20141124 begin
    @DatabaseField(columnName = COLUMN_DISPLAY)
    private boolean display;
    @DatabaseField(columnName = COLUMN_ACCESS_WIFI)
    private boolean wifiAccess;
    @DatabaseField(columnName = COLUMN_ACCESS_MOBILE)
    private boolean mobileAccess;
    // liukai added @20141124 end

    public App() {}

    public static String getMasterKey() {
        return COLUMN_PACKAGE_NAME;
    }

    public App(int uid, String packageName, String appName) {
        this.uid = uid;
        this.packageName = packageName;
        this.appName = appName;
    }

    public void update(App other, boolean isAdd) {
        if (isAdd) {
            uid = other.getUid();
            packageName = other.getPackageName();
            appName = other.getAppName();
            versionCode = other.getVersionCode();
            versionName = other.getVersionName();
            monitoring = other.monitoring;
            display = other.display;
            wifiAccess = other.wifiAccess;
            mobileAccess = other.mobileAccess;
        } else {
            if (!appName.contains(other.getAppName())) {
                appName = appName + other.getAppName();
            }
            monitoring = false;
        }
        LogWrapper.d(TAG, toString() + ":" + isAdd);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(@NonNull String name) {
        this.packageName = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(@NonNull String name) {
        this.appName = name;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int code) {
        this.versionCode = code;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String name) {
        this.versionName = name;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean b) {
        monitoring = b;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isWifiAccess() {
        return wifiAccess;
    }

    public void setWifiAccess(boolean wifiAccess) {
        this.wifiAccess = wifiAccess;
    }

    public boolean isMobileAccess() {
        return mobileAccess;
    }

    public void setMobileAccess(boolean mobileAccess) {
        this.mobileAccess = mobileAccess;
    }

    @Override
    public String toString() {
        return "App{" +
                "TAG='" + TAG + '\'' +
                ", id=" + id +
                ", uid=" + uid +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", monitoring=" + monitoring +
                ", display=" + display +
                ", wifiAccess=" + wifiAccess +
                ", mobileAccess=" + mobileAccess +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.uid);
        dest.writeString(this.packageName);
        dest.writeString(this.appName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeByte(monitoring ? (byte) 1 : (byte) 0);
        dest.writeByte(display ? (byte) 1 : (byte) 0);
        dest.writeByte(wifiAccess ? (byte) 1 : (byte) 0);
        dest.writeByte(mobileAccess ? (byte) 1 : (byte) 0);
    }

    private App(Parcel in) {
        this.id = in.readInt();
        this.uid = in.readInt();
        this.packageName = in.readString();
        this.appName = in.readString();
        this.versionCode = in.readInt();
        this.versionName = in.readString();
        this.monitoring = in.readByte() != 0;
        this.display = in.readByte() != 0;
        this.wifiAccess = in.readByte() != 0;
        this.mobileAccess = in.readByte() != 0;
    }

    public static final Creator<App> CREATOR = new Creator<App>() {
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };
}
