package third.com.snail.trafficmonitor.engine.data.bean;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by kevin on 14-9-24.
 * <p/>
 * Record traffic info for every application.
 */
public class AppTraffic implements Comparable<AppTraffic> {
    private final String TAG = AppTraffic.class.getSimpleName();

    public int appId;
    public int uid;
    public String packageName;
    public String appName;
    public long txBytes;
    public long rxBytes;
    public Drawable icon;
    public long wifiTxBytes;
    public long wifiRxBytes;
    /**
    * 当前app在当天所有时段的使用流量列表
    * */
    public List<TimeInfo> trafficInDayList;

    public void updateCost(@NonNull AppTraffic another) {
        txBytes += another.txBytes;
        rxBytes += another.rxBytes;
        wifiTxBytes += another.wifiTxBytes;
        wifiRxBytes += another.wifiRxBytes;
    }

    @Override
    public String toString() {
        return "AppTraffic{" +
                "uid=" + uid +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", txBytes=" + txBytes +
                ", rxBytes=" + rxBytes +
                ", icon=" + icon +
                ", wifiTxBytes=" + wifiTxBytes +
                ", wifiRxBytes=" + wifiRxBytes +
                '}';
    }

    @Override
    public int compareTo(@NonNull AppTraffic another) {
        long myTotal = rxBytes + txBytes;
        long anotherTotal = another.rxBytes + another.txBytes;
        if (anotherTotal - myTotal > 0) {
            return 1;
        } else if (anotherTotal - myTotal < 0) {
            return -1;
        }
        return 0;
    }
}
