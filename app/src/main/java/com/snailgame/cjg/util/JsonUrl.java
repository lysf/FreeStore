package com.snailgame.cjg.util;

import com.snailgame.cjg.global.AppConstants;

public class JsonUrl {

    private static JsonUrl jsonUrl;
    public static String DEFAULT_DOMAIN = "app1.snail.com";
    public static final String DEFAULT_DOMAIN_VALUE = "app1.snail.com";
    public String FREESTORE_BASE = "/cms/freestore/" + AppConstants.PLATFORM_KEY;

    public synchronized static JsonUrl getJsonUrl() {
        if (jsonUrl == null)
            jsonUrl = new JsonUrl();
        return jsonUrl;
    }


    public static void resetJsonUrl() {
        jsonUrl = new JsonUrl();
    }

    public String ROOT_URL = "http://api." + DEFAULT_DOMAIN;
    public String KUWAN_URL = "http://kuwan.snail.com";


    //首页
    public String JSON_URL_HOME = ROOT_URL + "/store/platform/center/headpage";

    //百宝箱接口
    public String JSON_URL_GET_TREASUREBOX_URL = ROOT_URL + FREESTORE_BASE + "/cms/treasureBox/treasureBox_v2.json";

    //基础配置
    public String JSON_URL_SNAIL_SYSTEM_CONFIG = ROOT_URL + FREESTORE_BASE + "/cms/terminalMng/client/systemConfig.json";
    //装机必备
    public String JSON_URL_SNAIL_NECESSARY_APP = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/45/45_1.json";

    //推荐
    //游戏
    public String JSON_URL_GAME_RECOMMEND = ROOT_URL + FREESTORE_BASE + "/cms/bannerRecord/game/bannerRecord_";
    //应用
    public String JSON_URL_APP_RECOMMEND = ROOT_URL + FREESTORE_BASE + "/cms/bannerRecord/app/bannerRecord_";


    //合集
    //游戏
    public String JSON_URL_GAME_THEME = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/gameColList/gameList_";
    //软件
    public String JSON_URL_APP_THEME = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/appColList/appList_";
    //合集明细
    public String JSON_URL_THEME_DETAIL = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/colDetail/colDetail_";


    //酷玩商城
    public String JSON_URL_STORE = ROOT_URL + "/cms/freestore/platform/kuwan/homepage/homepage.json";
    //通讯页签
    public String JSON_URL_COMMUNICATION = ROOT_URL + "/cms/freestore/android/cms/tongxinHomepage/homepage.json";
    //商品列表
    public String JSON_URL_STORE_GOODS_LIST = KUWAN_URL + "/mobile/index.php?act=goods&op=goods_list";
    //虚拟代充热门游戏接口
    public String JSON_URL_STORE_HOT_VIRTUAL_LIST = KUWAN_URL + "/mobile/index.php?act=goods_class&op=virtual_list";
    //虚拟代充全部游戏接口
    public String JSON_URL_STORE_ALL_VIRTUAL_LIST = KUWAN_URL + "/mobile/index.php?act=goods_class&op=all_virtual_list";
    //热门积分商品列表接口
    public String JSON_URL_STORE_HOT_POINT_LIST = KUWAN_URL + "/mobile/index.php?act=goods_class&op=hot_integral_list";
    public String JSON_URL_STORE_HOT_EXCHANGE_LIST = KUWAN_URL + "/mobile/index.php?act=integral_exchange&op=hot_list";
    //全部积分商品接口
    public String JSON_URL_STORE_ALL_POINT_LIST = KUWAN_URL + "/mobile/index.php?act=goods_class&op=all_integral_list";
    public String JSON_URL_STORE_ALL_EXCHANGE_LIST = KUWAN_URL + "/mobile/index.php?act=integral_exchange&op=list";
    //分类
    //游戏
    public String JSON_URL_GAME_CATEGORY = ROOT_URL + FREESTORE_BASE + "/mobileApps/category/gameCatList/gameList.json";
    //排行
    public String JSON_URL_GAME_RANK = ROOT_URL + "/cms/freestore/platform/rankList/";
    public String JSON_URL_GAME_RANK_ORDER = ROOT_URL + "/store/platform/extend/user/itunes/subscribe";
    //软件
    public String JSON_URL_APP_CATEGORY = ROOT_URL + FREESTORE_BASE + "/mobileApps/category/appCatList/appList.json";
    //标签明细
    public String JSON_URL_CATEGORY_TAG_DETAIL = ROOT_URL + FREESTORE_BASE + "/mobileApps/category/catDetail/catDetail_";
    //分类明细
    public String JSON_URL_CATEGORY_CAT_DETAIL = ROOT_URL + "/store/platform/app/category/list";

    //应用详情
    public String JSON_URL_APP_DETAIL = ROOT_URL + "/appSpace";
    //应用推荐列表 http://mm.1.com/content.html?id=99
    public String JSON_RECOMMEND_GAME = ROOT_URL + "/store/platform/app/recommend/list";
    //用户应用礼包列表
    public String JSON_URL_APP_SPREE = ROOT_URL + "/store/platform/article/user/app/spree/list/V2";

    //信息反馈
    public String FEEDBACK_URL = ROOT_URL + "/store/platform/center/user/feedback";

    //统计
    //APP访问开始记录
    public String JSON_URL_OPEN_APP = ROOT_URL + "/store/platform/app/log/enter";
    //App访问结束记录
    public String JSON_URL_CLOSE_APP = ROOT_URL + "/store/platform/app/log/out";


    /* ————————————————————用户接口文档——————————————————— */
    /*————1.用户物品————*/
    //查询上架礼包列表
    public String JSON_URL_QUERY_SPREE_LIST = ROOT_URL + "/store/platform/article/user/spree";

    //获取热门应用礼包列表
    public String JSON_URL_HOT_SPREE_LIST = ROOT_URL + "/store/platform/article/user/hot/spree/list/V2";

    //猜你喜欢礼包列表
    public String JSON_URL_ALL_SPREE_LIST = ROOT_URL + "/store/platform/article/user/favorite/spree/list/V2";

    //获取已安装应用礼包列表
    public String JSON_URL_LOCAL_APP_SPREE_LIST = ROOT_URL + "/store/platform/article/user/apps/spree/list";
    //预约礼包
    public String JSON_URL_ORDER_SPREE = ROOT_URL + "/store/platform/article/user/spree/subscribe";
    //取消预约礼包
    public String JSON_URL_CANCLEORDER_SPREE = ROOT_URL + "/store/platform/article/user/spree/unsubscribe";
    //查询礼包预约状态
    public String JSON_URL_QUERY_STATE = ROOT_URL + "/store/platform/article/user/spree/subscribe";

    //查询用户已领取礼包
    public String JSON_URL_QUERY_USR_SPREE = ROOT_URL + "/store/platform/article/user/spree/list";
    //领取礼包
    public String JSON_URL_GET_SPREE = ROOT_URL + "/store/platform/article/user/spree";
    //兑换礼包
    public String JSON_URL_EXCHANGE_SPREE = ROOT_URL + "/store/platform/article/user/spree/exchange";
    //淘号
    public String JSON_URL_TAO_SPREE = ROOT_URL + "/store/platform/article/user/spree/tao";

    //我的代金券
    public String JSON_URL_QUERY_USR_VOUCHER = ROOT_URL + "/store/platform/extend/user/voucher";
    //代金券使用记录
    public String JSON_URL_QUERY_VOUCHER_RECORD = ROOT_URL + "/store/platform/extend/user/voucher/record";
    //代金券支持游戏
    public String JSON_URL_VOUCHER_COOPER = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/";
    //商品代金券
    public String JSON_URL_KU_WAN_VOUCHER = KUWAN_URL + "/mobile/index.php?act=voucher&op=userVoucherList";
    //商品代金券合作游戏
    public String JSON_URL_KU_WAN_VOUCHER_COOPER = KUWAN_URL + "/api/index.php?act=voucher&op=getVoucherGoods";
    //蜗牛代金券
    public String JSON_URL_QUERY_USR_WN_VOUCHER = ROOT_URL + "/store/platform/extend/user/wn/voucher";
    //蜗牛代金券使用记录
    public String JSON_URL_QUERY_VOUCHER_WN_RECORD = ROOT_URL + "/store/platform/extend/user/wn/voucher/record";
    //获取所有特权
    public String JSON_URL_USR_PRIVILEGES = ROOT_URL + "/store/platform/extend/user/privilege/all";
    //修改用户特权通知状态
    public String JSON_URL_NOTIFY_PRIVILEGE_CHANGE = ROOT_URL + "/store/platform/extend/user/privilege/notify";

    public String JSON_URL_VOUCHER_NUM = ROOT_URL + "/store/platform/center/user/voucher/num";
    /*————2用户积分————*/

    // 个人中心 用户积分记录
    public String JSON_URL_USER_SCORE_GROUP = ROOT_URL + "/store/platform/integral/user/list/group";
    public String JSON_URL_USER_SCORE_HISTORY = ROOT_URL + "/store/platform/integral/user/list";

    public String JSON_URL_USER_CURRENCY_HISTORY = ROOT_URL + "/store/platform/center/user/currency/record";

    //用户任务列表
    public String USER_TASK_URL = ROOT_URL + "/store/platform/center/user/task/v2";
    public String USER_TASK_RECEIVE = ROOT_URL + "/store/platform/center/user/task/receive";

    /*————3 用户中心————*/
    //修改用户昵称
    public String JSON_URL_PERSONAL_NICKNAME = ROOT_URL + "/store/platform/center/user/nickname";
    //查询用户个人信息
    public String JSON_URL_USER_INFO = ROOT_URL + "/store/platform/center/user/account";
    //发送短信验证码
    public String JSON_URL_SEND_BIND_SMS = ROOT_URL + "/store/platform/center/phone/sms";
    //绑定或者解绑手机
    public String JSON_URL_BIND_OR_UNBIND_PHONE = ROOT_URL + "/store/platform/center/user/phone";
    //分享
    public String JSON_URL_PERSONAL_SHARE = ROOT_URL + "/store/platform/center/user/share";
    //图片上传
    public String JSON_URL_PHOTO_UPLOAD = ROOT_URL + "/store/platform/center/user/photo";

    //修改用户生日
    public String JSON_URL_USER_BIRTHDAY = ROOT_URL + "/store/platform/center/user/birthday";
    //个人中心统计
    public String JSON_URL_USER_STATE = ROOT_URL + "/store/platform/center/user/stat";
    //猜你喜欢
    public String JSON_GUESS_YOU_FAVOURITE = ROOT_URL + "/store/platform/app/user/favorite";
    //每日抽奖
    public String JSON_URL_SCRATCH_INFO = ROOT_URL + "/store/platform/activity/user/prize/status/V2";

    //更新
    //应用差分更新
    public String JSON_URL_APP_UPDATE_LIST_V2 = ROOT_URL + "/store/platform/app/patch/list";
    //版本更新（免商店）
    public String JSON_URL_UPDATE_URL = ROOT_URL + "/store/platform/center/check/version";


    //会员中心首页
    public String JSON_URL_MEMBER_PAGER = ROOT_URL + FREESTORE_BASE + "/cms/memberHomepage/homepage.json";

    //酷玩专购
    public String JSON_URL_MEMBER_KUWAN_BUY = ROOT_URL + "/cms/freestore/platform/cms/memberHomepage/homepage_tpl8.json";

    //查询会员等级特权信息
    public String JSON_URL_MEMBER_USER_PRIVILEGE = ROOT_URL + "/store/platform/member/user/privilege";
    //查询会员等级特权信息
    public String JSON_URL_MEMBER_USER_PRIVILEGE_TYPE = ROOT_URL + "/store/platform/member/user/privilege/type";
    //领取物品
    public String JSON_URL_MEMBER_USER_DRAW = ROOT_URL + "/store/platform/member/user/draw";
    /*————消息————*/
    //保存用户个推消息信息
    public String JOSN_URL_BIND_CID = ROOT_URL + "/store/platform/msg/user/getui";


    /*————搜索————*/
    //搜索建议列表接口
    public String JSON_URL_SEARCH_KEY_LIST = ROOT_URL + "/store/platform/app/keyword/list";
    //热词排行榜
    public String JSON_URL_SEARCH_HOTKEY = ROOT_URL + FREESTORE_BASE + "/cms/rankList/hotWord/hotWord.json";
    //搜索
    public String JSON_URL_SEARCH = ROOT_URL + "/store/platform/app/search";
    //分类搜索
    public String JSON_URL_SEARCH_V2 = ROOT_URL + "/store/platform/app/search/classify";

    /*————免流量特权————*/
    //免流量应用列表查询（广州联通）
    public String JSON_URL_FREE_GAME = ROOT_URL + "/store/platform/mvne/user/apps";
    //获取免流量应用下载地址（广州联通）
    public String JSON_URL_GET_FREE = ROOT_URL + "/store/platform/mvne/user/app/url";
    //免流量专区（41为免流量专区类型）
    public String JSON_URL_FREE_AREA = ROOT_URL + FREESTORE_BASE + "/mobileApps/collection/41/41";

    /*————评论————*/
    //应用评论列表   http://mm.1.com/content.html?id=176
    public String JSON_URL_COMMENT_LIST = ROOT_URL + "/store/platform/app/comments";
    //查询/提交个人评论
    public String JSON_URL_USR_COMMENT = ROOT_URL + "/store/platform/app/user/comment";

    /*————免流量统计————*/
    //流量统计地址
    public String STATIC_TRAFFIC_URL = ROOT_URL + "/store/platform/freeflow/user/flowdata";

    /*————活动————*/
    // 获取活动信息
    public String JSON_URL_SHARE_ACTIVITY = ROOT_URL + "/store/platform/activity/center/share/";


    //通知服务端游戏相关操作
    public String JSON_URL_STATICS_APP = ROOT_URL + "/store/platform/app/user/download";

    //资讯攻略
    public String APP_NEWS_URL = ROOT_URL + FREESTORE_BASE + "/appNews/gameGuide/newsList_";
    public String JSON_URL_INSTEDA_CHARGE = ROOT_URL + FREESTORE_BASE + "/cms/depositApps/depositApps.json";

    /*————网页————*/
    //会员专区
    public String JSON_URL_USER_ACTIVITY = ROOT_URL + "/cms/web/free/index.html";
    //积分墙 刮奖
    public String JSON_URL_SCORE_LUCKY = ROOT_URL + "/cms/web/center/integral.html";
    //福利码
    public String JSON_URL_CODE_EXCHANGE = ROOT_URL + "/cms/web/act/_still/fuli/index.html";
    //合约机协议
    public String JSON_URL_HEYUE_TREATY = ROOT_URL + "/cms/web/heyue/treaty.html";
    /* ------------------------------------------------------------任性的分割线-----------------------------------------------------------------*/
    /* 统计信息 */
    public String STATICS_URL = "http://sqm.woniu.com/actionimp.json";

    public String ONLINE_SHOP_DEFAULT_URL = "http://10040.snail.com/mobile/unbuy.html";
    public String JSON_URL_BBS = "http://mall.snail.com/site/ssousername/bbs";


    //论坛
    public String JSON_URL_FORM = "http://bbs.snail.com/source/plugin/mobile/mobile.php?module=forumdisplay";

    public String JSON_URL_FORUM_ITEM = "http://bbs.snail.com/forum.php?mod=viewthread&app=1";
    public String JSON_URL_FORUM_POST = "http://bbs.snail.com/forum.php?mod=post&action=newthread";

    //我的酷玩订单
    public String JSON_URL_ORDER = KUWAN_URL + "/wap/tmpl/member/order_list.html";
    //购物车
    public String JSON_URL_SHOPPING_CAR = KUWAN_URL + "/wap/tmpl/cart_list.html";
    //我的酷玩收货地址
    public String JSON_URL_ADDRESS = KUWAN_URL + "/wap/tmpl/member/address_list.html";

    //免卡余额查询
    public String JSON_URL_CHECK_BALANCE = "http://10040.snail.com/mobile/personal.html";

    // 充话费 地址
    public String JSON_URL_RECHARGE_CURRENCY = "http://10040.snail.com/mcharge/qcharge.html";

    // 充游戏 地址
    public String JSON_URL_RECHARGE_GAME = ROOT_URL + "/cms/freestore/android/cms/depositApps/depositApps.html";

    // 充流量 地址
    public String JSON_URL_RECHARGE_FLOW = "http://10040.snail.com/mcharge/fcharge.html";


    //免流量特权开通地址
    public String JSON_URL_FLOW_FREE = ROOT_URL + "/cms/web/free/owner.html";

    //免流量特权开通地址
    public String JSON_URL_MEMBER_INTRODUCE = ROOT_URL + "/cms/web/center/explain3.html";

    //用户等级信息V2
    public String JSON_URL_USER_LEVER_V2 = ROOT_URL + "/cms/web/center/explain2.html";


    //应用授权，从SDK跳入免商店
    public String JSON_SDK_AUTHO = ROOT_URL + "/store/platform/sdk/app/autho";

    //渠道应用
    public String JSON_URL_CHANNEL = ROOT_URL + "/store/platform/member/channel/app/v1";
    //畅享宝号段信息校验
    public String JSON_URL_CXB_PHONE_CHECK = "http://cxb1.snail.com:8082/cdb/segInfoCheck";

    //会员信息查询
    public String JSON_URL_MEMBER_INFO = ROOT_URL + "/store/platform/member/user/stat";

    //预约游戏  预约游戏列表接口 http://mm.1.com/content.html?id=853  http://mm.1.com/content.html?id=858
    public String JSON_URL_APP_APPOINTMENT = ROOT_URL + "/store/platform/app/user/appointment";
    //预约游戏状态
    public String JSON_URL_APPOINTMENT_STATE = ROOT_URL + "/store/platform/app/user/appointment/stat";

    //** --------資訊---------**/
    //查询频道列表 http://mm.1.com/content.html?id=903
    public String JSON_URL_NEWS_CHANNEL_LIST = ROOT_URL + "/store/platform/channel/list";

    //查询文章列表 http://mm.1.com/content.html?id=904
    public String JSON_URL_NEWS_CHANNEL_NEWS_CONTENT = ROOT_URL + "/store/platform/article/list";

    //保存用户不感兴趣列表 http://mm.1.com/content.html?id=909
    public String JSON_URL_NEWS_NO_INTEREST = ROOT_URL + "/store/platform/tags/noInterest";

    //用户点击行为保存 http://mm.1.com/content.html?id=910
    public String JSON_URL_NEWS_BEHAVIOUR_CLICK = ROOT_URL + "/store/platform/behaviour";

    //** --------朋友---------**/
    //	查询朋友列表  http://mm.1.com/content.html?id=918
    public String JSON_URL_FRIEND_MYFRIENDS = ROOT_URL + "/store/platform/friends/myFriends/list";

    //朋友推荐列表  http://mm.1.com/content.html?id=919
    public String JSON_URL_FRIEND_RECOMMEND_FRIENDS = ROOT_URL + "/store/platform/friends/recommend/list";

    //朋友关系处理  http://mm.1.com/content.html?id=922
    public String JSON_URL_FRIEND_FRIEND_HANDLE = ROOT_URL + "/store/platform/friends/handle";

    //用户游戏列表 http://mm.1.com/content.html?id=920
    public String JSON_URL_FRIEND_GAME_LIST = ROOT_URL + "/store/platform/friends/game/list";

    //朋友搜索 http://mm.1.com/content.html?id=921
    public String JSON_URL_FRIEND_SEARCH_LIST = ROOT_URL + "/store/platform/friends/search/list";

    //免卡用户套餐  http://mm.1.com/content.html?id=954
    public String JSON_URL_HOME_MOBILE = ROOT_URL + "/store/platform/center/user/package/info";
}
