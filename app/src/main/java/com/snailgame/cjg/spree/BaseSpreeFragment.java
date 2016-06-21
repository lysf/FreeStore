package com.snailgame.cjg.spree;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.spree.adapter.HotLocalSpreeAdapter;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.spree.model.SpreesAppModel;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;

/**
 * Created by TAJ_C on 2015/5/6.
 */
public abstract class BaseSpreeFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {
    static String TAG = BaseSpreeFragment.class.getName();

    protected LoadMoreListView mListView;
    protected HotLocalSpreeAdapter mAdapter;

    protected QueryTaskListTask task;
    protected ArrayList<SpreesAppModel.ModelItem> itemList;
    protected String mUrl;
    public static final String KEY_SPREE_LIST = "key_spree_list";
    protected ArrayList<String> mKeyArray;
    private int totalPage = 0;
    private int currentPage = 1;

    protected abstract void setJsonUrl();

    @Override
    protected int getLayoutId() {
        return R.layout.spree_fragment;
    }


    @Override
    protected void initView() {
        mListView = (LoadMoreListView) mContent.findViewById(R.id.spree_list);
        mListView.enableLoadingMore(true);
        mListView.setLoadingListener(this);

        View spaceView = new View(getActivity());
        spaceView.setLayoutParams(new AbsListView.LayoutParams(ComUtil.dpToPx(1), ComUtil.dpToPx(0)));
        mListView.addHeaderView(spaceView);

        mAdapter = new HotLocalSpreeAdapter(getActivity(), null, mRoute);
        mAdapter.setOnSpreeGetListener(new HotLocalSpreeAdapter.SpreeInterfaceListener() {
            @Override
            public void spreeGetAction(SpreeGiftInfo spreeGiftInfo) {
                if (mKeyArray == null) {
                    mKeyArray = new ArrayList<>();
                }
                SpreeUtils.getSpreeAction(getActivity(), mKeyArray, spreeGiftInfo, TAG);
            }


        });
        mListView.setAdapter(mAdapter);
        setJsonUrl();
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }

    @Override
    protected void loadData() {
        showLoading();
        if (false == TextUtils.isEmpty(mUrl)) {
            getSpreeData();
        }
    }

    @Override
    protected LoadMoreListView getListView() {
        return mListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryDb();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        itemList = savedInstanceState.getParcelableArrayList(KEY_SPREE_LIST);
        showView(itemList);
        if (savedInstanceState.getBoolean(KEY_NO_MORE, false))
            noMoreData();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (itemList != null && itemList.size() != 0) {
            outState.putParcelableArrayList(KEY_SPREE_LIST, itemList);
            outState.putBoolean(KEY_SAVE, true);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
        }
    }

    @Override
    public void onLoadMore() {
        getSpreeData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    /**
     * 获取 礼包列表 数据
     */
    private void getSpreeData() {
        String url = mUrl + "currentPage=" + currentPage +"&number=10";
        FSRequestHelper.newGetRequest(url, TAG, SpreesAppModel.class, new IFDResponse<SpreesAppModel>() {
            @Override
            public void onSuccess(SpreesAppModel result) {
                resetRefreshUi();

                if (result != null) {
                    if (itemList == null) {
                        itemList = result.getItemList();
                    } else {
                        itemList.addAll(result.getItemList());
                    }
                    showView(itemList);

                    if (result.getPageInfo() != null) {
                        totalPage = result.getPageInfo().getTotalPageCount();
                    }

                    if (totalPage == 1) {
                        getListView().inflateEmptyView(0);
                    }

                    if (currentPage >= totalPage) {
                        noMoreData();
                    }

                    currentPage++;
                }

            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, false, true, new ExtendJsonUtil());
    }


    private void showView(ArrayList<SpreesAppModel.ModelItem> itemList) {
        if (ListUtils.isEmpty(itemList)) {
            showEmpty();
            return;
        }

        if (mAdapter != null) {
            mAdapter.refreshData(itemList);
        }
    }


    /**
     * 下载 进度条UI 变动
     *
     * @param taskInfos
     */
    public void onDownloadInfoChange(ArrayList<TaskInfo> taskInfos) {
        if (mListView != null && taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                updateProgress(taskInfo);
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 更新应用列表进度
     *
     * @param taskInfo
     */
    private void updateProgress(TaskInfo taskInfo) {

        for (AppInfo appInfo : mAdapter.getAppInfoList()) {
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

            if (mAdapter != null && ListUtils.isEmpty(mAdapter.getAppInfoList())) {
                return false;
            }
            AppInfoUtils.updateDownloadState(mParentActivity, mAdapter.getAppInfoList());

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                mAdapter.notifyDataSetChanged();
            }

        }
    }

}
