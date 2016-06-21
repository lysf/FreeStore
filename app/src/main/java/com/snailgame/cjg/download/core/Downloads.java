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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The Download Manager
 *
 * @pending
 */
public final class Downloads implements BaseColumns {

    /**
     * DownloadProvider authority
     */
    public static final String AUTHORITY = "com.snailgame.downloads";
    /**
     * The content URI for accessing all downloads across all UIDs (requires the
     * ACCESS_ALL_DOWNLOADS permission).
     */
    public static final Uri ALL_DOWNLOADS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+DownloadProvider.CONTENT_PATH);
    /**
     * The permission to access the download manager
     */
    public static final String PERMISSION_ACCESS = "com.snailgame.permission.ACCESS_DOWNLOAD_MANAGER";
    /**
     * The max number of the downloading tasks at the same time, including the pausing task
     */
    public static final int MAX_DOWNLOAD_NUM = 3;

    /*
     * the following columns come from android download module (v2.3) source code
     */
    /**
     * The name of the column containing the URI of the data being downloaded.
     */
    public static final String COLUMN_URI = "uri";
    /**
     * The name of the column containing application-specific data.
     */
    @Deprecated
    public static final String COLUMN_APP_DATA = "entity";
    /**
     * The name of the column containing the flags that indicates whether the
     * initiating application is capable of verifying the integrity of the
     * downloaded file. When this flag is set, the download manager performs
     * downloads and reports success even in some situations where it can't
     * guarantee that the download has completed (e.g. when doing a byte-range
     * request without an ETag, or when it can't determine whether a download
     * fully completed).
     */
    @Deprecated
    public static final String COLUMN_NO_INTEGRITY = "no_integrity";
    /**
     * The name of the column containing the filename that the initiating
     * application recommends. When possible, the download manager will attempt
     * to use this filename, or a variation, as the actual name for the file.
     */
    public static final String COLUMN_FILE_NAME_HINT = "hint";
    /**
     * The name of the column containing the filename where the downloaded data
     * was actually stored.
     */
    public static final String _DATA = "_data";
    /**
     * The name of the column containing the MIME type of the downloaded data.
     */
    @Deprecated
    public static final String COLUMN_MIME_TYPE = "mimetype";
    /**
     * The name of the column containing the flag that controls the destination
     * of the download. See the DESTINATION_* constants for a list of legal
     * values.
     */
    @Deprecated
    public static final String COLUMN_DESTINATION = "destination";
    /**
     * The name of the column containing the flags that controls whether the
     * download is displayed by the UI. See the VISIBILITY_* constants for a
     * list of legal values.
     */
    @Deprecated
    public static final String COLUMN_VISIBILITY = "visibility";
    /**
     * The name of the column containing the current control state of the
     * download. Applications can write to this to control (pause/resume) the
     * download. the CONTROL_* constants for a list of legal values.
     */
    public static final String COLUMN_CONTROL = "control";
    /**
     * The name of the column containing the current status of the download.
     * Applications can read this to follow the progress of each download. See
     * the STATUS_* constants for a list of legal values.
     */
    public static final String COLUMN_STATUS = "status";
    /**
     * The name of the column containing the date at which some interesting
     * status changed in the download. Stored as a System.currentTimeMillis()
     * value.
     */
    public static final String COLUMN_LAST_MODIFICATION = "lastmod";
    /**
     * The name of the column containing the package name of the application
     * that initiating the download. The download manager will send
     * notifications to a component in this package when the download completes.
     */
    @Deprecated
    public static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";
    /**
     * The name of the column containing the component name of the class that
     * will receive notifications associated with the download. The
     * package/class combination is passed to
     * Intent.setClassName(String,String).
     */
    @Deprecated
    public static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";
    /**
     * If extras are specified when requesting a download they will be provided
     * in the intent that is sent to the specified class and package when a
     * download has finished.
     */
    public static final String COLUMN_NOTIFICATION_EXTRAS = "notificationextras";
    /**
     * The name of the column contain the values of the cookie to be used for
     * the download. This is used directly as the value for the Cookie: HTTP
     * header that gets sent with the request.
     */
    @Deprecated
    public static final String COLUMN_COOKIE_DATA = "cookiedata";
    /**
     * The name of the column containing the user agent that the initiating
     * application wants the download manager to use for this download.
     */
    @Deprecated
    public static final String COLUMN_USER_AGENT = "useragent";
    /**
     * The name of the column containing the referer (sic) that the initiating
     * application wants the download manager to use for this download.
     */
    @Deprecated
    public static final String COLUMN_REFERER = "referer";
    /**
     * The name of the column containing the total size of the file being
     * downloaded.
     */
    public static final String COLUMN_TOTAL_BYTES = "total_bytes";
    /**
     * The name of the column containing the size of the part of the file that
     * has been downloaded so far.
     */
    public static final String COLUMN_CURRENT_BYTES = "current_bytes";
    /**
     * The name of the column where the initiating application can provide the
     * UID of another application that is allowed to access this download. If
     * multiple applications share the same UID, all those applications will be
     * allowed to access this download. This column can be updated after the
     * download is initiated. This requires the permission
     * com.snailgame.permission.ACCESS_DOWNLOAD_MANAGER_ADVANCED.
     */
    @Deprecated
    public static final String COLUMN_OTHER_UID = "otheruid";
    /**
     * The name of the column where the initiating application can provided the
     * title of this download. The title will be displayed ito the user in the
     * list of downloads.
     */
    public static final String COLUMN_TITLE = "title";
    /**
     * The name of the column where the initiating application can provide the
     * description of this download. The description will be displayed to the
     * user in the list of downloads.
     */
    @Deprecated
    public static final String COLUMN_DESCRIPTION = "description";
    /**
     * The name of the column indicating whether the download was requesting
     * through the public API. This controls some differences in behavior.
     */
    @Deprecated
    public static final String COLUMN_IS_PUBLIC_API = "is_public_api";
    /**
     * The name of the column indicating whether roaming connections can be
     * used. This is only used for public API downloads.
     */
    @Deprecated
    public static final String COLUMN_ALLOW_ROAMING = "allow_roaming";
    /**
     * The name of the column holding a bitmask of allowed network types. This
     * is only used for public API downloads.
     */
    public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";
    /**
     * Whether or not this download should be displayed in the system's
     * Downloads UI. Defaults to true.
     */
    @Deprecated
    public static final String COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI = "is_visible_in_downloads_ui";
    /**
     * If true, the user has confirmed that this download can proceed over the
     * mobile network even though it exceeds the recommended maximum size.
     */
    @Deprecated
    public static final String COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT = "bypass_recommended_size_limit";
    /**
     * Set to true if this download is deleted.
     */
    @Deprecated
    public static final String COLUMN_DELETED = "deleted";
    /*
     * the following columns are used for snail app
     */
    //set total_bytes_default valid if get total_bytes fail
    public static final String COLUMN_TOTAL_BYTES_DEFAULT = "total_bytes_default";
    //set app id
    public static final String COLUMN_APP_ID = "app_id";
    //set app label
    public static final String COLUMN_APP_LABEL = "app_label";
    //set app package name
    public static final String COLUMN_APP_PKG_NAME = "app_pkg_name";
    //set app icon url
    public static final String COLUMN_APP_iCON_URL = "app_icon_url";
    //set app version name
    public static final String COLUMN_APP_VERSION_NAME = "app_version_name";
    //set app version code
    public static final String COLUMN_APP_VERSION_CODE = "app_version_code";
    //the total length of bytes is downloaded in wifi
    public static final String COLUMN_BYTES_IN_WIFI = "bytes_in_wifi";
    //the total length of bytes is downloaded in non-wifi, 3g and etc.
    public static final String COLUMN_BYTES_IN_3G = "bytes_in_3g";
    //the md5 for the downloading apk
    public static final String COLUMN_MD5 = "md5";
    //the flow free state for the downloading apk
    public static final String COLUMN_FLOW_FREE_STATE = "flow_free_state";
    //the downloading apk is in free area
    public static final String COLUMN_FREE_AREA_STATE = "free_area_state";
    //the flow free state for the downloading apk
    public static final String COLUMN_FLOW_FREE_STATE_V2 = "flow_free_state_v2";
    //the appp type for the downloading apk
    public static final String COLUMN_APP_TYPE = "app_type";
    //the patch type for the downloading apk
    public static final String COLUMN_PATCH_TYPE = "patch_type";
    //the patch url for the downloading apk
    public static final String COLUMN_DIFF_URL = "diff_url";
    //the patch size for the downloading apk
    public static final String COLUMN_DIFF_SIZE = "diff_size";
    //the patch md5 for the downloading apk
    public static final String COLUMN_DIFF_MD5 = "diff_md5";
    /*
     * Lists the destinations that an application can specify for a download.
     */
    /**
     * This download is allowed to run.
     */
    public static final int CONTROL_RUN = 0;
    /**
     * This download must pause at the first opportunity.
     */
    public static final int CONTROL_PAUSED = 1;
    /**
     * This download hasn't stated yet
     */
    public static final int STATUS_PENDING = 190;
    /**
     * This download hasn't stated yet for a Wi-Fi connection to proceed
     */
    public static final int STATUS_PENDING_FOR_WIFI = 191;
    /**
     * This download has started
     */
    public static final int STATUS_RUNNING = 192;
    /**
     * This download has been paused by the owning app.
     */
    public static final int STATUS_PAUSED_BY_APP = 193;
    /**
     * This download encountered some network error and is waiting before
     * retrying the request.
     */
    public static final int STATUS_WAITING_TO_RETRY = 194;
    /**
     * This download is waiting for network connectivity to proceed.
     */
    public static final int STATUS_WAITING_FOR_NETWORK = 195;
    /**
     * This download exceeded a size limit for mobile networks and is waiting
     * for a Wi-Fi connection to proceed.
     */
    public static final int STATUS_QUEUED_FOR_WIFI = 196;
    /**
     * This download has been paused by the owning app for network exception
     */
    public static final int STATUS_PAUSE_BY_NETWORK = 197;
    /**
     * This download has successfully completed. Warning: there might be other
     * status values that indicate success in the future. Use isSucccess() to
     * capture the entire category.
     */
    public static final int STATUS_SUCCESS = 200;
    /**
     * This download is installing after is downloaded completed.
     */
    public static final int STATUS_INSTALLING = 201;
    /**
     * This download is installing after is downloaded completed.
     */
    public static final int STATUS_PATCHING = 202;
    /**
     * The downloaded file's md5 not match with the md5 from server
     */
    public static final int STATUS_NO_MATCH_MD5 = 487;
    /**
     * The requested destination file already exists.
     */
    public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;
    /**
     * Some possibly transient error occurred, but we can't resume the download.
     */
    public static final int STATUS_CANNOT_RESUME = 489;
    /**
     * This download was canceled
     */
    public static final int STATUS_CANCELED = 490;
    /**
     * This download has completed with an error. Warning: there will be other
     * status values that indicate errors in the future. Use isStatusError() to
     * capture the entire category.
     */
    public static final int STATUS_UNKNOWN_ERROR = 491;
    /**
     * This download couldn't be completed because of a storage issue.
     * Typically, that's because the filesystem is missing or full. Use the more
     * specific {@link #STATUS_INSUFFICIENT_SPACE_ERROR} and
     * {@link #STATUS_DEVICE_NOT_FOUND_ERROR} when appropriate.
     */
    public static final int STATUS_FILE_ERROR = 492;
    /**
     * This download couldn't be completed because of an HTTP redirect response
     * that the download manager couldn't handle.
     */
    public static final int STATUS_UNHANDLED_REDIRECT = 493;
    /**
     * This download couldn't be completed because of an unspecified unhandled
     * HTTP code.
     */
    public static final int STATUS_UNHANDLED_HTTP_CODE = 494;
    /**
     * This download couldn't be completed because of an error receiving or
     * processing data at the HTTP level.
     */
    public static final int STATUS_HTTP_DATA_ERROR = 495;
    /**
     * This download patching failed because patching file is error.
     */
    public static final int STATUS_PATCHING_FAILED = 496;
    /**
     * This download couldn't be completed because there were too many
     * redirects.
     */
    public static final int STATUS_TOO_MANY_REDIRECTS = 497;
    /**
     * This download couldn't be completed due to insufficient storage space.
     * Typically, this is because the SD card is full.
     */
    public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 498;
    /**
     * This download couldn't be completed because no external storage device
     * was found. Typically, this is because the SD card is not mounted.
     */
    public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 499;

    private Downloads() {
    }

    /**
     * Returns whether the status is an error (i.e. 4xx or 5xx).
     */
    public static boolean isStatusError(int status) {
        return (status >= 400 && status < 600);
    }

    /**
     * Returns whether the download has completed (either with success or
     * error).
     */
    public static boolean isStatusCompleted(int status) {
        return (status >= 200 && status < 300)
                || (status >= 400 && status < 600);
    }

    /**
     * Constants related to HTTP request headers associated with each download.
     */
    public static class RequestHeaders {
        @Deprecated
        public static final String HEADERS_DB_TABLE = "request_headers";
        @Deprecated
        public static final String COLUMN_DOWNLOAD_ID = "download_id";
        @Deprecated
        public static final String COLUMN_HEADER = "header";
        @Deprecated
        public static final String COLUMN_VALUE = "value";
    }
}
