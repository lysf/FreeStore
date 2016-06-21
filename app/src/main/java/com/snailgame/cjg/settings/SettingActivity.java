package com.snailgame.cjg.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.AutoInstallEvent;
import com.snailgame.cjg.event.UpdateDialogReshowEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.settings.adapter.SettingAdapter;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener {
    static String TAG = SettingActivity.class.getName();
    private SettingAdapter adapter;

    private UpdateCallBackImpl mCallBack;        // 检测免商店更新的回调
    private Map<Integer, Boolean> map;

    public static final int TYPE_WIFI_DOWNLOAD = 0;
    public static final int TYPE_FLOW_DOWNLOAD_DIALOG = 1;
    public static final int TYPE_UNROOT_AUTO_INSTALL = 3;
    public static final int TYPE_ROOT_AUTO_INSTALL = 2;
    public static final int TYPE_AUTO_DEL_APK = 4;
    public static final int TYPE_MESSAGE_PUSH = 5;
    public static final int TYPE_UPDATE_IGNORE = 6;
    public static final int TYPE_CHECK_UPDATE = 7;
    public static final int TYPE_FEEDBACK = 8;
    public static final int TYPE_ABOUNT = 9;

    @Subscribe
    public void onUpdateDailogReshowed(UpdateDialogReshowEvent event) {
        if (mCallBack != null)
            mCallBack.reShow();
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, SettingActivity.class);
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
        return R.layout.activity_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.actionbar_setting);
        ListView settingListView = (ListView) findViewById(R.id.setting_list);
        initCheckBox();
        adapter = new SettingAdapter(SettingActivity.this, map);
        settingListView.setAdapter(adapter);
        settingListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        settingListView.setOnItemClickListener(this);
        MainThreadBus.getInstance().register(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_SETTING);

        map.put(TYPE_UNROOT_AUTO_INSTALL, AutoInstallAccessibilityService.isAccessibilitySettingsOn(this));
        adapter.notifyDataSetChanged();

        if (AutoInstallAccessibilityService.isAccessibilitySettingsOn(this)) {
            MobclickAgent.onEvent(this, UmengAnalytics.EVENT_UNROOT_AUTO_INSTALL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_SETTING);
    }

    private void initCheckBox() {
        map = new HashMap<>();
        map.put(TYPE_FLOW_DOWNLOAD_DIALOG, !SharedPreferencesUtil.getInstance().isDoNotAlertAnyMore1());
        map.put(TYPE_WIFI_DOWNLOAD, SharedPreferencesUtil.getInstance().isWifiOnly());
        map.put(TYPE_AUTO_DEL_APK, SharedPreferencesUtil.getInstance().isAutoDelApp());
        map.put(TYPE_MESSAGE_PUSH, SharedPreferencesUtil.getInstance().isOpenPush());
        map.put(TYPE_UNROOT_AUTO_INSTALL, AutoInstallAccessibilityService.isAccessibilitySettingsOn(this));
        map.put(TYPE_ROOT_AUTO_INSTALL, SharedPreferencesUtil.getInstance().isAutoInstall());
        map.put(TYPE_UPDATE_IGNORE, SharedPreferencesUtil.getInstance().isUpdate());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        switch (position) {
            case TYPE_WIFI_DOWNLOAD:
                map.put(TYPE_WIFI_DOWNLOAD, !map.get(TYPE_WIFI_DOWNLOAD));
                adapter.notifyDataSetChanged();
                SharedPreferencesUtil.getInstance().setWifiOnly(map.get(TYPE_WIFI_DOWNLOAD));
                wifiOnly();
                break;
            case TYPE_AUTO_DEL_APK:
                map.put(TYPE_AUTO_DEL_APK, !map.get(TYPE_AUTO_DEL_APK));
                adapter.notifyDataSetChanged();
                SharedPreferencesUtil.getInstance().setAutoDelApp(map.get(TYPE_AUTO_DEL_APK));
                break;

            case TYPE_FLOW_DOWNLOAD_DIALOG:
                map.put(TYPE_FLOW_DOWNLOAD_DIALOG, !map.get(TYPE_FLOW_DOWNLOAD_DIALOG));
                adapter.notifyDataSetChanged();
                SharedPreferencesUtil.getInstance().setDoNotAlertAnyMore1(!map.get(TYPE_FLOW_DOWNLOAD_DIALOG));
                break;

            case TYPE_MESSAGE_PUSH:
                map.put(TYPE_MESSAGE_PUSH, !map.get(TYPE_MESSAGE_PUSH));
                adapter.notifyDataSetChanged();
                SharedPreferencesUtil.getInstance().setOpenPush(map.get(TYPE_MESSAGE_PUSH));
                break;


            case TYPE_UNROOT_AUTO_INSTALL:
                try {
                    Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(killIntent);

                    startActivity(AppAutoInstallSettingDialogActivity.newIntent(this));
                } catch (Exception ActivityNotFoundException) {
                    ToastUtils.showMsg(this, getString(R.string.msg_mobile_dont_support_function));
                }

                break;

            case TYPE_ROOT_AUTO_INSTALL:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (CommandHelper.hasRoot()) {
                            map.put(TYPE_ROOT_AUTO_INSTALL, !map.get(TYPE_ROOT_AUTO_INSTALL));
                            MainThreadBus.getInstance().post(new AutoInstallEvent(true));
                            SharedPreferencesUtil.getInstance().setAutoInstall(map.get(TYPE_ROOT_AUTO_INSTALL));
                            SharedPreferencesUtil.getInstance().setAlertGrantRoot(false);
                        } else {
                            MainThreadBus.getInstance().post(new AutoInstallEvent(false));
                        }
                    }
                }).start();
                break;

            case TYPE_UPDATE_IGNORE:
                map.put(TYPE_UPDATE_IGNORE, !map.get(TYPE_UPDATE_IGNORE));
                adapter.notifyDataSetChanged();
                SharedPreferencesUtil.getInstance().setUpdate(map.get(TYPE_UPDATE_IGNORE));
                checkUpdateApk();
                break;
            case TYPE_CHECK_UPDATE:
                if (NetworkUtils.isNetworkAvailable(this)) {
                    final AlertDialog dialog = DialogUtils.createUpdateProgress(SettingActivity.this, ResUtil.getString(R.string.update_detecting), true);
                    UpdateUtil mUtil = new UpdateUtil();
                    mCallBack = new UpdateCallBackImpl(this, false) {
                        @Override
                        public void onCompleted(UpdateModel model) {
                            dialog.dismiss();
                            super.onCompleted(model);
                        }
                    };
                    dialog.show();
                    mUtil.checkUpdate(mCallBack, TAG);
                } else {
                    ToastUtils.showMsg(this, getString(R.string.network_error));
                }
                break;
            case TYPE_FEEDBACK:
                startActivity(FeedBackActivity.newIntent(SettingActivity.this));
                break;

            case TYPE_ABOUNT:
                // About
                startActivity(AboutActivity.newIntent(this));
                break;

            default:
                break;
        }
    }

    private void checkUpdateApk() {
        boolean isShowUpdateMessage = SharedPreferencesUtil.getInstance().isUpdate();
        int tag = PersistentVar.getInstance().getSystemConfig().getGameUpdateTimeSwitch();
        if (isShowUpdateMessage && tag == 1) {
            GameManageUtil.startCheckUpdateService(this);
        } else {
            GameManageUtil.stopCheckUpdateService();
        }
    }

    private void wifiOnly() {
        int allowedNetworkType = ~0;
        if (SharedPreferencesUtil.getInstance().isWifiOnly()) {
            allowedNetworkType = DownloadManager.Request.NETWORK_WIFI;
        }

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(
                DownloadManager.STATUS_PENDING
                        | DownloadManager.STATUS_PENDING_FOR_WIFI
                        | DownloadManager.STATUS_RUNNING
                        | DownloadManager.STATUS_PAUSED
                        | DownloadManager.STATUS_FAILED
        );
        long[] ids = DownloadHelper.getDownloadIdArr(this, query);
        if (ids != null && ids.length > 0)
            DownloadHelper.getDownloadManager(this).toggleAllowedNetworkType(allowedNetworkType, ids);

        if (!NetworkUtils.isWifiEnabled(this)) {
            switch (allowedNetworkType) {
                case DownloadManager.Request.NETWORK_WIFI:
                    query = new DownloadManager.Query();
                    query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                    ids = DownloadHelper.getDownloadIdArr(this, query);
                    break;
                default:
                    query = new DownloadManager.Query();
                    query.setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PAUSED);
                    ids = DownloadHelper.getDownloadIdArr(this, query);
                    break;
            }
            if (ids != null && ids.length > 0)
                DownloadHelper.getDownloadManager(this).markToPauseStatus(allowedNetworkType, ids);
        }
    }

    @Subscribe
    public void onAutoInstallChange(AutoInstallEvent event) {
        if (event.isSuccess()) {
            adapter.notifyDataSetChanged();
        } else {
            ToastUtils.showMsg(SettingActivity.this, getString(R.string.setting_auto_install_deny));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        if (mCallBack != null) {
            mCallBack.activityDestroy();
        }
    }
}


