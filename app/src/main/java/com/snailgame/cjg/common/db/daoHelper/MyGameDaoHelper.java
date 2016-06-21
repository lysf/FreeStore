package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.MyGame;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.downloadmanager.model.UpgradeAppListModel;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ApkUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MD5Util;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.service.LocalApkManageService;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shenzaih
 * on 14-3-13.
 * 对my_game_table的操作动作
 */
public class MyGameDaoHelper {
    public static final String TAG = MyGameDaoHelper.class.getName();

    private static final String PARAM_VERSION_CODE = "iVersioncodes";
    private static final String PARAM_PKG = "cPackages";

    public static void getUpgradeApk(final Context context) {
        ParamBuilder builder = new ParamBuilder();
        builder.getParamBuilder(context);

        Map<String, String> parameters = AccountUtil.getLoginMapParams();
        parameters.put(PARAM_VERSION_CODE, builder.versionCodeBuilder.toString());
        parameters.put(PARAM_PKG, builder.pkgNameBuilder.toString());

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_APP_UPDATE_LIST_V2, TAG, UpgradeAppListModel.class, new IFDResponse<UpgradeAppListModel>() {
            @Override
            public void onSuccess(UpgradeAppListModel result) {
                new UpdateData(result, context).start();
            }

            @Override
            public void onNetWorkError() {

            }

            @Override
            public void onServerError() {

            }
        }, parameters, true, new ExtendJsonUtil());
    }

    static class UpdateData extends Thread {
        UpgradeAppListModel result;
        Context context;

        public UpdateData(UpgradeAppListModel result, Context context) {
            this.result = result;
            this.context = context;
        }

        @Override
        public void run() {
            if (result == null || ListUtils.isEmpty(result.getItemList())) {
                return;
            }

            for (UpgradeAppListModel.ModelItem item : result.getItemList()) {
                if (!item.getcPackage().equals(context.getPackageName())) {
                    if (queryPackage(context, item.getcPackage())) {
                        update(context, item);
                    } else {
                        insert(context, item);
                    }

                }
            }
            try {
                DeskGameDaoHelper.insertDeskGameDb(context, result.getItemList());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ParamBuilder {
        StringBuilder versionCodeBuilder = new StringBuilder();
        StringBuilder pkgNameBuilder = new StringBuilder();

        public void getParamBuilder(Context context) {
            AppInfo tempAppInfo;
            long tempLocalVersionCode;
            String tempPkgName;

            List<AppInfo> installedApkList = LocalApkManageService.getInstalledAppList(context);
            for (int index = 0; index < installedApkList.size(); index++) {
                tempAppInfo = installedApkList.get(index);
                if (tempAppInfo == null)
                    continue;
                tempLocalVersionCode = tempAppInfo.getLocalAppVersionCode();
                tempPkgName = tempAppInfo.getPkgName();

                if (tempLocalVersionCode > 0 && tempPkgName != null) {
                    versionCodeBuilder.append(tempLocalVersionCode);
                    pkgNameBuilder.append(tempPkgName);
                    if (index != installedApkList.size() - 1) {
                        versionCodeBuilder.append(",");
                        pkgNameBuilder.append(",");
                    }
                }
            }
        }
    }


    public static boolean queryPackage(Context context, String s) {
        try {
            List<MyGame> list = FreeStoreDataHelper.getHelper(context).getMyGameDao().queryForEq(MyGame.COL_APK_PKG_NAME, s);
            if (list == null) return false;
            return list.size() != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断应用是否是增量更新
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isPatchUpdate(Context context, String pkgName) {
        try {
            List<MyGame> list = FreeStoreDataHelper.getHelper(context).getMyGameDao().queryForEq(MyGame.COL_APK_PKG_NAME, pkgName);
            if (list == null) return false;
            if (list.size() == 0) {
                return false;
            } else {
                return list.get(0).getDownloadIsPatch() != AppConstants.UPDATE_TYPE_NOPATCH;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int isPatch(UpgradeAppListModel.ModelItem modelItem) {
        if (modelItem == null)
            return AppConstants.UPDATE_TYPE_NOPATCH;

        if (TextUtils.isEmpty(modelItem.getcPatch()) || TextUtils.isEmpty(modelItem.getcPreMd5()))
            return AppConstants.UPDATE_TYPE_NOPATCH;

        if (Integer.valueOf(modelItem.getcPatch()) == AppConstants.UPDATE_TYPE_PATCH) {
            String oldApkSource = ApkUtils.getSourceApkPath(FreeStoreApp.getContext(), modelItem.getcPackage());
            if (TextUtils.isEmpty(oldApkSource))
                return AppConstants.UPDATE_TYPE_NOPATCH;

            if (MD5Util.checkMD5(modelItem.getcPreMd5(), oldApkSource))
                return Integer.parseInt(modelItem.getcPatch());
            else
                return AppConstants.UPDATE_TYPE_NOPATCH;
        } else {
            return Integer.parseInt(modelItem.getcPatch());
        }
    }


    public static void update(Context context, UpgradeAppListModel.ModelItem modelItem) {
        try {
            Dao<MyGame, Integer> myGameDao = FreeStoreDataHelper.getHelper(context).getMyGameDao();
            List<MyGame> list = myGameDao.queryForEq(MyGame.COL_APK_PKG_NAME, modelItem.getcPackage());
            if (ListUtils.isEmpty(list))
                return;
            MyGame myGame = list.get(0);
            myGame.setApkId(modelItem.getnAppId());
            myGame.setAppSize(String.valueOf(modelItem.getiSize()));
            myGame.setApkLabel(modelItem.getsAppName());
            myGame.setIconURL(modelItem.getcIcon());
            if (!TextUtils.isEmpty(modelItem.getiVersionCode())) {
                myGame.setVersionCode(Integer.parseInt(modelItem.getiVersionCode()));
            }
            myGame.setVersionName(modelItem.getcVersionName());
            if (!TextUtils.isEmpty(modelItem.getcUpdate())) {
                myGame.setDownloadIsUpdate(Integer.parseInt(modelItem.getcUpdate()));
            }

            myGame.setDownloadIsPatch(isPatch(modelItem));
            myGame.setUpgradeDesc(modelItem.getUpdateDesc());
            myGame.setApkUrl(modelItem.getcDownloadUrl());
            myGame.setApkMD5(modelItem.getcMd5());
            myGame.setDownloadFlowFreeState(modelItem.getcFlowFree());
            myGame.setAppType(modelItem.getcAppType());
            myGame.setDiffUrl(modelItem.getcDiffUrl());
            myGame.setDiffSize(String.valueOf(modelItem.getiDiffSize()));
            myGame.setDiffMD5(modelItem.getcDiffMd5());
            myGame.setPackageName(modelItem.getcPackage());
            myGame.setTotalInstallNum(modelItem.getiTotalInstallNum());
            myGameDao.update(myGame);
            MainThreadBus.getInstance().post(new MyGameDBChangeEvent(queryForAppInfo(context)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insert(Context context, UpgradeAppListModel.ModelItem modelItem) {
        try {
            MyGame myGame = new MyGame();
            myGame.setApkId(modelItem.getnAppId());
            myGame.setAppSize(String.valueOf(modelItem.getiSize()));
            myGame.setApkLabel(modelItem.getsAppName());
            myGame.setIconURL(modelItem.getcIcon());
            if (!TextUtils.isEmpty(modelItem.getiVersionCode())) {
                myGame.setVersionCode(Integer.parseInt(modelItem.getiVersionCode()));
            }
            myGame.setVersionName(modelItem.getcVersionName());
            if (!TextUtils.isEmpty(modelItem.getcUpdate())) {
                myGame.setDownloadIsUpdate(Integer.parseInt(modelItem.getcUpdate()));
            }
            myGame.setDownloadIsPatch(isPatch(modelItem));
            myGame.setUpgradeDesc(modelItem.getUpdateDesc());
            myGame.setApkUrl(modelItem.getcDownloadUrl());
            myGame.setApkMD5(modelItem.getcMd5());
            myGame.setDownloadFlowFreeState(modelItem.getcFlowFree());
            myGame.setAppType(modelItem.getcAppType());
            myGame.setDiffUrl(modelItem.getcDiffUrl());
            myGame.setDiffSize(String.valueOf(modelItem.getiDiffSize()));
            myGame.setDiffMD5(modelItem.getcDiffMd5());
            myGame.setPackageName(modelItem.getcPackage());
            myGame.setTotalInstallNum(modelItem.getiTotalInstallNum());
            FreeStoreDataHelper.getHelper(context).getMyGameDao().create(myGame);
            MainThreadBus.getInstance().post(new MyGameDBChangeEvent(queryForAppInfo(context)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updateIsUpdate(Context context, String pkgName) {
        try {
            Dao<MyGame, Integer> myGameDao = FreeStoreDataHelper.getHelper(context).getMyGameDao();
            UpdateBuilder<MyGame, Integer> updateBuilder = myGameDao.updateBuilder();
            updateBuilder.updateColumnValue(MyGame.COL_DOWNLOAD_IS_UPDATE, 0);
            updateBuilder.where().eq(MyGame.COL_APK_PKG_NAME, pkgName);
            PreparedUpdate<MyGame> preparedUpdate = updateBuilder.prepare();
            myGameDao.update(preparedUpdate);
            MainThreadBus.getInstance().post(new MyGameDBChangeEvent(queryForAppInfo(context)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新 忽略更新的version code
     *
     * @param context
     * @param pkgName
     * @param ignoreVersionCode
     */
    public static void updateUpgradeIgnoreCode(Context context, String pkgName, int ignoreVersionCode) {
        try {
            Dao<MyGame, Integer> myGameDao = FreeStoreDataHelper.getHelper(context).getMyGameDao();
            UpdateBuilder<MyGame, Integer> updateBuilder = myGameDao.updateBuilder();
            updateBuilder.updateColumnValue(MyGame.COL_UPGRADE_IGNORE_VERSION_CODE, ignoreVersionCode);
            updateBuilder.where().eq(MyGame.COL_APK_PKG_NAME, pkgName);
            PreparedUpdate<MyGame> preparedUpdate = updateBuilder.prepare();
            myGameDao.update(preparedUpdate);
            MainThreadBus.getInstance().post(new MyGameDBChangeEvent(queryForAppInfo(context)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class MyTask extends AsyncTask<String, Integer, List<AppInfo>> {
        private MyGameCallback myGameCallback;
        private Context context;

        public MyTask(Context context, MyGameCallback myGameCallback) {
            this.context = context;
            this.myGameCallback = myGameCallback;
        }

        @Override
        protected List<AppInfo> doInBackground(String... params) {
            List<AppInfo> appInfoList = queryForAppInfo(context);
            return appInfoList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            myGameCallback.Callback(appInfos);
        }
    }

    /**
     * 开启线程查询my_game_table已经安装的程序并且组成appInfo
     **/
    public static synchronized AsyncTask queryForAppInfoInThread(Context context, MyGameCallback myGameCallback) {
        return new MyTask(context, myGameCallback).execute();
    }

    /**
     * 查询my_game_table已经安装的程序并且组成appInfo
     **/
    public static synchronized List<AppInfo> queryForAppInfo(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        try {
            List<MyGame> list = FreeStoreDataHelper.getHelper(context).getMyGameDao().queryForAll();
            AppInfo appInfo;
            for (MyGame myGame : list) {
                if (!ComUtil.checkApkExist(context, myGame.getPackageName())) {
                    continue;
                }
                if (myGame.getPackageName().equals("com.snailgame.cjg")) {
                    continue;
                }
                appInfo = new AppInfo();
                appInfo.setAppId(myGame.getApkId());
                if (!TextUtils.isEmpty(myGame.getAppSize())) {
                    appInfo.setApkSize(Long.parseLong(myGame.getAppSize()));
                }
                appInfo.setIcon(myGame.getIconURL());
                appInfo.setPkgName(myGame.getPackageName());
                appInfo.setVersionName(myGame.getVersionName());
                appInfo.setVersionCode(myGame.getVersionCode());
                appInfo.setAppName(myGame.getApkLabel());
                appInfo.setIsUpdate(myGame.getDownloadIsUpdate());
                appInfo.setIsPatch(myGame.getDownloadIsPatch());
                appInfo.setApkUrl(myGame.getApkUrl());
                appInfo.setMd5(myGame.getApkMD5());
                appInfo.setTotalIntsallNum(myGame.getTotalInstallNum());
                appInfo.setDiffUrl(myGame.getDiffUrl());
                if (!TextUtils.isEmpty(myGame.getDiffSize())) {
                    appInfo.setDiffSize(Long.parseLong(myGame.getDiffSize()));
                }
                appInfo.setDiffMd5(myGame.getDiffMD5());
                appInfo.setUpgradeIgnoreCode(myGame.getUpgradeIgnoreVersionCode());
                appInfo.setUpgradeDesc(myGame.getUpgradeDesc());
                appInfo.setcAppType(myGame.getAppType());
                appInfo.setInstalledApkVersionName(ComUtil.getVersionNameByPackage(myGame.getPackageName(), context));
                appInfoList.add(appInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appInfoList;
    }

    public interface MyGameCallback {
        void Callback(List<AppInfo> appInfos);
    }

}
