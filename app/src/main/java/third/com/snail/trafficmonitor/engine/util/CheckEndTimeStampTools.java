package third.com.snail.trafficmonitor.engine.util;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.data.table.Profile;
import third.com.snail.trafficmonitor.engine.data.table.ProfileDao;

/**
 * Created by lic on 2015/1/7.
 * 查询profile表并且校正endtimestamp
 */
public class CheckEndTimeStampTools {

    private Calendar c;
    private int year, month, day;
    private List<Profile> profileList;
    private boolean isMonth;
    private ProfileDao profileDao;

    public CheckEndTimeStampTools(Context context, int year, int month, int day, boolean isMonth) throws SQLException {
        profileDao = new ProfileDao(context);
        this.year = year;
        this.month = month;
        this.day = day;
        this.isMonth = isMonth;
        c = Calendar.getInstance();
        if (isMonth) {
            initIsMonth();
        } else {
            init();
        }
    }

    /**
     * 按照月的机制查询profile表
     */
    private void initIsMonth() throws SQLException {
        c.set(year, month, 1, 0, 0, 0);
        long startTimeStamp = c.getTimeInMillis();
        c.set(year, month, day + 1, 0, 30, 0);
        long endTimeStamp = c.getTimeInMillis();

        QueryBuilder<Profile, Integer> queryBuilder = profileDao.getDao().queryBuilder();
        queryBuilder.where().eq(Profile.COLUMN_KEY, Profile.COLUMN_KEY_TIMESTAMP).and().between(Profile.COLUMN_VALUE, startTimeStamp, endTimeStamp);
        PreparedQuery<Profile> preparedQuery = queryBuilder.prepare();
        profileList = profileDao.query(preparedQuery);

    }

    /**
     * 查询profile表
     */
    private void init() throws SQLException {
        c.set(year, month, day, 0, 0, 0);
        long startTimeStamp = c.getTimeInMillis();
        c.set(year, month, day + 1, 0, 30, 0);
        long endTimeStamp = c.getTimeInMillis();
        QueryBuilder<Profile, Integer> queryBuilder = profileDao.getDao().queryBuilder();
        queryBuilder.where().eq(Profile.COLUMN_KEY, Profile.COLUMN_KEY_TIMESTAMP).and().between(Profile.COLUMN_VALUE, startTimeStamp, endTimeStamp);
        PreparedQuery<Profile> preparedQuery = queryBuilder.prepare();
        profileList = profileDao.query(preparedQuery);
    }

    /**
     * 检查ArrayList<Long>这种类型数据的endtimestamp
     */
    public void checkLong(ArrayList<Long> timestamps) {
        if (profileList.size() > 0) {
            for (int i = 0; i < timestamps.size(); i++) {
                c.setTimeInMillis(timestamps.get(i));
                int preHour = c.get(Calendar.HOUR_OF_DAY);
                int preDay = c.get(Calendar.DATE);
                for (int j = 0; j < profileList.size(); j++) {
                    c.setTimeInMillis(Long.parseLong(profileList.get(j).getValue()));
                    int afterHour = c.get(Calendar.HOUR_OF_DAY);
                    int afterDay = c.get(Calendar.DATE);
                    if (preDay == afterDay) {
                        if (afterHour == preHour) {
                            timestamps.set(i, Long.parseLong(profileList.get(j).getValue()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查List<TimeInfo>这种类型数据的endtimestamp
     */
    public void checkTimeInfo(List<TimeInfo> list) {
        if (profileList.size() > 0) {
            for (TimeInfo info : list) {
                c.setTimeInMillis(info.getEndTimeStampPlus());
                int preHour = c.get(Calendar.HOUR_OF_DAY);
                int preDay = c.get(Calendar.DATE);
                int preMonth = c.get(Calendar.MONTH);
                for (Profile profile : profileList) {
                    c.setTimeInMillis(Long.parseLong(profile.getValue()));
                    int afterHour = c.get(Calendar.HOUR_OF_DAY);
                    int afterDay = c.get(Calendar.DATE);
                    int afterMonth = c.get(Calendar.MONTH);
                    if (isMonth) {
                        if (preMonth == afterMonth) {
                            if (preDay == afterDay) {
                                info.setEndTimeStampPlus(Long.parseLong(profile.getValue()));
                            }
                        }
                    } else {
                        if (preDay == afterDay) {
                            if (afterHour == preHour) {
                                info.setEndTimeStampPlus(Long.parseLong(profile.getValue()));
                            }
                        }
                    }
                }
            }
        }

    }

}
