package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.event.TaskMsgChangedEvent;
import com.snailgame.cjg.event.UserTaskRefreshEvent;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.UserTaskFragmentAdapter;
import com.snailgame.cjg.personal.model.UserTaskModel;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 用户任务列表
 * Created by sunxy on 2015/2/2.
 */
public class UserTaskActivity extends SwipeBackActivity {
    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    @Bind(R.id.tv_task_week_msg)
    TextView weekTaskMsgView;

    @Bind(R.id.tv_task_one_msg)
    TextView oneTaskMsgView;

    private UserTaskFragmentAdapter mAdapter;

    private static final int PAGE_NUM = 2;

    @Subscribe
    public void changeMsgView(TaskMsgChangedEvent event) {
        if (event.getType() == UserTaskFragment.TAB_TASK_ONCE) {
            float phoneWidth = PhoneUtil.getScreenWidth();
            RelativeLayout.LayoutParams oneParams = (RelativeLayout.LayoutParams) oneTaskMsgView.getLayoutParams();
            oneParams.setMargins((int) (phoneWidth / 2 - ComUtil.dpToPx(45)) - ComUtil.dpToPx(15), ComUtil.dpToPx(5), 0, 0);
            oneTaskMsgView.setLayoutParams(oneParams);

            oneTaskMsgView.setText(String.valueOf(event.getNum()));
            oneTaskMsgView.setVisibility(event.getNum() == 0 ? View.GONE : View.VISIBLE);
            if (event.getNum() == 0 && event.isEntry()) {
                mViewPager.setCurrentItem(1);
            }
        } else if (event.getType() == UserTaskFragment.TAB_TASK_WEEK) {
            RelativeLayout.LayoutParams weekParams = (RelativeLayout.LayoutParams) weekTaskMsgView.getLayoutParams();
            weekParams.setMargins(0, ComUtil.dpToPx(5), ComUtil.dpToPx(45), 0);
            weekTaskMsgView.setLayoutParams(weekParams);

            weekTaskMsgView.setText(String.valueOf(event.getNum()));
            weekTaskMsgView.setVisibility(event.getNum() == 0 ? View.GONE : View.VISIBLE);
        }

    }

    public static Intent newIntent(Context context) {
        return new Intent(context, UserTaskActivity.class);
    }

    public static Intent newIntent(Context context, boolean isOutSideIn) {
        Intent intent = new Intent(context, UserTaskActivity.class);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    @Override
    protected void handleIntent() {
        if (getIntent() != null)
            // add for outside in
            isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);
    }

    @Override
    protected void initView() {
        ActionBarUtils.makeDetailActionbarStyle(this, getString(R.string.my_task), true, false, false);

        mAdapter = new UserTaskFragmentAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        tabStrip.setViewPager(mViewPager, PAGE_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {

            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_task;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTaskJson(JsonUrl.getJsonUrl().USER_TASK_URL);
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_USER_TASK);

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_USER_TASK);
    }

    public void loadTaskJson(String url) {
        createTask(url);
    }

    /**
     * 获取用户任务
     *
     * @param url
     */
    private void createTask(String url) {
        FSRequestHelper.newGetRequest(url, TAG,
                UserTaskModel.class, new IFDResponse<UserTaskModel>() {
                    @Override
                    public void onSuccess(UserTaskModel model) {
                        MainThreadBus.getInstance().post(
                                new UserTaskRefreshEvent(UserTaskRefreshEvent.RESULT_SUCCESS, model));
                    }

                    @Override
                    public void onNetWorkError() {
                        MainThreadBus.getInstance().post(
                                new UserTaskRefreshEvent(UserTaskRefreshEvent.RESULT_NETWORK_ERROR, null));
                    }

                    @Override
                    public void onServerError() {
                        MainThreadBus.getInstance().post(
                                new UserTaskRefreshEvent(UserTaskRefreshEvent.RESULT_ERROR, null));
                    }
                }, false);
    }
}