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

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.http.AndroidHttpClient;
import android.os.PowerManager;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.snailgame.cjg.common.db.dao.TrafficStaticInfo;
import com.snailgame.cjg.exception.MD5NoMatchException;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.statistics.TrafficStatisticsUtil;
import com.snailgame.cjg.util.ApkUtils;
import com.snailgame.cjg.util.HostUtil;
import com.snailgame.cjg.util.MD5Util;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.SignUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.PatchUtils;
import com.testin.agent.TestinAgent;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Runs an actual download
 */
public class DownloadThread extends Thread {

    private Context mContext;
    private DownloadInfo mInfo;
    private SystemFacade mSystemFacade;

    public DownloadThread(Context context, SystemFacade systemFacade, DownloadInfo info) {
        mContext = context;
        mSystemFacade = systemFacade;
        mInfo = info;
    }

    /**
     * Executes the download in a separate thread
     */
    @Override
    public void run() {
        LogUtils.d("DownloadThread run!!!@");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        State state = new State(mInfo);
        AndroidHttpClient client = null;
        PowerManager.WakeLock wakeLock = null;
        int finalStatus = Downloads.STATUS_UNKNOWN_ERROR;

        try {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.TAG);
            wakeLock.acquire();

            if (Constants.LOGV) {
                Log.v(Constants.TAG, "initiating download for " + mInfo.mUri);
            }

            client = AndroidHttpClient.newInstance(GlobalVar.getInstance().getUserAgent(FreeStoreApp.getContext()), mContext);

            boolean finished = false;
            while (!finished) {
                Log.i(Constants.TAG, "Initiating request for download " + mInfo.mId);
                state.mRequestUri = HostUtil.replaceUrl(state.mRequestUri, state.iFreeArea, state.cFreeFlow);
                LogUtils.i("download -->" + state.mRequestUri);
                HttpGet request = new HttpGet(state.mRequestUri);
                try {
                    executeDownload(state, client, request);
                    finished = true;
                } catch (RetryDownload exc) {
                    // fall through
                } finally {
                    request.abort();
                    request = null;
                }
            }

            if (Constants.LOGV) {
                Log.v(Constants.TAG, "download completed for " + mInfo.mUri);
            }
            finalizeDestinationFile(state);
            finalStatus = Downloads.STATUS_SUCCESS;
        } catch (StopRequest error) {
            // remove the cause before printing, in case it contains PII
            Log.w(Constants.TAG, "Aborting request for download " + mInfo.mId + ": " + error.getMessage());
            finalStatus = error.mFinalStatus;
            // fall through to finally block
        } catch (Throwable ex) { // sometimes the socket code throws unchecked
            // exceptions
            Log.w(Constants.TAG, "Exception for id " + mInfo.mId + ": " + ex);
            finalStatus = Downloads.STATUS_UNKNOWN_ERROR;
            // falls through to the code that reports an error
        } finally {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
            if (client != null) {
                client.close();
                client = null;
            }
            if (finalStatus == Downloads.STATUS_SUCCESS) {
                finalStatus = checkMD5AfterDownloadCompleted(finalStatus, state.md5, state.mFilename);
                finalStatus = patchingApk(state, finalStatus);
            }

            if (finalStatus == Downloads.STATUS_NO_MATCH_MD5) {
                StringBuilder sb = new StringBuilder()
                        .append("appId = ").append(mInfo.mAppId)
                        .append(" & appName = ").append(mInfo.mTitle)
                        .append(" & pkgName = ").append(mInfo.mAppPkgName);
                TestinAgent.uploadException(FreeStoreApp.getContext(), sb.toString(), new MD5NoMatchException());
            }
            mInfo.mStatus = finalStatus;
            cleanupDestination(state, finalStatus);
            notifyDownloadCompleted(finalStatus, state.mFilename, state.mNewUri);
            mInfo.mHasActiveThread = false;
        }
    }

    /**
     * 开始差分更新
     *
     * @param state
     * @param finalStatus
     * @return
     */
    private int patchingApk(State state, int finalStatus) {
        if (finalStatus == Downloads.STATUS_SUCCESS && mInfo.cPatchType == AppConstants.UPDATE_TYPE_PATCH) {
            File patchFile = new File(state.mFilename);
            if (ApkUtils.isInstalled(mContext, mInfo.mAppPkgName)) {
                updateDownloadStatus(Downloads.STATUS_PATCHING);

                String newApkPath = patchFile.getAbsolutePath() + DownloadInfo.SUFFIX_PATCHED_APK;
                state.mFilename = newApkPath;
                mInfo.mFileName = newApkPath;

                ContentValues values = new ContentValues();
                values.put(Downloads._DATA, newApkPath);
                mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);

                if (startPatching(newApkPath, patchFile.getAbsolutePath())) {//还原正常原包及差异包大小数据
                    values = new ContentValues();
                    values.put(Downloads.COLUMN_TOTAL_BYTES_DEFAULT, mInfo.iDiffSize);
                    values.put(Downloads.COLUMN_TOTAL_BYTES, mInfo.iDiffSize);
                    values.put(Downloads.COLUMN_DIFF_SIZE, mInfo.mTotalBytesDefault);
                    mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
                } else {
                    LogUtils.i("patch failed");
                    finalStatus = Downloads.STATUS_PATCHING_FAILED;
                    updateDownloadStatus(Downloads.STATUS_PATCHING_FAILED);
                }
            } else {
                LogUtils.i("patch file unverified");
                finalStatus = Downloads.STATUS_PATCHING_FAILED;
                updateDownloadStatus(Downloads.STATUS_PATCHING_FAILED);
            }
            patchFile.delete();
        }
        return finalStatus;
    }

    /**
     * 更新下载状态
     *
     * @param status
     */
    private void updateDownloadStatus(int status) {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_STATUS, status);
        if (status == Downloads.STATUS_PATCHING_FAILED) {
            values.put(Downloads.COLUMN_TOTAL_BYTES_DEFAULT, mInfo.iDiffSize);
            values.put(Downloads.COLUMN_TOTAL_BYTES, mInfo.iDiffSize);
            values.put(Downloads.COLUMN_DIFF_SIZE, mInfo.mTotalBytesDefault);
            values.put(Downloads.COLUMN_PATCH_TYPE, 0);
            // restart download service to send notification
            DownloadService.start(mContext);
        }
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
    }

    /**
     * 合并差分文件
     *
     * @param newApkPath
     * @param patchPath
     */
    private boolean startPatching(String newApkPath, String patchPath) {
        boolean isSucc = false;
        String oldApkSource = ApkUtils.getSourceApkPath(mContext, mInfo.mAppPkgName);

        if (!TextUtils.isEmpty(oldApkSource)) {
            int patchResult = PatchUtils.patch(oldApkSource, newApkPath, patchPath);
            if (patchResult == 0) {
                String signatureNew = SignUtils
                        .getUnInstalledApkSignature(mContext, newApkPath);
                String signatureSource = SignUtils
                        .getInstalledApkSignature(mContext, mInfo.mAppPkgName);
                if (!TextUtils.isEmpty(signatureNew)
                        && !TextUtils.isEmpty(signatureSource)
                        && signatureNew.equals(signatureSource)) {
                    LogUtils.i("Patching success");
                    isSucc = true;
                } else {
                    LogUtils.i("Patching failed with different signature");
                }
            } else {
                LogUtils.i("Patching failed");
            }
        } else {
            LogUtils.i("Found no source apk (PKG: " + mInfo.mAppPkgName + "), must update as whole apk");
        }
        return isSucc;
    }

    private int checkMD5AfterDownloadCompleted(int status, String md5, String filename) {
        if (status == Downloads.STATUS_SUCCESS && !MD5Util.checkMD5(md5, filename)) {
            status = Downloads.STATUS_NO_MATCH_MD5;
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_STATUS, status);
            // reset total bytes
            values.put(Downloads.COLUMN_LAST_MODIFICATION, System.currentTimeMillis());
            values.put(Downloads.COLUMN_TOTAL_BYTES, -1);
            values.put(Downloads.COLUMN_CURRENT_BYTES, 0);
            values.put(Downloads.COLUMN_BYTES_IN_WIFI, 0);
            values.put(Downloads.COLUMN_BYTES_IN_3G, 0);
            mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
        }
        return status;
    }

    /**
     * Fully execute a single download request - setup and send the request,
     * handle the response, and transfer the data to the destination file.
     */
    private void executeDownload(State state, AndroidHttpClient client, HttpGet request)
            throws StopRequest, RetryDownload {
        InnerState innerState = new InnerState();
        byte data[] = new byte[Constants.BUFFER_SIZE];

        setupDestinationFile(state, innerState);
        addRequestHeaders(innerState, request);

        // check just before sending the request to avoid using an invalid
        // connection at all
        checkConnectivity(state);

        HttpResponse response = sendRequest(state, client, request);
        handleExceptionalStatus(state, innerState, response);

        if (Constants.LOGV) {
            Log.v(Constants.TAG, "received response for " + mInfo.mUri);
        }

        processResponseHeaders(state, innerState, response);
        InputStream entityStream = openResponseEntity(state, response);
        transferData(state, innerState, data, entityStream);
    }

    /**
     * Check if current connectivity is valid for this request.
     */
    private void checkConnectivity(State state) throws StopRequest {
        int networkUsable = mInfo.checkCanUseNetwork();
        if (networkUsable != DownloadInfo.NETWORK_OK) {
            int status = Downloads.STATUS_WAITING_FOR_NETWORK;
            if (networkUsable == DownloadInfo.NETWORK_UNUSABLE_DUE_TO_SIZE) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
                mInfo.notifyPauseDueToSize(true);
            } else if (networkUsable == DownloadInfo.NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
                mInfo.notifyPauseDueToSize(false);
            } else if (networkUsable == DownloadInfo.NETWORK_TYPE_DISALLOWED_BY_REQUESTOR) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
            } else if (networkUsable == DownloadInfo.NETWORK_NO_CONNECTION) {
                if (SharedPreferencesUtil.getInstance().isWifiOnly()) {
                    status = Downloads.STATUS_QUEUED_FOR_WIFI;
                }
            } else if (networkUsable == DownloadInfo.NETWORK_WAIT_FOR_WIFI) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
            }
            throw new StopRequest(status,
                    mInfo.getLogMessageForNetworkError(networkUsable));
        }
    }

    /**
     * Transfer as much data as possible from the HTTP response to the
     * destination file.
     *
     * @param data         buffer to use to read data
     * @param entityStream stream for reading the HTTP response entity
     */
    private void transferData(State state, InnerState innerState, byte[] data,
                              InputStream entityStream) throws StopRequest {
        for (; ; ) {
            checkConnectivity(state);
            int bytesRead = readFromResponse(state, innerState, data, entityStream);
            if (bytesRead == -1) { // success, end of stream already reached
                handleEndOfStream(state, innerState);
                return;
            }

            state.mGotData = true;
            writeDataToDestination(state, data, bytesRead);
            innerState.mBytesSoFar += bytesRead;
            reportProgress(state, innerState);

            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "downloaded " + innerState.mBytesSoFar + " for " + mInfo.mUri);
            }

            checkPausedOrCanceled(state);
        }
    }

    /**
     * Called after a successful completion to take any necessary action on the
     * downloaded file.
     */
    private void finalizeDestinationFile(State state) throws StopRequest {
        // make sure the file is readable
        syncDestination(state);
    }

    /**
     * Called just before the thread finishes, regardless of status, to take any
     * necessary action on the downloaded file.
     */
    private void cleanupDestination(State state, int finalStatus) {
        closeDestination(state);
        if (state.mFilename != null && Downloads.isStatusError(finalStatus)) {
            new File(state.mFilename).delete();
            state.mFilename = null;
        }
    }

    /**
     * Sync the destination file to storage.
     */
    private void syncDestination(State state) {
        FileOutputStream downloadedFileStream = null;
        try {
            downloadedFileStream = new FileOutputStream(state.mFilename, true);
            downloadedFileStream.getFD().sync();
        } catch (FileNotFoundException ex) {
            Log.w(Constants.TAG, "file " + state.mFilename + " not found: " + ex);
        } catch (SyncFailedException ex) {
            Log.w(Constants.TAG, "file " + state.mFilename + " sync failed: " + ex);
        } catch (IOException ex) {
            Log.w(Constants.TAG, "IOException trying to sync " + state.mFilename + ": " + ex);
        } catch (RuntimeException ex) {
            Log.w(Constants.TAG, "exception while syncing file: ", ex);
        } finally {
            if (downloadedFileStream != null) {
                try {
                    downloadedFileStream.close();
                } catch (IOException ex) {
                    Log.w(Constants.TAG, "IOException while closing synced file: ", ex);
                } catch (RuntimeException ex) {
                    Log.w(Constants.TAG, "exception while closing file: ", ex);
                }
            }
        }
    }

    /**
     * Close the destination output stream.
     */
    private void closeDestination(State state) {
        try {
            // close the file
            if (state.mStream != null) {
                state.mStream.close();
                state.mStream = null;
            }
        } catch (IOException ex) {
            if (Constants.LOGV) {
                Log.v(Constants.TAG, "exception when closing the file after download : " + ex);
            }
            // nothing can really be done if the file can't be closed
        }
    }

    /**
     * Check if the download has been paused or canceled, stopping the request
     * appropriately if it has been.
     */
    private void checkPausedOrCanceled(State state) throws StopRequest {
        synchronized (mInfo) {
            if (mInfo.mControl == Downloads.CONTROL_PAUSED) {
                throw new StopRequest(Downloads.STATUS_PAUSED_BY_APP, "download paused by owner");
            }
        }
        if (mInfo.mStatus == Downloads.STATUS_CANCELED) {
            throw new StopRequest(Downloads.STATUS_CANCELED, "download canceled");
        }
    }

    /**
     * Report download progress through the database if necessary.
     */
    private void reportProgress(State state, InnerState innerState) {
        long now = mSystemFacade.currentTimeMillis();
        if (innerState.mBytesSoFar - innerState.mBytesNotified > Constants.MIN_PROGRESS_STEP
                && now - innerState.mTimeLastNotification > Constants.MIN_PROGRESS_TIME) {
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_CURRENT_BYTES, innerState.mBytesSoFar);

            // statics flow
            int currNetwork = mSystemFacade.getActiveNetworkType();
            if (currNetwork == ConnectivityManager.TYPE_WIFI) {
                mInfo.mBytesWifi = innerState.mBytesSoFar - mInfo.mBytes3G;
                values.put(Downloads.COLUMN_BYTES_IN_WIFI, mInfo.mBytesWifi);
            } else {
                mInfo.mBytes3G = innerState.mBytesSoFar - mInfo.mBytesWifi;
                values.put(Downloads.COLUMN_BYTES_IN_3G, mInfo.mBytes3G);
            }

            // rectify download task status
            if (mInfo.mStatus != Downloads.STATUS_RUNNING) {
                mInfo.mStatus = Downloads.STATUS_RUNNING;
                values.put(Downloads.COLUMN_STATUS, mInfo.mStatus);
                values.put(Downloads.COLUMN_NOTIFICATION_EXTRAS, Downloads.STATUS_PENDING);
            }

            mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
            innerState.mBytesNotified = innerState.mBytesSoFar;
            innerState.mTimeLastNotification = now;
        }
    }

    /**
     * Write a data buffer to the destination file.
     *
     * @param data      buffer containing the data to write
     * @param bytesRead how many bytes to write from the buffer
     */
    private void writeDataToDestination(State state, byte[] data, int bytesRead)
            throws StopRequest {
        for (; ; ) {
            try {
                if (mInfo.isFileDeleted) {
                    throw new FileNotFoundException("File has been deleted, fileName=" + state.mFilename);
                }
                if (state.mStream == null) {
                    state.mStream = new FileOutputStream(state.mFilename, true);
                }
                state.mStream.write(data, 0, bytesRead);
                return;
            } catch (IOException ex) {
                if (!Helpers.isExternalMediaMounted()) {
                    throw new StopRequest(Downloads.STATUS_DEVICE_NOT_FOUND_ERROR,
                            "external media not mounted while writing destination file");
                }

                long availableBytes = Helpers.getAvailableBytes(Helpers.getFilesystemRoot(state.mFilename));
                if (availableBytes < bytesRead) {
                    throw new StopRequest(Downloads.STATUS_INSUFFICIENT_SPACE_ERROR,
                            "insufficient space while writing destination file", ex);
                }
                throw new StopRequest(Downloads.STATUS_FILE_ERROR, "while writing destination file: " + ex.toString(), ex);
            }
        }
    }

    /**
     * Called when we've reached the end of the HTTP response stream, to update
     * the database and check for consistency.
     */
    private void handleEndOfStream(State state, InnerState innerState)
            throws StopRequest {
        ContentValues values = new ContentValues();
        values.put(Downloads.COLUMN_CURRENT_BYTES, innerState.mBytesSoFar);
        if (innerState.mHeaderContentLength == null) {
            values.put(Downloads.COLUMN_TOTAL_BYTES, innerState.mBytesSoFar);
        }

        TrafficStaticInfo info;
        // statics flow
        int currNetwork = mSystemFacade.getActiveNetworkType();
        if (currNetwork == ConnectivityManager.TYPE_WIFI) {
            mInfo.mBytesWifi = innerState.mBytesSoFar - mInfo.mBytes3G;
            values.put(Downloads.COLUMN_BYTES_IN_WIFI, mInfo.mBytesWifi);

            info = TrafficStatisticsUtil
                    .getTrafficStaticInfo(TrafficStatisticsUtil.NETWORK_TYPE_WIFI,
                            mInfo.mBytesWifi, TrafficStatisticsUtil.STATIC_DOWNLOAD);
        } else {
            mInfo.mBytes3G = innerState.mBytesSoFar - mInfo.mBytesWifi;
            values.put(Downloads.COLUMN_BYTES_IN_3G, mInfo.mBytes3G);
            info = TrafficStatisticsUtil
                    .getTrafficStaticInfo(TrafficStatisticsUtil.getNetworkType(),
                            mInfo.mBytes3G, TrafficStatisticsUtil.STATIC_DOWNLOAD);
        }
        TrafficStatisticsUtil.saveDataToDB(info);

        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);

        boolean lengthMismatched = (innerState.mHeaderContentLength != null)
                && (innerState.mBytesSoFar != Integer.parseInt(innerState.mHeaderContentLength));
        if (lengthMismatched) {
            throw new StopRequest(getFinalStatusForHttpError(state), "closed socket before end of file");
        }
    }

    /**
     * Read some data from the HTTP response stream, handling I/O errors.
     *
     * @param data         buffer to use to read data
     * @param entityStream stream for reading the HTTP response entity
     * @return the number of bytes actually read or -1 if the end of the stream
     * has been reached
     */
    private int readFromResponse(State state, InnerState innerState,
                                 byte[] data, InputStream entityStream) throws StopRequest {
        try {
            return entityStream.read(data);
        } catch (IOException ex) {
            logNetworkState();
            throw new StopRequest(getFinalStatusForHttpError(state),
                    "while reading response: " + ex.toString(), ex);
        }
    }

    /**
     * Open a stream for the HTTP response entity, handling I/O errors.
     *
     * @return an InputStream to read the response entity
     */
    private InputStream openResponseEntity(State state, HttpResponse response)
            throws StopRequest {
        try {
            return response.getEntity().getContent();
        } catch (IOException ex) {
            logNetworkState();
            throw new StopRequest(getFinalStatusForHttpError(state),
                    "while getting entity: " + ex.toString(), ex);
        }
    }

    private void logNetworkState() {
        LogUtils.d("Net " + (Helpers.isNetworkAvailable(mSystemFacade) ? "Up" : "Down"));
    }

    /**
     * Read HTTP response headers and take appropriate action, including setting
     * up the destination file and updating the database.
     */
    private void processResponseHeaders(State state, InnerState innerState,
                                        HttpResponse response) throws StopRequest {


        if (innerState.mContinuingDownload) {
            Header header = response.getFirstHeader("ETag");
            if (header == null || innerState.mHeaderETag == null) {
                throw new StopRequest(Downloads.STATUS_PAUSE_BY_NETWORK, "download paused by ETag not format");
            } else {
                if (!innerState.mHeaderETag.equals(header.getValue()))
                    throw new StopRequest(Downloads.STATUS_PAUSE_BY_NETWORK, "download paused by ETag not format");
            }
            // ignore response headers on resume requests
            return;
        }

        readResponseHeaders(state, innerState, response);

        try {
            state.mFilename = Helpers.generateSaveFile(mInfo.mHint,
                    (innerState.mHeaderContentLength != null) ? Long
                            .parseLong(innerState.mHeaderContentLength) : 0
            );
        } catch (Helpers.GenerateSaveFileError exc) {
            throw new StopRequest(exc.mStatus, exc.mMessage);
        }
        try {

            File downloadFile = new File(state.mFilename.substring(0, state.mFilename.lastIndexOf("/")));
            if (!downloadFile.exists()) {
                downloadFile.mkdir();
            }
            state.mStream = new FileOutputStream(state.mFilename);
        } catch (FileNotFoundException exc) {

            throw new StopRequest(Downloads.STATUS_FILE_ERROR,
                    state.mFilename + ",while opening destination file: " + exc.toString(), exc);
        }
        if (Constants.LOGV) {
            Log.v(Constants.TAG, "writing " + mInfo.mUri + " to " + state.mFilename);
        }

        updateDatabaseFromHeaders(state, innerState);
        // check connectivity again now that we know the total size
        checkConnectivity(state);
    }

    /**
     * Update necessary database fields based on values of HTTP response headers
     * that have been read.
     */
    private void updateDatabaseFromHeaders(State state, InnerState innerState) {
        ContentValues values = new ContentValues();
        values.put(Downloads._DATA, state.mFilename);
        if (innerState.mHeaderETag != null) {
            values.put(Constants.ETAG, innerState.mHeaderETag);
        }
        values.put(Downloads.COLUMN_TOTAL_BYTES, mInfo.mTotalBytes);
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
    }

    /**
     * Read headers from the HTTP response and store them into local state.
     */
    private void readResponseHeaders(State state, InnerState innerState,
                                     HttpResponse response) throws StopRequest {
        Header header = response.getFirstHeader("Content-Disposition");
        if (header != null) {
            innerState.mHeaderContentDisposition = header.getValue();
        }
        header = response.getFirstHeader("Content-Location");
        if (header != null) {
            innerState.mHeaderContentLocation = header.getValue();
        }
        header = response.getFirstHeader("ETag");
        if (header != null) {
            innerState.mHeaderETag = header.getValue();
        }
        String headerTransferEncoding = null;
        header = response.getFirstHeader("Transfer-Encoding");
        if (header != null) {
            headerTransferEncoding = header.getValue();
        }
        if (headerTransferEncoding == null) {
            header = response.getFirstHeader("Content-Length");
            if (header != null) {
                innerState.mHeaderContentLength = header.getValue();
                mInfo.mTotalBytes = Long
                        .parseLong(innerState.mHeaderContentLength);
            }
        } else {
            // Ignore content-length with transfer-encoding - 2616 4.4 3
            if (Constants.LOGVV) {
                Log.v(Constants.TAG,
                        "ignoring content-length because of xfer-encoding");
            }
        }
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Content-Disposition: " + innerState.mHeaderContentDisposition);
            Log.v(Constants.TAG, "Content-Length: " + innerState.mHeaderContentLength);
            Log.v(Constants.TAG, "Content-Location: " + innerState.mHeaderContentLocation);
            Log.v(Constants.TAG, "ETag: " + innerState.mHeaderETag);
            Log.v(Constants.TAG, "Transfer-Encoding: " + headerTransferEncoding);
        }
    }

    /**
     * Check the HTTP response status and handle anything unusual (e.g. not
     * 200/206).
     */
    private void handleExceptionalStatus(State state, InnerState innerState,
                                         HttpResponse response) throws StopRequest, RetryDownload {
        int statusCode = response.getStatusLine().getStatusCode();

        LogUtils.d("DownloadThread --->" + statusCode);
        if (statusCode == 503) {
            //* 处理HTTP 503 错误，Http 定义为，当前服务器出错，但是未来又可能会修复。
            throw new StopRequest(Downloads.STATUS_WAITING_TO_RETRY,
                    "got 503 Service Unavailable, will retry later");
        }
        if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307) {
            handleRedirect(state, response, statusCode);
        }

        int expectedStatus = innerState.mContinuingDownload ? 206 : Downloads.STATUS_SUCCESS;
        if (statusCode != expectedStatus) {
            handleOtherStatus(state, innerState, statusCode);
        }
    }

    /**
     * Handle a status that we don't know how to deal with properly.
     */
    private void handleOtherStatus(State state, InnerState innerState, int statusCode) throws StopRequest {
        int finalStatus;
        if (Downloads.isStatusError(statusCode)) {
            finalStatus = statusCode;
        } else if (statusCode >= 300 && statusCode < 400) {
            finalStatus = Downloads.STATUS_UNHANDLED_REDIRECT;
        } else if (innerState.mContinuingDownload && statusCode == Downloads.STATUS_SUCCESS) {
            if (checkSnailNetwork()) {
                finalStatus = Downloads.STATUS_CANNOT_RESUME;
            } else {
                finalStatus = Downloads.STATUS_PAUSE_BY_NETWORK;
            }
        } else {
            finalStatus = Downloads.STATUS_UNHANDLED_HTTP_CODE;
        }
        throw new StopRequest(finalStatus, "http error " + statusCode);
    }

    /**
     * Handle a 3xx redirect status.
     */
    private void handleRedirect(State state, HttpResponse response, int statusCode) throws StopRequest, RetryDownload {
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "got HTTP redirect " + statusCode);
        }
        if (state.mRedirectCount >= Constants.MAX_REDIRECTS) {
            throw new StopRequest(Downloads.STATUS_TOO_MANY_REDIRECTS, "too many redirects");
        }
        Header header = response.getFirstHeader("Location");
        if (header == null) {
            return;
        }
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Location :" + header.getValue());
        }

        String newUri;
        try {
            newUri = new URI(mInfo.mUri).resolve(new URI(header.getValue())).toString();
        } catch (URISyntaxException ex) {
            if (Constants.LOGV) {
                Log.d(Constants.TAG, "Couldn't resolve redirect URI " + header.getValue() + " for " + mInfo.mUri);
            }
            throw new StopRequest(Downloads.STATUS_HTTP_DATA_ERROR,
                    "Couldn't resolve redirect URI");
        }
        ++state.mRedirectCount;
        state.mRequestUri = newUri;
        if (statusCode == 301 || statusCode == 303) {
            // use the new URI for all future requests (should a retry/resume be
            // necessary)
            state.mNewUri = newUri;
        }
        throw new RetryDownload();
    }

    /**
     * Send the request to the server, handling any I/O exceptions.
     */
    private HttpResponse sendRequest(State state, AndroidHttpClient client,
                                     HttpGet request) throws StopRequest {
        try {
            return client.execute(request);
        } catch (IllegalArgumentException ex) {
            throw new StopRequest(Downloads.STATUS_HTTP_DATA_ERROR,
                    "while trying to execute request: " + ex.toString(), ex);
        } catch (IOException ex) {
            logNetworkState();
            throw new StopRequest(getFinalStatusForHttpError(state),
                    "while trying to execute request: " + ex.toString(), ex);
        }
    }

    private int getFinalStatusForHttpError(State state) {
        if (!Helpers.isNetworkAvailable(mSystemFacade)) {
            if (SharedPreferencesUtil.getInstance().isWifiOnly()) {
                return Downloads.STATUS_QUEUED_FOR_WIFI;
            }
            return Downloads.STATUS_WAITING_FOR_NETWORK;
        } else {
            return Downloads.STATUS_PAUSED_BY_APP;
        }
    }

    /**
     * Prepare the destination file to receive data. If the file already exists,
     * we'll set up appropriately for resumption.
     */
    private void setupDestinationFile(State state, InnerState innerState)
            throws StopRequest {
        if (!TextUtils.isEmpty(state.mFilename)) { // only true if we've already
            // run a thread for this
            // download
            // We're resuming a download that got interrupted
            File f = new File(state.mFilename);
            if (f.exists()) {
                long fileLength = f.length();
                if (fileLength == 0) {
                    // The download hadn't actually started, we can restart from
                    // scratch
                    f.delete();
                    state.mFilename = null;
                } else {
                    // All right, we'll be able to resume this download
                    try {
                        state.mStream = new FileOutputStream(state.mFilename, true);
                    } catch (FileNotFoundException exc) {
                        throw new StopRequest(Downloads.STATUS_FILE_ERROR,
                                "while opening destination for resuming: " + exc.toString(), exc);
                    }
                    innerState.mBytesSoFar = (int) fileLength;
                    if (mInfo.mTotalBytes != -1) {
                        innerState.mHeaderContentLength = Long.toString(mInfo.mTotalBytes);
                    }
                    innerState.mHeaderETag = mInfo.mETag;
                    innerState.mContinuingDownload = true;
                }
            }
        }
    }

    /**
     * Add custom headers for this download to the HTTP request.
     */
    private void addRequestHeaders(InnerState innerState, HttpGet request) {
        for (Pair<String, String> header : mInfo.getHeaders()) {
            request.addHeader(header.first, header.second);
        }

        if (innerState.mContinuingDownload) {
            if (innerState.mHeaderETag != null) {
                request.addHeader("If-Match", innerState.mHeaderETag);
            }
            request.addHeader("Range", "bytes=" + innerState.mBytesSoFar + "-");
        }
    }

    /**
     * Stores information about the completed download, and notifies the
     * initiating application.
     */
    private void notifyDownloadCompleted(int status, String filename, String uri) {
        notifyThroughDatabase(status, filename, uri);
        if (Downloads.isStatusCompleted(status)) {
            mInfo.sendIntentWhenDownloadCompleted();
        }
    }

    private void notifyThroughDatabase(int status,
                                       String filename, String uri) {
        ContentValues values = new ContentValues();
        if (status == Downloads.STATUS_PAUSE_BY_NETWORK) {
            status = Downloads.STATUS_PAUSED_BY_APP;
            values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_PAUSED);
        }
        values.put(Downloads.COLUMN_STATUS, status);
        values.put(Downloads._DATA, filename);
        if (uri != null) {
            // not update uri
//            values.put(Downloads.COLUMN_URI, uri);
        }
        values.put(Downloads.COLUMN_LAST_MODIFICATION, mSystemFacade.currentTimeMillis());
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
    }

    /**
     * State for the entire run() method.
     */
    private static class State {
        public String mFilename;
        public FileOutputStream mStream;
        public String mMimeType;
        public int mRedirectCount = 0;
        public String mNewUri;
        public boolean mGotData = false;
        public String mRequestUri;
        public String md5;
        public int iFreeArea;
        public String cFreeFlow;

        public State(DownloadInfo info) {
            mRequestUri = info.mUri;
            mFilename = info.mFileName;
            md5 = info.md5;
            iFreeArea = info.iFreeArea;
            cFreeFlow = info.cFreeFlow;
        }
    }

    /**
     * State within executeDownload()
     */
    private static class InnerState {
        public int mBytesSoFar = 0;
        public String mHeaderETag;
        public boolean mContinuingDownload = false;
        public String mHeaderContentLength;
        public String mHeaderContentDisposition;
        public String mHeaderContentLocation;
        public int mBytesNotified = 0;
        public long mTimeLastNotification = 0;
    }

    /**
     * Raised from methods called by run() to indicate that the current request
     * should be stopped immediately.
     * <p/>
     * Note the message passed to this exception will be logged and therefore
     * must be guaranteed not to contain any PII, meaning it generally can't
     * include any information about the request URI, headers, or destination
     * filename.
     */
    private class StopRequest extends Throwable {
        private static final long serialVersionUID = 1L;

        public int mFinalStatus;

        public StopRequest(int finalStatus, String message) {
            super(message);
            mFinalStatus = finalStatus;
        }

        public StopRequest(int finalStatus, String message, Throwable throwable) {
            super(message, throwable);
            mFinalStatus = finalStatus;
        }
    }

    /**
     * Raised from methods called by executeDownload() to indicate that the
     * download should be retried immediately.
     */
    private class RetryDownload extends Throwable {
        private static final long serialVersionUID = 1L;
    }


    /**
     * check is connect snail server well
     * add by xixh
     */
    private boolean checkSnailNetwork() {
        boolean ret = false;

        HttpGet httpGet = new HttpGet("http://api.app1.snail.com/cms/freestore.json");
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                if (!TextUtils.isEmpty(result) && result.equals("success\n\n"))
                    ret = true;
            }

        } catch (Exception e) {
        }

        return ret;
    }
}
