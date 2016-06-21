package com.snailgame.cjg.util;

import android.text.TextUtils;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * 免流量url跳转
 * Created by xixh on 2016/3/21.
 */
public class HostUtil {

    /**
     * 添加替换的host
     */
    public static void replaceHost() {
        cleanHost();
        String domain = SharedPreferencesUtil.getInstance().getReplaceDomain();
        boolean isFree = AccountUtil.isFree();
        if (isFree && !TextUtils.isEmpty(domain) && !NetworkUtils.isWifiEnabled(FreeStoreApp.getContext())) {
            JsonUrl.DEFAULT_DOMAIN = domain;
            JsonUrl.resetJsonUrl();
        }
    }


    /**
     * 清空替换的host
     */
    public static void cleanHost() {
        JsonUrl.DEFAULT_DOMAIN = JsonUrl.DEFAULT_DOMAIN_VALUE;
        JsonUrl.resetJsonUrl();
    }

    /**
     * 替换url
     */
    public static String replaceUrl(String url) {
        if (url != null) {
            String domain = SharedPreferencesUtil.getInstance().getReplaceDomain();
            boolean isFree = AccountUtil.isFree();
            if (isFree && !TextUtils.isEmpty(domain) && !NetworkUtils.isWifiEnabled(FreeStoreApp.getContext())) {
                if (url.contains("." + JsonUrl.DEFAULT_DOMAIN_VALUE))
                    url = url.replace("." + JsonUrl.DEFAULT_DOMAIN_VALUE, "." + domain);
                return url;
            }
        }

        return url;
    }

    /**
     * 替换url(下载)
     */
    public static String replaceUrl(String url, int iFreeArea, String cFreeFlow) {
        if (url != null) {
            String domain = SharedPreferencesUtil.getInstance().getReplaceDomain();
            boolean isFree = AccountUtil.isFree();
            if (!TextUtils.isEmpty(domain) && !NetworkUtils.isWifiEnabled(FreeStoreApp.getContext())) {
                if (isFree || ((iFreeArea == AppConstants.FREE_AREA_IN || AppInfo.isFreeArea(cFreeFlow))
                        && AccountUtil.isFreeAreaFree())) {
                    if (url.contains("." + JsonUrl.DEFAULT_DOMAIN_VALUE))
                        url = url.replace("." + JsonUrl.DEFAULT_DOMAIN_VALUE, "." + domain);
                    return url;
                }
            }
        }

        return url;
    }
}
