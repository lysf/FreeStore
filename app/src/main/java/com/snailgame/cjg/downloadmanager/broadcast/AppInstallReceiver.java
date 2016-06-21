package com.snailgame.cjg.downloadmanager.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.j256.ormlite.dao.Dao;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.DeskGame;
import com.snailgame.cjg.common.db.dao.MyGame;
import com.snailgame.cjg.common.db.daoHelper.DeskGameDaoHelper;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.core.DownloadReceiver;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.GameSdkDataUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.NotificationUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shenzaih on 14-2-20.
 * refactor: liuzl
 */
public class AppInstallReceiver extends BroadcastReceiver {
    public static final String TAG = AppInstallReceiver.class.getSimpleName();
    private Dao<MyGame, Integer> myGameDao;
    private AsyncTask queryTask;

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            myGameDao = FreeStoreDataHelper.getHelper(context).getMyGameDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String action = intent.getAction();
        String pkgName = intent.getDataString().split(":")[1];
        boolean isReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);//intent中带有的是否是更新app的字段

        //查询数据库中有下载成功或者正在安装的该应用程序的记录才发送下载统计记录
        TaskInfo taskInfo = DownloadHelper.getTaskInfoByPkgName(context, pkgName);
        if (taskInfo == null) {
            if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                try {
                    List<MyGame> list = myGameDao.queryForEq(MyGame.COL_APK_PKG_NAME, pkgName);
                    if (list != null && list.size() > 0) {
                        myGameDao.delete(list);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            notifyUIUpdate(context);
            return;
        }

        //程序新安装会发ACTION_PACKAGE_ADDED
        //程序卸载 ACTION_PACKAGE_REMOVED
        //程序更新会按照如下三步骤发送消息
        //1.ACTION_PACKAGE_REMOVED
        //2.ACTION_PACKAGE_ADDED
        //3.ACTION_PACKAGE_REPLACED


        //安装
        switch (action) {
            case Intent.ACTION_PACKAGE_ADDED:
                // 更新时不发送安装统计
                if (!isReplace) {
                    StaticsUtils.installSuccess(taskInfo.getAppId());
                    StaticsUtils.serverDownloadStatus(FreeStoreApp.getContext(), String.valueOf(taskInfo.getAppId()),
                            String.valueOf(StaticsUtils.STATICS_STATUS_INSTALL_COMPLETED));
                } else {
                    // 更新的统计在此处发送，是因为需要在这里拿到应用是否是增量更新
                    if (MyGameDaoHelper.isPatchUpdate(context, taskInfo.getAppPkgName()))
                        StaticsUtils.serverDownloadStatus(context, String.valueOf(taskInfo.getAppId()),
                                String.valueOf(StaticsUtils.STATICS_STATUS_UPGRADE_IN_PATCH));
                    else
                        StaticsUtils.serverDownloadStatus(context, String.valueOf(taskInfo.getAppId()),
                                String.valueOf(StaticsUtils.STATICS_STATUS_UPGRADE));

                    StaticsUtils.updateSuccess(taskInfo.getAppId());
                    // 更新时，在此处删除数据
                    try {
                        myGameDao.delete(myGameDao.queryForEq(MyGame.COL_APK_PKG_NAME, pkgName));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }


                try {
                    myGameDao.create(insertDb(taskInfo));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                // 判断该应用类型是否为游戏
                if (taskInfo.getAppType().equals(AppConstants.VALUE_TYPE_GAME)) {
                    DeskGame deskGame = new DeskGame();
                    deskGame.setPackageName(pkgName);
                    try {
                        DeskGameDaoHelper.insertToDb(context, deskGame);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (SharedPreferencesUtil.getInstance().isAutoDelApp()) {
                    deleteApkByPackageName(context, pkgName.trim(), taskInfo);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DownloadHelper.clearDownloadTask(context);
                        }
                    }, 2000);
                }

                //check if the auto install function is available
                if (!ApkInstaller.isAutoInstallAvailable(context)) {
                    buildOpenApkNotification(context, pkgName, taskInfo);
                }
                //卸载
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                // 更新时不发送卸载统计
                if (!isReplace) {
                    StaticsUtils.unInstall(String.valueOf(taskInfo.getAppId()));
                    StaticsUtils.serverDownloadStatus(FreeStoreApp.getContext(), String.valueOf(taskInfo.getAppId()),
                            String.valueOf(StaticsUtils.STATICS_STATUS_UNINSTALL));

                    // 卸载应用时删除数据
                    GameSdkDataUtil.removeDataV1(pkgName);

                    // 更新时，不在此处删除数据
                    try {
                        myGameDao.delete(myGameDao.queryForEq(MyGame.COL_APK_PKG_NAME, pkgName));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    DownloadHelper.clearDownloadTask(context);
                }

                if (taskInfo.getAppType().equals(AppConstants.VALUE_TYPE_GAME)) {
                    try {
                        DeskGameDaoHelper.deleteFromDb(context, pkgName);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(pkgName)) {
                    cancelNotification(context, pkgName);
                }
                //程序替换或更新
                break;
            case Intent.ACTION_PACKAGE_REPLACED:
                break;
        }
        notifyUIUpdate(context);

    }

    private MyGame insertDb(TaskInfo taskInfo) {
        MyGame myGame = new MyGame();
        myGame.setApkId(taskInfo.getAppId());
        myGame.setPackageName(taskInfo.getAppPkgName());
        myGame.setAppSize(String.valueOf(taskInfo.getApkTotalSize()));
        myGame.setApkLabel(taskInfo.getAppLabel());
        myGame.setIconURL(taskInfo.getAppIconUrl());
        myGame.setVersionCode(taskInfo.getAppVersionCode());
        myGame.setVersionName(taskInfo.getAppVersionName());
        myGame.setUpgradeIgnoreVersionCode(AppConstants.INVALID_UPDATE_IGNORE);
        myGame.setUpgradeDesc("");
        myGame.setDownloadIsUpdate(0);
        myGame.setDownloadIsPatch(0);
        myGame.setAppType(taskInfo.getAppType());
        myGame.setDownloadFlowFreeState(taskInfo.getFlowFreeState());
        return myGame;
    }


    /**
     * 如果数据库发生改变那就通知UI更新
     */
    private void notifyUIUpdate(final Context context) {
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(context, new MyGameDaoHelper.MyGameCallback() {
            @Override
            public void Callback(List<AppInfo> appInfos) {
                MainThreadBus.getInstance().post(new MyGameDBChangeEvent(appInfos));
            }
        });
    }

    /**
     * 当手动安装的游戏安装后立即卸载，取消原来的打开游戏通知
     *
     */
    private void cancelNotification(Context context, String pkgName) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pkgName.hashCode());
    }

    public static void deleteApkByPackageName(Context context, String packageName, TaskInfo taskInfo) {
        if (taskInfo == null || TextUtils.isEmpty(taskInfo.getApkLocalUri())) {
            if (TextUtils.isEmpty(packageName))
                return;
            else
                taskInfo = DownloadHelper.getTaskInfoByPkgName(context, packageName);
        }

        if (taskInfo != null && !TextUtils.isEmpty(taskInfo.getApkLocalUri())) {
            try {
                URI path = URI.create(taskInfo.getApkLocalUri());

                File file = new File(path);
                if (file.exists())
                    file.delete();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }
    }

    private void buildOpenApkNotification(Context context, String pkgName, TaskInfo taskInfo) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence apkName = ResUtil.getString(R.string.notification_download_unknow_app);
        PackageManager pm = context.getPackageManager();
        if (pm != null) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(pkgName, 0);
                if (packageInfo != null) {
                    apkName = pm.getApplicationLabel(packageInfo.applicationInfo);
                }
            } catch (PackageManager.NameNotFoundException exception) {
                LogUtils.e(exception.getMessage());
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationUtils.setIcon(context, builder);
        builder.setOngoing(false);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(apkName);
        builder.setAutoCancel(true);
        builder.setTicker(String.format(ResUtil.getString(R.string.notification_download_ticker_installed), apkName));
        builder.setContentText(context.getString(R.string.notification_download_state_installed));
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notify_normal_layout);
        remoteViews.setTextViewText(R.id.title_tv, apkName);
        remoteViews.setTextViewText(R.id.content_tv, context.getString(R.string.notification_download_state_installed));
        DateFormat df = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.time_tv, df.format(new Date()));
        builder.setContent(remoteViews);
        BitmapManager.showImg(taskInfo.getAppIconUrl(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            remoteViews.setImageViewBitmap(R.id.icon, response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        Intent openApkIntent = new Intent(DownloadManager.ACTION_OPEN_APK);
        openApkIntent.setClassName(context.getPackageName(), DownloadReceiver.class.getName());
        openApkIntent.putExtra(DownloadManager.DOWNLOAD_PKG_NAME, pkgName);
        PendingIntent openApkPendingIntent = PendingIntent.getBroadcast(context, pkgName.hashCode(), openApkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(openApkPendingIntent);
        notificationManager.notify((int) taskInfo.getTaskId(), builder.build());
    }

}
