package third.com.snail.trafficmonitor.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.util.UIUtil;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.data.bean.TrafficInfo;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.data.table.TrafficDao;
import third.com.snail.trafficmonitor.engine.util.CalenderMonthTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.ui.helper.ActionBarHelper;
import third.com.snail.trafficmonitor.ui.widget.NoScrollViewPager;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by lic on 2014/12/16.
 * 流量使用情况承载界面
 */
public class TimeBucketActivity extends SwipeBackActivity implements LoaderManager.LoaderCallbacks<TimeBucketActivity.ReturnData> {
    private final static String TAG = TimeBucketActivity.class.getSimpleName();
    public final static String APP_ID = "app_id";
    public final static String APP_NAME = "app_name";
    public final static String FIRST_PAGE = "first_page";
    @Bind(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @Bind(R.id.view_pager)
    NoScrollViewPager mPager;
    private TimeBucketInsideFragment mobileFragment, wifiFragment;
    private PagerAdapter mAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private int REFRESH_ID = 1;//loader的id
    private List<TimeInfo> timeList;
    private int currentDate, currentMonth, currentYear;
    private View progressBarLayout;
    private String appId = null;
    private String appName = null;
    private int firstPage;

    public static Intent newTimeBucket(Context context, int firstPage, String appId, String appName) {
        Intent intent = new Intent(context, TimeBucketActivity.class);
        intent.putExtra(APP_ID, appId);
        intent.putExtra(APP_NAME, appName);
        intent.putExtra(FIRST_PAGE, firstPage);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.engine_activity_time_bucket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int month = c.get(Calendar.MONTH);
        ActionBarHelper.makeCommonActionBar(this, String.format(getString(R.string.time_bucket_title), month + 1));
        currentDate = c.get(Calendar.DATE);
        currentMonth = c.get(Calendar.MONTH);
        currentYear = c.get(Calendar.YEAR);
        progressBarLayout = findViewById(R.id.progress_bar);
        appId = getIntent().getStringExtra(APP_ID);
        appName = getIntent().getStringExtra(APP_NAME);
        firstPage = getIntent().getIntExtra(FIRST_PAGE, 0);
        if (appName != null) {
            ActionBarHelper.makeCommonActionBar(this, appName);
        } else {
            ActionBarHelper.makeCommonActionBar(this, String.format(getString(R.string.time_bucket_title), month + 1));
        }
        getSupportLoaderManager().initLoader(REFRESH_ID, null, this);
    }

    public void initPager() {
        mPager.setScanScroll(false);
        mAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        mPager.setAdapter(mAdapter);
        mTabs.setViewPager(mPager, 2, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {

            }
        });
        UIUtil.changeViewPagerTabWithOutAnimation(mPager, firstPage);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {getString(R.string.mobile), "WIFI"};
        private ArrayList<Fragment> fragmentArrayList;

        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArrayList) {
            super(fm);
            this.fragmentArrayList = fragmentArrayList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

    }

    /**
     * 通过选择时间将每段时间分段，并且组成list
     */
    private void getTimeCutList() throws SQLException {
        CalenderMonthTools calenderMonthTools = new CalenderMonthTools(this, currentYear, currentMonth, currentDate);
        timeList = calenderMonthTools.measureDaysInMonth();
    }

    @Override
    public QueryChartData onCreateLoader(int i, Bundle bundle) {
        if (timeList != null) {
            timeList.clear();
        }
        try {
            getTimeCutList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new QueryChartData(this, timeList, currentYear, currentMonth, currentDate, appId);
    }

    @Override
    public void onLoadFinished(Loader<ReturnData> listLoader, ReturnData returnData) {
        progressBarLayout.setVisibility(View.GONE);
        getSupportLoaderManager().destroyLoader(REFRESH_ID);
        mobileFragment = TimeBucketInsideFragment.newInstance(true,
                (ArrayList<TrafficInfo>) returnData.mobileTrafficList, appId, firstPage);
        wifiFragment = TimeBucketInsideFragment.newInstance(false,
                (ArrayList<TrafficInfo>) returnData.wifiTrafficList, appId, firstPage);
        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(mobileFragment);
        fragmentArrayList.add(wifiFragment);
        initPager();
    }

    @Override
    public void onLoaderReset(Loader<ReturnData> listLoader) {
    }

    public static class QueryChartData extends AsyncTaskLoader<ReturnData> {
        PackageManager pm;
        private List<TimeInfo> timeList;
        private List<TrafficInfo> wifiTrafficList;
        private List<TrafficInfo> mobileTrafficList;
        private Context context;
        private int currentDate, currentMonth, currentYear;
        private String appId;

        public QueryChartData(Context context, List<TimeInfo> timeList,
                              int year, int month, int date, String appId) {
            super(context);
            this.timeList = timeList;
            wifiTrafficList = new ArrayList<>();
            mobileTrafficList = new ArrayList<>();
            this.context = context;
            this.currentYear = year;
            this.currentMonth = month;
            this.currentDate = date;
            this.appId = appId;
            pm = context.getPackageManager();
        }

        @Override
        protected void onStartLoading() {
            LogWrapper.d(TAG, "onStartLoading");
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
        public ReturnData loadInBackground() {
            if (ListUtils.isEmpty(timeList)) {
                return null;
            }
            TrafficDao trafficDao = null;
            try {
                trafficDao = new TrafficDao(getContext());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (trafficDao == null)
                return null;
            List<Traffic> result = null;

            TrafficInfo mobileTrafficInfo;
            TrafficInfo wifiTrafficInfo;
            for (TimeInfo timeInfo : timeList) {
                wifiTrafficInfo = new TrafficInfo();
                mobileTrafficInfo = new TrafficInfo();
                if (timeInfo.getStartDay() == currentDate) {
                    wifiTrafficInfo.setChecked(true);
                    mobileTrafficInfo.setChecked(true);
                }
                wifiTrafficInfo = new TrafficInfo();
                mobileTrafficInfo = new TrafficInfo();
                if (timeInfo.getStartDay() == currentDate) {
                    wifiTrafficInfo.setChecked(true);
                    mobileTrafficInfo.setChecked(true);
                }
                wifiTrafficInfo.setDayOfWeek(timeInfo.getDayOfWeek());
                wifiTrafficInfo.setDay(timeInfo.getStartDay());
                wifiTrafficInfo.setEndTimeStamp(timeInfo.getEndTimeStamp());
                wifiTrafficInfo.setStartTimeStamp(timeInfo.getStartTimeStamp());
                wifiTrafficInfo.setBytes(timeInfo.getWifiBytes());
                wifiTrafficInfo.setEndTimeStampPlus(timeInfo.getEndTimeStampPlus());
                mobileTrafficInfo.setDayOfWeek(timeInfo.getDayOfWeek());
                mobileTrafficInfo.setDay(timeInfo.getStartDay());
                mobileTrafficInfo.setEndTimeStamp(timeInfo.getEndTimeStamp());
                mobileTrafficInfo.setStartTimeStamp(timeInfo.getStartTimeStamp());
                mobileTrafficInfo.setBytes(timeInfo.getOtherBytes());
                mobileTrafficInfo.setEndTimeStampPlus(timeInfo.getEndTimeStampPlus());
                wifiTrafficList.add(wifiTrafficInfo);
                mobileTrafficList.add(mobileTrafficInfo);
            }

            if (appId != null && !TextUtils.isEmpty(appId)) {
                try {
                    QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
                    queryBuilder.where().eq(Traffic.COLUMN_APP_ID, appId).and().between(Traffic.COLUMN_END_TIMESTAMP, timeList.get(0).getStartTimeStamp(),
                            timeList.get(timeList.size() - 1).getEndTimeStampPlus());
                    PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
                    result = trafficDao.query(preparedQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO 优化
                String[] strings = new String[]{
                        EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE,
                        EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE
                };
                for (String string : strings) {
                    List<App> apps = null;
                    try {
                        apps = new AppDao(getContext()).query(App.COLUMN_PACKAGE_NAME, string);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (apps == null || apps.size() == 0) {
                        continue;
                    }
                    App app = apps.get(0);
                    try {
                        QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
                        queryBuilder.where().eq(Traffic.COLUMN_APP_ID, app.getId() + "").and().between(Traffic.COLUMN_END_TIMESTAMP, "" + timeList.get(0).getStartTimeStamp(),
                                "" + timeList.get(timeList.size() - 1).getEndTimeStampPlus());
                        PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
                        result = trafficDao.query(preparedQuery);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (result == null || result.size() == 0) {
                        continue;
                    }
                    if (app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                        for (TrafficInfo trafficInfo : mobileTrafficList) {
                            for (Traffic traffic : result) {
                                if (traffic.getStartTimestamp() > trafficInfo.getStartTimeStamp()
                                        && traffic.getEndTimestamp() <= trafficInfo.getEndTimeStampPlus()
                                        && traffic.getStartTimestamp() < trafficInfo.getEndTimeStamp()) {
                                    if (traffic.getNetwork() != null) {
                                        if (traffic.getNetwork().getNetworkType() != Network.NetworkType.WIFI
                                                && traffic.getApp().getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                                            trafficInfo.setBytes(trafficInfo.getBytes()
                                                    + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (TrafficInfo trafficInfo : wifiTrafficList) {
                            for (Traffic traffic : result) {
                                if (traffic.getStartTimestamp() > trafficInfo.getStartTimeStamp()
                                        && traffic.getEndTimestamp() <= trafficInfo.getEndTimeStampPlus()
                                        && traffic.getStartTimestamp() < trafficInfo.getEndTimeStamp()) {
                                    if (traffic.getNetwork() != null) {
                                        if (traffic.getNetwork().getNetworkType() == Network.NetworkType.WIFI
                                                && traffic.getApp().getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                                            trafficInfo.setBytes(trafficInfo.getBytes()
                                                    + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ReturnData returnData = new ReturnData(wifiTrafficList, mobileTrafficList);
                return returnData;
            }
            if (result == null || result.size() == 0) {
                ReturnData returnData = new ReturnData(wifiTrafficList, mobileTrafficList);
                return returnData;
            }
            //TODO 优化
            if (result.size() > 0) {
                for (int i = 0; i < timeList.size(); i++) {
                    TimeInfo timeInfo = timeList.get(i);
                    wifiTrafficInfo = wifiTrafficList.get(i);
                    mobileTrafficInfo = mobileTrafficList.get(i);
                    for (Traffic traffic : result) {
                        if (traffic.getStartTimestamp() > timeInfo.getStartTimeStamp()
                                && traffic.getEndTimestamp() <= timeInfo.getEndTimeStampPlus()
                                && traffic.getStartTimestamp() < timeInfo.getEndTimeStamp()) {
                            if (traffic.getNetwork() != null) {
                                if (traffic.getNetwork().getNetworkType() == Network.NetworkType.WIFI) {
                                    timeInfo.setWifiBytes(timeInfo.getWifiBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                    wifiTrafficInfo.setBytes(wifiTrafficInfo.getBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                } else {
                                    timeInfo.setOtherBytes(timeInfo.getOtherBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                    mobileTrafficInfo.setBytes(mobileTrafficInfo.getBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                }
                            }
                        }
                    }
                }

            } else {
                if (timeList != null && wifiTrafficList != null && mobileTrafficList != null) {
                    timeList.clear();
                    mobileTrafficList.clear();
                    wifiTrafficList.clear();
                }
            }
            ReturnData returnData = new ReturnData(wifiTrafficList, mobileTrafficList);
            return returnData;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ReturnData {
        public ReturnData(List<TrafficInfo> wifiTrafficList, List<TrafficInfo> mobileTrafficList) {
            this.wifiTrafficList = wifiTrafficList;
            this.mobileTrafficList = mobileTrafficList;
        }

        List<TrafficInfo> wifiTrafficList;
        List<TrafficInfo> mobileTrafficList;
    }
}
