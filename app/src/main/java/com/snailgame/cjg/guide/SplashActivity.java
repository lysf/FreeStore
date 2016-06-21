package com.snailgame.cjg.guide;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.server.BaiduLocationService;
import com.snailgame.cjg.common.server.ChannelAppInstallGetService;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.guide.widget.TimerCountdownView;
import com.snailgame.cjg.statistics.StatisticsReceiver;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.GameSdkDataUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MD5Util;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.SharedPreferencesHelper;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.SystemConfigUtil;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.util.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.ui.ShortcutActivity;

public class SplashActivity extends BaseFSActivity {
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int GO_APP = 1003;
    private static final int OPEN_HOME = 1004;


    // 延迟3秒
    private long beginTime;
    private String appIdOrUrl;
    private boolean mResumeToGuide = false;

    private MsgHandler mHandler = new MsgHandler(this);

    public static Intent newIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }


    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.splash;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullScreen();

        // 检测是否是不同渠道覆盖安装
        ChannelUtil.coverInstallCheck();

        SystemConfigUtil.getInstance().getUpdateConfigData(JsonUrl.getJsonUrl().JSON_URL_SNAIL_SYSTEM_CONFIG);
        EngineEnvironment.initIfaces(FreeStoreApp.getContext());
        LoginSDKUtil.snailBBsLogin();

        //百度定位
        startLocationService();
        //初始化个推 推送
        PushManager.getInstance().initialize(FreeStoreApp.getContext());

        StaticsUtils.init(getWindowManager(), FreeStoreApp.getContext());

        startService(UserInfoGetService.newIntent(this));

        beginTime = System.currentTimeMillis();
        LoginSDKUtil.setContext(FreeStoreApp.getContext());


        MyGameDaoHelper.getUpgradeApk(FreeStoreApp.getContext());

        // Clear download FreeStore old version apk
        new ClearOldVersionThread().start();

        //游戏SDK数据检测
        GameSdkDataUtil.checkGameIds(this);

        StatisticsReceiver.scheduleAlarms(this);

        if (SharedPreferencesUtil.getInstance().isFirstReboot()) {        // 第一次使用
            StaticsUtils.activeChannelId();
            //防重复
            ShortcutActivity.createShortcutSafety(this);
        }

        jumpToHome();


        if (SharedPreferencesUtil.getInstance().isFirstIn()) {
            StaticsUtils.event(this, "2", ChannelUtil.getChannelID(), IdentityHelper.getAccount(FreeStoreApp.getContext()), "36", "", "2");
            SharedPreferencesUtil.getInstance().setFirstIn(false);
        }
        if (SharedPreferencesUtil.getInstance().isNeedCreateDeskGameShortcut()) {
            //是否需要创建桌面游戏快捷图标
            if (!ComUtil.isShortcutExist(this, getString(R.string.mygame))) {
                createDeskGameShortcut();
            }
            SharedPreferencesUtil.getInstance().setisNeedCreateDeskGameShortcut(false);
        }

    }


    /**
     * 开启百度定位
     */
    private void startLocationService() {
        long lastGetLocationTime = SharedPreferencesHelper.getInstance().getValue(AppConstants.LAST_GET_LOCATION_TIME, 0l);
        //根据保存的上一次的时间，如果为0或者和现在时间比小于一天则开启定位（因为通过ip定位每天有10万次的限制）
        if (lastGetLocationTime == 0 || System.currentTimeMillis() - lastGetLocationTime >= 24 * 60 * 60 * 1000) {
            startService(BaiduLocationService.newIntent(this));
        }
    }

    // 显示推广及进入按钮
    private boolean showDynamicImage() {
        int showStatus = PersistentVar.getInstance().getSystemConfig().getSplashSwitch();
        if (showStatus == 0) {    // 不显示广告
            return true;
        }

        RelativeLayout relHolder = (RelativeLayout) findViewById(R.id.relHolder);
        ImageView imageBackground = (ImageView) findViewById(R.id.imageBackground);
        String picUrl = PersistentVar.getInstance().getSystemConfig().getSplashUrl();
        appIdOrUrl = PersistentVar.getInstance().getSystemConfig().getSplashImageAppId();

        if (picUrl.length() > 0) {
            String filePath = FileUtil.SD_IMAGE_PATH;
            final String fileName = MD5Util.md5Encrypt(picUrl);
            Bitmap splashBitmap = BitmapManager.getInstance().getBitmapCache().get(fileName);    // 从缓存读取
            if (splashBitmap == null) {    // 从本地读取
                String fullName = filePath + fileName;
                splashBitmap = BitmapUtil.readBitmapFromDisk(fullName);
                if (splashBitmap != null) {
                    BitmapManager.getInstance().getBitmapCache().put(fileName, splashBitmap);
                }
            }

            if (splashBitmap != null) {            // 显示图片及按
                initTimer();
                relHolder.setVisibility(View.GONE);
                imageBackground.setImageBitmap(splashBitmap);

                imageBackground.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (timerCountdownView != null)
                            timerCountdownView.destroyHandler();
                        mHandler.sendEmptyMessageDelayed(OPEN_HOME, 100);
                        mHandler.sendEmptyMessageDelayed(GO_APP, 200);
                    }
                });

                return false;
            }
        }

        return true;
    }

    private void openHome() {
        startActivity(MainActivity.newIntent(this));
    }

    // 点击推广图片
    private void openAppDetail() {
        if (TextUtils.isEmpty(appIdOrUrl)) {
            // do nothing
        } else if (appIdOrUrl.startsWith("http://")) {
            startActivity(WebViewActivity.newIntent(this,
                    appIdOrUrl));
        } else {
            int appId = 0;
            try {
                appId = Integer.valueOf(appIdOrUrl);
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
            startActivity(DetailActivity.newIntent(SplashActivity.this, appId, createRoute()));
        }

        finish();
    }

    private void jumpToHome() {
        long allStayTime = PersistentVar.getInstance().getSystemConfig().getSplashStayTime();
        long leftStayTime = 0;
        long costTime = System.currentTimeMillis() - beginTime;
        if (costTime < allStayTime) {
            leftStayTime = allStayTime - costTime;
        }
        if (leftStayTime < 200) {
            leftStayTime = 200;
        }
        if (!SharedPreferencesUtil.getInstance().shouldShowGuide()) {
            if (showDynamicImage())
                mHandler.sendEmptyMessageDelayed(GO_HOME, leftStayTime);
        } else {
            if (!channelEvent())
                mHandler.sendEmptyMessageDelayed(GO_GUIDE, leftStayTime);
        }
    }

    /**
     * 渠道安装
     */
    private boolean channelEvent() {
        if (ChannelAppInstallGetService.isInWhiteList()) {
            ChannelAppInstallGetService.getChannelApp(TAG, new ChannelAppInstallGetService.IChannelGetResult() {
                @Override
                public void onResult(boolean ret) {
                    if (ret) {
                        mResumeToGuide = true;
                        getProgressDialog().dismiss();
                    } else {
                        mHandler.sendEmptyMessageDelayed(GO_GUIDE, 200);
                    }
                }
            });

            getProgressDialog().show();
            return true;
        } else {
            return false;
        }

    }

    private Dialog splashProgressDialog;

    private Dialog getProgressDialog() {
        if (splashProgressDialog == null)
            splashProgressDialog = DialogUtils.splashProgressDialog(this);
        return splashProgressDialog;
    }

    private void goGuide() {
        startActivity(GuideActivity.newIntent(this));
        SplashActivity.this.finish();
    }

    private void goHome() {
        startActivity(MainActivity.newIntent(this));
        SplashActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_SPLASH);
        if (mResumeToGuide)
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, 200);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_SPLASH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 禁止点击菜单键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // Splash
        return new int[]{
                AppConstants.STATISTCS_FIRST_SPLASH,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
    }

    /**
     * 删除下载旧版免商店客户端的任务
     */
    private static class ClearOldVersionThread extends Thread {
        @Override
        public void run() {
            //根据包名获取下载任务信息
            TaskInfo task = DownloadHelper.getTaskInfoByPkgName(FreeStoreApp.getContext(), FreeStoreApp.getContext().getPackageName());

            if (task != null && task.getAppVersionCode() <= ComUtil.getSelfVersionCode()) {
                PackageInfoUtil.deleteApkFromDiskByUri(task.getApkLocalUri());
                DownloadHelper.cancelDownload(FreeStoreApp.getContext(), task.getTaskId());
            }
        }
    }


    /**
     * 首次启动创建快捷方式
     */
    private void createDeskGameShortcut() {
        // 安装的Intent
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.mygame));
        shortcut.putExtra("duplicate", false);
        // 启动对象
        ComponentName comp = new ComponentName(this.getPackageName(), this.getPackageName() + ".desktop.MyGameActivity");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
        // 快捷图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_desk_game);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // 发送广播
        sendBroadcast(shortcut);
    }


    static class MsgHandler extends Handler {
        private WeakReference<SplashActivity> mActivity;

        public MsgHandler(SplashActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case GO_HOME:
                        activity.goHome();
                        break;
                    case GO_GUIDE:
                        activity.goGuide();
                        break;
                    case GO_APP:
                        activity.openAppDetail();
                        break;
                    case OPEN_HOME:
                        activity.openHome();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 倒计时
     */
    @Bind(R.id.timer_count_down)
    TimerCountdownView timerCountdownView;
    @Bind(R.id.tv_time_countdown)
    TextView tv_time_countdown;

    private void initTimer() {
        timerCountdownView.setVisibility(View.VISIBLE);
        timerCountdownView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                timerCountdownView.destroyHandler();
                mHandler.sendEmptyMessageDelayed(GO_HOME, 100);
            }
        });
        timerCountdownView.setMaxTime(5);
        timerCountdownView.updateView();
        timerCountdownView.addCountdownTimerListener(new TimerCountdownView.CountdownTimerListener() {
            @Override
            public void onCountDown(int time) {
                tv_time_countdown.setText(String.format(getString(R.string.timer_countdown), time));
            }

            @Override
            public void onTimeArrive(boolean isArrive) {
                if (isArrive) {
                    timerCountdownView.destroyHandler();
                    goHome();
                }
            }
        });
    }


}
