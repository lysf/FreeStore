package com.snailgame.cjg.download.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.snailgame.fastdev.util.LogUtils;

public class RealSystemFacade implements SystemFacade {
    private Context mContext;
    private NotificationManager mNotificationManager;
    // 2 GB
    private static final long DOWNLOAD_MAX_BYTES_OVER_MOBILE = 1024 * 1024 * 1024;
    // 1 GB
    private static final long DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = 1024 * 1024 * 1024;

    public RealSystemFacade(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取网络的类型，当网络不可链接时，放回null
     * @return
     */
    public Integer getActiveNetworkType() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            LogUtils.w( "couldn't get connectivity manager");
            return null;
        }

        NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null) {
            if (Constants.LOGVV) {
                LogUtils.v("network is not available");
            }
            return null;
        }
        return activeInfo.getType();
    }

    public boolean isNetworkRoaming() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        boolean isMobile = (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE);
        final TelephonyManager mgr = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        boolean isRoaming = isMobile && mgr.isNetworkRoaming();
        if (Constants.LOGVV && isRoaming) {
            Log.v(Constants.TAG, "network is roaming");
        }
        return isRoaming;
    }

    public Long getMaxBytesOverMobile() {
        return DOWNLOAD_MAX_BYTES_OVER_MOBILE;
    }

    @Override
    public Long getRecommendedMaxBytesOverMobile() {
        return DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE;
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mContext.sendBroadcast(intent);
    }

    @Override
    public void postNotification(long id, Notification notification) {
        /**
         * TODO: The system notification manager takes ints, not longs, as IDs,
         * but the download manager uses IDs take straight from the database,
         * which are longs. This will have to be dealt with at some point.
         */
        mNotificationManager.notify((int) id, notification);
    }

    @Override
    public void cancelNotification(long id) {
        mNotificationManager.cancel((int) id);
    }

    @Override
    public void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    @Override
    public void startThread(Thread thread) {
        thread.start();
    }
}
