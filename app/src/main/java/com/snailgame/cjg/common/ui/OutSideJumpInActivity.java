package com.snailgame.cjg.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AuthoModel;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.model.JumpInfo;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.HashMap;
import java.util.Map;

/*
 * 从外部网页打开免商店
 * created by xixh 2014/09/28
 */
public class OutSideJumpInActivity extends Activity {
    static String TAG = OutSideJumpInActivity.class.getName();

    private final static String ACTION_SDK_JUMP = "com.snailgame.mobilesdk.jump";
    private final static String EXTRA_UID = "uid";
    private final static String EXTRA_SESSION_ID = "sessionid";
    private final static String EXTRA_JUMP_URL = "jumpurl";
    private final static String EXTRA_APPID = "appid";
    private final static String EXTRA_ACCOUNT = "account";
    private int[] mRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoute = createRoute();

        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            try {
                Uri uri = i_getvalue.getData();
                if (uri != null) {
                    final JumpInfo info = new JumpInfo();
                    info.setPageId(uri.getQueryParameter("pageId"));
                    info.setPageTitle(uri.getQueryParameter("pageTitle"));
                    info.setType(Integer.parseInt(uri.getQueryParameter("type")));
                    info.setUrl(uri.getQueryParameter("url"));
                    info.setChannel(uri.getQueryParameter("channel"));
                    info.setTask(uri.getQueryParameter("task"));
                    info.setMd5(uri.getQueryParameter("md5"));

                    JumpUtil.JumpActivity(OutSideJumpInActivity.this, info, mRoute, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        } else if (ACTION_SDK_JUMP.equals(action)) {    // 从SDK跳入免商店，用户信息覆盖
            String uid = i_getvalue.getStringExtra(EXTRA_UID);
            String sessionId = i_getvalue.getStringExtra(EXTRA_SESSION_ID);
            final String jumpUrl = i_getvalue.getStringExtra(EXTRA_JUMP_URL);
            String account = i_getvalue.getStringExtra(EXTRA_ACCOUNT);
            int appId = i_getvalue.getIntExtra(EXTRA_APPID, 36);

            if (!TextUtils.isEmpty(jumpUrl)) {
                if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(sessionId)) {
                    if (uid.equals(LoginSDKUtil.getLoginUin(FreeStoreApp.getContext()))) {
                        openInWeb(jumpUrl);
                    } else {
                        setContentView(R.layout.activity_outside_jump);
                        autho(appId, uid, sessionId, jumpUrl, account);
                    }
                } else {
                    openInWeb(jumpUrl);
                }
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    /**
     * 打开活动页
     *
     * @param url
     */
    private void openInWeb(final String url) {
        Intent intent = WebViewActivity.newIntent(OutSideJumpInActivity.this, url, true);
        startActivity(intent);
        finish();
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 外部打开
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_OUTWEB,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    /**
     * 应用授权
     */
    private void autho(int nAppId, final String nUserId, final String cIdentity, final String jumpUrl, final String account) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("nAppId", String.valueOf(nAppId));
        parameters.put("nUserId", nUserId);
        parameters.put("cIdentity", cIdentity);

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_SDK_AUTHO, TAG, AuthoModel.class, new IFDResponse<AuthoModel>() {
            @Override
            public void onSuccess(AuthoModel result) {
                if (result != null && result.getCode() == 0 && result.getItem() != null && !TextUtils.isEmpty(result.getItem().getcIdentity())) {
                    AccountUtil.userCover(FreeStoreApp.getContext(), nUserId, result.getItem().getcIdentity(), account);
                } else {
                    ToastUtils.showMsgLong(FreeStoreApp.getContext(), R.string.autho_fail_alert);
                }

                openInWeb(jumpUrl);
            }

            @Override
            public void onNetWorkError() {
                ToastUtils.showMsgLong(FreeStoreApp.getContext(), R.string.autho_fail_alert);
                openInWeb(jumpUrl);
            }

            @Override
            public void onServerError() {
                ToastUtils.showMsgLong(FreeStoreApp.getContext(), R.string.autho_fail_alert);
                openInWeb(jumpUrl);
            }
        }, parameters);
    }
}
