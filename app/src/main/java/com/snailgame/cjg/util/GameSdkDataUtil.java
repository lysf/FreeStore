package com.snailgame.cjg.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.snailgame.cjg.common.server.GameSourceGetService;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.statistics.TrafficStatisticsUtil;
import com.snailgame.cjg.util.model.GameSdkDataModel;
import com.snailgame.fastdev.util.ListUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏SDK数据保存
 * Created by xixh on 2015/6/23.
 */
public class GameSdkDataUtil {
    /**
     * 旧版数据保存
     */
    public static final String SDDATA_FILE_NAME = Environment.getExternalStorageDirectory()
            + "/FreeStore/snail_data";

    /**
     * 检测SDK所用数据
     */
    public static void checkSdkData() {
        new Thread() {
            @Override
            public void run() {
                saveData();
                addDataV1(FreeStoreApp.getContext().getPackageName());
            }
        }.start();
    }

    /**
     * 将应用版本号、渠道号、平台号保存到文件
     */
    private static void saveData() {
        String data = FileUtil.readFileSdcard(SDDATA_FILE_NAME);
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject obj = new JSONObject(data);
                String platformId = obj.getString(FileUtil.KEY_PLATFORM_ID);
                String channelId = obj.getString(FileUtil.KEY_CHANNEL_ID);
                String versionName = obj.getString(FileUtil.KEY_PLATFORM_VERSION);
                if (platformId.equals(String.valueOf(AppConstants.PLATFORM_ID)) && channelId.equals(ChannelUtil.getChannelID()) && versionName.equals(ComUtil.getSelfVersionName()))
                    return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject saveObj = new JSONObject();
        try {
            saveObj.put(FileUtil.KEY_CHANNEL_ID, ChannelUtil.getChannelID());
            saveObj.put(FileUtil.KEY_PLATFORM_VERSION, ComUtil.getSelfVersionName());
            saveObj.put(FileUtil.KEY_PLATFORM_ID, String.valueOf(AppConstants.PLATFORM_ID));
            FileUtil.writeFileSdcard(SDDATA_FILE_NAME, saveObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新版数据保存 V1
     */
    public static final String SDDATA_FILE_NAME_V1 = Environment.getExternalStorageDirectory()
            + "/snail/snail_data_v1";

    /**
     * 添加数据
     *
     * @param pkgName
     */
    public static void addDataV1(String pkgName) {
        if (TextUtils.isEmpty(pkgName))
            return;

        GameSdkDataModel datas = null;

        String data = FileUtil.readFileSdcard(SDDATA_FILE_NAME_V1);
        if (!TextUtils.isEmpty(data)) {
            datas = JSON.parseObject(data, GameSdkDataModel.class);
            if (datas != null && !ListUtils.isEmpty(datas.getInfos())) {
                for (GameSdkDataModel.BaseData appInfo : datas.getInfos()) {
                    if (appInfo.getCPackageName().equals(pkgName)) {
                        appInfo.setCChannel(ChannelUtil.getChannelID());
                        appInfo.setCPlatformVersion(ComUtil.getSelfVersionName());
                        appInfo.setIPlatformId(String.valueOf(AppConstants.PLATFORM_ID));
                        appInfo.setITimeStamp(TrafficStatisticsUtil.getCurrentTime());
                        saveDataV1(datas);
                        return;
                    }
                }
            }
        }

        if (datas == null) {
            datas = new GameSdkDataModel();
        }

        if (datas.getInfos() == null)
            datas.setInfos(new ArrayList<GameSdkDataModel.BaseData>());


        GameSdkDataModel.BaseData obj = new GameSdkDataModel.BaseData();
        obj.setCPackageName(pkgName);
        obj.setCChannel(ChannelUtil.getChannelID());
        obj.setCPlatformVersion(ComUtil.getSelfVersionName());
        obj.setIPlatformId(String.valueOf(AppConstants.PLATFORM_ID));
        obj.setITimeStamp(TrafficStatisticsUtil.getCurrentTime());
        datas.getInfos().add(obj);
        saveDataV1(datas);
    }

    /**
     * 删除数据
     *
     * @param pkgName
     */
    public static void removeDataV1(String pkgName) {
        if (TextUtils.isEmpty(pkgName))
            return;

        String data = FileUtil.readFileSdcard(SDDATA_FILE_NAME_V1);
        if (!TextUtils.isEmpty(data)) {
            GameSdkDataModel datas = JSON.parseObject(data, GameSdkDataModel.class);
            if (datas != null && !ListUtils.isEmpty(datas.getInfos())) {
                for (GameSdkDataModel.BaseData appInfo : datas.getInfos()) {
                    if (appInfo.getCPackageName().equals(pkgName)) {
                        datas.getInfos().remove(appInfo);
                        saveDataV1(datas);
                        return;
                    }
                }
            }
        }
    }


    /**
     * 保存数据
     *
     * @param datas
     */
    public static void saveDataV1(GameSdkDataModel datas) {
        FileUtil.writeFileSdcard(SDDATA_FILE_NAME_V1, JSON.toJSONString(datas));
    }

    /**
     * 检查游戏ids
     */
    public static void checkGameIds(Context context) {
        String gameIds = SharedPreferencesUtil.getInstance().getSdkGameId();
        if (TextUtils.isEmpty(gameIds))
            return;

        List<Long> list = JSON.parseArray(gameIds, Long.class);
        if (list == null || list.size() == 0)
            return;

        for (long id : list) {
            context.startService(GameSourceGetService.newIntent(context, id));
        }

    }

    /**
     * 检查游戏ids
     */
    public static void checkGameId(Context context, long id) {
        if (id == 0)
            return;

        context.startService(GameSourceGetService.newIntent(context, id));
    }

    /**
     * 保存游戏id
     *
     * @param id
     */
    public static void addGameId(long id) {
        List<Long> list = null;
        String gameIds = SharedPreferencesUtil.getInstance().getSdkGameId();
        if (TextUtils.isEmpty(gameIds)) {
            list = new ArrayList<>();
            list.add(id);
        } else {
            list = JSON.parseArray(gameIds, Long.class);
            if (list == null) {
                list = new ArrayList<>();
                list.add(id);
            } else {
                boolean hasIn = false;
                for (long gameid : list) {
                    if (gameid == id) {
                        hasIn = true;
                        break;
                    }
                }

                if (!hasIn)
                    list.add(id);
            }
        }

        SharedPreferencesUtil.getInstance().setSdkGameId(JSONArray.toJSONString(list));
    }

    /**
     * 删除游戏id
     *
     * @param id
     */
    public static void removeGameId(long id) {
        List<Long> list = null;
        String gameIds = SharedPreferencesUtil.getInstance().getSdkGameId();
        if (TextUtils.isEmpty(gameIds)) {
            list = new ArrayList<>();
        } else {
            list = JSON.parseArray(gameIds, Long.class);
            if (list == null) {
                list = new ArrayList<>();
            } else {
                boolean hasIn = false;
                for (long gameid : list) {
                    if (gameid == id) {
                        hasIn = true;
                        break;
                    }
                }

                if (!hasIn)
                    list.remove(id);
            }
        }

        SharedPreferencesUtil.getInstance().setSdkGameId(JSONArray.toJSONString(list));
    }
}
