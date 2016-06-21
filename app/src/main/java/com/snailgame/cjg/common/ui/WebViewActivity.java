package com.snailgame.cjg.common.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.zbar.lib.CaptureActivity;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * 网页
 */
public class WebViewActivity extends SwipeBackActivity {
    private WebViewFragment mFragment;
    private String titleText;
    private String mUrl;
    protected int[] mRoute;
    private final static String OPENWITHSDK = "_OpenType=SDK";
    // 进入界面时间
    public long inTime;
    private boolean enableFinish = false;//是否可以关闭Activity

    /**
     * 打开网页，若url中指定使用SDK打开，这使用WebUploadActivity打开
     *
     * @param context
     * @param url
     * @return
     */
    public static Intent newIntent(Context context, String url) {
        if (!TextUtils.isEmpty(url) && url.contains(OPENWITHSDK)) {
            Intent intent = LoginSDKUtil.newIntentForWebUpload(context, url);
            return intent;
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(AppConstants.WEBVIEW_URL, url);
            return intent;
        }
    }

    /**
     * @param context
     * @param url
     * @param enableFinish
     * @param code         无效参数，做区别用
     * @return
     */
    public static Intent newIntent(Context context, String url, boolean enableFinish, int code) {
        Intent intent = newIntent(context, url);
        intent.putExtra(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, enableFinish);
        return intent;
    }

    /**
     * 打开网页，若url中指定使用SDK打开，这使用WebUploadActivity打开
     *
     * @param context
     * @param url
     * @param isOutSideIn
     * @return
     */
    public static Intent newIntent(Context context, String url, boolean isOutSideIn) {
        Intent intent = newIntent(context, url);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    public static Intent newIntent(Context context, String url, boolean isOutSideIn, boolean enableFinish) {
        Intent intent = newIntent(context, url, isOutSideIn);
        intent.putExtra(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, enableFinish);
        return intent;
    }

    /**
     * 打开网页，若url中指定使用SDK打开，这使用WebUploadActivity打开
     *
     * @param context
     * @param url
     * @param route
     * @return
     */
    public static Intent newIntent(Context context, String url, int route[]) {
        if (!TextUtils.isEmpty(url) && url.contains(OPENWITHSDK)) {
            Intent intent = LoginSDKUtil.newIntentForWebUpload(context, url);
            return intent;
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(AppConstants.WEBVIEW_URL, url);
            intent.putExtra(AppConstants.KEY_ROUTE, route);
            return intent;
        }
    }

    public static Intent newIntent(Context context, String url, int route[], boolean enableFinish) {
        Intent intent = newIntent(context, url, route);
        intent.putExtra(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, enableFinish);
        return intent;
    }

    public static Intent newIntent(Context context, String url, boolean isShow, String stopText, int model) {
        if (!TextUtils.isEmpty(url) && url.contains(OPENWITHSDK)) {
            Intent intent = LoginSDKUtil.newIntentForWebUpload(context, url);
            return intent;
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(AppConstants.WEBVIEW_URL, url);
            intent.putExtra(AppConstants.WEBVIEW_MODEL, model);
            intent.putExtra(AppConstants.WEBVIEW_IS_SHOW, isShow);
            intent.putExtra(AppConstants.WEBVIEW_NOSHOW_TEXT, stopText);
            return intent;
        }
    }

    public static Intent newIntent(Context context, String url, boolean isShow, String stopText, int model, boolean enableFinish) {
        Intent intent = newIntent(context, url, isShow, stopText, model);
        intent.putExtra(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, enableFinish);
        return intent;
    }

    /**
     * 获取资讯WebViewActivity
     *
     * @return
     */
    public static Intent newNewsWebViewIntent(Context context, String newsId, String url, int route[]) {
        StaticsUtils.newsBehaviourClick(newsId);
        return newIntent(context, url, route);
    }

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
        return R.layout.webview_fragment_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getStandardUrl(getUrl());
        inTime = System.currentTimeMillis();
        sendPV();
        enableFinish = getIntent().getBooleanExtra(AppConstants.WEBVIEW_ACTIVITY_ENABLE_FINISH, true);
        setSwipeBackEnable(enableFinish);
        int model = getIntent().getIntExtra(AppConstants.WEBVIEW_MODEL, -1);
        String emptyShowTxt = getIntent().getStringExtra(AppConstants.WEBVIEW_NOSHOW_TEXT);
        boolean isShow = getIntent().getBooleanExtra(AppConstants.WEBVIEW_IS_SHOW, true);
        // add for outside in
        isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);

        if (model != -1) {
            ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), titleText);
            mFragment = WebViewFragment.create(mUrl, isShow, emptyShowTxt, model, enableFinish);
        } else {
            mFragment = WebViewFragment.create(mUrl, true, null, AppConstants.WEBVIEW_MODEL_COMMON, enableFinish);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.webview_content, mFragment);
        ft.commitAllowingStateLoss();
    }

    private void sendPV() {
        mRoute = getIntent().getIntArrayExtra(AppConstants.KEY_ROUTE);
        if (mRoute != null) {
            // pv统计
            StaticsUtils.onPV(mRoute);
        }
    }


    private String getUrl() {
        String url;

        //直接load
        url = getIntent().getStringExtra(AppConstants.WEBVIEW_URL);
        if (!TextUtils.isEmpty(url)) {
            return url;
        }

        titleText = ResUtil.getString(R.string.app_name);
        return url;
    }

    private String getStandardUrl(String url) {
        String newUrl;

        if (url != null && url.contains(AppConstants.STORE_ACCESS_KEY_USER_ID))
            newUrl = url;
        else if (url != null && url.contains("?"))
            newUrl = url + AccountUtil.getLoginParams().replace("?", "&");
        else {
            newUrl = url + AccountUtil.getLoginParams();
        }
        return newUrl;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragment != null && mFragment.getWebView() != null && mFragment.getWebView().canGoBack()) {
                mFragment.getWebView().goBack();
            } else if (enableFinish) {
                WebViewActivity.this.finish();
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.SCAN_REQUEST_CODE:
                    final AlertDialog aDialog = DialogUtils.scanSuccessDialog(WebViewActivity.this, data);
                    aDialog.show();
                    break;

                case WebViewFragment.FILECHOOSER_RESULTCODE:
                    if (mFragment != null) {
                        mFragment.afterChooseImage(requestCode, data);
                    }
                    break;

                case WebViewFragment.TAKEPICTURE_RESULTCODE:
                    if (mFragment != null) {
                        mFragment.afterChooseImage(requestCode, data);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public boolean getFinishEnable(){
        return this.enableFinish;
    }
    public void setEnableFinish(boolean enableFinish){
        this.enableFinish = enableFinish;
        setSwipeBackEnable(enableFinish);
    }
}
