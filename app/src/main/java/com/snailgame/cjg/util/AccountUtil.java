package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.event.CxbPhoneNumberChangeEvent;
import com.snailgame.cjg.event.ScratchInfoChangeEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.navigation.BindPhoneActivity;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.fastdev.util.ResUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * 跟用户相关的操作类
 * 登陆 登出的 Dialog和函数， 充值， 绑定账户手机号码
 * Created by TAJ_C on 2014/10/20.
 */
public class AccountUtil {
    public static String getLoginParams() {
        String userId = AppConstants.STORE_ACCESS_KEY_USER_ID + "=" + IdentityHelper.getUid(FreeStoreApp.getContext());
        String identity = AppConstants.STORE_ACCESS_KEY_IDENTITY + "=" + IdentityHelper.getIdentity(FreeStoreApp.getContext());
        String appId = AppConstants.STORE_ACCESS_KEY_APP_ID + "=" + IdentityHelper.getAppId();

        return "?" + userId + "&" + identity + "&" + appId;
    }

    public static Map<String, String> getLoginMapParams() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(AppConstants.STORE_ACCESS_KEY_USER_ID, "" + IdentityHelper.getUid(FreeStoreApp.getContext()));
        parameters.put(AppConstants.STORE_ACCESS_KEY_IDENTITY, "" + IdentityHelper.getIdentity(FreeStoreApp.getContext()));
        parameters.put(AppConstants.STORE_ACCESS_KEY_APP_ID, "" + IdentityHelper.getAppId());
        return parameters;
    }


    public static void userLogin(Context context) {
        LoginSDKUtil.snailLogin(context, getOnLoginListener());
    }

    public static void userLogout(Context context) {
        LoginSDKUtil.snailLogout(context);
        context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_USER_LOGOUT));
        MainThreadBus.getInstance().post(new ScratchInfoChangeEvent(null));
        StaticsUtils.resetAccount(context);
    }

    // 登录回调
    public static LoginSDKUtil.onLoginListener getOnLoginListener() {
        return new LoginSDKUtil.onLoginListener() {
            public void finishLoginProcess(int flag) {
                if (flag == LoginSDKUtil.SNAIL_COM_PLATFORM_SUCCESS) {
                    FreeStoreApp.getContext().startService(UserInfoGetService.newIntent(FreeStoreApp.getContext(), AppConstants.ACTION_USER_LOGIN));
                    StaticsUtils.resetAccount(FreeStoreApp.getContext());
                }
            }
        };
    }

    /**
     * 用户覆盖
     *
     * @param context
     * @param uid
     * @param sessionId
     */
    public static void userCover(Context context, String uid, String sessionId, String account) {
        LoginSDKUtil.setUserCover(context, uid, sessionId, account);

        context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_USER_COVER));
        MainThreadBus.getInstance().post(new ScratchInfoChangeEvent(null));

        StaticsUtils.resetAccount(FreeStoreApp.getContext());
    }

    /**
     * 账户 手机绑定
     *
     * @param context
     * @param bind
     */
    public static void showNavigation(Context context, boolean bind) {
        if (!bind) {
            String phoneNumber = null;
            // 有sim卡一键绑定 否则输入号码绑定
            if (PhoneUtil.getSimState(context) != TelephonyManager.SIM_STATE_ABSENT) {
                phoneNumber = ComUtil.readPhoneNumber(context);
                if (phoneNumber != null && phoneNumber.contains("+86"))
                    phoneNumber = phoneNumber.replace("+86", "");
            }
            if (phoneNumber == null) {
                context.startActivity(BindPhoneActivity.newIntent(context));
            } else {
                context.startActivity(BindPhoneActivity.newIntent(context, phoneNumber));
            }
        }
    }


    /**
     * 充值
     *
     * @param money 充值最低金额
     */
    public static void recharge(Activity activity, int money, LoginSDKUtil.onPayListener listener) {
        if (money <= 0)
            LoginSDKUtil.snailGoToCharge(activity, listener);
        else
            LoginSDKUtil.snailGoToCharge(activity, money, listener);
    }

    /**
     * 登陆失败 重新登录
     *
     * @param activity
     * @param tipText
     */
    public static void createLoginAgain(final Activity activity, String tipText) {
        final Dialog dialog = new Dialog(activity, R.style.Dialog);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView textView = ButterKnife.findById(dialog, R.id.tv_show_hint);
        textView.setText(tipText);
        textView.setTextColor(ResUtil.getColor(R.color.primary_text_color));

        TextView sureBtn = ButterKnife.findById(dialog, R.id.btn_sure);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // TAJ 调用登录函数未弹出登录界面，故 调用一次退出函数
                userLogout(activity);
                userLogin(activity);
            }
        });
        if (!ComUtil.isActivityNullOrFinish(activity)) {
            dialog.show();
        }
    }


    // 账户信息更改
    public static void onUsrInfoChange(UserInfo userInfo) {
        if (userInfo == null) {
            GlobalVar.getInstance().setUsrInfo(null);
            SharedPreferencesUtil.getInstance().setPoint("0");
            SharedPreferencesUtil.getInstance().setReplaceDomain("");
            SharedPreferencesUtil.getInstance().setNickName("");
            SharedPreferencesUtil.getInstance().setFlowFree(false);
            SharedPreferencesUtil.getInstance().setSnailFlowFree(false);
            SharedPreferencesUtil.getInstance().setSnailPhoneNumber(false);
            SharedPreferencesUtil.getInstance().setArea("x");
            SharedPreferencesUtil.getInstance().setPhoneNumber("");
            SharedPreferencesUtil.getInstance().setContractMachine(false);
            // 退出免商店 清除免卡信息
            SharedPreferencesUtil.getInstance().removeLastLoginID();

            SharedPreferencesUtil.getInstance().setFlowFreeStart("");
            SharedPreferencesUtil.getInstance().setFlowFreeEnd("");
            SharedPreferencesUtil.getInstance().setFlowFreeSize("");
            updateCxbPhoneNumber(false);
            AppConstants.account_type = "-1";
            AppConstants.login = false;
        } else {
            GlobalVar.getInstance().setUsrInfo(userInfo);

            SharedPreferencesUtil.getInstance().setFlowFree(userInfo.isbFreeFlow());
            SharedPreferencesUtil.getInstance().setSnailFlowFree(userInfo.isbFreeFlowAllPlatform());
            SharedPreferencesUtil.getInstance().setSnailPhoneNumber(userInfo.isbBossAccount());
            SharedPreferencesUtil.getInstance().setArea(userInfo.getsVendorArea());
            SharedPreferencesUtil.getInstance().setPoint(String.valueOf(userInfo.getiIntegral()));
            SharedPreferencesUtil.getInstance().setReplaceDomain(userInfo.getcOrientationDomain());
            //TODO
            SharedPreferencesUtil.getInstance().setNickName(userInfo.getsNickName());
            SharedPreferencesUtil.getInstance().setPhoneNumber(userInfo.getcPhone());
            SharedPreferencesUtil.getInstance().setFlowFreeStart(userInfo.getdStart());
            SharedPreferencesUtil.getInstance().setFlowFreeEnd(userInfo.getdEnd());
            SharedPreferencesUtil.getInstance().setFlowFreeSize(userInfo.getcFlowSize());

            SharedPreferencesUtil.getInstance().setContractMachine(userInfo.iscContractMachine());
            updateCxbPhoneNumber(userInfo.isbCxbNumber());
            AppConstants.account_type = userInfo.getcType();

            //更新网厅信息
            FreeStoreApp.getContext().sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_ONLINESHOP));
            /*
            * 用户免流量状态发生变化时，发送广播
            */
            FreeStoreApp.getContext().sendBroadcast(new Intent(AppConstants.ACTION_FREE_ACCOUNT_STATUS_CHANGE));
        }
        // 关闭百宝箱 切换首页免流量标识
        MainThreadBus.getInstance().post(new UserInfoLoadEvent());

        //代理信息更新
        HostUtil.replaceHost();
        GlobalVar.getInstance().generateUserAgent(FreeStoreApp.getContext());

    }

    // 更新用户畅享宝号段状态
    public static void updateCxbPhoneNumber(boolean bCxbNumber) {
        if (SharedPreferencesUtil.getInstance().isCxbPhoneNumber() == bCxbNumber)
            return;

        if (GlobalVar.getInstance().getUsrInfo() != null) {
            GlobalVar.getInstance().getUsrInfo().setbCxbNumber(bCxbNumber);
        }

        SharedPreferencesUtil.getInstance().setCxbPhoneNumber(bCxbNumber);
        MainThreadBus.getInstance().post(new CxbPhoneNumberChangeEvent());
    }

    public static boolean isUsrInfoGet() {
        return (FreeStoreApp.statusOfUsr == AppConstants.USR_STATUS_SUCCESS && GlobalVar.getInstance().getUsrInfo() != null);
    }

    public static void usrInfoNoSuccess(Activity activity, int status) {
        switch (status) {
            case AppConstants.USR_STATUS_EXPIRE_ERROR:
                AccountUtil.createLoginAgain(activity,
                        ResUtil.getString(R.string.personal_expire_tip));
                break;

            case AppConstants.USR_STATUS_FAILED:
                AccountUtil.createLoginAgain(activity,
                        ResUtil.getString(R.string.personal_info_failed));
                break;

            case AppConstants.USR_STATUS_NETWORK_ERROR:
                AccountUtil.createLoginAgain(activity, ResUtil.getString(R.string.personal_network_error));

            case AppConstants.USR_STATUS_RUNNING:
                ToastUtils.showMsg(activity, R.string.personal_info_running);
                break;

            default:
                if (GlobalVar.getInstance().getUsrInfo() == null) {
                    AccountUtil.createLoginAgain(activity,
                            ResUtil.getString(R.string.personal_info_failed));
                }

                break;
        }
    }

    /**
     * 在免流量体验区是否免
     *
     * @return
     */
    public static boolean isFreeAreaFree() {
        return SharedPreferencesUtil.getInstance().isFlowFree();
    }


    /**
     * 在免商店是否免（免流量体验区外）
     *
     * @return
     */
    public static boolean isFree() {
        return SharedPreferencesUtil.getInstance().isFlowFree() && SharedPreferencesUtil.getInstance().isSnailFlowFree();
    }

    /**
     * 代理用户是否免
     *
     * @return
     */
    public static boolean isAgentFree(Context context) {
        return (IdentityHelper.isLogined(context)
                && AppConstants.account_type.equals(AppConstants.AGENT_TYPE)
                && (isFree() || isFreeAreaFree()));
    }

    /**
     * 判断是否是vip 用户
     *
     * @param vip
     * @return
     */
    public static boolean isVipUser(int vip) {
        return vip >= 0;
    }


    /**
     * 判断是否是会员
     *
     * @return
     */
    public static boolean isMember() {
        return GlobalVar.getInstance().getMemberInfo() != null && GlobalVar.getInstance().getMemberInfo().getCurrentlevel() != null &&
                GlobalVar.getInstance().getMemberInfo().getCurrentlevel().getiLevelId() > 0;
    }


    /**
     * 判断是否为通信用户
     *
     * @return
     */
    public static synchronized boolean isCommunicationUser() {
        return isUsrInfoGet() && GlobalVar.getInstance().getUsrInfo().isbBossAccount();
    }
}
