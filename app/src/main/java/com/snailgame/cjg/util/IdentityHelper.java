package com.snailgame.cjg.util;

import android.content.Context;

import com.snailgame.cjg.global.AppConstants;

/**
 * Created by xingfinal on 14-2-18.
 */
public class IdentityHelper {

   public static boolean isLogined(Context context) {
        return LoginSDKUtil.isLogined(context);
    }

    public static String getAccount(Context context) {
        String account = LoginSDKUtil.getAccount(context);
        if(account==null)
            account="";
        return account;
    }

    public static String getDisplayAccount(Context context) {
        String account = LoginSDKUtil.getDisplayAccount(context);
        if(account==null)
            account="";
        return account;
    }

    public static String getUid(Context context) {
        return LoginSDKUtil.getLoginUin(context);
    }

    public static String getIdentity(Context context) {
        return LoginSDKUtil.getSessionId(context);
    }

    public static String getNickName(Context context) {
        return LoginSDKUtil.getNickName(context);
    }

    public static String getPortrait(Context context) {
        return LoginSDKUtil.getPhoto(context);
    }

    public static int getAppId() {
        return AppConstants.APP_ID;
    }

}
