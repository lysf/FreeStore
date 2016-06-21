package com.snailgame.cjg.common.server;

import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.core.DownloadProvider;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

/**
 * Created by taj on 14-3-12.
 */
public class DownloadChangeObserver extends ContentObserver {
    private Handler mHandler;
    private Context mContext;
    private int mWhat;
    public DownloadChangeObserver(Context context,Handler handler, int what) {
        super(handler);
        mHandler = handler;
        mContext = context;
        mWhat = what;
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        doChange(uri);
    }

    private void doChange(Uri uri) {
        if (uri == null) {
            int queryFilter = DownloadManager.STATUS_PENDING
                    | DownloadManager.STATUS_PENDING_FOR_WIFI
                    | DownloadManager.STATUS_RUNNING
                    | DownloadManager.STATUS_PAUSED
                    | DownloadManager.STATUS_FAILED;
            List<TaskInfo> taskInfos = DownloadHelper.getDownloadTasks(
                    mContext,
                    DownloadHelper.QUERY_TYPE_BY_STATUS,
                    queryFilter);
            if (!ListUtils.isEmpty(taskInfos)) {
                mHandler.sendMessage(Message.obtain(mHandler, mWhat, taskInfos));
            }
            return;
        }

        // Only observe the individual download which matches with "my_downloads/#"
        int match = DownloadProvider.sURIMatcher.match(uri);
        if (match != DownloadProvider.ALL_DOWNLOADS_ID) return;

        long downloadId = ContentUris.parseId(uri);
        if (downloadId == -1) return;

        List<TaskInfo> taskInfos = DownloadHelper.getDownloadTasks(
                mContext,
                DownloadHelper.QUERY_TYPE_BY_ID,
                (int)downloadId);
        if (!ListUtils.isEmpty(taskInfos)) {
            //TaskInfo taskInfo = taskInfos.get(0);
            mHandler.sendMessage(Message.obtain(mHandler, mWhat, taskInfos));
        }
    }

}

