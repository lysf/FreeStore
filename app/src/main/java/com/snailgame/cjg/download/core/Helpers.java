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
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Log;

import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.util.Random;

/**
 * Some helper functions for the download manager
 */
public class Helpers {
    public static Random sRandom = new Random(SystemClock.uptimeMillis());

    private Helpers() {
    }

    /**
     * Creates a filename (where the file should be saved) from info about a
     * download.
     */
    public static String generateSaveFile(String hint, long contentLength) throws GenerateSaveFileError {
        if (!isExternalMediaMounted()) {
            throw new GenerateSaveFileError(
                    Downloads.STATUS_DEVICE_NOT_FOUND_ERROR,
                    "external media not mounted");
        }
        String path = Uri.parse(hint).getPath();
        if (new File(path).exists()) {
            Log.d(Constants.TAG, "File already exists: " + path);
            throw new GenerateSaveFileError(
                    Downloads.STATUS_FILE_ALREADY_EXISTS_ERROR,
                    "requested destination file already exists");
        }
        if (getAvailableBytes(getFilesystemRoot(path)) < contentLength) {
            throw new GenerateSaveFileError(
                    Downloads.STATUS_INSUFFICIENT_SPACE_ERROR,
                    "insufficient space on external storage");
        }

        return path;
    }

    /**
     * @return the root of the filesystem containing the given path
     */
    public static File getFilesystemRoot(String path) {
        File external = Environment.getExternalStorageDirectory();
        if (path.startsWith(external.getPath())) {
            return external;
        }
        throw new IllegalArgumentException(
                "Cannot determine filesystem root for " + path);
    }

    public static boolean isExternalMediaMounted() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // No SD card found.
            LogUtils.e("no external storage");
            return false;
        }
        return true;
    }

    /**
     * @return the number of bytes available on the filesystem rooted at the
     * given File
     */
    public static long getAvailableBytes(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a
        // few blocks)
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }


    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(SystemFacade system) {
        return system.getActiveNetworkType() != null;
    }

    /**
     * Checks whether the filename looks legitimate
     */
    public static boolean isFilenameValid(String filename) {
        filename = filename.replaceFirst("/+", "/"); // normalize leading
        // slashes
        return filename.startsWith(Environment.getDownloadCacheDirectory()
                .toString())
                || filename.startsWith(Environment
                .getExternalStorageDirectory().toString());
    }

    /**
     * Exception thrown from methods called by generateSaveFile() for any fatal
     * error.
     */
    public static class GenerateSaveFileError extends Exception {
        private static final long serialVersionUID = 4293675292408637112L;

        int mStatus;
        String mMessage;

        public GenerateSaveFileError(int status, String message) {
            mStatus = status;
            mMessage = message;
        }
    }
}
