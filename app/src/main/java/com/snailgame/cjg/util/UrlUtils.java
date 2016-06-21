package com.snailgame.cjg.util;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by yftx on 6/24/14.
 * <p/>
 * 组拼url工具类
 */
public class UrlUtils {
    public static String getPagedUrl(String baseUrl, int currPage, int PAGE_NUM) {
        return baseUrl + AccountUtil.getLoginParams() + "&currentPage=" + currPage + "&number=" + PAGE_NUM;
    }


    /**
     * 根据具体资源的ID,算出最后的路径
     *
     * @param infoId   -对应资源ID
     * @param rootPath -访问资源的地址前缀 appSpace/
     * @param layer    4
     *                 -几层目录数
     * @param step     1000
     *                 -每层文件数量
     * @return 返回算出来的对应该资源的地址
     */
    public static String getAppDetailJsonPath(long infoId, String rootPath, int layer, int step) {
        StringBuilder rootStr = new StringBuilder(rootPath);
        long gene = 1;
        for (int i = 1; i < layer; i++) {
            gene *= step;
        }
        long tempId = infoId;
        for (int i = 0; i < layer; i++) {
            if (layer != i + 1) {
                int temp = (int) (tempId / gene); // 取整数部分
                tempId = tempId % gene; // 取余数部分
                rootStr.append("/").append(temp);
                gene /= step;
            } else {
                rootStr.append("/").append(infoId);
            }
        }
        return rootStr.toString();
    }


    /**
     * 为url添加请求参数
     *
     * @param url       原始url
     * @param paramsMap 参数map
     * @return 完整的url
     */
    public static String buildUrl(String url, Map<String, Object> paramsMap) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Url is empty");
        }

        if (paramsMap == null || paramsMap.isEmpty()) {
            return url;
        }

        String reqParamStr = "";// 参数字段串
        int count = 0;
        for (String paramName : paramsMap.keySet()) {
            reqParamStr += paramName + "=" + paramsMap.get(paramName);
            count++;
            if (count < paramsMap.size())
                reqParamStr += "&";
        }
        if (!reqParamStr.equals("")) {
            reqParamStr = "?" + reqParamStr.replace("{2,}", "");
        }
        url += reqParamStr;
        return url;
    }


    // 服务器<div> 解析出来时两个"\n",顾过滤
    public static String clearBlankLine(CharSequence charSequence) {
        return charSequence.toString().replace("\n\n", "\n");
    }
}
