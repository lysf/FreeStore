package com.snailgame.cjg.seekgame.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AppListFragment;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.seekgame.collection.CollectionFragment;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.StaticsUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 应用列表
 */
public class AppListActivity extends SwipeBackActivity {
    protected int appModel;

    @Bind(R.id.tv_collect_title)
    TextView mActionTitleView;

    /**
     * @param context     上下文
     * @param appModel    4：合集 其他:分类
     * @param sortType
     * @param title       合集/分类 名
     * @param isShowAlbum 是否显示顶部大图片
     * @param route       PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int appModel, int sortType, String title, boolean isShowAlbum, int[] route, boolean isOutSideIn) {
        Intent intent = newIntent(context, appModel, sortType, title, isShowAlbum, route);
        intent.putExtra(AppConstants.APP_MODEL, appModel);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    /**
     * @param context     上下文
     * @param appModel    4：合集 其他:分类
     * @param sortType
     * @param title       合集/分类 名
     * @param isShowAlbum 是否显示顶部大图片
     * @param route       PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int appModel, int sortType, String title, boolean isShowAlbum, int[] route) {
        Intent intent = newIntent(context, sortType, title, isShowAlbum, route);
        intent.putExtra(AppConstants.APP_MODEL, appModel);
        return intent;
    }

    /**
     * @param context     上下文
     * @param sortType    合集/分类 id
     * @param title       合集/分类 名
     * @param isShowAlbum 是否显示顶部大图片
     * @param route       PV用路径记载
     * @return
     */
    public static Intent newIntent(Context context, int sortType, String title, boolean isShowAlbum, int[] route) {
        Intent intent = new Intent(context, AppListActivity.class);
        intent.putExtra(AppConstants.APP_SORT_TYPE, sortType);
        intent.putExtra(AppConstants.COMPANY_NAME, title);
        intent.putExtra(AppConstants.APP_SORT_SHOW_ALBUM, isShowAlbum);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pv统计
        StaticsUtils.onPV(mRoute);
    }

    @Override
    protected void handleIntent() {
        Intent intent = getIntent();
        appModel = intent.getIntExtra(AppConstants.APP_MODEL, -1);
        int sortType = intent.getIntExtra(AppConstants.APP_SORT_TYPE, -1);
        boolean isShowHeader = intent.getBooleanExtra(AppConstants.APP_SORT_SHOW_ALBUM, false);
        // add for outside in
        isOutSideIn = intent.getBooleanExtra(KEY_IS_OUTSIDE_IN, false);

        View view = ActionBarUtils.makeDetailActionbarStyle(this, intent.getStringExtra(AppConstants.COMPANY_NAME), true, false, false);

        ButterKnife.bind(this, view);
        mRoute = intent.getIntArrayExtra(AppConstants.KEY_ROUTE);
        mRoute[AppConstants.STATISTCS_DEPTH_SIX] = sortType;

        String url;
        int[] route;
        Fragment fragment;
        switch (appModel) {
            // 免流量专区
            case AppConstants.VALUE_FREEAREA:
                mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_FREEAREA;
                route = mRoute.clone();
                url = JsonUrl.getJsonUrl().JSON_URL_FREE_AREA + "_";
                fragment = AppListFragment.create(url, AppConstants.VALUE_FREEAREA, isShowHeader, route);
                break;
            // 合集
            case AppConstants.VALUE_COLLECTION:
                mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_THEME;
                route = mRoute.clone();
                url = JsonUrl.getJsonUrl().JSON_URL_THEME_DETAIL + sortType + "_";
//                fragment = AppListFragment.create(url, AppConstants.VALUE_COLLECTION, isShowHeader, route);
                fragment = CollectionFragment.getInstance(url, route);
                break;
            // 标签
            case AppConstants.VALUE_CATEGORY_TAG:
                mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_SORT;
                route = mRoute.clone();
                url = JsonUrl.getJsonUrl().JSON_URL_CATEGORY_TAG_DETAIL + sortType + "_";
                fragment = AppListFragment.create(url, AppConstants.VALUE_CATEGORY_TAG, isShowHeader, route);
                break;
            // 分类
            default:
                mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_SORT;
                route = mRoute.clone();
                url = JsonUrl.getJsonUrl().JSON_URL_CATEGORY_CAT_DETAIL + "?iCategoryId=" + sortType + "&iPlatformId=" + AppConstants.PLATFORM_ID;
                fragment = AppListFragment.create(url, AppConstants.VALUE_CATEGORY, isShowHeader, route);
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commitAllowingStateLoss();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActionTitleView.setSelected(true);
            }
        }, 1000);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_COLLECTON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_COLLECTON);
    }
}
