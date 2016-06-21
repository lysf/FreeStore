package third.com.snail.trafficmonitor.engine.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Arrays;
import java.util.Calendar;

import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by lic on 2014/11/17.
 */
public class TimeTickAlarmManager {
    public final static String TAG = TimeTickAlarmManager.class.getSimpleName();
    public final static String ACTION = "com.snail.trafficmonitor.engine.TimeTick";

    /**
     * 判断Alarm是否存在
     */
    private static boolean isAlarmExits(Context context) {
        return (PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    /**
     * 尝试向系统注册每4小测量流量数据的Alarm
     *
     * @param context 上下文Context
     * @return true则已注册过，false尚未注册
     */
    public static boolean tryRegister(Context context) {
        //4.4以下版本需要查看是否已经存在alarm，以上则直接覆盖。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (isAlarmExits(context)) {
                return true;
            }
        }
        Intent intent = new Intent(ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //计算出当前时间之后的下次打点的4小时整点时间。
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int sumMinute = currentHour * 60 + currentMinute;
        int[] c = new int[6];
        c[0] = 4 * 60 - sumMinute;
        c[1] = 8 * 60 - sumMinute;
        c[2] = 12 * 60 - sumMinute;
        c[3] = 16 * 60 - sumMinute;
        c[4] = 20 * 60 - sumMinute;
        c[5] = 24 * 60 - sumMinute;
        Arrays.sort(c);
        for (int a : c) {
            if (a > 0) {
                int hour = a / 60;
                int minute = a % 60;
                calendar.add(Calendar.HOUR_OF_DAY, hour);
                calendar.add(Calendar.MINUTE, minute);
                break;
            }
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis());
        LogWrapper.d(TAG, "First start TIMETICK at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //4.4以下因为没有精确方法所以用不精准的循环的alarm，4.4以上则选择精确计算的alram，但是需要每次收到alarm后重新设置一次。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 4 * 60 * 60 * 1000, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        return false;
    }
}
