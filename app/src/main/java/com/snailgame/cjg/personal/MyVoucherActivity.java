package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.common.widget.RippleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.personal.adapter.VoucherFragmentAdapter;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.model.JumpInfo;
import com.snailgame.fastdev.image.BitmapManager;

import java.util.ArrayList;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 个人中心 -> 我的代金券
 * Created by pancl on 2015/4/20.
 */
public class MyVoucherActivity extends SwipeBackActivity {

    public static final int TAB_WONIU = 1;
    public static final int TAB_TUTU = 2;
    public static final int TAB_KUWAN = 3;
    public static final int TAB_YOUXI = 3;

    private int currentTab = TAB_WONIU;

    public static Intent newIntent(Context context) {
        return new Intent(context, MyVoucherActivity.class);
    }

    public static Intent newIntent(Context context, boolean isOutSideIn) {
        Intent intent = new Intent(context, MyVoucherActivity.class);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    public static Intent newIntent(Context context, int tab) {
        Intent intent = new Intent(context, MyVoucherActivity.class);
        intent.putExtra(AppConstants.KEY_VOUCHER_TAB, tab);
        return intent;
    }

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    RippleImageView my_voucher_advise;
    private JumpInfo voucherAdInfo;
    private static final int PAGE_NUM = 4;
    private int tabNum;

    @Override
    protected void handleIntent() {
        if (getIntent() != null) {
            currentTab = getIntent().getIntExtra(AppConstants.KEY_VOUCHER_TAB, TAB_WONIU);
            // add for outside in
            isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);
        }
    }

    @Override
    protected void initView() {
        boolean isShowWoniu = PersistentVar.getInstance().getSystemConfig().isVoucherWoniuSwitch();
        String showniuTitle = PersistentVar.getInstance().getSystemConfig().getVoucherWoniuTitle();
        ArrayList<String> titles = new ArrayList<>();
        if (isShowWoniu) {
            titles.add(showniuTitle);
        }

        titles.add(getString(R.string.voucher_tutu_title));
        titles.add(getString(R.string.voucher_kuwan_title));
        titles.add(getString(R.string.voucher_youxi_title));
        tabNum = isShowWoniu ? PAGE_NUM : PAGE_NUM - 1;
        VoucherFragmentAdapter mAdapter = new VoucherFragmentAdapter(getSupportFragmentManager(), titles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(tabNum);
        tabStrip.setViewPager(mViewPager, tabNum, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
            }
        });

        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.persion_my_voucher);

        if (currentTab >= TAB_WONIU && currentTab <= TAB_YOUXI) {
            mViewPager.setCurrentItem(isShowWoniu ? currentTab - 1 : currentTab - 2);
        }

        my_voucher_advise = (RippleImageView) findViewById(R.id.my_voucher_advise);
        initAdvise();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.my_voucher_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化列表下广告
     */
    private void initAdvise() {
        if (PersistentVar.getInstance().getSystemConfig().getVoucherAdStstus() == 1) {
            creatAdInfo();
            BitmapManager.showImg(PersistentVar.getInstance().getSystemConfig().getVoucherAdImgUrl(),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            if (response.getBitmap() == null) {
                                my_voucher_advise.setVisibility(View.GONE);
                            } else {
                                my_voucher_advise.setImageBitmap(response.getBitmap());
                                my_voucher_advise.setVisibility(View.VISIBLE);
                                my_voucher_advise.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (voucherAdInfo != null)
                                            JumpUtil.JumpActivity(MyVoucherActivity.this, voucherAdInfo, mRoute);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            my_voucher_advise.setVisibility(View.GONE);
                        }
                    }
            );
        }
    }

    /**
     * 创建跳转信息
     */
    private void creatAdInfo() {
        voucherAdInfo = new JumpInfo();
        voucherAdInfo.setPageId(PersistentVar.getInstance().getSystemConfig().getVoucherAdPageId());
        voucherAdInfo.setPageTitle(PersistentVar.getInstance().getSystemConfig().getVoucherAdPageTitle());
        int type = 0;
        try {
            type = Integer.parseInt(PersistentVar.getInstance().getSystemConfig().getVoucherAdType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        voucherAdInfo.setType(type);
        voucherAdInfo.setUrl(PersistentVar.getInstance().getSystemConfig().getVoucherAdUrl());
    }
}
