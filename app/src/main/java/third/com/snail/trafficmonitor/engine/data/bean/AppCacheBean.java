package third.com.snail.trafficmonitor.engine.data.bean;

import android.graphics.drawable.Drawable;

public class AppCacheBean {
    private long cachesize;
    private Drawable icon;
    private String name;
    private String packagename;
    private String version;

    public long getCachesize() {
        return cachesize;
    }

    public void setCachesize(long cachesize) {
        this.cachesize = cachesize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
