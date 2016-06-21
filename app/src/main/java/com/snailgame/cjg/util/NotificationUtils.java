package com.snailgame.cjg.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by xixh on 2015/3/24.
 */
public class NotificationUtils {

    public static void setIcon(Context context, NotificationCompat.Builder builder) {
        if (builder == null)
            return;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.notification);
        } else {
            builder.setSmallIcon(R.drawable.notification_small);
            builder.setColor(ResUtil.getColor(R.color.red));
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    ResUtil.getResources(),
                    R.drawable.notification));
        }

    }

    /**
     * 开启线程下载程序时，用来在下载线程中回调到主线程更新通知栏上面的下载应用的图标
     */
    public interface DownLoadNotifyIcon {
        void onLoadIcon(Context context, String url, RemoteViews remoteView);
    }
}
