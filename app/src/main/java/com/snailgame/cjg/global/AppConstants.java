package com.snailgame.cjg.global;


import android.os.Environment;

public class AppConstants {
    public static final String APP_VERSION_FLAG = "V";
    public static final String APP_DETAIL_APPID = "id";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_FREEAREA = "freearea";
    public static final String KEY_VOUCHER = "key_voucher";
    public static final String KEY_VOUCHER_TYPE = "key_voucher_type";
    public static final String KEY_VOUCHER_ID = "key_voucher_id";
    public static final String KEY_VOUCHER_GAMELIST = "key_voucher_gamelist";
    public static final String KEY_PRIVILEGES = "key_privileges";
    public static final String APP_DETAIL_TAB = "app_detail_tab";
    public static final String KEY_VOUCHER_TAB = "key_voucher_tab";
    public static final String KEY_MYVOUCHER_TYPE = "key_myvoucher_type";
    public static final String KEY_VOUCHER_ONCEUSR = "key_voucher_onceuse";
    public static final String APP_DETAIL_AUTO_DOWNLOAD = "app_detail_auto_download";

    public static final String KEY_FRIEND_USER_ID = "key_friend_user_id";
    public static final String KEY_FRIEND_USER = "key_friend_user";
    public static final String COMPANY_NAME = "company_name";
    public static final String APP_MODEL = "app_model";

    public static final String APP_DETAIL_URL = "app_detail_url";
    public static final String APP_DETAIL_MD5 = "app_detail_md5";

    /*applistfragment*/
    public static final String APP_LIST_URL = "key_url";

    public static final String SHARE_URL = "share_url";
    public static final String WXTIMELINE = "share_timeline";
    public static final String GAME_ID = "key_gameid";
    public static final String GAME_NAME = "app_name";
    public static final String SHARE_PIC = "share_pic";
    public static final String SHARE_TYPE = "share_type";


    public static final String SHARE_CONTENT = "share_content";
    public static final String EGGS_CONTENT = "eggs_content";

    //Store
    public static final String GOODS_LIST_TITLE = "goods_list_title";
    public static final String GOODS_LIST_ID = "goods_list_id";
    //Member
    public static final String MEMBER_TYPE = "member_type";
    public static final String MEMBER_TITLE = "member_title";
    public static final String MEMBER_PRILIEGE_ID = "member_priliege_id";
    //SharedPrefrenceHelper
    public final static String DEFAULT_SHARED_NAME = "freeStoreXmlData";
    public static final String KEY_FIRST_REBOOT = "frist_boot_153";
    public static final String KEY_LAST_PACKAGE = "key_package";
    public static final String FLOW_IS_FREE = "flow_is_free";
    public static final String FLOW_IS_FREE_SNAIL = "snail_flow_free";
    public static final String FLOW_FREE_START = "flow_free_start";
    public static final String FLOW_FREE_END = "flow_free_end";
    public static final String FLOW_FREE_SIZE = "flow_free_size";
    public static final String USER_VOUCHER_NUM = "user_voucher_num";
    public static final String IS_SNAIL_PHONE_NUMBER = "is_snail_phone_number";
    public static final String APP_SORT_TYPE = "sortType";
    public static final String APP_SORT_SHOW_ALBUM = "show_album";
    public static final String APP_PACKAGE_NAME = "com.snailgame.cjg";
    public static final String SHOW_SHOW_GUIDE = "show_show_guide_155";

    /*setting*/
    public static final String SETTING_WIFI_ONLY = "wifi_only";
    public static final String SETTING_AUTO_DELETE_APP = "auto_delete_app";
    public static final String SETTING_OPEN_PUSH = "open_push";
    public static final String SETTING_AUTO_INSTALL = "auto_install";
    public static final String SETTING_UPDATE = "update";

    /*AppListFragment*/
    public static final String KEY_APP_MODEL = "key_model";
    public static final int VALUE_RANK = 0;
    public static final int VALUE_CATEGORY = 1;      //分类
    public static final int VALUE_CATEGORY_TAG = 5;   //分类-标签
    public static final int VALUE_RECOMMEND = 3;    //推荐
    public static final int VALUE_HOME_RECOMMENT = 10;
    public static final int VALUE_COLLECTION = 4;   //合集
    public static final int VALUE_FREEAREA = 6;        //免流量专区
    public static final int VALUE_VOUCHER = 7;        //代金券
    public static final int VALUE_SEARCH_APP_LIST = 8;
    public static final int VALUE_FRIEND_GAMES = 9; //朋友游戏
    /*MygameDBObserver*/
    public static final int CODE_RESULT_ATIVITY_JUMP = 5;
    /*check apk isupdate*/
    public static final int UPDATE_INTERVAL = 12 * 60 * 60 * 1000;
    public static final int UPDATE_INTERVAL_NOTIFICATION = 24 * 60 * 60 * 1000;
    public static final long UPDATE_INTERVAL_MYSHELF = 24 * 60 * 60 * 1000L;
    public static final long RELOGIN_BBS_TIME = 25 * 60 * 1000;
    public static final String UPDATE_TYPE = "update_type";
    public static final String KEY_UPDATE_MODE = "key_update_mode";
    public static final String KEY_SERVICE_TYPE = "service_type";
    public static final int UPDATE_REQUEST_START = 0;
    public static final int UPDATE_REQUESTDATA = 1;
    public static final int UPDATE_CHECK_NOTIFICATION = 3;
    public static final int ONE_KEY_UPDATE = 4;//通知栏点击一键更新时给AutoCheckUpgradableGameService发送更新指令
    public static final int CANCLE_NOTIFY = 5;//通知栏中通知用户更新app的通知被手动取消
    public static final String IS_NOTIFICATION_TAG = "is_notification_tag";
    public static final String FIRST_SHOW_TAB_INDEX = "first_show_tab_index";
    /**
     * 标记是否已经获取root权限 true 以获取root权限 false 未获取用户权限
     */
    public static final String KEY_IS_SUPERUSER = "is_superuser";
    public static final String ACTION_USER_TYPE = "com.snailgame.cjg.action.user.type";
    public static final String ACTION_UPDATE_USR_INFO = "com.snailgame.cjg.action.update.usr.info";
    public static final String ACTION_GET_VOUCHER_NUM = "com.snailgame.cjg.action.get.voucher.num";
    public static final String ACTION_USER_LOGIN = "com.snailgame.cjg.action.user.login";
    public static final String ACTION_USER_LOGOUT = "com.snailgame.cjg.action.user.logout";
    public static final String ACTION_USER_COVER = "com.snailgame.cjg.action.user.cover";
    public static final String UPDATE_MYSHELF = "update_myshelf";
    public static final String UPDATE_MYSHELF_DESC = "update_myshelf_desc";
    public static final String UPDATE_MYSHELF_FORCE = "update_myshelf_force";

    public static final String KEY_IS_ROOT_AUTO_INSTALL = "is_root_auto_install";
    /*
    *   更新配置
     */
    //收藏
    public static final String SHARE_SOURCE = "source";
    public static final String SHARE_MENU = "menu_share";
    public static final String STORE_ACCESS_KEY_USER_ID = "nUserId";
    public static final String STORE_ACCESS_KEY_APP_ID = "nAppId";
    public static final String STORE_ACCESS_KEY_CLIENT_VERSION = "clientVersion";
    public static final String STORE_ACCESS_KEY_CLIENT_CHANNEL = "clientChannel";
    public static final String STORE_ACCESS_KEY_IDENTITY = "cIdentity";
    public static final String STORE_ACCESS_KEY_TYPE = "cType";
    public static final String STORE_ACCESS_KEY_PLATFORM_ID = "iPlatformId";
    public static final String PHONE_NUMBER = "phone_num";
    public static final String IS_CXB_PHONE_NUMBER = "is_cxb_phone_number";
    public static final String AREA = "area";
    public static final String REWARD_SCORE = "reward_score";
    public static final String LAST_LOGIN_ID = "last_l_i";
    public static final String KEY_REPLACE_DOMAIN = "replace_domain";
    public static final String POPUP_TIMES = "popup_tims";
    public static final String POPUP_RECOMMENT_ID = "popup_r_i";
    public static final String BIND_PHONE_NUMBER = "bind_phone";
    public static final String AGENT_TYPE = "3";//代理类型
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String BIND_CID = "bind_cid";
    public static final String NICK_NAME = "account_name";
    public static final String IS_CONTRACT_MACHINE = "is_contract_machine";
    public static final int DESK_GAME = 1;
    public static final int ALL_INSTALLED_GAME = 0;
    public static final String NOTIFICATION_IDS = "notif_ids";
    public static final String WIFI = "wifi";
    public static final String KEY_FIRST_IN = "key_first_in";
    public static final String PROVINCE_ID = "province_id";
    public static final String LAST_GET_LOCATION_TIME = "last_get_location_time";
    public static final String SEARCH_HISTORY_LIST = "search_history_list";
    public static final int SOURCE_SCORE = 10;
    public static final int SOURCE_APP = 11;
    public static final int SOURCE_APP_MODEL = 23; //软件
    public static final int SOURCE_TEST_NEW_APP = 24; //测新游
    public static final int SOUCE_POPUP = 20;
    public static final int SOURCE_NEWS = 26;
    public static final int SOURCE_FRIEND = 27;
    public static final String CHANNEL_ID = "channel_ID";
    public static final int EDIT_NICKNAME_RESULT_CODE = 6;
    public static final int EMPTY_VIEW_MARGIN_TOP = 20;
    public static final String RANK_ORDER_ID = "rank_order_id";
    public static final String RANK_FEED_POSITION = "rank_feed_position";
    public static final String RANK_COUNTRY_POSITION = "rank_country_position";
    public static final String RANK_FEED_NAME = "rank_feed_name";
    public static final String RANK_COUNTRY_NAME = "rank_country_name";
    public static final String KEY_NEED_CREATE_DESK_GAME_SHORTCUT = "need_create_desk_game_shortcut";
    public static final int SOURCE_CHARGE = 12;
    public static final String VIDEO_URL = "video_url";
    public static final String KEY_RANDOM_HOT_SEARCH = "key_random_hot_search";
    public static final int FRAGMENT_HOME = 22;
    public static final int FRAGMENT_APP_APPOINTMENT = 23;
    public static final String KEY_LAST_FREE_STATE = "key_last_free_state";
    public static String CLIENT_ID = "";
    public static String account_type = "-1";//代理类型

    public static boolean login = false;
    public static final String KEY_TASK_TAB = "key_task_tab";

    public static final String PARAMS_PAGING_NUMBER = "number";
    public static final String PARAMS_PAGING_CURR_PAGE = "currentPage";

    /* App Search */
    public static final String PARAMS_SEARCH_KEY_WORD = "sKeyWord";
    public static final String PARAMS_SEARCH_SEARCH_TYPE = "cSearchType";
    public static final String PARAMS_CHANNEL_ID = "iChannelId";
    public static final String PARAMS_VERSION_CODE = "cVersionCode";
    public static final String PARAMS_PLATFORM_ID = "iPlatformId";

    public static final String KEY_SEARCH_RESULT_TAB = "key_search_result_type";


    /*
        用户免流量状态发生变化时，广播标示
     */
    public static final String ACTION_FREE_ACCOUNT_STATUS_CHANGE = "com.snailgame.cjg.action.free.account.status.change";


    /*
     *  公用web view
     */
    public static final int WEBVIEW_MODEL_COMMON = 1;
    public static final int WEBVIEW_MODEL_COOL_PLAY = 2;
    public static final int WEBVIEW_MODEL_ONLINE_SHOP = 3;
    public static final int WEBVIEW_MODEL_BBS = 4;

    public static final String WEBVIEW_URL = "webview_url";
    public static final String WEBVIEW_IS_SHOW = "webview_is_show";
    public static final String WEBVIEW_NOSHOW_TEXT = "webview_no_show_text";
    public static final String WEBVIEW_MODEL = "webview_model";
    public static final String WEBVIEW_SHARE_ICON_URL = "webview_share_icon";
    public static final String WEBVIEW_ACTIVITY_ENABLE_FINISH = "webview_activity_enable_finish";

    public static final int POP_WINDOW_ANIMATION_DURATION = 300;

    public static final String USER_AGENT = "User-Agent";

    public static final int SOURCE_SPREE = 2;
    public static final int SOURCE_RECOMMENT_GAME = 3;
    public static final int SOURCE_COLLECTION = 4;
    public static final int SOURCE_ACTIVITY = 5;
    public static final int SOURCE_STRATEGY = 6;
    public static final int SOURCE_MESSAGE = 7;
    public static final int SOURCE_BBS = 8;
    public static final int SOURCE_GAME = 9;
    public static final String PROVICE = "baidu_provice";
    public static final String LATITUDE = "baidu_latitude";
    public static final String LONTITUDE = "baidu_lontitude";


    //账户信息 网络获取状态
    public static final int USR_STATUS_IDLE = 0;
    public static final int USR_STATUS_RUNNING = 1;
    public static final int USR_STATUS_SUCCESS = 2;
    public static final int USR_STATUS_FAILED = 3;
    public static final int USR_STATUS_NETWORK_ERROR = 4;
    public static final int USR_STATUS_EXPIRE_ERROR = 5;


    public static final String KEY_APP_ID = "key_app_id";

    //免商店 自更新 信息
    public static final String KEY_SILENCE_UPDATE = "key_silence_update";


    public static final int WEB_ACTIONBAR_ACTION_CLOSE = 0;
    public static final int WEB_ACTIONBAR_ACTION_BACK = 1;
    public static final int WEB_ACTIONBAR_ACTION_CUSTOM = 2;
    //分类
    public static final int CATEGORY_CAT = 1;
    //标签
    public static final int CATEGORY_TAG = 2;

    // 免流量专区
    public static final int FREE_AREA_IN = 1;
    public static final int FREE_AREA_OUT = 0;

    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String ACTIVITY_TITLE = "activity_title";


    public static final int INVALID_UPDATE_IGNORE = 0;

    public static final int UPDATE_TYPE_NOPATCH = 0;
    public static final int UPDATE_TYPE_PATCH = 1;

    public static final String VALUE_TYPE_GAME = "1";
    public static final String VALUE_TYPE_APP = "2";
    public static final String VALUE_TYPE_SKIN = "3";

    //特权弹框内标图图片占位符
    public static final String PRIVILEGE_PLACEHOLDER_STRING = "%@%";

    // 个推自定义事件
    public static final int GETUI_ACTION_PUSH_RECEIVER = 90001; // 收到个推消息
    public static final int GETUI_ACTION_NOTIFY_CLICK = 90002; // 用户点击通知栏消息
    public static final int GETUI_ACTION_LIST_CLICK = 90003; // 用户点击消息列表


    //当前使用的皮肤配置信息
    public static final String SKIN_PACKAGES = "skin_packages";
    public static final String KEY_SKIN_INFO = "key_skin_info";
    public static final int SKIN_PACKAGE_SUPPORT_VERSION = 1;


    //网页分享是否刷新
    public static final String WEBVIEW_SHARE_FLUSH = "webFlush=true";

    //每日抽奖，是否免费抽奖的状态
    public static final String SCRATCH_STATUS = "scratch_status";//1-----免费抽奖  2-----消耗积分抽奖

    public static final String SDK_GAME_ID = "sdk_game_id";
    public static final String APP_SOURCE_SELF = "0";  // 0,自研
    public static final String APP_SOURCE_JOINT = "1"; // 1,联运


    // webview使用广播
    public static final String WEBVIEW_BROADCAST_ACTION_ONLINESHOP = "com.snailgame.cjg.webview.onlineshop";
    public static final String WEBVIEW_BROADCAST_ACTION_COMMON = "com.snailgame.cjg.webview.common";
    public static final String WEBVIEW_BROADCAST_ACTION_USERDEAL = "com.snailgame.cjg.webview.userdeal";

    // 管理页面清理内存
    public static final String LAST_SCORE = "last_score";
    public static final String LAST_MEMORY = "last_memory";
    //首页最后一次刷新的时间
    public static final String LAST_REFRESH_TIME = "last_refresh_time";

    public static final int ERR_CODE_NOT_BIND_PHONE = 6241; // 领取礼包用户未绑定手机

    public static final String IMAGE_INDEX = "imageIndex";
    public static final String IMAGE_URLS = "imageUrls";

    public static final String USER_GUIDE_DOWNLOAD_TASKS_DELETE = "userGuideDownloadTasksDelete";
    public static final int USER_GUIDE_COUNT_DOWNLOAD_TASKS_DELETE = 5;

    public static final int SEARCH_TEXT_MAX_LENGTH = 20;

    public static final int APP_ID = 36;
    public static final int OS_TYPE = 1;
    // 平台ID
    public static final int PLATFORM_ID = 36;
    // 平台Key
    public static final String PLATFORM_KEY = "android";

    public static final String DO_NOT_ALERT_ANY_MORE_1 = "do_not_alert_any_more1";
    public static final String USE_FLOW_DOWNLOAD = "use_flow_download";

    /**
     * 提示授予root权限 true 提示 false 不提示
     */
    public static final String ALERT_GRANT_ROOT = "alert_grant_root";
    public static final String GETTUI_APP_ID = "kUd6dr9A4s6QiJ9rTz7Xd1";

    // PV，Download统计常量
    public static final int STATISTCS_DEFAULT_NULL = -1;
    // 第一层
    // Splash
    public static final int STATISTCS_FIRST_SPLASH = 1;
    // 首页
    public static final int STATISTCS_FIRST_HOMEPAGE = 2;
    // 游戏
    public static final int STATISTCS_FIRST_GAMEPAGE = 3;
    // 软件
    public static final int STATISTCS_FIRST_APPPAGE = 4;
    // 搜索
    public static final int STATISTCS_FIRST_SEARCH = 5;
    // 推送
    public static final int STATISTCS_FIRST_PUSH = 6;
    // 应用管理
    public static final int STATISTCS_FIRST_MANAGE = 7;
    // 应用详情
    public static final int STATISTCS_FIRST_DETAIL = 8;
    // 个人中心
    public static final int STATISTCS_FIRST_PERSONAL = 9;
    // 外部打开
    public static final int STATISTCS_FIRST_OUTWEB = 10;
    // 网页（活动）
    public static final int STATISTCS_FIRST_WEB = 11;
    // 免流量专区
    public static final int STATISTCS_FIRST_FREEAREA = 12;
    // 我的任务
    public static final int STATISTCS_FIRST_MYTASK = 13;
    // 侧边栏广告
    public static final int STATISTCS_FIRST_SLIDEAD = 14;
    // 渠道安装
    public static final int STATISTCS_FIRST_CHANNEL = 15;

    // 第二层
    // 分类
    public static final int STATISTCS_SECOND_SORT = 21;
    // 合集
    public static final int STATISTCS_SECOND_THEME = 22;
    // 推荐
    public static final int STATISTCS_SECOND_RECOMMEND = 23;
    // 排行
    public static final int STATISTCS_SECOND_RANK = 24;

    // 第三层
    // Banner
    public static final int STATISTCS_THIRD_BANNERAD = 41;
    // 快捷入口
    public static final int STATISTCS_THIRD_SHORTCUT = 42;
    // 小广告位
    public static final int STATISTCS_THIRD_HALFAD = 43;
    // 弹出广告
    public static final int STATISTCS_THIRD_DIALOGAD = 44;
    // 列表
    public static final int STATISTCS_THIRD_LIST = 45;
    // 相关推荐
    public static final int STATISTCS_THIRD_RELATED = 46;
    // 热词
    public static final int STATISTCS_THIRD_HOTKEY = 47;
    // 输入
    public static final int STATISTCS_THIRD_ENTER = 48;
    // 摇一摇
    public static final int STATISTCS_THIRD_SHAKE = 49;
    // 历史
    public static final int STATISTCS_THIRD_HISTORY = 50;

    // 第五层
    // 分类
    public static final int STATISTCS_FIFTH_SORT = 61;
    // 合集
    public static final int STATISTCS_FIFTH_THEME = 62;
    // 免流量专区
    public static final int STATISTCS_FIFTH_FREEAREA = 63;
    //排行榜
    public static final int STATISTCS_FIFTH_RANK = 64;
    //资讯
    public static final int STATISTCS_FIFTH_NEWS = 65;
    //礼包（热门礼包）
    public static final int STATISTCS_FIFTH_HOT_SPREE = 66;
    //礼包（已安装礼包）
    public static final int STATISTCS_FIFTH_INSTALL_SPREE = 67;
    //代金券
    public static final int STATISTCS_FIFTH_VOUCHER = 68;

    // 第八层
    public static final int STATISTCS_EIGHTH_LIST = 0;
    public static final int STATISTCS_EIGHTH_DETAIL = 1;

    // 数据结构
    // 底部tab页|顶部标签页|显示类型|位置|类型|类型id|位置|页面类型|应用id
    public static final int STATISTCS_DEPTH_NUM = 9;
    // 第一层 底部tab页
    public static final int STATISTCS_DEPTH_ONE = 0;
    // 第二层 顶部标签页
    public static final int STATISTCS_DEPTH_TWO = 1;
    // 第三层 显示类型
    public static final int STATISTCS_DEPTH_THREE = 2;
    // 第四层 位置
    public static final int STATISTCS_DEPTH_FOUR = 3;
    // 第五层 类型
    public static final int STATISTCS_DEPTH_FIVE = 4;
    // 第六层 类型id
    public static final int STATISTCS_DEPTH_SIX = 5;
    // 第七层 位置
    public static final int STATISTCS_DEPTH_SEVEN = 6;
    // 第八层 页面类型
    public static final int STATISTCS_DEPTH_EIGHT = 7;
    // 第九层 应用id
    public static final int STATISTCS_DEPTH_NINE = 8;


    public static final String PERSISTENTDIR = Environment.getExternalStorageDirectory() + "/" + "FreeStore" + "/" + "Persistent";

}
