/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.snailgame.cjg.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;

import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The download manager is a system service that handles long-running HTTP
 * downloads. Clients may request that a URI be downloaded to a particular
 * destination file. The download manager will conduct the download in the
 * background, taking care of HTTP interactions and retrying downloads after
 * failures or across connectivity changes and system reboots.
 * <p>
 * Instances of this class should be obtained through
 * {@link android.content.Context#getSystemService(String)} by passing
 * {@link android.content.Context#DOWNLOAD_SERVICE}.
 * <p>
 * Apps that request downloads through this API should register a broadcast
 * receiver for {@link #ACTION_NOTIFICATION_CLICKED} to appropriately handle
 * when the user clicks on a running download in a notification or from the
 * downloads UI.
 */
public class DownloadManager {
    /**
     * An identifier for a particular download, unique across the system.
     * Clients use this ID to make subsequent calls related to the download.
     */
    public final static String COLUMN_ID = BaseColumns._ID;
    /**
     * the file dir for the download file
     */
    public final static String DOWNLOAD_FILE_DIR = Environment.getExternalStorageDirectory()
            + "/" + "FreeStore" + "/" + "Download";
    /**
     * The client-supplied title for this download. This will be displayed in
     * system notifications. Defaults to the empty string.
     */
    public final static String COLUMN_TITLE = "title";
    /**
     * The client-supplied description of this download. This will be displayed
     * in system notifications. Defaults to the empty string.
     */
    public final static String COLUMN_DESCRIPTION = "description";
    /**
     * URI to be downloaded.
     */
    public final static String COLUMN_URI = "uri";
    /**
     * Total size of the download in bytes. This will initially be -1 and will
     * be filled in once the download starts.
     */
    public final static String COLUMN_TOTAL_SIZE_BYTES = "total_size";
    /**
     * Uri where downloaded file will be stored. If a destination is supplied by
     * client, that URI will be used here. Otherwise, the value will initially
     * be null and will be filled in with a generated URI once the download has
     * started.
     */
    public final static String COLUMN_LOCAL_URI = "local_uri";
    /**
     * Current status of the download, as one of the STATUS_* constants.
     */
    public final static String COLUMN_STATUS = "status";
    /**
     * Provides more detail on the status of the download. Its meaning depends
     * on the value of {@link #COLUMN_STATUS}.
     * <p>
     * When {@link #COLUMN_STATUS} is {@link #STATUS_FAILED}, this indicates the
     * type of error that occurred. If an HTTP error occurred, this will hold
     * the HTTP status code as defined in RFC 2616. Otherwise, it will hold one
     * of the ERROR_* constants.
     * <p>
     * When {@link #COLUMN_STATUS} is {@link #STATUS_PAUSED}, this indicates why
     * the download is paused. It will hold one of the PAUSED_* constants.
     * <p>
     * If {@link #COLUMN_STATUS} is neither {@link #STATUS_FAILED} nor
     * {@link #STATUS_PAUSED}, this column's value is undefined.
     *
     * @see <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6.1.1">RFC
     * 2616 status codes</a>
     */
    public final static String COLUMN_REASON = "reason";
    /**
     * Number of bytes download so far.
     */
    public final static String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";
    /**
     * Timestamp when the download was last modified, in
     * {@link System#currentTimeMillis System.currentTimeMillis()} (wall clock
     * time in UTC).
     */
    public final static String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";
    /**
     * The name of the column containing the filename that the initiating
     * application recommends. When possible, the download manager will attempt
     * to use this filename, or a variation, as the actual name for the file.
     */
    public final static String COLUMN_DOWNLOAD_HINT = "hint";
    /**
     * All custom columns for snail apps
     */
    public final static String COLUMN_TOTAL_SIZE_BYTES_DEFAULT = "total_size_default";
    public final static String COLUMN_APP_ID = "app_id";
    public final static String COLUMN_APP_LABEL = "app_label";
    public final static String COLUMN_APP_PKG_NAME = "app_pkg_name";
    public final static String COLUMN_APP_ICON_URL = "icon_url";
    public final static String COLUMN_APP_VERSION_NAME = "app_version_name";
    public final static String COLUMN_APP_VERSION_CODE = "app_version_cd";
    public final static String COLUMN_BYTES_IN_WIFI = "bytes_in_wifi";
    public final static String COLUMN_BYTES_IN_3G = "bytes_in_3g";
    public final static String COLUMN_MD5 = "md5";
    public final static String COLUMN_FLOW_FREE_STATE_V2 = "flow_free_state_v2";
    public final static String COLUMN_APP_TYPE = "app_type";
    public final static String COLUMN_PATCH_TYPE = "patch_type";

    // this array must contain all public columns
    private static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_DESCRIPTION,
            COLUMN_URI,
            COLUMN_TOTAL_SIZE_BYTES,
            COLUMN_LOCAL_URI,
            COLUMN_STATUS,
            COLUMN_REASON,
            COLUMN_BYTES_DOWNLOADED_SO_FAR,
            COLUMN_LAST_MODIFIED_TIMESTAMP,
            COLUMN_DOWNLOAD_HINT,
            COLUMN_TOTAL_SIZE_BYTES_DEFAULT,
            COLUMN_APP_ID,
            COLUMN_APP_LABEL,
            COLUMN_APP_PKG_NAME,
            COLUMN_APP_ICON_URL,
            COLUMN_APP_VERSION_NAME,
            COLUMN_APP_VERSION_CODE,
            COLUMN_BYTES_IN_WIFI,
            COLUMN_BYTES_IN_3G,
            COLUMN_MD5,
            COLUMN_FLOW_FREE_STATE_V2,
            COLUMN_APP_TYPE,
            COLUMN_PATCH_TYPE
    };

    private static final Set<String> LONG_COLUMNS = new HashSet<String>(
            Arrays.asList(
                    COLUMN_ID,
                    COLUMN_TOTAL_SIZE_BYTES,
                    COLUMN_STATUS,
                    COLUMN_REASON,
                    COLUMN_BYTES_DOWNLOADED_SO_FAR,
                    COLUMN_LAST_MODIFIED_TIMESTAMP,
                    COLUMN_TOTAL_SIZE_BYTES_DEFAULT,
                    COLUMN_APP_ID,
                    COLUMN_APP_VERSION_CODE,
                    COLUMN_BYTES_IN_WIFI,
                    COLUMN_BYTES_IN_3G
            )
    );
    /**
     * Value of {@link #COLUMN_STATUS} when the download is not created.
     * 初始值，还未加入下载
     */
    public final static int STATUS_INITIAL = 0;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to start.
     * 下载等待
     */
    public final static int STATUS_PENDING = 1 << 0;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is currently running.
     * 下载运行
     */
    public final static int STATUS_RUNNING = 1 << 1;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to retry or
     * resume.
     * 下载暂停
     */
    public final static int STATUS_PAUSED = 1 << 2;
    /**
     * Value of {@link #COLUMN_STATUS} when the download has successfully
     * completed.
     * 下载成功
     */
    public final static int STATUS_SUCCESSFUL = 1 << 3;
    /**
     * Value of {@link #COLUMN_STATUS} when the download has failed (and will
     * not be retried).
     * 下载失败
     */
    public final static int STATUS_FAILED = 1 << 4;
    /**
     * Value of {@link #COLUMN_STATUS} when the download has new version
     * 可更新
     */
    public static final int STATUS_EXTRA_UPGRADABLE = 1 << 5;
    /**
     * Value of {@link #COLUMN_STATUS} when the download already been installed
     * 已安装
     */
    public static final int STATUS_EXTRA_INSTALLED = 1 << 6;
    /**
     * Value of {@link #COLUMN_STATUS} when the download has failed (and will
     * not be retried).
     * 正在安装
     */
    public final static int STATUS_INSTALLING = 1 << 7;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is patching.
     * 正在验证
     */
    public final static int STATUS_PATCHING = 1 << 8;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is not ready .
     * 尚在准备
     */
    public final static int STATUS_NOTREADY = 1 << 9;
    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to start for WIFI .
     * 下载等待 预约WIFI下载
     */
    public final static int STATUS_PENDING_FOR_WIFI = 1 << 10;

    /**
     * Value of COLUMN_ERROR_CODE when the download has completed with an error
     * that doesn't fit under any other error code.
     */
    public final static int ERROR_UNKNOWN = 1000;
    /**
     * Value of {@link #COLUMN_REASON} when a storage issue arises which doesn't
     * fit under any other error code. Use the more specific
     * {@link #ERROR_INSUFFICIENT_SPACE} and {@link #ERROR_DEVICE_NOT_FOUND}
     * when appropriate.
     */
    public final static int ERROR_FILE_ERROR = 1001;
    /**
     * Value of {@link #COLUMN_REASON} when an HTTP code was received that
     * download manager can't handle.
     */
    public final static int ERROR_UNHANDLED_HTTP_CODE = 1002;
    /**
     * Value of {@link #COLUMN_REASON} when an error receiving or processing
     * data occurred at the HTTP level.
     */
    public final static int ERROR_HTTP_DATA_ERROR = 1004;
    /**
     * Value of {@link #COLUMN_REASON} when there were too many redirects.
     */
    public final static int ERROR_TOO_MANY_REDIRECTS = 1005;
    /**
     * Value of {@link #COLUMN_REASON} when there was insufficient storage
     * space. Typically, this is because the SD card is full.
     */
    public final static int ERROR_INSUFFICIENT_SPACE = 1006;
    /**
     * Value of {@link #COLUMN_REASON} when no external storage device was
     * found. Typically, this is because the SD card is not mounted.
     */
    public final static int ERROR_DEVICE_NOT_FOUND = 1007;
    /**
     * Value of {@link #COLUMN_REASON} when some possibly transient error
     * occurred but we can't resume the download.
     */
    public final static int ERROR_CANNOT_RESUME = 1008;
    /**
     * Value of {@link #COLUMN_REASON} when the requested destination file
     * already exists (the download manager will not overwrite an existing
     * file).
     */
    public final static int ERROR_FILE_ALREADY_EXISTS = 1009;
    /**
     * Value of {@link #COLUMN_REASON}
     */
    public final static int ERROR_FILE_MD5_NOT_MATCH = 1010;
    /**
     * Value of {@link #COLUMN_REASON}
     */
    public final static int ERROR_PATCHING_FILE_ERROR = 1011;
    /**
     * Value of {@link #COLUMN_REASON} when the download is paused because some
     * network error occurred and the download manager is waiting before
     * retrying the request.
     */
    public final static int PAUSED_WAITING_TO_RETRY = 1;
    /**
     * Value of {@link #COLUMN_REASON} when the download is waiting for network
     * connectivity to proceed.
     */
    public final static int PAUSED_WAITING_FOR_NETWORK = 2;
    /**
     * Value of {@link #COLUMN_REASON} when the download exceeds a size limit
     * for downloads over the mobile network and the download manager is waiting
     * for a Wi-Fi connection to proceed.
     */
    public final static int PAUSED_QUEUED_FOR_WIFI = 3;
    /**
     * Value of {@link #COLUMN_REASON} when the download is paused for some other reason.
     */
    public final static int PAUSED_UNKNOWN = 4;
    /**
     * Broadcast intent action sent by the download manager when a download completes.
     */
    public final static String ACTION_DOWNLOAD_COMPLETE = "com.snailgame.cjg.action.DOWNLOAD_COMPLETE";
    /**
     * Broadcast intent action sent by the download manager when the user clicks
     * on a running download, either from a system notification or from the
     * downloads UI.
     */
    public final static String ACTION_NOTIFICATION_CLICKED = "com.snailgame.cjg.action.DOWNLOAD_NOTIFICATION_CLICKED";
    /**
     * Broadcast intent action sent by the download manager when a download start.
     */
    public final static String ACTION_DOWNLOAD_START = "com.snailgame.cjg.action.DOWNLOAD_START";
    /**
     * Broadcast intent action for remove the notification when the downloaded apk is successfully
     */
    public final static String ACTION_OPEN_APK = "com.snailgame.cjg.action.ACTION_OPEN_APK";

    public final static String ACTION_INSTALL_APK = "com.snailgame.cjg.action.ACTION_INSTALL_APK";
    /**
     * Intent extra included with {@link #ACTION_DOWNLOAD_COMPLETE} intents,
     * indicating the ID (as a long) of the download that just completed.
     */
    public static final String EXTRA_DOWNLOAD_ID = "extra_download_id";
    public static final String GAME_ID = "game_id";
    public static final String DOWNLOAD_RESULT = "download_result";
    public static final String DOWNLOAD_FILE_DEST = "download_file_dest";
    public static final String DOWNLOAD_PKG_NAME = "download_pkg_name";
    public static final String DOWNLOAD_NOTIFY_TITLE = "download_notify_title";
    public static final String DOWNLOAD_HINT = "download_hint";
    // columns to request from DownloadProvider
    private static final String[] UNDERLYING_COLUMNS = new String[]{
            Downloads._ID,
            Downloads.COLUMN_TITLE,
            Downloads.COLUMN_URI,
            Downloads.COLUMN_TOTAL_BYTES,
            Downloads.COLUMN_STATUS,
            Downloads.COLUMN_CURRENT_BYTES,
            Downloads.COLUMN_LAST_MODIFICATION,
            Downloads.COLUMN_FILE_NAME_HINT,
            Downloads._DATA,
            Downloads.COLUMN_TOTAL_BYTES_DEFAULT,
            Downloads.COLUMN_APP_ID,
            Downloads.COLUMN_APP_LABEL,
            Downloads.COLUMN_APP_PKG_NAME,
            Downloads.COLUMN_APP_iCON_URL,
            Downloads.COLUMN_APP_VERSION_NAME,
            Downloads.COLUMN_APP_VERSION_CODE,
            Downloads.COLUMN_BYTES_IN_WIFI,
            Downloads.COLUMN_BYTES_IN_3G,
            Downloads.COLUMN_MD5,
            Downloads.COLUMN_FLOW_FREE_STATE_V2,
            Downloads.COLUMN_FREE_AREA_STATE,
            Downloads.COLUMN_APP_TYPE,
            Downloads.COLUMN_PATCH_TYPE,
            Downloads.COLUMN_DIFF_URL,
            Downloads.COLUMN_DIFF_SIZE,
            Downloads.COLUMN_DIFF_MD5
    };
    private ContentResolver mResolver;
    private Uri mBaseUri = Downloads.ALL_DOWNLOADS_CONTENT_URI;

    public DownloadManager(ContentResolver resolver) {
        mResolver = resolver;
    }

    /**
     * Get a parameterized SQL WHERE clause to select a bunch of given numerical column.
     */
    static String getWhereClauseForNumCols(int bunchSize, String colName) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("(");
        for (int i = 0; i < bunchSize; i++) {
            if (i > 0) {
                whereClause.append("OR ");
            }
            whereClause.append(colName);
            whereClause.append(" = ? ");
        }
        whereClause.append(")");
        return whereClause.toString();
    }

    /**
     * Get a parameterized SQL WHERE clause to select a bunch of given string column & column list.
     */
    static String getWhereClauseForStrCols(int bunchSize, String colName, List<String> colValues) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("(");
        for (int i = 0; i < bunchSize; i++) {
            if (i > 0) {
                whereClause.append("OR ");
            }
            whereClause.append(colName);
            whereClause.append(" = '" + colValues.get(i) + "' ");
        }
        whereClause.append(")");
        return whereClause.toString();
    }

    /**
     * Get the selection args for a clause returned by
     * {@link #getWhereClauseForNumCols(int, String)}.
     */
    static List<String> getWhereArgsForIdList(long[] ids) {
        List<String> whereArgs = new ArrayList<String>(ids.length);
        for (long id : ids) {
            whereArgs.add(Long.toString(id));
        }
        return whereArgs;
    }

    /**
     * Get the selection args for a clause returned by
     * {@link #getWhereClauseForNumCols(int, String)}.
     */
    static List<String> getWhereArgsForAppIdList(int[] appIds) {
        List<String> whereArgs = new ArrayList<String>(appIds.length);
        for (int appId : appIds) {
            whereArgs.add(Integer.toString(appId));
        }
        return whereArgs;
    }

    /**
     * transform string list to string array
     *
     * @param stringArray
     * @return
     */
    static String[] getStringArray(List<String> stringArray) {
        return stringArray.toArray(new String[stringArray.size()]);
    }

    /**
     * Create the dir if the dir not exist
     */
    public static File createDownloadDirIfNotExist() {
        File dir = new File(DownloadManager.DOWNLOAD_FILE_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                //throw new CreateDirFailException("Fail to create the download directory, dirPath=" + dir.getPath());
                return null;
            }
        }
        return dir;
    }

    /**
     * Enqueue a new download. The download will start automatically once the
     * download manager is ready to execute it and connectivity is available.
     *
     * @param request the parameters specifying this download
     * @return an ID for the download, unique across the system. This ID is used
     * to make future calls related to this download.
     */
    public long enqueue(Request request) {
        ContentValues values = request.toContentValues(false);
        Uri uri = mResolver.insert(mBaseUri, values);
        long id = 0L;
        if (uri == null) return id;
        try {
            id = Long.parseLong(uri.getLastPathSegment());
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
            id = 0L;
        }
        return id;
    }

    /**
     * Upgrade the existed download.
     *
     * @param id the id for the existed download
     */
    public void upgrade(long id, Request request) {
        ContentValues values = request.toContentValues(true);
        Uri downloadUri = ContentUris.withAppendedId(mBaseUri, id);
        if (downloadUri != null) {
            mResolver.update(downloadUri, values, null, null);
        }
    }

    /**
     * Update the allowed network type of the downloads
     * when user switch the "wifi-only" download button in setting UI,
     * and then pause the downloads manually
     *
     * @param allowedNetworkTypes the allowed network types
     * @param ids
     * @return the count of the updated rows
     */
    public int toggleAllowedNetworkType(int allowedNetworkTypes, long... ids) {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_ALLOWED_NETWORK_TYPES, allowedNetworkTypes);
        return mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID), getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Mark the status of the downloads to pause according to the allowed network types
     *
     * @param allowedNetworkTypes the allowed network types
     * @param ids
     * @return the count of the updated rows
     */
    public int markToPauseStatus(int allowedNetworkTypes, long... ids) {
        ContentValues values = new ContentValues();
        switch (allowedNetworkTypes) {
            case Request.NETWORK_WIFI:
                values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_QUEUED_FOR_WIFI);
                break;
            default:
                values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PAUSED_BY_APP);
                values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_PAUSED);
                break;
        }
        return mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID), getStringArray(getWhereArgsForIdList(ids)));
    }


    /**
     * Mark the status of the downloads to pause according
     *
     * @return the count of the updated rows
     */
    public int markToPauseStatus() {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PAUSED_BY_APP);
        values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_PAUSED);

        return mResolver.update(mBaseUri, values, Downloads.COLUMN_STATUS + "= '" + Downloads.STATUS_RUNNING + "'", null);
    }

    /**
     * Cancel downloads and remove them from the download manager. Each download
     * will be stopped if it was running, and it will no longer be accessible
     * through the download manager. If a file was already downloaded to
     * external storage, it will not be deleted.
     *
     * @param ids the IDs of the downloads to remove
     * @return the number of downloads actually removed
     */
    public int remove(long... ids) {
        if (ids == null || ids.length == 0) {
            // called with nothing to remove!
            throw new IllegalArgumentException(
                    "input param 'ids' can't be null");
        }
        return mResolver.delete(mBaseUri, getWhereClauseForNumCols(ids.length, Downloads._ID),
                getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Query the download manager about downloads that have been requested.
     *
     * @param query parameters specifying filters for this query
     * @return a Cursor over the result set of downloads, with columns
     * consisting of all the COLUMN_* constants.
     */
    public Cursor query(Query query) {
        Cursor underlyingCursor = query.runQuery(mResolver, UNDERLYING_COLUMNS,
                mBaseUri);
        if (underlyingCursor == null) {
            return null;
        }
        return new CursorTranslator(underlyingCursor);
    }

    /**
     * Open a downloaded file for reading. The download must have completed.
     *
     * @param id the ID of the download
     * @return a read-only {@link android.os.ParcelFileDescriptor}
     * @throws java.io.FileNotFoundException if the destination file does not already exist
     */
    public ParcelFileDescriptor openDownloadedFile(long id)
            throws FileNotFoundException {
        return mResolver.openFileDescriptor(getDownloadUri(id), "r");
    }

    /**
     * Pause the given downloads, which must be running. This method will only
     * work when called from within the download manager's process.
     *
     * @param ids the IDs of the downloads
     */
    public void pauseDownload(long... ids) {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PAUSED_BY_APP);
        values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_PAUSED);
        mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID),
                getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Resume the given downloads, which must be paused. This method will only
     * work when called from within the download manager's process.
     *
     * @param ids the IDs of the downloads
     */
    public void resumeDownload(long... ids) {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
        values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_RUN);
        mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID), getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Restart the given downloads, which must have already completed
     * (successfully or not). This method will only work when called from within
     * the download manager's process.
     *
     * @param ids the IDs of the downloads
     */
    public void restartDownload(long... ids) {
        Cursor cursor = query(new Query().setFilterById(ids));
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                int status = cursor
                        .getInt(cursor.getColumnIndex(COLUMN_STATUS));
                if (status != STATUS_SUCCESSFUL && status != STATUS_FAILED) {
                    throw new IllegalArgumentException(
                            "Cannot restart incomplete download: "
                                    + cursor.getLong(cursor
                                    .getColumnIndex(COLUMN_ID))
                    );
                }
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_CURRENT_BYTES, 0);
        values.put(Downloads.COLUMN_BYTES_IN_WIFI, 0);
        values.put(Downloads.COLUMN_BYTES_IN_3G, 0);
        values.put(Downloads.COLUMN_TOTAL_BYTES, -1);
        values.putNull(Downloads._DATA);
        values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
        mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID),
                getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Install the given downloads, which must be downloaded successfully. This method will only
     * work when called from within the download manager's process.
     *
     * @param statusVal
     * @param ids
     */
    public void installOperation(int statusVal, long... ids) {
        Cursor cursor = query(new Query().setFilterById(ids));
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                int status = cursor
                        .getInt(cursor.getColumnIndex(COLUMN_STATUS));
                if (status != STATUS_SUCCESSFUL && status != STATUS_INSTALLING && status != STATUS_PATCHING) {
                    //throw new IllegalArgumentException(
                    LogUtils.w("Can only install a successful download: "
                            + cursor.getLong(cursor
                            .getColumnIndex(COLUMN_ID)));
                }
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_STATUS, statusVal);
        mResolver.update(mBaseUri, values, getWhereClauseForNumCols(ids.length, Downloads._ID),
                getStringArray(getWhereArgsForIdList(ids)));
    }

    /**
     * Get the DownloadProvider URI for the download with the given ID.
     */
    Uri getDownloadUri(long id) {
        return ContentUris.withAppendedId(mBaseUri, id);
    }

    /**
     * This class contains all the information necessary to request a new
     * download. The URI is the only required parameter.
     * <p>
     * Note that the default download destination is a shared volume where the
     * system might delete your file if it needs to reclaim space for system
     * use. If this is a problem, use a location on external storage (see
     * {@link #setDestinationUri(android.net.Uri)}.
     */
    public static class Request {
        /**
         * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
         * {@link android.net.ConnectivityManager#TYPE_MOBILE}.
         */
        public static final int NETWORK_MOBILE = 1 << 0;

        /**
         * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
         * {@link android.net.ConnectivityManager#TYPE_WIFI}.
         */
        public static final int NETWORK_WIFI = 1 << 1;

        private Uri mUri;
        private Uri mDestinationUri;
        private CharSequence mTitle;
        private int mAllowedNetworkTypes = ~0; // default to all network types
        // set default total bytes if fail in getting the total byte from server
        private long mTotalBytesDefault = 0L;
        private int mAppId; // app id
        private String mAppLabel; //app label
        private String mAppPackage; // app package
        private String mAppIconUrl; // app icon url
        private String mAppVersionName;// app version name
        private long mAppVersionCode;// app version code
        private String md5; // app md5
        private String mFlowFreeState; // flow free state
        private int mFreeAreaState; // free area state

        private String mAppType;
        private int mPatchType;
        private int state;
        public String mDiffUrl;
        public long mDiffSize;
        public String mDiffMd5;

        /**
         * @param uri the HTTP URI to download.
         */
        public Request(Uri uri) {
            if (uri == null) {
                throw new NullPointerException();
            }
            String scheme = uri.getScheme();
            if (scheme == null || !scheme.equals("http")) {
                throw new IllegalArgumentException("Can only download HTTP URIs: " + uri);
            }
            mUri = uri;
        }

        /**
         * Set the local destination for the downloaded file. Must be a file URI
         * to a path on external storage, and the calling application must have
         * the WRITE_EXTERNAL_STORAGE permission.
         * <p>
         * If the URI is a directory(ending with "/"), destination filename will
         * be generated.
         *
         * @return this object
         */
        public Request setDestinationUri(Uri uri) {
            mDestinationUri = uri;
            return this;
        }

        /**
         * Set the title of this download, to be displayed in notifications (if
         * enabled). If no title is given, a default one will be assigned based
         * on the download filename, once the download starts.
         *
         * @return this object
         */
        public Request setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Restrict the types of networks over which this download may proceed.
         * By default, all network types are allowed.
         *
         * @param flags any combination of the NETWORK_* bit flags.
         * @return this object
         */
        public Request setAllowedNetworkTypes(int flags) {
            mAllowedNetworkTypes = flags;
            return this;
        }

        /**
         * set the default total bytes
         *
         * @param defaultTotalBytes the default total bytes
         * @return this object
         */
        public Request setDefaultTotalBytes(long defaultTotalBytes) {
            mTotalBytesDefault = defaultTotalBytes;
            return this;
        }

        /**
         * set the app id
         *
         * @param appId
         */
        public Request setAppId(int appId) {
            mAppId = appId;
            return this;
        }

        /**
         * set app label
         *
         * @param appLabel
         */
        public Request setAppLabel(String appLabel) {
            mAppLabel = appLabel;
            return this;
        }

        /**
         * set app package name
         *
         * @param appPackage
         */
        public Request setAppPackage(String appPackage) {
            mAppPackage = appPackage;
            return this;
        }

        /**
         * set app icon url
         *
         * @param appIconUrl
         */
        public Request setAppIconUrl(String appIconUrl) {
            mAppIconUrl = appIconUrl;
            return this;
        }

        /**
         * set app version name
         *
         * @param appVersionName
         */
        public Request setAppVersionName(String appVersionName) {
            mAppVersionName = appVersionName;
            return this;
        }

        /**
         * set app version code
         *
         * @param appVersionCode
         */
        public Request setAppVersionCode(long appVersionCode) {
            mAppVersionCode = appVersionCode;
            return this;
        }

        /**
         * set app md5
         *
         * @param md5
         */
        public Request setMD5(String md5) {
            this.md5 = md5;
            return this;
        }


        /**
         * set app flow free state
         *
         * @param flowFreeState
         */
        public Request setFlowFreeState(String flowFreeState) {
            mFlowFreeState = flowFreeState;
            return this;
        }


        /**
         * set app free area state
         *
         * @param freeAreaState
         */
        public Request setFreeAreaState(int freeAreaState) {
            mFreeAreaState = freeAreaState;
            return this;
        }

        /**
         * set app type
         *
         * @param mAppType
         * @return
         */
        public Request setAppType(String mAppType) {
            this.mAppType = mAppType;
            return this;
        }

        /**
         * set patch type
         *
         * @param mPatchType
         * @return
         */
        public Request setPatchType(int mPatchType) {
            this.mPatchType = mPatchType;
            return this;
        }


        /**
         * get app state
         *
         * @return
         */
        public int getState() {
            return state;
        }

        /**
         * set app state
         *
         * @param state
         * @return
         */
        public void setState(int state) {
            this.state = state;
        }

        /**
         * set patch url
         *
         * @param diffUrl
         * @return
         */
        public Request setDiffUrl(String diffUrl) {
            this.mDiffUrl = diffUrl;
            return this;
        }

        /**
         * set patch size
         *
         * @param diffSize
         * @return
         */
        public Request setDiffSize(long diffSize) {
            this.mDiffSize = diffSize;
            return this;
        }

        /**
         * set patch md5
         *
         * @param diffMd5
         * @return
         */
        public Request setDiffMd5(String diffMd5) {
            this.mDiffMd5 = diffMd5;
            return this;
        }

        /**
         * @return ContentValues to be passed to DownloadProvider.insert()
         */
        ContentValues toContentValues(boolean isUpgrade) {
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_URI, mUri.toString());
            values.put(Downloads.COLUMN_FILE_NAME_HINT, mDestinationUri.toString());
            values.put(Downloads.COLUMN_TITLE, mTitle.toString());
            values.put(Downloads.COLUMN_ALLOWED_NETWORK_TYPES, mAllowedNetworkTypes);
            values.put(Downloads.COLUMN_TOTAL_BYTES_DEFAULT, mTotalBytesDefault);
            values.put(Downloads.COLUMN_APP_ID, mAppId);
            values.put(Downloads.COLUMN_APP_LABEL, mAppLabel);
            values.put(Downloads.COLUMN_APP_PKG_NAME, mAppPackage);
            values.put(Downloads.COLUMN_APP_iCON_URL, mAppIconUrl);
            values.put(Downloads.COLUMN_APP_VERSION_NAME, mAppVersionName);
            values.put(Downloads.COLUMN_APP_VERSION_CODE, mAppVersionCode);
            values.put(Downloads.COLUMN_MD5, md5);
            values.put(Downloads.COLUMN_FLOW_FREE_STATE_V2, mFlowFreeState);
            values.put(Downloads.COLUMN_FREE_AREA_STATE, mFreeAreaState);
            values.put(Downloads.COLUMN_APP_TYPE, mAppType);
            values.put(Downloads.COLUMN_PATCH_TYPE, mPatchType);
            values.put(Downloads.COLUMN_DIFF_URL, mDiffUrl);
            values.put(Downloads.COLUMN_DIFF_SIZE, mDiffSize);
            values.put(Downloads.COLUMN_DIFF_MD5, mDiffMd5);
            if (isUpgrade) {
                values.put(Downloads.COLUMN_CURRENT_BYTES, 0);
                values.put(Downloads.COLUMN_BYTES_IN_WIFI, 0);
                values.put(Downloads.COLUMN_BYTES_IN_3G, 0);
                values.put(Downloads.COLUMN_TOTAL_BYTES, -1);
                values.putNull(Downloads._DATA);
                values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
                values.put(Downloads.COLUMN_LAST_MODIFICATION, System.currentTimeMillis());
            } else if (state == Downloads.STATUS_PENDING_FOR_WIFI)
                // 预约wifi下载
                values.put(Downloads.COLUMN_STATUS, state);

            return values;
        }
    }

    /**
     * This class may be used to filter download manager queries.
     */
    public static class Query {
        /**
         * Constant for use with {@link #orderBy}
         */
        public static final int ORDER_ASCENDING = 1;

        /**
         * Constant for use with {@link #orderBy}
         */
        public static final int ORDER_DESCENDING = 2;

        private long[] mIds = null;
        private int[] mAppIds = null;
        private Integer mStatusFlags = null;
        private String[] mUrls = null;
        private String[] mPkgNames = null;
        private String mOrderByColumn = Downloads.COLUMN_LAST_MODIFICATION;
        private int mOrderDirection = ORDER_DESCENDING;

        /**
         * Include only the downloads with the given IDs.
         *
         * @return this object
         */
        public Query setFilterById(long... ids) {
            mIds = ids;
            return this;
        }

        /**
         * Include only downloads with status matching any the given status
         * flags.
         *
         * @param flags any combination of the STATUS_* bit flags
         * @return this object
         */
        public Query setFilterByStatus(int flags) {
            mStatusFlags = flags;
            return this;
        }

        /**
         * Include only downloads with status matching any the given AppIDs
         *
         * @param appIds
         * @return this object
         */
        public Query setFilterByAppId(int... appIds) {
            mAppIds = appIds;
            return this;
        }

        /**
         * Include only downloads with status matching any the given urls
         *
         * @param urls
         * @return this object
         */
        public Query setFilterByUrl(String... urls) {
            mUrls = urls;
            return this;
        }

        /**
         * Include only downloads with status matching any the given pkgName
         *
         * @param pkgNames
         * @return this object
         */
        public Query setFilterByPkgName(String... pkgNames) {
            mPkgNames = pkgNames;
            return this;
        }

        /**
         * Change the sort order of the returned Cursor.
         *
         * @param column    one of the COLUMN_* constants; currently, only
         *                  {@link #COLUMN_LAST_MODIFIED_TIMESTAMP} and
         *                  {@link #COLUMN_TOTAL_SIZE_BYTES} are supported.
         * @param direction either {@link #ORDER_ASCENDING} or
         *                  {@link #ORDER_DESCENDING}
         * @return this object
         */
        public Query orderBy(String column, int direction) {
            if (direction != ORDER_ASCENDING && direction != ORDER_DESCENDING) {
                throw new IllegalArgumentException("Invalid direction: "
                        + direction);
            }

            if (column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP)) {
                mOrderByColumn = Downloads.COLUMN_LAST_MODIFICATION;
            } else if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
                mOrderByColumn = Downloads.COLUMN_TOTAL_BYTES;
            } else {
                throw new IllegalArgumentException("Cannot order by " + column);
            }
            mOrderDirection = direction;
            return this;
        }

        /**
         * Run this query using the given ContentResolver.
         *
         * @param projection the projection to pass to ContentResolver.query()
         * @return the Cursor returned by ContentResolver.query()
         */
        Cursor runQuery(ContentResolver resolver, String[] projection,
                        Uri baseUri) {
            Uri uri = baseUri;
            // @IMPORTANT
            // All parts should be wrapped with "(" & ")"
            List<String> selectionParts = new ArrayList<String>();
            List<String> selectionArgList = new ArrayList<String>();

            if (mIds != null) {
                selectionParts.add(getWhereClauseForNumCols(mIds.length, Downloads._ID));
                selectionArgList.addAll(getWhereArgsForIdList(mIds));
            }

            if (mAppIds != null) {
                selectionParts.add(getWhereClauseForNumCols(mAppIds.length, Downloads.COLUMN_APP_ID));
                selectionArgList.addAll(getWhereArgsForAppIdList(mAppIds));
            }

            if (mUrls != null) {
                selectionParts.add(getWhereClauseForStrCols(mUrls.length, Downloads.COLUMN_URI, Arrays.asList(mUrls)));
            }

            if (mPkgNames != null) {
                selectionParts.add(getWhereClauseForStrCols(mPkgNames.length, Downloads.COLUMN_APP_PKG_NAME, Arrays.asList(mPkgNames)));
            }

            if (mStatusFlags != null) {
                List<String> parts = new ArrayList<String>();
                if ((mStatusFlags & STATUS_PENDING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_PENDING));
                }
                if ((mStatusFlags & STATUS_PENDING_FOR_WIFI) != 0) {
                    parts.add(statusClause("=",
                            Downloads.STATUS_PENDING_FOR_WIFI));
                }
                if ((mStatusFlags & STATUS_RUNNING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_RUNNING));
                }
                if ((mStatusFlags & STATUS_PAUSED) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_PAUSED_BY_APP));
                    parts.add(statusClause("=",
                            Downloads.STATUS_WAITING_TO_RETRY));
                    parts.add(statusClause("=",
                            Downloads.STATUS_WAITING_FOR_NETWORK));
                    parts.add(statusClause("=",
                            Downloads.STATUS_QUEUED_FOR_WIFI));
                }
                if ((mStatusFlags & STATUS_SUCCESSFUL) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_SUCCESS));
                }
                if ((mStatusFlags & STATUS_FAILED) != 0) {
                    parts.add("(" + statusClause(">=", 400) + " AND "
                            + statusClause("<", 600) + ")");
                }
                if ((mStatusFlags & STATUS_INSTALLING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_INSTALLING));
                }
                if ((mStatusFlags & STATUS_PATCHING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_PATCHING));
                }
                selectionParts.add("(" + joinStrings(" OR ", parts) + ")");
            }

            String selection = joinStrings(" AND ", selectionParts);
            String[] selectionArgs = getStringArray(selectionArgList);
            String orderDirection = (mOrderDirection == ORDER_ASCENDING ? "ASC"
                    : "DESC");
            String orderBy = mOrderByColumn + " " + orderDirection;

            return resolver.query(uri, projection, selection, selectionArgs,
                    orderBy);
        }

        private String joinStrings(String joiner, Iterable<String> parts) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String part : parts) {
                if (!first) {
                    builder.append(joiner);
                }
                builder.append(part);
                first = false;
            }
            return builder.toString();
        }

        private String statusClause(String operator, int value) {
            return Downloads.COLUMN_STATUS + operator + "'" + value + "'";
        }
    }

    /**
     * This class wraps a cursor returned by DownloadProvider -- the
     * "underlying cursor" -- and presents a different set of columns, those
     * defined in the DownloadManager.COLUMN_* constants. Some columns
     * correspond directly to underlying values while others are computed from
     * underlying data.
     */
    private static class CursorTranslator extends CursorWrapper {
        public CursorTranslator(Cursor cursor) {
            super(cursor);
        }

        @Override
        public int getColumnIndex(String columnName) {
            return Arrays.asList(COLUMNS).indexOf(columnName);
        }

        @Override
        public int getColumnIndexOrThrow(String columnName)
                throws IllegalArgumentException {
            int index = getColumnIndex(columnName);
            if (index == -1) {
                throw new IllegalArgumentException("No such column: "
                        + columnName);
            }
            return index;
        }

        @Override
        public String getColumnName(int columnIndex) {
            int numColumns = COLUMNS.length;
            if (columnIndex < 0 || columnIndex >= numColumns) {
                throw new IllegalArgumentException("Invalid column index "
                        + columnIndex + ", " + numColumns + " columns exist");
            }
            return COLUMNS[columnIndex];
        }

        @Override
        public String[] getColumnNames() {
            String[] returnColumns = new String[COLUMNS.length];
            System.arraycopy(COLUMNS, 0, returnColumns, 0, COLUMNS.length);
            return returnColumns;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(int columnIndex) {
            return getLong(columnIndex);
        }

        private boolean isLongColumn(String column) {
            return LONG_COLUMNS.contains(column);
        }

        @Override
        public float getFloat(int columnIndex) {
            return (float) getDouble(columnIndex);
        }

        @Override
        public int getInt(int columnIndex) {
            return (int) getLong(columnIndex);
        }

        @Override
        public long getLong(int columnIndex) {
            return translateLong(getColumnName(columnIndex));
        }

        @Override
        public short getShort(int columnIndex) {
            return (short) getLong(columnIndex);
        }

        @Override
        public String getString(int columnIndex) {
            return translateString(getColumnName(columnIndex));
        }

        private String translateString(String column) {
            if (isLongColumn(column)) {
                return Long.toString(translateLong(column));
            }
            if (column.equals(COLUMN_TITLE)) {
                return getUnderlyingString(Downloads.COLUMN_TITLE);
            }
            if (column.equals(COLUMN_URI)) {
                return getUnderlyingString(Downloads.COLUMN_URI);
            }
            if (column.equals(COLUMN_APP_LABEL)) {
                return getUnderlyingString(Downloads.COLUMN_APP_LABEL);
            }
            if (column.equals(COLUMN_APP_PKG_NAME)) {
                return getUnderlyingString(Downloads.COLUMN_APP_PKG_NAME);
            }
            if (column.equals(COLUMN_APP_ICON_URL)) {
                return getUnderlyingString(Downloads.COLUMN_APP_iCON_URL);
            }
            if (column.equals(COLUMN_APP_VERSION_NAME)) {
                return getUnderlyingString(Downloads.COLUMN_APP_VERSION_NAME);
            }
            if (column.equals(COLUMN_MD5)) {
                return getUnderlyingString(Downloads.COLUMN_MD5);
            }
            if (column.equals(COLUMN_FLOW_FREE_STATE_V2)) {
                return getUnderlyingString(Downloads.COLUMN_FLOW_FREE_STATE_V2);
            }

            if (column.equals(COLUMN_APP_TYPE)) {
                return getUnderlyingString(Downloads.COLUMN_APP_TYPE);
            }
            if (column.equals(COLUMN_DOWNLOAD_HINT)) {
                return getUnderlyingString(Downloads.COLUMN_FILE_NAME_HINT);
            }
            assert column.equals(COLUMN_LOCAL_URI);
            return getLocalUri();
        }

        private String getLocalUri() {
            String localPath = getUnderlyingString(Downloads._DATA);
            if (localPath == null) {
                return null;
            }
            return Uri.fromFile(new File(localPath)).toString();
        }

        private long translateLong(String column) {
            if (!isLongColumn(column)) {
                // mimic behavior of underlying cursor -- most likely, throw
                // NumberFormatException
                return Long.valueOf(translateString(column));
            }

            if (column.equals(COLUMN_ID)) {
                return getUnderlyingLong(Downloads._ID);
            }
            if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
                return getUnderlyingLong(Downloads.COLUMN_TOTAL_BYTES);
            }
            if (column.equals(COLUMN_STATUS)) {
                return translateStatus((int) getUnderlyingLong(Downloads.COLUMN_STATUS));
            }
            if (column.equals(COLUMN_REASON)) {
                return getReason((int) getUnderlyingLong(Downloads.COLUMN_STATUS));
            }
            if (column.equals(COLUMN_BYTES_DOWNLOADED_SO_FAR)) {
                return getUnderlyingLong(Downloads.COLUMN_CURRENT_BYTES);
            }
            if (column.equals(COLUMN_TOTAL_SIZE_BYTES_DEFAULT)) {
                return getUnderlyingLong(Downloads.COLUMN_TOTAL_BYTES_DEFAULT);
            }
            if (column.equals(COLUMN_APP_ID)) {
                return getUnderlyingLong(Downloads.COLUMN_APP_ID);
            }
            if (column.equals(COLUMN_APP_VERSION_CODE)) {
                return getUnderlyingLong(Downloads.COLUMN_APP_VERSION_CODE);
            }
            if (column.equals(COLUMN_BYTES_IN_WIFI)) {
                return getUnderlyingLong(COLUMN_BYTES_IN_WIFI);
            }
            if (column.equals(COLUMN_BYTES_IN_3G)) {
                return getUnderlyingLong(COLUMN_BYTES_IN_3G);
            }

            assert column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP);
            return getUnderlyingLong(Downloads.COLUMN_LAST_MODIFICATION);
        }

        private long getReason(int status) {
            switch (translateStatus(status)) {
                case STATUS_FAILED:
                    return getErrorCode(status);

                case STATUS_PAUSED:
                    return getPausedReason(status);
                default:
                    return 0; // arbitrary value when status is not an error
            }
        }

        private long getPausedReason(int status) {
            switch (status) {
                case Downloads.STATUS_WAITING_TO_RETRY:
                    return PAUSED_WAITING_TO_RETRY;

                case Downloads.STATUS_WAITING_FOR_NETWORK:
                    return PAUSED_WAITING_FOR_NETWORK;

                case Downloads.STATUS_QUEUED_FOR_WIFI:
                    return PAUSED_QUEUED_FOR_WIFI;

                default:
                    return PAUSED_UNKNOWN;
            }
        }

        private long getErrorCode(int status) {
            if ((400 <= status && status < Downloads.STATUS_NO_MATCH_MD5)
                    || (500 <= status && status < 600)) {
                // HTTP status code
                return status;
            }

            switch (status) {
                case Downloads.STATUS_FILE_ERROR:
                    return ERROR_FILE_ERROR;

                case Downloads.STATUS_UNHANDLED_HTTP_CODE:
                case Downloads.STATUS_UNHANDLED_REDIRECT:
                    return ERROR_UNHANDLED_HTTP_CODE;

                case Downloads.STATUS_HTTP_DATA_ERROR:
                    return ERROR_HTTP_DATA_ERROR;

                case Downloads.STATUS_TOO_MANY_REDIRECTS:
                    return ERROR_TOO_MANY_REDIRECTS;

                case Downloads.STATUS_INSUFFICIENT_SPACE_ERROR:
                    return ERROR_INSUFFICIENT_SPACE;

                case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
                    return ERROR_DEVICE_NOT_FOUND;

                case Downloads.STATUS_CANNOT_RESUME:
                    return ERROR_CANNOT_RESUME;

                case Downloads.STATUS_FILE_ALREADY_EXISTS_ERROR:
                    return ERROR_FILE_ALREADY_EXISTS;
                case Downloads.STATUS_NO_MATCH_MD5:
                    return ERROR_FILE_MD5_NOT_MATCH;
                case Downloads.STATUS_PATCHING_FAILED:
                    return ERROR_PATCHING_FILE_ERROR;
                default:
                    return ERROR_UNKNOWN;
            }
        }

        private long getUnderlyingLong(String column) {
            return super.getLong(super.getColumnIndex(column));
        }

        private String getUnderlyingString(String column) {
            return super.getString(super.getColumnIndex(column));
        }

        private int translateStatus(int status) {
            switch (status) {
                case Downloads.STATUS_PENDING:
                    return STATUS_PENDING;

                case Downloads.STATUS_PENDING_FOR_WIFI:
                    return STATUS_PENDING_FOR_WIFI;

                case Downloads.STATUS_RUNNING:
                    return STATUS_RUNNING;

                case Downloads.STATUS_PAUSED_BY_APP:
                case Downloads.STATUS_WAITING_TO_RETRY:
                case Downloads.STATUS_WAITING_FOR_NETWORK:
                case Downloads.STATUS_QUEUED_FOR_WIFI:
                    return STATUS_PAUSED;

                case Downloads.STATUS_SUCCESS:
                    return STATUS_SUCCESSFUL;
                case Downloads.STATUS_INSTALLING:
                    return STATUS_INSTALLING;
                case Downloads.STATUS_PATCHING:
                    return STATUS_PATCHING;
                default:
                    assert Downloads.isStatusError(status);
                    return STATUS_FAILED;
            }
        }
    }
}
