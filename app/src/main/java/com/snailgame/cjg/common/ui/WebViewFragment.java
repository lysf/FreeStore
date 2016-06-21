/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.snailgame.cjg.common.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.FreeStoreInterface;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.share.IWebShare;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.widget.CommonWebView;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.seekgame.SeekGameAdapter;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.chromeClient;
import com.snailgame.cjg.wxapi.WechatShare;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A fragment that displays a WebView.
 * <p>
 * The WebView is automically paused or resumed when the Fragment is paused or resumed.
 */
public class WebViewFragment extends FixedSupportV4BugFragment implements IWebShare {
    static String TAG = WebViewFragment.class.getName();
    private Activity context;
    @Bind(R.id.layout)
    FrameLayout mLayout;
    @Bind(R.id.web_view)
    protected CommonWebView mWebView;
    @Bind(R.id.webview_stop_container)
    View mStopContainer;
    @Bind(R.id.tv_webview_stop)
    TextView mStopTextView;
    @Bind(R.id.tv_refresh)
    TextView mRefresh;    // 刷新按钮
    @Bind(R.id.webview_progress_bar)
    ProgressBar mProgressBar;

    private ActionBar mActionBar;
    private TextView mTitleView;
    private String shareTitle, shareUrl, shareDesc;
    private ShareDialog shareDialog;
    private ImageView shareImageView;
    private boolean shareImageClicked = false;
    private Bitmap mSharedIcon;
    private String mUrl;
    private boolean isShow;
    private String mNoShowText;
    private int mModel = 0;
    private boolean mIsWebViewAvailable;
    private LoadIconTask loadIconTask;
    private FreeStoreInterface androidCallback;

    private Animation mRefreshAnimation;    // 刷新动画
    private int mRefTime = 0;    // reload刷新次数
    private String interfaceName = "";

    private String mSharedIcoUrl;
    private boolean enableFinish;//是否允许关闭Activity


    public static final String localUrlHead = "http://localhost";
    public static final int FILECHOOSER_RESULTCODE = 10;
    public static final int TAKEPICTURE_RESULTCODE = 11;
    private BroadcastReceiver onlineShopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mModel == AppConstants.WEBVIEW_MODEL_ONLINE_SHOP)
                reloadUrl();
        }
    };


    private BroadcastReceiver commonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadUrl();
        }
    };


    private BroadcastReceiver userDealReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mModel == AppConstants.WEBVIEW_MODEL_COMMON)
                reloadUserDeal();
        }
    };


    private void reloadUserDeal() {
        if (mWebView != null)
            mWebView.reload();
    }


    public void reloadUrl() {
        if (mWebView != null) {
            if (mModel == AppConstants.WEBVIEW_MODEL_ONLINE_SHOP) {
                boolean isShow = SharedPreferencesUtil.getInstance().isSnailPhoneNumber();
                if (LoginSDKUtil.isLogined(FreeStoreApp.getContext()) && isShow) {
                    mUrl = PersistentVar.getInstance().getSystemConfig().getFreeCardUrl();
                } else {
                    mUrl = JsonUrl.getJsonUrl().ONLINE_SHOP_DEFAULT_URL;
                }
            }

            if (!TextUtils.isEmpty(mUrl)) {
                synCookies(getActivity(), mUrl);
            }

            mWebView.loadUrl(urlExtendHandle(mWebView.getUrl()));
//            mWebView.reload();
            mRefTime++;
        }
    }

    /**
     * 创建
     *
     * @param url        url地址
     * @param isShow     根据 systemConfig是否显示
     * @param noShowText 不显示时候的 提示
     * @param model      模块
     * @return
     */
    public static WebViewFragment create(String url, boolean isShow, String noShowText, int model) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle arg = new Bundle();
        arg.putString(AppConstants.WEBVIEW_URL, url);
        arg.putInt(AppConstants.WEBVIEW_MODEL, model);
        arg.putBoolean(AppConstants.WEBVIEW_IS_SHOW, isShow);
        arg.putString(AppConstants.WEBVIEW_NOSHOW_TEXT, noShowText);
        arg.putBoolean(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, true);
        fragment.setArguments(arg);
        return fragment;
    }

    /**
     *
     * @param url        url地址
     * @param isShow     根据 systemConfig是否显示
     * @param noShowText 不显示时候的 提示
     * @param model      模块
     * @param enableFinish 是否可以关闭Activity
     * @return
     */
    public static WebViewFragment create(String url, boolean isShow, String noShowText, int model,boolean enableFinish) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle arg = new Bundle();
        arg.putString(AppConstants.WEBVIEW_URL, url);
        arg.putInt(AppConstants.WEBVIEW_MODEL, model);
        arg.putBoolean(AppConstants.WEBVIEW_IS_SHOW, isShow);
        arg.putString(AppConstants.WEBVIEW_NOSHOW_TEXT, noShowText);
        arg.putBoolean(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, enableFinish);
        fragment.setArguments(arg);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        context = getActivity();
        if (bundle != null) {
            mUrl = bundle.getString(AppConstants.WEBVIEW_URL);
            mUrl = urlExtendHandle(mUrl);
            mModel = bundle.getInt(AppConstants.WEBVIEW_MODEL, 0);
            isShow = bundle.getBoolean(AppConstants.WEBVIEW_IS_SHOW, false);
            mNoShowText = bundle.getString(AppConstants.WEBVIEW_NOSHOW_TEXT);
            enableFinish = bundle.getBoolean(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH,true);
        }

        if (isShow) {
            getActivity().registerReceiver(onlineShopReceiver, new IntentFilter(AppConstants.WEBVIEW_BROADCAST_ACTION_ONLINESHOP));
            getActivity().registerReceiver(commonReceiver, new IntentFilter(AppConstants.WEBVIEW_BROADCAST_ACTION_COMMON));
            getActivity().registerReceiver(userDealReceiver, new IntentFilter(AppConstants.WEBVIEW_BROADCAST_ACTION_USERDEAL));
        }
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        View view = inflater.inflate(R.layout.webview_main, container, false);
        ButterKnife.bind(this, view);
        if (isShow) {
            mRefresh.setVisibility(View.VISIBLE);
            mRefreshAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);
            LinearInterpolator lin = new LinearInterpolator();
            mRefreshAnimation.setInterpolator(lin);

            mProgressBar.setProgress(0);
            mIsWebViewAvailable = true;
            mRefresh.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mWebView.reload();
                    mRefTime++;
                    if (mRefreshAnimation != null) {
                        mRefresh.startAnimation(mRefreshAnimation);
                    }
                }

            });
            if (!TextUtils.isEmpty(mUrl)) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isShow) {
            mStopContainer.setVisibility(View.VISIBLE);
            mStopTextView.setText(!TextUtils.isEmpty(mNoShowText)
                    ? mNoShowText : ResUtil.getString(R.string.function_not_work));
        } else {
            setupView();
        }
        setWebViewMarginTop();
    }

    private void setWebViewMarginTop() {
        if (mModel != AppConstants.WEBVIEW_MODEL_BBS && getActivity() instanceof MainActivity) {
            int toolbar_height = getResources().getDimensionPixelOffset(R.dimen.actionbar_height);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mWebView.getLayoutParams();
            if (params != null)
                params.topMargin = ComUtil.getStatesBarHeight() + toolbar_height;
            params = (FrameLayout.LayoutParams) mStopContainer.getLayoutParams();
            if (params != null)
                params.topMargin = ComUtil.getStatesBarHeight() + toolbar_height;
            params = (FrameLayout.LayoutParams) mProgressBar.getLayoutParams();
            if (params != null)
                params.topMargin = ComUtil.getStatesBarHeight() + toolbar_height;
        }
    }

    public void afterChooseImage(int requestCode, Intent data) {
        if (androidCallback != null) {
            androidCallback.afterChooseImage(requestCode, data);
        }
    }


    private void setupView() {
        if (getActivity() instanceof WebViewActivity) {
            mActionBar = ((WebViewActivity) getActivity()).getSupportActionBar();
            shareImageView = ActionBarUtils.makeWebViewActionbar(
                    mWebView, getActivity(), mActionBar, ResUtil.getString(R.string.actionbar_title_null), shareListener, this.mUrl);
            mTitleView = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_title);
            mWebView.setWebChromeClient(new chromeClient(mProgressBar, mTitleView));

            setupCommonWebView(mUrl);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mTitleView != null) {
                        mTitleView.setSelected(true);
                    }
                }
            }, 1000);
        } else {
            mWebView.setWebChromeClient(new chromeClient(mProgressBar));
        }

        mWebView.setDownloadListener(
                new DownloadListener() {
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String
                            mimetype, long contentLength) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }

        );

        androidCallback = new FreeStoreInterface(getActivity(), mWebView);
        androidCallback.setiWebShare(this);
        androidCallback.setEnableFinish(enableFinish);
        androidCallback.setTitleView(mTitleView);
        settingWebView(mUrl, androidCallback, FreeStoreInterface.class.getSimpleName());

    }

    protected void settingWebView(String url, Object callBack, String interfaceClassName) {
        settingWebView(url, callBack, new CommonWebViewClient(), interfaceClassName);
    }

    protected void settingWebView(String url, Object callBack, WebViewClient webViewClient, String interfaceClassName) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        String appCachePath = getActivity().getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        // 支持缩放，在SDK11以上，不显示缩放按钮
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);
        }
        // 自适应网页宽度
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        String defaultAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(defaultAgent + "FreeStoreAndroid" + " " + ComUtil.getSelfVersionName() + " " + GlobalVar.getInstance().getUserAgent(FreeStoreApp.getContext()));
        mWebView.addJavascriptInterface(callBack, interfaceClassName);
        interfaceName = interfaceClassName;
        synCookies(getActivity(), url);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(url);

    }


    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @TargetApi(11)
    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mWebView != null)
                mWebView.onResume();
        }
        super.onResume();

        switch (mModel) {
            //酷玩
            case AppConstants.WEBVIEW_MODEL_COOL_PLAY:
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_KUWAN);
                MainThreadBus.getInstance().register(this);
                break;

            //免卡
            case AppConstants.WEBVIEW_MODEL_ONLINE_SHOP:
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_FREE_CARD);
                MainThreadBus.getInstance().register(this);
                break;

            //论坛
            case AppConstants.WEBVIEW_MODEL_BBS:
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_BBS);
                MainThreadBus.getInstance().register(this);
                break;
            default:
                break;
        }

    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @TargetApi(11)
    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mWebView != null)
                mWebView.onPause();
        }
        switch (mModel) {
            //酷玩
            case AppConstants.WEBVIEW_MODEL_COOL_PLAY:
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_KUWAN);
                MainThreadBus.getInstance().unregister(this);
                break;

            //免卡
            case AppConstants.WEBVIEW_MODEL_ONLINE_SHOP:
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_FREE_CARD);
                MainThreadBus.getInstance().unregister(this);
                break;

            //论坛
            case AppConstants.WEBVIEW_MODEL_BBS:
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_BBS);
                MainThreadBus.getInstance().unregister(this);
                break;
            default:
                break;
        }

    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_MINE ||
                event.getTabPosition() == MainActivity.TAB_COOL_PLAY ||
                (event.getTabPosition() == MainActivity.TAB_SEEK_GAME && event.getPagePosition() == SeekGameAdapter.BBS)) {
            evaluateJavascript("javascript:scrollTo(0,0);");
        }
    }

    @SuppressLint("NewApi")
    public void evaluateJavascript(String javascript) {
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(javascript, null);
            } else {
                mWebView.loadUrl(javascript);
            }
        }
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsWebViewAvailable = false;
        mWebView.stopLoading();

        if (mWebView != null) {
            mWebView.removeAllViews();
            if (mLayout != null)
                mLayout.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }


        if (loadIconTask != null)
            loadIconTask.cancel(true);

        if (androidCallback != null) {
            androidCallback.cancelTask();
        }

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        if (androidCallback != null && androidCallback.getLoadImageTask() != null)
            androidCallback.getLoadImageTask().cancel(true);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isShow) {
            getActivity().unregisterReceiver(onlineShopReceiver);
            getActivity().unregisterReceiver(commonReceiver);
            getActivity().unregisterReceiver(userDealReceiver);
        }
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    public void setUrl(String url) {
        mUrl = url;
        if (mWebView != null)
            mWebView.loadUrl(url);
    }

    /**
     * 分享信息设置
     *
     * @param title
     * @param content
     * @param url
     * @param imageUrl
     */
    @Override
    public void setShare(String title, String content, String url, String imageUrl) {
        shareImageView.setVisibility(View.VISIBLE);

        shareTitle = title;
        shareUrl = url;
        shareDesc = content;
        GlobalVar.getInstance().setShareActivityUrl(shareUrl);
        if (!TextUtils.isEmpty(imageUrl)) {
            mSharedIcoUrl = imageUrl;
            if (loadIconTask != null) {
                mSharedIcon = null;
                loadIconTask.cancel(true);
            }
            loadIconTask = new LoadIconTask();
            loadIconTask.execute(imageUrl);
        } else {
            mSharedIcoUrl = "";
            mSharedIcon = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
            mSharedIcon = ComUtil.addWhiteBackground(mSharedIcon);
            BitmapUtil.savePicNoCompress(mSharedIcon, ComUtil.getShareImageFile(WechatShare.ACTIVITY_IMAGE_NAME).getAbsolutePath());
        }
    }

    public class CommonWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.loadUrl("javascript:window." + interfaceName + ".setTitleViewAction(0);");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("tel:")) {
                if (isAdded()) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    startActivity(intent);
                }
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideWebLoadingUI();
            if (mRefTime == 0) {
                if (mRefresh != null) {
                    mRefresh.setVisibility(View.GONE);
                    mRefresh.clearAnimation();
                }
            } else {
                mRefTime--;
            }

            if (url.contains("isHideActionbar=true")) {
                if (mActionBar != null) mActionBar.hide();
            } else if (mActionBar != null) {
                mActionBar.show();
            }

            view.loadUrl("javascript:window." + interfaceName
                    + ".parseMetaShare(document.getElementsByTagName('html')[0].innerHTML);");
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            hideWebLoadingUI();
            if (mTitleView != null) {
                mTitleView.setText(getString(R.string.load_webview_failture));
            }
            if (isShow) {
                ComUtil.showErrorPage(getActivity(), mWebView);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.indexOf(localUrlHead) == 0) {
                WebResourceResponse response = null;
                try {
                    InputStream localCopy = new FileInputStream(new File(
                            url.substring(localUrlHead.length())));
                    response = new WebResourceResponse("image/png",
                            "UTF-8", localCopy);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return response;
            }
            return super.shouldInterceptRequest(view, url);
        }
    }


    public void hideWebLoadingUI() {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
    }

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(shareTitle))
                shareTitle = mTitleView.getText().toString();
            shareImageClicked = true;
            if (mSharedIcon != null) {
                showShareDialog();
            } else {
                ToastUtils.showMsgLong(context, R.string.loading_share_image);
            }
        }
    };

    /**
     * @param url
     */
    private void setupCommonWebView(String url) {
        if (url.contains("isHideActionbar=true"))
            mActionBar.hide();
        else {
            mActionBar.show();
        }
        shareImageView.setVisibility(View.GONE);
        shareImageView.setBackgroundResource(R.drawable.ab_btn_selector);
        shareImageView.setImageResource(R.drawable.detail_ab_share_white);
    }


    private void showShareDialog() {
        shareImageClicked = false;
        shareDialog = new ShareDialog(context);
        ShareItemListener shareItemListener = new ShareItemListener(context, shareTitle, shareDesc, shareUrl, mSharedIcon, shareDialog, mSharedIcoUrl);
        shareDialog.setOnItemClickListener(shareItemListener);
        shareDialog.show();
    }

    class LoadIconTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                mSharedIcon = BitmapFactory.decodeStream(new URL(params[0]).openStream());
                if (mSharedIcon == null)
                    mSharedIcon = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
                mSharedIcon = ComUtil.addWhiteBackground(mSharedIcon);
                BitmapUtil.savePicNoCompress(mSharedIcon, ComUtil.getShareImageFile(WechatShare.ACTIVITY_IMAGE_NAME).getAbsolutePath());
                if (shareTitle != null && shareUrl != null && shareImageClicked) {
                    return true;
                }
            } catch (IOException e) {
                LogUtils.e(e.getMessage());
            } catch (OutOfMemoryError e) {
                LogUtils.e(e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aboolean) {
            super.onPostExecute(aboolean);
            if (aboolean) {
                showShareDialog();
            }
        }
    }

    private void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(getDomain(url) + "[0]", AppConstants.STORE_ACCESS_KEY_USER_ID + "=" + IdentityHelper.getUid(FreeStoreApp.getContext()));//cookies是在HttpClient中获得的cookie
        cookieManager.setCookie(getDomain(url) + "[1]", AppConstants.STORE_ACCESS_KEY_IDENTITY + "=" + IdentityHelper.getIdentity(FreeStoreApp.getContext()));
        cookieManager.setCookie(getDomain(url) + "[2]", AppConstants.STORE_ACCESS_KEY_APP_ID + "=" + IdentityHelper.getAppId());
        CookieSyncManager.getInstance().sync();
    }


    /**
     * 取 绝对域名
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                if ((i < url.length() - 1 && url.charAt(i + 1) != '/')) {
                    return url.substring(0, i);
                } else if (i < url.length() - 1 && url.charAt(i + 1) == '/') {
                    i++;
                }
            }
        }
        return url;
    }


    /**
     * url 特殊处理（通信）
     *
     * @param url
     * @return
     */
    private String urlExtendHandle(String url) {
        if (TextUtils.isEmpty(url))
            return url;

        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext()) && url.contains("mall.snail.com") && !url.contains("freeNetHallLogin")) {
            StringBuilder builder = new StringBuilder().append("http://mall.snail.com/site/freeNetHallLogin?type=freestore")
                    .append("&userId=").append(IdentityHelper.getUid(FreeStoreApp.getContext()))
                    .append("&identity=").append(IdentityHelper.getIdentity(FreeStoreApp.getContext()))
                    .append("&appId=").append(IdentityHelper.getAppId())
                    .append("&href=").append(url);
            return builder.toString();
        } else {
            return url;
        }
    }

    public FreeStoreInterface getAndroidCallback() {
        return androidCallback;
    }
}
