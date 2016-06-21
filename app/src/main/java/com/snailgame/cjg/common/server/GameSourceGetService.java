package com.snailgame.cjg.common.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.detail.model.AppDetailResp;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.GameSdkDataUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.fastdev.network.IFDResponse;

/**
 * 游戏来源获取，若游戏是自营或者联运,则保存数据
 * Created by xixh on 2015/6/24.
 */
public class GameSourceGetService extends IntentService {
    static String TAG = GameSourceGetService.class.getName();
    private static final String KEY_APPID = "appId";

    public static Intent newIntent(Context context, long appId) {
        Intent intent = new Intent(context, GameSourceGetService.class);
        intent.putExtra(KEY_APPID, appId);
        return intent;
    }

    public GameSourceGetService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        long id = intent.getLongExtra(KEY_APPID, 0);
        getGameSource(id);
    }


    /**
     * 获取应用来源
     */
    private void getGameSource(final long id) {
        FSRequestHelper.newGetRequest(buildGameDetailUrl(id), TAG, AppDetailResp.class, new IFDResponse<AppDetailResp>() {
            @Override
            public void onSuccess(AppDetailResp result) {
                if (result != null && result.getItem() != null) {
                    if (result.getItem().getcSource().equals(AppConstants.APP_SOURCE_SELF) ||
                            result.getItem().getcSource().equals(AppConstants.APP_SOURCE_JOINT))
                        GameSdkDataUtil.addDataV1(result.getItem().getcPackage());
                    GameSdkDataUtil.removeGameId(id);
                } else {
                    GameSdkDataUtil.addGameId(id);
                }
            }

            @Override
            public void onNetWorkError() {
                GameSdkDataUtil.addGameId(id);
            }

            @Override
            public void onServerError() {
                GameSdkDataUtil.addGameId(id);
            }
        }, true);
    }

    private String buildGameDetailUrl(long id) {
        String url = UrlUtils.getAppDetailJsonPath(id, JsonUrl.getJsonUrl().JSON_URL_APP_DETAIL, 4, 1000) + "/detail.json";

        return url;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
