package com.snailgame.cjg.spree;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.JsonUrl;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 已安装游戏 礼包
 * Created by TAJ_C on 2015/4/30.
 */
public class LocalAppSpreeFragment extends BaseSpreeFragment implements MyGameDaoHelper.MyGameCallback {

    private AsyncTask queryTask;

    public static LocalAppSpreeFragment getInstance(int[] route) {
        LocalAppSpreeFragment localAppSpreeFragment = new LocalAppSpreeFragment();
        Bundle bundle = new Bundle();
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        localAppSpreeFragment.setArguments(bundle);
        return localAppSpreeFragment;
    }

    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        if (event != null) {
            loadData(event.getAppInfoList());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(getActivity(), this);
    }


    @Override
    protected void initView() {
        mRoute[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_INSTALL_SPREE;
        super.initView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getEmptyView().setEmptyMessage(getString(R.string.spree_local_app_none));
    }

    @Override
    protected void setJsonUrl() {
    }

    @Override
    public void Callback(List<AppInfo> appInfos) {
        loadData(appInfos);
    }

    private void loadData(List<AppInfo> appInfos) {
        StringBuffer appIds = new StringBuffer();
        for (AppInfo appInfo : appInfos) {
            appIds.append(appInfo.getAppId()).append(",");
        }
        if (appIds.length() > 0) {
            appIds.deleteCharAt(appIds.length() - 1);
        }
        mUrl = JsonUrl.getJsonUrl().JSON_URL_LOCAL_APP_SPREE_LIST + "?iAppIds=" + appIds + "&";
        loadData();
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

    @Override
    public void onDestroyView() {
        if (queryTask != null)
            queryTask.cancel(true);
        super.onDestroyView();
    }
}
