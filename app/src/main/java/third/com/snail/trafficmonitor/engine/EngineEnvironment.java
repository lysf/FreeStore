package third.com.snail.trafficmonitor.engine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;
import third.com.snail.trafficmonitor.engine.data.bean.SystemInsideUID;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.manager.TimeTickAlarmManager;
import third.com.snail.trafficmonitor.engine.service.TrafficMonitor;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

import static third.com.snail.trafficmonitor.engine.util.LogWrapper.makeTag;

/**
 * Created by kevin on 14/12/15.
 */
public class EngineEnvironment {
    private static final String TAG = makeTag(EngineEnvironment.class);

    public static final String PACKAGE_NAME_SPECIAL_WIFI_INTERFACE = "network.interface.wifi";
    public static final String PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE = "network.interface.mobile";

    private static final int MEMORY_LOG_FILE_MAX_SIZE = 1 * 1024 * 1024; //内存中日志文件最大值，1M
    private static final int MEMORY_LOG_FILE_SAVE_DAYS = 7; //内存中日志文件最多保存最近的7天

    public static final String ENGINE_TRAFFIC_FUNCTION_ENABLE_KEY = "com.snail.trafficmonitor.enable14";

    public static final String DEBUG_SECRET_CODE = "9091888";

    private int fistUseYear;
    private int fistUseMonth;
    private int fistUseWeek;
    private int fistUseDay;
    private static EngineEnvironment INSTANCE;
    private Context mAppContext;

    private EngineEnvironment(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public static EngineEnvironment getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (EngineEnvironment.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EngineEnvironment(context);
                }
            }
        }
        return INSTANCE;
    }

    public static boolean isTrafficEnable() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 必须在Activity中进行初始化才可生效
     *
     * @param context
     */
    public static void initIfaces(Context context) {
        SpManager manager = SpManager.getInstance(context);
        String mobile = null;
        String wifi = null;

        String[] a = TrafficTool.getMobileIfaces();
        if (a != null && a.length >= 1) {
            mobile = a[0];
            LogWrapper.e(TAG, mobile);
        }

        StringBuilder sb = new StringBuilder();
        CommandHelper.runCmd("getprop wifi.interface", sb);
        String result = sb.toString();
        LogWrapper.e(TAG, result);
        if (!TextUtils.isEmpty(result)) {
            wifi = result;
        }
        if (mobile != null) {
            mobile = mobile.replace("\n", "");
            manager.putString(SpManager.SpData.DEFAULT_MOBILE_IFACE, mobile);
        }
        if (wifi != null) {
            wifi = wifi.replace("\n", "");
            manager.putString(SpManager.SpData.DEFAULT_WIFI_IFACE, wifi);
        }
    }

    /**
     * 初始化流量统计引擎所需的环境
     */
    public void initEnvironments() {
        if (mAppContext == null) {
            throw new RuntimeException("EngineEnvironment doesn't init yet.");
        }
        initCommandHelper();
        if (isTrafficEnable()) {
            handleTrafficApartments(true);
            initTimeTickAlarmManager();
            initSystemInsideUID();
            initDatabase();
        } else {
            handleTrafficApartments(false);
        }
    }

    /**
     * 如果手机系统版本不支持流量统计功能，那就禁止流量统计功能的组件初始化
     */
    private void handleTrafficApartments(boolean enable) {
        final PackageManager pm = mAppContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mAppContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS
                            | PackageManager.GET_META_DATA);
            if (pi == null) {
                LogWrapper.e(TAG, "No package info found in " + mAppContext.getPackageName());
                return;
            }
            ArrayList<ComponentInfo> infos = new ArrayList<>();
            infos.addAll(Arrays.asList(pi.activities));
            infos.addAll(Arrays.asList(pi.providers));
            infos.addAll(Arrays.asList(pi.receivers));
            infos.addAll(Arrays.asList(pi.services));
            for (ComponentInfo i : infos) {
                if (i.metaData == null || !i.metaData.getBoolean(ENGINE_TRAFFIC_FUNCTION_ENABLE_KEY, false)) {
                    continue;
                }
                String className = i.name;
                LogWrapper.d(TAG, "Class: " + className + " Enable? " + enable);
                pm.setComponentEnabledSetting(new ComponentName(mAppContext, Class.forName(className)),
                        enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogWrapper.e(TAG, "No package info found in " + mAppContext.getPackageName() + e);
        } catch (ClassNotFoundException e) {
            LogWrapper.e(TAG, "No class found " + e);
        }
    }

    /**
     * 初始化流量统计的表
     */
    private void initDatabase() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int nextMonth;
        int nextYear;
        if (month >= Calendar.DECEMBER) {
            nextYear = year + 1;
            nextMonth = Calendar.JANUARY;
        } else {
            nextYear = year;
            nextMonth = month + 1;
        }
        //为防止异常，将这个月和下个月的表都检查并创建
        try {
            TrafficDataHelper.getHelper(mAppContext)
                    .dropOrCreateTrafficTable(year, month, true);
            TrafficDataHelper.getHelper(mAppContext)
                    .dropOrCreateTrafficTable(nextYear, nextMonth, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(mAppContext, TrafficMonitor.class);
        intent.setAction(TrafficMonitor.ACTION_INIT_ALL_DATABASE);
        intent.addFlags(1);
        mAppContext.startService(intent);
    }

    /**
     * 初始化alarmmanager
     */
    private void initTimeTickAlarmManager() {
        boolean exist = TimeTickAlarmManager.tryRegister(mAppContext);
        if (exist) {
            LogWrapper.d(TAG, "TimeTick alarm has been registered before.");
        }
    }

    /**
     * 初始化相关系统对应的uid
     */
    private void initSystemInsideUID() {
        SystemInsideUID.InsideUid.init();
    }

    /**
     * 初始化adb command
     */
    private void initCommandHelper() {
        CommandHelper.init();
    }

    /**
     * 根据提供的年、月获取对应的当月的表名
     *
     * @return 返回表名
     */
    public String getTrafficTableName() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        String monthStr = String.format("%02d", month);
        String tableName = "traffic_" + year + monthStr;
        return tableName;
    }


    public int getFistUseYear() {
        return fistUseYear;
    }

    public int getFistUseMonth() {
        return fistUseMonth;
    }

    public int getFistUseWeek() {
        return fistUseWeek;
    }

    public int getFistUseDay() {
        return fistUseDay;
    }

    public void setFistUseYear(int fistUseYear) {
        this.fistUseYear = fistUseYear;
    }

    public void setFistUseMonth(int fistUseMonth) {
        this.fistUseMonth = fistUseMonth;
    }

    public void setFistUseWeek(int fistUseWeek) {
        this.fistUseWeek = fistUseWeek;
    }

    public void setFistUseDay(int fistUseDay) {
        this.fistUseDay = fistUseDay;
    }

    private static String getSDPath(Context context) {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在

        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取data/data目录
        } else {
            sdDir = context.getFilesDir();
        }
        return sdDir.toString();
    }

}
