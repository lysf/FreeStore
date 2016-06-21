package com.snailgame.cjg.common.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.snailgame.cjg.common.model.CxbModel;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.event.MemberChangeEvent;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.event.UserPrivilegesEvent;
import com.snailgame.cjg.event.VoucherUpdateEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.member.model.MemberInfoNetModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.VoucherNotifyActivity;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.personal.model.UserInfoModel;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.cjg.personal.model.VoucherModel;
import com.snailgame.cjg.settings.AutoReLoginBBsService;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息获取 更新Service
 * Created by taj on 2014/11/10.
 */
public class UserInfoGetService extends IntentService {
    static String TAG = UserInfoGetService.class.getName();

    public static Intent newIntent(Context context) {
        return new Intent(context, UserInfoGetService.class);
    }

    public static Intent newIntent(Context context, String actionType) {
        Intent intent = newIntent(context);
        intent.putExtra(AppConstants.ACTION_USER_TYPE, actionType);
        return intent;
    }

    public UserInfoGetService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //startServer 传过来的type, 用户操作type 如-- 登录 退出 更新用户信息
            String type = intent.getStringExtra(AppConstants.ACTION_USER_TYPE);
            if (type == null) {
                if (LoginSDKUtil.isLogined(FreeStoreApp.getContext())
                        && FreeStoreApp.statusOfUsr == AppConstants.USR_STATUS_IDLE) { //当前未处于获取用户信息状态
                    //第一次登陆 需调用， 发出登陆通知
                    userLoginReceiver(this);
                }
            } else {
                switch (type) {
                    //用户登录
                    case AppConstants.ACTION_USER_LOGIN:
                        userLoginReceiver(this);
                        break;
                    //用户登出
                    case AppConstants.ACTION_USER_LOGOUT:
                        userLogoutReceiver(this);
                        break;
                    //用户数据覆盖 用于游戏SDK
                    case AppConstants.ACTION_USER_COVER:
                        userCover(this);
                        break;
                    //用户数据更新
                    case AppConstants.ACTION_UPDATE_USR_INFO:
                        usrInfoChangeReceiver();
                        break;
                    //获取代金券数量
                    case AppConstants.ACTION_GET_VOUCHER_NUM:
                        getVoucherNum();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 用户登录
     *
     * @param context
     */
    private void userLoginReceiver(Context context) {
        MainThreadBus.getInstance().post(new UserLoginEvent());
        context.sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_COMMON));

        // Get user info
        getUserInfoAndStatus();
        getUserPrivileges();
        getVoucherNum();
        // Get volume from the server

        getMemberInfo();

        AppConstants.login = true;

        if (!ComUtil.isServiceRunning(context, AutoReLoginBBsService.class.getName())) {
            context.startService(AutoReLoginBBsService.newIntent(context));
        }
    }

    /**
     * 用户登出
     *
     * @param context
     */
    private void userLogoutReceiver(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        MainThreadBus.getInstance().post(new UserLogoutEvent());
        context.sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_COMMON));

        AccountUtil.onUsrInfoChange(null);
        FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_IDLE;
        GlobalVar.getInstance().setUsrPrivileges(null);
        GlobalVar.getInstance().setMemberInfo(null);

        SharedPreferencesUtil.getInstance().setBindCid(false);

        AutoReLoginBBsService.cancelReLoginAlarm(context);
    }


    /**
     * 用户信息覆盖
     *
     * @param context
     */
    private void userCover(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        AccountUtil.onUsrInfoChange(null);
        FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_IDLE;
        GlobalVar.getInstance().setUsrPrivileges(null);
        GlobalVar.getInstance().setMemberInfo(null);
        SharedPreferencesUtil.getInstance().setBindCid(false);

        AutoReLoginBBsService.cancelReLoginAlarm(context);

        MainThreadBus.getInstance().post(new UserLoginEvent());
        context.sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_COMMON));
        // Get user info
        getUserInfoAndStatus();
        getUserPrivileges();
        getVoucherNum();
        getMemberInfo();
        // Get volume from the server

        AppConstants.login = true;

        if (!ComUtil.isServiceRunning(context, AutoReLoginBBsService.class.getName())) {
            context.startService(AutoReLoginBBsService.newIntent(context));
        }
    }

    /**
     * 用户信息更新
     */
    private void usrInfoChangeReceiver() {
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext())) {
            getUserInfoAndStatus();
            getUserPrivileges();
            getVoucherNum();
            getMemberInfo();
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfoAndStatus() {
        FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_RUNNING;
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_USER_INFO, TAG, UserInfoModel.class, new IFDResponse<UserInfoModel>() {
            @Override
            public void onSuccess(final UserInfoModel result) {
                if (result == null) {
                    FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_FAILED;
                    return;
                }

                if (result.getCode() == 0 && result.getItemModel() != null) {
                    FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_SUCCESS;
                    UserInfo info = result.getItemModel();
                    AccountUtil.onUsrInfoChange(info);

                    // Cxb打开且用户手机号不为空的情况下，检测手机号是否为蜗牛号段
                    if (PersistentVar.getInstance().getSystemConfig().isCxbStatus() && !TextUtils.isEmpty(info.getcPhone()))
                        checkCxbPhone(info.getcPhone());
                } else if (result.getCode() == 1008) {
                    FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_EXPIRE_ERROR;
                } else {
                    FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_FAILED;
                }

                MainThreadBus.getInstance().post(new UserInfoChangeEvent());
            }

            @Override
            public void onNetWorkError() {
                FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_NETWORK_ERROR;
                MainThreadBus.getInstance().post(new UserInfoChangeEvent());
            }

            @Override
            public void onServerError() {
                FreeStoreApp.statusOfUsr = AppConstants.USR_STATUS_NETWORK_ERROR;
                MainThreadBus.getInstance().post(new UserInfoChangeEvent());
            }
        }, false, true, new ExtendJsonUtil());
    }

    /**
     * 获取用户特权
     */
    private void getUserPrivileges() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_USR_PRIVILEGES, TAG, UserPrivilegesModel.class, new IFDResponse<UserPrivilegesModel>() {
            @Override
            public void onSuccess(UserPrivilegesModel result) {
                if (result == null) {
                    return;
                }

                if (result.getCode() == 0 && !ListUtils.isEmpty(result.getItemList())) {
                    GlobalVar.getInstance().setUsrPrivileges(result);
                    List<UserPrivilegesModel.ModelItem> itemList = new ArrayList<>();
                    for (UserPrivilegesModel.ModelItem item : result.getItemList()) {
                        if (item.isOpened() && TextUtils.equals(item.getcNotice(), UserPrivilegesModel.ModelItem.NEED_NOTICE)) {
                            itemList.add(item);
                        }
                    }
                    if (itemList.size() > 0) {
                        startActivity(VoucherNotifyActivity.newIntent(FreeStoreApp.getContext(), result.getItemList()));
                    }
                    MainThreadBus.getInstance().post(new UserPrivilegesEvent());
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, false, true, new ExtendJsonUtil());
    }


    /**
     * 获取代金券数量
     */
    private void getVoucherNum() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_VOUCHER_NUM, TAG, VoucherModel.class, new IFDResponse<VoucherModel>() {
            @Override
            public void onSuccess(VoucherModel result) {
                if (result != null && result.getItemModel() != null) {
                    boolean isVoucherWoniu = PersistentVar.getInstance().getSystemConfig().isVoucherWoniuSwitch();

                    int voucherNum = isVoucherWoniu ? result.getItemModel().getTotalVoucher() :
                            result.getItemModel().getFreestoreVoucher() + result.getItemModel().getKuwanVoucher();

                    SharedPreferencesUtil.getInstance().setVoucherNum(voucherNum);
                    MainThreadBus.getInstance().post(new VoucherUpdateEvent(voucherNum));

                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, false, true, new ExtendJsonUtil());
    }


    /**
     * 查询会员信息
     */
    private void getMemberInfo() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_MEMBER_INFO, TAG, MemberInfoNetModel.class, new IFDResponse<MemberInfoNetModel>() {
            @Override
            public void onSuccess(MemberInfoNetModel result) {
                if (result == null) {
                    return;
                }
                if (result.getCode() == 0) {
                    GlobalVar.getInstance().setMemberInfo(result.getMemberInfoModel());
                    MainThreadBus.getInstance().post(new MemberChangeEvent());
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, false, true, new ExtendJsonUtil());
    }


    /**
     * // 畅享宝号段信息检验
     *
     * @param phoneNumber
     */
    private void checkCxbPhone(String phoneNumber) {
        String url = JsonUrl.getJsonUrl().JSON_URL_CXB_PHONE_CHECK + "?phnum=" + phoneNumber;
        FSRequestHelper.newGetRequest(url, TAG, CxbModel.class, new IFDResponse<CxbModel>() {
            @Override
            public void onSuccess(CxbModel result) {
                boolean isCxbPhone = false;
                if (result != null && result.getMsgcode() == 1) {
                    isCxbPhone = true;
                }

                AccountUtil.updateCxbPhoneNumber(isCxbPhone);
            }

            @Override
            public void onNetWorkError() {
                AccountUtil.updateCxbPhoneNumber(false);
            }

            @Override
            public void onServerError() {
                AccountUtil.updateCxbPhoneNumber(false);
            }
        }, false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
