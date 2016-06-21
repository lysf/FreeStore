/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snailgame.cjg.download.core;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.event.DownloadTaskRemoveEvent;
import com.snailgame.cjg.event.NotifyRemoveEvent;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.NotificationUtils;
import com.snailgame.fastdev.image.BitmapManager;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Performs the background downloads requested by applications that use the
 * Downloads provider.
 */
public class DownloadService extends Service implements NotificationUtils.DownLoadNotifyIcon {


    /**
     * The Service's view of the list of downloads, mapping download IDs to the
     * corresponding info object. This is kept independently from the content
     * provider, and the Service only initiates downloads based on this data, so
     * that it can deal with situation where the data in the content provider
     * changes or disappears.
     */
    public Map<Long, DownloadInfo> mDownloads = new HashMap<Long, DownloadInfo>();
    /**
     * The thread that updates the internal download list from the content provider.
     */
    UpdateThread mUpdateThread;
    SystemFacade mSystemFacade;

    /**
     * Observer to get notified when the content observer's data changes
     */
    private DownloadContentObserver mObserver;
    /**
     * Class to handle Notification Manager updates
     */
    private DownloadNotificationHelper mNotifier;
    /**
     * Whether the internal download list should be updated from the content provider.
     */
    private boolean mPendingUpdate;
    private FileObserver mFileObserver;

    public static void start(Context context) {
        context.startService(new Intent(context, DownloadService.class));
    }

    /**
     * Returns an IBinder instance when someone wants to connect to this
     * service. Binding to this service is not allowed.
     *
     * @throws UnsupportedOperationException
     */
    public IBinder onBind(Intent i) {
        throw new UnsupportedOperationException(
                "Cannot bind to Download Manager Service");
    }

    /**
     * Initializes the service when it is first created
     */
    private String url;
    private RemoteViews remoteViews;
    private static final int LOAD_MSG = 1;

    private Handler handler = new MsgHandler(this);


    @Override
    public void onCreate() {
        super.onCreate();
        if (Constants.LOGVV) {
            Log.d(Constants.TAG, "Download Service onCreate");
        }

        if (mSystemFacade == null) {
            mSystemFacade = new RealSystemFacade(this);
        }

        mObserver = new DownloadContentObserver();
        getContentResolver().registerContentObserver(Downloads.ALL_DOWNLOADS_CONTENT_URI, true, mObserver);
        mNotifier = new DownloadNotificationHelper(this, mSystemFacade);
        mFileObserver = new DownloadFileObserver();
        mFileObserver.startWatching();
        updateFromProvider();

        MainThreadBus.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int returnValue = super.onStartCommand(intent, flags, startId);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onStart");
        }
        updateFromProvider();
        return returnValue;
    }

    /**
     * Cleans up when the service is destroyed
     */
    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mObserver);
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
        super.onDestroy();
        if (Constants.LOGVV) {
            Log.d(Constants.TAG, "Download Service onDestroy");
        }

        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * Parses data from the content provider into private array
     */
    private void updateFromProvider() {
        synchronized (this) {
            mPendingUpdate = true;
            if (mUpdateThread == null) {
                mUpdateThread = new UpdateThread();
                mSystemFacade.startThread(mUpdateThread);
            }
        }
    }

    /**
     * Drops old rows from the database to prevent it from growing too large
     */
    private void trimDatabase() {
        Cursor cursor = getContentResolver().query(Downloads.ALL_DOWNLOADS_CONTENT_URI,
                new String[]{Downloads._ID}, Downloads.COLUMN_STATUS + " >= '200'", null,
                Downloads.COLUMN_LAST_MODIFICATION);
        if (cursor == null) {
            // This isn't good - if we can't do basic queries in our database,
            // nothing's gonna work
            Log.e(Constants.TAG, "null cursor in trimDatabase");
            return;
        }
        if (cursor.moveToFirst()) {
            int numDelete = cursor.getCount() - Constants.MAX_DOWNLOADS;
            int columnId = cursor.getColumnIndexOrThrow(Downloads._ID);
            while (numDelete > 0) {
                Uri downloadUri = ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, cursor.getLong(columnId));
                getContentResolver().delete(downloadUri, null, null);
                if (!cursor.moveToNext()) {
                    break;
                }
                numDelete--;
            }
        }
        cursor.close();
    }

    /**
     * Keeps a local copy of the info about a download, and initiates the
     * download if appropriate.
     */
    private DownloadInfo insertDownload(DownloadInfo.Reader reader) {
        DownloadInfo info = reader.newDownloadInfo(this, mSystemFacade);
        if (mDownloads.get(info.mId) == null) {
            mDownloads.put(info.mId, info);
        }
        if (Constants.LOGVV) {
            info.logVerboseInfo();
        }
        info.startIfReady(true);
        return info;
    }

    /**
     * Updates the local copy of the info about a download.
     */
    private void updateDownload(DownloadInfo.Reader reader, DownloadInfo info, long now) {
        int oldStatus = info.mStatus;

        reader.updateFromDatabase(info);

        if (Constants.LOGVV) {
            info.logVerboseInfo();
        }

        boolean justCompleted = !Downloads.isStatusCompleted(oldStatus)
                && Downloads.isStatusCompleted(info.mStatus);
        if (justCompleted) {
            mSystemFacade.cancelNotification(info.mId);
        }

        info.startIfReady(false);
    }

    /**
     * Removes the local copy of the info about a download.
     */
    private void deleteDownload(long id) {
        DownloadInfo info = mDownloads.get(id);
        if (info.mStatus == Downloads.STATUS_RUNNING) {
            info.mStatus = Downloads.STATUS_CANCELED;
        }
        if (info.mFileName != null) {
            new File(info.mFileName).delete();
        }
        mSystemFacade.cancelNotification(info.mId);
        mDownloads.remove(info.mId);
    }

    @Override
    public void onLoadIcon(Context context, String url, RemoteViews remoteViews) {
        this.url = url;
        this.remoteViews = remoteViews;
        handler.sendEmptyMessage(LOAD_MSG);
    }

    private void loadIcon() {
        BitmapManager.showImg(url,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (remoteViews == null) return;
                        if (response.getBitmap() != null) {
                            //volly下载下来的是 RGB_565 的位图，在6.0机型上某些图不显示，需要进行转换，但是图片占用内存会变大
                            Bitmap clone = response.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                            remoteViews.setImageViewBitmap(R.id.icon, clone);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }

    /**
     * Receives notifications when the data in the content provider changes
     */
    private class DownloadContentObserver extends ContentObserver {

        public DownloadContentObserver() {
            super(new Handler());
        }

        /**
         * Receives notification when the data in the observed content provider
         * changes.
         */
        public void onChange(final boolean selfChange) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Service ContentObserver received notification");
            }
            updateFromProvider();
        }

    }

    /**
     * Observer for the download file directory
     */
    private class DownloadFileObserver extends FileObserver {
        public DownloadFileObserver() {
            super(DownloadManager.DOWNLOAD_FILE_DIR, FileObserver.DELETE | FileObserver.CREATE);
        }

        @Override
        public void onEvent(int event, String path) {
            if (TextUtils.isEmpty(path)) return;
            switch (event) {
                case FileObserver.DELETE:
                    Set<Long> set = new HashSet<Long>(mDownloads.keySet());
                    DownloadInfo downloadInfo;
                    String fileName;
                    String tempFileName;

                    for (Long id : set) {
                        downloadInfo = mDownloads.get(id);
                        if (downloadInfo != null) {
                            fileName = downloadInfo.mFileName;
                            if (TextUtils.isEmpty(fileName)) continue;

                            tempFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                            if (!TextUtils.isEmpty(tempFileName) && path.equals(tempFileName)
                                    && !path.endsWith(DownloadInfo.SUFFIX_PATCHED_APK)) {
                                downloadInfo.isFileDeleted = true;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class UpdateThread extends Thread {
        public UpdateThread() {
            super("Download Service");
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (Constants.LOGVV) {
                Log.i(Constants.TAG, "Download Service running ....");
            }

            trimDatabase();

            boolean keepService = false;
            // for each update from the database, remember which download is
            // supposed to get restarted soonest in the future
            for (; ; ) {
                synchronized (DownloadService.this) {
                    if (mUpdateThread != this) {
                        throw new IllegalStateException("multiple UpdateThreads in DownloadService");
                    }
                    if (!mPendingUpdate) {
                        mUpdateThread = null;
                        if (!keepService) {
                            stopSelf();
                        }
                        return;
                    }
                    mPendingUpdate = false;
                }

                long now = mSystemFacade.currentTimeMillis();
                keepService = false;
                Set<Long> idsNoLongerInDatabase = new HashSet<Long>(mDownloads.keySet());

                Cursor cursor = getContentResolver().query(Downloads.ALL_DOWNLOADS_CONTENT_URI, null, null, null, null);
                if (cursor == null) {
                    continue;
                }
                try {
                    DownloadInfo.Reader reader = new DownloadInfo.Reader(cursor);
                    int idColumn = cursor.getColumnIndexOrThrow(Downloads._ID);
                    if (Constants.LOGVV) {
                        Log.i(Constants.TAG, "number of rows from downloads-db: " + cursor.getCount());
                    }
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        idsNoLongerInDatabase.remove(id);
                        DownloadInfo info = mDownloads.get(id);
                        if (info != null) {
                            updateDownload(reader, info, now);
                        } else {
                            info = insertDownload(reader);
                        }
                        if (info.hasCompletionNotification()) {
                            keepService = true;
                        }
                        long next = info.nextAction();
                        if (next == 0) {
                            keepService = true;
                        }
                    }
                } finally {
                    cursor.close();
                }
                for (Long id : idsNoLongerInDatabase) {
                    deleteDownload(id);
                }
                mNotifier.updateWith(mDownloads.values(), DownloadService.this);

                ArrayList<DownloadInfo> downloadInfoArray = new ArrayList<DownloadInfo>();
                for (DownloadInfo info : mDownloads.values()) {
                    if (info.mStatus == Downloads.STATUS_SUCCESS) continue;
                    downloadInfoArray.add(info);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    @Subscribe
    public void onRemoveDownload(DownloadTaskRemoveEvent event) {
        String pkgName = event.getPkgName();

        if (TextUtils.isEmpty(pkgName)) {
            return;
        }

        if (mNotifier != null)
            mNotifier.removeFromPendingList(pkgName);
    }


    @Subscribe
    public void onRemoveNotigy(NotifyRemoveEvent event) {
        if (mNotifier != null)
            mNotifier.removeFromBuilderMap(event.getNotifyId());
    }

    static class MsgHandler extends Handler {
        private WeakReference<DownloadService> mService;
        public MsgHandler(DownloadService service) {
            this.mService = new WeakReference<DownloadService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadService service = mService.get();
            if(service != null){
                switch (msg.what) {
                    case LOAD_MSG:
                        service.loadIcon();
                        break;
                }
            }
        }
    }
}
