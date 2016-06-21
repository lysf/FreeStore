package com.snailgame.cjg.global;

/**
 * Created by xixh on 2016/2/22.
 */

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.snailgame.cjg.detail.model.InsteadCharge;
import com.snailgame.cjg.home.model.UserMobileModel;
import com.snailgame.cjg.manage.model.TreasureBoxInfo;
import com.snailgame.cjg.member.model.MemberInfoModel;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GlobalVar {
    private static GlobalVar mGlobalVar;
    private String StaticsVarString;        // 统计登录用户在线时长：用户key值
    private UserInfo usrInfo;            //当前用户信息数据
    private UserPrivilegesModel usrPrivileges;  //用户 特权数据
    private MemberInfoModel memberInfo;    //用户会员数据
    private UserMobileModel.ModelItem userMobile;// 合约机用户信息
    private List<TreasureBoxInfo> mTreasureBoxInfoList;  /*管理界面百宝箱list*/
    private List<String> listMainValue;//progress 接收时间改变广播进程类名称
    private List<String> listAppWidgetValue;//progress 接收系统重要广播进程类名称
    private List<String> importPreferenceList = new ArrayList<>();//progress 全局重要进程名称类

    private String shareAppId = "36"; //详情页分享的应用ID

    //详情页分享的应用名称
    private String shareGameName = "";
    //区分是邀请还是分享内容
    private boolean isShareMenu;
    //分享的url地址
    private String shareActivityUrl = "http://m.app.snail.com/";

    private HashMap<Integer, InsteadCharge> insteadChargeArrayMap;

    private String COMMON_USER_AGENT = "";
    private String COMMON_USER_AGENT_LOC = "0";   // 经纬度信息

    public synchronized static GlobalVar getInstance() {
        if (mGlobalVar == null) {
            mGlobalVar = new GlobalVar();
        }

        return mGlobalVar;
    }


    public void generateUserAgent(Context context) {
        SharedPreferencesHelper sharedHelper = SharedPreferencesHelper.getInstance();
        String phoneNum = sharedHelper.getValue(AppConstants.PHONE_NUMBER, "-");

        phoneNum = TextUtils.isEmpty(phoneNum) ? "-" : phoneNum;
        String imei = TextUtils.isEmpty(PhoneUtil.getIMEICode(context)) ? "-" : PhoneUtil.getIMEICode(context);
        String curPhoneNum = TextUtils.isEmpty(PhoneUtil.getPhoneNumber(context)) ? "-" : PhoneUtil.getPhoneNumber(context);
        String uid = TextUtils.isEmpty(IdentityHelper.getUid(context)) ? "-" : IdentityHelper.getUid(context);

        StringBuilder info = new StringBuilder();
        info.append("FreeStoreClient:")
                .append("Android ").append(Build.VERSION.RELEASE).append("|")//android 版本
                .append(Build.BRAND).append(" ").append(Build.MODEL).append("|")//手机型号
                .append(imei).append("|")//imei
                .append(curPhoneNum).append("|")//用户当前手机号
                .append(uid).append("|")//用户uid
                .append(phoneNum); //用户绑定手机号
        COMMON_USER_AGENT = info.toString();

    }

    /**
     * 获取请求需要带的useragent串
     *
     * @return
     */
    public String getUserAgent(Context context) {
        //FreeStoreClient:系统版本|手机型号|imei|用户当前手机号|用户uid|用户绑定手机号|网络类型|imsi|地区
        //获取不到的值都用"-"符号代替
        String networkType = NetworkUtils.getActiveNetworkType(context) == -1 ? "-" : String.valueOf(NetworkUtils.getActiveNetworkType(context));
        String imsi = PhoneUtil.getImsi(context);
        return COMMON_USER_AGENT + "|" + networkType + "|" + (TextUtils.isEmpty(imsi) ? "-" : imsi) + "|" +  GlobalVar.getInstance().getCOMMON_USER_AGENT_LOC();
    }

}
