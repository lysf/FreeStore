package com.snailgame.cjg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;

import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.guide.SplashActivity;
import com.snailgame.cjg.util.LollipopUtil;
import com.snailgame.cjg.util.SmartBarUtils;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.fastdev.FastDevActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yftx on 6/16/14.
 * <p/>
 * 统计基础功能
 */
public abstract class BaseFSActivity extends FastDevActivity {
    public static final String KEY_IS_OUTSIDE_IN = "is_outside_in"; // 是否从外部打开
    private long resumeTime;
    protected int[] mRoute;
    public boolean isOutSideIn = false;

    protected void setWindowFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
        }
        StaticsUtils.onCreate();
        LollipopUtil.setupRecentDesc(this);

        if (this instanceof MainActivity)
            SmartBarUtils.hide(this, this.getWindow());
        else
            SmartBarUtils.hide(this.getWindow().getDecorView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 如果是从外部打开，在关闭时返回首页
        if (isOutSideIn && !(this instanceof MainActivity) && !(this instanceof SplashActivity))
            startActivity(MainActivity.newIntent(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeTime = System.currentTimeMillis();
        MobclickAgent.onResume(this);   // umeng统计
        StaticsUtils.onResume();        // 数据中心统计
        StaticsUtils.serverOnResume(FreeStoreApp.getContext());   // 免商店后台统计
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        StaticsUtils.onPause((System.currentTimeMillis() - resumeTime) / 1000);
        StaticsUtils.serverOnPause(FreeStoreApp.getContext());
    }
}