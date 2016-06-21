package com.snailgame.cjg.search;

import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.search.model.SearchInfo;
import com.snailgame.cjg.search.model.SearchShakeModel;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pancl on 2014/11/19.
 */
public class ShakeHelper implements IGuessFavourite {
    static final String TAG = ShakeHelper.class.getName();
    private List<SearchInfo> shakeList;

    public ShakeHelper() {
    }

    protected void getShakeList() {
        String url;
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            url = JsonUrl.getJsonUrl().JSON_GUESS_YOU_FAVOURITE + AccountUtil.getLoginParams();
        } else {
            url = JsonUrl.getJsonUrl().JSON_GUESS_YOU_FAVOURITE;
        }
        createGetShakeListAsyncTask(url);
    }


    /**
     * 获取 摇出好玩意 数据
     *
     * @param url
     */
    private void createGetShakeListAsyncTask(String url) {
        FSRequestHelper.newGetRequest(url, TAG,
                SearchShakeModel.class, new IFDResponse<SearchShakeModel>() {
                    @Override
                    public void onSuccess(SearchShakeModel model) {
                        if (model != null)
                            shakeList = model.getSearchShakeList();
                    }

                    @Override
                    public void onNetWorkError() {
                    }

                    @Override
                    public void onServerError() {
                    }
                }, false, true, new ExtendJsonUtil());

    }


    private List<SearchInfo> getShakeSearchInfos() {
        List<SearchInfo> searchInfos = new ArrayList<SearchInfo>();
        if (shakeList == null || shakeList.size() == 0) {
            return searchInfos;
        }
        int indexs[] = getShakeRandomValue(shakeList.size());
        for (int index : indexs) {
            searchInfos.add(shakeList.get(index));
        }
        return searchInfos;
    }

    private int[] getShakeRandomValue(int size) {
        Random random = new Random();

        int randIndex, temp;
        int[] seed = new int[size];
        for (int i = 0; i < seed.length; i++) {
            seed[i] = i;
        }
        int len = seed.length;
        int reLen = len >= 8 ? 8 : len;
        int[] returnValue = new int[reLen];
        for (int i = 0; i < returnValue.length; i++) {
            randIndex = Math.abs(random.nextInt()) % len;
            returnValue[i] = seed[randIndex];
            temp = seed[randIndex];
            seed[randIndex] = seed[len - 1];
            seed[len - 1] = temp;
            len--;
        }
        return returnValue;
    }

    @Override
    public List<SearchInfo> getFavouriteList() {
        return getShakeSearchInfos();
    }
}
