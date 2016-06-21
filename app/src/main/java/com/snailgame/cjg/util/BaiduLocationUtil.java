package com.snailgame.cjg.util;

import android.content.Context;
import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaiduLocationModel;
import com.snailgame.cjg.event.LoactionChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lic on 2016/3/16.
 * 百度ip定位工具类
 */
public class BaiduLocationUtil {
    private final static String TAG = BaiduLocationUtil.class.getSimpleName();
    private final String baidulocationSign = "zNqwrhfFDeHfNcuLrZGaiy5R";//百度ip定位api所需要的key
    private final String coorString = "bd09ll";//当url中coor不出现时，默认为百度墨卡托坐标；coor=bd09ll时，返回为百度经纬度坐标
    Context context;
    private String provinceArray[];
    private Map<String, Integer> PROVINCES = new HashMap<>();
    private String provinceId = "-1";
    private SharedPreferencesHelper helper;

    public BaiduLocationUtil(Context context) {
        provinceArray = ResUtil.getStringArray(R.array.province);
        helper = SharedPreferencesHelper.getInstance();
        initProvinceMap();
    }

    /**
     * 获取百度ip定位
     */
    public void getBaiduLocation() {
        FSRequestHelper.newGetRequest(getBaiduLocationUrl(), TAG, BaiduLocationModel.class, new IFDResponse<BaiduLocationModel>() {
            @Override
            public void onSuccess(BaiduLocationModel result) {
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
                if (result != null && result.getContent() != null
                        && result.getContent().getPoint() != null
                        && result.getContent().getAddressDetail() != null) {
                    helper.putValue(AppConstants.LAST_GET_LOCATION_TIME, System.currentTimeMillis());
                    helper.putValue(AppConstants.LATITUDE, String.valueOf(result.getContent().getPoint().getX()));
                    helper.putValue(AppConstants.LONTITUDE, String.valueOf(result.getContent().getPoint().getY()));

                    String province = result.getContent().getAddressDetail().getProvince();
                    helper.putValue(AppConstants.PROVICE, province);

                    if (!TextUtils.isEmpty(province)) {
                        queryProvinceId(province);
                    }

                    helper.commitValue();
                    LogUtils.d("province [" + province + "] provinceId [" + provinceId + "]");
                    //首页过滤广播
                    MainThreadBus.getInstance().post(new LoactionChangeEvent());
                } else {
                    helper.putValue(AppConstants.LAST_GET_LOCATION_TIME, 0L);
                    LogUtils.d("get location onError");
                }
            }

            @Override
            public void onNetWorkError() {
                helper.putValue(AppConstants.LAST_GET_LOCATION_TIME, 0L);
                helper.commitValue();
                LogUtils.d("get location onNetWorkError");
            }

            @Override
            public void onServerError() {
                LogUtils.d("get location onServerError");
                helper.putValue(AppConstants.LAST_GET_LOCATION_TIME, 0L);
                helper.commitValue();
            }
        }, false);
    }

    /**
     * 拼接百度ip定位所需url
     *
     * @return
     */
    private String getBaiduLocationUrl() {
        String locationRootUrl = "http://api.map.baidu.com/location/ip?";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(locationRootUrl).append("ak=").append(baidulocationSign)
                .append("&coor=").append(coorString);
        return stringBuilder.toString();
    }

    /**
     * 根据省份名称获取省份编号
     *
     * @param province
     */
    private void queryProvinceId(String province) {
        String key;
        for (Map.Entry<String, Integer> entry : PROVINCES.entrySet()) {
            key = entry.getKey();
            if (key.contains(province) || province.contains(key)) {
                provinceId = String.valueOf(entry.getValue());
                GlobalVar.getInstance().setCOMMON_USER_AGENT_LOC(provinceId);
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
                helper.putValue(AppConstants.PROVINCE_ID, provinceId);
                helper.commitValue();
            }
        }

    }

    /**
     * 初始化省份编码
     */
    private void initProvinceMap() {
        PROVINCES.put(provinceArray[0], 110000);
        PROVINCES.put(provinceArray[1], 310000);
        PROVINCES.put(provinceArray[2], 120000);
        PROVINCES.put(provinceArray[3], 500000);
        PROVINCES.put(provinceArray[4], 230000);
        PROVINCES.put(provinceArray[5], 220000);
        PROVINCES.put(provinceArray[6], 210000);
        PROVINCES.put(provinceArray[7], 150000);
        PROVINCES.put(provinceArray[8], 130000);
        PROVINCES.put(provinceArray[9], 140000);
        PROVINCES.put(provinceArray[10], 610000);
        PROVINCES.put(provinceArray[11], 370000);
        PROVINCES.put(provinceArray[12], 650000);
        PROVINCES.put(provinceArray[13], 540000);
        PROVINCES.put(provinceArray[14], 630000);
        PROVINCES.put(provinceArray[15], 620000);
        PROVINCES.put(provinceArray[16], 640000);
        PROVINCES.put(provinceArray[17], 410000);
        PROVINCES.put(provinceArray[18], 320000);
        PROVINCES.put(provinceArray[19], 420000);
        PROVINCES.put(provinceArray[20], 330000);
        PROVINCES.put(provinceArray[21], 340000);
        PROVINCES.put(provinceArray[22], 350000);
        PROVINCES.put(provinceArray[23], 360000);
        PROVINCES.put(provinceArray[24], 430000);
        PROVINCES.put(provinceArray[25], 520000);
        PROVINCES.put(provinceArray[26], 510000);
        PROVINCES.put(provinceArray[27], 440000);
        PROVINCES.put(provinceArray[28], 530000);
        PROVINCES.put(provinceArray[29], 450000);
        PROVINCES.put(provinceArray[30], 460000);
        PROVINCES.put(provinceArray[31], 810000);
        PROVINCES.put(provinceArray[32], 820000);
        PROVINCES.put(provinceArray[33], 710000);
    }

}
