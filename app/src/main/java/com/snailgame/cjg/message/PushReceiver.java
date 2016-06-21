package com.snailgame.cjg.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.daoHelper.PushModelDaoHelper;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.event.CIDGetEvent;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.model.Friend;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.message.model.MessagePushExInfo;
import com.snailgame.cjg.personal.TaskColorEggsActivity;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.LogUtils;


public class PushReceiver extends BroadcastReceiver {
    private static final String KEY_USERID = "userId";
    private String notificationTitle = "";
    private String notificationMessage = "";
    private String notificationExpand = "";
    private String msgId = "";
    private String taskId = "";
    private String userId = "";

    // 用户是否打开接收推送
    private boolean isOpenPush = true;

    /**
     * 推送类型
     */
    public static final int PUSHTYPE_OPEN_CLIENT = 1;           // 启动客户端
    public static final int PUSHTYPE_OPEN_SHARE = 2;            // 跳转流量分享界面
    public static final int PUSHTYPE_OPEN_MAIN = 4;             // 登录首页
    public static final int PUSHTYPE_OPEN_COLLECTION = 5;       // 跳转合集
    public static final int PUSHTYPE_OPEN_DETAIL = 6;           // 跳转指定游戏页面
    public static final int PUSHTYPE_OPEN_URL = 7;              // 跳转指定URL
    public static final int PUSHTYPE_INFO_NOTICE = 8;           // 系统公告
    public static final int PUSHTYPE_INFO_BALANCE = 9;          // 推送余额
    public static final int PUSHTYPE_INFO_USER = 10;            // 用户信息变更
    public static final int PUSHTYPE_INFO_TASK = 11;            // 任务推送
    public static final int PUSHTYPE_INFO_VOUCHER = 12;         // 代金券推送
    public static final int PUSHTYPE_INFO_COMMENT = 13;         // 评论回复提醒
    public static final int PUSHTYPE_INFO_MESSAGE = 14;         // 跳转指定资讯
    public static final int PUSHTYPE_INFO_DETAIL_SPREE = 15;    // 应用详情-礼包标签
    public static final int PUSHTYPE_INFO_COLOR_EGGS = 16;      // 彩蛋 弹出彩蛋窗口
    public static final int PUSHTYPE_FRIEND_ADD = 17;           //朋友 添加推送

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        isOpenPush = SharedPreferencesUtil.getInstance().isOpenPush();
        int action = bundle.getInt(PushConsts.CMD_ACTION);
        switch (action) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                taskId = bundle.getString("taskid");
                msgId = bundle.getString("messageid");

                // 自定义事件 90001：收到个推消息
                StaticsUtils.sendGETUIEvent(context, AppConstants.GETUI_ACTION_PUSH_RECEIVER, msgId, taskId);

                if (payload != null) {
                    String data = new String(payload);
                    parseDataAndNotify(context, data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                AppConstants.CLIENT_ID = bundle.getString("clientid");
                MainThreadBus.getInstance().post(new CIDGetEvent());
                LogUtils.i("CIDGetEvent post");
                break;
            case PushConsts.THIRDPART_FEEDBACK:
            /*String appid = bundle.getString("appid");
            String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
                break;
            default:
                break;
        }
    }

    private void parseDataAndNotify(Context context, String data) {
        LogUtils.d("PushReceiver ---> " + data);
        parseJson(data);

        //登录情况下  推送userId 不为空 并且 与当前登录账户 userId不相符 则不处理
        if (IdentityHelper.isLogined(context) && (!TextUtils.isEmpty(userId) && !IdentityHelper.getUid(context).equals(userId))) {
            return;
        }
        //未登录情况下  推送userId 不为空  则不处理
        if (!IdentityHelper.isLogined(context) && !TextUtils.isEmpty(userId)) {//未登录 并且userId 不为空
            return;
        }

        MessagePushExInfo info = JSON.parseObject(notificationExpand, MessagePushExInfo.class);
        if (info == null)
            return;

        if (info.getType() == PUSHTYPE_INFO_USER) { // 用户信息变更
            if (isValidUser(context))
                context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_UPDATE_USR_INFO));
        } else if (info.getType() == PUSHTYPE_INFO_TASK) {  // 任务完成
            if (isValidUser(context))
                ToastUtils.showMsg(context, notificationMessage);
        } else if (info.getType() == PUSHTYPE_INFO_VOUCHER) { //代金卷
            if (TextUtils.isEmpty(notificationTitle) && TextUtils.isEmpty(notificationMessage)) {
                //通知刷新
                context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_GET_VOUCHER_NUM));
            } else {
                context.startService(UserInfoGetService.newIntent(context, AppConstants.ACTION_GET_VOUCHER_NUM));
                //弹出代金卷通知
                long currentTime = System.currentTimeMillis();
                String bigImageUrl = info.getBigImgUrl();
                String imageUrl = info.getImgUrl();
                if (bigImageUrl.contains("|")) {
                    String[] strings = bigImageUrl.split("\\|");
                    bigImageUrl = strings[0];
                }
                if (imageUrl.contains("|")) {
                    String[] strings = imageUrl.split("\\|");
                    imageUrl = strings[0];
                }
                PushModel model = new PushModel(0, notificationTitle, notificationMessage, notificationExpand, "",
                        imageUrl, PushModel.NOT_READ, msgId, taskId, bigImageUrl, PushModel.IS_EXIT, userId);
                Notifier notifier = new Notifier(context);
                notifier.notify((int) currentTime, model, true);
            }
        } else if (info.getType() == PUSHTYPE_INFO_COMMENT || info.getType() == PUSHTYPE_INFO_DETAIL_SPREE) {
            notify(context, info);
        } else if (info.getType() < 10 && info.getType() > 0) {
            if (isOpenPush)
                notify(context, info);
        } else if (info.getType() == PUSHTYPE_INFO_COLOR_EGGS) {
            //  是否在前端判断
            if (ComUtil.isApplicationShowing(AppConstants.APP_PACKAGE_NAME)) {
                context.startActivity(TaskColorEggsActivity.newIntent(context, notificationMessage));
            }
        } else if (info.getType() == PUSHTYPE_FRIEND_ADD) {
            info.setImgUrl(info.getCPhoto());
            notify(context, info);
            sendFriendApplyEvent(info);
        }
    }

    // 好友
    private void sendFriendApplyEvent(MessagePushExInfo info) {
        Friend friend = new Friend();
        friend.setGameName(info.getSGameName());
        friend.setNickName(info.getSNickName());
        friend.setUserId(info.getNUserId());
        friend.setPhoto(info.getCPhoto());

        BaseDataModel model = new BaseDataModel();
        model.setCode(0);
        FriendHandleEvent event = new FriendHandleEvent(FriendHandleUtil.FRIEND_HANDLE_APPLY, friend, model);
        MainThreadBus.getInstance().post(event);
    }

    private void notify(Context context, MessagePushExInfo info) {
        long currentTime = System.currentTimeMillis();
        PushModel pushModel = new PushModel(0, notificationTitle, notificationMessage, notificationExpand, String.valueOf(currentTime),
                "", PushModel.NOT_READ, msgId, taskId, "", PushModel.IS_EXIT, userId);
        if(info.getType() != PUSHTYPE_FRIEND_ADD){
            PushModelDaoHelper.insert(context, pushModel);
        }
        String bigImageUrl = info.getBigImgUrl();
        String imageUrl = info.getImgUrl();
        if (bigImageUrl.contains("|")) {
            String[] strings = bigImageUrl.split("\\|");
            bigImageUrl = strings[0];
        }
        if (imageUrl.contains("|")) {
            String[] strings = imageUrl.split("\\|");
            imageUrl = strings[0];
        }
        PushModel model = new PushModel(0, notificationTitle, notificationMessage, notificationExpand, "",
                imageUrl, PushModel.NOT_READ, msgId, taskId, bigImageUrl, PushModel.IS_EXIT, userId);
        model.setType(info.getType());
        Notifier notifier = new Notifier(context);
        //如果是应用详情
        if (info.getType() == PUSHTYPE_OPEN_DETAIL || info.getType() == PUSHTYPE_INFO_COMMENT ||
                info.getType() == PUSHTYPE_INFO_DETAIL_SPREE || info.getType() == PUSHTYPE_FRIEND_ADD) {
            notifier.notify((int) currentTime, model, true);
        } else {
            notifier.notify((int) currentTime, model, false);
        }
    }

    /**
     * @param context
     * @return
     */
    private boolean isValidUser(Context context) {
        return IdentityHelper.isLogined(context) && IdentityHelper.getUid(context).equals(userId);
    }


    private void parseJson(String data) {
        try {
            JSONObject object = JSON.parseObject(data);
            if (object.containsKey(PushModel.COL_PUSH_TITLE))
                notificationTitle = object.getString(PushModel.COL_PUSH_TITLE);
            if (object.containsKey(PushModel.COL_PUSH_EXPAND_MESSAGE))
                notificationExpand = object.getString(PushModel.COL_PUSH_EXPAND_MESSAGE);
            if (object.containsKey(PushModel.COL_PUSH_CONTENT))
                notificationMessage = object.getString(PushModel.COL_PUSH_CONTENT);
            if (object.containsKey(KEY_USERID))
                userId = object.getString(KEY_USERID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
