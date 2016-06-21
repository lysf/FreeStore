package com.snailgame.cjg.util.skin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.Skin;
import com.snailgame.cjg.common.db.daoHelper.SkinlDaoHelper;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.skin.SkinDownloadServices;
import com.snailgame.cjg.skin.model.SkinPackage;
import com.snailgame.cjg.util.plug.PluginManager;
import com.snailgame.cjg.util.plug.PluginPackage;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pancl on 2015/4/8.
 */
public class SkinManager {
    private static SkinManager sInstance;
    private static final String SPLIT_CHAR = ":";

    private PluginPackage skinPluginPackage;
    private SkinPackage skinPackage;

    private List<ISkinDrawable> iSkinDrawables;
    private List<ISkinColor> iSkinColors;

    private SkinManager() {
        iSkinDrawables = new ArrayList<>();
        iSkinColors = new ArrayList<>();
    }

    public static SkinManager getInstance() {
        if (sInstance == null) {
            synchronized (SkinManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * 加载皮肤包并通知所有已注册换肤对象
     *
     * @param skinPackagePath
     */
    public PluginPackage loadSkinPackage(String skinPackagePath) {
        PluginPackage pluginPackage = PluginManager.getInstance(FreeStoreApp.getContext()).loadApk(skinPackagePath);
        if (pluginPackage == null)
            return null;

        if (this.skinPluginPackage != null && this.skinPluginPackage != pluginPackage) {
            PluginManager.getInstance(FreeStoreApp.getContext()).removePackage(
                    this.skinPluginPackage.packageName + this.skinPluginPackage.versionCode);
        }
        this.skinPluginPackage = pluginPackage;
        this.skinPackage = loadSkinPackageConfig(this.skinPluginPackage.packageName, this.skinPluginPackage.versionCode);
        notifySkinChange();
        return skinPluginPackage;
    }

    /**
     * 通知所有已注册换肤对象
     */
    private void notifySkinChange() {
        LogUtils.i("notifySkinChange");
        for (ISkinDrawable iSkinDrawable : iSkinDrawables) {
            iSkinDrawable.onDrawableChanged(getSkinDrawable(FreeStoreApp.getContext(), iSkinDrawable.getDrawableResId()));
        }
        for (ISkinColor iSkinColor : iSkinColors) {
            iSkinColor.onColorChanged(getSkinColor(FreeStoreApp.getContext(), iSkinColor.getColorResId()));
        }
    }

    /**
     * 注册可换肤对象
     *
     * @param iSkinDrawable
     * @return
     */
    public boolean registerSkinables(ISkinDrawable iSkinDrawable) {
        if (skinPluginPackage != null) {
            iSkinDrawable.onDrawableChanged(getSkinDrawable(FreeStoreApp.getContext(), iSkinDrawable.getDrawableResId()));
        }
        return iSkinDrawables.add(iSkinDrawable);
    }

    public void unregisterSkinables(ISkinDrawable iSkinDrawable) {
        if (iSkinDrawables.contains(iSkinDrawable)) {
            iSkinDrawables.remove(iSkinDrawable);
        }
    }

    /**
     * 注册可换肤对象
     *
     * @param iSkinColor
     * @return
     */
    public boolean registerSkinables(ISkinColor iSkinColor) {
        if (skinPluginPackage != null) {
            iSkinColor.onColorChanged(getSkinColor(FreeStoreApp.getContext(), iSkinColor.getColorResId()));
        }
        return iSkinColors.add(iSkinColor);
    }

    public void unregisterSkinables(ISkinColor iSkinColor) {
        if (iSkinColors.contains(iSkinColor)) {
            iSkinColors.remove(iSkinColor);
        }
    }


    public void unregisterSkinables(String TAG) {
        if (TAG == null)
            return;

        List<ISkinColor> tempC = new ArrayList<>();
        for (ISkinColor iSkinColor : iSkinColors) {
            if (iSkinColor.getTag() == null)
                continue;

            if (iSkinColor.getTag().equals(TAG))
                tempC.add(iSkinColor);
        }
        iSkinColors.removeAll(tempC);

        List<ISkinDrawable> tempD = new ArrayList<>();
        for (ISkinDrawable iSkinDrawable : iSkinDrawables) {
            if (iSkinDrawable.getTag() == null)
                continue;

            if (iSkinDrawable.getTag().equals(TAG))
                tempD.add(iSkinDrawable);
        }
        iSkinDrawables.removeAll(tempD);
    }

    /**
     * 根据资源ID获取皮肤Drawable
     *
     * @param context
     * @param resId
     * @return
     */
    public
    @Nullable
    Drawable getSkinDrawable(Context context, @DrawableRes int resId) {
        Date now = new Date();
        if (skinPluginPackage == null || skinPackage == null ||
                now.before(skinPackage.getsStartDate()) || now.after(skinPackage.getsEndDate())) {
            return ResUtil.getDrawable(resId);
        } else {
            String resName = context.getResources().getResourceName(resId);
            resName = skinPluginPackage.packageName + SPLIT_CHAR + resName.split(SPLIT_CHAR)[1];
            resId = skinPluginPackage.resources.getIdentifier(resName, null, null);
            if (resId > 0) {
                try {
                    return skinPluginPackage.resources.getDrawable(resId);
                } catch (OutOfMemoryError e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * 根据资源ID获取皮肤Color
     *
     * @param context
     * @param resId
     * @return
     */
    public int getSkinColor(Context context, @ColorRes int resId) {
        Date now = new Date();
        if (skinPluginPackage == null || skinPackage == null ||
                now.before(skinPackage.getsStartDate()) || now.after(skinPackage.getsEndDate())) {
            return ResUtil.getColor(resId);
        } else {
            String resName = ResUtil.getResourceName(resId);
            resName = skinPluginPackage.packageName + SPLIT_CHAR + resName.split(SPLIT_CHAR)[1];
            resId = skinPluginPackage.resources.getIdentifier(resName, null, null);
            if (resId > 0) {
                return skinPluginPackage.resources.getColor(resId);
            } else {
                return R.color.black;
            }
        }
    }

    /**
     * 加载当前可用皮肤配置
     *
     * @param packageName
     * @param versionCode
     * @return
     */
    private SkinPackage loadSkinPackageConfig(String packageName, int versionCode) {
        String skinPackages = PersistentVar.getInstance().getSystemConfig().getSkinPackages();
        if (!TextUtils.isEmpty(skinPackages)) {
            try {
                List<SkinPackage> skinPackageList = JSON.parseArray(skinPackages, SkinPackage.class);
                if (!ListUtils.isEmpty(skinPackageList)) {
                    for (SkinPackage skinPackage : skinPackageList) {
                        if (TextUtils.equals(skinPackage.getsPkgName(), packageName)
                                && TextUtils.equals(skinPackage.getiVersionCode(), String.valueOf(versionCode))) {
                            return skinPackage;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.d("cannot parse skinpackages to array");
            }
        }
        return null;
    }

    /**
     * 获取当前皮肤包信息
     *
     * @return
     */
    public SkinPackage getSkinPackage() {
        return skinPackage;
    }

    /**
     * 初始加载皮肤包
     *
     * @param context
     */
    public void initSkinPackage(Context context) {
        List<SkinPackage> skinPackageList = getSkinPackageList();
        if (!ListUtils.isEmpty(skinPackageList)) {
            Date now = new Date();
            for (SkinPackage skinPackage : skinPackageList) {
                if (skinPackage.getiSkinVersion() == AppConstants.SKIN_PACKAGE_SUPPORT_VERSION
                        && now.after(skinPackage.getsStartDate())
                        && now.before(skinPackage.getsEndDate())) {
                    Intent downloadSkinIntent = new Intent(context, SkinDownloadServices.class);
                    downloadSkinIntent.putExtra(AppConstants.KEY_SKIN_INFO, skinPackage);
                    context.startService(downloadSkinIntent);
                    break;
                }
            }
        }
    }

    /**
     * 反序列化皮肤包配置信息
     *
     * @return
     */
    private List<SkinPackage> getSkinPackageList() {
        String skinPackages = PersistentVar.getInstance().getSystemConfig().getSkinPackages();
        if (!TextUtils.isEmpty(skinPackages)) {
            try {
                return JSON.parseArray(skinPackages, SkinPackage.class);
            } catch (Exception e) {
                LogUtils.d("cannot parse skinpackages to array");
            }
        }
        return null;
    }

    private SkinPackage getSkinPackage(String apkPkgName, int apkVersionCode) {
        List<SkinPackage> skinPackageList = getSkinPackageList();
        if (!ListUtils.isEmpty(skinPackageList)) {
            for (SkinPackage skinPackage : skinPackageList) {
                if (TextUtils.equals(skinPackage.getsPkgName(), apkPkgName)
                        && TextUtils.equals(skinPackage.getiVersionCode(), String.valueOf(apkVersionCode))) {
                    return skinPackage;
                }
            }
        }
        return null;
    }

    /**
     * 清理皮肤表中文件不存在的记录，以及过期应用
     *
     * @param context
     */
    public void clearSkins(Context context) {
        List<Skin> skinList = SkinlDaoHelper.queryAll(context);
        if (skinList == null || skinList.size() > 0) {
            return;
        }
        Date now = new Date();
        for (Skin skin : skinList) {
            Date startDate = SkinPackage.getSkinPackageDate(skin.getStartTime());
            Date endDate = SkinPackage.getSkinPackageDate(skin.getEndTime());
            SkinPackage skinPackage = getSkinPackage(skin.getApkPackageName(), Integer.parseInt(skin.getApkVersionCode()));
            File skinLocalFile = new File(skin.getSkinLocalPath());
            if (skinLocalFile.exists()) { //如果文件存在
                if (skinPackage == null) { //检查是否配置里有该项配置，无配置，则根据数据库起止检查是否过期，如过期，删除文件且删除数据库记录
                    if (now.before(startDate) || now.after(endDate)) {
                        LogUtils.i("skinPackage is null and " + skin.getSkinLocalPath() + " invalid so remove file and record");
                        skinLocalFile.delete();
                        SkinlDaoHelper.delete(context, skin.getSkinLocalPath());
                    }
                } else { //有该配置，则根据该配置起止检查是否过期，如过期，删除文件且删除数据库记录
                    if (now.before(skinPackage.getsStartDate()) || now.after(skinPackage.getsEndDate())) {
                        LogUtils.i("skinPackage is not null and " + skin.getSkinLocalPath() + " invalid so remove file and record");
                        skinLocalFile.delete();
                        SkinlDaoHelper.delete(context, skin.getSkinLocalPath());
                    } else if (!TextUtils.equals(skinPackage.getsStartTime(), skin.getStartTime())
                            || !TextUtils.equals(skinPackage.getsEndTime(), skin.getEndTime())) { //未过期，判断配置起止是否与数据库起止相同，不相同则将配置起止更新至数据库起止
                        LogUtils.i("skinPackage is not null and " + skin.getSkinLocalPath() + " valid but time not same so update record");
                        SkinlDaoHelper.update(context, skinPackage, skin.getSkinLocalPath());
                    }
                }
            } else {//如果文件不存在，立即删除数据库记录
                LogUtils.i(skin.getSkinLocalPath() + " not exist and remove record");
                SkinlDaoHelper.delete(context, skin.getSkinLocalPath());
            }
        }
    }
}
