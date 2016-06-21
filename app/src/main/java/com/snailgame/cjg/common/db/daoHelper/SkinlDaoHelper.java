package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.Skin;
import com.snailgame.cjg.skin.model.SkinPackage;
import com.snailgame.fastdev.util.LogUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lic on 2015/7/24.
 * 处理skin表中的增删改查
 */
public class SkinlDaoHelper {

    /**
     * 更新 忽略更新的version code
     *
     * @param context
     * @param info
     * @param skinLocalPath
     */
    public static void update(Context context, SkinPackage info, String skinLocalPath) {
        if (info == null) {
            LogUtils.i("update SkinPackage info is null");
            return;
        }
        try {
            Dao<Skin, Integer> skinDao = FreeStoreDataHelper.getHelper(context).getSkinDao();
            UpdateBuilder<Skin, Integer> updateBuilder = skinDao.updateBuilder();
            updateBuilder.updateColumnValue(Skin.COL_SKIN_VERSION, info.getiSkinVersion());
            updateBuilder.updateColumnValue(Skin.COL_APK_SIZE, info.getcSize());
            updateBuilder.updateColumnValue(Skin.COL_APK_URL, info.getcApkUrl());
            updateBuilder.updateColumnValue(Skin.COL_APK_MD5, info.getcMd5Code());
            updateBuilder.updateColumnValue(Skin.COL_APK_VERSION_CODE, info.getiVersionCode());
            updateBuilder.updateColumnValue(Skin.COL_APK_VERSION_NAME, info.getcVersionName());
            updateBuilder.updateColumnValue(Skin.COL_START_TIME, info.getsStartTime());
            updateBuilder.updateColumnValue(Skin.COL_END_TIME, info.getsEndTime());
            updateBuilder.updateColumnValue(Skin.COL_SKIN_LOCAL_PATH, skinLocalPath);
            updateBuilder.where().eq(Skin.COL_APK_PKG_NAME, info.getsPkgName());
            PreparedUpdate<Skin> preparedUpdate = updateBuilder.prepare();
            skinDao.update(preparedUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入
     *
     * @param context
     * @param info
     * @param skinLocalPath
     */
    public static void insert(Context context, SkinPackage info, String skinLocalPath) {
        if (info == null) {
            LogUtils.i("insert SkinPackage info is null");
            return;
        }
        try {
            Dao<Skin, Integer> skinDao = FreeStoreDataHelper.getHelper(context).getSkinDao();
            Skin skin = new Skin();
            skin.setStartTime(info.getsStartTime());
            skin.setEndTime(info.getsEndTime());
            skin.setSkinLocalPath(skinLocalPath);
            skin.setApkVersionName(info.getcVersionName());
            skin.setApkVersionCode(info.getiVersionCode());
            skin.setApkMD5(info.getcMd5Code());
            skin.setApkUrl(info.getcApkUrl());
            skin.setApkSize(info.getcSize());
            skin.setSkinVersion(String.valueOf(info.getiSkinVersion()));
            skin.setApkPackageName(info.getsPkgName());
            skinDao.create(skin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询
     *
     * @param context
     * @param appPkgName
     */
    public static List<Skin> query(Context context, String appPkgName) {
        if (appPkgName == null) {
            LogUtils.i("query appPkgName info is null");
            return null;
        }
        try {
            Dao<Skin, Integer> skinDao = FreeStoreDataHelper.getHelper(context).getSkinDao();
            return skinDao.queryForEq(Skin.COL_APK_PKG_NAME, appPkgName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询全部
     *
     * @param context
     */
    public static List<Skin> queryAll(Context context) {
        try {
            Dao<Skin, Integer> skinDao = FreeStoreDataHelper.getHelper(context).getSkinDao();
            return skinDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按照路径删除
     *
     * @param context
     * @param skinLocalPath
     */
    public static void delete(Context context, String skinLocalPath) {
        try {
            Dao<Skin, Integer> skinDao = FreeStoreDataHelper.getHelper(context).getSkinDao();
            DeleteBuilder<Skin, Integer> deleteBuilder = skinDao.deleteBuilder();
            deleteBuilder.where().eq(Skin.COL_SKIN_LOCAL_PATH, skinLocalPath);
            PreparedDelete<Skin> preparedDelete = deleteBuilder.prepare();
            skinDao.delete(preparedDelete);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
