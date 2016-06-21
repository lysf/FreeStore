package com.snailgame.cjg.downloadmanager.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.ServiceStopEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenzaih on 2014/4/8.
 */
public class GameManageUtil {
    public static final String TAG = GameManageUtil.class.getSimpleName();

    public static SpannableStringBuilder getTextContent(long dataVolume, long patchDataVolume) {
        String s = Formatter.formatShortFileSize(FreeStoreApp.getContext(), dataVolume);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String s2 = ResUtil.getString(R.string.update_flow);

        if (s != null && s.length() > 0) {
            stringBuilder.append(s2).append(s);
            int start = s2.length();
            if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) {
                stringBuilder.setSpan(new StrikethroughSpan(), start, start + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(" ").append(ResUtil.getString(R.string.update_flow_zero));
                stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)), start + s.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (dataVolume != patchDataVolume) {
                stringBuilder.setSpan(new StrikethroughSpan(), start, start + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(" ").append(Formatter.formatShortFileSize(FreeStoreApp.getContext(), patchDataVolume));
                stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)), start + s.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)), start, start + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            stringBuilder.append(s2);
        }

        return stringBuilder;
    }

    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int appTotalNum, long appTotalSize) {
        String totalAppSize = Formatter.formatShortFileSize(FreeStoreApp.getContext(), appTotalSize);
        String totalAppNum = String.valueOf(appTotalNum);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String s1 = ResUtil.getString(R.string.update_num);
        String s2 = ResUtil.getString(R.string.save);
        //如果登录了免商店且现在是免流量状态才显示省了多少流量
        if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) {
            stringBuilder.append(totalAppNum + s1 + s2 + totalAppSize);
            int totalAppNumLength = totalAppNum.length();
            int s1Length = s1.length();
            int s2Length = s2.length();
            int totalAppSizeLength = totalAppSize.length();
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#6dc343")), 0, totalAppNumLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#6dc343")), totalAppNumLength + s1Length + s2Length,
                    totalAppNumLength + s1Length + s2Length + totalAppSizeLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            stringBuilder.append(totalAppNum + s1);
            int totalAppNumLength = totalAppNum.length();
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#6dc343")), 0, totalAppNumLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return stringBuilder;

    }


    public static void startCheckUpdateService(Context context) {
        context.startService(SnailFreeStoreService.newIntent(context, SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME, AppConstants.UPDATE_REQUEST_START));
    }

    public static void stopCheckUpdateService() {
        MainThreadBus.getInstance().post(new ServiceStopEvent(SnailFreeStoreService.TYPE_AUTO_CHECK_UPGRADABLE_GAME));
    }


    /**
     * Forward to HomeActivity when user click on "Back" button
     * if Current activity is launched from status bar, otherwise close current activity
     */
    public static void forwardToHomeOrCloseActivity(Activity context) {
        if (checkIfLaunchFromStatusBar(context)) {
            context.startActivity(MainActivity.newIntent(context, Intent.FLAG_ACTIVITY_NEW_TASK));
            context.finish();
            return;

        }
        context.finish();
    }

    /**
     * Check if Current activity is launched from status bar
     *
     * @return true if Current activity is launched from status bar, otherwise false
     */
    public static boolean checkIfLaunchFromStatusBar(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        if (runningTaskInfoList != null) {
            for (ActivityManager.RunningTaskInfo info : runningTaskInfoList) {
                if (info.baseActivity != null && info.topActivity != null) {
                    String bClassName = info.baseActivity.getClassName();
                    String tClassName = info.topActivity.getClassName();
                    if (bClassName != null && bClassName.equals(tClassName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 返回本地可更新的appinfo
     * 过滤免商店自身应用、未安装、版本已是最新、当前正在下载（根据 {@param #isShowLoading}）的AppInfo
     *
     * @param appInfos
     * @param isShowLoading
     * @return
     */
    public static List<AppInfo> getUpdateInfos(Context context, List<AppInfo> appInfos, boolean isShowLoading) {
        int queryFilter = DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_PENDING
                | DownloadManager.STATUS_PENDING_FOR_WIFI
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING
                | DownloadManager.STATUS_PATCHING;

        List<TaskInfo> taskInfoList = DownloadHelper.getDownloadTasks(
                FreeStoreApp.getContext(),
                DownloadHelper.QUERY_TYPE_BY_STATUS,
                queryFilter);

        List<AppInfo> infos = new ArrayList<AppInfo>();
        for (AppInfo appInfo1 : appInfos) {
            if (appInfo1.getIsUpdate() == 1 &&
                    !appInfo1.getPkgName().equals(FreeStoreApp.getContext().getPackageName())) {
                PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(FreeStoreApp.getContext(), appInfo1.getPkgName());
                if (packageInfo == null) {      // 未安装
                    continue;
                } else {
                    if (packageInfo.versionCode >= appInfo1.getVersionCode() || appInfo1.getVersionCode() <= appInfo1.getUpgradeIgnoreCode())
                        continue;
                }

                AppInfo appInfo = fillAppInfo(taskInfoList, appInfo1);
                // 如果是不需要显示正在下载的任务，忽略它
                if (!isShowLoading && appInfo.isDownloading()) {
                    continue;
                }
                infos.add(appInfo);
            }
        }
        return infos;
    }

    /**
     * 返回本地可更新的appinfo
     * 过滤免商店自身应用、未安装、版本已是最新、当前正在下载（根据 {@param #isShowLoading}）的AppInfo
     *
     * @param appInfos
     * @return
     */
    public static ArrayList<AppInfo> getUpgradeIgnoreList(Context context, List<AppInfo> appInfos) {
        int queryFilter = DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_PENDING
                | DownloadManager.STATUS_PENDING_FOR_WIFI
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING
                | DownloadManager.STATUS_PATCHING;

        List<TaskInfo> taskInfoList = DownloadHelper.getDownloadTasks(
                FreeStoreApp.getContext(),
                DownloadHelper.QUERY_TYPE_BY_STATUS,
                queryFilter);

        ArrayList<AppInfo> infos = new ArrayList<AppInfo>();
        for (AppInfo appInfo1 : appInfos) {
            if (appInfo1.getIsUpdate() == 1 &&
                    !appInfo1.getPkgName().equals(FreeStoreApp.getContext().getPackageName())) {
                PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(FreeStoreApp.getContext(), appInfo1.getPkgName());
                if (packageInfo == null) {      // 未安装
                    continue;
                } else {
                    // 本地安装版本已经是最新
                    if (packageInfo.versionCode >= appInfo1.getVersionCode())
                        continue;
                }

                if (appInfo1.getVersionCode() <= appInfo1.getUpgradeIgnoreCode()) {
                    AppInfo appInfo = fillAppInfo(taskInfoList, appInfo1);
                    // 如果是不需要显示正在下载的任务，忽略它
                    if (appInfo.isDownloading()) {
                        continue;
                    }
                    infos.add(appInfo);
                }
            }
        }
        return infos;
    }

    private static AppInfo fillAppInfo(List<TaskInfo> taskInfoList, AppInfo appInfo1) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(appInfo1.getAppName());
        appInfo.setApkUrl(appInfo1.getApkUrl());
        appInfo.setVersionCode(appInfo1.getVersionCode());
        appInfo.setVersionName(appInfo1.getVersionName());
        appInfo.setApkSize(appInfo1.getApkSize());
        appInfo.setIcon(appInfo1.getIcon());
        appInfo.setIsUpdate(appInfo1.getIsUpdate());
        appInfo.setIsPatch(appInfo1.getIsPatch());
        appInfo.setDiffUrl(appInfo1.getDiffUrl());
        appInfo.setDiffSize(appInfo1.getDiffSize());
        appInfo.setDiffMd5(appInfo1.getDiffMd5());
        appInfo.setUpgradeDesc(appInfo1.getUpgradeDesc());
        appInfo.setUpgradeIgnoreCode(appInfo1.getUpgradeIgnoreCode());
        appInfo.setPkgName(appInfo1.getPkgName());
        appInfo.setAppId(appInfo1.getAppId());
        appInfo.setMd5(appInfo1.getMd5());
        appInfo.setcAppType(appInfo1.getcAppType());
        appInfo.setInstalledApkVersionName(appInfo1.getInstalledApkVersionName());
        appInfo.setTotalIntsallNum(appInfo1.getTotalIntsallNum());
        for (TaskInfo taskInfo : taskInfoList) {
            if (appInfo1.getAppId() == taskInfo.getAppId()) {
                appInfo.setDownloading(true);
                break;
            }
        }
        return appInfo;
    }


}
