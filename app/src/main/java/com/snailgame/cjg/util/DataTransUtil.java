package com.snailgame.cjg.util;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * Created by sunxy on 2015/6/10.
 */
public class DataTransUtil {
    private static final String justNow = FreeStoreApp.getContext().getString(R.string.just_now);
    private static final String minuteAgo = FreeStoreApp.getContext().getString(R.string.minute_ago);
    private static final String hourAgo = FreeStoreApp.getContext().getString(R.string.hour_ago);
    private static final String dayAgo = FreeStoreApp.getContext().getString(R.string.day_ago);

    public static String getLastRefreshTime(long lastRefreshTime) {
        String interval = null;
        long time = (System.currentTimeMillis() - lastRefreshTime) / (1000);
        if (time < 60) {
            interval = justNow;
        } else if (time / 60 < 60) {
            interval = time / 60 + minuteAgo;
        } else if (time / 3600 < 24) {
            interval = time / 3600 + hourAgo;
        } else {
            interval = (time / (3600 * 24))+dayAgo;
        }
        return interval;
    }
}
