package com.snailgame.cjg.common.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.event.ScratchInfoChangeEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.ScratchInfoModel;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.network.IFDResponse;

/**
 * 每日抽奖信息获取
 * Created by lic on 2015/5/11.
 */
public class ScratchInfoGetService extends IntentService {
    static String TAG = ScratchInfoGetService.class.getName();

    public ScratchInfoGetService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, ScratchInfoGetService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getScratchInfo();
    }

    /**
     * 获取每日抽奖信息
     */
    private void getScratchInfo() {
        if (!LoginSDKUtil.isLogined(FreeStoreApp.getContext())) {
            return;
        }

        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_SCRATCH_INFO, TAG, ScratchInfoModel.class, new IFDResponse<ScratchInfoModel>() {
            @Override
            public void onSuccess(ScratchInfoModel result) {
                MainThreadBus.getInstance().post(new ScratchInfoChangeEvent(result));
                if (result != null && result.getItemModel() != null) {
                    SharedPreferencesUtil.getInstance().setScratchStatus(result.getItemModel().getStatus());
                } else {
                    SharedPreferencesUtil.getInstance().setScratchStatus(0);
                }
            }

            @Override
            public void onNetWorkError() {
                SharedPreferencesUtil.getInstance().setScratchStatus(0);
            }

            @Override
            public void onServerError() {
                SharedPreferencesUtil.getInstance().setScratchStatus(0);
            }
        }, false, true, new ExtendJsonUtil());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
