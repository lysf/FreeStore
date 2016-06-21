package com.snailgame.cjg.common.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.util.BaiduLocationUtil;

/**
 * 获取百度定位信息
 * Created by lic on 2016/03/18
 */
public class BaiduLocationService extends IntentService {
    static String TAG = BaiduLocationService.class.getName();

    public BaiduLocationService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, BaiduLocationService.class);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getLocation();
    }

    /**
     * 获取开启定位
     */
    private void getLocation() {
        new BaiduLocationUtil(this).getBaiduLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
