package com.snailgame.cjg.detail;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 应用介绍
 *
 * @author yftx
 */
public class DetailActivity extends SwipeBackActivity {
    private DetailFragment fragment;
    private int gameId;
    private int iFreeArea = AppConstants.FREE_AREA_OUT;
    private int tab;
    private boolean autoDownload;

    // 推广活动用 渠道应用下载地址及MD5
    private String downloadUrl;
    private String MD5;
    private Window mWindow;

    /**
     * @param context      上下文
     * @param appId        应用id
     * @param route        PV用路径记载
     * @param autoDownload 是否自动下载
     * @return
     */
    public static Intent newIntent(Context context, int appId, int[] route, boolean autoDownload) {
        Intent intent = newIntent(context, appId, route);
        intent.putExtra(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, autoDownload);
        return intent;
    }

    /**
     * @param context      上下文
     * @param appId        应用id
     * @param route        PV用路径记载
     * @param autoDownload 是否自动下载
     * @param isOutSideIn  是否外部跳转
     * @param downloadUrl  下载地址(活动推广)
     * @param MD5          MD5(活动推广)
     * @return
     */
    public static Intent newIntent(Context context, int appId, int[] route, boolean autoDownload, boolean isOutSideIn, String downloadUrl, String MD5) {
        Intent intent = newIntent(context, appId, route, autoDownload);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        intent.putExtra(AppConstants.APP_DETAIL_URL, downloadUrl);
        intent.putExtra(AppConstants.APP_DETAIL_MD5, MD5);
        return intent;
    }


    /**
     * @param context 上下文
     * @param appId   应用id
     * @param route   PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int appId, int[] route) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(AppConstants.APP_DETAIL_APPID, appId);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        intent.putExtra(AppConstants.APP_DETAIL_TAB, DetailFragment.TAB_DETAIL);
        return intent;
    }

    /**
     * @param context 上下文
     * @param appId   应用id
     * @param route   PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int appId, int[] route, int tab) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(AppConstants.APP_DETAIL_APPID, appId);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        intent.putExtra(AppConstants.APP_DETAIL_TAB, tab);
        return intent;
    }

    /**
     * @param context 上下文
     * @param appId   应用id
     * @param route   PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int appId, int iFreeArea, int[] route) {
        Intent intent = newIntent(context, appId, route);
        intent.putExtra(AppConstants.KEY_FREEAREA, iFreeArea);
        intent.putExtra(AppConstants.APP_DETAIL_TAB, DetailFragment.TAB_DETAIL);
        return intent;
    }

    /**
     * 打开应用详情页的礼包标签
     *
     * @param context 上下文
     * @param appId   应用id
     * @param route   PV用路径记载
     * @return
     */
    public static Intent callScorePage(Context context, int appId, int[] route) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(AppConstants.APP_DETAIL_APPID, appId);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        intent.putExtra(AppConstants.APP_DETAIL_TAB, DetailFragment.TAB_SPREE);
        return intent;
    }


    /**
     * 打开应用详情页的礼包标签
     *
     * @param context 上下文
     * @param appId   应用id
     * @param route   PV用路径记载
     * @return
     */
    public static Intent callScorePage(Context context, int appId, int[] route, boolean isOutSideIn) {
        Intent intent = callScorePage(context, appId, route);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        translateStatusbar();
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }

    private void translateStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindow = getWindow();
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
        }
    }

    public void setStatusbarColor(int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alpha <= 30)
                mWindow.setStatusBarColor(ResUtil.getColor(R.color.translucent_30_black));
            else {
                mWindow.setStatusBarColor(Color.argb(alpha, 217, 217, 217));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_APP_DETAIL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_APP_DETAIL);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            setStatusbarColor(255);
        super.onBackPressed();
    }

    /**
     * 判断来的请求是否正确
     *
     * @return true 传的游戏id不为0
     */
    @Override
    protected void handleIntent() {
        gameId = getIntent().getIntExtra(AppConstants.APP_DETAIL_APPID, 0);
        mRoute = getIntent().getIntArrayExtra(AppConstants.KEY_ROUTE);
        tab = getIntent().getIntExtra(AppConstants.APP_DETAIL_TAB, DetailFragment.TAB_DETAIL);
        iFreeArea = getIntent().getIntExtra(AppConstants.KEY_FREEAREA, AppConstants.FREE_AREA_OUT);
        autoDownload = getIntent().getBooleanExtra(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, false);
        downloadUrl = getIntent().getStringExtra(AppConstants.APP_DETAIL_URL);
        MD5 = getIntent().getStringExtra(AppConstants.APP_DETAIL_MD5);
        // add for outside in
        isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);

        if (gameId != 0) {
            transFragment(gameId);
        } else {
            finish();
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_detail;
    }

    private void transFragment(int gameId) {
        fragment = DetailFragment.getInstance(gameId, iFreeArea, mRoute, tab, autoDownload, downloadUrl, MD5);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
    }

}