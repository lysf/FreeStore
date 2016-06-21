package third.com.snail.trafficmonitor.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.UmengAnalytics;
import com.umeng.analytics.MobclickAgent;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.bean.AppTraffic;
import third.com.snail.trafficmonitor.engine.data.bean.FlowInfo;
import third.com.snail.trafficmonitor.engine.data.bean.TimeInfo;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.receiver.TrafficDataUpdater;
import third.com.snail.trafficmonitor.engine.util.CalenderDayTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;
import third.com.snail.trafficmonitor.ui.helper.ActionBarHelper;
import third.com.snail.trafficmonitor.ui.loader.MonthAppTrafficLoader;
import third.com.snail.trafficmonitor.ui.widget.ILoading;
import third.com.snail.trafficmonitor.ui.widget.SummaryPieChart;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static third.com.snail.trafficmonitor.engine.util.LogWrapper.makeTag;

/**
 * Created by kevin on 14/12/15.
 */
public class SummaryActivity extends SwipeBackActivity implements LoaderManager.LoaderCallbacks<List<AppTraffic>>, ILoading {
    private final String TAG = makeTag(SummaryActivity.class);

    @Bind(R.id.chart_view)
    SummaryPieChart mChartView;
    @Bind(R.id.total_container)
    View mTotalContainer;
    @Bind(R.id.progress_bar)
    View mProgress;
    @Bind(R.id.tv_total)
    TextView mTotalTv;
    @Bind(R.id.mobile_cost_tv)
    TextView mMobileCostTv;
    @Bind(R.id.wifi_cost_tv)
    TextView mWifiCostTv;
    @Bind(R.id.wifi_cost_unit_tv)
    TextView mWifiCostUnitTv;
    @Bind(R.id.line_chart)
    LineChart mLineChartView;

    @Bind(R.id.traffic_root_function_container)
    View mRootFunctionContainer;
    @Bind(R.id.traffic_root_function_desc)
    TextSwitcher mFunctionDesc;
    @Bind(R.id.traffic_root_function_cb)
    CheckBox mFunctionCheckBox;

    private TrafficDataUpdater mReceiver;
    private long mobileBytes;
    private int month, hour, year, date;
    /**
     * 当天所有时段的移动流量列表
     */
    private List<TimeInfo> timeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        month = c.get(Calendar.MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        year = c.get(Calendar.YEAR);
        date = c.get(Calendar.DATE);
        ActionBarHelper.makeCommonActionBar(this, String.format(getString(R.string.summary_title), month + 1));
        mFunctionDesc.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(SummaryActivity.this);
                tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                        , FrameLayout.LayoutParams.MATCH_PARENT));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                return tv;
            }
        });
        initCharView();
        initRootFunctionViews();
        initLineChartView();
        prepareLoading();
        showLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_SUMMARY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_SUMMARY);
    }

    @OnClick(R.id.month_detail)
    void monthClick() {
        //本月流量使用情况
        Intent intent;
        if (mobileBytes > 0) {
            intent = TimeBucketActivity.newTimeBucket(SummaryActivity.this, 0, null, null);
        } else {
            intent = TimeBucketActivity.newTimeBucket(SummaryActivity.this, 1, null, null);
        }
        startActivity(intent);
    }

    @OnClick(R.id.app_detail)
    void appClick() {
        //本月应用使用详情
        startActivity(new Intent(SummaryActivity.this, AppRankActivity.class));
    }

    private void initRootFunctionViews() {
        if (CommandHelper.hasRoot()) {
            mRootFunctionContainer.setVisibility(View.VISIBLE);
            boolean opened = SpManager.getInstance(this).getBoolean(SpManager.SpData.OPEN_ROOT_ADVANCED_FUNCTION);
            mFunctionCheckBox.setChecked(opened);
            mFunctionDesc.setText(getString(opened ? R.string.traffic_root_function_open : R.string.traffic_root_function_close));

            mFunctionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mFunctionDesc.setText(getString(isChecked ? R.string.traffic_root_function_open : R.string.traffic_root_function_close));
                    SpManager.getInstance(SummaryActivity.this).putBoolean(SpManager.SpData.OPEN_ROOT_ADVANCED_FUNCTION, isChecked);
                    if (isChecked) {
                        CommandHelper.requestRoot();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.engine_summary_activity_layout;
    }

    private void loadPieCharData(long wifi, long mobile) {
        ArrayList<Entry> yValue = new ArrayList<>();
        float mobilePercent = mobile / (float) (mobile + wifi) * 100f;
        LogWrapper.d(TAG, "W: " + wifi + " M: " + mobile + " %: " + mobilePercent);
        yValue.add(new Entry(100f - mobilePercent, 1));
        yValue.add(new Entry(mobilePercent, 2));
        ArrayList<String> xValue = new ArrayList<>();
        xValue.add("");
        xValue.add("");

        ArrayList<SummaryPieChart.ExtraInfo> extraInfos = new ArrayList<>();
        extraInfos.add(new SummaryPieChart.ExtraInfo(TrafficTool.getCost(wifi), getString(R.string.wifi_cost_subtitle),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, getResources().getDisplayMetrics()),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()),
                getResources().getColor(R.color.text_gray)));
        extraInfos.add(new SummaryPieChart.ExtraInfo(TrafficTool.getCost(mobile), getString(R.string.mobile_cost_subtitle),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, getResources().getDisplayMetrics()),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()),
                getResources().getColor(R.color.chart_mobile_red)));

        PieDataSet set = new PieDataSet(yValue, "Y");
        set.setSliceSpace(0f);
        set.setColors(new int[]{R.color.chart_wifi_green, R.color.chart_mobile_red}, this);
        PieData data = new PieData(xValue, set);
        mChartView.setData(data);
        mChartView.setDrawExtra(true);
        mChartView.setExtraInfo(extraInfos);
        mChartView.setRotationAngle(90f + (180f * mobilePercent / 100f));
        mChartView.invalidate();
        LogWrapper.d(TAG, "loadPieCharData " + mChartView.getRotationAngle());
    }

    private void initCharView() {
        mChartView.setBackgroundColor(getResources().getColor(R.color.chart_background));
        mChartView.setHoleRadius(56f);
        mChartView.setDrawHoleEnabled(true);
        mChartView.setCenterText("");
        mChartView.setDescription("");
        mChartView.setTouchEnabled(false);
        mChartView.setUnit("%");
        mChartView.setDrawUnitsInChart(true);
        mChartView.setDrawLegend(false);
        mChartView.setDrawExtra(false);
        mChartView.setNoDataText("");
        mChartView.setExtraSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22f, getResources().getDisplayMetrics()));
        mChartView.setValueTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, getResources().getDisplayMetrics()));
        mChartView.setLinePaint(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()),
                getResources().getColor(R.color.text_gray));
//        mChartView.endbleDebug();
    }

    private void initLineChartView() {
        //设置显示坐标具体参数
        mLineChartView.setDrawYValues(true);
        mLineChartView.setDrawGridBackground(false);
        mLineChartView.setDescription("");
        mLineChartView.setDragScaleEnabled(true);
        //设置不显示高亮的显示效果
        mLineChartView.setHighlightEnabled(false);
        //设置不显示单位
        mLineChartView.setDrawUnitsInChart(false);
        mLineChartView.setBackgroundColor(getResources().getColor(R.color.white));
        //设置不显示图例
        mLineChartView.setDrawLegend(false);
        //设置不能滑动和点击
        mLineChartView.setTouchEnabled(false);
        //标注当前时间点高亮
        mLineChartView.setmIndicesToHightlight(getCurrentTimeTickIndex(hour));
        //设置无数据样式
        mLineChartView.setNoDataText(getResources().getString(R.string.no_data));
        XLabels xl = mLineChartView.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setAdjustXLabels(true);

        YLabels y1 = mLineChartView.getYLabels();
        y1.setDrawUnitsInYLabel(false);
    }

    /**
     * 将当天时间分段，并且组成list
     */
    private List<TimeInfo> getTimeCutList() throws SQLException {
        CalenderDayTools calendarDayTools = new CalenderDayTools(this, year,
                month, date);
        List<TimeInfo> timeList = calendarDayTools.measure();
        return timeList;
    }

    private void setLineChartView(List<TimeInfo> list) throws SQLException {
        //如果当天移动流量为空那么我们就重新给他赋值
        if (list == null) {
            list = getTimeCutList();
        }
        //判断当天是不是有流量为空
        boolean isMobileTrafficEmpty = true;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getOtherBytes() != 0) {
                isMobileTrafficEmpty = false;
                break;
            }
        }
        LogWrapper.d("setLineChartView");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            xVals.add((i * 4) + "-" + (i + 1) * 4);
        }
        ArrayList<Entry> yVals = new ArrayList<>();
        DecimalFormat sDf = new DecimalFormat("0.00");
        for (int i = 0; i < list.size(); i++) {
            String s = sDf.format((double) list.get(i).getOtherBytes() / 1024 / 1024);
            float sNum = 0;
            try {
                sNum = Float.parseFloat(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            yVals.add(new Entry(sNum, i));
        }
        LineDataSet set = new LineDataSet(yVals, "DataSet");
        set.setLineWidth(1f);
        set.setCircleSize(4.5f);
        set.setColor(Color.rgb(249, 116, 117));
        set.setCircleColor(Color.rgb(117, 192, 74));
        set.setDrawFilled(true);
        set.setFillColor(Color.rgb(248, 234, 234));
        LineData mData = new LineData(xVals, set);
        mLineChartView.setData(mData);
        //如果当天移动流量全都为0，那么就将Y轴的范围设为0-100
        if (isMobileTrafficEmpty) {
            mLineChartView.setYRange(0, 10, true);
        }
        Legend l = mLineChartView.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLineChartView.animateX(1000);
        mLineChartView.invalidate();
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
    public Loader<List<AppTraffic>> onCreateLoader(int i, Bundle bundle) {
        return new MonthAppTrafficLoader(this, EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE,
                EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE);
    }

    @Override
    public void onLoadFinished(Loader<List<AppTraffic>> listLoader, List<AppTraffic> appTraffics) {
        long wifi = 0L;
        long mobile = 0L;
        /**
         * 当前app（wifi_interface）在今天各时间段里使用的wifi流量
         * */
        List<TimeInfo> timeListOfwifi = null;
        for (AppTraffic t : appTraffics) {
            if (t.packageName.equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                mobile += (t.rxBytes + t.txBytes - t.wifiTxBytes - t.wifiRxBytes);
                timeList = t.trafficInDayList;
            } else {
                timeListOfwifi = t.trafficInDayList;
                wifi += (t.wifiRxBytes + t.wifiTxBytes);
            }
        }
        long costWifiBytesInDay = 0;
        long costMobileBytesInDay = 0;
        if (timeList != null) {
            for (TimeInfo timeInfo : timeList) {
                costMobileBytesInDay += timeInfo.getOtherBytes();
            }
        }

        if (timeListOfwifi != null) {
            for (TimeInfo timeInfo : timeListOfwifi) {
                costWifiBytesInDay += timeInfo.getWifiBytes();
            }
        }
        mobileBytes = mobile;
        if (appTraffics.size() == 0 || wifi + mobile == 0) {
            dismissLoading(false);
            return;
        }
        dismissLoading(true);
        mTotalTv.setText(TrafficTool.getCost(wifi + mobile));
        mMobileCostTv.setText(TrafficTool.getCost(costMobileBytesInDay));
        FlowInfo info = TrafficTool.getCostLong(costWifiBytesInDay);
        mWifiCostTv.setText(info.getBytes());
        mWifiCostUnitTv.setText(info.getBytesType());
        loadPieCharData(wifi, mobile);
        try {
            setLineChartView(timeList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<AppTraffic>> listLoader) {
    }

    @Override
    public void prepareLoading() {
        mReceiver = new TrafficDataUpdater(new TrafficDataUpdater.OnTrafficListener() {
            @Override
            public void onDataUpdated() {
                loadData();
            }
        });
        registerReceiver(mReceiver, TrafficDataUpdater.getIntentFilter());
        TrafficDataUpdater.requestUpdate(this);
    }

    @Override
    public void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
        mTotalContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void dismissLoading(boolean hasData) {
        mProgress.setVisibility(View.GONE);
        mTotalContainer.setVisibility(View.VISIBLE);
        if (hasData) {
        } else {
        }
    }

}
