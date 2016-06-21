package com.snailgame.cjg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.snailgame.cjg.common.InsteadChargeHelper;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BindCidModel;
import com.snailgame.cjg.common.model.NecessaryAppInfo;
import com.snailgame.cjg.common.model.NecessaryAppInfoModel;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.common.server.ScratchInfoGetService;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.NecessaryAppDialog;
import com.snailgame.cjg.communication.CommunicationFragment;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.cjg.detail.model.InsteadCharge;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.DownloadService;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.CIDGetEvent;
import com.snailgame.cjg.event.DownloadManageChangeEvent;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.NecessaryDialogDismissEvent;
import com.snailgame.cjg.event.RefreshMsgNumEvent;
import com.snailgame.cjg.event.ScratchInfoChangeEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.event.UpdateDialogReshowEvent;
import com.snailgame.cjg.event.UpdateMySelfEvent;
import com.snailgame.cjg.event.UpdateNotificationEvent;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.home.HomeFragment;
import com.snailgame.cjg.manage.ManageActivity;
import com.snailgame.cjg.manage.model.TreasureBoxInfoModel;
import com.snailgame.cjg.message.NoticeActivity;
import com.snailgame.cjg.message.model.MsgNum;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.UserCenterFragment;
import com.snailgame.cjg.personal.model.ScratchInfoModel;
import com.snailgame.cjg.search.AppSearchActivity;
import com.snailgame.cjg.search.SearchFragmentAdapter;
import com.snailgame.cjg.seekgame.SeekGameFragment;
import com.snailgame.cjg.settings.SnailFreeStoreServiceUtil;
import com.snailgame.cjg.settings.UpdateCallBackImpl;
import com.snailgame.cjg.settings.UpdateUtil;
import com.snailgame.cjg.store.StoreFragment;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UIUtil;
import com.snailgame.cjg.util.interfaces.UpdateCompleteDialogListener;
import com.snailgame.cjg.util.skin.SkinManager;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.snailgame.mobilesdk.SnailCommplatform;
import com.squareup.otto.Subscribe;
import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseFSActivity implements View.OnClickListener {
    static String TAG = MainActivity.class.getName();

    @Bind(R.id.tabs_rg)
    RadioGroup radioGroup;

    @Bind(R.id.tab_content)
    ViewPager mContentPager;

    @Bind(R.id.banner_mask)
    View mBannerMask;

    @Bind(R.id.treasurebox_msg)
    TextView treasurebox_msg;

    @Bind(R.id.msg_layout)
    View msgLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbarView;

    @Bind(R.id.notice_area)
    FrameLayout mMsgButton;

    @Bind(R.id.notice_red_point)
    View mRedPointView;

    @Bind(R.id.iv_manage)
    ImageView imageMessing;

    @Bind(R.id.toolbar_searchview)
    View mToolbarSearchView;

    @Bind(R.id.scratch_img_red)
    ImageView scratchImg;
    //我的标签栏是否已经被被点击来负责取消小红点
    boolean isMineTabClickedToCancelRedDot;

    ImageView mToolbarScanner;

    TextView mToolbarUserCenterText;
    TextView mToolbarRightActionText;
    TextView mToolbarHotSearchTipsView;//toolbar搜索框热门搜索

    private Drawable mToolbarDrawable;
    private Drawable mSearchDrawable;
    private Window window;
    private Dialog mUpdateDialog;

    private AsyncTask queryTask;
    private int alpha;
    private int userCenterAlpha;
    boolean homeFragmentDestroyed;
    private boolean homeDataLoaded = false;


    private int currentTab = TAB_HOME;
    private ArrayList<RadioButton> tabButtons;
    private UpdateCallBackImpl mCallBack;        // 检测免商店更新的回调


    private static Boolean isQuit = false;
    private static Boolean isTabValid = true;

    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_TWENTY = 51;
    private static final int ALPHA_FULL = 255;
    private static final int ALPHA_FIFTY = 126;
    Timer timer = new Timer();


    private boolean isFirstIn = false;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static Intent newIntent(Context context, int flags) {
        Intent intent = newIntent(context);
        intent.setFlags(flags);
        return intent;
    }

    @Subscribe
    public void onUpdateDailogReshowed(UpdateDialogReshowEvent event) {
        if (mCallBack != null)
            mCallBack.reShow();
    }

    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        int updatecount = GameManageUtil.getUpdateInfos(MainActivity.this, event.getAppInfoList(), false).size();
        setUpdateCount(updatecount);
    }

    //点击更新应用后发送的广播
    @Subscribe
    public void updateChange(UpdateChangeEvent updateChangeEvent) {
        getUpgradeInfo();
    }

    //更新过程中删除更新任务后发送的广播
    @Subscribe
    public void downloadManageChange(DownloadManageChangeEvent downloadManageEvent) {
        getUpgradeInfo();
    }

    /**
     * 查询推送数据库的回调
     */
    @Subscribe
    public void onMsgNumUpdated(RefreshMsgNumEvent event) {
        final int count = event.getCount();
        setNoticeMsgNum(count);
    }

    /**
     * 用户退出
     */
    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        if (currentTab == TAB_MINE) {
            mToolbarRightActionText.setVisibility(View.GONE);
            scratchImg.setVisibility(View.GONE);
        }
        isMineTabClickedToCancelRedDot = false;

        tabRefresh();
    }

    /**
     * 用户登录
     */
    @Subscribe
    public void onUserLogin(UserLoginEvent event) {
        if (currentTab == TAB_MINE) {
            mToolbarRightActionText.setVisibility(View.VISIBLE);
        }

        tabRefresh();
    }


    /**
     * 通知数据库更新发出的广播来刷新通知中心的图标上的数量
     */
    @Subscribe
    public void onPushDbChanged(UpdateNotificationEvent event) {
        refreshMsgNum();
    }

    /**
     * 积分变更
     */
    @Subscribe
    public void onScratchChanged(ScratchInfoChangeEvent event) {
        if (GlobalVar.getInstance().getUsrInfo() == null || isMineTabClickedToCancelRedDot)
            return;
        ScratchInfoModel scratchInfoModel = event.getScratchInfoModel();
        if (scratchImg == null) {
            return;
        }
        if (scratchInfoModel == null) {
            scratchImg.setVisibility(View.GONE);
            return;
        }
        ScratchInfoModel.ScratchInfo scratchInfo = scratchInfoModel.getItemModel();
        if (scratchInfoModel.getCode() == 0) {
            if (scratchInfo != null) {
                if (scratchInfo.getStatus() == ScratchInfoModel.ScratchInfo.STATUS_FREE) {
                    scratchImg.setVisibility(View.VISIBLE);
                } else {
                    scratchImg.setVisibility(View.GONE);
                }
            } else {
                scratchImg.setVisibility(View.GONE);
            }
        } else {
            scratchImg.setVisibility(View.GONE);
        }
    }


    /**
     * 下载完成后，提示更新dialog
     */
    @Subscribe
    public void showUpdateDiaolog(UpdateMySelfEvent event) {
        final UpdateModel.ModelItem item = event.getUpdateItem();
        String localUrl = event.getLocalUrl();
        if (item == null) return;

        mUpdateDialog = DialogUtils.showUpdateSuccessDialog(this, localUrl, item, new UpdateCompleteDialogListener() {
            @Override
            public void onSureBtnClick(String downloadUrl) {
                installApk(downloadUrl);
            }
        });
        mUpdateDialog.show();
    }

    boolean isShowSignDeal;

    @Subscribe
    public void onUserInfoChange(UserInfoChangeEvent event) {
        if (LoginSDKUtil.isLogined(this)) {
            if (AccountUtil.isUsrInfoGet()) {
                if (!SharedPreferencesUtil.getInstance().isBindCid()) {
                    bindCid();
                }
                if (!isShowSignDeal && GlobalVar.getInstance().getUsrInfo().iscContractMachine() && !GlobalVar.getInstance().getUsrInfo().iscSignDeal()) {//合约机用户未签署协议
                    isShowSignDeal = true;
                    Intent intent = WebViewActivity.newIntent(FreeStoreApp.getContext(), JsonUrl.getJsonUrl().JSON_URL_HEYUE_TREATY, false, 0);
                    startActivity(intent);
                }
            } else {
                AccountUtil.usrInfoNoSuccess(this, FreeStoreApp.statusOfUsr);
            }
        }

        tabRefresh();
    }

    @Subscribe
    public void onCIDGet(CIDGetEvent event) {
        LogUtils.i("CIDGetEvent receive");
        if (LoginSDKUtil.isLogined(this)) {
            bindCid();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        translateStatusbar();
        super.onCreate(savedInstanceState);

        addTabFragments();
        findRadioGroupViews();
        createActionBar();


        //installPreApp();
        SnailFreeStoreServiceUtil.initSnailFreeStoreService(this);

        refreshMsgNum();
        getUpgradeInfo();

        registerReceiver();
        CheckUpdateSelf();

        if (SharedPreferencesUtil.getInstance().isFirstReboot()) {
            isFirstIn = true;
            getNecessaryApp();     // 获取装机必备
        }
        startDownloadService();

        // 清理下载任务列表
        DownloadHelper.clearDownloadTask(FreeStoreApp.getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 清理下载任务列表
            SkinManager.getInstance().clearSkins(this);

            // 皮肤初始化
            SkinManager.getInstance().initSkinPackage(this);
        }
        getInsteadChargeLists();

    }

    /**
     * 通信用户，通信与游戏位置互换
     */
    private void tabRefresh() {
        Drawable drawable1 = ResUtil.getDrawable(R.drawable.tab_communication);
        Drawable drawable2 = ResUtil.getDrawable(R.drawable.tab_play_game);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        if (AccountUtil.isCommunicationUser()) {
            tabButtons.get(TAB_SEEK_GAME).setCompoundDrawables(null, drawable1, null, null);
            tabButtons.get(TAB_SEEK_GAME).setText(R.string.tab_title3);

            tabButtons.get(TAB_COMMUNICATION).setCompoundDrawables(null, drawable2, null, null);
            tabButtons.get(TAB_COMMUNICATION).setText(R.string.tab_title2);
        } else {
            tabButtons.get(TAB_COMMUNICATION).setText(R.string.tab_title3);
            tabButtons.get(TAB_COMMUNICATION).setCompoundDrawables(null, drawable1, null, null);

            tabButtons.get(TAB_SEEK_GAME).setText(R.string.tab_title2);
            tabButtons.get(TAB_SEEK_GAME).setCompoundDrawables(null, drawable2, null, null);
        }
    }

    /**
     * 通信用户，通信与游戏位置互换
     */
    private int indexRefresh(int index) {
        if (AccountUtil.isCommunicationUser()) {
            if (index == TAB_SEEK_GAME)
                index = TAB_COMMUNICATION;
            else if (index == TAB_COMMUNICATION)
                index = TAB_SEEK_GAME;
        }

        return index;
    }

    private void getInsteadChargeLists() {
        HashMap<Integer, InsteadCharge> insteadChargeHashMap = GlobalVar.getInstance().getInsteadChargeArrayMap();
        if (insteadChargeHashMap != null) {
            InsteadChargeHelper.getInsteadCharge();
        }
    }

    private void translateStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
        }
    }

    public void setStatusBarColor(int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alpha <= 30)
                window.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
            else {
                window.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
            }
        }

    }

    /**
     * 启动下载服务
     * 当判断Wifi开启时，启动服务
     */
    private void startDownloadService() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) return;
        //判断当前网络是否可用，网络可用标准，1.链接 2.可用 3.wifi状态
        if (networkInfo.isConnected()
                && networkInfo.isAvailable()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // 当链接为wifi状态时运行开启DownloadService
            DownloadService.start(this);
        }
    }

    private void registerReceiver() {
        MainThreadBus.getInstance().register(this);
    }


    /**
     * 获取通知中心图标上面的消息数量并显示在图标上（刷新依靠数据库更新操作时的otto广播）
     */
    private void refreshMsgNum() {
        MsgNum.getInstance().getNums();
    }


    public void setToolbarStatusBarAlpha(int alpha) {
        if (currentTab == TAB_HOME) {

            mToolbarDrawable.setAlpha(alpha);
            int searchAlpha = alpha + ALPHA_TWENTY;
            mSearchDrawable.setAlpha(searchAlpha > ALPHA_FULL ? ALPHA_FULL : searchAlpha);
            mToolbarHotSearchTipsView.setTextColor(ResUtil.getColor(alpha > ALPHA_FIFTY ? R.color.general_text_color : R.color.translucent_70_white));

            setPointColorBelongAlpha(alpha);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (alpha <= 30)
                    window.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
                else {
                    window.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
                }
            }
            this.alpha = alpha;
        }
    }

    public void setActionBarStatusBarAlpha(int alpha) {
        if (currentTab == TAB_MINE) {
//            mToolbarView.setAlpha(alpha/255f);
            mToolbarUserCenterText.setAlpha(alpha / 255f);
            mToolbarDrawable.setAlpha(alpha);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (alpha <= 10)
                    window.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
                else {
                    window.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
                }
            }
            userCenterAlpha = alpha;
        }
    }

    private void setPointColorBelongAlpha(int alpha) {
        if (alpha > 100) {
            mRedPointView.setBackgroundResource(R.drawable.white_point);
        } else {
            mRedPointView.setBackgroundResource(R.drawable.red_point);
        }
    }

    public void setToolbarAlpha(int alpha) {
        mToolbarDrawable.setAlpha(alpha);
        int searchAlpha = alpha + ALPHA_TWENTY;
        mSearchDrawable.setAlpha(searchAlpha > ALPHA_FULL ? ALPHA_FULL : searchAlpha);
        mToolbarHotSearchTipsView.setTextColor(ResUtil.getColor(alpha > ALPHA_FIFTY ? R.color.general_text_color : R.color.translucent_70_white));

        if (alpha > 100)
            mRedPointView.setBackgroundResource(R.drawable.white_point);
        else {
            mRedPointView.setBackgroundResource(R.drawable.red_point);
        }
    }

    public View getToolBarView() {
        return mToolbarView;
    }

    public View getBannerMask() {
        return mBannerMask;
    }


    private void createActionBar() {
        mToolbarHotSearchTipsView = (TextView) mToolbarView.findViewById(R.id.hot_search_tips);
        mToolbarScanner = (ImageView) mToolbarView.findViewById(R.id.messing_scanner);
        mToolbarUserCenterText = (TextView) mToolbarView.findViewById(R.id.user_text_title);
        mToolbarRightActionText = (TextView) mToolbarView.findViewById(R.id.tv_right_action);

        mMsgButton.setOnClickListener(this);
        imageMessing.setOnClickListener(this);
        msgLayout.setOnClickListener(this);

        mToolbarSearchView.setOnClickListener(this);

        mToolbarView.findViewById(R.id.messing_scanner).setOnClickListener(this);
        mToolbarRightActionText.setOnClickListener(this);

        //设置大家都在搜 字段
        String hot_search = getHotSearch();
        SharedPreferencesUtil.getInstance().setHotSearch(hot_search);
        mToolbarHotSearchTipsView.setText(getString(R.string.hot_search) + hot_search);
        mToolbarHotSearchTipsView.setTextColor(ResUtil.getColor(R.color.translucent_70_white));
        mSearchDrawable = mToolbarSearchView.getBackground();
        mSearchDrawable.setAlpha(ALPHA_TWENTY);
        mToolbarDrawable = mToolbarView.getBackground();
        mToolbarDrawable.setAlpha(ALPHA_TRANSPARENT);
        mToolbarView.setVisibility(View.VISIBLE);

        setToolbarMarginTop();
        getTreasureBox();        // 获取百宝箱信息
    }


    private String getHotSearch() {
        String hot_keys = PersistentVar.getInstance().getSystemConfig().getHotSearch();
        if (!TextUtils.isEmpty(hot_keys)) {
            if (hot_keys.contains(",")) {
                String keys[] = hot_keys.split(",");
                int length = keys.length;
                return keys[new Random().nextInt(length - 1)];
            } else {
                return hot_keys;
            }
        }
        return "";
    }


    private void setToolbarMarginTop() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolbarView.getLayoutParams();
        if (params != null)
            params.topMargin = ComUtil.getStatesBarHeight();

        params = (FrameLayout.LayoutParams) mBannerMask.getLayoutParams();
        if (params != null)
            params.topMargin = ComUtil.getStatesBarHeight();
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
        return R.layout.content_frame;
    }

    private void addTabFragments() {
        PagerAdapter adapter = new MainTabPager(getSupportFragmentManager());
        mContentPager.setAdapter(adapter);
    }


    public final static int TAB_HOME = 0, TAB_SEEK_GAME = 1, TAB_COMMUNICATION = 2, TAB_COOL_PLAY = 3, TAB_MINE = 4;
    private final static int TAB_COUNT = 5;


    class MainTabPager extends FixedFragmentStatePagerAdapter {

        public MainTabPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            switch (position) {
                case TAB_HOME:
                    f = HomeFragment.getInstance(isFirstIn);
                    break;
                case TAB_SEEK_GAME:
                    f = new SeekGameFragment();
                    break;
                case TAB_COMMUNICATION:
                    f = new CommunicationFragment();
                    break;
                case TAB_COOL_PLAY:
                    f = new StoreFragment();
                    break;
                case TAB_MINE:
                    f = new UserCenterFragment();
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }


    private void findRadioGroupViews() {
        RadioButton homeBtn = ButterKnife.findById(radioGroup, R.id.tab_home);
        RadioButton playGameBtn = ButterKnife.findById(radioGroup, R.id.tab_play_game);

        RadioButton communicationBtn = ButterKnife.findById(radioGroup, R.id.tab_communication);
        RadioButton coolPlayBtn = ButterKnife.findById(radioGroup, R.id.tab_cool_play);
        RadioButton mineBtn = ButterKnife.findById(radioGroup, R.id.mine);

        tabButtons = new ArrayList<>(TAB_COUNT);
        tabButtons.add(homeBtn);
        tabButtons.add(playGameBtn);
        tabButtons.add(communicationBtn);
        tabButtons.add(coolPlayBtn);
        tabButtons.add(mineBtn);

        for (int i = 0; i < TAB_COUNT; i++) {
            tabButtons.get(i).setOnClickListener(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (SharedPreferencesUtil.getInstance().isFirstReboot()) {
            ToastUtils.showMsg(this, getString(R.string.first_boot_note));
            SharedPreferencesUtil.getInstance().setFirstReboot(false);
        }

        if (!ComUtil.isServiceRunning(this, ScratchInfoGetService.class.getName())) {
            startService(ScratchInfoGetService.newIntent(this));
        }

    }

    @Override
    public void onBackPressed() {
        exitAppByDoubleClickOnBackKey();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 退出应用提示信息
     */
    public void exitAppByDoubleClickOnBackKey() {
        if (PersistentVar.getInstance().getSystemConfig().getShowExistDialog() == 1) {
            Dialog dialog = DialogUtils.showExistDialog(this, existListener);
            dialog.show();
        } else {
            if (!isQuit) {
                isQuit = true;
                ToastUtils.showMsg(getBaseContext(), getString(R.string.double_click_exit));
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                SnailFreeStoreServiceUtil.initRemindUser(MainActivity.this);
                finish();
            }
        }
    }

    View.OnClickListener existListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SnailFreeStoreServiceUtil.initRemindUser(MainActivity.this);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queryTask != null)
            queryTask.cancel(true);
        MainThreadBus.getInstance().unregister(this);

        if (mCallBack != null)    // 将activity设为null
            mCallBack.activityDestroy();

        if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
            mUpdateDialog.dismiss();
        }
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
//        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Action bar event
            case R.id.notice_area:
                startActivity(NoticeActivity.newIntent(this));
                break;

            case R.id.messing_scanner:
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), CaptureActivity.SCAN_REQUEST_CODE);
                break;

            case R.id.tv_right_action:
                if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                    Dialog dialog = DialogUtils.showUserQuitDialog(MainActivity.this, false);
                    dialog.show();
                }
                break;

            case R.id.toolbar_searchview:
                int searchResultTabIndex = SearchFragmentAdapter.TAB_ALL;
                if (currentTab == TAB_SEEK_GAME || currentTab == TAB_COMMUNICATION) {
                    searchResultTabIndex = SearchFragmentAdapter.TAB_GAME_APP;
                } else if (currentTab == TAB_COOL_PLAY) {
                    searchResultTabIndex = SearchFragmentAdapter.TAB_KUWAN;
                }
                startActivity(AppSearchActivity.newIntent(this, searchResultTabIndex));
                break;

            // Tab bar event
            case R.id.tab_home:
                changeContentAndButtonState(TAB_HOME);
                switchTab(TAB_HOME);
                break;

            case R.id.tab_play_game:
                changeContentAndButtonState(TAB_SEEK_GAME);
                switchTab(TAB_SEEK_GAME);
                break;

            case R.id.tab_communication:
                changeContentAndButtonState(TAB_COMMUNICATION);
                switchTab(TAB_COMMUNICATION);
                break;

            case R.id.tab_cool_play:
                changeContentAndButtonState(TAB_COOL_PLAY);
                switchTab(TAB_COOL_PLAY);
                break;

            case R.id.mine:
                //如果没登陆就算没有点击过我的标签页
                if (GlobalVar.getInstance().getUsrInfo() != null) {
                    isMineTabClickedToCancelRedDot = true;
                }
                if (scratchImg.getVisibility() == View.VISIBLE) {
                    scratchImg.setVisibility(View.GONE);
                }
                changeContentAndButtonState(TAB_MINE);
                switchTab(TAB_MINE);
                break;

            case R.id.iv_manage:
            case R.id.msg_layout:
                startActivity(ManageActivity.newIntent(this));
                break;

            default:
                break;
        }
    }


    // 获取百宝箱信息
    private void getTreasureBox() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_GET_TREASUREBOX_URL, TAG, TreasureBoxInfoModel.class, new IFDResponse<TreasureBoxInfoModel>() {
            @Override
            public void onSuccess(TreasureBoxInfoModel result) {
                if (result != null)
                    GlobalVar.getInstance().setMTreasureBoxInfoList(result.getTreasureBoxInfos());

            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, true, true, new ExtendJsonUtil());
    }

    private void switchTab(int currentTab) {
        mToolbarView.setBackgroundColor(ResUtil.getColor(R.color.red));

        switch (indexRefresh(currentTab)) {
            case TAB_HOME:
                selectHomeTab();
                homeFragmentDestroyed = false;
                break;
            case TAB_SEEK_GAME:
                homeFragmentDestroyed = false;
                leaveHomeTab(false);
                break;
            case TAB_MINE:
                homeFragmentDestroyed = true;
                leaveHomeTab(true);
                break;
            default:
                homeFragmentDestroyed = true;
                leaveHomeTab(false);
                break;
        }
    }

    /**
     * 当前选中的tab不是首页
     */
    private void leaveHomeTab(boolean isUserFragment) {
        mToolbarView.setVisibility(View.VISIBLE);
        mToolbarUserCenterText.setAlpha(1);
        //处理首页下拉后切换到其他页签 Actionbar会变白问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mToolbarView.setAlpha(1);
        }
        unTransparentStatusbarAndToolbar();
        mToolbarUserCenterText.setVisibility(isUserFragment ? View.VISIBLE : View.GONE);
        mBannerMask.setVisibility(isUserFragment ? View.GONE : View.VISIBLE);
        //判断是否登陆
        if (!LoginSDKUtil.isLogined(MainActivity.this)) {
            mToolbarRightActionText.setVisibility(View.GONE);
        } else {
            mToolbarRightActionText.setVisibility(isUserFragment ? View.VISIBLE : View.GONE);
        }
        mMsgButton.setVisibility(isUserFragment ? View.GONE : View.VISIBLE);
        mToolbarSearchView.setVisibility(isUserFragment ? View.GONE : View.VISIBLE);
        if (isUserFragment) {
            msgLayout.setVisibility(View.GONE);
            imageMessing.setVisibility(View.GONE);
            setActionBarStatusBarAlpha(userCenterAlpha);
        } else {
            if (!TextUtils.isEmpty(treasurebox_msg.getText().toString()) &&
                    Integer.valueOf(treasurebox_msg.getText().toString()) > 0) {

                msgLayout.setVisibility(View.VISIBLE);
                imageMessing.setVisibility(View.GONE);
            } else {
                msgLayout.setVisibility(View.GONE);
                imageMessing.setVisibility(View.VISIBLE);
            }
        }
    }

    private void unTransparentStatusbarAndToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.argb(ALPHA_FULL, 214, 69, 70));
        }
        mToolbarDrawable.setAlpha(ALPHA_FULL);
        mSearchDrawable.setAlpha(ALPHA_FULL);
        mToolbarHotSearchTipsView.setTextColor(ResUtil.getColor(R.color.general_text_color));
        mRedPointView.setBackgroundResource(R.drawable.white_point);
    }

    /**
     * 当前选中的tab是首页
     */
    private void selectHomeTab() {
        mToolbarView.setAlpha(1);
        mToolbarUserCenterText.setAlpha(1);
        mBannerMask.setVisibility(View.VISIBLE);
        mToolbarUserCenterText.setVisibility(View.GONE);
        mToolbarRightActionText.setVisibility(View.GONE);
        mMsgButton.setVisibility(View.VISIBLE);

        mToolbarSearchView.setVisibility(View.VISIBLE);
        String treasurebox = treasurebox_msg.getText().toString();
        if (!TextUtils.isEmpty(treasurebox) && Integer.valueOf(treasurebox) > 0) {
            msgLayout.setVisibility(View.VISIBLE);
            imageMessing.setVisibility(View.GONE);
        } else {
            msgLayout.setVisibility(View.GONE);
            imageMessing.setVisibility(View.VISIBLE);
        }
        if (!homeFragmentDestroyed) {
            setStatusBarColor(alpha);
            setToolbarAlpha(alpha);
            if (homeDataLoaded) {
                mToolbarView.setVisibility(View.VISIBLE);
            } else {
                mToolbarView.setVisibility(View.GONE);
            }
        } else {
            mToolbarView.setVisibility(View.GONE);
            setStatusBarColor(ALPHA_TRANSPARENT);
            setToolbarAlpha(ALPHA_TRANSPARENT);
        }

    }

    /**
     * 当首页数据显示时
     */
    public void onHomeViewShowed() {
        homeDataLoaded = true;
        if (currentTab == TAB_HOME) {
            mToolbarView.setVisibility(View.VISIBLE);
        }
    }


    private void changeContentAndButtonState(int selectIndex) {
        if (isTabValid) {
            isTabValid = false;

            setTabButtonClickState(false, selectIndex);

            selectTab(selectIndex);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isTabValid = true;
                    setTabButtonClickState(true, currentTab);
                }
            }, 200);
        }
    }


    private void setTabButtonClickState(boolean state, int index) {
        for (int i = 0; i < tabButtons.size(); i++) {
            if (i != index) {
                unChecked(tabButtons.get(i));
            } else {
                checked(tabButtons.get(i));
            }
            tabButtons.get(i).setClickable(state);
        }
    }

    private void unChecked(RadioButton radioButton) {
        radioButton.setPressed(false);
        radioButton.setChecked(false);
        radioButton.setSelected(false);
        radioButton.setTextColor(ResUtil.getColor(R.color.general_text_color));
    }

    private void checked(RadioButton radioButton) {
        radioButton.setChecked(true);
        radioButton.setSelected(true);
        radioButton.setTextColor(ResUtil.getColor(R.color.red));
    }

    private void selectTab(int selectIndex) {
        if (currentTab == selectIndex) {
            MainThreadBus.getInstance().post(new TabClickedEvent(indexRefresh(currentTab), -1));
        }

        UIUtil.changeViewPagerTabWithOutAnimation(mContentPager, indexRefresh(selectIndex));
        currentTab = selectIndex; // 更新目标tab为当前tab
    }


    private void installApk(String downloadUrl) {
        try {
            Uri uri = Uri.parse(downloadUrl);
            if (uri != null) {
                ApkInstaller apkInstaller = new ApkInstaller(this, 0, uri.getPath(), AppConstants.APP_PACKAGE_NAME);
                apkInstaller.execute();
            } else {
                ApkInstaller.installApk(downloadUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置右上角推送消息数量
     *
     * @param count notice num
     */
    private void setNoticeMsgNum(int count) {
        if (mMsgButton == null)
            return;
        if (count > 0) {
            mRedPointView.setVisibility(View.VISIBLE);
        } else {
            mRedPointView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取百宝箱图标上面的应用可更新数量并显示在图标上
     */
    private void getUpgradeInfo() {
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(MainActivity.this, new MyGameDaoHelper.MyGameCallback() {
            @Override
            public void Callback(List<AppInfo> appInfos) {
                int updatecount = GameManageUtil.getUpdateInfos(MainActivity.this, appInfos, false).size();
                setUpdateCount(updatecount);
            }
        });
    }


    // 获取装机必备
    private void getNecessaryApp() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_SNAIL_NECESSARY_APP, TAG, NecessaryAppInfoModel.class, new IFDResponse<NecessaryAppInfoModel>() {
            @Override
            public void onSuccess(NecessaryAppInfoModel result) {
                if (result != null && result.getInfos() != null && result.getPage() != null) {
                    ArrayList<InstallGameInfo> installedGames = PackageInfoUtil.getAllInstalledGames(MainActivity.this);
                    Iterator<NecessaryAppInfo> iterator = result.getInfos().iterator();
                    while (iterator.hasNext()) {
                        NecessaryAppInfo necessaryAppInfo = iterator.next();
                        for (InstallGameInfo installGameInfo : installedGames) {
                            if (necessaryAppInfo.getcPackage().equals(installGameInfo.getPackageName())) {
                                iterator.remove();
                            }
                        }
                    }
                    //如果全部都安装过就不显示
                    if (result.getInfos().size() == 0) {
                        // 不弹出情况下，判定为已弹出
                        MainThreadBus.getInstance().post(new NecessaryDialogDismissEvent());
                        return;
                    }
                    //只保留最多九个应用
                    if (result.getInfos().size() > 9) {
                        result.setInfos(result.getInfos().subList(0, 9));
                    }

                    NecessaryAppDialog necessaryAppDialog = new NecessaryAppDialog(MainActivity.this);
                    necessaryAppDialog.setData(result);
                    necessaryAppDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            MainThreadBus.getInstance().post(new NecessaryDialogDismissEvent());
                        }
                    });
                    necessaryAppDialog.show();
                } else {
                    // 异常情况下，判定为已弹出
                    MainThreadBus.getInstance().post(new NecessaryDialogDismissEvent());
                }
            }

            @Override
            public void onNetWorkError() {
                // 异常情况下，判定为已弹出
                MainThreadBus.getInstance().post(new NecessaryDialogDismissEvent());
            }

            @Override
            public void onServerError() {
                // 异常情况下，判定为已弹出
                MainThreadBus.getInstance().post(new NecessaryDialogDismissEvent());
            }
        }, false, true, new ExtendJsonUtil());
    }

    // 检测自身更新
    private void CheckUpdateSelf() {
        UpdateUtil mUtil = new UpdateUtil();
        mCallBack = new UpdateCallBackImpl(this, true);
        mUtil.checkUpdate(mCallBack, TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.SCAN_REQUEST_CODE:
                    final AlertDialog aDialog = DialogUtils.scanSuccessDialog(MainActivity.this, data);
                    aDialog.show();
                    break;
            }
        }
    }

    /**
     * 游戏更新数量
     *
     * @param updateCount update app num
     */
    private void setUpdateCount(int updateCount) {
        if (currentTab != TAB_MINE) {
            if (updateCount > 0) {
                msgLayout.setVisibility(View.VISIBLE);
                imageMessing.setVisibility(View.GONE);
                setTreasureboxMsg(treasurebox_msg, updateCount);
            } else {
                msgLayout.setVisibility(View.GONE);
                imageMessing.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setTreasureboxMsg(TextView treasurebox_msg, int updatecount) {
        if (updatecount >= 10) {
            if (updatecount >= 100)
                updatecount = 99;
            treasurebox_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.getDimensionPixelSize(R.dimen.text_size_little));
        } else {
            treasurebox_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.getDimensionPixelSize(R.dimen.text_size_normal));
        }
        treasurebox_msg.setText(String.valueOf(updatecount));
    }


    /**
     * 绑定 cid
     */
    private void bindCid() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cCID", AppConstants.CLIENT_ID);
        parameters.put("cAppID", AppConstants.GETTUI_APP_ID);
        parameters.put("cChannelID", ChannelUtil.getChannelID());
        parameters.put("cPhoneType", "ANDROID");
        parameters.put("cIMEI", SnailCommplatform.getInstance().getCIMEI(FreeStoreApp.getContext()));
        parameters.put("nAppVersion", String.valueOf(ComUtil.getSelfVersionCode()));

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JOSN_URL_BIND_CID, TAG, BindCidModel.class, new IFDResponse<BindCidModel>() {
            @Override
            public void onSuccess(BindCidModel result) {
                if (result != null && result.getCode() == 0) {
                    String tag = result.getVal();
                    if (TextUtils.isEmpty(tag)) {//如果没有tag则不需要绑定tag
                        SharedPreferencesUtil.getInstance().setBindCid(true);
                        return;
                    }
                    String[] tags;
                    if (tag.contains(",")) {
                        tags = tag.split(",");
                    } else {
                        tags = new String[]{tag};
                    }
                    final Tag[] tagParam = new Tag[tags.length];
                    for (int i = 0; i < tags.length; i++) {
                        Tag t = new Tag();
                        t.setName(tags[i]);
                        tagParam[i] = t;
                    }

                    int i = PushManager.getInstance().setTag(MainActivity.this, tagParam);
                    switch (i) {
                        case PushConsts.SETTAG_SUCCESS:
                            SharedPreferencesUtil.getInstance().setBindCid(true);
                            LogUtils.d("set tag success");
                            break;
                        case PushConsts.SETTAG_ERROR_COUNT:
                            LogUtils.d("set tag fail");
                            break;
                        default:
                            break;
                    }

                }
            }

            @Override
            public void onNetWorkError() {

            }

            @Override
            public void onServerError() {

            }
        }, parameters);
    }
}