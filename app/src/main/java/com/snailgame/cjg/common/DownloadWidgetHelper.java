package com.snailgame.cjg.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.message.model.MsgNum;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.StaticsUtils;

/**
 * 下载组件助手，提供组件状态的控制和切换，以及处理相应下载逻辑
 * Created by pancl on 2014/10/22.
 */
public class DownloadWidgetHelper {

    private static DownloadWidgetHelper helper;

    public static DownloadWidgetHelper getHelper() {
        if (helper == null)
            helper = new DownloadWidgetHelper();
        return helper;
    }

    /**
     * 下载状态
     */
    private static final int DOWNLOAD_TAG_NEW = 1; // 新增或升级下载
    private static final int DOWNLOAD_TAG_CONTINUE = 2; // 继续下载
    private static final int DOWNLOAD_TAG_RESTART = 3; // 重新下载

    /**
     * 检查下载状态是否已下载完成，若完成搜索本地应用是否已安装且版本较新，若是则设定下载状态为已安装
     *
     * @param appInfo  应用信息
     * @param mContext 上下文
     * @return 最新下载状态
     */
    public int checkDownloadState(AppInfo appInfo, Context mContext) {
        // add for check the button status when apk is auto installed successfully
        int downloadState = appInfo.getDownloadState();
        if (downloadState == DownloadManager.STATUS_SUCCESSFUL
                || downloadState == 0) {
            PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(mContext, appInfo.getPkgName());
            if (packageInfo != null && packageInfo.versionCode >= appInfo.getVersionCode()) {
                downloadState = DownloadManager.STATUS_EXTRA_INSTALLED;
                appInfo.setDownloadState(downloadState);
            }
        }
        return downloadState;
    }

    /**
     * 根据应用当前状态，判断应该转换到哪个状态
     *
     * @param downloadState            下载状态
     * @param iDownloadStateSwitchable 状态切换的对象
     */
    public synchronized void switchState(int downloadState, IDownloadStateSwitchable iDownloadStateSwitchable) {
        iDownloadStateSwitchable.switching();
        switch (downloadState) {
            case DownloadManager.STATUS_PENDING:
                iDownloadStateSwitchable.setState(R.id.download_state_waiting);
                iDownloadStateSwitchable.switchToWaiting();
                break;
            case DownloadManager.STATUS_PENDING_FOR_WIFI:
                iDownloadStateSwitchable.setState(R.id.download_state_waiting_for_wifi);
                iDownloadStateSwitchable.switchToWaitingForWifi();
                break;
            case DownloadManager.STATUS_PAUSED:
                iDownloadStateSwitchable.setState(R.id.download_state_continue);
                iDownloadStateSwitchable.switchToContinue();
                break;
            case DownloadManager.STATUS_RUNNING:
                iDownloadStateSwitchable.setState(R.id.download_state_pause);
                iDownloadStateSwitchable.switchToPause();
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                iDownloadStateSwitchable.setState(R.id.download_state_install);
                iDownloadStateSwitchable.switchToInstall();
                break;
            case DownloadManager.STATUS_EXTRA_INSTALLED:
                iDownloadStateSwitchable.setState(R.id.download_state_open);
                iDownloadStateSwitchable.switchToOpen();
                break;
            case DownloadManager.STATUS_FAILED:
                iDownloadStateSwitchable.setState(R.id.download_state_fail);
                iDownloadStateSwitchable.switchToRedownload();
                break;
            case DownloadManager.STATUS_INSTALLING:
                iDownloadStateSwitchable.setState(R.id.download_state_installing);
                iDownloadStateSwitchable.switchToInstalling();
                break;
            case DownloadManager.STATUS_PATCHING:
                iDownloadStateSwitchable.setState(R.id.download_state_patching);
                iDownloadStateSwitchable.switchToPatching();
                break;
            case DownloadManager.STATUS_EXTRA_UPGRADABLE:
                iDownloadStateSwitchable.setState(R.id.download_state_upgrade);
                iDownloadStateSwitchable.switchToUpgrade();
                break;
            case DownloadManager.STATUS_NOTREADY:
                iDownloadStateSwitchable.setState(R.id.download_state_notready);
                iDownloadStateSwitchable.switchToNotReady();
                break;
            default:
                iDownloadStateSwitchable.setState(R.id.download_state_download);
                iDownloadStateSwitchable.switchToDownload();
        }
        iDownloadStateSwitchable.switched();

        if (downloadState == 0
                || downloadState == DownloadManager.STATUS_FAILED
                || downloadState == DownloadManager.STATUS_SUCCESSFUL
                || downloadState == DownloadManager.STATUS_INSTALLING
                || downloadState == DownloadManager.STATUS_PATCHING
                || downloadState == DownloadManager.STATUS_EXTRA_INSTALLED
                || downloadState == DownloadManager.STATUS_EXTRA_UPGRADABLE
                || downloadState == DownloadManager.STATUS_NOTREADY) {
            iDownloadStateSwitchable.setFlowFreeView(true);
        } else {
            iDownloadStateSwitchable.setFlowFreeView(false);
        }

        iDownloadStateSwitchable.setAppointmentState();
    }

    /**
     * 根据状态值处理相应下载逻辑
     *
     * @param mContext                 上下文
     * @param iDownloadStateSwitchable 状态切换的对象
     * @param currAppInfo              下载必要参数
     */
    public void handleState(final Context mContext, final IDownloadStateSwitchable iDownloadStateSwitchable, final AppInfo currAppInfo) {
        final int status = iDownloadStateSwitchable.getState();
        switch (status) {
            case R.id.download_state_download:
            case R.id.download_state_upgrade:
                StaticsUtils.onDownload(currAppInfo.getRoute());
                DownloadConfirm.showDownloadConfirmDialog(mContext, new DownloadConfirm.IConfirmResult() {
                    @Override
                    public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                        if (currAppInfo != null) {
                            //TAJ 应用详情升级--》下载管理取消--》升级列表不会更新显示
                            /*if (finalDownloadTag == DOWNLOAD_TAG_NEW && isDialogResult) {
                                MyGameDBUtil.updateIsUpdate(mContext, currAppInfo.getPkgName());
                            }*/
                            //升级情况 刷新数量
                            if (status == R.id.download_state_upgrade) {
                                MsgNum.getInstance().getNums();
                            }
                            if (!isUseFlowDownLoad) {
                                currAppInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                            }
                            startDownload(mContext, iDownloadStateSwitchable, currAppInfo, DOWNLOAD_TAG_NEW);
                        }
                    }

                    @Override
                    public void doDismissDialog(boolean isDialogDismiss) {

                    }
                }, currAppInfo);
                break;
            case R.id.download_state_continue:
                startDownload(mContext, iDownloadStateSwitchable, currAppInfo, DOWNLOAD_TAG_CONTINUE);
                break;
            case R.id.download_state_fail:
                startDownload(mContext, iDownloadStateSwitchable, currAppInfo, DOWNLOAD_TAG_RESTART);
                break;
            case R.id.download_state_pause:
                iDownloadStateSwitchable.switching();
                if (!DownloadHelper.pauseDownload(mContext, currAppInfo.getApkDownloadId())) {
                    iDownloadStateSwitchable.switchToPause();
                    iDownloadStateSwitchable.switched();
                }
                break;
            case R.id.download_state_install:
                String apkLocalUri = currAppInfo.getLocalUri();
                if (apkLocalUri != null) {
                    ApkInstaller.installApk(apkLocalUri);
                }
                break;
            case R.id.download_state_patching:
            case R.id.download_state_notready:
                break;
            case R.id.download_state_open:
                openGame(mContext, currAppInfo);
                break;
            case R.id.download_state_waiting_for_wifi:
                startDownload(mContext, iDownloadStateSwitchable, currAppInfo, DOWNLOAD_TAG_CONTINUE);
                break;
            default:
                break;
        }
    }

    public static void openGame(Context mContext, AppInfo currAppInfo) {
        Intent openApkIntent = ComUtil.getLaunchIntentForPackage(mContext, currAppInfo.getPkgName());
        if (openApkIntent != null) {
            mContext.startActivity(openApkIntent);
        }
        StaticsUtils.openGameInFreeStore(currAppInfo.getAppId());
    }

    /**
     * 根据当前下载状态（downloadTag）执行不同下载逻辑
     *
     * @param mContext                 上下文
     * @param iDownloadStateSwitchable 状态切换的对象
     * @param currAppInfo              下载必要参数
     * @param downloadTag              参考 DOWNLOAD_TAG_NEW, DOWNLOAD_TAG_CONTINUE, DOWNLOAD_TAG_RESTART
     */
    private void startDownload(Context mContext, final IDownloadStateSwitchable iDownloadStateSwitchable, AppInfo currAppInfo, int downloadTag) {
        switch (downloadTag) {
            case DOWNLOAD_TAG_NEW:
                DownloadHelper.startDownload(mContext, currAppInfo, new DownloadHelper.IDownloadCheck() {
                    @Override
                    public void onPostResult(boolean result) {
                        if (!result) {
                            switchState(DownloadManager.STATUS_INITIAL, iDownloadStateSwitchable);
                        }
                    }
                });
                break;
            case DOWNLOAD_TAG_CONTINUE:
                if (!DownloadHelper.resumeDownload(mContext, currAppInfo.getApkDownloadId())) {
                    switchState(DownloadManager.STATUS_PAUSED, iDownloadStateSwitchable);
                }
                break;
            case DOWNLOAD_TAG_RESTART:
                if (!DownloadHelper.restartDownload(mContext, currAppInfo.getApkDownloadId())) {
                    switchState(DownloadManager.STATUS_FAILED, iDownloadStateSwitchable);
                }
                break;
            default:
                break;
        }


    }
}
