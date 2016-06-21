package com.snailgame.cjg.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.home.AppAppointmentFragment;
import com.snailgame.cjg.seekapp.SeekAppFragment;
import com.snailgame.cjg.util.ActionBarUtils;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by TAJ_C on 2015/9/28.
 */
public class FragmentContainerActivity extends SwipeBackActivity {

    private int type;
    public static final int ACTIVITY_APP_APPOINTMENT = 2; //首页--快速入口-- 测新游
    public static final int ACTIVITY_SEEK_APP = 3;// 首页--快速入口--软件

    public static Intent newIntent(Context context, int type, String title) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        intent.putExtra(AppConstants.ACTIVITY_TYPE, type);
        intent.putExtra(AppConstants.ACTIVITY_TITLE, title);
        return intent;
    }

    @Override
    protected void handleIntent() {
        type = getIntent().getIntExtra(AppConstants.ACTIVITY_TYPE, -1);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarUtils.makeDetailActionbarStyle(this, getIntent().getStringExtra(AppConstants.ACTIVITY_TITLE), true, false, false);

        Fragment fragment = getContent(type);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment)
                .commit();

    }

    private Fragment getContent(int type) {
        switch (type) {
            case ACTIVITY_APP_APPOINTMENT:
                return AppAppointmentFragment.getIntent();

            case ACTIVITY_SEEK_APP:
                return new SeekAppFragment();
            default:
                return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (type) {
            case ACTIVITY_APP_APPOINTMENT:
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_APP_APPOINTMENT);
                break;

            default:
                break;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        switch (type) {
            case ACTIVITY_APP_APPOINTMENT:
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_APP_APPOINTMENT);
                break;
            default:
                break;
        }
    }


}