package com.snailgame.cjg.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.daoHelper.PushModelDaoHelper;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.message.model.MessagePushExInfo;
import com.snailgame.cjg.util.MessageJumpUtil;
import com.snailgame.cjg.util.StaticsUtils;

/**
 * 推送消息通知栏点击广播
 * Created by xixh on 2015/4/13.
 */
public class NotifyReceiver extends BroadcastReceiver {
    public static final String KEY_MODEL = "push_model";
    public static final String IS_USER_CANCLE = "is_user_cancle";

    public static Intent contentIntent(Context context, PushModel model) {
        if (model == null)
            return null;
        Intent intent = new Intent(context, NotifyReceiver.class);
        intent.putExtra(KEY_MODEL, model);
        intent.putExtra(IS_USER_CANCLE, false);
        return intent;
    }

    public static Intent deleteIntent(Context context, PushModel model) {
        if (model == null)
            return null;
        Intent intent = new Intent(context, NotifyReceiver.class);
        intent.putExtra(KEY_MODEL, model);
        intent.putExtra(IS_USER_CANCLE, true);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        PushModel model = intent.getParcelableExtra(KEY_MODEL);
        if (model == null)
            return;
        //用户手动取消通知
        if (intent.getBooleanExtra(IS_USER_CANCLE, false)) {
            updateIsExit(model);
        } else {
            // 自定义事件 90002：用户点击通知栏消息
            StaticsUtils.sendGETUIEvent(context, AppConstants.GETUI_ACTION_NOTIFY_CLICK, model.getMsgId(), model.getTaskId());
            updateHasRead(model);
            MessagePushExInfo messagePushExInfo = JSON.parseObject(model.getExpandMsg(), MessagePushExInfo.class);
            MessageJumpUtil.JumpActivity(context, messagePushExInfo, createRoute());
        }
    }

    // 更新消息为已读
    private void updateHasRead(final PushModel model) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                PushModelDaoHelper.updateHasRead(FreeStoreApp.getContext(), model);
            }
        };
        thread.start();
    }

    // 更新消息通知不存在
    private void updateIsExit(final PushModel model) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                PushModelDaoHelper.updateIsExit(FreeStoreApp.getContext(), model);
            }
        };
        thread.start();
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 推送
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_PUSH,
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
}
