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

/**
 * Contains the internal constants that are used in the download manager.
 * As a general rule, modifying these constants should be done with care.
 */
public class Constants {

    /**
     * Tag used for debugging/logging
     */
    public static final String TAG = "DownloadManager";

    /**
     * The column that used to be used for the HTTP method of the request
     */
    @Deprecated
    public static final String RETRY_AFTER_X_REDIRECT_COUNT = "method";

    /**
     * The column that used to be used for the magic OTA update filename
     */
    @Deprecated
    public static final String OTA_UPDATE = "otaupdate";

    /**
     * The column that used to be used to reject system filetypes
     */
    @Deprecated
    public static final String NO_SYSTEM_FILES = "no_system";

    /**
     * The column that is used for the downloads's ETag
     */
    public static final String ETAG = "etag";

    /**
     * The column that is used for the initiating app's UID
     */
    @Deprecated
    public static final String UID = "uid";

    /**
     * The column that is used to count retries
     */
    @Deprecated
    public static final String FAILED_CONNECTIONS = "numfailed";

    /**
     * the intent that gets sent when clicking an incomplete/failed download
     */
    public static final String ACTION_LIST = "com.snailgame.cjg.action.DOWNLOAD_LIST";

    /**
     * The buffer size used to stream the data
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * The minimum amount of progress that has to be done before the progress bar gets updated
     */
    public static final int MIN_PROGRESS_STEP = 4096;

    /**
     * The minimum amount of time that has to elapse before the progress bar gets updated, in ms
     */
    public static final long MIN_PROGRESS_TIME = 1500;

    /**
     * The maximum number of rows in the database (FIFO)
     */
    public static final int MAX_DOWNLOADS = 1000;

    /**
     * The maximum number of redirects.
     */
    public static final int MAX_REDIRECTS = 5; // can't be more than 7.
    public static final boolean LOGV = true;
    public static final boolean LOGVV = false;
    /**
     * 网络不可用
     */
    public static final int NET_WORK_TYPE_NONE = -1;


}
