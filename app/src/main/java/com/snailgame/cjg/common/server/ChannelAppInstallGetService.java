package com.snailgame.cjg.common.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.common.model.ChannelAppModel;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.guide.ChannelAppActivity;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

/**
 * 渠道应用安装
 * Created by xixh on 2015/12/18.
 */
public class ChannelAppInstallGetService extends IntentService {
    static String TAG = ChannelAppInstallGetService.class.getName();

    public static Intent newIntent(Context context) {
        return new Intent(context, ChannelAppInstallGetService.class);
    }

    public ChannelAppInstallGetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getChannelApp(TAG, null);
    }


    /**
     * 获取渠道应用
     */
    public static void getChannelApp(String tag, final IChannelGetResult iChannelGetResult) {
        String url = JsonUrl.getJsonUrl().JSON_URL_CHANNEL + "?iChannelId=" + ChannelUtil.getChannelID();
        FSRequestHelper.newGetRequest(url, tag, ChannelAppModel.class, new IFDResponse<ChannelAppModel>() {
            @Override
            public void onSuccess(final ChannelAppModel result) {
                if (result != null && result.getCode() == 0 && result.getItem() != null) {
                    ChannelAppModel.ChannelAppModelItem item = result.getItem();
                    if (TextUtils.isEmpty(item.getCType())) {
                        channelGetResult(iChannelGetResult, false);
                        return;
                    }

                    // 打开应用详情
                    if (item.getCType().equals(ChannelAppModel.ChannelAppModelItem.TYPE_OPEN_APP)
                            || item.getCType().equals(ChannelAppModel.ChannelAppModelItem.TYPE_OPEN_APP_V)) {
                        String MD5 = null, spreeTitle = null, spreeDesc = null;
                        if (!TextUtils.isEmpty(item.getSExtend())) {
                            JSONObject object = JSON.parseObject(item.getSExtend());
                            if (object.containsKey("cMd5"))
                                MD5 = object.getString("cMd5");
                            if (object.containsKey("cSpreeTitle"))
                                spreeTitle = object.getString("cSpreeTitle");
                            if (object.containsKey("cSpreeDesc"))
                                spreeDesc = object.getString("cSpreeDesc");
                        }
                        Intent intent = ChannelAppActivity.newIntent(FreeStoreApp.getContext(),
                                item.getNAppId(), createRoute(), true, false, item.getSUrl(), MD5, spreeTitle, spreeDesc);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        FreeStoreApp.getContext().startActivity(intent);
                        channelGetResult(iChannelGetResult, true);
                        // 打开活动页面
                    } else if (item.getCType().equals(ChannelAppModel.ChannelAppModelItem.TYPE_OPEN_URL)
                            && !TextUtils.isEmpty(item.getSUrl())) {
                        Intent intent = WebViewActivity.newIntent(FreeStoreApp.getContext(),
                                item.getSUrl(), createRoute());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        FreeStoreApp.getContext().startActivity(intent);
                        channelGetResult(iChannelGetResult, true);
                    } else {
                        channelGetResult(iChannelGetResult, false);
                    }
                } else {
                    channelGetResult(iChannelGetResult, false);
                }
            }

            @Override
            public void onNetWorkError() {
                channelGetResult(iChannelGetResult, false);
            }

            @Override
            public void onServerError() {
                channelGetResult(iChannelGetResult, false);
            }
        }, false);
    }

    public static void channelGetResult(IChannelGetResult iChannelGetResult, boolean ret) {
        if (iChannelGetResult != null)
            iChannelGetResult.onResult(ret);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 用于统计路径
     */
    private static int[] createRoute() {
        // Splash
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_CHANNEL,
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
     * 是否在白名单中，根据白名单走不同流程
     *
     * @return
     */
    public static boolean isInWhiteList() {
        String channelID = ChannelUtil.getChannelID();
        boolean ret = false;
        try {
            if (Integer.parseInt(channelID) > 10000)
                ret = true;
        } catch (Exception e) {
        }

        return ret;
    }

    public interface IChannelGetResult {
        void onResult(boolean ret);
    }
}
