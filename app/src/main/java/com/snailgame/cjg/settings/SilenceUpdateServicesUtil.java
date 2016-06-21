package com.snailgame.cjg.settings;

import android.content.Context;
import android.os.AsyncTask;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.downloadmanager.broadcast.AppInstallReceiver;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UpdateMySelfEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * 在非强制更新下，如果是 wifi，启动一个service 静默下载安装包后 提示更新
 * Created by TAJ_C on 2014/12/15.
 */
public class SilenceUpdateServicesUtil {

    private UpdateModel.ModelItem mUpdateModelItem;
    private DownloadingTaskListTask mDownloadTask;

    private Context mContext;
    private static SilenceUpdateServicesUtil instance;

    public static SilenceUpdateServicesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SilenceUpdateServicesUtil(context);
        }
        return instance;
    }

    public SilenceUpdateServicesUtil(Context context) {
        this.mContext = context;
        init();
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                if (taskInfo.getAppPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                    changeState(taskInfo);
                    break;
                }
            }
        }
    }

    public void init() {
        MainThreadBus.getInstance().register(this);
    }

    public void start(UpdateModel.ModelItem modelItem) {
//        if (intent != null && intent.hasExtra() != null && intent.getExtras().get(AppConstants.KEY_SNAILSTORE_UPDATE_INFO) != null) {
        mUpdateModelItem = modelItem;
        if (mUpdateModelItem != null) {
            //封装为appInfo
            AppInfo appInfo = new AppInfo();
            appInfo.setApkUrl(mUpdateModelItem.getcApkUrl());
            appInfo.setAppName(mUpdateModelItem.getsName());
            appInfo.setPkgName(mContext.getPackageName());
            appInfo.setIcon(mUpdateModelItem.getcIcon());
            appInfo.setVersionCode(Integer.parseInt(mUpdateModelItem.getnVersionCode()));
            appInfo.setAppId(Integer.parseInt(mUpdateModelItem.getnAppId()));
            appInfo.setVersionName(mUpdateModelItem.getcVersion());
            appInfo.setMd5(mUpdateModelItem.getcMd5Code());
            appInfo.setApkSize(Integer.parseInt(mUpdateModelItem.getcSize()));
            appInfo.setIsUpdate(1);
            appInfo.setIsPatch(0);

            //查询是否已经下载 是-->弹出dialog,否-->下载
            queryTask(appInfo);
        }
//        }
    }




    /**
     * 根据状态 处理
     *
     * @param taskInfo
     */
    private void changeState(TaskInfo taskInfo) {
        switch (taskInfo.getDownloadState()) {
            case DownloadManager.STATUS_SUCCESSFUL:        // 下载完成
                MainThreadBus.getInstance().post(new UpdateMySelfEvent(mUpdateModelItem, taskInfo.getApkLocalUri()));
                break;

            case DownloadManager.STATUS_FAILED:    // 下载失败 则删除任务等待下一次
                DownloadManager manager = DownloadHelper.getDownloadManager(mContext);
                if (manager.remove(taskInfo.getTaskId()) == 1) {
                    // delete apk
                    PackageInfoUtil.deleteApkFromDiskByUri(taskInfo.getApkLocalUri());
                }
                break;
            case DownloadManager.STATUS_PAUSED:     // 下载暂停
                DownloadHelper.resumeDownload(mContext, taskInfo.getTaskId());
                break;

            default:
                break;
        }
    }


    public void onDestroy() {
        if (mDownloadTask != null)
            mDownloadTask.cancel(true);

        MainThreadBus.getInstance().unregister(this);
        instance = null;
    }

    /**
     * 查询是否已经下载 是-->弹出dialog,否-->下载
     *
     * @param appInfo
     */
    private void queryTask(AppInfo appInfo) {
        mDownloadTask = new DownloadingTaskListTask(appInfo);
        mDownloadTask.execute();
    }

    class DownloadingTaskListTask extends AsyncTask<Void, Void, Boolean> {
        List<TaskInfo> taskInfoList;
        TaskInfo mTaskInfo;
        AppInfo appInfo;

        public DownloadingTaskListTask(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int queryFilter = DownloadManager.STATUS_RUNNING
                    | DownloadManager.STATUS_FAILED
                    | DownloadManager.STATUS_PAUSED
                    | DownloadManager.STATUS_PENDING
                    | DownloadManager.STATUS_PENDING_FOR_WIFI
                    | DownloadManager.STATUS_SUCCESSFUL
                    | DownloadManager.STATUS_INSTALLING
                    | DownloadManager.STATUS_PATCHING;

            taskInfoList = DownloadHelper.getDownloadTasks(
                    mContext,
                    DownloadHelper.QUERY_TYPE_BY_STATUS,
                    queryFilter);

            if (ListUtils.isEmpty(taskInfoList)) {
                return false;
            }

            for (TaskInfo taskInfo : taskInfoList) {
                if (taskInfo.getAppPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                    if (taskInfo.getAppVersionCode() == appInfo.getVersionCode()) {
                        mTaskInfo = taskInfo;
                        return true;
                    } else {
                        AppInstallReceiver.deleteApkByPackageName(mContext, AppConstants.APP_PACKAGE_NAME, taskInfo);
                        DownloadHelper.clearDownloadTask(mContext);
                    }
                } else {
                    continue;
                }
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                changeState(mTaskInfo);
            } else {
                //如果之前没有下载 则下载
                DownloadHelper.startDownload(mContext, appInfo);
            }
        }
    }
}
