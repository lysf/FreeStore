package third.com.snail.trafficmonitor.ui.loader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snailgame.cjg.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.AppWrapper;
import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.data.table.TrafficDao;
import third.com.snail.trafficmonitor.engine.util.CalenderMonthTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by kevin on 14/12/23.
 */
public class AppWrapperDataLoader extends AsyncTaskLoader<List<AppWrapper>> {
    private final String TAG = AppWrapperDataLoader.class.getSimpleName();

    private PackageManager pm;
    private int year;
    private int month;
    private long start;
    private long end;

    public AppWrapperDataLoader(Context context, int year, int month) {
        super(context);
        pm = context.getPackageManager();
        this.year = year;
        this.month = month;
        Calendar c = Calendar.getInstance();
        List<TimeInfo> info = new CalenderMonthTools(context, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).measureWeeksInMonth();
        start = info.get(0).getStartTimeStamp();
        end = info.get(info.size() - 1).getEndTimeStamp();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public List<AppWrapper> loadInBackground() {
        LogWrapper.d(TAG, "loadInBackground");
        TrafficDao trafficDao = null;
        try {
            trafficDao = new TrafficDao(getContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (trafficDao == null)
            return null;
        //查询符合时间戳的 Traffic数据库信息
        List<Traffic> data = null;
        try {
            QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
            queryBuilder.where().between("end_timestamp", start, end);
            PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
            data = trafficDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (data == null) {
            LogWrapper.d(TAG, String.format("Cant find any traffic info between %d and %d", start, end));
            return null;
        }
        LogWrapper.d(TAG, "found data " + data.size() + " with start:" + start + " and end: " + end);
        List<App> appList = null;
        try {
            appList = new AppDao(getContext()).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<AppWrapper> result = new ArrayList<AppWrapper>();
        //TODO 以下的数据组合算法需要优化，耗时稍长
        if (appList == null)
            return result;
        for (App app : appList) {
            if (app.getPackageName().equals("com.snailgame.cjg")) continue;
            if (!app.isDisplay()) continue;
            AppWrapper appWrapper = new AppWrapper();
            appWrapper.setApp(app);
            try {
                appWrapper.setIcon(pm.getApplicationIcon(app.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                appWrapper.setIcon(getContext().getResources().getDrawable(R.drawable.ic_default));
            }
            long totalRx = 0;
            long totalTx = 0;
            long wifiRx = 0;
            long wifiTx = 0;
            for (Traffic t : data) {
                if (t.getApp().getId() == app.getId()) {
                    totalRx += t.getDownloadBytes();
                    totalTx += t.getUploadBytes();
                    if (t.getNetwork().getNetworkType() == Network.NetworkType.WIFI) {
                        wifiTx += t.getUploadBytes();
                        wifiRx += t.getDownloadBytes();
                    }
                }
            }
            appWrapper.setTotalRx(totalRx);
            appWrapper.setTotalTx(totalTx);
            appWrapper.setWifiRx(wifiRx);
            appWrapper.setWifiTx(wifiTx);
            result.add(appWrapper);
        }
        return result;
    }
}
