package com.snailgame.cjg.skin;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

import com.snailgame.cjg.common.db.dao.Skin;
import com.snailgame.cjg.common.db.daoHelper.SkinlDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.SkinDownloadedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.skin.model.SkinPackage;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.plug.PluginPackage;
import com.snailgame.cjg.util.skin.SkinManager;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台服务下载皮肤
 * Created by pancl on 2015/04/10.
 */
public class SkinDownloadServices extends Service {

    private DownloadingTaskListTask mDownloadTask;
    private String apkPkgName;
    private int apkVersionCode;

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(true);
        if (taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                if (taskInfo.getAppPkgName().equals(apkPkgName)
                        && taskInfo.getAppVersionCode() == apkVersionCode) {
                    changeState(taskInfo);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onSkinDownloaded(SkinDownloadedEvent event) {
        PluginPackage pluginPackage = SkinManager.getInstance().loadSkinPackage(event.getLocalPath());
        if (pluginPackage != null)
            saveSkinInfotoDB(event.getAppPkgName(), pluginPackage.versionCode, event.getLocalPath());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null && intent.getExtras().get(AppConstants.KEY_SKIN_INFO) != null) {
            SkinPackage skinPackage = (SkinPackage) intent.getExtras().get(AppConstants.KEY_SKIN_INFO);
            if (skinPackage != null) {
                apkPkgName = skinPackage.getsPkgName();
                apkVersionCode = Integer.parseInt(skinPackage.getiVersionCode());
                //封装为appInfo
                AppInfo appInfo = new AppInfo();
                appInfo.setApkUrl(skinPackage.getcApkUrl());
                appInfo.setAppName(apkPkgName + skinPackage.getiVersionCode());
                appInfo.setPkgName(apkPkgName);
                appInfo.setVersionCode(Integer.parseInt(skinPackage.getiVersionCode()));
                appInfo.setVersionName(skinPackage.getcVersionName());
                appInfo.setMd5(skinPackage.getcMd5Code());
                appInfo.setApkSize(Integer.parseInt(skinPackage.getcSize()));
                appInfo.setcAppType(AppConstants.VALUE_TYPE_SKIN);

                //查询是否已经下载
                queryTask(appInfo);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 根据状态 处理
     *
     * @param taskInfo
     */
    private void changeState(TaskInfo taskInfo) {
        switch (taskInfo.getDownloadState()) {
            case DownloadManager.STATUS_SUCCESSFUL:        // 下载完成
                String skinLocalPath = Uri.parse(taskInfo.getApkLocalUri()).getPath();
                onSkinDownloaded(new SkinDownloadedEvent(skinLocalPath, taskInfo.getAppPkgName()));
                break;
            case DownloadManager.STATUS_FAILED:    // 下载失败 则删除任务等待下一次
                DownloadManager manager = DownloadHelper.getDownloadManager(this);
                if (manager.remove(taskInfo.getTaskId()) == 1) {
                    // delete apk
                    PackageInfoUtil.deleteApkFromDiskByUri(taskInfo.getApkLocalUri());
                }
                break;
            case DownloadManager.STATUS_PAUSED:     // 下载暂停
                DownloadHelper.resumeDownload(this, taskInfo.getTaskId());
                break;

            default:
                break;
        }
    }

    /**
     * 皮肤信息存数据库 {@link Skin}
     *
     * @param appPkgName
     * @param versionCode
     * @param skinLocalPath
     */
    private void saveSkinInfotoDB(String appPkgName, int versionCode, String skinLocalPath) {
        List<Skin> skinList = SkinlDaoHelper.query(this, appPkgName);
        if (skinList != null && skinList.size() > 0) {
            for (Skin skin : skinList) {
                if (Integer.parseInt(skin.getApkVersionCode()) != versionCode) {
                    //系统配置皮肤与当前皮肤版本不一致，即为最新下载，需删除原皮肤，且更新数据库
                    File skinLocalFile = new File(skin.getSkinLocalPath());
                    if (skinLocalFile.exists()) {
                        LogUtils.i("PKG: " + appPkgName + ", VCODE: " + versionCode +
                                " record not exist, remove " + skin.getSkinLocalPath() + " and update db");
                        skinLocalFile.delete();
                    }
                }
            }
            SkinlDaoHelper.update(this, SkinManager.getInstance().getSkinPackage(), skinLocalPath);
        } else {
            SkinlDaoHelper.insert(this, SkinManager.getInstance().getSkinPackage(), skinLocalPath);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadTask != null)
            mDownloadTask.cancel(true);
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 查询是否已经下载
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
                    SkinDownloadServices.this,
                    DownloadHelper.QUERY_TYPE_BY_STATUS,
                    queryFilter);

            if (ListUtils.isEmpty(taskInfoList)) {
                return false;
            }

            for (TaskInfo taskInfo : taskInfoList) {
                if (taskInfo.getAppPkgName().equals(apkPkgName)
                        && taskInfo.getAppVersionCode() == apkVersionCode) {
                    mTaskInfo = taskInfo;
                    return true;
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
                DownloadHelper.startDownload(SkinDownloadServices.this, appInfo);
            }
        }
    }
}
