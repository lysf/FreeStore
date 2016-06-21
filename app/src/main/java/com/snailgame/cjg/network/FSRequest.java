package com.snailgame.cjg.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.fastdev.network.FDRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * add http header
 * Created by xixh on 2016/3/4.
 */
public class FSRequest extends FDRequest {
    public FSRequest(int method, String url, String tag, Class clazz, Response.Listener listener, Response.ErrorListener errorListener, boolean extendJson, IExtendJson iExtendJson) {
        super(method, url, tag, clazz, listener, errorListener, extendJson, iExtendJson);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> map = new HashMap<String, String>();
        Map<String, String> headers = super.getHeaders();
        if (headers != null)
            map.putAll(headers);


        /*****************FreeStore http header start********************/
        map.put(AppConstants.USER_AGENT, GlobalVar.getInstance().getUserAgent(FreeStoreApp.getContext()));
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            map.put(AppConstants.STORE_ACCESS_KEY_USER_ID, IdentityHelper.getUid(FreeStoreApp.getContext()));
            map.put(AppConstants.STORE_ACCESS_KEY_IDENTITY, IdentityHelper.getIdentity(FreeStoreApp.getContext()));
        }
        map.put(AppConstants.STORE_ACCESS_KEY_APP_ID, String.valueOf(IdentityHelper.getAppId()));
        map.put(AppConstants.STORE_ACCESS_KEY_TYPE, String.valueOf(AppConstants.OS_TYPE));
        map.put(AppConstants.STORE_ACCESS_KEY_PLATFORM_ID, String.valueOf(AppConstants.PLATFORM_ID));
        map.put(AppConstants.STORE_ACCESS_KEY_CLIENT_VERSION, String.valueOf(ComUtil.getSelfVersionCode()));
        map.put(AppConstants.STORE_ACCESS_KEY_CLIENT_CHANNEL, ChannelUtil.getChannelID());
        /*****************FreeStore http header end********************/

        return map;
    }

}
