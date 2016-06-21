package third.com.snail.trafficmonitor.engine.data.bean;

import java.util.List;

/**
 * Created by kevin on 14-10-8.
 * <p/>
 * 保存在缓存中的数据格式Json
 */
public class AppRecord {
    public int networkId;
    public List<AppRecordCell> details;

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public List<AppRecordCell> getDetails() {
        return details;
    }

    public void setDetails(List<AppRecordCell> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AppRecord{" +
                "networkId=" + networkId +
                ", details=" + details.toString() +
                '}';
    }
}
