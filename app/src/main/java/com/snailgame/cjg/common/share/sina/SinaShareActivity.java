/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snailgame.cjg.common.share.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.receiver.GetPointsReceiver;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;


/**
 * Created by lic on 16-2-23.
 */
public class SinaShareActivity extends Activity implements IWeiboHandler.Response {
    final static String TAG = SinaShareActivity.class.getSimpleName();

    private AuthInfo mAuthInfo;

    private Oauth2AccessToken mAccessToken;

    private SsoHandler mSsoHandler;
    private IWeiboShareAPI mWeiboShareAPI;

    private String shareUrl;

    // private TextView shareText;

    private String gameId;

    private String gameName;


    private ShareType shareType;


    private String shareContent;

    private Bitmap shareImg;

    private enum ShareType {
        INVITED, NORMAL_WEB, DETAIL_SHARE
    }

    //这两个值都是为了修复微博sdk自身的bug，防止在分享微博是没有分享，点击保存返回当前页面时，当前页面没有finish，
    // 导致一个空的activity覆盖在有内容actrivity上面，导致无法点击
    private boolean isOnStop, isSendShareMessage;

    //修复分享微博后，将微博清除数据，导致免商店分享到微博失败，因为我们程序已经鉴权过一次，但是微博数据清空，两者不对应，导致失败。但是第二次就能成功，所以可以这么做，如果失败一次就再分享一次即可。
    private int shareFailedTimes;

    /**
     * 分享应用
     */
    public static Intent newIntent(Context context, String shareUrl, String gameId, String gameName, Bitmap bitmap) {
        Intent intent = new Intent(context, SinaShareActivity.class);
        intent.putExtra(AppConstants.SHARE_URL, shareUrl);
        intent.putExtra(AppConstants.GAME_ID, gameId);
        intent.putExtra(AppConstants.GAME_NAME, gameName);
        intent.putExtra(AppConstants.SHARE_TYPE, ShareType.DETAIL_SHARE);
        intent.putExtra(AppConstants.SHARE_PIC, bitmap);
        return intent;
    }


    /**
     * 分享网页（活动等）
     */
    public static Intent newIntent(Context context, String shareUrl, String shareTitle, Bitmap bitmap) {
        Intent intent = new Intent(context, SinaShareActivity.class);
        intent.putExtra(AppConstants.SHARE_URL, shareUrl);
        intent.putExtra(AppConstants.SHARE_CONTENT, shareTitle);
        intent.putExtra(AppConstants.SHARE_TYPE, ShareType.NORMAL_WEB);
        intent.putExtra(AppConstants.SHARE_PIC, bitmap);
        return intent;
    }

    /**
     * 邀请用户
     */
    public static Intent newIntent(Context context, String ShareUrl) {
        Intent intent = new Intent(context, SinaShareActivity.class);
        intent.putExtra(AppConstants.SHARE_URL, ShareUrl);
        intent.putExtra(AppConstants.SHARE_TYPE, ShareType.INVITED);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        Intent intent = getIntent();
        if (intent == null) return;
        gameId = intent.getStringExtra(AppConstants.GAME_ID);
        gameName = intent.getStringExtra(AppConstants.GAME_NAME);
        shareType = (ShareType) intent.getSerializableExtra(AppConstants.SHARE_TYPE);
        shareContent = intent.getStringExtra(AppConstants.SHARE_CONTENT);
        shareUrl = getIntent().getStringExtra(AppConstants.SHARE_URL);
        shareImg = getIntent().getParcelableExtra(AppConstants.SHARE_PIC);
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(SinaShareActivity.this, mAuthInfo);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();
//        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
//        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
//        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
//        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        if (!mAccessToken.isSessionValid()) {
            // SSO 授权, 仅客户端
            mSsoHandler.authorizeClientSso(new AuthListener());
        } else {
            doShare();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 区分分享类型
     */
    private void doShare() {
        isSendShareMessage = true;
        isOnStop = false;
        mWeiboShareAPI.registerApp();
        if (shareType == ShareType.DETAIL_SHARE) {
            sendMessage(true, true);
        } else if (shareType == ShareType.NORMAL_WEB) {
            sendMessage(true, true);
        } else if (shareType == ShareType.INVITED) {
            sendMessage(true, false);
        }

    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(SinaShareActivity.this, mAccessToken);
                doShare();
            } else {
                String code = values.getString("code");
                String message = "fail:";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                ToastUtils.showMsg(SinaShareActivity.this, R.string.share_fail);
                Log.d(TAG, message);
                finish();
            }
        }

        @Override
        public void onCancel() {
            ToastUtils.showMsg(SinaShareActivity.this, R.string.share_cancle);
            Log.d(TAG, "Auth Cancle");
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                ToastUtils.showMsg(SinaShareActivity.this, R.string.share_fail);
            } else {
                ToastUtils.showMsg(SinaShareActivity.this, R.string.weibosdk_demo_not_support_api_hint);
            }
            Log.d(TAG, "Auth exception : " + e.getMessage());
            finish();
        }
    }

    /**
     * 分享成功后获取积分点
     */
    private void getSharePoints() {
        //如果是侧边栏点击则默认id 36
        if (TextUtils.isEmpty(gameId)) {
            gameId = "36";
        }
        if (shareType == ShareType.INVITED) {
            sendBroadcast(GetPointsReceiver.newIntent(this, gameId, gameName, "2", true));
        } else {
            sendBroadcast(GetPointsReceiver.newIntent(this, gameId, gameName, "2", false));
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     *
     * @see {@link #onActivityResult} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage(boolean hasText, boolean hasImage) {
//
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                sendMultiMessage(hasText, hasImage);
            } else {
                sendSingleMessage(hasText);
            }
        } else {
            ToastUtils.showMsg(SinaShareActivity.this, R.string.weibosdk_demo_not_support_api_hint);
            finish();
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText  分享的内容是否有文本
     * @param hasImage 分享的内容是否有图片
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
//
//        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(SinaShareActivity.this, request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText 分享的内容是否有文本
     */

    private void sendSingleMessage(boolean hasText) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(SinaShareActivity.this, request);
    }

    /**
     * 获取分享的文本模板。
     *
     * @return 分享的文本模板
     */
    private String getSharedText() {
        if (shareType == ShareType.DETAIL_SHARE) {
            return getString(R.string.share_good_app) + gameName + getString(R.string.share_free_tip) + shareUrl;
        } else if (shareType == ShareType.NORMAL_WEB) {
            return shareContent + shareUrl;
        } else {
            return getString(R.string.invite_friend_content) + shareUrl;
        }
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        if (shareImg == null) {
            shareImg = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
        }
        imageObject.setImageObject(shareImg);
        return imageObject;
    }


    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            Log.d(TAG, "baseResp errCode : " + baseResp.errCode);
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ToastUtils.showMsg(SinaShareActivity.this, R.string.share_success);
                    if (IdentityHelper.isLogined(SinaShareActivity.this)) {
                        getSharePoints();
                    }
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    ToastUtils.showMsg(SinaShareActivity.this, R.string.share_cancle);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    //第一次分享失败就在分享一次
                    shareFailedTimes++;
                    if (shareFailedTimes == 1) {
                        shareFailedTimes++;
                        doShare();
                        return;
                    } else {
                        ToastUtils.showMsg(SinaShareActivity.this, R.string.share_fail);
                    }

                    break;
                default:
                    break;
            }
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //捕捉到分享微博但是保存到草稿箱的情况，此情况无回调，必须手动结束当前activity，否则会导致一个空的activity覆盖在底下的activiy上面。
        if (shareFailedTimes != 1 && isOnStop && isSendShareMessage && !isFinishing()) {
            finish();
        }
    }

}