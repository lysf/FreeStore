package third.com.snail.trafficmonitor.engine.data.bean;

import android.graphics.drawable.Drawable;


public class AppListCustomData {
    private Drawable mAppIcon;
    private String mAppName;
    private boolean isSystem;
    private boolean mAppCheck;
    private String packageName;
    private String apkName;
    public Drawable getAppIcon() {
        return this.mAppIcon;
    }

    public void setAppIcon(Drawable draw) {
        this.mAppIcon = draw;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public void setAppName(String text) {
        this.mAppName = text;
    }

    public boolean getCheckBox() {
        return mAppCheck;
    }

    public void setCheckBox(boolean state) {
        this.mAppCheck = state;
    }

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public void setSystem(boolean isSystem) { this.isSystem = isSystem;}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }
}
