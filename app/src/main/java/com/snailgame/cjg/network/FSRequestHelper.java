package com.snailgame.cjg.network;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.snailgame.fastdev.FastDevApplication;
import com.snailgame.fastdev.network.FDRequest;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.network.RequestHelper;

import java.util.Map;

/**
 * 网络请求辅助类
 * <p/>
 * Created by xixh on 2015/6/17.
 */
public class FSRequestHelper extends RequestHelper {
    /**
     * 新建网络Get请求
     *
     * @param url         请求地址
     * @param tag         Tag，用于取消请求
     * @param clazz       响应数据结构类
     * @param iFSResp     回调
     * @param <T>         响应数据结构类
     * @param shouldCache 是否保存缓存
     * @param extendJson  是否为标准Json格式
     * @param iExtendJson 非标准Json转化为标准Json接口
     */
    public static <T> void newGetRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, boolean shouldCache, boolean extendJson, FDRequest.IExtendJson iExtendJson) {
        FSRequest request = new FSRequest(Request.Method.GET, url, tag, clazz, new Response.Listener<T>() {

            @Override
            public void onResponse(T response) {
                iFSResp.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError ||
                        error instanceof ParseError)
                    iFSResp.onServerError();
                else
                    iFSResp.onNetWorkError();
            }
        }, extendJson, iExtendJson);

        request.setShouldCache(shouldCache);
        FastDevApplication.getRequestQueue().add(request);
    }

    /**
     * 新建网络Get请求
     *
     * @param url         请求地址
     * @param tag         Tag，用于取消请求
     * @param clazz       响应数据结构类
     * @param iFSResp     回调
     * @param <T>         响应数据结构类
     * @param shouldCache 是否保存缓存
     */
    public static <T> void newGetRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, boolean shouldCache) {
        newGetRequest(url, tag, clazz, iFSResp, shouldCache, false, null);
    }

    /**
     * 新建网络Post请求
     *
     * @param url         请求地址
     * @param tag         Tag，用于取消请求
     * @param clazz       响应数据结构类
     * @param iFSResp     回调
     * @param <T>         响应数据结构类
     * @param params      请求参数
     * @param extendJson  是否为标准Json格式
     * @param iExtendJson 非标准Json转化为标准Json接口
     */
    public static <T> void newPostRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, final Map<String, String> params, boolean extendJson, FDRequest.IExtendJson iExtendJson) {
        FSRequest request = new FSRequest(Request.Method.POST, url, tag, clazz, new Response.Listener<T>() {

            @Override
            public void onResponse(T response) {
                iFSResp.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError ||
                        error instanceof ParseError)
                    iFSResp.onServerError();
                else
                    iFSResp.onNetWorkError();
            }
        }, extendJson, iExtendJson) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

        };

        request.setShouldCache(false);
        FastDevApplication.getRequestQueue().add(request);
    }


    /**
     * 新建网络Post请求
     *
     * @param url     请求地址
     * @param tag     Tag，用于取消请求
     * @param clazz   响应数据结构类
     * @param iFSResp 回调
     * @param <T>     响应数据结构类
     * @param params  请求参数
     */
    public static <T> void newPostRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, final Map<String, String> params) {
        newPostRequest(url, tag, clazz, iFSResp, params, false, null);
    }


    /**
     * 新建网络Post请求
     *
     * @param url         请求地址
     * @param tag         Tag，用于取消请求
     * @param clazz       响应数据结构类
     * @param iFSResp     回调
     * @param <T>         响应数据结构类
     * @param params      请求参数
     * @param extendJson  是否为标准Json格式
     * @param iExtendJson 非标准Json转化为标准Json接口
     */
    public static <T> void newPostRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, final String params, boolean extendJson, FDRequest.IExtendJson iExtendJson) {
        Map<String, String> parameters = parseParams(params);
        newPostRequest(url, tag, clazz, iFSResp, parameters, extendJson, iExtendJson);
    }

    /**
     * 新建网络Post请求
     *
     * @param url     请求地址
     * @param tag     Tag，用于取消请求
     * @param clazz   响应数据结构类
     * @param iFSResp 回调
     * @param <T>     响应数据结构类
     * @param params  请求参数
     */
    public static <T> void newPostRequest(String url, String tag, final Class<T> clazz, final IFDResponse<T> iFSResp, final String params) {
        Map<String, String> parameters = parseParams(params);
        newPostRequest(url, tag, clazz, iFSResp, parameters);
    }
}

