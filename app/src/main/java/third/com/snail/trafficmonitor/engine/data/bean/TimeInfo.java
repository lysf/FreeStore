package third.com.snail.trafficmonitor.engine.data.bean;

/**
 * Created by lic on 2014/9/25.
 */
public class TimeInfo {

    /**
     * 时间段开始
     */
    private int startDay;

    /**
     * 时间段的结束是几号
     */
    private int endDay;

    /**
     * wifi流量
     */
    private long wifiBytes;

    /**
     * 其他流量
     */
    private long otherBytes;

    /**
     * 开始时间戳
     */
    private long startTimeStamp;

    /**
     * 结束时间戳
     */
    private long endTimeStamp;

    /**
     * 结束时间为整点打点的时间
     */
    private long endTimeStampPlus;

    /**
     * 当天是星期几
     */
    private int dayOfWeek;

    /**
     * 将当天或者当前时段标注的判断值
     */
    private boolean isChecked;

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public long getWifiBytes() {
        return wifiBytes;
    }

    public long getOtherBytes() {
        return otherBytes;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public void setWifiBytes(long wifiBytes) {
        this.wifiBytes = wifiBytes;
    }

    public void setOtherBytes(long otherBytes) {
        this.otherBytes = otherBytes;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public long getEndTimeStampPlus() {
        return endTimeStampPlus;
    }

    public void setEndTimeStampPlus(long endTimeStampPlus) {
        this.endTimeStampPlus = endTimeStampPlus;
    }
}
