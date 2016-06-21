/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Allows application to interact with the download manager.
 */
public final class DownloadProvider extends ContentProvider {
    /**
     * URI matcher used to recognize URIs sent by applications
     */
    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /**
     * URI matcher constant for the URI of all downloads in the system
     */
    private static final int ALL_DOWNLOADS = 1;
    /**
     * URI matcher constant for the URI of an individual download
     */
    public static final int ALL_DOWNLOADS_ID = 2;
    /**
     * Database filename
     */
    private static final String DB_NAME = "downloads.db";
    /**
     * Current database version
     */
    private static final int DB_VERSION = 116;
    /**
     * Name of table in the database
     */
    private static final String DB_TABLE = "downloads";
    public static final String CONTENT_PATH = "snail_downloads";
    private static final String[] sAppReadableColumnsArray = new String[]{
            Downloads._ID,
            Downloads._DATA,
            Downloads.COLUMN_CONTROL,
            Downloads.COLUMN_STATUS,
            Downloads.COLUMN_LAST_MODIFICATION,
            Downloads.COLUMN_TOTAL_BYTES,
            Downloads.COLUMN_CURRENT_BYTES,
            Downloads.COLUMN_TITLE,
            Downloads.COLUMN_URI,
            Downloads.COLUMN_FILE_NAME_HINT,
            Downloads.COLUMN_APP_ID
    };

    /**
     * URI matcher constant for the URI of a download's request headers
     */
    static {
        sURIMatcher.addURI(Downloads.AUTHORITY, CONTENT_PATH, ALL_DOWNLOADS);
        sURIMatcher.addURI(Downloads.AUTHORITY, CONTENT_PATH + "/#", ALL_DOWNLOADS_ID);
    }


    private static HashSet<String> sAppReadableColumnsSet;

    static {
        sAppReadableColumnsSet = new HashSet<String>();
        Collections.addAll(sAppReadableColumnsSet, sAppReadableColumnsArray);
    }

    /**
     * The database that lies underneath this content provider
     */
    private SQLiteOpenHelper mOpenHelper;
    private Context mContext;
    private ContentResolver mContentResolver;

    /**
     * Initializes the content provider when it is created.
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();
        if (mContext == null) {
            Log.e(Constants.TAG, "couldn't get context");
            return false;
        }

        mContentResolver = mContext.getContentResolver();
        if (mContentResolver == null) {
            Log.e(Constants.TAG, "couldn't get context resolver");
            return false;
        }

        mOpenHelper = new DatabaseHelper(mContext);
        return true;
    }

    /**
     * Returns the content-provider-style MIME types of the various types
     * accessible through this content provider.
     */
    @Override
    public String getType(final Uri uri) {
        return null;
    }

    /**
     * Inserts a row in the database
     */
    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        checkInsertPermissions(values);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db == null) {
            Log.e(Constants.TAG, "couldn't get writable database");
            return null;
        }

        // note we disallow inserting into ALL_DOWNLOADS
        int match = sURIMatcher.match(uri);
        // 预约wifi下载
        if (!values.containsKey(Downloads.COLUMN_STATUS))
            values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
        values.put(Downloads.COLUMN_LAST_MODIFICATION, System.currentTimeMillis());
        values.put(Downloads.COLUMN_TOTAL_BYTES, -1);
        values.put(Downloads.COLUMN_CURRENT_BYTES, 0);
        long rowID = db.insert(DB_TABLE, null, values);
        if (rowID == -1) {
            LogUtils.e("couldn't insert into downloads database");
            return null;
        }

        DownloadService.start(mContext);
        notifyContentChanged(uri, match, new String[]{String.valueOf(rowID)});
        return ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, rowID);
    }

    /**
     * Apps with the ACCESS_DOWNLOAD_MANAGER permission can access this provider
     * freely, subject to constraints in the rest of the code. Apps without that
     * may still access this provider through the public API, but additional
     * restrictions are imposed. We check those restrictions here.
     *
     * @param values ContentValues provided to insert()
     * @throws SecurityException if the caller has insufficient permissions
     */
    private void checkInsertPermissions(ContentValues values) {
        if (mContext.checkCallingOrSelfPermission(Downloads.PERMISSION_ACCESS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mContext.enforceCallingOrSelfPermission(android.Manifest.permission.INTERNET,
                "INTERNET permission is required to use the download manager");


        // ensure the request fits within the bounds of a public API request
        // first copy so we can remove values
        values = new ContentValues(values);

        // remove the rest of the columns that are allowed (with any value)
        values.remove(Downloads.COLUMN_URI);
        values.remove(Downloads.COLUMN_TITLE);
        values.remove(Downloads.COLUMN_FILE_NAME_HINT); // checked later in
        // later in
        // insert()
        values.remove(Downloads.COLUMN_ALLOWED_NETWORK_TYPES);
        // snail app columns
        values.remove(Downloads.COLUMN_TOTAL_BYTES_DEFAULT);
        values.remove(Downloads.COLUMN_APP_ID);
        values.remove(Downloads.COLUMN_APP_LABEL);
        values.remove(Downloads.COLUMN_APP_PKG_NAME);
        values.remove(Downloads.COLUMN_APP_iCON_URL);
        values.remove(Downloads.COLUMN_APP_VERSION_NAME);
        values.remove(Downloads.COLUMN_APP_VERSION_CODE);
        values.remove(Downloads.COLUMN_BYTES_IN_WIFI);
        values.remove(Downloads.COLUMN_BYTES_IN_3G);
        values.remove(Downloads.COLUMN_MD5);
        values.remove(Downloads.COLUMN_FLOW_FREE_STATE);
        values.remove(Downloads.COLUMN_FREE_AREA_STATE);
        values.remove(Downloads.COLUMN_FLOW_FREE_STATE_V2);
        values.remove(Downloads.COLUMN_APP_TYPE);
        values.remove(Downloads.COLUMN_PATCH_TYPE);
        values.remove(Downloads.COLUMN_DIFF_URL);
        values.remove(Downloads.COLUMN_DIFF_SIZE);
        values.remove(Downloads.COLUMN_DIFF_MD5);
        values.remove(Downloads.COLUMN_STATUS);
        // any extra columns are extraneous and disallowed
        if (values.size() > 0) {
            StringBuilder error = new StringBuilder("Invalid columns in request: ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : values.valueSet()) {
                if (!first) {
                    error.append(", ");
                }
                first = false;
                error.append(entry.getKey());
            }
            throw new SecurityException(error.toString());
        }
    }

    /**
     * Starts a database query
     */
    @Override
    public Cursor query(final Uri uri, String[] projection,
                        final String selection, final String[] selectionArgs,
                        final String sort) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if (db == null) {
            LogUtils.e("couldn't get readable database");
            return null;
        }

        int match = sURIMatcher.match(uri);
        if (match == -1) {
            if (Constants.LOGV) {
                Log.v(Constants.TAG, "querying unknown URI: " + uri);
            }
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);

        if (Constants.LOGVV) {
            logVerboseQueryInfo(projection, selection, selectionArgs, sort, db);
        }

        Cursor ret = db.query(DB_TABLE, projection, fullSelection.getSelection(),
                fullSelection.getParameters(), null, null, sort);
        if (ret != null) {
            ret.setNotificationUri(mContentResolver, uri);
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "created cursor " + ret + " on behalf of " + Binder.getCallingPid());
            }
        } else {
            if (Constants.LOGV) {
                Log.v(Constants.TAG, "query failed in downloads database");
            }
        }

        return ret;
    }


    private String getDownloadIdFromUri(final Uri uri) {
        return uri.getPathSegments().get(1);
    }


    /**
     * Updates a row in the database
     */
    @Override
    public int update(final Uri uri, final ContentValues values,
                      final String where, final String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db == null) {
            LogUtils.e("couldn't get writable database");
            return 0;
        }

        int count;
        ContentValues filteredValues;
        filteredValues = values;
        String filename = values.getAsString(Downloads._DATA);
        if (filename != null) {
            Cursor c = query(uri, new String[]{Downloads.COLUMN_TITLE},
                    null, null, null);
            if (!c.moveToFirst() || c.getString(0).length() == 0) {
                values.put(Downloads.COLUMN_TITLE,
                        new File(filename).getName());
            }
            c.close();
        }

        Integer status = values.getAsInteger(Downloads.COLUMN_STATUS);
        boolean isRestart = status != null && status == Downloads.STATUS_PENDING;
        int match = sURIMatcher.match(uri);
        switch (match) {
            case ALL_DOWNLOADS:
            case ALL_DOWNLOADS_ID:
                SqlSelection selection = getWhereClause(uri, where, whereArgs, match);
                if (filteredValues.size() > 0) {
                    count = db.update(DB_TABLE, filteredValues,
                            selection.getSelection(), selection.getParameters());
                } else {
                    count = 0;
                }
                break;

            default:
                LogUtils.e("updating unknown/invalid URI: " + uri);
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }

        String notifyExtras = filteredValues.getAsString(Downloads.COLUMN_NOTIFICATION_EXTRAS);
        if (TextUtils.isEmpty(notifyExtras)) notifyContentChanged(uri, match, whereArgs);

        if (isRestart) {
            DownloadService.start(mContext);
        }
        return count;
    }

    /**
     * Notify of a change through both URIs (/my_downloads and /all_downloads)
     *
     * @param uri      either URI for the changed download(s)
     * @param uriMatch the match ID from {@link #sURIMatcher}
     */
    private void notifyContentChanged(final Uri uri, int uriMatch, String[] whereArgs) {
        Long downloadId;
        if (uriMatch == ALL_DOWNLOADS_ID) {
            downloadId = Long.parseLong(getDownloadIdFromUri(uri));
            Uri uriToNotify = ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, downloadId);
            mContentResolver.notifyChange(uriToNotify, null);
        } else if (whereArgs != null) {
            for (String id : whereArgs) {
                downloadId = Long.parseLong(id);
                Uri uriToNotify = ContentUris.withAppendedId(Downloads.ALL_DOWNLOADS_CONTENT_URI, downloadId);
                mContentResolver.notifyChange(uriToNotify, null);
            }
        }
    }

    private SqlSelection getWhereClause(final Uri uri, final String where,
                                        final String[] whereArgs, int uriMatch) {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        if (uriMatch == ALL_DOWNLOADS_ID) {
            selection.appendClause(Downloads._ID + " = ?", getDownloadIdFromUri(uri));
        }
        return selection;
    }

    /**
     * Deletes a row in the database
     */
    @Override
    public int delete(final Uri uri, final String where,
                      final String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db == null) {
            Log.e(Constants.TAG, "couldn't get writable database");
            return 0;
        }

        int count;
        int match = sURIMatcher.match(uri);
        switch (match) {
            case ALL_DOWNLOADS:
            case ALL_DOWNLOADS_ID:
                SqlSelection selection = getWhereClause(uri, where, whereArgs, match);
                count = db.delete(DB_TABLE, selection.getSelection(), selection.getParameters());
                break;

            default:
                Log.d(Constants.TAG, "deleting unknown/invalid URI: " + uri);
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
        notifyContentChanged(uri, match, null);
        return count;
    }

    private void logVerboseQueryInfo(String[] projection,
                                     final String selection, final String[] selectionArgs,
                                     final String sort, SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("starting query, database is ");
        if (db != null) {
            sb.append("not ");
        }
        sb.append("null; ");
        if (projection == null) {
            sb.append("projection is null; ");
        } else if (projection.length == 0) {
            sb.append("projection is empty; ");
        } else {
            for (int i = 0; i < projection.length; ++i) {
                sb.append("projection[");
                sb.append(i);
                sb.append("] is ");
                sb.append(projection[i]);
                sb.append("; ");
            }
        }
        sb.append("selection is ");
        sb.append(selection);
        sb.append("; ");
        if (selectionArgs == null) {
            sb.append("selectionArgs is null; ");
        } else if (selectionArgs.length == 0) {
            sb.append("selectionArgs is empty; ");
        } else {
            for (int i = 0; i < selectionArgs.length; ++i) {
                sb.append("selectionArgs[");
                sb.append(i);
                sb.append("] is ");
                sb.append(selectionArgs[i]);
                sb.append("; ");
            }
        }
        sb.append("sort is ");
        sb.append(sort);
        sb.append(".");
        LogUtils.d(sb.toString());
    }

    /**
     * This class encapsulates a SQL where clause and its parameters. It makes
     * it possible for shared methods (like
     * {@link DownloadProvider#getWhereClause(android.net.Uri, String, String[], int)}) to
     * return both pieces of information, and provides some utility logic to
     * ease piece-by-piece construction of selections.
     */
    private static class SqlSelection {
        public StringBuilder mWhereClause = new StringBuilder();
        public List<String> mParameters = new ArrayList<String>();

        public <T> void appendClause(String newClause, final T... parameters) {
            if (newClause == null || newClause.length() == 0) {
                return;
            }
            if (mWhereClause.length() != 0) {
                mWhereClause.append(" AND ");
            }
            mWhereClause.append("(");
            mWhereClause.append(newClause);
            mWhereClause.append(")");
            if (parameters != null) {
                for (Object parameter : parameters) {
                    mParameters.add(parameter.toString());
                }
            }
        }

        public String getSelection() {
            return mWhereClause.toString();
        }

        public String[] getParameters() {
            String[] array = new String[mParameters.size()];
            return mParameters.toArray(array);
        }
    }

    /**
     * Creates and updated database on demand when opening it. Helper class to
     * create database the first time the provider is initialized and upgrade it
     * when a new version of the provider needs an updated version of the
     * database.
     */
    private final class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        /**
         * Creates database the first time we try to open it.
         */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "populating new database");
            }
            onUpgrade(db, 0, DB_VERSION);
        }

        /**
         * Updates the database format when a content provider is used with a
         * database that was created with a different format.
         * <p/>
         * Note: to support downgrades, creating a table should always drop it
         * first if it already exists.
         */
        @Override
        public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {

            if (oldV == 31) {
                // 31 and 100 are identical, just in different codelines.
                // Upgrading from 31 is the
                // same as upgrading from 100.
                oldV = 100;
            } else if (oldV < 100) {
                // no logic to upgrade from these older version, just recreate
                // the DB
                Log.i(Constants.TAG,
                        "Upgrading downloads database from version " + oldV
                                + " to version " + newV
                                + ", which will destroy all old data"
                );
                oldV = 99;
            } else if (oldV > newV) {
                // user must have downgraded software; we have no way to know
                // how to downgrade the
                // DB, so just recreate it
                Log.i(Constants.TAG,
                        "Downgrading downloads database from version " + oldV
                                + " (current version is " + newV
                                + "), destroying all old data"
                );
                oldV = 99;
            }
            for (int version = oldV + 1; version <= newV; version++) {
                upgradeTo(db, version);
            }
        }

        /**
         * Upgrade database from (version - 1) to version.
         */
        @SuppressWarnings("deprecation")
        private void upgradeTo(SQLiteDatabase db, int version) {
            switch (version) {
                case 100:
                    createDownloadsTable(db);
                    break;

                case 101:
                    createHeadersTable(db);
                    break;

                case 102:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_IS_PUBLIC_API,
                            "INTEGER NOT NULL DEFAULT 0");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_ALLOW_ROAMING,
                            "INTEGER NOT NULL DEFAULT 0");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_ALLOWED_NETWORK_TYPES,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;

                case 103:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI,
                            "INTEGER NOT NULL DEFAULT 1");
                    break;

                case 104:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;

                case 105:
                    fillNullValues(db);
                    break;

                case 106:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_DELETED,
                            "BOOLEAN NOT NULL DEFAULT 0");
                    break;

                case 107:
                    deleteAllHistoryData(db);
                    addColumn(db, DB_TABLE, Downloads.COLUMN_TOTAL_BYTES_DEFAULT,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;
                case 108:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_ID,
                            "INTEGER NOT NULL DEFAULT 0");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_LABEL,
                            "TEXT NOT NULL DEFAULT 'UNKNOWN'");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_PKG_NAME, "TEXT");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_iCON_URL, "TEXT");
                    break;
                case 109:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_VERSION_NAME,
                            "TEXT NOT NULL DEFAULT 'UNKNOWN'");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_VERSION_CODE,
                            "INTEGER NOT NULL DEFAULT 0");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_BYTES_IN_WIFI, "INTEGER");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_BYTES_IN_3G, "INTEGER");
                    break;
                case 110:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_MD5, "TEXT");
                    break;
                case 111:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_FLOW_FREE_STATE,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;
                case 112:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_FREE_AREA_STATE,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;
                case 113:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_FLOW_FREE_STATE_V2,
                            "TEXT NOT NULL DEFAULT '0000'");
                    fillFlowFree(db);
                    break;

                case 114:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_APP_TYPE,
                            "TEXT NOT NULL DEFAULT 'UNKNOWN'");
                    break;
                case 115:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_PATCH_TYPE,
                            "INTEGER NOT NULL DEFAULT 0");
                    break;
                case 116:
                    addColumn(db, DB_TABLE, Downloads.COLUMN_DIFF_URL,
                            "TEXT");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_DIFF_SIZE,
                            "INTEGER NOT NULL DEFAULT 0");
                    addColumn(db, DB_TABLE, Downloads.COLUMN_DIFF_MD5,
                            "TEXT");
                    break;
                default:
                    throw new IllegalStateException("Don't know how to upgrade to "
                            + version);
            }
        }

        private void deleteAllHistoryData(SQLiteDatabase db) {
            db.execSQL("delete from " + DB_TABLE);
        }

        /**
         * insert() now ensures these four columns are never null for new
         * downloads, so this method makes that true for existing columns, so
         * that code can rely on this assumption.
         */
        @SuppressWarnings("deprecation")
        private void fillNullValues(SQLiteDatabase db) {
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_CURRENT_BYTES, 0);
            fillNullValuesForColumn(db, values);
            values.put(Downloads.COLUMN_TOTAL_BYTES, -1);
            fillNullValuesForColumn(db, values);
            values.put(Downloads.COLUMN_TITLE, "");
            fillNullValuesForColumn(db, values);
            values.put(Downloads.COLUMN_DESCRIPTION, "");
            fillNullValuesForColumn(db, values);
        }

        private void fillNullValuesForColumn(SQLiteDatabase db,
                                             ContentValues values) {
            String column = values.valueSet().iterator().next().getKey();
            db.update(DB_TABLE, values, column + " is null", null);
            values.clear();
        }

        private void fillFlowFree(SQLiteDatabase db) {
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_FLOW_FREE_STATE_V2, AppInfo.FREE_DOWNLOAD_AREA);
            db.update(DB_TABLE, values, null, null);
        }

        /**
         * Add a column to a table using ALTER TABLE.
         *
         * @param dbTable          name of the table
         * @param columnName       name of the column to add
         * @param columnDefinition SQL for the column definition
         */
        private void addColumn(SQLiteDatabase db, String dbTable,
                               String columnName, String columnDefinition) {
            db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName
                    + " " + columnDefinition);
        }


        /**
         * Creates the table that'll hold the download information.
         */
        @SuppressWarnings("deprecation")
        private void createDownloadsTable(SQLiteDatabase db) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
                db.execSQL("CREATE TABLE " + DB_TABLE + "("
                        + Downloads._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Downloads.COLUMN_URI + " TEXT, "
                        + Constants.RETRY_AFTER_X_REDIRECT_COUNT + " INTEGER, "
                        + Downloads.COLUMN_APP_DATA + " TEXT, "
                        + Downloads.COLUMN_NO_INTEGRITY + " BOOLEAN, "
                        + Downloads.COLUMN_FILE_NAME_HINT + " TEXT, "
                        + Constants.OTA_UPDATE + " BOOLEAN, "
                        + Downloads._DATA + " TEXT, "
                        + Downloads.COLUMN_MIME_TYPE + " TEXT, "
                        + Downloads.COLUMN_DESTINATION + " INTEGER, "
                        + Constants.NO_SYSTEM_FILES + " BOOLEAN, "
                        + Downloads.COLUMN_VISIBILITY + " INTEGER, "
                        + Downloads.COLUMN_CONTROL + " INTEGER, "
                        + Downloads.COLUMN_STATUS + " INTEGER, "
                        + Constants.FAILED_CONNECTIONS + " INTEGER, "
                        + Downloads.COLUMN_LAST_MODIFICATION + " BIGINT, "
                        + Downloads.COLUMN_NOTIFICATION_PACKAGE + " TEXT, "
                        + Downloads.COLUMN_NOTIFICATION_CLASS + " TEXT, "
                        + Downloads.COLUMN_NOTIFICATION_EXTRAS + " TEXT, "
                        + Downloads.COLUMN_COOKIE_DATA + " TEXT, "
                        + Downloads.COLUMN_USER_AGENT + " TEXT, "
                        + Downloads.COLUMN_REFERER + " TEXT, "
                        + Downloads.COLUMN_TOTAL_BYTES + " INTEGER, "
                        + Downloads.COLUMN_CURRENT_BYTES + " INTEGER, "
                        + Constants.ETAG + " TEXT, "
                        + Constants.UID + " INTEGER, "
                        + Downloads.COLUMN_OTHER_UID + " INTEGER, "
                        + Downloads.COLUMN_TITLE + " TEXT, "
                        + Downloads.COLUMN_DESCRIPTION + " TEXT); ");
            } catch (SQLException ex) {
                LogUtils.e("couldn't create table in downloads database");
                throw ex;
            }
        }
        @SuppressWarnings("deprecation")
        private void createHeadersTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + Downloads.RequestHeaders.HEADERS_DB_TABLE);
            db.execSQL("CREATE TABLE " + Downloads.RequestHeaders.HEADERS_DB_TABLE + "("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Downloads.RequestHeaders.COLUMN_DOWNLOAD_ID + " INTEGER NOT NULL,"
                    + Downloads.RequestHeaders.COLUMN_HEADER + " TEXT NOT NULL,"
                    + Downloads.RequestHeaders.COLUMN_VALUE + " TEXT NOT NULL" + ");");
        }
    }

    /**
     * Remotely opens a file
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (Constants.LOGVV) {
            logVerboseOpenFileInfo(uri, mode);
        }

        Cursor cursor = query(uri, new String[]{"_data"}, null, null, null);
        String path;
        try {
            int count = (cursor != null) ? cursor.getCount() : 0;
            if (count != 1) {
                // If there is not exactly one result, throw an appropriate
                // exception.
                if (count == 0) {
                    throw new FileNotFoundException("No entry for " + uri);
                }
                throw new FileNotFoundException("Multiple items at " + uri);
            }

            cursor.moveToFirst();
            path = cursor.getString(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (path == null) {
            throw new FileNotFoundException("No filename found.");
        }
        if (!Helpers.isFilenameValid(path)) {
            throw new FileNotFoundException("Invalid filename.");
        }
        if (!"r".equals(mode)) {
            throw new FileNotFoundException("Bad mode for " + uri + ": " + mode);
        }

        ParcelFileDescriptor ret = ParcelFileDescriptor.open(new File(path),
                ParcelFileDescriptor.MODE_READ_ONLY);

        if (ret == null) {
            if (Constants.LOGV) {
                Log.v(Constants.TAG, "couldn't open file");
            }
            throw new FileNotFoundException("couldn't open file");
        }
        return ret;
    }

    private void logVerboseOpenFileInfo(Uri uri, String mode) {
        Log.v(Constants.TAG, "openFile uri: " + uri + ", mode: " + mode
                + ", uid: " + Binder.getCallingUid());
        Cursor cursor = query(Downloads.ALL_DOWNLOADS_CONTENT_URI, new String[]{"_id"},
                null, null, "_id");
        if (cursor == null) {
            Log.v(Constants.TAG, "null cursor in openFile");
        } else {
            if (!cursor.moveToFirst()) {
                Log.v(Constants.TAG, "empty cursor in openFile");
            } else {
                do {
                    Log.v(Constants.TAG, "row " + cursor.getInt(0) + " available");
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        cursor = query(uri, new String[]{"_data"}, null, null, null);
        if (cursor == null) {
            Log.v(Constants.TAG, "null cursor in openFile");
        } else {
            if (!cursor.moveToFirst()) {
                Log.v(Constants.TAG, "empty cursor in openFile");
            } else {
                String filename = cursor.getString(0);
                Log.v(Constants.TAG, "filename in openFile: " + filename);
                if (new File(filename).isFile()) {
                    Log.v(Constants.TAG, "file exists in openFile");
                }
            }
            cursor.close();
        }
    }

}
