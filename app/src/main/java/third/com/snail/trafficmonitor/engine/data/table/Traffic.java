package third.com.snail.trafficmonitor.engine.data.table;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by kevin on 14-9-23.
 * <p/>
 * Traffics
 */
@DatabaseTable(tableName = "traffics")
public class Traffic {
    private final String TAG = Traffic.class.getSimpleName();

    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_START_TIMESTAMP = "start_timestamp";
    public final static String COLUMN_END_TIMESTAMP = "end_timestamp";
    public final static String COLUMN_UPLOAD_BYTES = "upload_bytes";
    public final static String COLUMN_DOWNLOAD_BYTES = "download_bytes";
    public final static String COLUMN_APP_ID = "app_id";
    public final static String COLUMN_NETWORK_ID = "network_id";

    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUMN_START_TIMESTAMP)
    long startTimestamp;
    @DatabaseField(columnName = COLUMN_END_TIMESTAMP)
    long endTimestamp;
    @DatabaseField(columnName = COLUMN_UPLOAD_BYTES)
    private long uploadBytes;
    @DatabaseField(columnName = COLUMN_DOWNLOAD_BYTES)
    private long downloadBytes;
    @DatabaseField(columnName = COLUMN_APP_ID, index = true)
    private int appId;
    @DatabaseField(columnName = COLUMN_NETWORK_ID, index = true)
    private int networkId;
    private Network network;
    private App app;

    public Traffic() {
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getUploadBytes() {
        return uploadBytes;
    }

    public void setUploadBytes(long uploadBytes) {
        this.uploadBytes = uploadBytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getDownloadBytes() {
        return downloadBytes;
    }

    public void setDownloadBytes(long downloadBytes) {
        this.downloadBytes = downloadBytes;
    }

    public Traffic update(Traffic other) {
        setDownloadBytes(getDownloadBytes() + other.getDownloadBytes());
        setUploadBytes(getUploadBytes() + other.getUploadBytes());
        setEndTimestamp(other.getEndTimestamp());
        //没有升级NetworkID，目前的需求是只需要区分 Mobile/WIFI，所以当网络类型相同时无需升级NetworkID
        return this;
    }

    @Override
    public String toString() {
        return "Traffic{" +
                "TAG='" + TAG + '\'' +
                ", id=" + id +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", uploadBytes=" + uploadBytes +
                ", downloadBytes=" + downloadBytes +
                ", appId=" + appId +
                ", networkId=" + networkId +
                '}';
    }
}
