package third.com.snail.trafficmonitor.engine.data.bean;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import third.com.snail.trafficmonitor.engine.data.table.App;

/**
 * Created by kevin on 14/12/23.
 */
public class AppWrapper {
    private long totalRx;
    private long totalTx;
    private long wifiRx;
    private long wifiTx;
    private App app;
    private Drawable icon;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getTotalRx() {
        return totalRx;
    }

    public void setTotalRx(long totalRx) {
        this.totalRx = totalRx;
    }

    public long getTotalTx() {
        return totalTx;
    }

    public void setTotalTx(long totalTx) {
        this.totalTx = totalTx;
    }

    public long getWifiRx() {
        return wifiRx;
    }

    public void setWifiRx(long wifiRx) {
        this.wifiRx = wifiRx;
    }

    public long getWifiTx() {
        return wifiTx;
    }

    public void setWifiTx(long wifiTx) {
        this.wifiTx = wifiTx;
    }

    public int getId() {
        return app.getId();
    }

    public void setId(int id) {
        app.setId(id);
    }

    public int getUid() {
        return app.getUid();
    }

    public void setUid(int uid) {
        app.setUid(uid);
    }

    public String getPackageName() {
        return app.getPackageName();
    }

    public void setPackageName(@NonNull String name) {
        app.setPackageName(name);
    }

    public String getAppName() {
        return app.getAppName();
    }

    public void setAppName(@NonNull String name) {
        app.setAppName(name);
    }

    public int getVersionCode() {
        return app.getVersionCode();
    }

    public void setVersionCode(int code) {
        app.setVersionCode(code);
    }

    public String getVersionName() {
        return app.getVersionName();
    }

    public void setVersionName(String name) {
        app.setVersionName(name);
    }

    public boolean isMonitoring() {
        return app.isMonitoring();
    }

    public void setMonitoring(boolean b) {
        app.setMonitoring(b);
    }

    public boolean isDisplay() {
        return app.isDisplay();
    }

    public void setDisplay(boolean display) {
        app.setDisplay(display);
    }

    public boolean isWifiAccess() {
        return app.isWifiAccess();
    }

    public void setWifiAccess(boolean wifiAccess) {
        app.setWifiAccess(wifiAccess);
    }

    public boolean isMobileAccess() {
        return app.isMobileAccess();
    }

    public void setMobileAccess(boolean mobileAccess) {
        app.setMobileAccess(mobileAccess);
    }

}
