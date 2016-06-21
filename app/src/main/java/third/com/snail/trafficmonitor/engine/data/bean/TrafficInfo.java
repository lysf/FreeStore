package third.com.snail.trafficmonitor.engine.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by licong on 2014/12/22.
 */
public class TrafficInfo implements Comparable<TrafficInfo>, Parcelable {

    /**
     * 当天日期
     */
    private int day;

    /**
     * 流量
     */
    private long bytes;

    /**
     * 开始时间戳
     */
    private long startTimeStamp;

    /**
     * 结束时间戳
     */
    private long endTimeStamp;

    /**
     * 当天是星期几
     */
    private int dayOfWeek;

    /**
     * 将当天或者当前时段标注的判断值
     */
    private boolean isChecked;

    /**
     * 结束时间加五分钟的时间戳，为了配合打点使用
     */
    private long endTimeStampPlus;

    /**
     * 是否播放progressBar的动画
     */
    private boolean isShowAnimation = true;

    public TrafficInfo() {
    }

    public void setEndTimeStampPlus(long endTimeStampPlus) {
        this.endTimeStampPlus = endTimeStampPlus;
    }

    public long getEndTimeStampPlus() {

        return endTimeStampPlus;
    }

    public boolean isShowAnimation() {
        return isShowAnimation;
    }

    public void setShowAnimation(boolean isShowAnimation) {
        this.isShowAnimation = isShowAnimation;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public long getBytes() {
        return bytes;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public int getDay() {
        return day;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int compareTo(@NonNull TrafficInfo another) {
        if (another.bytes - bytes > 0) {
            return 1;
        } else if (another.bytes - bytes < 0) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "WifiTrafficInfo{" +
                "day='" + day + '\'' +
                ", bytes=" + bytes +
                ", startTimeStamp=" + startTimeStamp +
                ", endTimeStamp=" + endTimeStamp +
                ", dayOfWeek=" + dayOfWeek +
                ", isChecked=" + isChecked +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.day);
        parcel.writeLong(this.getBytes());
        parcel.writeLong(this.startTimeStamp);
        parcel.writeLong(this.endTimeStamp);
        parcel.writeInt(this.dayOfWeek);
        parcel.writeByte(isChecked ? (byte) 1 : (byte) 0);
    }

    public static final Parcelable.Creator<TrafficInfo> CREATOR = new Parcelable.Creator<TrafficInfo>() {
        public TrafficInfo createFromParcel(Parcel in) {
            return new TrafficInfo(in);
        }

        public TrafficInfo[] newArray(int size) {
            return new TrafficInfo[size];
        }
    };

    private TrafficInfo(Parcel in) {
        this.day = in.readInt();
        this.bytes = in.readLong();
        this.startTimeStamp = in.readLong();
        this.endTimeStamp = in.readLong();
        this.dayOfWeek = in.readInt();
        this.isChecked = in.readByte() != 0;
    }


}
