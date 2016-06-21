package com.snailgame.cjg.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.snail.pay.PayConst;
import com.snail.statistics.SnailStatistics;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.mobilesdk.OnLoginProcessListener;
import com.snailgame.mobilesdk.OnPayProcessListener;
import com.snailgame.mobilesdk.SnailCommplatform;
import com.snailgame.mobilesdk.SnailErrorCode;
import com.snailgame.mobilesdk.aas.ui.WebUploadActivity;

/**
 * Created by xixh on 2015/4/28.
 */
public class LoginSDKUtil extends SnailErrorCode {

    public static final int MOBILE_WEB_PAGE = PayConst.PagerType.MOBILE_WEB_PAGE.value();
    public static final int STORE_CHARGE_PAGE = PayConst.PagerType.STORE_CHARGE_PAGE.value();
    public static final int GAME_WEB_PAGE = PayConst.PagerType.GAME_WEB_PAGE.value();

    /**
     * 是否已登录
     *
     * @param context
     * @return
     */
    public static boolean isLogined(Context context) {
        return SnailCommplatform.getInstance().isLogined(context);
    }

    /**
     * 设置渠道号
     *
     * @param context
     * @param cid
     */
    public static void setChannelID(Context context, String cid) {
        SnailCommplatform.getInstance().setChannelID(context, cid);
    }

    /**
     * 登录论坛
     */
    public static void snailBBsLogin() {
        SnailCommplatform.getInstance().snailBBsLogin();
    }


    /**
     * 设置上下文
     *
     * @param context
     */
    public static void setContext(Context context) {
        SnailCommplatform.getInstance().setContext(context);
    }


    /**
     * 获取账号名
     *
     * @param context
     * @return
     */
    public static String getAccount(android.content.Context context) {
        return SnailCommplatform.getInstance().getAccount(context);
    }

    /**
     * 登录
     *
     * @param context
     * @param onLoginProcessListener
     */
    public static void snailLogin(Context context, OnLoginProcessListener onLoginProcessListener) {
        SnailCommplatform.getInstance().snailLogin(context, onLoginProcessListener);
    }

    /**
     * 登出
     *
     * @param context
     */
    public static void snailLogout(Context context) {
        SnailCommplatform.getInstance().snailLogout(context);
    }


    /**
     * 充值
     *
     * @param activity
     * @param onPayProcessListener
     * @param page
     */
    public static void snailGoToCharge(Activity activity, OnPayProcessListener onPayProcessListener, int page) {
        SnailCommplatform.getInstance().snailGoToCharge(activity, onPayProcessListener, AccountUtil.getOnLoginListener(),
                PersistentVar.getInstance().getSystemConfig().getRechargeCurrencyUrl(), PersistentVar.getInstance().getSystemConfig().getRechargeGameUrl(),
                page, PersistentVar.getInstance().getSystemConfig().getRechargeFlowUrl());
    }


    /**
     * 充值
     *
     * @param activity
     * @param onPayProcessListener
     */
    public static void snailGoToCharge(Activity activity, OnPayProcessListener onPayProcessListener) {
        snailGoToCharge(activity, onPayProcessListener, MOBILE_WEB_PAGE);
    }

    /**
     * 充值
     *
     * @param activity
     * @param leastCharge
     * @param onPayProcessListener
     * @param page
     */
    public static void snailGoToCharge(Activity activity, int leastCharge, OnPayProcessListener onPayProcessListener, int page) {
        SnailCommplatform.getInstance().snailGoToCharge(activity, leastCharge, onPayProcessListener, AccountUtil.getOnLoginListener(),
                PersistentVar.getInstance().getSystemConfig().getRechargeCurrencyUrl(), PersistentVar.getInstance().getSystemConfig().getRechargeGameUrl(),
                page, PersistentVar.getInstance().getSystemConfig().getRechargeFlowUrl());
    }

    /**
     * 充值
     *
     * @param activity
     * @param leastCharge
     * @param onPayProcessListener
     */
    public static void snailGoToCharge(Activity activity, int leastCharge, OnPayProcessListener onPayProcessListener) {
        snailGoToCharge(activity, leastCharge, onPayProcessListener, MOBILE_WEB_PAGE);
    }

    /**
     * 获取账号（登录过程中可能会用账号登陆或者手机登录，这个字段表示的就是如果你是手机登录或者账号登录中间任何一种登陆成功后显示的名称）
     *
     * @param context
     * @return
     */
    public static String getDisplayAccount(Context context) {
        return SnailCommplatform.getInstance().getDisplayAccount(context);
    }

    /**
     * 获取用户ID
     *
     * @param context
     * @return
     */
    public static String getLoginUin(Context context) {
        return SnailCommplatform.getInstance().getLoginUin(context);
    }

    /**
     * 获取Session
     *
     * @param context
     * @return
     */
    public static String getSessionId(Context context) {
        return SnailCommplatform.getInstance().getSessionId(context);
    }

    /**
     * 设置用户登录信息
     *
     * @param context
     * @param uid
     * @param sessionId
     */
    public static void setUserCover(Context context, String uid, String sessionId, String account) {
        SnailCommplatform.getInstance().setLoginUid(context, uid);
        SnailCommplatform.getInstance().setSessionId(context, sessionId);
        if (!TextUtils.isEmpty(account))
            SnailCommplatform.getInstance().setAccount(context, account);
    }


    /**
     * 获取昵称
     *
     * @param context
     * @return
     */
    public static String getNickName(Context context) {
        return SnailCommplatform.getInstance().getNickName(context);
    }

    /**
     * 获取头像
     *
     * @param context
     * @return
     */
    public static String getPhoto(Context context) {
        return SnailCommplatform.getInstance().getPhoto(context);
    }


    /**
     * 网厅支付
     *
     * @param activity
     * @param account
     * @param productName
     * @param orderNum
     * @param orderMoney
     * @param orderSource
     * @param platformId
     * @param onPayProcessListener
     */
    public static void snailNetworkHallPay(Activity activity, String account, String productName, String orderNum, String orderMoney, String orderSource, int platformId, OnPayProcessListener onPayProcessListener) {
        SnailCommplatform.getInstance().snailNetworkHallPay(activity, account, productName, orderNum, orderMoney, orderSource, platformId, onPayProcessListener);
    }

    /**
     * 修改密码
     *
     * @param context
     */
    public static void snailChangePsw(Context context) {
        SnailCommplatform.getInstance().snailChangePsw(context);
    }

    /**
     * 绑定手机号
     *
     * @param context
     */
    public static void snailBindMobile(Context context) {
        SnailCommplatform.getInstance().snailBindMobile(context);
    }

    /**
     * 使用SDK网页打开
     *
     * @param context
     * @param url
     * @return
     */
    public static Intent newIntentForWebUpload(Context context, String url) {
        return WebUploadActivity.newIntent(context, url);
    }


    /**
     * 使用SDK网页打开
     *
     * @param context
     * @param url
     */
    public static void openInWebUpload(Context context, String url) {
        WebUploadActivity.actionStart(context, url);
    }

    /**
     * 支付监听
     */
    public static abstract class onPayListener extends OnPayProcessListener {

    }


    /**
     * 登录监听
     */
    public static abstract class onLoginListener extends OnLoginProcessListener {

    }

    /**
     * 设置延迟时间
     *
     * @param minDelay
     * @param maxDelay
     */
    public static void setDelays(int minDelay, int maxDelay) {
        SnailStatistics.setDelays(minDelay, maxDelay);
    }

    /**
     * 统计发送
     *
     * @param context
     * @param url
     */
    public static void commitOneEvent(Context context, String url) {
        SnailStatistics.commitOneEvent(context, url);
    }

    /**
     * 统计发送
     *
     * @param context
     * @param url
     * @param intime
     * @param addData
     * @param channelId
     */
    public static void commitOneEvent(Context context, String url, boolean intime, String addData, String channelId) {
        SnailStatistics.commitOneEvent(context, url, intime, addData, channelId);
    }

    /**
     * 统计发送
     *
     * @param context
     * @param url
     * @param intime
     * @param addData
     */
    public static void commitOneEvent(Context context, String url, boolean intime, String addData) {
        SnailStatistics.commitOneEvent(context, url, intime, addData);
    }

    /**
     * 统计发送
     *
     * @param context
     * @param url
     * @param channelId
     */
    public static void commitOneEvent(Context context, String url, String channelId) {
        SnailStatistics.commitOneEvent(context, url, GlobalVar.getInstance().getCOMMON_USER_AGENT_LOC(), channelId);
    }
}
