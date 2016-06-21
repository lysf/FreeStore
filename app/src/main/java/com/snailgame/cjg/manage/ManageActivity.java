package com.snailgame.cjg.manage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.downloadmanager.GameManageActivity;
import com.snailgame.cjg.downloadmanager.adapter.GameManageFragmentAdapter;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.DownloadManageChangeEvent;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.manage.model.TreasureBoxOfflineInfo;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.com.snail.trafficmonitor.engine.data.bean.ProcessBean;
import third.com.snail.trafficmonitor.engine.util.process.ProcessUtil;
import third.com.snail.trafficmonitor.ui.widget.RiseNumberTextview.RiseNumberTextView;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by lic on 2015/9/22.
 * 管理界面
 */
public class ManageActivity extends SwipeBackActivity implements MyGameDaoHelper.MyGameCallback {
    private final static String TAG = ManageActivity.class.getSimpleName();

    @Bind(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @Bind(R.id.tv_riseNumber)
    RiseNumberTextView riseNumber;
    @Bind(R.id.app_update_number)
    TextView appUpdateNumber;
    @Bind(R.id.grid)
    FullGridView fullGridView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.no_update)
    TextView noUpdateTextView;
    @Bind(R.id.tv_score)
    TextView scoreTextView;
    @Bind(R.id.score_low_tips)
    TextView scoreLowTips;
    private AsyncTask queryTask, downloadChangeTask;
    private ProcessUtil mUtil;
    private List<TreasureBoxOfflineInfo> mTreasureBoxInfoList;
    private long beforeMemory;
    private int cleanScore;
    private boolean isCleanInit;
    private boolean inCleaning = true;

    private List<ProcessBean> currentProcessList;
    private long currentMemory;
    private MsgHandler handler = new MsgHandler(this);


    /**
     * 打开的intent
     *
     * @param context
     */
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ManageActivity.class);
        return intent;
    }


    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        handMsgNum(event.getAppInfoList());
    }

    @Subscribe
    public void updateChange(UpdateChangeEvent updateChangeEvent) {
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(ManageActivity.this, ManageActivity.this);
    }

    @Subscribe
    public void downloadManageChange(DownloadManageChangeEvent downloadManageEvent) {
        downloadChangeTask = MyGameDaoHelper.queryForAppInfoInThread(ManageActivity.this, this);
    }

    @Override
    public void Callback(List<AppInfo> appInfos) {
        handMsgNum(appInfos);
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
        return R.layout.activity_manage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeGameManageActionbar(this, getSupportActionBar(), R.string.manage);
        MainThreadBus.getInstance().register(this);
        // 防止scrollview进去不在起点
        scrollView.smoothScrollTo(0, 20);
        //设置底部百宝箱的grid
        if (ListUtils.isEmpty(GlobalVar.getInstance().getMTreasureBoxInfoList())) {
            setGridOfflineData();
            ManageGridOffLineAdapter manageGridOffLineAdapter = new ManageGridOffLineAdapter(this, mTreasureBoxInfoList);
            fullGridView.setAdapter(manageGridOffLineAdapter);
        } else {
            ManageGridAdapter manageGridAdapter = new ManageGridAdapter(this, GlobalVar.getInstance().getMTreasureBoxInfoList());
            fullGridView.setAdapter(manageGridAdapter);

        }
        initViewFlipper();
        initRiseNumber();
        Timer timer = new Timer();//实例化Timer类
        //延迟设置分数控件, 防止卡顿
        timer.schedule(new TimerTask() {
            public void run() {
                mUtil = new ProcessUtil(ManageActivity.this);
                currentProcessList = mUtil.getRunningProcess();
                currentMemory = mUtil.getMemory();
                handler.sendEmptyMessage(1);
                this.cancel();
            }
        }, 300);
    }

    /**
     * 计算分数并且设置分数控件显示的值
     */
    private void runRiseNumber() {
        if (Math.abs(currentMemory - SharedPreferencesUtil.getInstance().getLastMemory()) > (long) 50000000) {
            long processMemory = 0;
            for (ProcessBean processBean : currentProcessList) {
                processMemory += processBean.getMemory();
            }
            long nextMemory;
            if (processMemory < (long) 1000000) {
                nextMemory = currentMemory + (processMemory * 1000);
            } else {
                nextMemory = currentMemory + (processMemory * 100);
            }
            double score = ((double) currentMemory / (double) nextMemory) * (double) 100;
            if (score > 100) {
                score = 100;
            }
            riseNumber.withNumber((int) score);
            cleanScore = (int) score;
        } else {
            riseNumber.withNumber(SharedPreferencesUtil.getInstance().getLastScore());
            cleanScore = SharedPreferencesUtil.getInstance().getLastScore();
        }
        isCleanInit = true;
        scoreTextView.setVisibility(View.VISIBLE);
        riseNumber.start();
        inCleaning = true;
    }

    /**
     * 初始化分数控件
     */
    private void initRiseNumber() {
        riseNumber.setDuration(1000);
        //修改字体
        String fontPath = "fonts/Roboto-Light.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        riseNumber.setTypeface(tf);
        riseNumber.setOnEnd(new RiseNumberTextView.EndListener() {
            @Override
            public void onEndFinish() {
                if (!isCleanInit) {
                    long cleanMemory = Math.abs(mUtil.getMemory() - beforeMemory);
                    scoreLowTips.setText(getString(R.string.has_scored_memoary, FileUtil.formatShortFileSize(ManageActivity.this, cleanMemory)));
                }
                scoreLowTips.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cleanScore < 80) {
                            scoreLowTips.setText(getString(R.string.score_low_tips));
                        } else if (cleanScore < 100) {
                            scoreLowTips.setText(getString(R.string.score_normal_tips));
                        } else {
                            scoreLowTips.setText(getString(R.string.score_high_tips));
                        }
                    }
                }, 1000);
                inCleaning = false;
            }
        });

    }


    /**
     * 设置如果获取百宝箱失败的话默认的图标
     */
    private void setGridOfflineData() {
        int[] resIds = {R.drawable.traffic_statistics, R.drawable.traffic_control, R.drawable.app_manage, R.drawable.recommend};
        String[] names = ResUtil.getStringArray(R.array.manage_grid_offline_array);
        TreasureBoxOfflineInfo treasureBoxOfflineInfo;
        mTreasureBoxInfoList = new ArrayList<>();
        for (int i = 0; i < resIds.length; i++) {
            treasureBoxOfflineInfo = new TreasureBoxOfflineInfo(i, resIds[i], names[i]);
            mTreasureBoxInfoList.add(treasureBoxOfflineInfo);
        }
    }

    /**
     * 刷新应用更新数量
     */
    private void handMsgNum(List<AppInfo> infos) {
        initUpdateLayout(infos);
    }

    private void initViewFlipper() {
        Animation lInAnim = AnimationUtils.loadAnimation(this, R.anim.push_top_in);
        Animation lOutAnim = AnimationUtils.loadAnimation(this, R.anim.push_top_out);
        viewFlipper.setInAnimation(lInAnim);
        viewFlipper.setOutAnimation(lOutAnim);
//        viewFlipper.setAutoStart(true);
        //设置切换时间为1.5秒
        viewFlipper.setFlipInterval(1500);
    }

    private void initUpdateLayout(List<AppInfo> appInfos) {
        List<AppInfo> updateSubApps = GameManageUtil.getUpdateInfos(ManageActivity.this, appInfos, false);
        Collections.sort(updateSubApps, comparator);
        List<AppInfo> updateApps;
        //只保留五条更新
        if (updateSubApps.size() > 5) {
            updateApps = updateSubApps.subList(0, 5);
        } else {
            updateApps = updateSubApps;
        }
        int updatecount = updateSubApps.size();
        if (updatecount > 0) {
            viewFlipper.setVisibility(View.VISIBLE);
            noUpdateTextView.setVisibility(View.GONE);
            appUpdateNumber.setVisibility(View.VISIBLE);
        } else {
            viewFlipper.setVisibility(View.GONE);
            noUpdateTextView.setVisibility(View.VISIBLE);
            appUpdateNumber.setVisibility(View.GONE);
            return;
        }
        appUpdateNumber.setText(String.valueOf(updatecount));
        viewFlipper.removeAllViews();
        for (AppInfo appInfo : updateApps) {
            View view = LayoutInflater.from(ManageActivity.this).inflate(R.layout.activity_manage_app_update, null);
            TextView textView = ButterKnife.findById(view, R.id.people_update_num);
            FSSimpleImageView imageView = ButterKnife.findById(view, R.id.app_icon);
            int installNum = appInfo.getTotalIntsallNum() * 51;
            if (installNum > 10000) {
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                double num = (double) installNum / (double) 10000;
                textView.setText(ResUtil.getString(R.string.num_people_ten_thousand_update, decimalFormat.format(num)));
            } else if (installNum < 1000) {
                long num = Math.round(Math.random() * 500 + 1000);
                textView.setText(ResUtil.getString(R.string.num_people_update, num));
            } else {
                textView.setText(ResUtil.getString(R.string.num_people_update, installNum));
            }
            imageView.setImageUrl(appInfo.getIcon());
            viewFlipper.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        //如果只有一条更新数据则不开始动画
        if (updatecount > 1) {
            if (!viewFlipper.isFlipping()) {
                viewFlipper.startFlipping();
            }
        } else {
            viewFlipper.stopFlipping();
        }
    }

    /**
     * 按照下载量对可更新应用实行从大到小的排序
     */
    private Comparator<AppInfo> comparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if (lhs.getTotalIntsallNum() > rhs.getTotalIntsallNum()) {
                return -1;
            } else if (lhs.getTotalIntsallNum() < rhs.getTotalIntsallNum()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    /**
     * 一键清理内存并且跑分
     */
    @OnClick(R.id.score_low_tips)
    void cleanMemeory() {
        if (inCleaning || mUtil == null)
            return;
        MobclickAgent.onEvent(this, UmengAnalytics.EVENT_CLEAR_MEMORY);
        new Thread(new Runnable() {
            @Override
            public void run() {
                beforeMemory = mUtil.getMemory();
                List<ProcessBean> list = mUtil.getRunningProcess();
                mUtil.killProgress(list);
                SharedPreferencesUtil.getInstance().setLastMemory(mUtil.getMemory());
                SharedPreferencesUtil.getInstance().setLastScore(100);
            }
        }).start();
        scoreLowTips.setText(getString(R.string.in_speed_up));
        cleanScore = 100;
        isCleanInit = false;
        riseNumber.withNumber(100);
        riseNumber.start();
        inCleaning = true;
    }

    @OnClick(R.id.score_layout)
    void scoreClick() {
        cleanMemeory();
    }

    @OnClick(R.id.update_layout)
    void goUpdateActivity() {
        startActivity(GameManageActivity.newIntentForShowFirstTab(ManageActivity.this, GameManageFragmentAdapter.TAB_UPDATE));
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_NOTICE);
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(ManageActivity.this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_NOTICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queryTask != null)
            queryTask.cancel(true);
        if (downloadChangeTask != null)
            downloadChangeTask.cancel(true);
        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    static class MsgHandler extends Handler {
        private WeakReference<ManageActivity> mActivity;

        public MsgHandler(ManageActivity activity) {
            this.mActivity = new WeakReference<ManageActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ManageActivity activity = mActivity.get();
            if (activity != null) {
                activity.runRiseNumber();
            }
            super.handleMessage(msg);
        }
    }
}
