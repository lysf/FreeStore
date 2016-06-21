package com.snailgame.cjg.settings;

import android.app.Activity;

import com.snailgame.cjg.R;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import org.json.JSONObject;

public class FeedbackUtil {
    public static final String TAG = "FeedbackUtil";


    /**
     * 发送 应用反馈 给服务端
     * @param context
     * @param content
     * @param contact
     * @param appId
     */
    public static void sendAppFeedback(final Activity context, String content, final String contact, String appId, String TAG) {
        String postBody ="&sContext=" + content
                        + "&sContact=" + contact
                        + "&nFeedbackAppId=" + String.valueOf(appId);

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().FEEDBACK_URL, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");

                    if (code == 0) {
                        context.finish();
                        ToastUtils.showMsg(context, ResUtil.getString(R.string.feed_back_success));
                    } else {
                        ToastUtils.showMsg(context, ResUtil.getString(R.string.operate_fail));
                    }
                } catch (Exception e) {
                    ToastUtils.showMsg(context, ResUtil.getString(R.string.operate_fail));
                    LogUtils.e(e.getMessage());
                }

            }

            @Override
            public void onNetWorkError() {
                ToastUtils.showMsg(context, ResUtil.getString(R.string.operate_fail));
            }

            @Override
            public void onServerError() {
                ToastUtils.showMsg(context, ResUtil.getString(R.string.operate_fail));
            }
        }, postBody);
    }


}
