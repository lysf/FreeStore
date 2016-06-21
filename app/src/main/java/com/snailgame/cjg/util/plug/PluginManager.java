package com.snailgame.cjg.util.plug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private static final String TAG = "PluginManager";

    private static PluginManager sInstance;
    private Context mContext;
    private final HashMap<String, PluginPackage> mPackagesHolder = new HashMap<>();

    private String mNativeLibDir = null;

    private PluginManager(Context context) {
        mContext = context.getApplicationContext();
        mNativeLibDir = mContext.getDir("pluginlib", Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static PluginManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null) {
                    sInstance = new PluginManager(context);
                }
            }
        }

        return sInstance;
    }

    public PluginPackage getPackage(String pkgNameVersionCode) {
        return mPackagesHolder.get(pkgNameVersionCode);
    }

    public PluginPackage removePackage(String pkgNameVersionCode) {
        return mPackagesHolder.remove(pkgNameVersionCode);
    }

    public void resetPackages() {
        mPackagesHolder.clear();
    }

    /**
     * Load a apk. Before start a plugin Activity, we should do this first.<br/>
     * NOTE : will only be called by host apk.
     *
     * @param dexPath plugin path
     * @return
     */
    public PluginPackage loadApk(String dexPath) {
        if (TextUtils.isEmpty(dexPath))
            return null;

        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            LogUtils.e("There's no activities or services");
            return null;
        }

        return preparePluginEnv(packageInfo, dexPath);
    }

    /**
     * prepare plugin runtime env, has DexClassLoader, Resources, and so on.
     *
     * @param packageInfo
     * @param dexPath
     * @return
     */
    private PluginPackage preparePluginEnv(PackageInfo packageInfo, String dexPath) {
        PluginPackage pluginPackage = mPackagesHolder.get(packageInfo.packageName + packageInfo.versionCode);
        if (pluginPackage != null) {
            return pluginPackage;
        }
        DexClassLoader dexClassLoader = createDexClassLoader(dexPath);
        AssetManager assetManager = createAssetManager(dexPath);
        Resources resources = createResources(assetManager);
        // create pluginPackage
        pluginPackage = new PluginPackage(dexClassLoader, resources, packageInfo);
        mPackagesHolder.put(pluginPackage.packageName + pluginPackage.versionCode, pluginPackage);
        return pluginPackage;
    }

    private DexClassLoader createDexClassLoader(String dexPath) {
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        String dexOutputPath = dexOutputDir.getAbsolutePath();
        return new DexClassLoader(dexPath, dexOutputPath, mNativeLibDir, mContext.getClassLoader());
    }

    private AssetManager createAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Resources createResources(AssetManager assetManager) {
        Resources superRes = ResUtil.getResources();
        return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }
}
