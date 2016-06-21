package com.snailgame.cjg.personal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by sunxy on 2015/2/6.
 */
public class UserCenterActivity extends SwipeBackActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, UserCenterActivity.class);
    }

    public static Intent newIntent(Context context, boolean isOutSideIn) {
        Intent intent = newIntent(context);
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

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否登陆
        if (!LoginSDKUtil.isLogined(UserCenterActivity.this)) {
            AccountUtil.userLogin(this);
            finish();
        } else {
            createActionbar();
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new UserCenterFragment()).commitAllowingStateLoss();
        }
    }

    public void createActionbar() {
        ActionBar actionBar = getSupportActionBar();
        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_right_btn, null);
        actionBarView.findViewById(R.id.setting_actionbar).setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        actionBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((TextView) actionBarView.findViewById(R.id.tv_title)).setText(R.string.actionbar_personal_center);
        TextView exitBtn = ((TextView) actionBarView.findViewById(R.id.tv_right_action));
        exitBtn.setTextColor(ResUtil.getColor(R.color.white));
        exitBtn.setText(R.string.btn_exit);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);

        actionBarView.findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        actionBarView.findViewById(R.id.tv_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                    Dialog dialog = DialogUtils.showUserQuitDialog(UserCenterActivity.this, true);
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_PERSONAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_PERSONAL);
    }

    private class ChargeCallBack extends LoginSDKUtil.onPayListener {

        @Override
        public void finishPayProcess(int i) {
            if (LoginSDKUtil.SNAIL_COM_PLATFORM_SUCCESS == i) {
                //充值成功
                startService(UserInfoGetService.newIntent(UserCenterActivity.this, AppConstants.ACTION_UPDATE_USR_INFO));
            }
        }
    }
}