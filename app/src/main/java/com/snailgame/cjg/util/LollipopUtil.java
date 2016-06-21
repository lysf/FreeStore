package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by kevin on 15/3/23.
 */
public class LollipopUtil {
    public static void setupRecentDesc(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String label = activity.getString(R.string.app_name);
            Bitmap icon = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
            int color = ResUtil.getColor(R.color.theme_color_primary_dark);
            ActivityManager.TaskDescription desc = new ActivityManager.TaskDescription(label, icon, color);
            activity.setTaskDescription(desc);
        }
    }
}
