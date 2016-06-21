package com.snailgame.cjg.friend.utils;

import android.content.Context;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.model.Friend;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TAJ_C on 2016/5/11.
 */
public class FriendHandleUtil {

    //标识：  0：接受, 1：添加 , 2：拒绝 3:删除
    public static final int FRIEND_HANDLE_RECEIVE = 0;
    public static final int FRIEND_HANDLE_ADD = 1;
    public static final int FRIEND_HANDLE_REJECT = 2;
    public static final int FRIEND_HANDLE_DEL = 3;
    public static final int FRIEND_HANDLE_APPLY = 4;// 推送好友申请用
    public static void handleFriend(final Context context, String tag, final Friend friend, final int handle) {
        Map<String, String> parmas = new HashMap<>();
        if (friend != null) {
            parmas.put("nFriendId", String.valueOf(friend.getUserId()));
        }
        parmas.put("handleFlag", String.valueOf(handle));
        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_FRIEND_FRIEND_HANDLE, tag, BaseDataModel.class, new IFDResponse<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel result) {
                handleSuccessResult(result, handle, context, friend);
            }

            @Override
            public void onNetWorkError() {
                handleFailedResult(handle, context, friend);
            }

            @Override
            public void onServerError() {
                handleFailedResult(handle, context, friend);
            }
        }, parmas);
    }

    private static void handleSuccessResult(BaseDataModel result, int handle, Context context, Friend friend) {
        switch (handle) {
            case FRIEND_HANDLE_RECEIVE:
                ToastUtils.showMsg(context, context.getString(R.string.friend_receive_sucess));
                break;
            case FRIEND_HANDLE_ADD:
                ToastUtils.showMsg(context, context.getString(R.string.friend_add_success));
                break;
            case FRIEND_HANDLE_REJECT:
                ToastUtils.showMsg(context, context.getString(R.string.friend_reject_success));
                break;
            case FRIEND_HANDLE_DEL:
                ToastUtils.showMsg(context, context.getString(R.string.friend_del_success));
                break;
            default:
                break;
        }

        MainThreadBus.getInstance().post(new FriendHandleEvent(handle, friend, result));
    }

    private static void handleFailedResult(int handle, Context context, Friend friend) {
        switch (handle) {
            case FRIEND_HANDLE_RECEIVE:
                ToastUtils.showMsg(context, context.getString(R.string.friend_receive_failed));
                break;
            case FRIEND_HANDLE_ADD:
                ToastUtils.showMsg(context, context.getString(R.string.friend_add_failed));
                break;
            case FRIEND_HANDLE_REJECT:
                ToastUtils.showMsg(context, context.getString(R.string.friend_reject_failed));
                break;
            case FRIEND_HANDLE_DEL:
                ToastUtils.showMsg(context, context.getString(R.string.friend_del_failed));
                break;
            default:
                break;
        }

        MainThreadBus.getInstance().post(new FriendHandleEvent(handle, friend, null));
    }

}
