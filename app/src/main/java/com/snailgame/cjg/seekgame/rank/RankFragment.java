package com.snailgame.cjg.seekgame.rank;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.RankFilterEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekgame.SeekGameAdapter;
import com.snailgame.cjg.seekgame.rank.adapter.RankAdapter;
import com.snailgame.cjg.seekgame.rank.adapter.RankViewHolder;
import com.snailgame.cjg.seekgame.rank.model.RankModel;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesHelper;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunxy on 2015/3/23.
 * 游戏排行
 */
public class RankFragment extends AbsBaseFragment {
    public static final int TOP_3 = 3;
    private static final String TAG = RankFragment.class.getSimpleName();
    private int top3ResId[];
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.rank_update_time)
    TextView updateTime;
    @Bind(R.id.rank_filter)
    TextView rankFilter;
    @Bind(R.id.top_layout)
    RelativeLayout rankTopLayout;
    private View headerView;
    private final static String KEY_URL = "key_url";
    private final static String KEY_MODEL = "key_model";
    private static final String DEFAULT_COUNTRY = FreeStoreApp.getContext().getString(R.string.china);
    private static final String DEFAULT_FEED_TYPE = FreeStoreApp.getContext().getString(R.string.rank_best_shell);
    private static final String MANUL_CHANGE_USER_VISIBLE = "key_change_user_visible";
    private List<RankViewHolder> headerHolderLists;
    private RankAdapter mAdapter;
    private RankModel mModel;
    private ArrayList<AppInfo> appInfoList = new ArrayList<>();
    private String url;
    private final Object syncObj = new Object();
    private QueryTaskListTask task;

    /**
     * @param url   链接
     * @param route PV用路径记载
     * @return
     */
    public static RankFragment getInstance(String url, boolean isManulChangeUserVisible, int[] route) {
        RankFragment fragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        bundle.putBoolean(MANUL_CHANGE_USER_VISIBLE, isManulChangeUserVisible);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_RANK);
        queryDb();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_RANK);
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.rank_fragment;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        handleArguments();
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.top_3_rank_item, loadMoreListView, false);
        headerView.setVisibility(View.GONE);
        initHeaderView(headerView);
        mAdapter = new RankAdapter(getActivity(), appInfoList, mRoute);
        loadMoreListView.addHeaderView(headerView);
        loadMoreListView.setAdapter(mAdapter);
    }


    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
            if (bundle.getBoolean(MANUL_CHANGE_USER_VISIBLE))    //只有需要手动控制isLoadinUserVisibile 才会改变isLoadinUserVisibile为false
                isLoadinUserVisibile = false;
        }
    }

    @Override
    protected void loadData() {
        showLoading();
        enableLoadMore();
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
        createOrGetRankTask(helper.getValue(AppConstants.RANK_COUNTRY_NAME, DEFAULT_COUNTRY)
                , helper.getValue(AppConstants.RANK_FEED_NAME, DEFAULT_FEED_TYPE));
    }

    private void enableLoadMore() {
        loadMoreListView.enableLoadingMore(true);
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        showLoading();
        mModel = savedInstanceState.getParcelable(KEY_MODEL);
        showRankLists(mModel);
    }

    @Override
    protected void saveData(Bundle outState) {
        if (appInfoList != null && appInfoList.size() != 0 && mModel != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putParcelable(KEY_MODEL, mModel);
        }
    }

    private void createOrGetRankTask(String country, String feedType) {
        String questUrl = url + ComUtil.getEncodeString(country) + "-" + ComUtil.getEncodeString(feedType) + ".json";
        FSRequestHelper.newGetRequest(questUrl, TAG, RankModel.class, new IFDResponse<RankModel>() {
            @Override
            public void onSuccess(RankModel result) {
                mModel = result;
                showRankLists(mModel);
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, true);
    }

    /**
     * 显示排行列表
     */
    private void showRankLists(RankModel mModel) {
        if (mModel != null && mModel.getRoot() != null && mModel.getRoot().getAppLists() != null) {
            appInfoList.clear();
            AppInfoUtils.addRankDataToList(getActivity(), mModel.getRoot().getAppLists(), appInfoList);
            refreshHeaderView();
            mAdapter.notifyDataSetChanged();
            showUpdateTime(mModel);
            if (ListUtils.isEmpty(mModel.getRoot().getAppLists()))
                showEmpty();
            noMoreData();
            rankTopLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showUpdateTime(RankModel mModel) {
        String updateTimeString = mModel.getRoot().getUpdatedTime();
        if (updateTimeString != null && updateTimeString.trim().contains(" "))
            updateTime.setText(String.format(getString(R.string.rank_update), updateTimeString.split(" ")[1]));
    }

    /**
     * 初始化top3 headerVew
     */
    private void initHeaderView(View headerView) {
        headerHolderLists = new ArrayList<>(TOP_3);
        headerHolderLists.add(new RankViewHolder(getActivity(), headerView.findViewById(R.id.top_1)));
        headerHolderLists.add(new RankViewHolder(getActivity(), headerView.findViewById(R.id.top_2)));
        headerHolderLists.add(new RankViewHolder(getActivity(), headerView.findViewById(R.id.top_3)));
    }

    /**
     * 刷新Top3按钮以及进度条
     */
    private void refreshHeaderView() {
        if (appInfoList.size() >= TOP_3 && headerHolderLists.size() >= TOP_3) {
            for (int i = 0; i < TOP_3; i++) {
                RankViewHolder holder = headerHolderLists.get(i);
                mAdapter.refreshItemView(holder, appInfoList.get(i), i);
            }
            headerView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 显示筛选对话框
     */
    @OnClick(R.id.rank_filter)
    void showFilterDialog() {
        String countryArrays[];
        String countryString = PersistentVar.getInstance().getSystemConfig().getCountryLists();
        if (!TextUtils.isEmpty(countryString))
            countryArrays = countryString.trim().split(",");
        else {
            countryArrays = getResources().getStringArray(R.array.rank_country_lists);//本地默认
        }
        DialogUtils.showRankFilterDialog(getActivity(), countryArrays);
        // ComUtil.setDrawableRight(rankFilter,R.drawable.ic_extend_up);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Subscribe
    public void onFilterChanged(RankFilterEvent event) {
        if (!event.isDismiss()) {
            clearCurrentData();
            Log.i(TAG,"----------event.getType---"+event.getType());
            createOrGetRankTask(event.getCountry(), event.getType());
        }
        // ComUtil.setDrawableRight(rankFilter,R.drawable.ic_extend_down);
    }

    private void clearCurrentData() {
        appInfoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                updateProgress(taskInfo);
            }
            if (mAdapter != null) {
                refreshHeaderView();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 刷新下载进度条
     *
     * @param taskInfo
     */
    private void updateProgress(TaskInfo taskInfo) {

        for (AppInfo appInfo : appInfoList) {
            if (appInfo != null) {
                if (taskInfo.getAppId() == appInfo.getAppId()) {
                    // calculate the task download speed
                    DownloadHelper.calcDownloadSpeed(getActivity(), appInfo, taskInfo);
                    appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                    appInfo.setDownloadTotalSize(-1 == taskInfo.getApkTotalSize() ? AppInfoUtils.getPatchApkSize(appInfo) : taskInfo
                            .getApkTotalSize());
                    appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                    appInfo.setDownloadState(taskInfo.getDownloadState());
                    appInfo.setLocalUri(taskInfo.getApkLocalUri());
                    appInfo.setApkDownloadId(taskInfo.getTaskId());
                    int reason = taskInfo.getReason();
                    DownloadHelper.handleMsgForPauseOrError(getActivity(), appInfo.getAppName(),
                            taskInfo.getDownloadState(), reason);
                    break;
                }
            }
        }
    }

    private void queryDb() {
        if (task != null)
            task.cancel(true);
        task = new QueryTaskListTask();
        task.execute();
    }

    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ListUtils.isEmpty(appInfoList)) {
                return false;
            }
            synchronized (syncObj) {
                AppInfoUtils.updateDownloadState(mParentActivity, appInfoList);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (mAdapter != null) {
                    refreshHeaderView();
                    mAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_SEEK_GAME &&
                event.getPagePosition() == SeekGameAdapter.RANK &&
                loadMoreListView != null) {
            loadMoreListView.setSelection(0);
        }
    }
}
