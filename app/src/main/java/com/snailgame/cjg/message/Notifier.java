
package com.snailgame.cjg.message;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.daoHelper.PushModelDaoHelper;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.NotificationUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.ListUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {
    private Context context;
    private NotificationManager notificationManager;
    private final Handler handler = new Handler();

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 如果推送过来的是活动或者合集或者其他之外就弹出自定义大banner的通知栏
     *
     * @param id        推送后产生的通知id
     * @param model     推送的实体类
     * @param isAppPush 推送是否是游戏或者应用或者是小编回复，如果是的话通知栏显示的布局不一样, 或为显示正方形 icon
     */
    public void notify(int id, PushModel model, boolean isAppPush) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setTicker(model.getTitle())
                .setContentTitle(model.getTitle())
                .setContentText(model.getContent())
                .setAutoCancel(true);
        NotificationUtils.setIcon(context, builder);
        builder.setContentIntent(PendingIntent.getBroadcast(context, id, NotifyReceiver.contentIntent(context, model), PendingIntent.FLAG_CANCEL_CURRENT));
        //设置被用户手动取消或者一键清除通知后程序收到通知被取消的地方，但是需要注意的是，由于和contentIntent设置的PendingIntent相同，那么就必须设置requestcode不同
        //如果相同会导致第一个不生效
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, id + 1, NotifyReceiver.deleteIntent(context, model), PendingIntent.FLAG_CANCEL_CURRENT));
        Notification notify = builder.build();
        if (!isAppPush && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            initExpandRemoteViews(notify, model, id);
        }
        initCollapseRemoteViews(notify, model, id, isAppPush);
        notify.defaults = Notification.DEFAULT_ALL;
        notify.defaults |= Notification.DEFAULT_SOUND;
        notify.defaults |= Notification.DEFAULT_VIBRATE;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notify);
        String ids = SharedPreferencesUtil.getInstance().getNotificationID();
        if (!TextUtils.isEmpty(ids))
            ids = ids + "," + id;
        else {
            ids = String.valueOf(id);
        }
        SharedPreferencesUtil.getInstance().setNotificationID(ids);
    }

    /**
     * 初始化有大图的展开通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initExpandRemoteViews(final Notification notify, final PushModel model, final int id) {
        if (TextUtils.isEmpty(model.getIconBigUrl()))
            return;
        final RemoteViews expandRemoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notify_push_expand_layout);
        expandRemoteViews.setTextViewText(R.id.content_tv, model.getTitle());
        BitmapManager.showImg(model.getIconBigUrl(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<PushModel> models = PushModelDaoHelper.queryForEq(FreeStoreApp.getContext(),
                                            PushModel.COL_PUSH_MESSAGE_ID, model.getMsgId());
                                    if (ListUtils.isEmpty(models))
                                        return;
                                    PushModel pushModel = models.get(0);
                                    if (pushModel != null && pushModel.getIsExit() == PushModel.IS_EXIT) {
                                        //volly下载下来的是 RGB_565 的位图，在6.0机型上某些图不显示，需要进行转换，但是图片占用内存会变大
                                        Bitmap clone = response.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                                        expandRemoteViews.setImageViewBitmap(R.id.content_img, clone);
                                        notify.bigContentView = expandRemoteViews;
                                        notify.defaults = 0;
                                        notificationManager.notify(id, notify);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
    }

    /**
     * 初始化收缩起来的未展开通知栏
     */
    private void initCollapseRemoteViews(final Notification notify, final PushModel model, final int id, boolean isAppPush) {
        final RemoteViews remoteViews;
        if (isAppPush) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.notify_normal_layout);
        } else {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.notify_push_layout);
        }
        remoteViews.setTextViewText(R.id.title_tv, model.getTitle());
        remoteViews.setTextViewText(R.id.content_tv, model.getContent());
        if(model.getType() == PushReceiver.PUSHTYPE_FRIEND_ADD){//如果是好友，则改变默认图片
            remoteViews.setImageViewResource(R.id.icon,R.drawable.ic_share_avatar_default);
        }
        DateFormat df = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.time_tv, df.format(new Date()));
        notify.contentView = remoteViews;
        BitmapManager.showImg(model.getIcoUrl(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<PushModel> models = PushModelDaoHelper.queryForEq(FreeStoreApp.getContext(),
                                            PushModel.COL_PUSH_MESSAGE_ID, model.getMsgId());
                                    if (ListUtils.isEmpty(models))
                                        return;
                                    PushModel pushModel = models.get(0);
                                    if (pushModel != null && pushModel.getIsExit() == PushModel.IS_EXIT) {
                                        //volly下载下来的是 RGB_565 的位图，在6.0机型上某些图不显示，需要进行转换，但是图片占用内存会变大
                                        Bitmap clone = response.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                                        remoteViews.setImageViewBitmap(R.id.icon, clone);
                                        notify.defaults = 0;
                                        notificationManager.notify(id, notify);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }

}
