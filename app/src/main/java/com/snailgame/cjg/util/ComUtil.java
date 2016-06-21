package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.SystemConfig;
import com.snailgame.cjg.common.ui.WebViewFragment;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.wxapi.WechatShare;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class ComUtil {
    public static int VersionCode = -1;

    // 判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    //判断是否为 170号码
    public static boolean is170MobileNo(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        Pattern p = Pattern.compile("^(17[0,1])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    //dp 转 px
    public static int dpToPx(int dp) {
        DisplayMetrics metrics = ResUtil.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    /**
     * 获取用户省份
     *
     * @return
     */
    public static String getProvinceId(String defValue) {
        return SharedPreferencesHelper.getInstance().getValue(AppConstants.PROVINCE_ID, defValue);
    }

    // 根据包名 获取包名对于版本名称
    public static String getVersionNameByPackage(String packageName, Context context) {
        PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(context, packageName);
        String versionName = null;
        if (packageInfo != null) {
            versionName = packageInfo.versionName;
        }
        return versionName;
    }

    // 获取免商店 版本号
    public static int getSelfVersionCode() {
        if (VersionCode <= 0) {
            Context context = FreeStoreApp.getContext();
            PackageInfo info = PackageInfoUtil.getPackageInfoByName(context, context.getPackageName());
            if (info != null)
                VersionCode = info.versionCode;
        }

        return VersionCode;
    }


    // 获取免商店 版本名
    public static String getSelfVersionName() {
        String versionName = "";
        Context context = FreeStoreApp.getContext();
        PackageInfo info = PackageInfoUtil.getPackageInfoByName(context, context.getPackageName());
        if (info != null)
            versionName = info.versionName;
        return versionName;
    }


    /**
     * 服务是否运行
     *
     * @param mContext
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        if (mContext == null || serviceName == null) {
            return false;
        }

        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null || manager.getRunningServices(Integer.MAX_VALUE) == null)
            return false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检测Activity 状态
     *
     * @param activity
     * @return
     */
    public static boolean isActivityNullOrFinish(Activity activity) {
        return activity == null || activity.isFinishing();
    }


    /**
     * 根据包名检查本地是否 已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 根据包名 获得启动的Intent
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static Intent getLaunchIntentForPackage(Context context, String pkgName) {
        Intent intent = null;
        if (context != null && !TextUtils.isEmpty(pkgName)) {
            intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        }
        return intent;
    }


    /**
     * 读取用户手机号
     *
     * @param context
     * @return
     */
    public static String readPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();//手机号码
    }


    /**
     * 检查Intent 是否在 系统存在
     *
     * @param context
     * @param targetIntent
     * @return
     */
    public static boolean isIntentSafe(Context context, Intent targetIntent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(targetIntent, 0);
        return activities.size() > 0;
    }


    /**
     * 显示 分享应用选择器
     *
     * @param context
     * @param content
     */
    public static void showIntentChoser(Context context, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (isIntentSafe(context, intent)) {
            String title = ResUtil.getString(R.string.chooser_title);
            Intent chooser = Intent.createChooser(intent, title);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chooser);
            }
        }
    }

    /**
     * 获得本地已经安装的应用
     *
     * @param appInfos
     * @param context
     * @return
     */
    public static List<AppInfo> getInstalledApk(List<AppInfo> appInfos, Context context) {
        if (context == null) return null;
        PackageManager manager = context.getPackageManager();
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos) {
            PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(context, appInfo.getPkgName());
            if (packageInfo != null) {
                AppInfo info = new AppInfo();
                info.setAppId(appInfo.getAppId());
                info.setPkgName(packageInfo.packageName);
                info.setAppName(appInfo.getAppName());
                info.setVersionName(packageInfo.versionName);
                info.setApkSize((int) new File(packageInfo.applicationInfo.publicSourceDir).length());
                info.setIcon(appInfo.getIcon());
                appInfoList.add(info);
            }
        }
        return appInfoList;
    }


    /**
     * 获取分享图片
     *
     * @param imageName
     * @return
     */
    public static File getShareImageFile(String imageName) {
        File dir = new File(WechatShare.IMAGE_SHARE_DIR);
        if (!dir.exists()) dir.mkdir();
        return new File(dir.getPath() + "/" + imageName);
    }


    /**
     * 显示错误页面
     *
     * @param context
     * @param webView
     */
    public static void showErrorPage(Context context, final WebView webView) {
        View errorView = View.inflate(context, R.layout.listview_error, null);
        final ViewGroup parentView = (ViewGroup) webView.getParent();
        errorView.setOnClickListener(null);

        View reloadView = ButterKnife.findById(errorView, R.id.buttonError);
        reloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.removeAllViews();
                parentView.addView(webView);
                webView.reload();
            }
        });
        if (parentView != null) {
            parentView.removeAllViews();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            parentView.addView(errorView, 0, lp);
        }
    }

    /**
     * Get four random integer value from 0~99
     *
     * @param arg
     * @param limitSize
     * @return
     */
    public static int[] getRandomValue(int arg, int limitSize) {
        Random random = new Random();

        int randIndex, temp;
        int[] seed = new int[arg];
        for (int i = 0; i < seed.length; i++) {
            seed[i] = i;
        }
        int len = seed.length;
        int length = arg;
        if (length > limitSize) length = limitSize;
        int[] returnValue = new int[length];
        for (int i = 0; i < returnValue.length; i++) {
            if (len > 0) {
                randIndex = Math.abs(random.nextInt()) % len;
                returnValue[i] = seed[randIndex];
                temp = seed[randIndex];
                seed[randIndex] = seed[len - 1];
                seed[len - 1] = temp;
                len--;
            }

        }

        return returnValue;
    }


    /**
     * 复制到 剪切板中
     *
     * @param context
     * @param content
     */
    public static void copyToClipBoard(Context context, String content) {
        ClipboardManager copy = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(content);
        ToastUtils.showMsgLong(context, context.getResources().getString(R.string.copy_success));
    }


    /**
     * 根据手机的分辨率从 sp的单位 转成为 px(像素)
     */
    public static float sp2px(float spValue, Context context) {
        final float fontScale = ResUtil.getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static void setDrawableLeft(TextView title, int resId) {
        Drawable drawable = ResUtil.getDrawable(resId);
        if (drawable == null)
            return;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        title.setCompoundDrawables(drawable, null, null, null);
    }

    public static void setDrawableRight(TextView title, int resId) {
        Drawable drawable = ResUtil.getDrawable(resId);
        if (drawable == null)
            return;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        title.setCompoundDrawables(null, null, drawable, null);
    }

    public static void setDrawableTop(TextView title, int resId) {
        Drawable drawable = ResUtil.getDrawable(resId);
        if (drawable == null)
            return;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        title.setCompoundDrawables(null, drawable, null, null);
    }


    /**
     * IMEI
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 判断一个程序是否显示在前端,根据测试此方法执行效率在11毫秒,无需担心此方法的执行效率
     *
     * @param packageName
     * @return true---&gt;在前端,false---&gt;不在前端
     */
    public static boolean isApplicationShowing(String packageName) {
        ActivityManager am = (ActivityManager) FreeStoreApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
        if (!ListUtils.isEmpty(taskInfos)) {
            ComponentName cn = taskInfos.get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isTopActivity(String activityName) {
        ActivityManager am = (ActivityManager) FreeStoreApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
        if (!ListUtils.isEmpty(taskInfos)) {
            ComponentName cn = taskInfos.get(0).topActivity;
            if (!TextUtils.isEmpty(cn.getClassName()) && cn.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打开外部浏览器
     *
     * @param url
     * @return
     */
    public static void openExternalBrowser(Activity activity, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            activity.startActivity(intent);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 只针对小米的launcher和tcl的launcher来通过遍历launcher的快捷方式provider来判断是否已经存在快捷方式
     *
     * @param context,shortCutName
     * @return
     */
    public static boolean isShortcutExist(Context context, String shortCutName) {
        String[] AUTHORS = new String[]{"com.android.launcher.settings",
                "com.miui.home.launcher.settings",
                "com.lewa.launcher.settings"};
        ContentResolver resolver = context.getContentResolver();
        try {
            for (String author : AUTHORS) {
                Uri uri = Uri.parse("content://" + author + "/favorites?notify=true");
                Cursor c = resolver.query(uri, new String[]{"title"}, "title=?", new String[]{shortCutName}, null);
                if (c != null && c.getCount() > 0) {
                    c.close();
                    return true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Fragment getBBSFragment() {
        boolean isOpen = true;
        SystemConfig systemConfig = PersistentVar.getInstance().getSystemConfig();
        String channels = systemConfig.getHideForumChannelIds();
        String version = systemConfig.getScoreForumStopVersion();
        if (systemConfig.getStopForumFunc() == 1) {
            if (version.contains(String.valueOf(ComUtil.getSelfVersionCode())) || channels.contains(ChannelUtil.getChannelID())) {
                isOpen = false;
            }
        }
        WebViewFragment fragment = WebViewFragment.create(
                systemConfig.getBbsUrl(), isOpen, systemConfig.getStopForumDes(), AppConstants.WEBVIEW_MODEL_BBS);
        return fragment;
    }

    public static String getEncodeString(String q) {
        try {
            return URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return q;
    }

    public static int parseColor(String color, int defaultColor) {
        try {
            return Color.parseColor("#" + color);
        } catch (Exception e) {
            return defaultColor;
        }
    }

    // 低于4.0的设备只允许使用免厅，其他功能不支持
    public static boolean workInLowSystem() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    public static String durationInSecondsToString(int duration) {
        String time;
        if (duration >= 3600) {
            time = (duration / 3600 + ":");
            duration = duration % 3600;
        } else {
            time = "0:";
        }
        if (duration > 60) {
            time += "0" + (duration / 60 + ":");
            duration = duration % 60;
        } else {
            time += "00:";
        }
        if (duration < 10)
            time = time + "0" + duration;
        else {
            time += duration;
        }
        return time;
    }

    /**
     * 获取状态栏高度,只有5.0以上才需要减去状态栏高度
     *
     * @return
     */
    public static int  getStatesBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int i = ResUtil.getIdentifier("status_bar_height", "dimen", "android");
            int j = 0;
            if (i > 0)
                j = ResUtil.getDimensionPixelSize(i);
            return j;
        }
        return 0;
    }


    /**
     * 更新应用列表进度
     *
     * @param taskInfo
     * @param collectionItemList
     */
    public static void updateProgress(Context context, TaskInfo taskInfo, List<AppInfo> collectionItemList) {
        for (AppInfo appInfo : collectionItemList) {
            if (taskInfo.getAppId() == appInfo.getAppId()) {
                DownloadHelper.calcDownloadSpeed(context, appInfo, taskInfo);
                appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                appInfo.setDownloadTotalSize(-1 == taskInfo.getApkTotalSize() ? AppInfoUtils.getPatchApkSize(appInfo) : taskInfo
                        .getApkTotalSize());
                appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                appInfo.setDownloadState(taskInfo.getDownloadState());
                appInfo.setLocalUri(taskInfo.getApkLocalUri());
                appInfo.setApkDownloadId(taskInfo.getTaskId());
                appInfo.setInDownloadDB(true);
                appInfo.setDownloadPatch(taskInfo.getApkTotalSize() < appInfo.getApkSize());
                int reason = taskInfo.getReason();
                DownloadHelper.handleMsgForPauseOrError(context, appInfo.getAppName(),
                        taskInfo.getDownloadState(), reason);
                break;
            }
        }
    }


    public static Bitmap addWhiteBackground(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        Bitmap bitmapWithWhiteBackground = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmapWithWhiteBackground);
        canvas.drawColor(ResUtil.getColor(R.color.white));
        canvas.drawBitmap(bitmap, 0, 0, paint);
//        bitmap.recycle();
        return bitmapWithWhiteBackground;
    }


    public static String getFormatDate(String time) {
        DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat outFormat1 = new SimpleDateFormat("yyyy.MM.dd");

        try {
            Date date = inFormat.parse(time);
            long interval = (System.currentTimeMillis() - date.getTime()) / 1000;
            if (interval < 60) {
                return ResUtil.getString(R.string.date_just_now);
            } else if (interval < 60 * 60) {
                return ResUtil.getString(R.string.date_several_minutes_ago, interval / 60);
            } else if (interval < 60 * 60 * 24) {
                return ResUtil.getString(R.string.date_several_hours_ago, interval / (60 * 60));
            } else if (interval < 60 * 60 * 24 * 10) {
                return ResUtil.getString(R.string.date_several_days_ago, interval / (60 * 60 * 24));
            } else {
                return outFormat1.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = inputString.trim().toCharArray();
        String output = "";
        try {
            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else
                    output += java.lang.Character.toString(curchar);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }

}
