package third.com.snail.trafficmonitor.engine.data.bean;

/**
 * Created by lic on 2014/9/28.
 * 流量的实体类
 */
public class FlowInfo {

    /**
     * 流量的数值
     */
    String bytes;
    /**
     * 流量的单位
     */
    String bytesType;

    public String getBytes() {
        return bytes;
    }

    public String getBytesType() {
        return bytesType;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public void setBytesType(String bytesType) {
        this.bytesType = bytesType;
    }
}
