package com.snailgame.cjg.util.plug;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * A plugin apk. Activities in a same apk share a same AssetManager, Resources
 * and DexClassLoader.
 */
public class PluginPackage {

    public String packageName;
    public int versionCode;
    public String defaultActivity;
    public DexClassLoader classLoader;
    public AssetManager assetManager;
    public Resources resources;
    public PackageInfo packageInfo;

    public PluginPackage(DexClassLoader loader, Resources resources,
                         PackageInfo packageInfo) {
        this.packageName = packageInfo.packageName;
        this.versionCode = packageInfo.versionCode;
        this.classLoader = loader;
        this.assetManager = resources.getAssets();
        this.resources = resources;
        this.packageInfo = packageInfo;

        defaultActivity = parseDefaultActivityName();
    }

    private String parseDefaultActivityName() {
        if (packageInfo.activities != null && packageInfo.activities.length > 0) {
            return packageInfo.activities[0].name;
        }
        return "";
    }
}
