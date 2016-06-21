
package com.snailgame.cjg.download.core;

import android.app.Notification;
import android.content.Intent;


public interface SystemFacade {
    /**
     * @see System#currentTimeMillis()
     */
    long currentTimeMillis();

    /**
     * @return Network type (as in ConnectivityManager.TYPE_*) of currently active network, or null
     * if there's no active connection.
     */
    Integer getActiveNetworkType();

    /**
     * @see android.telephony.TelephonyManager#isNetworkRoaming
     */
    boolean isNetworkRoaming();

    /**
     * @return maximum size, in bytes, of downloads that may go over a mobile connection; or null if
     * there's no limit
     */
    Long getMaxBytesOverMobile();

    /**
     * @return recommended maximum size, in bytes, of downloads that may go over a mobile
     * connection; or null if there's no recommended limit.  The user will have the option to bypass
     * this limit.
     */
    Long getRecommendedMaxBytesOverMobile();

    /**
     * Send a broadcast intent.
     */
    void sendBroadcast(Intent intent);

    /**
     * Post a system notification to the NotificationManager.
     */
    void postNotification(long id, Notification notification);

    /**
     * Cancel a system notification.
     */
    void cancelNotification(long id);

    /**
     * Cancel all system notifications.
     */
    void cancelAllNotifications();

    /**
     * Start a thread.
     */
    void startThread(Thread thread);
}
