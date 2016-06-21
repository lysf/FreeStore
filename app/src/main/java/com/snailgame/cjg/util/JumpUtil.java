package com.snailgame.cjg.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.FragmentContainerActivity;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.SystemConfig;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.friend.FriendMainActivity;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.guide.SplashActivity;
import com.snailgame.cjg.home.AppNewsActivity;
import com.snailgame.cjg.home.QuickPopupActivity;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.navigation.BindPhoneActivity;
import com.snailgame.cjg.news.NewsActivity;
import com.snailgame.cjg.personal.MyVoucherActivity;
import com.snailgame.cjg.personal.UserCenterActivity;
import com.snailgame.cjg.personal.UserTaskActivity;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;
import com.snailgame.cjg.seekgame.recommend.AppListActivity;
import com.snailgame.cjg.util.model.JumpInfo;
import com.zbar.lib.CaptureActivity;

/**
 * 页面跳转类 从ComUtil抽出
 * Created by TAJ_C on 2014/10/20.
 */
public class JumpUtil {
    public static final int SPLASH_PAGE_TYPE = 1;
    public static final int WEB_PAGE_ONE_TYPE = 3;
    public static final int HOME_PAGE_TYPE = 4;
    public static final int COLLECTION_PAGE_TYPE = 5;
    public static final int DETAIL_PAGE_TYPE = 6;
    public static final int WEB_PAGE_ACTIVITY_TYPE = 7;
    public static final int WEB_PAGE_TWO_TYPE = 9;
    public static final int VOUCHER_PAGE_TYPE = 12;
    public static final int SCORE_PAGE_TYPE = 13;
    public static final int BIND_PHONE_PAGE_TYPE = 14;
    public static final int USER_CENTER_PAGE_TYPE = 15;
    public static final int COLLECTION_FREE_AREA_TYPE = 16;
    public static final int SCAN_PAGE_TYPE = 17;
    public static final int GAME_SPREE_TYPE = 18;
    public static final int OPEN_URL_IN_SDK = 19;
    public static final int OPEN_URL_IN_SYSTEM_BROWSER = 20;

    // 界面跳转
    public static boolean JumpActivity(Context context, JumpInfo info, int[] route) {
        return JumpActivity(context, info, route, false);
    }

    // 界面跳转
    public static boolean JumpActivity(Context context, JumpInfo info, int[] route, boolean isOutSideIn) {
        boolean ret = true;
        boolean isActivityFinish = false;
        Intent intent = null;
        if (info != null) {
            switch (info.getType()) {
                case SPLASH_PAGE_TYPE:         // 欢迎页
                    intent = SplashActivity.newIntent(context);
                    break;
                case HOME_PAGE_TYPE:         // 首页
                    intent = MainActivity.newIntent(context);
                    break;
                case COLLECTION_PAGE_TYPE:         // 合集
                    if (!TextUtils.isEmpty(info.getPageId())
                            && !TextUtils.isEmpty(info.getPageTitle())) {
                        intent = CollectionActivity.newIntent(context,
                                Integer.parseInt(info.getPageId()), route, isOutSideIn);
                    }
                    break;
                case DETAIL_PAGE_TYPE:         // 应用详情
                    if (!TextUtils.isEmpty(info.getPageId())) {
                        int appId = 0;
                        try {
                            appId = Integer.parseInt(info.getPageId());
                        } catch (Exception e) {

                        }

                        if (appId == 0) {
                            ToastUtils.showMsg(context, R.string.app_derail_json_parse_error);
                        } else {
                            intent = DetailActivity.newIntent(context, Integer.parseInt(info.getPageId()), route,
                                    !TextUtils.isEmpty(info.getTask()) && info.getTask().equals(JumpInfo.TASK_AUTO_INSTALL) ? true : false,
                                    isOutSideIn, info.getUrl(), info.getMd5());
                        }
                    }
                    break;
                case WEB_PAGE_ONE_TYPE:
                case WEB_PAGE_ACTIVITY_TYPE:
                case WEB_PAGE_TWO_TYPE:         // 网页
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        intent = WebViewActivity.newIntent(context,
                                info.getUrl(), isOutSideIn);
                    }
                    break;
                case VOUCHER_PAGE_TYPE:         // 代金券
                    if (IdentityHelper.isLogined(context)) {
                        intent = MyVoucherActivity.newIntent(context, isOutSideIn);
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;
                case SCORE_PAGE_TYPE:     // 跳转到赚积分
                    intent = UserTaskActivity.newIntent(context, isOutSideIn);
                    isActivityFinish = true;
                    break;
                case BIND_PHONE_PAGE_TYPE:  // 跳转到绑定用户手机
                    if (IdentityHelper.isLogined(context)) {
                        if (TextUtils.isEmpty(info.getPageId()))
                            intent = BindPhoneActivity.newIntent(context);
                        else
                            intent = BindPhoneActivity.newIntent(context, info.getPageId());
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;
                case USER_CENTER_PAGE_TYPE:  // 打开个人中心
                    if (IdentityHelper.isLogined(context)) {
                        intent = UserCenterActivity.newIntent(context, isOutSideIn);
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;
                case COLLECTION_FREE_AREA_TYPE:  // 免流量专区
                    if (IdentityHelper.isLogined(context)) {
                        if (!TextUtils.isEmpty(info.getPageId())
                                && !TextUtils.isEmpty(info.getPageTitle())) {
                            intent = AppListActivity.newIntent(context, AppConstants.VALUE_FREEAREA,
                                    Integer.parseInt(info.getPageId()), info.getPageTitle(), true, route, isOutSideIn);
                        }
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;
                case SCAN_PAGE_TYPE:  // 打开扫一扫页面
                    if (context instanceof Activity)
                        ((Activity) context).startActivityForResult(CaptureActivity.newIntent(context, isOutSideIn), CaptureActivity.SCAN_REQUEST_CODE);
                    break;
                case GAME_SPREE_TYPE:  // 打开应用详情的礼包标签页
                    if (!TextUtils.isEmpty(info.getPageId())) {
                        intent = DetailActivity.callScorePage(context, Integer.parseInt(info.getPageId()), route, isOutSideIn);
                    }
                    break;
                case OPEN_URL_IN_SDK:  // 在sdk里面打开网页
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        LoginSDKUtil.openInWebUpload(context, info.getUrl());
                    }
                    break;
                case OPEN_URL_IN_SYSTEM_BROWSER:  // 在系统浏览器里打开网页
                    if (!TextUtils.isEmpty(info.getUrl()) && context instanceof Activity) {
                        ComUtil.openExternalBrowser((Activity) context, info.getUrl());
                    }
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        if (ret && intent != null) {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            if (isActivityFinish && context instanceof Activity)
                ((Activity) context).finish();
        }
        return ret;
    }

    public static void startUserDetailActivity(Context context) {
        context.startActivity(WebViewActivity.newIntent(context,
                JsonUrl.getJsonUrl().JSON_URL_USER_ACTIVITY));
    }


    public static void startDetailActivity(Context context, int appId, int[] route) {
        context.startActivity(DetailActivity.newIntent(context, appId, route));
    }

    public static void startWebActivity(final Context context, String jumpUrl) {
        context.startActivity(WebViewActivity.newIntent(context,
                jumpUrl));
    }

    public static void startCollection(Context context, String refId, String title, int[] route) {
        context.startActivity(CollectionActivity.newIntent(context, Integer.parseInt(refId), route));
    }

    public static void jumpRecmmonGame(Context context, int[] route) {
        int[] newRoute = route.clone();
        newRoute[AppConstants.STATISTCS_DEPTH_FOUR] = AppConstants.STATISTCS_DEFAULT_NULL;
        context.startActivity(QuickPopupActivity.newIntent(context, QuickPopupActivity.TYPE_GAME_RECOMMEND, newRoute));
    }

    public static void jumpSpree(Context context, int[] route) {
        context.startActivity(QuickPopupActivity.newIntent(context, QuickPopupActivity.TYPE_SPREE, route));
    }


    public static void jumpCollection(Context context, int[] route) {
        int[] newRoute = route.clone();
        newRoute[AppConstants.STATISTCS_DEPTH_FOUR] = AppConstants.STATISTCS_DEFAULT_NULL;
        context.startActivity(QuickPopupActivity.newIntent(context, QuickPopupActivity.TYPE_GAME_COLLECTION, newRoute));
    }

    private static void jumpBBS(Context context) {
        boolean isOpen = true;
        SystemConfig systemConfig = PersistentVar.getInstance().getSystemConfig();
        String channels = systemConfig.getHideForumChannelIds();
        String version = systemConfig.getScoreForumStopVersion();
        if (systemConfig.getStopForumFunc() == 1) {
            if (version.contains(String.valueOf(ComUtil.getSelfVersionCode())) || channels.contains(ChannelUtil.getChannelID())) {
                isOpen = false;
            }
        }

        context.startActivity(WebViewActivity.newIntent(context, systemConfig.getBbsUrl(),
                isOpen, systemConfig.getStopForumDes(), AppConstants.WEBVIEW_MODEL_BBS));
    }

    private static void jumpScoreActivity(Context context) {
        context.startActivity(UserTaskActivity.newIntent(context));
    }

    public static void itemJump(Context context, String jumpUrl, String source, ContentModel contentModel, int[] route) {
        //跳转原则 优先跳转url 然后才是应用内跳转
        if (!TextUtils.isEmpty(source) && TextUtils.isDigitsOnly(source)) {
            //应用内跳转根据souce字段
            switch (Integer.parseInt(source)) {
                case AppConstants.SOURCE_SPREE:
                    //礼包
                    jumpSpree(context, route);
                    break;
                case AppConstants.SOURCE_RECOMMENT_GAME:
                    //游戏
                    String refId = contentModel.getsRefId();
                    if (!TextUtils.isEmpty(refId) && TextUtils.isDigitsOnly(refId))
                        startDetailActivity(context, Integer.parseInt(refId), route);
                    break;
                case AppConstants.SOURCE_COLLECTION:
                    //合集
                    startCollection(context, contentModel.getsRefId(), contentModel.getsTitle(), route);
                    break;
                case AppConstants.SOURCE_STRATEGY:
                    //资讯
                    context.startActivity(AppNewsActivity.newIntent(context));
                    break;
                case AppConstants.SOURCE_BBS:
                    //论坛
                    jumpBBS(context);
                    break;
                case AppConstants.SOURCE_GAME:
                    //跳转到游戏推荐
                    String id = contentModel.getsRefId();
                    if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id))
                        startDetailActivity(context, Integer.parseInt(id), route);
                    break;
                case AppConstants.SOURCE_APP:
                    //跳转到应用推荐
                    int[] newRoute = route.clone();
                    newRoute[AppConstants.STATISTCS_DEPTH_FOUR] = AppConstants.STATISTCS_DEFAULT_NULL;
                    context.startActivity(QuickPopupActivity.newIntent(context, QuickPopupActivity.TYPE_APP_RECOMMEND, newRoute));
                    break;
                default:
                    if (!TextUtils.isEmpty(jumpUrl)) {
                        if (Integer.parseInt(source) == AppConstants.SOURCE_STRATEGY) {
                            context.startActivity(WebViewActivity.newIntent(context,
                                    jumpUrl, route));
                        } else {
                            startWebActivity(context, jumpUrl);
                        }

                    }
                    break;
            }
        } else if (!TextUtils.isEmpty(jumpUrl)) {
            if (Integer.parseInt(source) == AppConstants.SOURCE_STRATEGY) {
                context.startActivity(WebViewActivity.newIntent(context,
                        jumpUrl, route));
            } else {
                startWebActivity(context, jumpUrl);
            }

        }
    }

    // 跳转原则 优先应用内部跳转，若不支持，这跳转指定网页
    public static void templateJump(final Activity activity, String jumpUrl, String source, String refId, String title, int[] route) {
        if (!TextUtils.isEmpty(source) && TextUtils.isDigitsOnly(source)) {
            //应用内跳转根据souce字段
            switch (Integer.parseInt(source)) {
                case AppConstants.SOURCE_SPREE:
                    //礼包
                    jumpSpree(activity, route);
                    break;
                case AppConstants.SOURCE_RECOMMENT_GAME:
                    //跳转到游戏推荐
                    jumpRecmmonGame(activity, route);
                    break;
                case AppConstants.SOURCE_COLLECTION:
                    //合集
                    int id = 0;
                    try {
                        id = Integer.parseInt(refId);
                    } catch (Exception e) {

                    }
                    if (TextUtils.isEmpty(refId) || id == 0)
                        jumpCollection(activity, route);
                    else
                        activity.startActivity(CollectionActivity.newIntent(activity, id, route));
                    break;
                case AppConstants.SOURCE_STRATEGY:
                    //攻略
                    activity.startActivity(AppNewsActivity.newIntent(activity));
                    break;

                case AppConstants.SOURCE_BBS:
                    //论坛
                    if (PersistentVar.getInstance().getSystemConfig().isCxbStatus() && SharedPreferencesUtil.getInstance().isCxbPhoneNumber()) {
                        if (!ComUtil.checkApkExist(FreeStoreApp.getContext(), PersistentVar.getInstance().getSystemConfig().getCxbPackageName())) {
                            startDetailActivity(activity, PersistentVar.getInstance().getSystemConfig().getCxbAppId(), route);
                        } else {
                            try {
                                Intent intent = new Intent("com.snail.cxb");
                                intent.putExtra("phnum", SharedPreferencesUtil.getInstance().getPhoneNumber());
                                activity.startActivity(intent);
                            } catch (Exception e) {

                            }
                        }
                    } else
                        jumpBBS(activity);
                    break;
                case AppConstants.SOURCE_GAME:
                    //跳转到游戏推荐
                    jumpRecmmonGame(activity, route);
                    break;

                case AppConstants.SOURCE_SCORE:
                    //跳转到积分墙
                    jumpScoreActivity(activity);
                    break;
                case AppConstants.SOURCE_APP:
                    //跳转到应用推荐
                    activity.startActivity(QuickPopupActivity.newIntent(activity, QuickPopupActivity.TYPE_APP_RECOMMEND, route));
                    break;

                case AppConstants.SOURCE_APP_MODEL:
                    activity.startActivity(FragmentContainerActivity.newIntent(activity,
                            FragmentContainerActivity.ACTIVITY_SEEK_APP, activity.getString(R.string.home_app_title)));
                    break;

                case AppConstants.SOURCE_TEST_NEW_APP:
                    activity.startActivity(FragmentContainerActivity.newIntent(activity,
                            FragmentContainerActivity.ACTIVITY_APP_APPOINTMENT, activity.getString(R.string.home_test_new_app)));
                    break;

                case AppConstants.SOURCE_CHARGE:
                    //跳转到支付界面
                    LoginSDKUtil.snailGoToCharge(activity, new LoginSDKUtil.onPayListener() {
                        @Override
                        public void finishPayProcess(int i) {
                            if (LoginSDKUtil.SNAIL_COM_PLATFORM_SUCCESS == i) {
                                //充值成功
                                activity.startService(UserInfoGetService.newIntent(activity, AppConstants.ACTION_UPDATE_USR_INFO));
                            }
                        }
                    });
                    break;

                case AppConstants.SOURCE_NEWS:
                    activity.startActivity(NewsActivity.newIntent(activity));
                    break;

                case AppConstants.SOURCE_FRIEND:
                    if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                        AccountUtil.userLogin(activity);
                        return;
                    } else {
                        activity.startActivity(FriendMainActivity.newIntent(activity));
                    }
                    break;
                default:
                    if (!TextUtils.isEmpty(jumpUrl)) {
                        startWebActivity(activity, jumpUrl);
                    }
                    break;
            }
        } else if (!TextUtils.isEmpty(jumpUrl)) {
            startWebActivity(activity, jumpUrl);
        }
    }
}
