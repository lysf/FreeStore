package com.snailgame.cjg.settings;

import android.app.Activity;
import android.content.Intent;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;

public class UpdateCallBackImpl implements UpdateUtil.CallBack {
    private boolean bForce = false;
    private boolean mSilent = true;
    private Activity mActivity;
    private AppInfo appInfo;
    private UpdateModel model;

    public UpdateCallBackImpl(Activity activity, boolean isSilent) {
        mActivity = activity;
        mSilent = isSilent;
    }

    // 当activity被销毁后，不做处理
    public void activityDestroy() {
        mActivity = null;
    }

    @Override
    public void onCompleted(final UpdateModel model) {
        if (model == null || !model.getMsg().equals("OK") || model.itemModel == null || mActivity == null) {
            if (mActivity != null && !(mActivity instanceof MainActivity)) {
                ToastUtils.showMsg(FreeStoreApp.getContext(), FreeStoreApp.getContext().getString(R.string.update_failed));
            }
            return;
        }
        if (!model.itemModel.getbUpdate()) {
            if (!mSilent)
                ToastUtils.showMsg(FreeStoreApp.getContext(), FreeStoreApp.getContext().getString(R.string.no_update));
            return;
        }

        SharedPreferencesUtil.getInstance().setSilenceUpdate(false);

        if (!model.itemModel.getcForceUpdate().equals("1")) {
            bForce = false;
            //在非强制更新下，如果是 wifi，启动一个service 静默下载安装包后 提示更新（仅仅在首页自动更新时这样做，手动更新则还是弹出对话框）
            if (NetworkUtils.isWifiEnabled(mActivity) && mSilent) {
                SharedPreferencesUtil.getInstance().setSilenceUpdate(true);

                mActivity.startService(SnailFreeStoreService.newIntent(mActivity,
                        SnailFreeStoreService.TYPE_SILENCE_UPDATE, 0, model.itemModel));
                //不显示Diaolg,跳出该函数
                return;
            }
        } else {
            bForce = true;
        }

        this.model = model;
        appInfo = new AppInfo();
        appInfo.setApkUrl(model.itemModel.getcApkUrl());
        appInfo.setAppName(model.itemModel.getsName());
        appInfo.setPkgName(mActivity.getPackageName());
        appInfo.setIcon(model.itemModel.getcIcon());
        appInfo.setVersionCode(Integer.parseInt(model.itemModel.getnVersionCode()));
        appInfo.setAppId(Integer.parseInt(model.itemModel.getnAppId()));
        appInfo.setVersionName(model.itemModel.getcVersion());
        appInfo.setMd5(model.itemModel.getcMd5Code());
        appInfo.setApkSize(Integer.parseInt(model.itemModel.getcSize()));
        appInfo.setIsUpdate(1);
        appInfo.setIsPatch(0);
        mActivity.startActivity(UpdateDialogActivity.newIntent(mActivity, true, appInfo, model.itemModel.getsDesc(), Intent.FLAG_ACTIVITY_NEW_TASK, bForce));
    }

    /*
     *强制更新中取消后重新弹出升级对话框
     */
    public void reShow() {
        if (appInfo != null && model != null && bForce)
            mActivity.startActivity(UpdateDialogActivity.newIntent(mActivity, true, appInfo, model.itemModel.getsDesc(), Intent.FLAG_ACTIVITY_NEW_TASK, bForce));
    }
}
