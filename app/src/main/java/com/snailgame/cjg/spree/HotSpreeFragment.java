package com.snailgame.cjg.spree;


import android.os.Bundle;

import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.JsonUrl;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * 热门游戏礼包 fragment
 * Created by TAJ_C on 2015/4/30.
 */
public class HotSpreeFragment extends BaseSpreeFragment {

    public static HotSpreeFragment getInstance(int[] route) {
        HotSpreeFragment hotSpreeFragment = new HotSpreeFragment();
        Bundle bundle = new Bundle();
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        hotSpreeFragment.setArguments(bundle);
        return hotSpreeFragment;
    }


    @Override
    protected void setJsonUrl() {
        mUrl = JsonUrl.getJsonUrl().JSON_URL_HOT_SPREE_LIST + "?" ;
    }

    @Override
    protected void initView() {
        mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_HOT_SPREE;
        super.initView();
    }

    @Subscribe
    public void getGiftSuccess(SpreeGetSuccessEvent event) {
        if (mAdapter != null) {
            mAdapter.refreshData(event.getSpreeGiftInfo());
        }
    }


    @Subscribe
    public void onUserLogin(UserLoginEvent event) {
        loadData();
    }

    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        loadData();
    }


    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        onDownloadInfoChange(taskInfos);
    }
}
