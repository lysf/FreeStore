package third.com.snail.trafficmonitor.engine.data.table;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by kevin on 14-10-8.
 * <p/>
 * Record all apps' detail in last timestamp
 */
@DatabaseTable(tableName = "record")
public class Record implements Parcelable {

    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_APP_ID = "app_id";
    public final static String COLUMN_NETWORK_ID = "network_id";
    public final static String COLUMN_TIMESTAMP = "timestamp";
    public final static String COLUMN_RX_BYTES = "rx_bytes";
    public final static String COLUMN_TX_BYTES = "tx_bytes";

    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUMN_APP_ID, index = true)
    private int appId;
    private App app;
    @DatabaseField(columnName = COLUMN_NETWORK_ID, index = true)
    private int networkId;
    private Network network;
    @DatabaseField(columnName = COLUMN_TIMESTAMP)
    private long timestamp;
    @DatabaseField(columnName = COLUMN_RX_BYTES)
    private long rxBytes;
    @DatabaseField(columnName = COLUMN_TX_BYTES)
    private long txBytes;

    public Record() {
    }


    public Record update(Record another) {
        network = another.network;
        timestamp = another.timestamp;
        rxBytes = another.rxBytes;
        txBytes = another.txBytes;
        return this;
    }

    public int getAppId() {
        return appId;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
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
        return "Record{" +
                "id=" + id +
                ", app=" + app +
                ", network=" + network +
                ", timestamp=" + timestamp +
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
        dest.writeInt(this.id);
        dest.writeParcelable(this.app, 0);
        dest.writeParcelable(this.network, 0);
        dest.writeLong(this.timestamp);
        dest.writeLong(this.rxBytes);
        dest.writeLong(this.txBytes);
    }

    private Record(Parcel in) {
        this.id = in.readInt();
        this.app = in.readParcelable(App.class.getClassLoader());
        this.network = in.readParcelable(Network.class.getClassLoader());
        this.timestamp = in.readLong();
        this.rxBytes = in.readLong();
        this.txBytes = in.readLong();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
