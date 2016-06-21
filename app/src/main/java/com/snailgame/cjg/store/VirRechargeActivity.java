package com.snailgame.cjg.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.store.adapter.VirRechargeFragmentAdapter;
import com.snailgame.cjg.util.ActionBarUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 虚拟代充
 * Created by TAJ_C on 2015/11/27.
 */
public class VirRechargeActivity extends SwipeBackActivity {

    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;


    private VirRechargeFragmentAdapter mAdapter;

    private static final int PAGE_NUM = 2;


    public static Intent newIntent(Context context) {
        return new Intent(context, VirRechargeActivity.class);
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
        return R.layout.home_content_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getString(R.string.store_recharge_title));

        mAdapter = new VirRechargeFragmentAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        tabStrip.setViewPager(mViewPager, PAGE_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_VIR_RECHARGE);

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_VIR_RECHARGE);
    }

}