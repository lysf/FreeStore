package com.snailgame.cjg.settings;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;

import java.util.HashMap;
import java.util.List;

/**
 * 免root自动装
 * Created by TAJ_C on 2015/9/9.
 */
@SuppressLint("NewApi")
public class AutoInstallAccessibilityService extends AccessibilityService {

    private static HashMap<String, Boolean> autoInstallMap;

    private static final String[] INSTALL = {"安装", "install"};
    private static final String[] NEXT = {"下一步", "next"};
    private static final String[] FINISH = {"完成", "complete"};

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                if (event == null || event.getSource() == null || getRootInActiveWindow() == null) {
                    return;
                }

                if (event.getPackageName() == null ||

                        (false == event.getPackageName().equals("com.android.packageinstaller") &&
                                false == event.getPackageName().equals("com.google.android.packageinstaller") &&
                                false == event.getPackageName().equals("com.lenovo.safecenter") &&
                                false == event.getPackageName().equals("com.lenovo.security") &&
                                false == event.getPackageName().equals("com.xiaomi.gamecenter") &&
                                 false == event.getPackageName().equals("com.samsung.android.packageinstaller"))) {
                    return;
                }


                if (false == isAutoInstall(getRootInActiveWindow())) {
                    return;
                }
                clickButtonByName(event.getSource(), INSTALL);
                clickButtonByName(event.getSource(), NEXT);
                clickButtonByName(getRootInActiveWindow(), FINISH);

            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }

    }


    private boolean isAutoInstall(AccessibilityNodeInfo nodeInfo) {
        int queryFilter = DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_EXTRA_UPGRADABLE
                | DownloadManager.STATUS_EXTRA_INSTALLED;

        List<TaskInfo> taskInfoList = DownloadHelper.getDownloadTasks(
                AutoInstallAccessibilityService.this,
                DownloadHelper.QUERY_TYPE_BY_STATUS,
                queryFilter);


        if (autoInstallMap == null) {
            autoInstallMap = new HashMap<>();
        }

        for (TaskInfo taskInfo : taskInfoList) {
            String appName = taskInfo.getAppLabel();
            if (!TextUtils.isEmpty(appName) && autoInstallMap != null &&
                    !autoInstallMap.containsKey(appName)) {
                autoInstallMap.put(appName, true);
            }
        }


        for (String appName : autoInstallMap.keySet()) {
            if (!TextUtils.isEmpty(appName) && nodeInfo != null) {
                List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(appName);
                if (!ListUtils.isEmpty(nodeInfos)) {
                    return true;
                }
            }

        }
        return false;

    }


    @Override
    public void onInterrupt() {

    }

    private void clickButtonByName(AccessibilityNodeInfo accessibilityNodeInfo, String[] btnNames) {
        for (String btnName : btnNames) {
            List<AccessibilityNodeInfo> okNodes = accessibilityNodeInfo.findAccessibilityNodeInfosByText(btnName);
            if (!ListUtils.isEmpty(okNodes)) {
                for (AccessibilityNodeInfo node : okNodes) {
                    CharSequence charSequence = node.getText();
                    if (TextUtils.isEmpty(charSequence) || !charSequence.toString().equals(btnName)) {
                        continue;
                    }

                    if ((node.getClassName().equals("android.widget.Button") ||
                            node.getClassName().equals("android.widget.TextView")) && node.isEnabled()) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }

    }

    // To check if service is enabled
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + AutoInstallAccessibilityService.class.getName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            // Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoInstallMap != null) {
            autoInstallMap.clear();
        }
    }
}
