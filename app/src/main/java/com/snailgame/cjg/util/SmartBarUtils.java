package com.snailgame.cjg.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.snailgame.fastdev.util.ResUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 魅族 SmartBar适配
 * Created by xixh on 2015/3/10.
 */
public class SmartBarUtils {
    private static String smartBarStatus = null;


    /**
     * 以下三个方法原作者为c跳跳(http://weibo.com/u/1698085875),
     * 由Shawn(http://weibo.com/linshen2011)在其基础上改进了一种判断SmartBar是否存在的方法,
     * 注意该方法反射的接口只存在于2013年6月之后魅族的flyme固件中
     */

    /**
     * 方法一:uc等在使用的方法(新旧版flyme均有效)，
     * 此方法需要配合requestWindowFeature(Window.FEATURE_NO_TITLE
     * )使用,缺点是程序无法使用系统actionbar
     *
     * @param decorView
     *            window.getDecorView
     */
    public static void hide(View decorView) {
        if (!hasSmartBarLocal())
            return;

        try {
            @SuppressWarnings("rawtypes")
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = Integer.TYPE;
            Method localMethod = View.class.getMethod("setSystemUiVisibility",
                    arrayOfClass);
            Field localField = View.class
                    .getField("SYSTEM_UI_FLAG_HIDE_NAVIGATION");
            Object[] arrayOfObject = new Object[1];
            try {
                arrayOfObject[0] = localField.get(null);
            } catch (Exception e) {

            }
            localMethod.invoke(decorView, arrayOfObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法二：此方法需要配合requestWindowFeature(Window.FEATURE_NO_TITLE)使用
     * ，缺点是程序无法使用系统actionbar
     *
     * @param context
     * @param window
     */
    public static void hide(Context context, Window window) {
        hide(context, window, 0);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = ResUtil.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ResUtil.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 方法三：需要使用顶部actionbar的应用请使用此方法
     *
     * @param context
     * @param window
     * @param smartBarHeight
     *            set SmartBarUtils.SMART_BAR_HEIGHT_PIXEL
     */
    private static void hide(Context context, Window window, int smartBarHeight) {
        if (!hasSmartBarLocal())
            return;

        if (ResUtil.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        int statusBarHeight = getStatusBarHeight(context);

        window.getDecorView()
                .setPadding(0, statusBarHeight, 0, -smartBarHeight);
    }

    /**
     * 判断有无SmartBar
     * @return
     */
    private static boolean hasSmartBarLocal() {
        if (smartBarStatus == null) {
            smartBarStatus = String.valueOf(hasSmartBar());
        }

        return Boolean.parseBoolean(smartBarStatus);
    }

    /**
     * 新型号可用反射调用Build.hasSmartBar()来判断有无SmartBar
     * @return
     */
    private static boolean hasSmartBar() {
        try {
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return (Boolean) method.invoke(null);
        } catch (Exception localException) {
        }

        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }
}
