package com.snailgame.cjg.download;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.download.model.FreeDownloadModel;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadRemoveEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 10/12/13.
 */
public class DownloadHelper {
    static String TAG = DownloadHelper.class.getName();

    public static final String DEFAULT_DOWNLOAD_SPEED = "0KB";
    private static DownloadManager downloadManager = null;
    private static LongSparseArray<Long[]> downloadSpeedMap = new LongSparseArray();
    //    private static Map<Long, Long[]> downloadSpeedMap = new HashMap<Long, Long[]>();
    public static final int QUERY_TYPE_BY_STATUS = 1;
    public static final int QUERY_TYPE_BY_ID = 2;

    public static DownloadManager getDownloadManager(Context context) {
        synchronized (DownloadHelper.class) {
            if (downloadManager == null) {
                downloadManager = new DownloadManager(context.getContentResolver());
            }
            return downloadManager;
        }
    }

    /**
     * 开始一个下载任务
     * 默认不需要知道是否通过检查，如需要，可调用同名方法，实现最后一个参数接口IDownloadCheck
     *
     * @param context
     * @param appInfo
     */
    public synchronized static void startDownload(final Context context, final AppInfo appInfo) {
        startDownload(context, appInfo, new IDownloadCheck() {
            @Override
            public void onPostResult(boolean result) {

            }
        });
    }

    /**
     * 开始一个下载任务
     *
     * @param context
     * @param appInfo        一个应用实例，该应用实例应该至少包含以下属性：
     *                       1. App url 应用下载链接（不能为空）
     *                       2. App id 应用Id（整形，唯一，不能为空）
     *                       3. App label 应用名称（不能为空）
     *                       4. App package name 应用包名（不能为空）
     *                       5. App icon url 应用图标（不能为空）
     *                       6. App size 应用大小（长整型，不能为空，供网络无法获取下载包大小时，显示该值）
     *                       7. App version name 应用版本名称（长整型，不能为空）
     *                       8. App version code 应用版本号码（长整型，不能为空）
     *                       9. App MD5 应用MD5（字符串，不能为空）
     * @param iDownloadCheck 下载检查的接口，如果需要知道是否通过检查，则实现该接口，反之实现没有该参数的同名方法
     * @return true 向下载表中插入数据成功，下载会在未来开始 false向下载表中插入数据失败。
     */
    public synchronized static void startDownload(final Context context, final AppInfo appInfo, final IDownloadCheck iDownloadCheck) {
        if (appInfo == null)
            return;

        if (IdentityHelper.isLogined(context)
                && AppConstants.account_type.equals(AppConstants.AGENT_TYPE)
                && (AccountUtil.isFree() || AccountUtil.isFreeAreaFree())) {
            downloadForReacquireUrl(context, appInfo, iDownloadCheck);
        } else {
            iDownloadCheck.onPostResult(download(context, appInfo.getApkUrl(), appInfo));
        }
    }

    /**
     * 针对广州联通需要再次请求真实的下载地址
     *
     * @param context
     * @param appInfo
     * @param iDownloadCheck
     */
    private static void downloadForReacquireUrl(final Context context, final AppInfo appInfo, final IDownloadCheck iDownloadCheck) {
        String url = JsonUrl.getJsonUrl().JSON_URL_GET_FREE + "?iAppId=" + appInfo.getAppId();
        FSRequestHelper.newGetRequest(url, TAG, FreeDownloadModel.class, new IFDResponse<FreeDownloadModel>() {
            @Override
            public void onSuccess(FreeDownloadModel result) {
                if (result != null && result.getItem() != null) {
                    if (!TextUtils.isEmpty(result.getItem().getcMd5()))
                        appInfo.setMd5(result.getItem().getcMd5());

                    final String downloadUrl = result.getItem().getcDownloadUrl();
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        appInfo.setApkUrl(downloadUrl);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //主线程执行
                                iDownloadCheck.onPostResult(download(context, downloadUrl, appInfo));
                            }
                        });
                    }
                } else {
                    // TODO
                }

            }

            @Override
            public void onNetWorkError() {
                //TODO
            }

            @Override
            public void onServerError() {
                //TODO
            }
        }, false);
    }

    /**
     * 下载检查的接口
     * 由于下载分异步线程执行，则当需要知道本次下载是否通过检查时，实现该接口
     */
    public interface IDownloadCheck {
        /**
         * 当返回检查结果时
         *
         * @param result 是否通过检查
         */
        void onPostResult(boolean result);
    }

    /**
     * 检测app是否已经存在与下载表
     *
     * @param context
     * @param downloadUrl
     * @return
     */
    private static boolean isApkExists(Context context, String downloadUrl) {
        DownloadManager.Query query = getDownloadQuery();
        query.setFilterByUrl(downloadUrl);
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return false;
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    /**
     * 创建下载任务
     * 先检测下载条件，然后创建下载任务请求并加入DownloadManager的下载队列
     *
     * @param context
     * @param downloadUrl
     * @param appInfo
     * @return
     */
    private static boolean download(Context context, String downloadUrl, AppInfo appInfo) {
        if ((TextUtils.isEmpty(downloadUrl) || "null".equals(downloadUrl))) {
            ToastUtils.showMsg(context, R.string.error_msg_download_invalid_url, appInfo.getAppName());
            return false;
        }

        if (isApkExists(context, downloadUrl)) {
            if (!TextUtils.isEmpty(appInfo.getPkgName()) && !appInfo.getPkgName().equals(AppConstants.APP_PACKAGE_NAME)
                    && !TextUtils.equals(appInfo.getcAppType(), AppConstants.VALUE_TYPE_SKIN))    // 下载免商店自身及皮肤不提示
                ToastUtils.showMsg(context, R.string.error_msg_download_already_exists, appInfo.getAppName());
            return false;
        }

        // 免流量状态设置默认值为不免
        if (appInfo.getcFlowFree() == null) {
            appInfo.setcFlowFree(AppInfo.FREE_NULL);
        }

        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // 判断只有WIFI环境才可以下载应用条件
        if (SharedPreferencesUtil.getInstance().isWifiOnly()) {
            if (NetworkUtils.isWifiEnabled(context)) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            } else if (appInfo.getDownloadState() == Downloads.STATUS_PENDING_FOR_WIFI) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            } else {
                ToastUtils.showMsg(context, R.string.error_msg_download_wifi_only);
                return false;
            }
        }

        // create a new download dir to replace the default download dir
        File downloadDir = DownloadManager.createDownloadDirIfNotExist();
        if (downloadDir == null) {
            ToastUtils.showMsg(context, context.getString(R.string.download_dir_not_found));
            return false;
        }

        request.setDestinationUri(
                Uri.withAppendedPath(Uri.fromFile(downloadDir), appInfo.getPkgName() + "-" + +System.currentTimeMillis() + ".apk"))
                .setAppId(appInfo.getAppId())
                .setAppLabel(appInfo.getAppName())
                .setAppPackage(appInfo.getPkgName())
                .setTitle(appInfo.getAppName())
                .setAppIconUrl(appInfo.getIcon())
                .setAppType(appInfo.getcAppType())
                .setPatchType(appInfo.getIsPatch())
                .setDiffUrl(appInfo.getDiffUrl())
                .setDiffSize(appInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH ? appInfo.getApkSize() : appInfo.getDiffSize())
                .setDiffMd5(appInfo.getDiffMd5())
                .setDefaultTotalBytes(appInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH ? appInfo.getDiffSize() : appInfo.getApkSize())
                .setAppVersionName(appInfo.getVersionName())
                .setAppVersionCode(appInfo.getVersionCode())
                .setMD5(appInfo.getMd5())
                .setFlowFreeState(appInfo.getcFlowFree())
                .setFreeAreaState(appInfo.getiFreeArea())
                .setState(appInfo.getDownloadState());

        long id = getTaskIdByAppId(context, appInfo.getAppId());
        // insert a new download task if the app id not exists in db, upgrade
        // otherwise
        if (id == 0L) {
            id = getDownloadManager(context).enqueue(request);
            StaticsUtils.startDownload(context, appInfo.getAppId());
        } else {
            getDownloadManager(context).upgrade(id, request);
        }
        if (id > 0L) {
            appInfo.setApkDownloadId(id);
        }
        return true;
    }

    /**
     * Manually pause the download task
     *
     * @param context
     * @param ids
     * @return
     */
    public synchronized static boolean pauseDownload(Context context, long... ids) {
        try {
            getDownloadManager(context).pauseDownload(ids);
        } catch (IllegalArgumentException ex) {
            LogUtils.e(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 检测是否只在Wifi模式下进行下载，如果是且Wifi未开，则弹出提示并返回false，否则true
     *
     * @param context
     * @return
     */
    private static boolean checkWifiOnly(Context context) {
        if (SharedPreferencesUtil.getInstance().isWifiOnly() && !NetworkUtils.isWifiEnabled(context)) {
            ToastUtils.showMsg(context,
                    ResUtil.getString(R.string.error_msg_download_wifi_only));
            return false;
        }
        return true;
    }

    /**
     * 继续下载
     *
     * @param context
     * @param ids
     * @return
     */
    public synchronized static boolean resumeDownload(Context context, long... ids) {
        if (checkWifiOnly(context)) {
            try {
                getDownloadManager(context).resumeDownload(ids);
                return true;
            } catch (IllegalArgumentException ex) {
                LogUtils.e(ex.getMessage());
            }
        }
        return false;
    }

    /**
     * 对下载完成的任务或者下载失败的任务重新下载
     *
     * @param context
     * @param ids
     * @return
     */
    public synchronized static boolean restartDownload(Context context, long... ids) {
        if (checkWifiOnly(context)) {
            try {
                getDownloadManager(context).restartDownload(ids);
                return true;
            } catch (IllegalArgumentException ex) {
                LogUtils.e(ex.getMessage());
            }
        }
        return false;
    }

    /**
     * 保存下载任务的安装状态
     *
     * @param context
     * @param status
     * @param ids
     */
    public synchronized static void installDownload(Context context, int status, long... ids) {
        getDownloadManager(context).installOperation(status, ids);
    }

    /**
     * 取消相应的下载任务
     *
     * @param context
     * @param ids
     * @return
     */
    public synchronized static int cancelDownload(Context context, long... ids) {
        try {
            return getDownloadManager(context).remove(ids);
        } catch (IllegalArgumentException ex) {
            LogUtils.e(ex.getMessage());
            return 0;
        }
    }

    /**
     * 清除下载任务信息
     *
     * @param context
     */
    public static void clearDownloadTask(Context context) {
        int queryFilter = DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_PENDING
                | DownloadManager.STATUS_PENDING_FOR_WIFI
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING
                | DownloadManager.STATUS_PATCHING;

        DownloadManager.Query query = new DownloadManager.Query().setFilterByStatus(queryFilter);
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return;

        int colDownloadId = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
        int colDownloadSize = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        while (cursor.moveToNext()) {
            long downloadId = cursor.getLong(colDownloadId);
            long downloadSize = cursor.getLong(colDownloadSize);
            try {
                getDownloadManager(context).openDownloadedFile(downloadId);
            } catch (FileNotFoundException e) {
                // 尚未开始下载的任务，不会被清理
                if (downloadSize > 0) {
                    getDownloadManager(context).remove(downloadId);

                    MainThreadBus.getInstance().post(new DownloadRemoveEvent(downloadId));
                }
            }
        }

        if (!cursor.isClosed())
            cursor.close();
    }

    /**
     * 根据查询条件，获取相应的下载任务信息
     */
    public static List<TaskInfo> getDownloadTasks(Context context, int queryType, int queryFilter) {
        List<TaskInfo> mDownloadTasks = new ArrayList<>();

        DownloadManager.Query query;
        switch (queryType) {
            case QUERY_TYPE_BY_ID:
                query = new DownloadManager.Query().setFilterById(queryFilter);
                break;
            case QUERY_TYPE_BY_STATUS:
                query = new DownloadManager.Query().setFilterByStatus(queryFilter);
                break;
            default:
                LogUtils.e("Invalid query type, Type=" + queryType);
                return mDownloadTasks;
        }

        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return mDownloadTasks;

        int colDownloadId = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
        int colDesc = cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
        int colUri = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
        int colTotalSize = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        int colLocalUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        int colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int colReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int colTotalDownloaded = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        int colDownloadHint = cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_HINT);
        int colTotalSizeDefault = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES_DEFAULT);
        int colAppId = cursor.getColumnIndex(DownloadManager.COLUMN_APP_ID);
        int colAppLabel = cursor.getColumnIndex(DownloadManager.COLUMN_APP_LABEL);
        int colAppPkgName = cursor.getColumnIndex(DownloadManager.COLUMN_APP_PKG_NAME);
        int colAppIconUrl = cursor.getColumnIndex(DownloadManager.COLUMN_APP_ICON_URL);
        int colAppVersionName = cursor.getColumnIndex(DownloadManager.COLUMN_APP_VERSION_NAME);
        int colAppVersionCode = cursor.getColumnIndex(DownloadManager.COLUMN_APP_VERSION_CODE);
        int colBytesInWifi = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_IN_WIFI);
        int colBytesIn3G = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_IN_3G);
        int colFlowFreeState = cursor.getColumnIndex(DownloadManager.COLUMN_FLOW_FREE_STATE_V2);
        int colAppType = cursor.getColumnIndex(DownloadManager.COLUMN_APP_TYPE);

        while (cursor.moveToNext()) {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setTaskId(cursor.getLong(colDownloadId));
            taskInfo.setDownloadState(cursor.getInt(colStatus));
            taskInfo.setAppPkgName(cursor.getString(colAppPkgName));

            if (taskInfo.getDownloadState() == DownloadManager.STATUS_SUCCESSFUL) {
                try {
                    getDownloadManager(context).openDownloadedFile(taskInfo.getTaskId());
                } catch (FileNotFoundException e) {
                    PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(context, taskInfo.getAppPkgName());
                    if (packageInfo == null) {
                        getDownloadManager(context).remove(taskInfo.getTaskId());
                        continue;
                    }
                }
            }

            taskInfo.setDownloadedSize(cursor.getLong(colTotalDownloaded));

            long totalSize = cursor.getLong(colTotalSize);
            long totalSizeDefault = cursor.getLong(colTotalSizeDefault);

            taskInfo.setApkTotalSize(totalSize != -1 ? totalSize : totalSizeDefault);
            if (taskInfo.getApkTotalSize() != 0)
                taskInfo.setTaskPercent((double)(taskInfo.getDownloadedSize() * 100) / taskInfo.getApkTotalSize());
            taskInfo.setDesc(cursor.getString(colDesc));
            taskInfo.setApkUri(cursor.getString(colUri));
            taskInfo.setApkLocalUri(cursor.getString(colLocalUri));
            taskInfo.setReason(cursor.getInt(colReason));
            taskInfo.setAppId(cursor.getInt(colAppId));
            taskInfo.setAppLabel(cursor.getString(colAppLabel));
            taskInfo.setDownloadHint(cursor.getString(colDownloadHint));
            taskInfo.setAppIconUrl(cursor.getString(colAppIconUrl));
            taskInfo.setAppVersionName(cursor.getString(colAppVersionName));
            taskInfo.setAppVersionCode(cursor.getInt(colAppVersionCode));
            taskInfo.setBytesInWifi(cursor.getInt(colBytesInWifi));
            taskInfo.setBytesIn3G(cursor.getInt(colBytesIn3G));
            taskInfo.setFlowFreeState(cursor.getString(colFlowFreeState));
            taskInfo.setAppType(cursor.getString(colAppType));
            mDownloadTasks.add(taskInfo);
        }
        if (!cursor.isClosed())
            cursor.close();
        return mDownloadTasks;
    }

    /**
     * 获取下载DownloadId数组
     *
     * @param context
     * @param query
     * @return
     */
    public static long[] getDownloadIdArr(Context context, DownloadManager.Query query) {
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return null;

        long[] idArr = new long[cursor.getCount()];
        int colDownloadId = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
        int colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

        while (cursor.moveToNext()) {
            long downloadId = cursor.getLong(colDownloadId);
            int status = cursor.getInt(colStatus);
            // delete the download task if the downloaded file is not exist
            // (only when the status of the download task is successful)
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                try {
                    getDownloadManager(context).openDownloadedFile(downloadId);
                } catch (FileNotFoundException e) {
                    LogUtils.e(e.getMessage());
                    getDownloadManager(context).remove(downloadId);
                    continue; // ignore this task, and go to next
                }
            }
            idArr[cursor.getPosition()] = downloadId;
        }
        if (!cursor.isClosed())
            cursor.close();
        return idArr;
    }

    /**
     * 获取提供3G方式下载的字节数的数组
     *
     * @param context
     * @param query
     * @return
     */
    public static int[] getDownload3gFlowArr(Context context, DownloadManager.Query query) {
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return null;

        int[] volume3gArr = new int[cursor.getCount()];
        int col3G = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_IN_3G);
        int colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

        while (cursor.moveToNext()) {
            int volume3g;
            int status = cursor.getInt(colStatus);

            switch (status) {
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_PENDING_FOR_WIFI:
                case DownloadManager.STATUS_RUNNING:
                case DownloadManager.STATUS_PAUSED:
                    volume3g = cursor.getInt(col3G);
                    break;
                default:
                    volume3g = 0;
                    break;
            }
            volume3gArr[cursor.getPosition()] = volume3g;
        }

        if (!cursor.isClosed())
            cursor.close();

        return volume3gArr;
    }

    /**
     * 根据下载任务的status获取当前该状态的任务个数
     *
     * @param context
     * @param status  任务的状态
     * @return
     */
    public static int getApkCountByStatus(Context context, int status) {
        int count = 0;
        DownloadManager.Query downloadQuery = new DownloadManager.Query();
        downloadQuery.setFilterByStatus(status);
        DownloadManager mDownloadManager = DownloadHelper
                .getDownloadManager(context);
        if (mDownloadManager == null)
            return count;

        Cursor cursor = mDownloadManager.query(downloadQuery);
        if (cursor == null || cursor.isClosed())
            return count;

        count = cursor.getCount();
        if (count > 0) {
            int pkgNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_APP_PKG_NAME);
            while (cursor.moveToNext()) {
                // 判断商店自身更新
                String pkgName = cursor.getString(pkgNameIndex);
                if (pkgName.equals(AppConstants.APP_PACKAGE_NAME)) {
                    count--;
                    break;
                }
            }
        }
        cursor.close();
        return count;
    }

    /**
     * calculate the download speed of a specified task
     *
     * @param context
     * @param appInfo
     * @param runningTask
     */
    public static void calcDownloadSpeed(Context context, AppInfo appInfo, TaskInfo runningTask) {
        int downloadState = runningTask.getDownloadState();
        if (downloadState == DownloadManager.STATUS_RUNNING) {
            if (downloadSpeedMap.get(runningTask.getTaskId()) == null) {
                Long[] speedArr = new Long[3];
                speedArr[0] = runningTask.getDownloadedSize();
                speedArr[1] = System.currentTimeMillis();
                speedArr[2] = 0l;
                downloadSpeedMap.put(runningTask.getTaskId(), speedArr);
                appInfo.setDownloadSpeed(DEFAULT_DOWNLOAD_SPEED);
            } else {
                long oldDownloadedSize = downloadSpeedMap.get(runningTask.getTaskId())[0];
                long newDownloadedSize = runningTask.getDownloadedSize();
                long oldUpdateTime = downloadSpeedMap.get(runningTask.getTaskId())[1];
                long newUpdateTime = System.currentTimeMillis();
                long elapsedTime = newUpdateTime - oldUpdateTime > 0 ? (newUpdateTime - oldUpdateTime) : 1;
                long speed = 0;
                if (newDownloadedSize > oldDownloadedSize) {
                    speed = (long) (((newDownloadedSize - oldDownloadedSize) * 1000 / (elapsedTime)) * 0.98);
                    appInfo.setDownloadSpeed(FileUtil.formatShortFileSize(context, speed)); // 0.98 = 1000/1024
                } else {
                    if (elapsedTime > 1000) {
                        appInfo.setDownloadSpeed(DEFAULT_DOWNLOAD_SPEED);
                    } else {
                        speed = downloadSpeedMap.get(runningTask.getTaskId())[2];
                        appInfo.setDownloadSpeed(FileUtil.formatShortFileSize(context, speed));
                    }
                }
                downloadSpeedMap.get(runningTask.getTaskId())[0] = newDownloadedSize;
                downloadSpeedMap.get(runningTask.getTaskId())[1] = newUpdateTime;
                downloadSpeedMap.get(runningTask.getTaskId())[2] = speed;
            }
        } else {
            appInfo.setDownloadSpeed(DEFAULT_DOWNLOAD_SPEED);
            if (downloadState == DownloadManager.STATUS_SUCCESSFUL) {
                if (downloadSpeedMap.get(runningTask.getTaskId()) != null) {
                    downloadSpeedMap.remove(runningTask.getTaskId());
                }
            }
        }
    }

    /**
     * 处理暂停或者失败后的提示信息
     *
     * @param context
     * @param label   下载应用名称
     * @param state   下载的任务状态
     * @param reason  暂停或者是失败的原因
     */
    public static void handleMsgForPauseOrError(Context context, String label,
                                                int state, int reason) {
        int tipId = -1;
        switch (state) {
            case DownloadManager.STATUS_PAUSED:
                switch (reason) {
                    // 暂停，等待继续
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        tipId = R.string.pause_msg_download_waiting_for_retry;
                        break;
                    // 等待可用网络
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        tipId = R.string.error_msg_download_waiting_for_network;
                        break;
                    // 等待可用WIFI
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        tipId = R.string.error_msg_download_queued_for_wifi;
                        break;
                }
                break;
            case DownloadManager.STATUS_FAILED:
                switch (reason) {
                    // 未发现存储卡
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        tipId = R.string.error_msg_download_device_not_found;
                        break;
                    // 文件已存在
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        tipId = R.string.error_msg_download_file_exists;
                        break;
                    // 存储卡已满
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        tipId = R.string.error_msg_download_insufficient_space;
                        break;
                    // 未处理的HTTP错误
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        tipId = R.string.error_msg_download_unknown_http_code;
                        break;
                    // 下载文件有异常
                    case DownloadManager.ERROR_FILE_MD5_NOT_MATCH:
                        tipId = R.string.error_msg_download_md5_not_match;
                        break;
                    // 更新差分文件有异常
                    case DownloadManager.ERROR_PATCHING_FILE_ERROR:
                        tipId = R.string.error_msg_download_patching_file_error;
                        break;
                    // 未知错误
                    case DownloadManager.ERROR_FILE_ERROR:
                        tipId = R.string.error_msg_download_file_not_found;
                        break;
                    default:
                        tipId = R.string.error_msg_download_unknown_error;
                        break;
                }
                break;
        }
        if (tipId != -1)
            ToastUtils.showMsg(context, String.format(context.getString(tipId), label));
    }

    /**
     * 通过PkgName获取下载的信息
     *
     * @param context
     * @param pkgName 应用包名
     * @return
     */
    public static TaskInfo getTaskInfoByPkgName(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            LogUtils.w("Package is null or empty");
            return null;
        }

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING | DownloadManager.STATUS_PATCHING);
        query.setFilterByPkgName(pkgName);
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return null;

        int colTotalSize = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        int colStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int colAppId = cursor.getColumnIndex(DownloadManager.COLUMN_APP_ID);
        int colAppLabel = cursor.getColumnIndex(DownloadManager.COLUMN_APP_LABEL);
        int colAppPkgName = cursor.getColumnIndex(DownloadManager.COLUMN_APP_PKG_NAME);
        int colAppIconUrl = cursor.getColumnIndex(DownloadManager.COLUMN_APP_ICON_URL);
        int colAppVersionName = cursor.getColumnIndex(DownloadManager.COLUMN_APP_VERSION_NAME);
        int colAppVersionCode = cursor.getColumnIndex(DownloadManager.COLUMN_APP_VERSION_CODE);
        int colAppFreeFlow = cursor.getColumnIndex(DownloadManager.COLUMN_FLOW_FREE_STATE_V2);
        int colMd5 = cursor.getColumnIndex(DownloadManager.COLUMN_MD5);
        int colLocalUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        int colAppType = cursor.getColumnIndex(DownloadManager.COLUMN_APP_TYPE);
        int taskId = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
        int colDownloadHint = cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_HINT);
        TaskInfo taskInfo = null;
        if (cursor.moveToNext()) {
            taskInfo = new TaskInfo();
            taskInfo.setApkTotalSize(cursor.getLong(colTotalSize));
            taskInfo.setDownloadState(cursor.getInt(colStatus));
            taskInfo.setAppId(cursor.getInt(colAppId));
            taskInfo.setAppLabel(cursor.getString(colAppLabel));
            taskInfo.setAppPkgName(cursor.getString(colAppPkgName));
            taskInfo.setAppIconUrl(cursor.getString(colAppIconUrl));
            taskInfo.setAppVersionName(cursor.getString(colAppVersionName));
            taskInfo.setAppVersionCode(cursor.getInt(colAppVersionCode));
            taskInfo.setFlowFreeState(cursor.getString(colAppFreeFlow));
            taskInfo.setMd5(cursor.getString(colMd5));
            taskInfo.setApkLocalUri(cursor.getString(colLocalUri));
            taskInfo.setAppType(cursor.getString(colAppType));
            taskInfo.setTaskId(cursor.getLong(taskId));
            taskInfo.setDownloadHint(cursor.getString(colDownloadHint));
        }

        if (!cursor.isClosed())
            cursor.close();
        return taskInfo;
    }

    /**
     * 通过appid查询下载表中是否有该下载信息
     *
     * @param context
     * @param apkId
     * @return 返回0L表示该app未在下载表中下载
     */
    public static long getTaskIdByAppId(Context context, int apkId) {
        if (apkId <= 0) {
            LogUtils.w("apkId is invalid.");
            return 0L;
        }

        DownloadManager.Query query = getDownloadQuery();
        query.setFilterByAppId(apkId);
        Cursor cursor = getDownloadManager(context).query(query);
        if (cursor == null || cursor.isClosed())
            return 0L;

        try {
            int idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
            if (cursor.moveToNext()) {
                return cursor.getInt(idIndex);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        } finally {
            cursor.close();
        }
        return 0L;
    }

    /**
     * 查询download表中的记录，
     * 下载成功，下载等待，下载运行，下载暂停，下载失败，安装成功六种状态
     * 均为下载表中的正确状态
     *
     * @return
     */
    private static DownloadManager.Query getDownloadQuery() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(
                DownloadManager.STATUS_SUCCESSFUL
                        | DownloadManager.STATUS_PENDING
                        | DownloadManager.STATUS_PENDING_FOR_WIFI
                        | DownloadManager.STATUS_RUNNING
                        | DownloadManager.STATUS_PAUSED
                        | DownloadManager.STATUS_FAILED
                        | DownloadManager.STATUS_INSTALLING
                        | DownloadManager.STATUS_PATCHING
        );
        return query;
    }
}
