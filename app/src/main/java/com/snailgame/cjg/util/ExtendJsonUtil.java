package com.snailgame.cjg.util;

import com.alibaba.fastjson.JSON;
import com.snailgame.fastdev.network.FDRequest;
import com.snailgame.fastdev.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtendJsonUtil implements FDRequest.IExtendJson {

    /**
     * 解析json
     *
     * @param originalJsonStr 从服务端获取的原始json字符串
     * @return 解析完成后的标准格式的json字符串
     * @throws JSONException
     */
    private static String parseJson(String originalJsonStr) throws JSONException {
        JSONObject plainJsonObject = new JSONObject();
        JSONArray plainJsonArray = new JSONArray();

        JSONObject originalJsonObject = new JSONObject(originalJsonStr);

        /*
            ======================================================
                                  解析json正文
            ======================================================
         */
        JSONObject listObject = originalJsonObject.optJSONObject("list");
        JSONObject itemObject = originalJsonObject.optJSONObject("item");
        JSONArray dataArray = originalJsonObject.optJSONArray("data");
        JSONArray headerArray = originalJsonObject.optJSONArray("header");
        int resultCode = originalJsonObject.optInt("code", -1);
        String resultMsg = originalJsonObject.optString("msg");
        JSONObject pageObject = originalJsonObject.optJSONObject("page");
        // 新增val字段支持
        String resultVal = originalJsonObject.optString("val");
        // 新增albumHeader字段支持
        JSONObject albumHeaderObject = originalJsonObject.optJSONObject("albumHeader");

        // 1. (1)list不为空时处理
        //    (2)list为空时，data和header不为空时处理
        if (listObject != null || (dataArray != null && headerArray != null)) {
            if (listObject != null) {
                // 解析header信息，为空不处理
                headerArray = listObject.optJSONArray("header");
                if (headerArray == null) {
                    LogUtils.e("Json header is null, jsonStr=" + originalJsonStr);
                    return null;
                }
                // 解析data信息，为空不处理
                dataArray = listObject.getJSONArray("data");
                if (dataArray == null) {
                    LogUtils.e("Json data is null, jsonStr=" + originalJsonStr);
                    return null;
                }
                pageObject = listObject.optJSONObject("page");
                albumHeaderObject = listObject.optJSONObject("albumHeader");
            }

            // 循环处理子数字
            matchHeaderToData(dataArray, headerArray, plainJsonArray);
            plainJsonObject.put("list", plainJsonArray);
        }
        // 2. 如果list和data， header均不存在，item不为空时处理
        else if (itemObject != null) {
            plainJsonObject.put("item", itemObject);
        }
        // 3. 处理错误信息
        else if (resultCode > 0) {
            // do nothing
            LogUtils.e("An error occurred , code=" + resultCode + ", msg=" + resultMsg);
        }
        // 3. json格式有问题，无法解析
        else {
            LogUtils.e("Json format is invalid, jsonStr=" + originalJsonStr);
            return null;
        }

         /*
            ======================================================
                  解析json分页page、返回码code以及返回码描述msg
            ======================================================
         */
        plainJsonObject.put("page", pageObject);
        if (albumHeaderObject != null)
            plainJsonObject.put("albumHeader", albumHeaderObject);
        plainJsonObject.put("code", resultCode);
        plainJsonObject.put("msg", resultMsg);

        if (resultVal != null)
            plainJsonObject.put("val", resultVal);

        return plainJsonObject.toString();
    }


    /**
     * 解析json
     *
     * @param originalJsonStr 从服务端获取的原始json字符串
     * @return 解析完成后的标准格式的json字符串
     * @throws JSONException
     */
    public static String parseHomeJson(String originalJsonStr) throws JSONException {
        JSONObject plainJsonObject = new JSONObject();
        JSONArray plainJsonArray = new JSONArray();

        JSONObject originalJsonObject = new JSONObject(originalJsonStr);

        /*
            ======================================================
                                  解析json正文
            ======================================================
         */
        JSONObject listObject = originalJsonObject.optJSONObject("list");
        JSONArray dataArray;
        JSONArray headlineHeaderArray;
        JSONArray contentHeaderArray;
        int resultCode = originalJsonObject.optInt("code", -1);
        String resultMsg = originalJsonObject.optString("msg");


        // 1. (1)list不为空时处理
        //    (2)list为空时，data和header不为空时处理
        if (listObject != null) {
            // 解析header信息，为空不处理
            dataArray = listObject.optJSONArray("data");
            headlineHeaderArray = listObject.optJSONArray("headlineHeader");
            contentHeaderArray = listObject.optJSONArray("contentHeader");
            if (dataArray == null || headlineHeaderArray == null || contentHeaderArray == null) {
                LogUtils.e("Json header is null, jsonStr=" + originalJsonStr);
                return null;
            }
            // 循环处理子数字
            matchHomeHeaderToData(dataArray, headlineHeaderArray, contentHeaderArray, plainJsonArray);
            plainJsonObject.put("list", plainJsonArray);
        }

        // 3. 处理错误信息
        else if (resultCode > 0) {
            // do nothing
            LogUtils.e("An error occurred , code=" + resultCode + ", msg=" + resultMsg);
        }
        // 3. json格式有问题，无法解析
        else {
            LogUtils.e("Json format is invalid, jsonStr=" + originalJsonStr);
            return null;
        }

        plainJsonObject.put("code", resultCode);
        plainJsonObject.put("msg", resultMsg);

        return plainJsonObject.toString();
    }


    /**
     * 拼接header和data
     *
     * @param dataArray   data数组
     * @param headerArray header数组
     * @param resultArray 结果数组
     * @throws JSONException
     */
    private static void matchHeaderToData(JSONArray dataArray, JSONArray headerArray, JSONArray resultArray)
            throws JSONException {
        JSONObject itemJsonObject;
        JSONArray itemArray;
        for (int i = 0; i < dataArray.length(); i++) {
            itemArray = dataArray.getJSONArray(i);
            itemJsonObject = new JSONObject();
            for (int j = 0; j < itemArray.length(); j++) {
                itemJsonObject.put(headerArray.optString(j), itemArray.opt(j));
            }
            resultArray.put(itemJsonObject);
        }
    }

    /**
     * 拼接header和data
     *
     * @param dataArray   data数组
     * @param headlineHeaderArray header数组
     * @param contentHeaderArray header数组
     * @param resultArray 结果数组
     * @throws JSONException
     */
    private static void matchHomeHeaderToData(JSONArray dataArray, JSONArray headlineHeaderArray,
                                              JSONArray contentHeaderArray, JSONArray resultArray)
            throws JSONException {
        JSONObject itemJsonObject;
        JSONArray itemArray;
        for (int i = 0; i < dataArray.length(); i++) {
            itemArray = dataArray.getJSONArray(i);
            itemJsonObject = new JSONObject();
            int headLineLength = itemArray.length();
            for (int j = 0; j < headLineLength; j++) {
                itemJsonObject.put(headlineHeaderArray.optString(j), itemArray.opt(j));
                if (j == headLineLength - 1 && itemArray.optJSONArray(j) != null) {
                    matchContentHeader(itemArray.optJSONArray(j), headlineHeaderArray.optString(j),
                            contentHeaderArray, itemJsonObject);
                }
            }
            resultArray.put(itemJsonObject);
        }
    }

    private static void matchContentHeader(JSONArray dataArray, String headlineHeaderArray, JSONArray contentHeaderArray, JSONObject resultObject) throws JSONException {
        JSONObject itemJsonObject;
        JSONArray itemArray;
        int length = dataArray.length();
        JSONArray resultArray = new JSONArray();
        for (int i = 0; i < length; i++) {
            itemArray = dataArray.getJSONArray(i);
            itemJsonObject = new JSONObject();
            int headLineLength = itemArray.length();
            for (int j = 0; j < headLineLength; j++) {
                itemJsonObject.put(contentHeaderArray.optString(j), itemArray.opt(j));
            }
            resultArray.put(itemJsonObject);
            resultObject.put(headlineHeaderArray, resultArray);
        }

    }

    /**
     * 将从服务器获取的原始json字符串解析成对应的对象模型
     *
     * @param originalJsonStr 从服务器获取的原始json字符串
     * @param clazz           对应的对象模型Class
     * @param <T>             泛型
     * @return 对应的对象模型实力
     */
    public static <T> T parseJsonToModel(String originalJsonStr, Class<T> clazz) {
        String plainJsonStr = null;
        try {
            plainJsonStr = parseJson(originalJsonStr);
            if (originalJsonStr == null) {
                return null;
            }
            return JSON.parseObject(plainJsonStr, clazz);
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
        return null;
    }


    /**
     * 将从服务器获取的原始json字符串解析成对应的对象模型
     *
     * @param originalJsonStr 从服务器获取的原始json字符串
     * @param clazz           对应的对象模型Class
     * @param <T>             泛型
     * @return 对应的对象模型实力
     */
    public static <T> T parseHomeJsonToModel(String originalJsonStr, Class<T> clazz) {
        String plainJsonStr = null;
        try {
            plainJsonStr = parseHomeJson(originalJsonStr);
            if (originalJsonStr == null) {
                return null;
            }
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
        return JSON.parseObject(plainJsonStr, clazz);
    }


    @Override
    public String parse(String originalJsonStr) {
        try {
            return parseJson(originalJsonStr);
        } catch (Exception e) {
            return  null;
        }
    }
}
