package third.com.snail.trafficmonitor.engine.util.process;

import com.snailgame.cjg.global.GlobalVar;

/**
 * Created by lic on 2014/12/08
 * 比较重要的一些应用程序的进程
 */
public class ImportPreference {

    static {
        String[] arrayOfString = new String[38];
        arrayOfString[0] = "android";
        arrayOfString[1] = "com.google.android.providers.gmail";
        arrayOfString[2] = "com.android.providers.telephony";
        arrayOfString[3] = "com.android.phone";
        arrayOfString[4] = "com.android.providers.settings";
        arrayOfString[5] = "com.android.mms";
        arrayOfString[6] = "com.android.email";
        arrayOfString[7] = "com.android.alarmclock";
        arrayOfString[8] = "com.android.providers.im";
        arrayOfString[9] = "com.android.googlesearch";
        arrayOfString[10] = "com.android.providers.downloads";
        arrayOfString[11] = "com.google.android.providers.enhancedgooglesearch";
        arrayOfString[12] = "com.android.providers.drm";
        arrayOfString[13] = "com.android.providers.media";
        arrayOfString[14] = "com.android.voicedialer";
        arrayOfString[15] = "com.android.providers.applications";
        arrayOfString[16] = "com.android.providers.userdictionary";
        arrayOfString[17] = "com.android.launcher";
        arrayOfString[18] = "com.android.providers.contacts";
        arrayOfString[19] = "com.htc.android.htcime";
        arrayOfString[20] = "com.android.contacts";
        arrayOfString[21] = "com.google.android.inputmethod.pinyin";
        arrayOfString[22] = "com.android.providers.subscribedfeeds";
        arrayOfString[23] = "com.android.globalsearch";
        arrayOfString[24] = "com.google.android.server.checkin";
        arrayOfString[25] = "com.android.setupwizard";
        arrayOfString[26] = "com.svox.pico";
        arrayOfString[27] = "system";
        arrayOfString[28] = "com.htc.widget.clockwidget";
        arrayOfString[29] = "com.htc.android.htcime";
        arrayOfString[30] = "com.google.android.partnersetup";
        arrayOfString[31] = "com.android.defcontainer";
        arrayOfString[32] = "com.android.vending";
        arrayOfString[33] = "com.google.android.gsf";
        arrayOfString[34] = "com.android.systemui";
        arrayOfString[35] = "com.snailgame.cjg";
        arrayOfString[36] = "com.snailgame.cjg:pushservice";
        arrayOfString[37] = "com.snailgame.cjg:traffic_monitor";
        for (int x = 0; x < arrayOfString.length; x++) {
            GlobalVar.getInstance().getImportPreferenceList().add(arrayOfString[x]);
        }
    }

    /**
     * 通过对比看是否为重要进程或者是否是否绑定一些重要的广播
     */
    public static boolean isImportant(String processname) {
        boolean flag = false;
        if (GlobalVar.getInstance().getListMainValue().contains(processname) || GlobalVar.getInstance().getImportPreferenceList().contains(processname)) {
            return true;
        }
        return flag;
    }

}
