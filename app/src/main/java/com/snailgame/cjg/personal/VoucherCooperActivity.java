package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.ui.AppListFragment;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.personal.adapter.VoucherFragmentAdapter;
import com.snailgame.cjg.personal.model.MyVoucherGoodsModel;
import com.snailgame.cjg.personal.model.MyVoucherModel;
import com.snailgame.cjg.personal.model.VoucherWNModel;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.JsonUrl;

import java.util.ArrayList;
import java.util.List;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 个人中心 -> 我的代金券合作游戏
 * Created by pancl on 2015/4/20.
 */
public class VoucherCooperActivity extends SwipeBackActivity {
    private ActionBar mActionBar;
    private TextView mTitleView;
    private View headerView;
    private int headerHeight;

    @Override
    protected void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            Parcelable parcelable = intent.getParcelableExtra(AppConstants.KEY_VOUCHER);
            if (parcelable instanceof MyVoucherModel.ModelItem) {
                ArrayList<VoucherWNModel.ModelItem> gameList = intent.getParcelableArrayListExtra(AppConstants.KEY_VOUCHER_GAMELIST);
                boolean onceUse = intent.getBooleanExtra(AppConstants.KEY_VOUCHER_ONCEUSR, false);
                initVoucherCooperFragment((MyVoucherModel.ModelItem) parcelable, gameList, onceUse);
            } else if (parcelable instanceof MyVoucherGoodsModel.ModelItem) {
                initVoucherGoodsFragment((MyVoucherGoodsModel.ModelItem) parcelable);
            }
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
        return R.layout.voucher_cooper_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化 商品代金券 Fragment
     *
     * @param item
     */
    private void initVoucherGoodsFragment(MyVoucherGoodsModel.ModelItem item) {
        mActionBar = getSupportActionBar();
        ActionBarUtils.makeCommonActionbar(this, mActionBar, String.valueOf(item.getsVoucherName()));
        mActionBar.getCustomView().setBackgroundResource(R.drawable.voucher_cooper_actionbar_bg);
        initActionBar();

        VoucherGoodsFragment voucherGoodsFragment = VoucherGoodsFragment.create(item);
        VoucherCooperGoodsFragment voucherCooperGoodsFragment = VoucherCooperGoodsFragment.create(item);
        voucherCooperGoodsFragment.setOnScrollListener(new ScrollListener(false));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.voucher_cooper_content_header, voucherGoodsFragment);
        ft.add(R.id.voucher_cooper_content_list, voucherCooperGoodsFragment);
        ft.commitAllowingStateLoss();

        setHeaderHeight(voucherCooperGoodsFragment);
    }

    /**
     * 初始化 游戏代金券 Fragment
     *
     * @param item
     * @param gameList
     */
    private void initVoucherCooperFragment(final MyVoucherModel.ModelItem item, ArrayList<VoucherWNModel.ModelItem> gameList, boolean onceUse) {
        final boolean isVoucherGame = gameList == null;

        mActionBar = getSupportActionBar();
        if (onceUse)
            ActionBarUtils.makeCommonActionbar(this, mActionBar, String.valueOf(item.getsVoucherName()));
        else
            ActionBarUtils.makeCommonActionbar(this, mActionBar, String.valueOf(item.getsVoucherName()),R.drawable.voucher_detail_ic, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(VoucherDetailActivity.newIntent(VoucherCooperActivity.this, item,
                            isVoucherGame ? VoucherFragmentAdapter.TAB_GAME : VoucherFragmentAdapter.TAB_WN));
                }
            });
        mActionBar.getCustomView().setBackgroundResource(R.drawable.voucher_cooper_actionbar_bg);
        initActionBar();

        mRoute = createRoute();
        mRoute[AppConstants.STATISTCS_DEPTH_SIX] = Integer.parseInt(item.getcSource());

        VoucherGameFragment voucherGameFragment = VoucherGameFragment.newIntent(item, onceUse);

        final AppListFragment appListFragment = isVoucherGame ? getMyVoucherFragment(item) : getVoucherWNFragment(gameList);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.voucher_cooper_content_header, voucherGameFragment);
        ft.add(R.id.voucher_cooper_content_list, appListFragment);
        ft.commitAllowingStateLoss();

        setHeaderHeight(appListFragment);
    }

    @NonNull
    private AppListFragment getMyVoucherFragment(MyVoucherModel.ModelItem item) {
        String collection_type = "5" + (TextUtils.equals(item.getcSource(), "2") ? item.getiVoucherId() : item.getcSource());
        String url = JsonUrl.getJsonUrl().JSON_URL_VOUCHER_COOPER + collection_type + "/" + collection_type + "_";
        AppListFragment appListFragment = AppListFragment.create(url, AppConstants.VALUE_VOUCHER, false, mRoute.clone());
        appListFragment.setOnScrollListener(new ScrollListener(true));
        return appListFragment;
    }

    @NonNull
    private AppListFragment getVoucherWNFragment(ArrayList<VoucherWNModel.ModelItem> gameList) {
        AppListFragment appListFragment = AppListFragment.create(null, AppConstants.VALUE_VOUCHER, false, mRoute.clone());
        appListFragment.setOnScrollListener(new ScrollListener(true));

        List<BaseAppInfo> infos = new ArrayList<>(gameList.size());
        for (VoucherWNModel.ModelItem item : gameList) {
            BaseAppInfo info = new BaseAppInfo();
            info.setAppId(item.getnAppId());
            info.setAppName(item.getsAppName());
            info.setApkSize(item.getiSize());
            info.setIcon(item.getcIcon());
            info.setApkUrl(item.getcDownloadUrl());
            info.setVersionCode(item.getiVersionCode());
            info.setPkgName(item.getcPackage());
            info.setcFlowFree(item.getcFlowFree());
            info.setVersionName(item.getcVersionName());
            info.setMd5(item.getcMd5());
            info.setsAppDesc(item.getsAppDesc());
            info.setcAppType(item.getcAppType());
            infos.add(info);
        }
        appListFragment.setBaseAppInfos(infos);
        return appListFragment;
    }

    private void setHeaderHeight(final IHeaderSpace iHeaderSpace) {
        headerView = findViewById(R.id.voucher_cooper_content_header);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    headerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    headerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                headerHeight = headerView.getMeasuredHeight();
                iHeaderSpace.setHeaderSpace(headerHeight);
            }
        });
    }

    public interface IHeaderSpace {
        void setHeaderSpace(int headerHeight);
    }

    private class ScrollListener implements AbsListView.OnScrollListener {
        boolean hasHeaderSpaceView = false;

        public ScrollListener(boolean hasHeaderSpaceView) {
            this.hasHeaderSpaceView = hasHeaderSpaceView;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        private int getScrollY(AbsListView view) {
            View c = view.getChildAt(0);
            if (c == null) {
                return 0;
            }

            int firstVisiblePosition = view.getFirstVisiblePosition();
            int top = c.getTop() - headerHeight;

            return -top + (firstVisiblePosition - (hasHeaderSpaceView ? 1 : 0)) * c.getHeight();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int scrollY = getScrollY(view);
            ViewHelper.setTranslationY(headerView, -scrollY);
        }
    }

    public static Intent newIntent(Context context, MyVoucherModel.ModelItem item, ArrayList<VoucherWNModel.ModelItem> gameList, boolean onceUse) {
        Intent intent = new Intent(context, VoucherCooperActivity.class);
        intent.putExtra(AppConstants.KEY_VOUCHER, item);
        intent.putParcelableArrayListExtra(AppConstants.KEY_VOUCHER_GAMELIST, gameList);
        intent.putExtra(AppConstants.KEY_VOUCHER_ONCEUSR, onceUse);
        return intent;
    }

    public static Intent newIntent(Context context, MyVoucherGoodsModel.ModelItem item) {
        Intent intent = new Intent(context, VoucherCooperActivity.class);
        intent.putExtra(AppConstants.KEY_VOUCHER, item);
        return intent;
    }

    private void initActionBar() {
        mTitleView = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_title);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTitleView != null) {
                    mTitleView.setSelected(true);
                }
            }
        }, 1000);
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 首页
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_PERSONAL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_FIFTH_VOUCHER,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }
}
