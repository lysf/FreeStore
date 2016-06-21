package third.com.snail.trafficmonitor.engine.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kevin on 14-7-22.
 *
 * SharePreference Manager
 */
public class SpManager {
    private static SpManager manager;
    private SharedPreferences sp;
    private Context context;

    public static class SpData {
        public static final String NAME = "traffics";
        public static final int INVALID_INTEGER = -1;
        public static final int INVALID_LONG = -1;
        public static final String INVALID_STRING = "";

        // 0 no, 1 yes, -1 not set
        public static final String FIRST_USE_KEY = "first_use";
        // boolean
        public static final String DATABASE_ININTED = "inited";
        public static final String USAGE_WIFI_TOTAL = "wifi_total";
        public static final String USAGE_MOBILE_TOTAL = "mobile_total";

        public static final String USAGE_UNTRUST_WIFI_TOTAL = "untrust_wifi_total";
        public static final String USAGE_UNTRUST_MOBILE_TOTAL = "untrust_mobile_total";
        public static final String FIRST_USER_YEAR= "first_use_year";
        public static final String FIRST_USER_MONTH = "first_use_month";
        public static final String FIRST_USER_WEEK = "first_use_week";
        public static final String FIRST_USER_DAY = "first_use_day";

        public static final String SPLIT_TABLE_KEY = "split_tables";

        // string
        public static final String DEFAULT_WIFI_IFACE = "d_wifi_iface";
        // string
        public static final String DEFAULT_MOBILE_IFACE = "d_mobile_iface";
        // boolean
        public static final String OPEN_ROOT_ADVANCED_FUNCTION = "open_root";
        public static final String OPEN_PROCESS_ADVANCED_FUNCTION = "process_advanced";
    }

    private SpManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(SpData.NAME, Context.MODE_PRIVATE);
    }

    public static SpManager getInstance(Context context) {
        if (manager == null) {
            manager = new SpManager(context);
        }
        return manager;
    }

    public void putInteger(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public int getInteger(String key) {
        return sp.getInt(key, SpData.INVALID_INTEGER);
    }

    public long getLong(String key) {
        return sp.getLong(key, SpData.INVALID_LONG);
    }

    public String getString(String key) {
        return sp.getString(key, SpData.INVALID_STRING);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
