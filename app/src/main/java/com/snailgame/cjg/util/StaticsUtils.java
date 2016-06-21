package com.snailgame.cjg.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.igexin.sdk.PushManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.mobilesdk.SnailCommplatform;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: sunxy Date: 14-2-17 Time: 下午6:58
 */
public class StaticsUtils {
    public static final int STATICS_STATUS_START_DOWNLOAD = 0;
    public static final int STATICS_STATUS_DOWNLOAD_COMPLETED = 1;
    public static final int STATICS_STATUS_INSTALL_COMPLETED = 2;
    public static final int STATICS_STATUS_UNINSTALL = 3;
    public static final int STATICS_STATUS_UPGRADE = 4;
    public static final int STATICS_STATUS_UPGRADE_IN_PATCH = 5;
    private static final String DOWNLOAD_SUCCESS = "1";
    private static final String DOWNLOAD_ERROR = "0";
    private static final String OPEN_GAME_CHANNEL_ID = "8";
    private static String mac = "", ip = "";
    private static String account = "";
    private static String uid = "";
    private static String iNetCate = "0";

    private static final String FREESTORE_STATICS_URL = "http://dm.app.snail.com/collect/freestore.json";    // 免商店后台统计

    private final static String event = "http://dd.woniu.com/_event.gif";

    /* 入口程序初始化 */
    public static void init(WindowManager windowManager, Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        // 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            mac = info.getMacAddress();
            ip = int2ip(info.getIpAddress());
        }
        account = LoginSDKUtil.getAccount(context);
        uid = LoginSDKUtil.getLoginUin(context);
    }

    /**
     * 重置account
     *
     * @param context
     */
    public static void resetAccount(Context context) {
        account = LoginSDKUtil.getAccount(context);
        uid = LoginSDKUtil.getLoginUin(context);
    }

    /**
     * 统计下载开始
     * 数据库中创建该条记录的时候即为用户尝试下载
     *
     * @param context
     * @param gameId
     */
    public static void startDownload(Context context, long gameId) {
        StaticsUtils.beginDownload(String.valueOf(gameId));
        StaticsUtils.serverDownloadStatus(context, String.valueOf(gameId), String.valueOf(StaticsUtils.STATICS_STATUS_START_DOWNLOAD));
    }

    /**
     * 下载成功
     *
     * @param context
     * @param gameId
     */
    public static void downloadSuccess(Context context, long gameId) {
        StaticsUtils.downloadResult(String.valueOf(gameId), DOWNLOAD_SUCCESS);
        StaticsUtils.serverDownloadStatus(context, String.valueOf(gameId),
                String.valueOf(StaticsUtils.STATICS_STATUS_DOWNLOAD_COMPLETED));
    }

    /**
     * 下载失败
     *
     * @param gameId
     */
    public static void downloadError(long gameId) {
        StaticsUtils.downloadResult(String.valueOf(gameId), DOWNLOAD_ERROR);
    }


    /**
     * 统计应用内打开游戏
     */
    public static void openGameInFreeStore(int game_id) {
        send(buildStaticsUrl(Action.OPEN_GAME_IN_FREE_STORE, String.valueOf(game_id), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.OPEN_GAME_IN_FREE_STORE, "", String.valueOf(game_id)), "");
    }

    /**
     * 详情曝光
     *
     * @param game_id
     */
    public static void AppDetailExposure(int game_id) {
        send(buildStaticsUrl(Action.OPEN_APP_DETAIL, String.valueOf(game_id), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.OPEN_APP_DETAIL, "", String.valueOf(game_id)), "");
    }

    /**
     * 开始下载
     *
     * @param game_id
     */
    public static void beginDownload(String game_id) {
        send(buildStaticsUrl(Action.DOWNLOAD_START, game_id, ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.DOWNLOAD_START, "", game_id), "");
    }

    /**
     * 下载结束以及结果
     *
     * @param game_id
     * @param act_result
     */
    public static void downloadResult(String game_id, String act_result) {
        send(buildStaticsUrl(Action.DOWNLOAD_RESULT, game_id, act_result));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.DOWNLOAD_RESULT, act_result, game_id), "");
    }

    /**
     * 前后台切换
     */
    public static void onResume() {
        send(buildStaticsUrl(Action.ON_RESUME, String.valueOf(AppConstants.APP_ID), "1"), true);
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ON_RESUME, "1", ""), "");
    }

    /**
     * 统计当前手机打开的游戏
     *
     * @param game_id
     */
    public static void openGame(int game_id) {
        send(buildStaticsUrl(Action.OPEN_GAME, String.valueOf(game_id), ""), OPEN_GAME_CHANNEL_ID);
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.OPEN_GAME, "", String.valueOf(game_id)), OPEN_GAME_CHANNEL_ID);
    }

    /**
     * 一下两个方法专门用于手动设置id
     *
     * @param url
     * @param channelId
     */
    private static void send(String url, String channelId) {
        send(url, false, null, channelId);
    }

    private static void send(String url, boolean b, String addData, String channelId) {
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), url, b, addData, channelId);

    }

    /**
     * 安装成功
     *
     * @param game_id
     */
    public static void installSuccess(int game_id) {
        send(buildStaticsUrl(Action.APP_INSTALL_SUCCESS, String.valueOf(game_id), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.APP_INSTALL_SUCCESS, "", String.valueOf(game_id)), "");
    }

    /**
     * 更新成功
     *
     * @param game_id
     */
    public static void updateSuccess(int game_id) {
        send(buildStaticsUrl(Action.APP_UPDATE_SUCCESS, String.valueOf(game_id), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.APP_UPDATE_SUCCESS, "", String.valueOf(game_id)), "");
    }


    /**
     * 卸载成功
     *
     * @param game_id
     */
    public static void unInstall(String game_id) {
        send(buildStaticsUrl(Action.APP_UNINSTALL_SUCCESS, game_id, ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.APP_UNINSTALL_SUCCESS, "", game_id), "");
    }

    /**
     * 打开应用本身
     */
    public static void onCreate() {
        send(buildStaticsUrl(Action.ON_CREATE, String.valueOf(AppConstants.APP_ID), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ON_CREATE, "", ""), "");
    }

    /**
     * 渠道激活
     */
    public static void activeChannelId() {
        send(buildStaticsUrl(Action.ACTIVE_CHANNELID, String.valueOf(AppConstants.APP_ID), ""));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ACTIVE_CHANNELID, "", ""), "");
    }

    /**
     * 前后台切换
     */
    public static void onPause(long pauseTime) {
        String url = buildStaticsUrl(Action.ON_PAUSE, String.valueOf(AppConstants.APP_ID), "");
        send(url, true, String.valueOf(pauseTime));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ON_PAUSE, "", String.valueOf(pauseTime)), "");
    }

    /**
     * PV统计
     */
    public static void onPV(int[] route) {
        String url = buildStaticsUrl(Action.ON_PV, String.valueOf(AppConstants.APP_ID), "");
        send(url, false, intArray2String(route));
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ON_PV, "", intArray2String(route)), "");
    }

    /**
     * 尝试下载统计
     */
    public static void onDownload(int[] route) {
        String url = buildStaticsUrl(Action.ON_DOWNLOAD, String.valueOf(AppConstants.APP_ID), "");
        send(url, false, intArray2String(route));

        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ON_DOWNLOAD, "", intArray2String(route)), "");
    }

    /**
     * 预约免卡
     */
    public static void orderFreecard() {
        send(buildStaticsUrl(Action.ORDER_FREECARD, String.valueOf(AppConstants.APP_ID), "1"), true);
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.ORDER_FREECARD, "1", ""), "");
    }

    /**
     * 登录免厅
     */
    public static void loginFreehall() {
        send(buildStaticsUrl(Action.LOGIN_FREEHALL, String.valueOf(AppConstants.APP_ID), "1"), true);
        // 免商店后台统计
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), buildFreeStoreStaticsUrl(Action.LOGIN_FREEHALL, "1", ""), "");
    }


    /**
     * 统计中心统计
     *
     * @param actId
     * @param gameID
     * @param actResult
     * @return
     */
    private static String buildStaticsUrl(Enum<Action> actId, String gameID, String actResult) {
        return JsonUrl.getJsonUrl().STATICS_URL + "?"
                + "game_id=" + gameID
                + "&server_id="
                + "&act_id=" + actId
                + "&act_result=" + actResult
                + "&act_time="
                + "&acount=" + account
                + "&role_name="
                + "&c_ip=" + ip
                + "&c_type=19"
                + "&server_ip="
                + "&mac_addr=" + mac;
    }


    /**
     * 免商店后台统计
     *
     * @param actId
     * @param actResult
     * @param addData
     * @return
     */
    private static String buildFreeStoreStaticsUrl(Enum<Action> actId, String actResult, String addData) {
        return FREESTORE_STATICS_URL + "?"
                + "eId=" + actId
                + "&aId=" + uid
                + "&acc=" + account
                + "&s=" + System.currentTimeMillis()
                + "&e="
                + "&r=" + actResult
                + "&c=" + addData;
    }

    private static void send(String url) {
        send(url, false, null);
    }

    private static void send(String url, boolean b) {
        send(url, b, null);
    }

    private static void send(String url, boolean b, String addData) {
        LoginSDKUtil.commitOneEvent(FreeStoreApp.getContext(), url, b, addData);

    }

    public static void serverDownloadStatus(Context context, String nAppId, String cStatus) {
        String uid = IdentityHelper.getUid(context);
        String identity = IdentityHelper.getIdentity(context);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("iAppId", nAppId);
        parameters.put("nAppId", String.valueOf(AppConstants.APP_ID));
        parameters.put("cIdentity", identity);
        parameters.put("nUserId", uid);
        parameters.put("cImei", SnailCommplatform.getInstance().getCIMEI(FreeStoreApp.getContext()));
        parameters.put("cStatus", cStatus);
        parameters.put("iNetCate", iNetCate);
        parameters.put("cChannel", ChannelUtil.getChannelID());
        parameters.put("iPlatformId", String.valueOf(AppConstants.PLATFORM_ID));
        PostData(JsonUrl.getJsonUrl().JSON_URL_STATICS_APP, parameters);
    }

    public static void serverOnResume(Context context) {
        if (IdentityHelper.isLogined(context)) {
            String uid = IdentityHelper.getUid(context);
            String identity = IdentityHelper.getIdentity(context);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("nAppId", String.valueOf(AppConstants.APP_ID));
            parameters.put("nUserId", uid);
            parameters.put("cIdentity", identity);
            PostData(JsonUrl.getJsonUrl().JSON_URL_OPEN_APP, parameters);
        }
    }

    public static void serverOnPause(Context context) {
        if (IdentityHelper.isLogined(context) && !TextUtils.isEmpty(GlobalVar.getInstance().getStaticsVarString())) {
            String uid = IdentityHelper.getUid(context);
            String identity = IdentityHelper.getIdentity(context);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("nAppId", String.valueOf(AppConstants.APP_ID));
            parameters.put("cIdentity", identity);
            parameters.put("nUserId", uid);
            parameters.put("nVisitLogId", GlobalVar.getInstance().getStaticsVarString());
            PostData(JsonUrl.getJsonUrl().JSON_URL_CLOSE_APP, parameters);
        }
    }

    public static String int2ip(long ipInt) {
        return String.valueOf(ipInt & 0xFF) + "."
                + ((ipInt >> 8) & 0xFF) + "."
                + ((ipInt >> 16) & 0xFF) + "."
                + ((ipInt >> 24) & 0xFF);
    }

    private static String intArray2String(int[] route) {
        StringBuilder result = new StringBuilder();
        if (route != null && route.length > 0) {
            int length = route.length;
            for (int i = 0; i < length; i++) {
                if (i == length - 1)
                    result.append(route[i]);
                else
                    result.append(route[i]).append(":");
            }
        }
        return result.toString();
    }

    public static void sendGETUIEvent(Context context, int eventId, String msgId, String taskId) {
        // 个推统计自定义事件
        PushManager.getInstance().sendFeedbackMessage(context, taskId, msgId, eventId);

//        // umeng统计自定义事件
//        HashMap<String,String> map = new HashMap<String,String>();
//        map.put(AppConstants.GETUI_KEY_MSGID, msgId);
//        map.put(AppConstants.GETUI_KEY_TASKID, taskId);
//        MobclickAgent.onEvent(context, String.valueOf(eventId), map);
    }

    private enum Action {
        //app后台切前台，actionResult固定为1
        ON_RESUME(10),
        //app前台切后台，extend_data传从程序进入前台到切到后台的时间
        ON_PAUSE(20),
        //打开自己
        ON_CREATE(32),
        //打开单个app详情页面
        OPEN_APP_DETAIL(36002),
        //下载开始
        DOWNLOAD_START(50512),
        //下载结束，actresult 成功 1 失败 0
        DOWNLOAD_RESULT(50513),
        //监控免商店中下载的app在指定时间是否被打开
        OPEN_GAME(36101),
        //app安装成功
        APP_INSTALL_SUCCESS(50510),
        //app卸载成功
        APP_UNINSTALL_SUCCESS(50511),
        //app激活信息
        ACTIVE_CHANNELID(30),
        //免商店内打开app
        OPEN_GAME_IN_FREE_STORE(36003),
        //app更新成功信息
        APP_UPDATE_SUCCESS(50514),
        //预约免卡
        ORDER_FREECARD(36102),
        //登录免厅
        LOGIN_FREEHALL(36103),
        //PV
        ON_PV(36004),
        //尝试下载
        ON_DOWNLOAD(50515);

        private int id;

        Action(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }

    /**
     * post data
     *
     * @param url
     * @param params
     */
    private static void PostData(final String url, Map<String, String> params) {
        FSRequestHelper.newPostRequest(url, "", String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (!url.equals(JsonUrl.getJsonUrl().JSON_URL_OPEN_APP))
                    return;

                try {
                    JSONObject object = new JSONObject(result);
                    if ((object != null) && object.has("val")) {
                        GlobalVar.getInstance().setStaticsVarString(object.getString("val"));
                    }
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, params);
    }


    /**
     * post data
     *
     * @param url
     * @param params
     */
    private static void PostDataWithNoResponse(final String url, Map<String, String> params) {
        FSRequestHelper.newPostRequest(url, "", String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, params);
    }

    /**
     * 自定义
     *
     * @param context    上下文
     * @param eventID    事件ID
     * @param channelID  媒体ID
     * @param account    帐号
     * @param gameID     游戏ID
     * @param resolution 分辨率
     * @param phoneType  手机系统类型  1代表IOS  2代表安卓 3代表WP
     */
    public static void event(final Context context, final String eventID, final String channelID,
                             final String account, final String gameID, final String resolution, final String phoneType) {
        StringBuilder sb = new StringBuilder();
        sb.append(eventID + "|");//事件ID
        sb.append(channelID + "|");//媒体ID
        sb.append(PhoneUtil.getMacAddress(context) + "|");//MAC
        sb.append(PhoneUtil.getDeviceUUID(context) + "|");//设备ID
        sb.append(ComUtil.getSelfVersionName() + "|");//游戏版本
        sb.append(URLEncoder.encode(account == null ? "" : account) + "|");//帐号
        sb.append(gameID + "|");//游戏ID
        sb.append(Build.MODEL + "|");//机型
        sb.append(Build.VERSION.RELEASE + "|");//系统版本
        sb.append(resolution + "|");//分辨率
        sb.append(phoneType);//手机系统类型  1代表IOS  2代表安卓 3代表WP

        Map<String, String> parameters = new HashMap<>();
        parameters.put("data", sb.toString());
        PostDataWithNoResponse(event, parameters);
    }


    /**
     * 获取网络状态
     *
     * @return
     */
    public static String getNetEnv() {
        JSONObject netEnv = new JSONObject();
        try {
            netEnv.put("IP", getLocalIpAddress());
            String netType = PhoneUtil.getNetType(FreeStoreApp.getContext());
            if (!TextUtils.isEmpty(netType) && !netType.equals("NONE") && !netType.equals("UNKNOWN"))
                netEnv.put("network", PhoneUtil.getNetType(FreeStoreApp.getContext()));
            netEnv.put("device", "mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return netEnv.toString();
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String ip = "";
        WifiManager wifiMgr = (WifiManager) FreeStoreApp.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            ip = StaticsUtils.int2ip(info.getIpAddress());
        }

        return ip;
    }


    /**
     * 获取位置类型
     *
     * @return
     */
    public static String getPosType() {
        return "ll";
    }


    /**
     * 获取位置
     *
     * @return
     */
    public static String getPosition() {
        String lontitude = SharedPreferencesHelper.getInstance().getValue(AppConstants.LONTITUDE, "");
        String latiude = SharedPreferencesHelper.getInstance().getValue(AppConstants.LATITUDE, "");
        if (TextUtils.isEmpty(lontitude) || TextUtils.isEmpty(latiude))
            return "";
        else
            return lontitude + ":" + latiude;
    }


    /**
     * 资讯用户点击行为保存
     *
     * @param newsId
     */
    public static void newsBehaviourClick(String newsId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("iNewsId", newsId);
        parameters.put("cPosType", getPosType());
        parameters.put("cPosition", getPosition());
        parameters.put("cNetEnv", getNetEnv());
        parameters.put("cImei", PhoneUtil.getDeviceUUID(FreeStoreApp.getContext()));
        parameters.put("iPlatformId", String.valueOf(AppConstants.PLATFORM_ID));
        PostData(JsonUrl.getJsonUrl().JSON_URL_NEWS_BEHAVIOUR_CLICK, parameters);
    }
}
