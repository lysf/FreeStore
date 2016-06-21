package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.personal.adapter.CurrencyHistoryFragmentAdatper;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.snailgame.mobilesdk.OnPayProcessListener;
import com.snailgame.mobilesdk.SnailErrorCode;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import butterknife.OnClick;
import third.scrolltab.ScrollTabHolder;

/**
 * 兔兔币 充值/使用 记录
 * Created by TAJ_C on 2015/4/20.
 */
public class CurrencyHistoryActivity extends BaseHistoryAcitvity {

    private CurrencyHistoryFragmentAdatper mAdapter;
    public static Intent newIntent(Context context) {



        Intent intent = new Intent(context, CurrencyHistoryActivity.class);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginSDKUtil.isLogined(this)) {
            createActionbar();
            setupView();
        }

    }

    private void setupView() {

        headerTitleView.setText(getString(R.string.personal_currency_title));
        gainView.setText(getString(R.string.personal_currency_charge));
        introduceView.setText(getString(R.string.currency_history_hint));
        introduceView.setTextSize(12);
        mAdapter = new CurrencyHistoryFragmentAdatper(this, getSupportFragmentManager());
        mAdapter.setTabHolderScrollingContent(this);
        viewPager.setAdapter(mAdapter);
        tabStrip.setViewPager(viewPager, TAB_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
                SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mAdapter
                        .getScrollTabHolders();
                ScrollTabHolder currentHolder = scrollTabHolders
                        .valueAt(position);
                if (currentHolder != null && mHeaderView != null)
                    currentHolder.adjustScroll((int) (mHeaderView
                            .getHeight() + ViewHelper
                            .getTranslationY(mHeaderView))
                            + ComUtil.dpToPx(9));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindData(GlobalVar.getInstance().getUsrInfo());
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_CURRENCY_HISTORY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_CURRENCY_HISTORY);
    }

    private void bindData(UserInfo info) {
        if (info != null) {
            numTextView.setValue((double) info.getiMoney());
        }
    }


    public void createActionbar() {
        ActionBar actionBar = getSupportActionBar();
        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(
                R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        ((TextView) actionBarView.findViewById(R.id.tv_title))
                .setText(R.string.personal_my_currency);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);

        actionBarView.findViewById(R.id.tv_title).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
    }

    @OnClick(R.id.tv_jump_get)
    void showChargeScreen() {
        LoginSDKUtil.snailGoToCharge(this, new ChargeCallBack(), LoginSDKUtil.STORE_CHARGE_PAGE);
    }


    private class ChargeCallBack extends OnPayProcessListener {

        @Override
        public void finishPayProcess(int i) {
            if (SnailErrorCode.SNAIL_COM_PLATFORM_SUCCESS == i) {
                //充值成功
                startService(UserInfoGetService.newIntent(FreeStoreApp.getContext(), AppConstants.ACTION_UPDATE_USR_INFO));
            }
        }
    }

    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        bindData(GlobalVar.getInstance().getUsrInfo());
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        // nothing
    }


}