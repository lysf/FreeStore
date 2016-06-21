package com.snailgame.cjg.settings;

import android.os.Handler;

import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 检测更新 util
 */
public class UpdateUtil {


    /**
     * 检测更新
     * @param l
     */
    public void checkUpdate(final CallBack l, String TAG) {
        final Handler mHandler = new Handler();

        FSRequestHelper.newGetRequest(buildUpdateUrl(), TAG, UpdateModel.class, new IFDResponse<UpdateModel>() {
            @Override
            public void onSuccess(final UpdateModel result) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onCompleted(result);
                    }
                });
            }

            @Override
            public void onNetWorkError() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onCompleted(null);
                    }
                });
            }

            @Override
            public void onServerError() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onCompleted(null);
                    }
                });
            }
        }, false, true, new ExtendJsonUtil());
    }


    private String buildUpdateUrl() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppConstants.PARAMS_VERSION_CODE, ComUtil.getSelfVersionCode());
        params.put(AppConstants.PARAMS_CHANNEL_ID, ChannelUtil.getChannelID());
        params.put(AppConstants.PARAMS_PLATFORM_ID, AppConstants.PLATFORM_ID);
        return UrlUtils.buildUrl(JsonUrl.getJsonUrl().JSON_URL_UPDATE_URL, params);
    }

    public interface CallBack {
        void onCompleted(UpdateModel model);
    }

}
