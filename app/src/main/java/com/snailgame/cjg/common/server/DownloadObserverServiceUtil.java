package com.snailgame.cjg.common.server;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.util.MainThreadBus;

import java.util.ArrayList;

/**
 * Created by sunxy on 2014/12/8.
 */
public class DownloadObserverServiceUtil {

    private DownloadChangeObserver downloadObserver = null;
    private static final int MSG_UPDATE_PROGRESS = 0;

    private static DownloadObserverServiceUtil instance;
    private Context context;

    //处理应用下载时，在列表中进度的显示
    private Handler handler = new MsgHandler();


    public static DownloadObserverServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadObserverServiceUtil(context);
        }
        return instance;
    }

    public DownloadObserverServiceUtil(Context context) {
        this.context = context;
        initDownloadObserver();
    }


    private void initDownloadObserver() {
        downloadObserver = new DownloadChangeObserver(context, handler, MSG_UPDATE_PROGRESS);
        context.getContentResolver().registerContentObserver(Downloads.ALL_DOWNLOADS_CONTENT_URI, true, downloadObserver);
    }


    public void onDestroy() {
        context.getContentResolver().unregisterContentObserver(downloadObserver);
        if (handler != null)
            handler.removeMessages(MSG_UPDATE_PROGRESS);
    }

    static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    ArrayList<TaskInfo> taskInfos = (ArrayList<TaskInfo>) msg.obj;
                    MainThreadBus.getInstance().post(new DownloadInfoChangeEvent(taskInfos));
                    break;
                default:
                    break;
            }
        }
    }

}
