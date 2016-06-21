package third.com.snail.trafficmonitor.engine.util;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;

/**
 * Created by lic on 2014/9/25.
 * 计算一天几个小时的工具类
 */
public class CalenderDayTools {

    private int hour;
    private int minute;
    private int second;
    private int TimeCutNum;
    private int year;
    private int month;
    private int day;
    private Context context;
    public CalenderDayTools(Context context, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (day == calendar.get(Calendar.DATE)) {
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);
            this.second = calendar.get(Calendar.SECOND);
        } else {
            this.hour = 23;
            this.minute = 59;
            this.second = 59;
        }
        this.year = year;
        this.month = month;
        this.day = day;
        this.context = context;
        getTimeCutNum();
    }

    /**
     * 4小时为单位。获取总共有几段
     */
    private void getTimeCutNum() {
        if (hour % 4 == 0) {
            if (minute != 0) {
                TimeCutNum = hour / 4 + 1;
            } else {
                TimeCutNum = hour / 4;
            }
        } else {
            TimeCutNum = hour / 4 + 1;
        }
    }

    /**
     * 4小时为单位。获取总共有几段
     */
    public List<TimeInfo> measure() throws SQLException {
        List<TimeInfo> list = new ArrayList<>();
        TimeInfo timeInfo;
        for (int i = 0; i < TimeCutNum; i++) {
            timeInfo = new TimeInfo();
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, i * 4, 0, 0);
            timeInfo.setStartTimeStamp(c.getTimeInMillis());
            c.set(year, month, day, i * 4 + 4, 0, 0);
            timeInfo.setEndTimeStamp(c.getTimeInMillis());
            c.add(Calendar.MINUTE, 5);
            timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
            list.add(timeInfo);
        }
        //检测endTimeStamp
        new CheckEndTimeStampTools(context, year, month, day, false).checkTimeInfo(list);
        return list;
    }


}
