package com.snailgame.cjg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.util.HostUtil;

/**
 * 监听网络变化
 * Created by xixh on 2014/11/26.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 重置host
        HostUtil.replaceHost();
    }
}
