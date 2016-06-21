package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.snailgame.mobilesdk.SnailCommplatform;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: xingfinal
 * Date: 13-5-10
 * Time: 下午5:42
 */
public class PhoneUtil {

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static float getScreenWidth() {
        Context context = FreeStoreApp.getContext();
        DisplayMetrics displaymetrics = ResUtil.getDisplayMetrics();
        return (float) displaymetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static float getScreenHeight() {
        Context context = FreeStoreApp.getContext();
        DisplayMetrics displaymetrics = ResUtil.getDisplayMetrics();
        return (float) displaymetrics.heightPixels;
    }

    /**
     * 获取应用区域的高度（除去状态栏高度）
     * @return
     */
    public static int getAppAreaHeight(Activity activity){
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.height();
    }

    public static int getSimState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimState();
    }


    /**
     * 获取设备基本信息 1、手机机型 2、手机CPU 3、RAM容量 4、ROM容量 5、屏幕分辨率 6、操作系统及版本 7、核心数
     * 8、设备号 9、设备IMEI号
     * 并且组成json
     */
    public static String getDeviceInfo(Activity activity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceModel", android.os.Build.MODEL);
            jsonObject.put("cpuName", getCpuInfo());
            jsonObject.put("ramSize", getRamSpace(activity));
            jsonObject.put("romSize", getRomSpace(activity));
            jsonObject.put("screenResolution", getDisplayMetrics(activity));
            jsonObject.put("systemVersion", "Android " + android.os.Build.VERSION.RELEASE);
            jsonObject.put("coreNumber", getNumCores());
            jsonObject.put("deviceId", SnailCommplatform.getInstance().getCIMEI(FreeStoreApp.getContext()));
            jsonObject.put("IMEICode", TextUtils.isEmpty(getIMEICode(activity)) ? "-" : getIMEICode(activity));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    /**
     * 获取cpu名称
     *
     * @return 如果失败返回""
     */
    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2;
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            if (arrayOfString.length > 2) {
                for (int i = 3; i < arrayOfString.length; i++) {
                    cpuInfo[1] = cpuInfo[1] + arrayOfString[i] + " ";
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
            return "";
        }
        //华为的cpu的cpuinfo文件的第二行才是cpu的名称，第一行是频率，所以需要判断
        if (cpuInfo[0].startsWith("0")) {
            return cpuInfo[1];
        } else {
            return cpuInfo[0];
        }
    }


    /**
     * 获取当前cpu核心数
     *
     * @return cpu真实核心数，如果失败则返回""
     */
    public static String getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length + "";
        } catch (Exception e) {
            //Print exception
            e.printStackTrace();
            //Default to return 1 core
            return "";
        }
    }

    /**
     * 获取当前系统的RAM容量大小
     *
     * @return 如果sdk版本小于16则返回""
     */
    public static String getRamSpace(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        if (Build.VERSION.SDK_INT < 16) {
            return "";
        } else {
            am.getMemoryInfo(memory);
            String totalSize = FileUtil.formatFileSize(activity, memory.totalMem);
            return totalSize;
        }
    }

    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前系统的ROM容量大小
     *
     * @return rom大小
     */
    public static String getRomSpace(Activity activity) {
        //机身存储
        File devicePath = Environment.getDataDirectory();
        StatFs deviceStat = new StatFs(devicePath.getPath());
        long deviceBlockCount;
        long deviceBlockSize;
        if (Build.VERSION.SDK_INT < 18) {
            deviceBlockCount = deviceStat.getBlockCount();
            deviceBlockSize = deviceStat.getBlockSize();
        } else {
            deviceBlockCount = deviceStat.getBlockCountLong();
            deviceBlockSize = deviceStat.getBlockSizeLong();
        }
        //内部存储
        File externalPath = Environment.getExternalStorageDirectory();
        StatFs externalStat = new StatFs(externalPath.getPath());
        long externalBlockCount;
        long externalBlockSize;
        if (Build.VERSION.SDK_INT < 18) {
            externalBlockCount = externalStat.getBlockCount();
            externalBlockSize = externalStat.getBlockSize();
        } else {
            externalBlockCount = externalStat.getBlockCountLong();
            externalBlockSize = externalStat.getBlockSizeLong();
        }
        String totalSize = FileUtil.formatFileSize(activity, deviceBlockCount * deviceBlockSize +
                externalBlockCount * externalBlockSize);
        return totalSize;
    }

    /**
     * 获取当前手机的屏幕分辨率
     *
     * @return 手机分辨率
     */
    public static String getDisplayMetrics(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;
        return W + "*" + H;
    }


    /*
     * 获取手机当前的网络类型
     */
    public static String getNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return "NONE";
        }
        String typeName = info.getTypeName();
        if (typeName.equalsIgnoreCase("WIFI")) { //wifi连接
            return "WIFI";
        } else if (typeName.equalsIgnoreCase("MOBILE")) {
            TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String netType;
            switch (telephone.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    netType = "2G";
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    netType = "3G";
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    netType = "4G";
                    break;
                default:
                    netType = "UNKNOWN";
                    break;
            }
            return netType;
        } else {
            return "UNKNOWN";
        }
    }


    private static TelephonyManager telephonyManager;

    /**
     * 获取手机的IMEI值，为手机的唯一标示符。
     *
     * @param context
     * @return 手机的IMEI值。
     */
    public static String getIMEICode(Context context) {
        String code = "";

        try {
            if (telephonyManager == null) {
                telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
            }

            code = telephonyManager.getDeviceId();
        } catch (Exception var2) {
        }

        return code;
    }


    /**
     * 获取手机号码
     *
     * @param context
     * @return 手机号码
     */
    public static String getPhoneNumber(Context context) {
        String number = "";

        try {
            if (telephonyManager == null) {
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }

            number = telephonyManager.getLine1Number();
        } catch (Exception var2) {
        }

        return number;
    }


    /**
     * 获取手机Imsi
     *
     * @param context
     * @return Imsi
     */
    public static String getImsi(Context context) {
        String imsi = "";
        try {
            if (telephonyManager == null) {
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }

            imsi = telephonyManager.getSubscriberId();
        } catch (Exception var2) {
        }

        return imsi;
    }


    /**
     * 获取MAC地址
     *
     * @param context
     * @return MAC地址
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            String macAddress = info.getMacAddress();
            LogUtils.d("mac address is " + macAddress);
            return macAddress;
        }
        return "";
    }

    /**
     * 获取sim卡序列号
     *
     * @param context
     * @return sim卡序列号
     */
    public static String getSimSerial(Context context) {
        String serial = "";

        try {
            if (telephonyManager == null) {
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }

            serial = telephonyManager.getSimSerialNumber();
        } catch (Exception var2) {
        }

        return serial;
    }


    public static String getCardUUID(Context context) {
        String simSerial = getSimSerial(context) == "" ? "" : getSimSerial(context);
        if (!TextUtils.isEmpty(simSerial)) {
            if (simSerial.length() > 10) {
                return simSerial;
            }

            if (simSerial.length() > 3) {
                return simSerial + getIMEICode(context) == "" ? "" : getIMEICode(context);
            }
        }

        String phoneNumber = getPhoneNumber(context) == "" ? "" : getPhoneNumber(context);
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 5 ? phoneNumber : null;
    }

    public static String getDeviceUUID(Context context) {
        String uuid = getCardUUID(context);
        if (uuid != null) {
            return uuid;
        } else {
            String imei = getIMEICode(context) == "" ? "" : getIMEICode(context);
            if (!TextUtils.isEmpty(imei) && imei.length() > 6) {
                return imei;
            } else {
                String mac = getMacAddress(context);
                return !TextUtils.isEmpty(mac) && mac.length() > 6 ? mac : null;
            }
        }
    }

}
