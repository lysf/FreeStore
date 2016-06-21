package com.snailgame.cjg.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.util.ActionBarUtils;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 资讯
 * Created by sunxy on 2014/9/26.
 */
public class AppNewsActivity extends SwipeBackActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AppNewsActivity.class);
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
        return R.layout.app_news_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeGameManageActionbar(this, getSupportActionBar(), R.string.app_news_title);
        mRoute=createRoute();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.app_news_content, AppNewsFragment.getInstance(mRoute));
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_APP_NEWS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_APP_NEWS);
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 首页
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_HOMEPAGE,
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
}