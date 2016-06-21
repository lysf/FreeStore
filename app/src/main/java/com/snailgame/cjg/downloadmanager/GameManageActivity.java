package com.snailgame.cjg.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.downloadmanager.adapter.GameManageFragmentAdapter;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.DownloadManageChangeEvent;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.UIUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-11
 * Time : 下午2:56
 * Description :游戏管理：下在管理。已安装，更新
 */
public class GameManageActivity extends SwipeBackActivity implements MyGameDaoHelper.MyGameCallback {

    public static Intent newIntent(Context context) {
        return new Intent(context, GameManageActivity.class);
    }

    public static Intent newIntent(Context context, boolean isNotificationTag) {
        Intent intent = newIntent(context);
        intent.putExtra(AppConstants.IS_NOTIFICATION_TAG, isNotificationTag);
        return intent;
    }

    public static Intent newIntent(Context context, int flags) {
        Intent intent = newIntent(context);
        intent.setFlags(flags);
        return intent;
    }

    public static Intent newIntentForShowFirstTab(Context context, int tabIndex) {
        Intent intent = newIntent(context);
        intent.putExtra(AppConstants.FIRST_SHOW_TAB_INDEX, tabIndex);
        return intent;
    }


    public static final String TAG = GameManageActivity.class.getSimpleName();
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    private GameManageFragmentAdapter mAdapter;
    @Bind(R.id.benifit_msg)
    TextView mUpdateNumView;
    private Boolean isNotif;

    private static final int PAGE_NUM = 3;
    private AsyncTask downloadChangeTask, updateChangeTask, queryTask;

    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        handMsgNum(event.getAppInfoList());
    }


    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        mAdapter = new GameManageFragmentAdapter(getSupportFragmentManager(), ResUtil.getStringArray(R.array.game_manage));
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(PAGE_NUM);
        tabStrip.setViewPager(mViewPager, PAGE_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
                if (position != GameManageFragmentAdapter.TAB_DOWNLOAD)
                    resetMultiSelectableFragmentStatus(GameManageFragmentAdapter.TAB_DOWNLOAD);
                if (position != GameManageFragmentAdapter.TAB_INSTALLED)
                    resetMultiSelectableFragmentStatus(GameManageFragmentAdapter.TAB_INSTALLED);
            }
        });
        int showTabIndex = getIntent().getIntExtra(AppConstants.FIRST_SHOW_TAB_INDEX, GameManageFragmentAdapter.TAB_DOWNLOAD);
        mViewPager.setCurrentItem(showTabIndex);
        // ActionBar
        ActionBarUtils.makeGameManageActionbar(this, getSupportActionBar(), R.string.game_label);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_game_manager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(!GameManageUtil.checkIfLaunchFromStatusBar(this));
        init();

        MainThreadBus.getInstance().register(this);
    }

    private void init() {
        getIsNotificationTag(getIntent());
    }


    /**
     * 取消可多选界面多选状态
     */
    private void resetMultiSelectableFragmentStatus(int fragmentTab) {
        IMultiSelectableFragment iMultiSelectableFragment = getMultiSelectableFragment(fragmentTab);
        if (iMultiSelectableFragment != null) {
            iMultiSelectableFragment.finishActionMode();
        }
    }

    /**
     * 是否可多选界面在多选状态
     *
     * @return
     */
    private boolean isFragmentInActionMode(int fragmentTab) {
        IMultiSelectableFragment iMultiSelectableFragment = getMultiSelectableFragment(fragmentTab);
        return iMultiSelectableFragment != null && iMultiSelectableFragment.isActionMode();
    }

    private IMultiSelectableFragment getMultiSelectableFragment(int fragmentTab) {
        return (IMultiSelectableFragment) getSupportFragmentManager().
                findFragmentByTag(getFragmentName(R.id.viewpager, fragmentTab));
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_MANAGE);
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(GameManageActivity.this, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_MANAGE);
    }

    /**
     * launchMode为singleTask的时候，通过Intent启到一个Activity,如果系统已经存在一个实例，系统就会将请求发送到这个实例上，
     * 但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法，而是调用onNewIntent方法
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIsNotificationTag(intent);
    }

    private void getIsNotificationTag(Intent intent) {
        isNotif = intent.getBooleanExtra(AppConstants.IS_NOTIFICATION_TAG, false);
        if (isNotif) {
            UIUtil.changeViewPagerTabWithOutAnimation(mViewPager, 2);
            startService(SnailFreeStoreService.newIntent(this,
                    SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME, AppConstants.CANCLE_NOTIFY));
        }
    }

    @Override
    public void onBackPressed() {
        if (isFragmentInActionMode(GameManageFragmentAdapter.TAB_DOWNLOAD)) {
            resetMultiSelectableFragmentStatus(GameManageFragmentAdapter.TAB_DOWNLOAD);
        } else if (isFragmentInActionMode(GameManageFragmentAdapter.TAB_INSTALLED)) {
            resetMultiSelectableFragmentStatus(GameManageFragmentAdapter.TAB_INSTALLED);
        } else {
            GameManageUtil.forwardToHomeOrCloseActivity(this);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void updateChange(UpdateChangeEvent updateChangeEvent) {
        DownloadManageFragment downloadManageFragment = (DownloadManageFragment) getSupportFragmentManager()
                .findFragmentByTag(getFragmentName(R.id.viewpager, GameManageFragmentAdapter.TAB_DOWNLOAD));
        downloadManageFragment.queryTask();
        updateChangeTask = MyGameDaoHelper.queryForAppInfoInThread(GameManageActivity.this, this);
    }

    @Override
    public void Callback(List<AppInfo> appInfos) {
        handMsgNum(appInfos);
    }

    private void handMsgNum(List<AppInfo> infos) {
        int num = GameManageUtil.getUpdateInfos(GameManageActivity.this, infos, false).size();
        if (num > 0) {
            mUpdateNumView.setText(String.valueOf(num));
            mUpdateNumView.setVisibility(View.VISIBLE);
        } else {
            mUpdateNumView.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void downloadManageChange(DownloadManageChangeEvent downloadManageEvent) {
        UpdateFragment updateFragment = (UpdateFragment) getSupportFragmentManager()
                .findFragmentByTag(getFragmentName(R.id.viewpager, GameManageFragmentAdapter.TAB_UPDATE));
        updateFragment.loadData();
        downloadChangeTask = MyGameDaoHelper.queryForAppInfoInThread(GameManageActivity.this, this);
    }

    private String getFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadChangeTask != null)
            downloadChangeTask.cancel(true);
        if (updateChangeTask != null)
            updateChangeTask.cancel(true);
        if (queryTask != null)
            queryTask.cancel(true);
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * 用于统计路径
     */
    public static int[] createRoute() {
        // 游戏管理
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_MANAGE,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    protected interface IMultiSelectableFragment {
        void finishActionMode();

        boolean isActionMode();
    }
}