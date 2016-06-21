package com.snailgame.cjg.search;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.search.adapter.SearchGameAppAdapter;
import com.snailgame.cjg.search.model.SearchInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import third.commonsware.cwac.merge.MergeAdapter;

/**
 * 搜索 -> 无搜索结果时猜你喜欢推荐应用列表fragment
 * Created by pancl on 2015/6/4.
 */
public abstract class GuessFavouriteFragment extends AbsBaseFragment implements AdapterView.OnItemClickListener {
    static String TAG = GuessFavouriteFragment.class.getName();
    protected Activity mContext;

    protected final List<AppInfo> searchResultList = new ArrayList<>();
    protected SearchGameAppAdapter searchResultAdapter;
    protected LoadMoreListView searchResultListView;
    protected QueryTaskListTask queryTaskListTask;

    protected EmptyView mEmptyView;
    private View headerView;
    protected String searchText;
    protected int nextPage = 1;
    private long mStartTime = 0L;
    private static final int DISPLAY_NUMBER = 10;
    protected IGuessFavourite guessFavourite;
    protected MergeAdapter mergeAdapter = new MergeAdapter();

    /**
     * 组建访问搜索接口参数
     *
     * @param mKey
     * @param page
     * @param searchType
     * @param isSingle   是否单个分类搜索
     * @return
     */
    protected String buildUrl(String mKey, int page, int searchType, boolean isSingle) {
        try {
            String keyWord = URLEncoder.encode(mKey, "utf-8");
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put(AppConstants.PARAMS_SEARCH_KEY_WORD, keyWord);
            if (isSingle) {
                paramsMap.put(AppConstants.PARAMS_PAGING_NUMBER, DISPLAY_NUMBER);
                paramsMap.put(AppConstants.PARAMS_PAGING_CURR_PAGE, page);
                paramsMap.put(AppConstants.PARAMS_SEARCH_SEARCH_TYPE, searchType);
            }
//            paramsMap.put(AppConstants.PARAMS_SEARCH_DYNAMIC, "");
//            paramsMap.put(AppConstants.PARAMS_SEARCH_APP_TYPE, "");

            return UrlUtils.buildUrl(JsonUrl.getJsonUrl().JSON_URL_SEARCH_V2, paramsMap);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.getMessage());
            return "";
        }
    }

    protected String buildUrl(String mKey, int page, int searchType) {
        return buildUrl(mKey, page, searchType, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainThreadBus.getInstance().register(this);
        headerView = inflater.inflate(R.layout.activity_app_search_guess_header, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
    }

    @Override
    protected void saveData(Bundle outState) {
    }

    @Override
    protected void initView() {
        mContext = getActivity();

        searchResultListView = (LoadMoreListView) mContent.findViewById(R.id.content);
        searchResultListView.setPadding(0, ResUtil.getDimensionPixelSize(R.dimen.item_padding), 0, 0);
        searchResultListView.setClipToPadding(false);
//        searchResultListView.setDivider(getResources().getDrawable(R.drawable.transparent));
//        searchResultListView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.list_divider_height));
    }

    @Override
    protected LoadMoreListView getListView() {
        return searchResultListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchResultListView.getVisibility() == View.VISIBLE) {
            if (queryTaskListTask != null)
                queryTaskListTask.cancel(true);
            queryTaskListTask = new QueryTaskListTask();
            queryTaskListTask.execute();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (queryTaskListTask != null) queryTaskListTask.cancel(true);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * 点击某一搜索结果页内游戏
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= parent.getAdapter().getCount())
            return;
        AppInfo info = (AppInfo) parent.getAdapter().getItem(position);

        if (info == null)
            return;

        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
        startActivity(DetailActivity.newIntent(mContext, info.getAppId(), route));
    }

    protected void fillItemData(List<SearchInfo> searchList) {
        updateSearchInfos(searchList);

        synchronized (searchResultList) {
            AppInfoUtils.updatePatchInfo(mContext, searchResultList);
        }

        if (AccountUtil.isAgentFree(mContext)) {
            AppInfoUtils.updateAppFreeState(mContext, AppInfoUtils.getAllGameIds(searchResultList), searchResultAdapter, TAG);
        } else {
            searchResultAdapter.refreshData(searchResultList);
            searchResultAdapter.notifyDataSetChanged();
        }

    }

    protected void updateSearchInfos(List<SearchInfo> searchList) {
        int queryFilter = DownloadManager.STATUS_RUNNING
                | DownloadManager.STATUS_FAILED
                | DownloadManager.STATUS_PAUSED
                | DownloadManager.STATUS_PENDING
                | DownloadManager.STATUS_PENDING_FOR_WIFI
                | DownloadManager.STATUS_SUCCESSFUL
                | DownloadManager.STATUS_INSTALLING
                | DownloadManager.STATUS_PATCHING;

        List<TaskInfo> downloadTasks = DownloadHelper.getDownloadTasks(
                mContext,
                DownloadHelper.QUERY_TYPE_BY_STATUS,
                queryFilter);

        for (SearchInfo app : searchList) {
            //  现在都为空
           /* if (TextUtils.isEmpty(app.getcStatus()) || Integer.valueOf(app.getcStatus()) != 1) {
                continue;
            }*/
            AppInfo appInfo = new AppInfo();
            appInfo.setcFlowFree(app.getcFlowFree());
            appInfo.setAppId(app.getnAppId());
            appInfo.setAppName(app.getsAppName());
            appInfo.setVersionName(app.getcVersionName());
            appInfo.setIcon(app.getcIcon());
            appInfo.setApkUrl(app.getcDownloadUrl());
            appInfo.setApkSize(app.getiSize());
            appInfo.setPkgName(app.getcPackage());
            appInfo.setVersionCode(app.getiVersionCode());
            appInfo.setMd5(app.getcMd5());
            appInfo.setsAppDesc(app.getsAppDesc());

            boolean isDownLoad = false;
            for (TaskInfo taskInfo : downloadTasks) {
                if (app.getnAppId() == taskInfo.getAppId()) {
                    appInfo.setApkDownloadId(taskInfo.getTaskId());
                    appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                    appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                    appInfo.setDownloadTotalSize(taskInfo.getApkTotalSize());
                    appInfo.setDownloadState(taskInfo.getDownloadState());
                    appInfo.setLocalUri(taskInfo.getApkLocalUri());
                    appInfo.setDownloadPatch(taskInfo.getApkTotalSize() < appInfo.getApkSize());
                    appInfo.setInDownloadDB(true);
                    isDownLoad = true;
                    break;
                }
            }

            if (!isDownLoad) {
                appInfo.setDownloadPatch(false);
                appInfo.setInDownloadDB(false);
            }

            PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(mContext,
                    appInfo.getPkgName());
            if (packageInfo != null) {
                if (appInfo.getDownloadState() == DownloadManager.STATUS_SUCCESSFUL || appInfo.getDownloadState() == 0) {
                    if (packageInfo.versionCode == appInfo.getVersionCode()) {
                        appInfo.setDownloadState(DownloadManager.STATUS_EXTRA_INSTALLED);
                    }
                    if (appInfo.getDownloadState() != DownloadManager.STATUS_SUCCESSFUL
                            && packageInfo.versionCode < appInfo.getVersionCode()) {
                        appInfo.setDownloadState(DownloadManager.STATUS_EXTRA_UPGRADABLE);

                    }
                }
            }

            // 若下载地址为空，则该应用为敬请期待
            if (TextUtils.isEmpty(appInfo.getApkUrl()))
                appInfo.setDownloadState(DownloadManager.STATUS_NOTREADY);

            synchronized (searchResultList) {
                searchResultList.add(appInfo);
            }
        }
    }

    public void setGuessFavourite(IGuessFavourite guessFavourite) {
        this.guessFavourite = guessFavourite;
    }

    /**
     * 显示猜你喜欢
     */
    protected void showGuessYouFavourite() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultListView.setAdapter(null);
                showHeader(true);
                searchResultListView.setAdapter(searchResultAdapter);
                setOnItemClickListener();
                synchronized (searchResultList) {
                    searchResultList.clear();
                }
                if (guessFavourite != null) {
                    fillItemData(guessFavourite.getFavouriteList());
                }
            }
        });
    }

    protected void showHeader(boolean isShow) {
        if (isShow) {
            if (searchResultListView.getHeaderViewsCount() == 0) {
                searchResultListView.addHeaderView(headerView);
            }
        } else {
            searchResultListView.removeHeaderView(headerView);
        }
    }

    protected void setOnItemClickListener() {
        searchResultListView.setOnItemClickListener(GuessFavouriteFragment.this);
    }

    protected void resetRefreshUi() {
        int delay = 200;
        if (System.currentTimeMillis() - mStartTime <= 1500)
            delay = 2000;

        searchResultListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchResultListView.onLoadMoreComplete();
            }
        }, delay);
        mStartTime = 0L;
    }

    /**
     * 重新刷新上次搜索结果列表
     */
    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            synchronized (searchResultList) {
                if (ListUtils.isEmpty(searchResultList)) {
                    return false;
                }
                synchronized (searchResultList) {
                    AppInfoUtils.updateDownloadState(mContext, searchResultList);
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                //对于SearchAllFragment这个子类它是由两个adapter对listview进行适配的，每次刷新时，两个adapter都要更新，防止状态出错
                if (getFragmentTag().equals(SearchAllFragment.TAG)) {
                    searchResultAdapter.notifyDataSetChanged();
                    mergeAdapter.notifyDataSetChanged();
                } else {
                    searchResultAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    //    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (searchResultListView != null && taskInfos != null) {
            synchronized (searchResultList) {
                AppInfoUtils.updateDownloadState(mContext, searchResultList);
            }
            for (TaskInfo taskInfo : taskInfos) {
                updateProgress(taskInfo);
            }
            if (searchResultAdapter != null) {
                searchResultAdapter.notifyDataSetChanged();
            }
        }
    }

    //通过监听变化，更新下载搜索结果列表
    protected void updateProgress(TaskInfo taskInfo) {
        synchronized (searchResultList) {
            for (AppInfo appInfo : searchResultList) {
                if (taskInfo.getAppId() == appInfo.getAppId()) {
                    // calculate the task download speed
                    DownloadHelper.calcDownloadSpeed(mContext, appInfo, taskInfo);

                    appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                    appInfo.setDownloadTotalSize(-1 == taskInfo.getApkTotalSize() ? AppInfoUtils.getPatchApkSize(appInfo) : taskInfo
                            .getApkTotalSize());
                    appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                    appInfo.setDownloadState(taskInfo.getDownloadState());
                    appInfo.setLocalUri(taskInfo.getApkLocalUri());
                    appInfo.setInDownloadDB(true);
                    appInfo.setDownloadPatch(taskInfo.getApkTotalSize() < appInfo.getApkSize());
                    appInfo.setApkDownloadId(taskInfo.getTaskId());
                    int reason = taskInfo.getReason();
                    DownloadHelper.handleMsgForPauseOrError(mContext, appInfo.getAppName(), taskInfo.getDownloadState(), reason);
                    break;
                }
            }
        }
    }

    //当应用免费信息改变时 接受广播处理
//    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        //刷新list
        if (searchResultAdapter != null) {
            AppInfoUtils.updateAppFreeState(mContext, AppInfoUtils.getAllGameIds(searchResultList), searchResultAdapter, TAG);
        }
        if (!IdentityHelper.isLogined(mContext) && searchResultAdapter != null) {
            searchResultAdapter.resetIfreeFlow();
        }
    }

    /**
     * 获取子类的标志位来证明是哪个子类
     */
    public abstract String getFragmentTag();
}
