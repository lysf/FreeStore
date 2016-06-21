package com.snailgame.cjg.seekgame.collection;

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
import com.snailgame.cjg.util.JsonUrl;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 合集详情
 * Created by xixh on 2106/05/26.
 */
public class CollectionActivity extends SwipeBackActivity {
    private int sortType;

    private Window mWindow;

    /**
     * @param context     上下文
     * @param sortType    合集ID
     * @param route       PV用路径记载
     * @param isOutSideIn 是否外部跳转
     * @return
     */
    public static Intent newIntent(Context context, int sortType, int[] route, boolean isOutSideIn) {
        Intent intent = newIntent(context, sortType, route);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }


    /**
     * @param context  上下文
     * @param sortType 合集ID
     * @param route    PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int sortType, int[] route) {
        Intent intent = new Intent(context, CollectionActivity.class);
        intent.putExtra(AppConstants.APP_SORT_TYPE, sortType);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        translateStatusBar();
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }

    private void translateStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindow = getWindow();
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(getResources().getColor(R.color.translucent_15_black));
        }
    }

    public void setStatusBarColor(int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alpha <= 30)
                mWindow.setStatusBarColor(getResources().getColor(R.color.translucent_15_black));
            else {
                mWindow.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
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
            setStatusBarColor(255);
        super.onBackPressed();
    }

    /**
     * 判断来的请求是否正确
     *
     * @return true 传的游戏id不为0
     */
    @Override
    protected void handleIntent() {
        sortType = getIntent().getIntExtra(AppConstants.APP_SORT_TYPE, 0);
        mRoute = getIntent().getIntArrayExtra(AppConstants.KEY_ROUTE);
        // add for outside in
        isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);

        transFragment();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_collection;
    }

    private void transFragment() {
        CollectionFragment fragment = CollectionFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_THEME_DETAIL + sortType + "_", mRoute);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
    }
}