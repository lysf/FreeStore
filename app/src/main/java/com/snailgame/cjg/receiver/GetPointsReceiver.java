package com.snailgame.cjg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.receiver.model.PointModel;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxy on 14-3-31.
 */
public class GetPointsReceiver extends BroadcastReceiver {
    static String TAG = GetPointsReceiver.class.getName();
    private Handler handler;


    public static Intent newIntent(Context context, String appId, String gameName, String source, boolean isMenuShare) {
        Intent intent = new Intent(context, GetPointsReceiver.class);
        intent.putExtra(AppConstants.GAME_ID, appId);
        intent.putExtra(AppConstants.GAME_NAME, gameName);
        intent.putExtra(AppConstants.SHARE_SOURCE, source);//来自新浪微博
        intent.putExtra(AppConstants.SHARE_MENU, isMenuShare);
        return intent;
    }

    private final String KEY = "result";

    public void onReceive(final Context context, final Intent intent) {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bundle bundle = msg.getData();
                if (bundle == null) return;
                PointModel result = bundle.getParcelable(KEY);

                boolean menu = intent.getBooleanExtra(AppConstants.SHARE_MENU, false);
                context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_UPDATE_USR_INFO));
                String shareUrl = GlobalVar.getInstance().getShareActivityUrl();
                if (!TextUtils.isEmpty(shareUrl) && shareUrl.contains(AppConstants.WEBVIEW_SHARE_FLUSH))
                    FreeStoreApp.getContext().sendBroadcast(new Intent(AppConstants.WEBVIEW_BROADCAST_ACTION_COMMON));

                if (!menu && result != null) {
                    if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().equals("OK")) {
                        if (!TextUtils.isEmpty(result.getVal())) {
                            ToastUtils.showMsg(context, result.getVal());
                        }
                    }
                }
            }
        };
        if (intent != null && IdentityHelper.isLogined(context)) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("nShareAppId", intent.getStringExtra(AppConstants.GAME_ID));
            parameters.put("cSource", intent.getStringExtra(AppConstants.SHARE_SOURCE));
            parameters.put("url", GlobalVar.getInstance().getShareActivityUrl());

            String url = JsonUrl.getJsonUrl().JSON_URL_PERSONAL_SHARE;

            /**
             * 积分变化 请求
             */
            FSRequestHelper.newPostRequest(url, TAG, PointModel.class, new IFDResponse<PointModel>() {
                @Override
                public void onSuccess(PointModel result) {
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY, result);
                    message.setData(bundle);
                    handler.sendMessage(message);


                }

                @Override
                public void onNetWorkError() {

                }

                @Override
                public void onServerError() {

                }
            }, parameters);
        }
    }
}
