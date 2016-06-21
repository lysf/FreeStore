package third.com.snail.trafficmonitor.engine.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by licong
 */
public class FileTrafficObject implements Parcelable {
    private final String TAG = FileTrafficObject.class.getSimpleName();
    private String iface;
    public String uid;
    public long txBytes;
    public long rxBytes;
    public long foregroundBytes;
    public long backgroundBytes;

    public FileTrafficObject() {}
    public String getUid() {
        return uid;
    }

    public String getIface() {
        return iface;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public long getForegroundBytes() {
        return foregroundBytes;
    }

    public long getBackgroundBytes() {
        return backgroundBytes;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public void setForegroundBytes(long foregroundBytes) {
        this.foregroundBytes = foregroundBytes;
    }

    public void setBackgroundBytes(long backgroundBytes) {
        this.backgroundBytes = backgroundBytes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "RootTrafficObject{" +
                "iface='" + iface + '\'' +
                ", uid=" + uid +
                ", txBytes=" + txBytes +
                ", rxBytes=" + rxBytes +
                ", foregroundBytes=" + foregroundBytes +
                ", backgroundBytes=" + backgroundBytes +
                '}';
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.iface);
        parcel.writeString(this.uid);
        parcel.writeLong(this.txBytes);
        parcel.writeLong(this.rxBytes);
        parcel.writeLong(this.foregroundBytes);
        parcel.writeLong(this.backgroundBytes);

    }
    public static final Creator<FileTrafficObject> CREATOR = new Creator<FileTrafficObject>() {
        public FileTrafficObject createFromParcel(Parcel in) {
            return new FileTrafficObject(in);
        }

        public FileTrafficObject[] newArray(int size) {
            return new FileTrafficObject[size];
        }
    };

    private FileTrafficObject(Parcel in) {
        this.iface = in.readString();
        this.uid = in.readString();
        this.txBytes = in.readLong();
        this.rxBytes = in.readLong();
        this.foregroundBytes = in.readLong();
        this.backgroundBytes = in.readLong();
}

}
