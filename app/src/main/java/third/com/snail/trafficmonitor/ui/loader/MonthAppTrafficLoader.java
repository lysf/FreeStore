package third.com.snail.trafficmonitor.ui.loader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.AppTraffic;
import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.data.table.TrafficDao;
import third.com.snail.trafficmonitor.engine.util.CalenderDayTools;
import third.com.snail.trafficmonitor.engine.util.CalenderMonthTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by kevin on 14/12/22.
 */
public class MonthAppTrafficLoader extends AsyncTaskLoader<List<AppTraffic>> {
    private final String TAG = MonthAppTrafficLoader.class.getSimpleName();
    private PackageManager pm;
    private List<Traffic> data;
    private long start;
    private long end;
    private int year;
    private int month;
    private int date;
    /**
     * 限定查询哪些package名
     */
    private ArrayList<String> limitPackages;

    public MonthAppTrafficLoader(Context context, String... packages) {
        super(context);
        pm = context.getPackageManager();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        date = c.get(Calendar.DATE);
        List<TimeInfo> info = new CalenderMonthTools(context, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).measureWeeksInMonth();
        start = info.get(0).getStartTimeStamp();
        end = info.get(info.size() - 1).getEndTimeStamp();
        LogWrapper.d(TAG, new Date(start).toString());
        LogWrapper.d(TAG, new Date(end).toString());
        if (packages != null && packages.length > 0) {
            limitPackages = new ArrayList<>(Arrays.asList(packages));
        }
    }

    @Override
    protected void onStartLoading() {
        LogWrapper.d(TAG, "onStartLoading" + getId());
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        LogWrapper.d(TAG, "onStopLoading");
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public List<AppTraffic> loadInBackground() {
        LogWrapper.d(TAG, "loadInBackground");
        TrafficDao trafficDao = null;
        AppDao appDao = null;
        List<AppTraffic> result = new ArrayList<AppTraffic>();
        try {
            trafficDao = new TrafficDao(getContext());
            appDao = new AppDao(getContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (trafficDao == null || appDao == null)
            return result;
        //查询符合时间戳的 Traffic数据库信息
        if (limitPackages != null && limitPackages.size() > 0) {
            for (String pkgName : limitPackages) {
                List<App> apps = null;
                try {
                    apps = appDao.query(App.COLUMN_PACKAGE_NAME, pkgName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (apps == null || apps.size() == 0) {
                    continue;
                }
                App app = apps.get(0);
                List<Traffic> traffics = null;
                try {
                    QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
                    queryBuilder.where().eq(Traffic.COLUMN_APP_ID, app.getId()).and().rawComparison(Traffic.COLUMN_START_TIMESTAMP, ">=", start)
                            .and().rawComparison(Traffic.COLUMN_END_TIMESTAMP, "<=", end);
                    PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
                    traffics = trafficDao.query(preparedQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (traffics == null || traffics.size() == 0) {
                    continue;
                }
                AppTraffic traffic = null;
                try {
                    traffic = equipAppTraffic(app, traffics);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                result.add(traffic);
            }
            return result;
        }
        try {
            QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
            queryBuilder.where().between("end_timestamp", start, end);
            PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
            data = trafficDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ListUtils.isEmpty(data)) {
            return result;
        }
        List<App> appList = null;
        try {
            appList = appDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (appList == null) {
            return result;
        }
        //TODO 以下的数据组合算法需要优化，耗时稍长
        for (App app : appList) {
            if (!app.isDisplay()) {
                continue;
            }
            AppTraffic traffic = null;
            try {
                traffic = equipAppTraffic(app, data);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (traffic.rxBytes + traffic.txBytes > 0) {
                result.add(traffic);
            }
        }
        return result;
    }

    /**
     * 将当天时间分段，并且组成list
     */
    private List<TimeInfo> getTimeCutList() throws SQLException {
        CalenderDayTools calendarDayTools = new CalenderDayTools(getContext(), year,
                month, date);
        List<TimeInfo> timeList = calendarDayTools.measure();
        return timeList;
    }

    private AppTraffic equipAppTraffic(App app, List<Traffic> traffics) throws SQLException {
        List<TimeInfo> timeInfos = getTimeCutList();
        AppTraffic traffic = new AppTraffic();
        traffic.appId = app.getId();
        traffic.uid = app.getUid();
        traffic.appName = app.getAppName();
        traffic.packageName = app.getPackageName();
        try {
            traffic.icon = pm.getApplicationIcon(app.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            traffic.icon = getContext().getResources().getDrawable(R.drawable.ic_default);
        }
        if (traffics == null) {
            traffic.trafficInDayList = timeInfos;
            return traffic;
        }
        for (Traffic t : traffics) {
            //将当前app当天流量数据插入到当天的数据列表中去
            for (TimeInfo timeInfo : timeInfos) {
                if (t.getStartTimestamp() > timeInfo.getStartTimeStamp()
                        && t.getEndTimestamp() <= timeInfo.getEndTimeStampPlus()
                        && t.getStartTimestamp() < timeInfo.getEndTimeStamp()) {
                    if (t.getNetwork().getNetworkType() != Network.NetworkType.WIFI) {
                        timeInfo.setOtherBytes(timeInfo.getOtherBytes() + t.getUploadBytes() + t.getDownloadBytes());
                    } else {
                        timeInfo.setWifiBytes(timeInfo.getWifiBytes() + t.getUploadBytes() + t.getDownloadBytes());
                    }
                }
            }
            if (t.getApp().getId() == app.getId()) {
                traffic.rxBytes += t.getDownloadBytes();
                traffic.txBytes += t.getUploadBytes();
                if (t.getNetwork().getNetworkType() == Network.NetworkType.WIFI) {
                    traffic.wifiTxBytes += t.getUploadBytes();
                    traffic.wifiRxBytes += t.getDownloadBytes();
                }
            }
        }
        traffic.trafficInDayList = timeInfos;
        return traffic;
    }
}
