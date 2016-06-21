package third.com.snail.trafficmonitor.engine.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by kevin on 14-10-8.
 * <p/>
 * 保存在缓存中的每一个应用的数据结构
 */
public class AppRecordCell implements Parcelable {
    public int appId;
    public long timestamp;
    public long rxBytes;
    public long txBytes;

    public AppRecordCell() {}

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    @Override
    public String toString() {
        return "AppRecordCell{" +
                "appId=" + appId +
                ", timestamp=" + new Date(timestamp).toString() +
                ", rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appId);
        dest.writeLong(this.timestamp);
        dest.writeLong(this.rxBytes);
        dest.writeLong(this.txBytes);
    }

    private AppRecordCell(Parcel in) {
        this.appId = in.readInt();
        this.timestamp = in.readLong();
        this.rxBytes = in.readLong();
        this.txBytes = in.readLong();
    }

    public static final Creator<AppRecordCell> CREATOR = new Creator<AppRecordCell>() {
        public AppRecordCell createFromParcel(Parcel source) {
            return new AppRecordCell(source);
        }

        public AppRecordCell[] newArray(int size) {
            return new AppRecordCell[size];
        }
    };
}
