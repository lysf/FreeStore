package com.snailgame.cjg.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;

import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.DownloadService;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.download.core.RealSystemFacade;
import com.snailgame.cjg.download.core.SystemFacade;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import third.com.snail.trafficmonitor.engine.util.StorageUtils;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * handle the app download over event
 */
public class ApkInstaller {

    /**
     * Add for Much Start 2015/10/15
     */
    //系统安装接口需要的安装标志
    private static final int FLAG_INSTALL_INTERNAL = 0x00000010;
    private static final int FLAG_INSTALL_EXTERNAL = 0x00000008;
    private static final int FLAG_INSTALL_REPLACE_EXISTING = 0x00000002;
    private static final int FLAG_INSTALL_SILENTLY_INTERNAL = FLAG_INSTALL_INTERNAL | FLAG_INSTALL_REPLACE_EXISTING;
    private static final int FLAG_INSTALL_SILENTLY_EXTERNAL = FLAG_INSTALL_EXTERNAL | FLAG_INSTALL_REPLACE_EXISTING;

    //系统安装接口返回的结果码
    private static final int INSTALL_SUCCEEDED                              = 1;
    private static final int INSTALL_FAILED_INSUFFICIENT_STORAGE            = -4;

    /**
     * Add for Much End 2015/10/15
     */

    private Context mContext;
    private SystemFacade mSystemFacade;
    private final long notificationId;
    private final String filePath;
    private final String pkgName;

    public ApkInstaller(Context context, long notificationId, String filePath, String pkgName) {
        mContext = context;
        mSystemFacade = new RealSystemFacade(mContext);
        this.notificationId = notificationId;
        this.filePath = filePath;
        this.pkgName = pkgName;
    }


    public synchronized void execute() {
        //静默安装启用  免商店自动更一直使用系统的
        if (isAutoInstallAvailable(mContext) && (!pkgName.equals(AppConstants.APP_PACKAGE_NAME))) {
            if (isSystemApp())
                installWithSystem();
            else
                installWithRoot();
        } else {
            installWithSystem();
        }
    }

    private void installWithSystem() {
        installApk(filePath, true, false, notificationId);
        SharedPreferencesUtil.getInstance().setAutoInstall(false);
    }

    public void installWithRoot() {
        try {
            final Process process = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            // Execute commands that require root access
            os.writeBytes("pm install -r \"" + filePath + "\"\n");
            os.flush();

            DownloadHelper.installDownload(mContext, Downloads.STATUS_INSTALLING, notificationId);
            // restart download service to send notification
            DownloadService.start(mContext);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream errIs = null;
                    InputStream inIs = null;

                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int read = -1;
                        errIs = process.getErrorStream();
                        while((read = errIs.read()) != -1){
                            baos.write(read);
                        }

                        baos.write('\n');
                        inIs = process.getInputStream();
                        while((read = inIs.read()) != -1){
                            baos.write(read);
                        }

                        String result = new String(baos.toByteArray());

                        if (result.toLowerCase().contains("success")) {
                            // Success :-)
                        } else {
                            cancelNotification(notificationId);
                            installWithSystem();
                        }
                    } catch (Exception e) {
                        cancelNotification(notificationId);
                        installWithSystem();
                    } finally {
                        try{
                            if(errIs != null){
                                errIs.close();
                            }
                            if(inIs != null){
                                inIs.close();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        process.destroy();

                        DownloadHelper.installDownload(mContext, Downloads.STATUS_SUCCESS, notificationId);
                        // restart download service to send notification
                        DownloadService.start(mContext);
                    }
                }
            }).start();
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    private static void cancelNotification(long notificationID) {
        new RealSystemFacade(FreeStoreApp.getContext()).cancelNotification(notificationID);
    }

    public static boolean isAutoInstallAvailable(Context context) {
        return SharedPreferencesUtil.getInstance().isAutoInstall()
                && CommandHelper.hasRoot();
    }

    /**
     * 安装应用程序
     *
     * @param apkLocalUri APK文件的路径
     */
    public static void installApk(String apkLocalUri) {
        installApk(apkLocalUri, false);
    }

    /**
     * 安装应用程序
     *
     * @param apkLocalUri APK文件的路径
     */
    public static void installApk(String apkLocalUri, boolean isFilePath, boolean failde, long notificationId) {
        if (apkLocalUri == null) return;

        if (isSystemApp() && !failde) {
            installSilently(apkLocalUri, isFilePath, notificationId);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(isFilePath ? Uri.fromFile(new File(apkLocalUri)) : Uri.parse(apkLocalUri),
                    "application/vnd.android.package-archive");
            if (isSystemApp()) {
                i.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);//设置为可信来源
                i.putExtra("android.intent.snail.NOT_UNKNOWN_SOURCE", true);//设置私有标识
            }
            FreeStoreApp.getContext().startActivity(i);
        }
    }

    /**
     * 安装应用程序
     *
     * @param apkLocalUri APK文件的路径
     */
    public static void installApk(String apkLocalUri, boolean isFilePath) {
        installApk(apkLocalUri, isFilePath, false, -1);
    }


    // 判断机器是否为MUCH或者W3D
    public static boolean isSystemApp() {
        return (Build.MODEL.startsWith("MUCH") || Build.MODEL.equals("W3D")) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    }

    // 静默安装(MUCH)
    private static void installSilently(final String apkLocalUri, final boolean isFilePath, final long notificationId) {
        DownloadHelper.installDownload(FreeStoreApp.getContext(), Downloads.STATUS_INSTALLING, notificationId);
        // restart download service to send notification
        DownloadService.start(FreeStoreApp.getContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                int flags = FLAG_INSTALL_SILENTLY_INTERNAL;
                int selectLocation = StorageUtils.getInstallPath(FreeStoreApp.getContext());
                if (selectLocation == StorageUtils.APP_INSTALL_SDCARD) {
                    flags = FLAG_INSTALL_SILENTLY_EXTERNAL;
                }
                doInstallSilently(flags, apkLocalUri, isFilePath, notificationId);
            }
        }).start();
    }

    // 静默安装(MUCH)
    private static void doInstallSilently(final int flags, final String apkLocalUri, final boolean isFilePath, final long notificationId) {
        PackageManager pm = FreeStoreApp.getContext().getPackageManager();
        try {
            Method method = pm.getClass().getDeclaredMethod("installPackage",
                    Uri.class, IPackageInstallObserver.class, int.class, String.class);

            method.invoke(pm, isFilePath ? Uri.fromFile(new File(apkLocalUri)) : Uri.parse(apkLocalUri), new IPackageInstallObserver.Stub() {

                @Override
                public void packageInstalled(String packageName, int returnCode)
                        throws RemoteException {
                    if (INSTALL_SUCCEEDED == returnCode) {
                        DownloadHelper.installDownload(FreeStoreApp.getContext(), Downloads.STATUS_SUCCESS, notificationId);
                        // restart download service to send notification
                        DownloadService.start(FreeStoreApp.getContext());

                        return;
                    } else if (INSTALL_FAILED_INSUFFICIENT_STORAGE == returnCode)
                        doInstallSilently(FLAG_INSTALL_SILENTLY_INTERNAL, apkLocalUri, isFilePath, notificationId);
                    else {
                        cancelNotification(notificationId);
                        installApk(apkLocalUri, isFilePath, true, notificationId);
                    }

                }
            }, flags, "");
            return;
        } catch (IllegalArgumentException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        cancelNotification(notificationId);
        installApk(apkLocalUri, isFilePath, true, notificationId);
    }

}
