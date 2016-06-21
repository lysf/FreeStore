package third.com.snail.trafficmonitor.engine.util;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;

/**
 * Created by lic on 2014/9/25.
 * 计算一个月有几个礼拜
 */
public class CalenderMonthTools {

    private int year;
    private int month;
    private int day;
    public int weekNum;
    private int month_end_day;
    private int month_start_day;
    private int month_start_day_of_week;
    private int month_end_day_of_week;
    private int month_end_hour;
    private int month_end_minute;
    private int month_end_second;
    private Context context;
    public CalenderMonthTools(Context context, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.year = year;
        this.month = month;
        this.day = day;
        this.context = context;
        init();
    }

    private void init() {
        month_start_day = 1;
        month_end_day = getMonthEndDay();
        this.month_end_hour = 23;
        this.month_end_minute = 59;
        this.month_end_second = 59;
        getMonthStartDayOfWeek(year, month);
        getMonthEndDayOfWeek(year, month);
        getWeekNum();
    }

    /**
     * 获取当天是在这个月里面的第几个星期
     */
    public int getCurrentDayInWeekOfNumber() {
        List<TimeInfo> list = measureWeeksInMonth();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartDay() <= day && list.get(i).getEndDay() >= day) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * 获取当月的最后一天是几号
     */
    public int getMonthEndDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        int month_end_day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return month_end_day;
    }

    /**
     * 获取当月的第一天是礼拜几
     */
    private void getMonthStartDayOfWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, month_start_day);
        month_start_day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当月的最后一天是礼拜几
     */
    private void getMonthEndDayOfWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, month_end_day);
        month_end_day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当月一共有几个礼拜
     */
    private void getWeekNum() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, month_end_day);
        weekNum = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        LogWrapper.d("weekNum" + weekNum);
    }

    /**
     * 获取这个月的每个礼拜的开始时间和结束时间
     */
    public List<TimeInfo> measureWeeksInMonth() {
        List<TimeInfo> list = new ArrayList<TimeInfo>();
        TimeInfo timeInfo;
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < weekNum; i++) {
            timeInfo = new TimeInfo();
            if (i == 0) {
                timeInfo.setStartDay(month_start_day);
                timeInfo.setEndDay(getDateFromStart(timeInfo.getStartDay(), month_start_day_of_week));
                c.set(year, month, timeInfo.getStartDay(), 0, 0, 0);
                timeInfo.setStartTimeStamp(c.getTimeInMillis());
                c.set(year, month, timeInfo.getEndDay(), 23, 59, 59);
                timeInfo.setEndTimeStamp(c.getTimeInMillis());
                c.add(Calendar.MINUTE, 5);
                timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
                list.add(timeInfo);
            } else if (i == weekNum - 1) {
                timeInfo.setEndDay(month_end_day);
                timeInfo.setStartDay(getDateFromEnd(timeInfo.getEndDay(), month_end_day_of_week));
                c.set(year, month, timeInfo.getStartDay(), 0, 0, 0);
                timeInfo.setStartTimeStamp(c.getTimeInMillis());
                c.set(year, month, timeInfo.getEndDay(), month_end_hour, month_end_minute, month_end_second);
                timeInfo.setEndTimeStamp(c.getTimeInMillis());
                c.add(Calendar.MINUTE, 5);
                timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
                list.add(timeInfo);
            } else {
                timeInfo.setStartDay(list.get(i - 1).getEndDay() + 1);
                timeInfo.setEndDay(timeInfo.getStartDay() + 6);
                c.set(year, month, timeInfo.getStartDay(), 0, 0, 0);
                timeInfo.setStartTimeStamp(c.getTimeInMillis());
                c.set(year, month, timeInfo.getEndDay(), 23, 59, 59);
                timeInfo.setEndTimeStamp(c.getTimeInMillis());
                c.add(Calendar.MINUTE, 5);
                timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
                list.add(timeInfo);
            }
        }
        return list;
    }

    /**
     * 获取这个月的每天的开始时间和结束时间
     */
    public List<TimeInfo> measureDaysInMonth() throws SQLException {
        List<TimeInfo> list = new ArrayList<TimeInfo>();
        TimeInfo timeInfo;
        Calendar c = Calendar.getInstance();
        for (int i = month_start_day; i < month_end_day + 1; i++) {
            timeInfo = new TimeInfo();
            timeInfo.setStartDay(i);
            c.set(year, month, i, 0, 0, 0);
            timeInfo.setStartTimeStamp(c.getTimeInMillis());
            c.set(year, month, i, 23, 59, 59);
            timeInfo.setEndTimeStamp(c.getTimeInMillis());
            c.add(Calendar.MINUTE, 5);
            timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
            c.set(year, month, i);
            timeInfo.setDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
            if (i == day) {
                timeInfo.setIsChecked(true);
            }
            list.add(timeInfo);
        }
        //检测endTimeStamp
        new CheckEndTimeStampTools(context, year, month, month_end_day, true).checkTimeInfo(list);
        return list;
    }

    /**
     * 查找第一个礼拜的结束那天是几号
     */
    private int getDateFromStart(int day, int week) {
        switch (week) {
            case Calendar.SUNDAY:
                return day;
            case Calendar.MONDAY:
                return day + 6;
            case Calendar.TUESDAY:
                return day + 5;
            case Calendar.WEDNESDAY:
                return day + 4;
            case Calendar.THURSDAY:
                return day + 3;
            case Calendar.FRIDAY:
                return day + 2;
            case Calendar.SATURDAY:
                return day + 1;
        }
        return -1;
    }

    /**
     * 查找最后一个礼拜开始那天是几号
     */
    private int getDateFromEnd(int day, int week) {
        switch (week) {
            case Calendar.SUNDAY:
                return day - 6;
            case Calendar.MONDAY:
                return day;
            case Calendar.TUESDAY:
                return day - 1;
            case Calendar.WEDNESDAY:
                return day - 2;
            case Calendar.THURSDAY:
                return day - 3;
            case Calendar.FRIDAY:
                return day - 4;
            case Calendar.SATURDAY:
                return day - 5;
        }
        return -1;
    }


}
