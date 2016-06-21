package com.snailgame.cjg.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.SystemConfModel;
import com.snailgame.cjg.common.model.SystemConfig;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shenzaih on 2014/4/17.
 */
public class SystemConfigUtil {
    static String TAG = SystemConfigUtil.class.getName();

    private static SystemConfigUtil systemConfigUtil;

    private SystemConfig systemConfig;

    public static SystemConfigUtil getInstance() {
        if (systemConfigUtil == null)
            systemConfigUtil = new SystemConfigUtil();
        return systemConfigUtil;
    }

    private SystemConfigUtil() {
        //TODO 读取数据
        systemConfig = new SystemConfig();

    }


    public void getUpdateConfigData(String url) {
        FSRequestHelper.newGetRequest(url, TAG, SystemConfModel.class, new IFDResponse<SystemConfModel>() {
            @Override
            public void onSuccess(SystemConfModel result) {
                new ConfigData(result).start();
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, false, true, new ExtendJsonUtil());
    }


    /**
     * 获取 配置信息
     */
    public class ConfigData extends Thread {
        SystemConfModel model;

        public ConfigData(SystemConfModel model) {
            this.model = model;
        }

        public void run() {
            if (model == null || model.getItemList() == null) {
                return;
            }

            for (SystemConfModel.ModelItem conf : model.getItemList()) {
                if (conf.getcFuncName().equals("game_update_time") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setGameUpdateIntervel(Long.valueOf(conf.getsConf()));
                    systemConfig.setGameUpdateTimeSwitch(conf.getcStatus());
                } else if (conf.getcFuncName().equals("appstore_update_time") && !TextUtils.isEmpty(conf.getsConf())) {
                    try {
                        systemConfig.setAppStoreUpdateTime(Long.valueOf(conf.getsConf()));
                    } catch (Exception e) {

                    }
                    systemConfig.setAppStoreUpdateTimeSwitch(conf.getcStatus());
                } else if (conf.getcFuncName().equals("app_remind_time") && !TextUtils.isEmpty(conf.getsConf())) {
                    try {
                        systemConfig.setAppRemindTime(Integer.valueOf(conf.getsConf()));
                    } catch (Exception e) {

                    }
                    systemConfig.setAppRemindTimeSwitch(conf.getcStatus());
                } else if (conf.getcFuncName().equals("share_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setShareUrl(conf.getsConf());
                    systemConfig.setShareTitle(conf.getsFuncName());
                } else if (conf.getcFuncName().equals("splash_url_v2") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setSplashUrl(conf.getsConf());
                    systemConfig.setSplashSwitch(conf.getcStatus());

                    final String filePath = FileUtil.SD_IMAGE_PATH;
                    final String picUrl = conf.getsConf();
                    final String fileName = MD5Util.md5Encrypt(picUrl);
                    final String fullName = filePath + fileName;

                    Bitmap splashBitmap = BitmapManager.getInstance().getBitmapCache().get(fileName);
                    if (splashBitmap == null) {
                        new Thread() {
                            public void run() {
                                File file = new File(fullName);
                                if (!file.exists()) {
                                    downloadSplashPic(picUrl, filePath, fileName);
                                }
                            }
                        }.start();
                    }
                } else if (conf.getcFuncName().equals("splash_image_appid") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setSplashImageAppId(conf.getsConf());
                } else if (conf.getcFuncName().equals("splash_stay_time") && !TextUtils.isEmpty(conf.getsConf())) {
                    try {
                        systemConfig.setSplashStayTime(Long.valueOf(conf.getsConf()));
                    } catch (Exception e) {

                    }

                } else if (conf.getcFuncName().equals("not_login_decl_v2") && !TextUtils.isEmpty(conf.getsConf())) {
                    //弹窗配置信息
                    //未登录首次下载声明
                    systemConfig.setDownloadNotLogin(conf.getsConf());
                } else if (conf.getcFuncName().equals("free_card_control") && !TextUtils.isEmpty(conf.getsConf())) {
                    //免卡墙模块功能停用原因展现
                    systemConfig.setStopFreeCardFunc(conf.getcStatus());
                    String confString = conf.getsConf();
                    if (confString != null && !TextUtils.isEmpty(confString)) {
                        try {
                            String s = conf.getsConf();
                            JSONObject object = JSON.parseObject(s);

                            if (object.containsKey("desc")) {
                                systemConfig.setStopFreeCardDes(object.getString("desc"));
                            }
                            if (object.containsKey("versionCode")) {
                                systemConfig.setScoreFreeCardStopVersion(object.getString("versionCode"));
                            }
                            if (object.containsKey("channelIds")) {
                                systemConfig.setHideFreeCardChannelIds(object.getString("channelIds"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (conf.getcFuncName().equals("forum_control") && !TextUtils.isEmpty(conf.getsConf())) {
                    //论坛墙模块功能停用原因展现
                    systemConfig.setStopForumFunc(conf.getcStatus());
                    try {
                        JSONObject object = JSON.parseObject(conf.getsConf());

                        if (object.containsKey("desc")) {
                            systemConfig.setStopForumDes(object.getString("desc"));
                        }
                        if (object.containsKey("versionCode")) {
                            systemConfig.setScoreForumStopVersion(object.getString("versionCode"));
                        }
                        if (object.containsKey("channelIds")) {
                            systemConfig.setHideForumChannelIds(object.getString("channelIds"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (conf.getcFuncName().equals("quit_explain") && !TextUtils.isEmpty(conf.getsConf())) {
                    //双击back键 弹出窗口的文字为后台配置
                    systemConfig.setExistDialogDes(conf.getsConf());
                    systemConfig.setShowExistDialog(conf.getcStatus());
                } else if (conf.getcFuncName().equals("homePage_pop") && !TextUtils.isEmpty(conf.getsConf())) {
                    //弹出次数
                    try {
                        systemConfig.setPopupTimes(Integer.parseInt(conf.getsConf()));
                    } catch (Exception e) {

                    }
                } else if (conf.getcFuncName().equals("free_card_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    //免卡页面URL
                    systemConfig.setFreeCardUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("cool_play_url")) {
                    //酷玩页面URL
                    systemConfig.setCoolPlayLock(conf.getcStatus() == 1);
                    String coolPlayUrl = conf.getsConf();
                    if (coolPlayUrl == null)
                        coolPlayUrl = "";
                    systemConfig.setCoolPlayUrl(coolPlayUrl);

                } else if (conf.getcFuncName().equals("order_address_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    //酷玩收货地址
                    systemConfig.setOrderAddressUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("my_order_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    //酷玩我的订单
                    systemConfig.setMyOrderUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("shopping_car_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    //酷玩购物车
                    systemConfig.setShoppingCarUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("bbs_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    //论坛页面URL
                    systemConfig.setBbsUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("voucher_ad") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setVoucherAdStstus(conf.getcStatus());

                    //代金券广告
                    try {
                        JSONObject object = JSON.parseObject(conf.getsConf());

                        if (object.containsKey("pageId")) {
                            systemConfig.setVoucherAdPageId(object.getString("pageId"));
                        }
                        if (object.containsKey("pageTitle")) {
                            systemConfig.setVoucherAdPageTitle(object.getString("pageTitle"));
                        }
                        if (object.containsKey("type")) {
                            systemConfig.setVoucherAdType(object.getString("type"));
                        }
                        if (object.containsKey("url")) {
                            systemConfig.setVoucherAdUrl(object.getString("url"));
                        }
                        if (object.containsKey("imgUrl")) {
                            systemConfig.setVoucherAdImgUrl(object.getString("imgUrl"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (conf.getcFuncName().equals("user_level_info_url_v2") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setUserLevelInfoUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("itunes_rankList_country") && !TextUtils.isEmpty(conf.getsConf())) {

                    systemConfig.setCountryLists(conf.getsConf());
                } else if (conf.getcFuncName().equals("skin_packages") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setSkinPackages(conf.getsConf());
                } else if (conf.getcFuncName().equals("recharge_currency_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setRechargeCurrencyUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("recharge_game_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setRechargeGameUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("recharge_flow_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setRechargeFlowUrl(conf.getsConf());
                }else if (conf.getcFuncName().equals("flow_privilege_open_url") && !TextUtils.isEmpty(conf.getsConf())) {
                    systemConfig.setFlowPrivilegeUrl(conf.getsConf());
                } else if (conf.getcFuncName().equals("voucher_woniu_title")) {
                    systemConfig.setVoucherWoniuSwitch(conf.getcStatus() == 1);
                    systemConfig.setVoucherWoniuTitle(conf.getsConf());
                } else if (conf.getcFuncName().equals("hot_search")) {
                    systemConfig.setHotSearch(conf.getsConf());
                } else if (conf.getcFuncName().equals("member_introduce_url")) {
                    systemConfig.setMemberIntroduce(conf.getsConf());
                } else if (conf.getcFuncName().equals("cxb_config") && !TextUtils.isEmpty(conf.getsConf())) {
                    //畅享宝配置
                    try {
                        JSONObject object = JSON.parseObject(conf.getsConf());

                        if (object.containsKey("status")) {
                            systemConfig.setCxbStatus(object.getBoolean("status"));
                        }
                        if (object.containsKey("iconUrl")) {
                            systemConfig.setCxbIconUrl(object.getString("iconUrl"));
                        }

                        if (object.containsKey("title")) {
                            systemConfig.setCxbTitle(object.getString("title"));
                        }

                        if (object.containsKey("packageName")) {
                            systemConfig.setCxbPackageName(object.getString("packageName"));
                        }
                        if (object.containsKey("appId")) {
                            systemConfig.setCxbAppId(object.getIntValue("appId"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(conf.getcFuncName().equals("heyue_treaty_url") && !TextUtils.isEmpty(conf.getsConf())){
                    //合约机协议
                    systemConfig.setHeyue_treaty_url(conf.getsConf());
                }
            }

            PersistentVar.getInstance().setSystemConfig(systemConfig);
        }
    }

    private void downloadSplashPic(String picUrl, String filePath, String fileName) {
        picUrl = HostUtil.replaceUrl(picUrl);
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String pathName = filePath + fileName;
            File file = new File(pathName);
            if (file.exists()) {
                file.delete();
            }

            InputStream is = null;
            try {
                // 从网络上获取图片
                URL url = new URL(picUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            } finally {
                if (is != null) {
                    is.close();
                    is = null;
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }
}
