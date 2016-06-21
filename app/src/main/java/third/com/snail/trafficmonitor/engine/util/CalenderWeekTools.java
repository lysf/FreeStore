package third.com.snail.trafficmonitor.engine.util;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;

/**
 * Created by lic on 2014/9/25.
 * 计算一天几个小时
 */
public class CalenderWeekTools {
    private int hour;
    private int minute;
    private int second;
    private int year;
    private int month;
    private int day;
    private int day_of_week;
    private int wewkCutNum;
    public int startDayOfWeek;
    public int endDayOfweek;

    public CalenderWeekTools(int year, int month, int day) {
        this.hour = 23;
        this.minute = 59;
        this.second = 59;
        this.year = year;
        this.month = month;
        this.day = day;
        getMonthStartDayOfWeek();
        wewkCutNum = getWeekNum();
    }

    private void getMonthStartDayOfWeek() {//获取当前是礼拜几
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
    }

    private int getWeekNum() {//获取当前礼拜有几天
        int i = 0;
        switch (day_of_week) {
            case Calendar.SUNDAY:
                i = 7;
                break;
            case Calendar.MONDAY:
                i = 1;
                break;
            case Calendar.TUESDAY:
                i = 2;
                break;
            case Calendar.WEDNESDAY:
                i = 3;
                break;
            case Calendar.THURSDAY:
                i = 4;
                break;
            case Calendar.FRIDAY:
                i = 5;
                break;
            case Calendar.SATURDAY:
                i = 6;
                break;
        }
        if (day - i + 1 > 0 && day + 7 - i < getMonthEndDay()) {//如果当前礼拜的礼拜一不是上个月的，那就直接返回，如果是上个月的那就要重新计算
            startDayOfWeek = 1;
            endDayOfweek = 7;
            return i;
        } else {
            if (day - i + 1 < 0) {//当前礼拜的礼拜一是上个月
                startDayOfWeek = i - day + 1;
                endDayOfweek = 7;
                return endDayOfweek - startDayOfWeek + 1;
            } else {//当前礼拜的礼拜天是下个月
                startDayOfWeek = 1;
                endDayOfweek = getMonthEndDay() - day + i;
                return endDayOfweek - startDayOfWeek + 1;
            }
        }
    }

    private int getMonthEndDay() {//获取当月的最后一天是几号
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        return cal.getActualMaximum(Calendar.DATE);
    }

    public List<TimeInfo> measure() {
        List<TimeInfo> list = new ArrayList<TimeInfo>();
        TimeInfo timeInfo;
        for (int i = 0; i < wewkCutNum; i++) {
            timeInfo = new TimeInfo();
            Calendar c = Calendar.getInstance();
            timeInfo.setDayOfWeek(startDayOfWeek + i);
            c.set(year,month,day - wewkCutNum + i + 1,0,0,0);
            timeInfo.setStartTimeStamp(c.getTimeInMillis());
            c.set(year,month,day - wewkCutNum + i + 1,23,59,59);
            timeInfo.setEndTimeStamp(c.getTimeInMillis());
            c.add(Calendar.MINUTE, 5);
            timeInfo.setEndTimeStampPlus(c.getTimeInMillis());
            list.add(timeInfo);
        }
        return list;
    }




}
