package third.com.snail.trafficmonitor.ui;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.data.table.TrafficDao;
import third.com.snail.trafficmonitor.engine.util.CalenderDayTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by lic on 2014/12/17.
 * 折线图所在的fragment
 */
public class LineChartFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<TimeInfo>> {
    private final static String TAG = LineChartFragment.class.getSimpleName();
    @Bind(R.id.line_chart)
    LineChart mChart;
    private LineData mData;
    private int loaderId = 1;
    @Bind(R.id.progress_bar)
    LinearLayout progressLayout;
    @Bind(R.id.no_data)
    LinearLayout noDataLayout;
    @Bind(R.id.data_layout)
    RelativeLayout dataLayout;

    public static LineChartFragment newInstance(boolean isMobile, int date, String appId) {
        LineChartFragment f = new LineChartFragment();
        Bundle args = new Bundle();
        args.putInt(HOUR, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        args.putInt(DATE, date);
        args.putInt(YEAR, Calendar.getInstance().get(Calendar.YEAR));
        args.putInt(MONTH, Calendar.getInstance().get(Calendar.MONTH));
        args.putBoolean(IS_MOBILE, isMobile);
        args.putString(APP_ID, appId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.engine_fragment_line_chart, container, false);
        ButterKnife.bind(this, view);
        //设置显示坐标具体参数
        mChart.setDrawYValues(true);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setDragScaleEnabled(true);
        //设置不显示高亮的显示效果
        mChart.setHighlightEnabled(false);
        mChart.setUnit("MB");
        //设置不显示单位
        mChart.setDrawUnitsInChart(false);
        mChart.setBackgroundColor(getActivity().getResources().getColor(R.color.chart_background));
        //设置不显示图例
        mChart.setDrawLegend(false);
        //设置不能滑动和点击
//        mChart.setDisableZoomListener();
        mChart.setTouchEnabled(false);
        if (getArguments().getInt(DATE) == Calendar.getInstance().get(Calendar.DATE)) {
            mChart.setmIndicesToHightlight(getCurrentTimeTickIndex(getArguments().getInt(HOUR)));
            //去掉标记的view
//            MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.engine_custom_marker_view);
//            mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
//            mChart.setMarkerView(mv);
        }
        XLabels xl = mChart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setAdjustXLabels(true);

        YLabels y1 = mChart.getYLabels();
        y1.setDrawUnitsInYLabel(false);

        getLoaderManager().initLoader(loaderId, null, this);
        return view;
    }

    /**
     * 通过当前时间计算，应该是将哪个点标注高亮
     */
    public int getCurrentTimeTickIndex(int currentHour) {
        int temp[] = {4, 8, 12, 16, 20, 24};
        int selectIndex = -1;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] - currentHour > 0) {
                selectIndex = i;
                break;
            }
        }
        return selectIndex;
    }

    @Override
    public QueryChartData onCreateLoader(int i, Bundle bundle) {
        LogWrapper.e("onCreateLoader");
        try {
            return new QueryChartData(getActivity(), getArguments().getInt(YEAR),
                    getArguments().getInt(MONTH), getArguments().getInt(DATE), getArguments().getString(APP_ID));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<TimeInfo>> listLoader, List<TimeInfo> timeList) {
        progressLayout.setVisibility(View.GONE);
        getLoaderManager().destroyLoader(loaderId);
        if (timeList != null && timeList.size() != 0) {
            boolean isMobileTrafficEmpty = true;
            boolean isWifiTrafficEmpty = true;
            for (int i = 0; i < timeList.size(); i++) {
                if (timeList.get(i).getOtherBytes() != 0) {
                    isMobileTrafficEmpty = false;
                    break;
                }
            }
            for (int i = 0; i < timeList.size(); i++) {
                if (timeList.get(i).getWifiBytes() != 0) {
                    isWifiTrafficEmpty = false;
                    break;
                }
            }
            if (getArguments().getBoolean(IS_MOBILE)) {
                if (isMobileTrafficEmpty) {
                    noDataLayout.setVisibility(View.VISIBLE);
                } else {
                    dataLayout.setVisibility(View.VISIBLE);
                    setChartView(timeList);
                }
            } else {
                if (isWifiTrafficEmpty) {
                    noDataLayout.setVisibility(View.VISIBLE);
                } else {
                    dataLayout.setVisibility(View.VISIBLE);
                    setChartView(timeList);
                }
            }
        } else {
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setChartView(List<TimeInfo> list) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            xVals.add((i * 4) + "-" + (i + 1) * 4);
        }
        ArrayList<Entry> yVals = new ArrayList<>();
        DecimalFormat sDf = new DecimalFormat("0.00");
        for (int i = 0; i < list.size(); i++) {
            if (getArguments().getBoolean(IS_MOBILE)) {
                String s = sDf.format((double) list.get(i).getOtherBytes() / 1024 / 1024);
                yVals.add(new Entry((Float.parseFloat(s)), i));
            } else {
                String s = sDf.format((double) list.get(i).getWifiBytes() / 1024 / 1024);
                yVals.add(new Entry((Float.parseFloat(s)), i));
            }
        }
        LineDataSet set = new LineDataSet(yVals, "DataSet 1");
        set.setLineWidth(1f);
        set.setCircleSize(4.5f);
        set.setColor(Color.rgb(249, 116, 117));
        set.setCircleColor(Color.rgb(117, 192, 74));
        set.setDrawFilled(true);
        set.setFillColor(Color.rgb(248, 234, 234));
        mData = new LineData(xVals, set);
        mChart.setData(mData);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mChart.animateX(500);
        mChart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<List<TimeInfo>> listLoader) {
    }

    public static class QueryChartData extends AsyncTaskLoader<List<TimeInfo>> {
        PackageManager pm;
        private List<TimeInfo> timeList;
        private Context context;
        private int currentMonth, currentYear, currentDate;
        private String appId;

        public QueryChartData(Context context, int year, int month, int date, String appId) throws SQLException {
            super(context);
            this.context = context;
            pm = context.getPackageManager();
            this.currentMonth = month;
            this.currentYear = year;
            this.currentDate = date;
            this.appId = appId;
            this.timeList = getTimeCutList();

        }

        /**
         * 通过选择时间将每段时间分段，并且组成list
         */
        private List<TimeInfo> getTimeCutList() throws SQLException {
            CalenderDayTools calendarDayTools = new CalenderDayTools(context, currentYear,
                    currentMonth, currentDate);
            List<TimeInfo> timeList = calendarDayTools.measure();
            return timeList;
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
        public List<TimeInfo> loadInBackground() {
            if (ListUtils.isEmpty(timeList)) {
                return null;
            }
            TrafficDao trafficDao = null;
            try {
                trafficDao = new TrafficDao(context);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (trafficDao == null)
                return null;
            List<Traffic> result = null;
            if (appId != null && !appId.equals("")) {
                try {
                    QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
                    queryBuilder.where().eq("app_id", appId).and().between("end_timestamp", timeList.get(0).getStartTimeStamp(),
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
                        apps = new AppDao(context).query(App.COLUMN_PACKAGE_NAME, string);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (apps == null || apps.size() == 0) {
                        continue;
                    }
                    App app = apps.get(0);
                    try {
                        QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
                        queryBuilder.where().eq(Traffic.COLUMN_APP_ID, app.getId()).and().between(Traffic.COLUMN_END_TIMESTAMP, timeList.get(0).getStartTimeStamp(),
                                timeList.get(timeList.size() - 1).getEndTimeStampPlus());
                        PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
                        result = trafficDao.query(preparedQuery);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (result == null || result.size() == 0) {
                        continue;
                    }
                    for (TimeInfo timeInfo : timeList) {
                        for (Traffic traffic : result) {
                            if (traffic.getStartTimestamp() > timeInfo.getStartTimeStamp()
                                    && traffic.getEndTimestamp() <= timeInfo.getEndTimeStampPlus()
                                    && traffic.getStartTimestamp() < timeInfo.getEndTimeStamp()) {
                                if (traffic.getNetwork() != null) {
                                    if (traffic.getNetwork().getNetworkType() != Network.NetworkType.WIFI
                                            && traffic.getApp().getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                                        timeInfo.setOtherBytes(timeInfo.getOtherBytes()
                                                + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                    }
                                    if (traffic.getNetwork().getNetworkType() == Network.NetworkType.WIFI
                                            && traffic.getApp().getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                                        timeInfo.setWifiBytes(timeInfo.getWifiBytes()
                                                + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                    }
                                }
                            }
                        }
                    }
                }
                return timeList;
            }
            if (result != null && result.size() > 0) {
                for (Traffic traffic : result) {
                    for (TimeInfo timeInfo : timeList) {
                        if (traffic.getStartTimestamp() > timeInfo.getStartTimeStamp()
                                && traffic.getEndTimestamp() <= timeInfo.getEndTimeStampPlus()
                                && traffic.getStartTimestamp() < timeInfo.getEndTimeStamp()) {
                            if (traffic.getNetwork() != null) {
                                if (traffic.getNetwork().getNetworkType() == Network.NetworkType.WIFI) {
                                    timeInfo.setWifiBytes(timeInfo.getWifiBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                } else {
                                    timeInfo.setOtherBytes(timeInfo.getOtherBytes()
                                            + traffic.getDownloadBytes() + traffic.getUploadBytes());
                                }
                            }
                        }
                    }
                }
            } else {
                if (timeList != null) {
                    timeList.clear();
                }
            }
            return timeList;
        }
    }
}
