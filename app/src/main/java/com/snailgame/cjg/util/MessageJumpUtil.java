package com.snailgame.cjg.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.detail.DetailFragment;
import com.snailgame.cjg.event.FriendScreenChangeEvent;
import com.snailgame.cjg.friend.FriendMainActivity;
import com.snailgame.cjg.guide.SplashActivity;
import com.snailgame.cjg.message.PushReceiver;
import com.snailgame.cjg.message.model.MessagePushExInfo;
import com.snailgame.cjg.personal.MyVoucherActivity;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;

/**
 * Created by TAJ_C on 2015/7/1.
 */
public class MessageJumpUtil {
    // 界面跳转
    public static void JumpActivity(Context context, MessagePushExInfo info, int[] route) {
        boolean isActivityFinish = false;
        Intent intent = null;
        if (info != null) {
            switch (info.getType()) {
                case PushReceiver.PUSHTYPE_OPEN_CLIENT:         // 欢迎页
                    intent = SplashActivity.newIntent(context);
                    break;
                case PushReceiver.PUSHTYPE_OPEN_MAIN:         // 首页
                    intent = MainActivity.newIntent(context);
                    break;
                case PushReceiver.PUSHTYPE_OPEN_COLLECTION:         // 合集
                    if (!TextUtils.isEmpty(info.getPageId())
                            && !TextUtils.isEmpty(info.getPageTitle())) {
                        intent = CollectionActivity.newIntent(context, Integer.parseInt(info.getPageId()), route);
                    }
                    break;
                case PushReceiver.PUSHTYPE_OPEN_DETAIL:         // 应用详情
                    if (!TextUtils.isEmpty(info.getPageId())) {
                        int appId = Integer.parseInt(info.getPageId());
                        if (appId == 0) {
                            ToastUtils.showMsg(context, R.string.app_derail_json_parse_error);
                        } else {
                            intent = DetailActivity.newIntent(context, Integer.parseInt(info.getPageId()), route);
                        }
                    }
                    break;
                case PushReceiver.PUSHTYPE_OPEN_URL:            // 网页
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        intent = WebViewActivity.newIntent(context,
                                info.getUrl());
                    }
                    break;
                case PushReceiver.PUSHTYPE_INFO_VOUCHER:         // 代金券
                    if (IdentityHelper.isLogined(context)) {
                        intent = MyVoucherActivity.newIntent(context, Integer.valueOf(info.getPageId()));
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;

                case PushReceiver.PUSHTYPE_INFO_COMMENT:         // 评论回复
                    if (!TextUtils.isEmpty(info.getPageId())) {
                        int appId = Integer.parseInt(info.getPageId());
                        if (appId == 0) {
                            ToastUtils.showMsg(context, R.string.app_derail_json_parse_error);
                        } else {
                            intent = DetailActivity.newIntent(context, Integer.parseInt(info.getPageId()), route, DetailFragment.TAB_COMMENT);
                        }
                    }
                    break;
                case PushReceiver.PUSHTYPE_OPEN_SHARE:
                case PushReceiver.PUSHTYPE_INFO_NOTICE:
                    break;
                case PushReceiver.PUSHTYPE_INFO_DETAIL_SPREE:    // 礼包预约，跳转指定游戏礼包页
                    if (!TextUtils.isEmpty(info.getPageId())) {
                        int appId = Integer.parseInt(info.getPageId());
                        if (appId == 0) {
                            ToastUtils.showMsg(context, R.string.app_derail_json_parse_error);
                        } else {
                            intent = DetailActivity.newIntent(context, Integer.parseInt(info.getPageId()), route, DetailFragment.TAB_SPREE);
                        }
                    }
                    break;
                case PushReceiver.PUSHTYPE_FRIEND_ADD: //朋友点击
                    if (IdentityHelper.isLogined(context)) {
                        if (ComUtil.isTopActivity(FriendMainActivity.class.getName())) {
                            MainThreadBus.getInstance().post(new FriendScreenChangeEvent());
                        } else {
                            intent = FriendMainActivity.newIntent(context);
                        }
                    } else {
                        AccountUtil.userLogin(context);
                    }
                    break;
                default:
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        intent = WebViewActivity.newIntent(context,
                                info.getUrl());
                    }
                    break;
            }
        }

        if (intent != null) {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            if (isActivityFinish && context instanceof Activity)
                ((Activity) context).finish();
        }
    }

}
