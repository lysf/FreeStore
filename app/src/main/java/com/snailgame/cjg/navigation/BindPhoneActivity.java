package com.snailgame.cjg.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.event.UpdateUserInfoPhoneEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunxy on 14-7-4.
 */
public class BindPhoneActivity extends BaseFSActivity implements TextWatcher {
    static String TAG = BindPhoneActivity.class.getName();

    public static Intent newIntent(Context context) {
        return newIntent(context, null);
    }

    public static Intent newIntent(Context context, boolean isOutSideIn) {
        Intent intent = newIntent(context, null);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    public static Intent newIntent(Context context, String phoneNumber) {
        Intent intent = new Intent(context, BindPhoneActivity.class);
        intent.putExtra(AppConstants.PHONE_NUMBER, phoneNumber);
        return intent;
    }

    public static Intent newIntent(Context context, String phoneNumber, boolean isOutSideIn) {
        Intent intent = newIntent(context, phoneNumber);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    @Bind(R.id.input_number)
    EditText phoneNumberView;
    @Bind(R.id.verification_code)
    EditText verificationCodeView;

    @Bind(R.id.get_verification_code)
    TextView verificationGetCodeView;

    @Bind(R.id.phone_bind)
    TextView bindView;
    @Bind(R.id.bind_phone_agreement)
    TextView agreementView;

    private String phone, verification;
    private CountDownHandler handler;

    private int count = 60;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeDetailActionbarStyle(this, getString(R.string.bind_phone_number_for_free), true, false, false);

        unClickableVercode();
        phoneNumberView.addTextChangedListener(this);
        handler = new CountDownHandler();
        phoneNumberView.setSingleLine();
        agreementView.setText(getString(R.string.bind_agreement));
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void handleIntent() {
        if (getIntent() != null) {
            String number = getIntent().getStringExtra(AppConstants.PHONE_NUMBER);
            // add for outside in
            isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);
            if (number != null)
                phoneNumberView.setText(number);
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
        return R.layout.bin_phone_number;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeMessages(0);

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
    }


    @OnClick(R.id.phone_bind)
    void bindPhone() {
        verification = verificationCodeView.getText().toString();
        phone = phoneNumberView.getText().toString();

        if (TextUtils.isEmpty(phone) || !ComUtil.isMobileNO(phone)) {
            ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.input_right_number);
            return;
        }

        if (TextUtils.isEmpty(verification)) {
            ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.input_verification_code);
            return;
        }

        bindView.setText(getString(R.string.binding));
        bindView.setClickable(false);
        bindView.setBackgroundResource(R.color.get_ver_code_bg);
        bind();
    }

    @OnClick(R.id.get_verification_code)
    void getVerificateCode() {
        phone = phoneNumberView.getText().toString();

        if (!TextUtils.isEmpty(phone) && ComUtil.isMobileNO(phone)) {
            handler.sendEmptyMessage(0);
            getVerificationCode(phone);
            unClickableVercode();
        } else {
            ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.input_right_number);
        }
    }

    /**
     * 绑定手机请求
     */
    private void bind() {
        String url = JsonUrl.getJsonUrl().JSON_URL_BIND_OR_UNBIND_PHONE;
        String postBody = "nPhone=" + phone + "&cSmsCode=" + verification + "&cType=1" + "&cIMSI=" + PhoneUtil.getImsi(this);

        FSRequestHelper.newPostRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null && !TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = JSON.parseObject(result);
                        if (object != null && object.containsKey("code")) {
                            if (object.getIntValue("code") == 0) {
                                ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.bind_success);

                                MainThreadBus.getInstance().post(new UpdateUserInfoPhoneEvent(phone));

                                Intent intentService = UserInfoGetService.newIntent(BindPhoneActivity.this, AppConstants.ACTION_UPDATE_USR_INFO);
                                startService(intentService);

                                SharedPreferencesUtil.getInstance().setPhoneNumber(phoneNumberView.getText().toString());
                                FreeStoreApp.getContext().sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_USERDEAL));

                                finish();
                            } else {
                                ToastUtils.showMsgLong(BindPhoneActivity.this, object.getString("msg"));
                                resetBindBtn();
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.bind_fail);
                resetBindBtn();
            }

            @Override
            public void onNetWorkError() {
                bindError();
            }

            @Override
            public void onServerError() {
                bindError();
            }
        }, postBody);
    }


    /**
     * 绑定失败
     */
    private void bindError() {
        ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.bind_fail);
        resetBindBtn();
    }


    /**
     * 获取验证码
     */
    private void getVerificationCode(String phone) {
        String url = JsonUrl.getJsonUrl().JSON_URL_SEND_BIND_SMS + "?nPhoneNum=" + phone;

        FSRequestHelper.newGetRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null && !TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = JSON.parseObject(result);
                        if (object != null && object.containsKey("code")) {
                            if (object.getIntValue("code") == 0) {
                                return;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.get_code_fail);
            }

            @Override
            public void onNetWorkError() {
                ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.get_code_fail);
            }

            @Override
            public void onServerError() {
                ToastUtils.showMsgLong(BindPhoneActivity.this, R.string.get_code_fail);
            }
        }, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (ComUtil.isMobileNO(s.toString()))
            clickableVercode();
        else {
            unClickableVercode();
        }

    }


    class CountDownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (count >= 0) {
                verificationGetCodeView.setText(String.format(getString(R.string.get_code_again), count));
                --count;
                sendEmptyMessageDelayed(0, 1000);
            } else {
                resetGetCodeBtn();
            }
        }
    }

    private void resetGetCodeBtn() {
        handler.removeMessages(0);
        count = 60;
        verificationGetCodeView.setClickable(true);
        verificationGetCodeView.setBackgroundResource(R.drawable.btn_green_selector);
        verificationGetCodeView.setText(getString(R.string.get_verification));
    }

    private void resetBindBtn() {
        bindView.setClickable(true);
        bindView.setText(getString(R.string.one_key_bind));
        bindView.setBackgroundResource(R.drawable.btn_green_selector);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_BIND_PHONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_BIND_PHONE);
    }

    private void unClickableVercode() {
        verificationGetCodeView.setClickable(false);
        verificationGetCodeView.setBackgroundResource(R.color.common_window_bg);
        verificationGetCodeView.setTextColor(ResUtil.getColor(R.color.have_updated_color));
    }

    private void clickableVercode() {
        verificationGetCodeView.setClickable(true);
        verificationGetCodeView.setBackgroundResource(R.drawable.btn_green_selector);
        verificationGetCodeView.setTextColor(ResUtil.getColor(R.color.white));
    }
}