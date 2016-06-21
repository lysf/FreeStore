package com.snailgame.cjg.common.inter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.snail.pay.SnailPay;
import com.snail.pay.listener.GetUserPayPlatIdListener;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.server.ScratchInfoGetService;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.common.share.IWebShare;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.CommonWebView;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.model.JumpInfo;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.snailgame.mobilesdk.OnGetSTListener;
import com.snailgame.mobilesdk.SnailCommplatform;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by yftx on 7/19/14.
 */
public class WebViewInterface extends ImageUploadInterface {

    private boolean enableFinish ;//是否可以关闭Activity
    protected TextView mTitleView;
    private ShareItemListener.LoadShareImageTask task;
    private IWebShare iWebShare;    // 网页分享设置接口

    public WebViewInterface(Activity activity, CommonWebView webView) {
        super(webView, activity);
    }


    public void setiWebShare(IWebShare iWebShare) {
        this.iWebShare = iWebShare;
    }

    /**
     * 传入 Actionbar Title View
     *
     * @param v
     */
    public void setTitleView(TextView v) {
        mTitleView = v;
    }
    public void setEnableFinish(boolean enableFinish){
        this.enableFinish = enableFinish;
    }

    /**
     * 控制Activity销毁
     * @param enableFinish
     */
    @JavascriptInterface
    public void setFinishEnable(boolean enableFinish){
        setEnableFinish(enableFinish);
        if(mActivity instanceof WebViewActivity){
            ((WebViewActivity)mActivity).setEnableFinish(enableFinish);
        }
    }
    /**
     * 拷贝数据到 剪切板
     *
     * @param text
     */
    @JavascriptInterface
    public void copyToClipboard(String text) {
        ClipboardManager cmb = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
    }

    /**
     * 设置 Actionbar的title
     *
     * @param text
     */
    @JavascriptInterface
    public void setWebViewTitle(final String text) {
        if (mTitleView != null) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTitleView.setText(text);
                }
            });
        }
    }

    /**
     * 登录
     */
    @JavascriptInterface
    public void login() {
        // 在登录身份异常的情况下，重新登录前需登出清理缓存
        if (IdentityHelper.isLogined(mActivity))
            AccountUtil.userLogout(mActivity);

        AccountUtil.userLogin(mActivity);
    }

    /**
     * 登出
     */
    @JavascriptInterface
    public void logout() {
        if (IdentityHelper.isLogined(mActivity))
            AccountUtil.userLogout(mActivity);
    }

    /**
     * 获取身份验证串
     * appID|UID|IDENTITY
     *
     * @return 返回身份验证相关信息
     */
    @JavascriptInterface
    public String getLoginInfo() {
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext())) {
            return AccountUtil.getLoginParams().replace("?", "");
        } else {
            return AppConstants.STORE_ACCESS_KEY_USER_ID + "=&" + AppConstants.STORE_ACCESS_KEY_IDENTITY + "=&"
                    + AppConstants.STORE_ACCESS_KEY_APP_ID + "=";
        }
    }

    /**
     * 关闭网页
     */
    @JavascriptInterface
    public void finish() {
        mActivity.finish();
    }

    /**
     * 刷新在线商店
     */
    @JavascriptInterface
    public void refreshOnlineShop() {
        mActivity.sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_ONLINESHOP));
    }

    /**
     * 分享
     *
     * @param title    分享名
     * @param content  分享内容
     * @param url      分享的url
     * @param imageUrl 分享的图片url
     */
    @JavascriptInterface
    public void share(String title, String content, String url, String imageUrl) {
        if (TextUtils.isEmpty(title) && mTitleView != null) {
            title = mTitleView.getText().toString();
        }
        GlobalVar.getInstance().setShareActivityUrl(url);
        ShareDialog shareDialog;
        ShareItemListener listener;
        if (TextUtils.isEmpty(imageUrl)) {
            Bitmap bitmap = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
            bitmap = ComUtil.addWhiteBackground(bitmap);
            shareDialog = new ShareDialog(mActivity);
            listener = new ShareItemListener(mActivity, title, content, url, bitmap, shareDialog, imageUrl);
            shareDialog.setOnItemClickListener(listener);
            shareDialog.show();
            return;
        } else {
            shareDialog = new ShareDialog(mActivity);
            listener = new ShareItemListener(mActivity, title, content, url, null, shareDialog, imageUrl);
            shareDialog.setOnItemClickListener(listener);
            shareDialog.show();
        }
        if (task != null)
            task.cancel(true);
        task = listener.getLoadImageTask();
    }

    public ShareItemListener.LoadShareImageTask getLoadImageTask() {
        return task;
    }

    /**
     * 网页上创建下载任务
     *
     * @param downloadUrl apk下载url
     * @param appName     apk packageName
     * @param pkgName     apk 显示名称
     * @param icoUrl      apk ico url
     * @param versionCode apk 版本号
     * @param apkId       apkID
     * @param apkVersion  apk 版本
     * @param md5         apk md5
     * @param apkSize     apk大小
     */
    @JavascriptInterface
    public void createDownloadTask(String downloadUrl, String appName, String pkgName,
                                   String icoUrl, String versionCode, String apkId,
                                   String apkVersion, String md5, String apkSize) {
        final AppInfo appInfo = new AppInfo();
        appInfo.setApkUrl(downloadUrl);
        appInfo.setAppName(appName);
        appInfo.setPkgName(pkgName);
        appInfo.setIcon(icoUrl);
        appInfo.setVersionCode(Integer.parseInt(versionCode));
        appInfo.setAppId(Integer.parseInt(apkId));
        appInfo.setVersionName(apkVersion);
        appInfo.setMd5(md5);
        appInfo.setApkSize(Integer.parseInt(apkSize));

        DownloadConfirm.showDownloadConfirmDialog(mActivity, new DownloadConfirm.IConfirmResult() {
            @Override
            public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                if (!isUseFlowDownLoad) {
                    appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                }
                DownloadHelper.startDownload(mActivity, appInfo);
            }

            @Override
            public void doDismissDialog(boolean isDialogDismiss) {

            }
        });

        int[] route = createRoute();
        route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
        route[AppConstants.STATISTCS_DEPTH_NINE] = Integer.parseInt(apkId);
        // 尝试下载统计
        StaticsUtils.onDownload(route);
    }

    /**
     * 检测某应用程序是否已经安装
     *
     * @param pkgName
     * @return true 应用程序已经安装 false 应用程序未安装
     */
    @JavascriptInterface
    public boolean checkAppIsInstall(String pkgName) {
        try {
            FreeStoreApp.getContext().getPackageManager().getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 充值接口
     *
     * @param money 充值最低金额
     */
    @JavascriptInterface
    public void charge(int money) {
        AccountUtil.recharge(mActivity, money, new LoginSDKUtil.onPayListener() {
            @Override
            public void finishPayProcess(int ret) {
                if (mWebView != null)
                    mWebView.loadUrl("javascript:OnCharge(" + ret + ")");

                if (ret == LoginSDKUtil.SNAIL_COM_PLATFORM_SUCCESS)   // 支付成功，数据刷新
                    refreshUserInfo();
            }
        });
    }

    /**
     * 获取用户个人信息 1、是否BSS帐号 2、获取账号名(蜗牛通行证) 3、displayAccount（有可能是别名（一般情况下是手机号）或者蜗牛通行证） 4、获取昵称 5、获取用户绑定手机号
     *
     * @return 由个人信息组成的json
     */
    @JavascriptInterface
    public String getUserInfo() {
        boolean isBssAccount = false;
        String account = "";
        String displayAccount = "";
        String nickName = "";
        String phoneNumber = "";
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext())) {
            isBssAccount = SharedPreferencesUtil.getInstance().isSnailPhoneNumber();
            account = IdentityHelper.getAccount(FreeStoreApp.getContext());
            displayAccount = IdentityHelper.getDisplayAccount(FreeStoreApp.getContext());
            nickName = SharedPreferencesUtil.getInstance().getNickName();
            phoneNumber = SharedPreferencesUtil.getInstance().getPhoneNumber();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isBssAccount", isBssAccount);
            jsonObject.put("woniutongxingzheng", account);
            jsonObject.put("displayAccount", displayAccount);
            jsonObject.put("nickName", nickName);
            jsonObject.put("phoneNumber", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 获取设备信息 1、手机机型 2、手机CPU 3、RAM容量 4、ROM容量 5、屏幕分辨率 6、操作系统及版本 7、核心数 8、设备号 9、设备IMEI号
     *
     * @return 由设备信息组成的json
     */
    @JavascriptInterface
    public String getDeviceInfo() {
        return PhoneUtil.getDeviceInfo(mActivity);
    }

    /**
     * 获取手机网络类型
     *
     * @return NONE 2G 3G 4G WIFI UNKNOWN中的一个
     */
    @JavascriptInterface
    public String getNetStatus() {
        return PhoneUtil.getNetType(mActivity);
    }

    /**
     * 刷新用户信息
     */
    @JavascriptInterface
    public void refreshUserInfo() {
        mActivity.startService(UserInfoGetService.newIntent(mActivity, AppConstants.ACTION_UPDATE_USR_INFO));
    }

    /**
     * 刷新用户抽奖状态
     */
    @JavascriptInterface
    public void refreshUserLuckyInfo() {
        mActivity.startService(ScratchInfoGetService.newIntent(mActivity));
    }

    /**
     * 网厅支付
     * String  account      账号
     * String  productName  产品名称
     * String  orderNum     订单号（计费提供的接口生成）
     * String  orderMoney   支付金额(支持小数)
     * String  orderSource  调用支付渠道所需的参数串（计费提供的接口生成）
     * int     platformId   支付渠道ID(支付宝：200；银联：198)
     */
    @JavascriptInterface
    public void snailNetworkHallPay(String account, String productName, String orderNum, String orderMoney, String orderSource, int platformId) {
        LoginSDKUtil.snailNetworkHallPay(mActivity, account, productName, orderNum, orderMoney, orderSource, platformId, new LoginSDKUtil.onPayListener() {
            @Override
            public void finishPayProcess(int i) {
                if (mWebView != null)
                    mWebView.loadUrl("javascript:OnSnailNetworkHallPay(" + i + ")");

                if (i == LoginSDKUtil.SNAIL_COM_PLATFORM_SUCCESS)   // 支付成功，数据刷新
                    refreshUserInfo();
            }
        });
    }


    /**
     * 设置ActionBar左侧按钮动作
     *
     * @param type 0：关闭窗口
     *             1：webview 返回
     *             2：页面控制
     */
    @JavascriptInterface
    public void setTitleButtonAction(final int type) {
        if (mTitleView == null)
            return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case AppConstants.WEB_ACTIONBAR_ACTION_BACK:
                        ComUtil.setDrawableLeft(mTitleView, R.drawable.ic_back_normal);
                        mTitleView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (mWebView.canGoBack()) {
                                    mWebView.goBack();
                                } else if(enableFinish){
                                    mActivity.finish();
                                }
                            }
                        });
                        break;
                    case AppConstants.WEB_ACTIONBAR_ACTION_CUSTOM:
                        ComUtil.setDrawableLeft(mTitleView, R.drawable.ic_back_normal);
                        mTitleView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mWebView.loadUrl("javascript:OnSetTitleButtonAction()");
                            }
                        });
                        break;
                    case AppConstants.WEB_ACTIONBAR_ACTION_CLOSE:
                    default:
                        ComUtil.setDrawableLeft(mTitleView, R.drawable.ic_close_webview);
                        mTitleView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if(enableFinish) {
                                    mActivity.finish();
                                }
                            }
                        });
                        break;
                }
            }
        });

    }

    /**
     * 网页打开客户端页面
     * 如果字段没有的话默认传""
     *
     * @param pageId    跳转合集和应用详情页面所需参数
     * @param pageTitle 跳转合集页面时，合集页面的title
     * @param pageType  按照此字段选择打开页面具体对应关系参照JumpUtil的静态常量
     * @param url       跳转网页时打开的url
     */
    @JavascriptInterface
    public boolean openLocalPage(String pageId, String pageTitle, int pageType, String url) {
        if (pageType == JumpUtil.COLLECTION_FREE_AREA_TYPE) {
            if (IdentityHelper.isLogined(mActivity)) {
                if (TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getPhoneNumber())) {
                    // 未绑定，提示绑定免流量手机
                    AccountUtil.showNavigation(mActivity, false);
                } else {
                    // 登录且已绑定免流量手机用户才可进入免流量体验区
                    jumpToLocalPage(String.valueOf(AppConstants.STATISTCS_DEFAULT_NULL), mActivity.getString(R.string.free_area),
                            JumpUtil.COLLECTION_FREE_AREA_TYPE, "", createFreeAreaRoute());
                }
            } else {
                // 未登录登录，提示用户登录
                AccountUtil.userLogin(mActivity);
            }
        } else if (pageType == JumpUtil.USER_CENTER_PAGE_TYPE) {
            if (LoginSDKUtil.isLogined(mActivity)) {
                if (AccountUtil.isUsrInfoGet()) {
                    jumpToLocalPage("", "", JumpUtil.USER_CENTER_PAGE_TYPE, "", createRoute());
                } else {
                    AccountUtil.usrInfoNoSuccess(mActivity, FreeStoreApp.statusOfUsr);
                }
            } else {
                AccountUtil.userLogin(mActivity);
            }
        } else {
            return jumpToLocalPage(pageId, pageTitle, pageType, url, createRoute());
        }
        return true;
    }

    /**
     * 拨打电话
     *
     * @param phoneNumber 电话号码
     */
    @JavascriptInterface
    public void telCall(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showMsg(mActivity, mActivity.getString(R.string.service_phone_lose));
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            mActivity.startActivity(intent);
        }
    }


    /**
     * 网页打开客户端页面
     * 如果字段没有的话默认传""
     *
     * @param pageId    跳转合集和应用详情页面所需参数
     * @param pageTitle 跳转合集页面时，合集页面的title
     * @param pageType  按照此字段选择打开页面
     * @param url       跳转网页打开的url
     * @param route     用于统计路径
     */
    public boolean jumpToLocalPage(String pageId, String pageTitle, int pageType, String url, int[] route) {
        JumpInfo info = new JumpInfo();
        info.setPageId(pageId);
        info.setPageTitle(pageTitle);
        info.setType(pageType);
        info.setUrl(url);
        return JumpUtil.JumpActivity(mActivity, info, route);
    }

    /**
     * 提供给网页作为网页判断可支持的支付方式
     *
     * @param
     */
    @JavascriptInterface
    public void getUsePlatformId() {
        SnailPay.getOnUserPayPlatId(mActivity, new GetUserPayPlatIdListener() {

            @Override
            public void onSuccess(final String ids) {
                mWebView.post(new Runnable() {

                    @Override
                    public void run() {
                        evaluateJavascript("javascript:OnGetUsePlatformId(\"" + ids + "\")");
                    }
                });
            }
        });
    }

    /**
     * 获取ST
     *
     * @param service
     */
    @JavascriptInterface
    public void snailGetSt(String service) {
        SnailCommplatform.getInstance().snailGetSt(service, new OnGetSTListener() {

            @Override
            public void onGetSt(String service, String st) {
                try {
                    service = URLEncoder.encode(service, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String javascript = "javascript:OnSnailGetSt(\"%s\",\"%s\")";
                evaluateJavascript(String.format(javascript, service, st));
            }
        });
    }


    /**
     * 获取停留时间
     */
    private long getStayTime() {
        if (mActivity instanceof WebViewActivity) {
            long stayTime = System.currentTimeMillis() - ((WebViewActivity) mActivity).inTime;
            if (stayTime > 0)
                return stayTime / 1000;
        }

        return 0;
    }

    /**
     * 获取资讯行为统计信息
     */
    @JavascriptInterface
    public String getBehaviourInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stayTime", getStayTime());
            jsonObject.put("posType", StaticsUtils.getPosType());
            jsonObject.put("position", StaticsUtils.getPosition());
            jsonObject.put("netEnv", StaticsUtils.getNetEnv());
            jsonObject.put("cImei", PhoneUtil.getDeviceUUID(FreeStoreApp.getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    /**
     * 设置分享
     *
     * @param title    分享名
     * @param content  分享内容
     * @param url      分享的url
     * @param imageUrl 分享的图片url
     */
    @JavascriptInterface
    public void setShare(final String title, final String content, final String url, final String imageUrl) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iWebShare != null)
                    iWebShare.setShare(title, content, url, imageUrl);
            }
        });
    }


    /**
     * 显示页面内容
     *
     * @param html
     */
    @JavascriptInterface
    public void parseMetaShare(String html) {
        try {
            String title = null, content = null, url = null, imageUrl = null;
            Document document = Jsoup.parse(html);
            Elements elements = document.select("meta");
            for (Element meta : elements) {
                switch (meta.attr("property")) {
                    case "freestore:webpage:url":
                        url = meta.attr("content");
                        break;
                    case "freestore:webpage:title":
                        title = meta.attr("content");
                        break;
                    case "freestore:webpage:description":
                        content = meta.attr("content");
                        break;
                    case "freestore:webpage:image":
                        imageUrl = meta.attr("content");
                        break;
                    default:
                        break;
                }
            }

            if (!TextUtils.isEmpty(title) &&
                    !TextUtils.isEmpty(url))
                setShare(title, content, url, imageUrl);
        } catch (Exception e) {

        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 网页
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_WEB,
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
     * 用于统计路径
     */
    private int[] createFreeAreaRoute() {
        // 免流量专区
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_FREEAREA,
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


}
