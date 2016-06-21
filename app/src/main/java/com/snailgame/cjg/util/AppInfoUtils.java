package com.snailgame.cjg.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.FreeGameItemEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.seekgame.rank.model.RankAppInfo;
import com.snailgame.cjg.util.interfaces.FreeGameRefrsh;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

/**
 * Created by sunxy on 14-3-11.
 */
public class AppInfoUtils {

    public static void bindData(Activity context, List<BaseAppInfo> infos,
                                List<AppInfo> mAppInfoList, CommonListItemAdapter mAdapter, String TAG) {

        addNewDataToList(context, infos, mAppInfoList);

        updatePatchInfo(context, mAppInfoList);

        if (AccountUtil.isAgentFree(context)) {
            updateAppFreeState(context, AppInfoUtils.getAllGameIds(mAppInfoList), mAdapter, TAG);
        } else if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    public static void bindData(Context context, List<BaseAppInfo> infos, List<AppInfo> mAppInfoList, String Tag) {
        addNewDataToList(context, infos, mAppInfoList);
        updatePatchInfo(context, mAppInfoList);
        updateAppFreeState(context, getAllGameIds(mAppInfoList), Tag);
    }

    public static void bindHomeData(Context context, List<BaseAppInfo> infos, List<AppInfo> mAppInfoList, String TAG) {

        addNewDataToList(context, infos, mAppInfoList);

        if (context == null) {
            return;
        }

        updatePatchInfo(context, mAppInfoList);

        if (AccountUtil.isAgentFree(context)) {
            updateAppFreeState(context, AppInfoUtils.getAllGameIds(mAppInfoList), TAG);
        }
    }


    /**
     * 更新Patch相关信息
     *
     * @param context      数据库my_game表所有信息
     * @param mAppInfoList 需要更新的所有信息
     */
    public static void updatePatchInfo(Context context, List<AppInfo> mAppInfoList) {
        List<AppInfo> appInfos = MyGameDaoHelper.queryForAppInfo(context);

        for (AppInfo appInfo : mAppInfoList) {
            for (AppInfo info : appInfos) {
                if (TextUtils.equals(info.getPkgName(), appInfo.getPkgName())) {
                    appInfo.setApkSize(info.getApkSize());
                    if (info.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH) {
                        appInfo.setIsPatch(info.getIsPatch());
                        appInfo.setDiffUrl(info.getDiffUrl());
                        appInfo.setDiffSize(info.getDiffSize());
                        appInfo.setDiffMd5(info.getDiffMd5());
                    }
                    break;
                }
            }
        }
    }

    /**
     * 获取AppInfo目前的包大小
     *
     * @param appInfo
     * @return
     */
    public static long getPatchApkSize(AppInfo appInfo) {
        if (appInfo.isInDownloadDB()) {
            if (appInfo.isDownloadPatch()) {
                return appInfo.getDiffSize();
            } else {
                return appInfo.getApkSize();
            }
        } else {
            if (appInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH) {
                return appInfo.getDiffSize();
            } else {
                return appInfo.getApkSize();
            }
        }
    }


    /**
     * 更新应用的免状态
     *
     * @param context
     * @param ids
     * @param adapter
     */
    public static void updateAppFreeState(final Context context, String ids, final CommonListItemAdapter adapter, String TAG) {
        if (adapter != null) {
            if (AccountUtil.isAgentFree(context)) {
                QueryFreeGameUtils.queryFreeGame(ids, new FreeGameRefrsh() {
                    @Override
                    public void refresh(List<FreeGameItem> freeGameItems) {
                        adapter.refreshAppListItem(freeGameItems);
                    }
                }, TAG);
            } else {
                adapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * 更新应用的免状态
     *
     * @param context
     * @param ids
     */
    public static void updateAppFreeState(final Context context, String ids, String TAG) {
        if (AccountUtil.isAgentFree(context)) {
            QueryFreeGameUtils.queryFreeGame(ids, new FreeGameRefrsh() {
                @Override
                public void refresh(List<FreeGameItem> freeGameItems) {
                    if (freeGameItems != null) {
                        FreeGameItemEvent event = new FreeGameItemEvent();
                        event.setFreeGameItems(freeGameItems);
                        MainThreadBus.getInstance().post(event);
                    }
                }
            }, TAG);
        }

    }


    /**
     * 获取appInfo list
     *
     * @param context
     * @param infos
     * @param mAppInfoList
     */
    public static void addNewDataToList(Context context, List<BaseAppInfo> infos,
                                        List<AppInfo> mAppInfoList) {

        for (BaseAppInfo item : infos) {
            AppInfo appInfo = new AppInfo(item);
            appInfo.setOriginCFlowFree(appInfo.getcFlowFree());
            mAppInfoList.add(appInfo);
        }

        updateDownloadState(context, mAppInfoList);
    }

    /**
     * 获取appInfo list
     *
     * @param context
     * @param infos
     * @param mAppInfoList
     */
    public static void addRankDataToList(Context context, List<RankAppInfo> infos,
                                         List<AppInfo> mAppInfoList) {

        for (RankAppInfo item : infos) {
            AppInfo appInfo = new AppInfo(item);
            appInfo.setiRefId(item.getRefId()); //不为零 则可以下载
            appInfo.setCommonLevel(item.getCommonLevel());//软件评分
            appInfo.setIcon(item.getRssImg());
            appInfo.setAppName(item.getRssName());
            appInfo.setRssId(item.getRssId());
            mAppInfoList.add(appInfo);
        }

        updateDownloadState(context, mAppInfoList);
    }

    /**
     * 根据下载task 更改列表appinfo的数据
     *
     * @param context
     * @param aAppInfoList
     */
    public static synchronized void updateDownloadState(Context context, List<AppInfo> aAppInfoList) {
        int queryFilter = DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_PENDING
                | DownloadManager.STATUS_PENDING_FOR_WIFI
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING
                | DownloadManager.STATUS_PATCHING;

        List<TaskInfo> taskInfoList = DownloadHelper.getDownloadTasks(
                context,
                DownloadHelper.QUERY_TYPE_BY_STATUS,
                queryFilter);

        for (AppInfo appInfo : aAppInfoList) {

            boolean isDownLoad = false;

            if (!ListUtils.isEmpty(taskInfoList)) {
                for (TaskInfo taskInfo : taskInfoList) {
                    // app id不为0 排行中预约应用、皮肤 两个appId 均为0 导致错误显示
                    if (taskInfo.getAppId() == appInfo.getAppId() && appInfo.getAppId() != 0) {
                        appInfo.setAppName(taskInfo.getAppLabel());
                        appInfo.setPkgName(taskInfo.getAppPkgName());
                        appInfo.setIcon(taskInfo.getAppIconUrl());
                        appInfo.setApkDownloadId(taskInfo.getTaskId());
                        long totalSize = taskInfo.getApkTotalSize();
                        appInfo.setDownloadPatch(totalSize < appInfo.getApkSize());
                        appInfo.setApkSize(totalSize < 0 ? appInfo.getApkSize() : taskInfo.getApkTotalSize());
                        appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                        appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                        appInfo.setLocalUri(taskInfo.getApkLocalUri());
                        appInfo.setDownloadState(taskInfo.getDownloadState());
                        appInfo.setVersionCode(taskInfo.getAppVersionCode());
                        appInfo.setcAppType(taskInfo.getAppType());
                        DownloadHelper.calcDownloadSpeed(context, appInfo, taskInfo);
                        appInfo.setInDownloadDB(true);
                        isDownLoad = true;
                        break;
                    }
                }
            }

            if (!isDownLoad) {
                appInfo.setDownloadState(DownloadManager.STATUS_INITIAL);  //如果不在下载列表中重置为初始状态
                appInfo.setDownloadPatch(false);
                appInfo.setInDownloadDB(false);
            }


            PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(context, appInfo.getPkgName());
            if (packageInfo != null) {
                if (appInfo.getDownloadState() == DownloadManager.STATUS_SUCCESSFUL
                        || appInfo.getDownloadState() == DownloadManager.STATUS_INITIAL) {
                    if ((packageInfo.versionCode >= appInfo.getVersionCode())) {
                        appInfo.setDownloadState(DownloadManager.STATUS_EXTRA_INSTALLED);
                    }

                    if (appInfo.getDownloadState() != DownloadManager.STATUS_SUCCESSFUL
                            && packageInfo.versionCode < appInfo.getVersionCode()) {
                        appInfo.setDownloadState(DownloadManager.STATUS_EXTRA_UPGRADABLE);
                    }
                }

            }

            // 若下载地址为空，则该应用为敬请期待
            if (TextUtils.isEmpty(appInfo.getApkUrl()))
                appInfo.setDownloadState(DownloadManager.STATUS_NOTREADY);
        }
    }


    /**
     * 获取应用列表的id串
     *
     * @param appInfoList
     * @return ids
     */
    public static String getAllGameIds(List<AppInfo> appInfoList) {
        if (appInfoList == null || appInfoList.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        int length = appInfoList.size();
        AppInfo appInfo;
        for (int i = 0; i < length; i++) {
            appInfo = appInfoList.get(i);
            if (i == 0)
                stringBuilder.append(appInfo.getAppId());
            else {
                stringBuilder.append(",").append(appInfo.getAppId());
            }
        }

        return stringBuilder.toString();
    }
}
