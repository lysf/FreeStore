package com.snailgame.cjg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.settings.SnailFreeStoreServiceUtil;


/**
 * Created with IntelliJ IDEA.
 * User: shenzaih
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SnailFreeStoreServiceUtil.initGameUpdate(context);
            SnailFreeStoreServiceUtil.initMyselfUpdate(context);
        }
    }
}
