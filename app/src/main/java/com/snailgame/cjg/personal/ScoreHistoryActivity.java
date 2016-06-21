package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.personal.adapter.ScoreHistoryFragmentAdapter;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.store.PointStoreActivity;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import butterknife.OnClick;
import third.scrolltab.ScrollTabHolder;

/**
 * 个人中心 - 积分 created by xixh 14-08-22
 */
public class ScoreHistoryActivity extends BaseHistoryAcitvity {


    private ScoreHistoryFragmentAdapter mAdapter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ScoreHistoryActivity.class);
        return intent;
    }

    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        bindData(GlobalVar.getInstance().getUsrInfo());
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

        headerTitleView.setText(getString(R.string.left_score));
        gainView.setText(getString(R.string.score_get));
        introduceView.setText(getString(R.string.score_exchange));
        introduceView.setTextColor(ResUtil.getColor(R.color.btn_green_normal));
        Drawable drawable = ResUtil.getDrawable(R.drawable.icon_go);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        introduceView.setCompoundDrawables(null, null, drawable, null);
        introduceView.setCompoundDrawablePadding(ComUtil.dpToPx(6));
        introduceView.setTextSize(14);
        mAdapter = new ScoreHistoryFragmentAdapter(this, getSupportFragmentManager());
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
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_SCORE_HISTORY);
        bindData(GlobalVar.getInstance().getUsrInfo());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_SCORE_HISTORY);
    }


    private void bindData(UserInfo info) {
        if (info != null) {
            numTextView.setValue(info.getiIntegral());
        }
    }

    public void createActionbar() {
        ActionBar actionBar = getSupportActionBar();
        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(
                R.layout.actionbar_right_btn, null);
        actionBarView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((TextView) actionBarView.findViewById(R.id.tv_title))
                .setText(R.string.score_actionbar);
        ((TextView) actionBarView.findViewById(R.id.tv_right_action))
                .setText(R.string.score_introduce);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        actionBarView.findViewById(R.id.tv_title).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        actionBarView.findViewById(R.id.tv_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showScoreHistoryHintDialog(ScoreHistoryActivity.this, null);

            }
        });
    }


    @OnClick(R.id.tv_jump_get)
    void showScoreEarnScreen() {
        if (LoginSDKUtil.isLogined(this)) {
            // 进入 我的任务界面
            startActivity(UserTaskActivity.newIntent(this));
            finish();
        } else {
            AccountUtil.userLogin(this);
        }
    }

    @OnClick(R.id.introduce_container)
    public void showHint() {
        startActivity(PointStoreActivity.newIntent(ScoreHistoryActivity.this));
    }


}