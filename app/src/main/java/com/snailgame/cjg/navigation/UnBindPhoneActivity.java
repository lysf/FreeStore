package com.snailgame.cjg.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.PopupDialog;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by sunxy on 14-8-20.
 */
public class UnBindPhoneActivity extends SwipeBackActivity {
    static String TAG = UnBindPhoneActivity.class.getName();

    public static Intent newIntent(Context context, String bindPhone, String accountName) {
        Intent intent = new Intent(context, UnBindPhoneActivity.class);
        intent.putExtra(AppConstants.BIND_PHONE_NUMBER, bindPhone);
        intent.putExtra(AppConstants.NICK_NAME, accountName);
        return intent;
    }

    @Bind(R.id.account_name)
    TextView bindAccountView;
    @Bind(R.id.bind_number)
    TextView bindNumberView;
    @Bind(R.id.unbind_number)
    TextView tvUnbindView;

    private String phoneNumber;


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
        return R.layout.activity_unbind;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getString(R.string.personal_option_change_bind));

        String nickName = getIntent().getStringExtra(AppConstants.NICK_NAME);
        if (nickName != null && !TextUtils.isEmpty(nickName))
            bindAccountView.setText(getString(R.string.unbind_account) + nickName);
        phoneNumber = getIntent().getStringExtra(AppConstants.BIND_PHONE_NUMBER);
        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber)) {
            bindNumberView.setText(getString(R.string.current_bind_account) + phoneNumber);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_UNBIND_PHONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_UNBIND_PHONE);
    }

    @OnClick(R.id.unbind_number)
     void unbindPhone() {
        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber))
            unbind();
        else {
            ToastUtils.showMsgLong(UnBindPhoneActivity.this, getString(R.string.get_bind_number_fail));
        }
    }

    /**
     * 解绑请求
     */
    private void unbind() {
        tvUnbindView.setClickable(false);
        tvUnbindView.setText(getString(R.string.unbinding));
        String url = JsonUrl.getJsonUrl().JSON_URL_BIND_OR_UNBIND_PHONE;
        String postBody = "nPhone=" + phoneNumber + "&cType=2" + "&cIMSI=" + PhoneUtil.getImsi(this);

        FSRequestHelper.newPostRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null && !TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = JSON.parseObject(result);
                        if (object.containsKey("msg")) {
                            if (object.getString("msg").equals("OK")) {
                                showDialog();
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tvUnbindView.setClickable(true);
                tvUnbindView.setText(getString(R.string.unbind));
            }

            @Override
            public void onNetWorkError() {
                unbindError();
            }

            @Override
            public void onServerError() {
                unbindError();
            }
        }, postBody);
    }

    /**
     * 解绑失败
     */
    private void unbindError() {
        tvUnbindView.setClickable(true);
        tvUnbindView.setText(getString(R.string.unbind));
        ToastUtils.showMsgLong(UnBindPhoneActivity.this, getString(R.string.unbind_fail));
    }


    private void showDialog() {
        final PopupDialog dialog = DialogUtils.unbindPhoneDialog(this);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}