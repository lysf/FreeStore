package third.com.snail.trafficmonitor.engine.data.table;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kevin on 14-9-23.
 * <p/>
 * Network
 */
@DatabaseTable(tableName = "networks")
public class Network implements Parcelable {
    private final String TAG = Network.class.getSimpleName();
    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_NETWORK_NAME = "network_name";
    public final static String COLUMN_NETWORK_TYPE = "network_type";
    public final static String COLUMN_WIFI_SSID = "wifi_ssid";
    public final static String COLUMN_WIFI_BSSID = "wifi_bssid";
    public final static String COLUMN_IFACE = "iface";

    /**
     * Master key is "network_name" if "network_type" is WIFI,
     * otherwise MASTER key is "wifi_bssid".
     */
    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUMN_NETWORK_NAME)
    private String networkName;
    @DatabaseField(columnName = COLUMN_NETWORK_TYPE, canBeNull = false)
    private String networkType;
    @DatabaseField(columnName = COLUMN_WIFI_SSID)
    private String wifiSSID;
    @DatabaseField(columnName = COLUMN_WIFI_BSSID)
    private String wifiBSSID;
    @DatabaseField
    private String iface;

    public static final String NETWORK_TYPE = "network_type";

    public enum NetworkType {
        WIFI,
        MOBILE_2G,
        MOBILE_3G,
        MOBILE_4G,
        OTHER,
        NONE,
    }

    public static String typeToString(NetworkType type) {
        switch (type) {
            case WIFI:
                return "wifi";
            case MOBILE_2G:
                return "2g";
            case MOBILE_3G:
                return "3g";
            case MOBILE_4G:
                return "4g";
            case OTHER:
                return "other";
            case NONE:
                return "none";
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public static NetworkType stringToType(String string) {
        if (string.equals("wifi")) {
            return NetworkType.WIFI;
        } else if (string.equals("2g")) {
            return NetworkType.MOBILE_2G;
        } else if (string.equals("3g")) {
            return NetworkType.MOBILE_3G;
        } else if (string.equals("4g")) {
            return NetworkType.MOBILE_4G;
        } else if (string.equals("other")) {
            return NetworkType.OTHER;
        } else if (string.equals("none")) {
            return NetworkType.NONE;
        } else {
            throw new IllegalArgumentException("Unknown type string: " + string);
        }
    }

    public Network() {
    }

    public Network(String networkName, NetworkType type) {
        this.networkName = networkName;
        this.networkType = typeToString(type);
    }

    public static String getMasterKey(boolean wifi) {
        if (wifi) {
            return "wifi_bssid";
        } else {
            return "network_name";
        }
    }

    public void update(Network network) {
        networkName = network.getNetworkName();
        networkType = typeToString(network.getNetworkType());
        wifiSSID = network.getWifiSSID();
        wifiBSSID = network.getWifiBSSID();
        iface = network.getIface();
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Network)) {
            return false;
        }
        Network data = (Network) another;
        if (data.id != 0 && id != 0 && data.id == id) {
            return true;
        }
        if (data.networkType.equals(networkType)) {
            if (stringToType(data.networkType) == NetworkType.NONE) {
                return true;
            } else if (data.getNetworkType() == NetworkType.WIFI) {
                return data.getWifiBSSID().equals(getWifiBSSID());
            } else {
                return data.getNetworkName().equals(getNetworkName())
                        && data.getNetworkType() == getNetworkType();
            }
        } else {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public NetworkType getNetworkType() {
        return stringToType(networkType);
    }

    public void setNetworkType(@NonNull NetworkType type) {
        this.networkType = typeToString(type);
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public String getWifiBSSID() {
        return wifiBSSID;
    }

    public void setWifiBSSID(String bssid) {
        wifiBSSID = bssid;
    }

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    @Override
    public String toString() {
        return "Network{" +
                "TAG='" + TAG + '\'' +
                ", networkName='" + networkName + '\'' +
                ", networkType='" + networkType + '\'' +
                ", wifiSSID='" + wifiSSID + '\'' +
                ", wifiBSSID='" + wifiBSSID + '\'' +
                ", iface='" + iface + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.networkName);
        dest.writeString(this.networkType);
        dest.writeString(this.wifiSSID);
        dest.writeString(this.wifiBSSID);
        dest.writeString(this.iface);
    }

    private Network(Parcel in) {
        this.networkName = in.readString();
        this.networkType = in.readString();
        this.wifiSSID = in.readString();
        this.wifiBSSID = in.readString();
        this.iface = in.readString();
    }

    public static final Creator<Network> CREATOR = new Creator<Network>() {
        public Network createFromParcel(Parcel source) {
            return new Network(source);
        }

        public Network[] newArray(int size) {
            return new Network[size];
        }
    };
}
