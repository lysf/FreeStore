package com.snailgame.cjg.util;

import com.snailgame.cjg.common.model.FreeGameModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.interfaces.FreeGameRefrsh;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

/**
 * Created by sunxy on 14-7-8.
 */
public class QueryFreeGameUtils {

    /**
     * 查询 游戏免费状态
     * @param gameIds
     * @param freeGameRefrsh
     * @param TAG
     * @return
     */
    public static String queryFreeGame(String gameIds, final FreeGameRefrsh freeGameRefrsh, String TAG) {
        String url = JsonUrl.getJsonUrl().JSON_URL_FREE_GAME + "?iAppId=" + gameIds;

        FSRequestHelper.newGetRequest(url, TAG, FreeGameModel.class, new IFDResponse<FreeGameModel>() {
            @Override
            public void onSuccess(FreeGameModel result) {
                if (result == null || ListUtils.isEmpty(result.getInfos()))
                    freeGameRefrsh.refresh(null);
                else {
                    freeGameRefrsh.refresh(result.getInfos());
                }
            }

            @Override
            public void onNetWorkError() {
                freeGameRefrsh.refresh(null);
            }

            @Override
            public void onServerError() {
                freeGameRefrsh.refresh(null);
            }
        }, false, true, new ExtendJsonUtil());

        return TAG;
    }

}
