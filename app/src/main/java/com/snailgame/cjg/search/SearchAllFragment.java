package com.snailgame.cjg.search;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.AppNewsModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.search.adapter.SearchActiveAdapter;
import com.snailgame.cjg.search.adapter.SearchGameAppAdapter;
import com.snailgame.cjg.search.adapter.SearchKuwanAdapter;
import com.snailgame.cjg.search.model.SearchAllModel;
import com.snailgame.cjg.search.model.SearchInfo;
import com.snailgame.cjg.search.model.SearchKuwanModel;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 搜索 -> 全部fragment
 * Created by pancl on 2015/4/30.
 */
public class SearchAllFragment extends GuessFavouriteFragment implements ISearchTabController, AdapterView.OnItemClickListener, LoadMoreListView.OnLoadMoreListener {
    static final String TAG = SearchAllFragment.class.getName();
    private HashMap<String, ListAdapter> adapterHashMap = new HashMap<>();
    private SearchGameAppAdapter searchGameAppAdapter;

    private volatile boolean isNewLoaded = false;
    private static final String TYPE_INDEX_APP = "app"; //游戏应用
    private static final String TYPE_INDEX_STRATEGY = "strategy";//动态
    private static final String TYPE_INDEX_KUWAN = "kuwan";//酷玩

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (searchResultListView != null && taskInfos != null) {
            AppInfoUtils.updateDownloadState(mContext, searchResultList);
            for (TaskInfo taskInfo : taskInfos) {
                super.updateProgress(taskInfo);
            }
            if (searchGameAppAdapter != null) {
                searchGameAppAdapter.notifyDataSetChanged();
            }
            if (searchResultAdapter != null) {
                searchResultAdapter.notifyDataSetChanged();
            }
        }
    }

    //当应用免费信息改变时 接受广播处理
    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        //刷新list
        CommonListItemAdapter commonListItemAdapter = (CommonListItemAdapter) adapterHashMap.get(TYPE_INDEX_APP);
        AppInfoUtils.updateAppFreeState(mContext, AppInfoUtils.getAllGameIds(searchResultList), commonListItemAdapter, TAG);
        if (!IdentityHelper.isLogined(mContext)) {
            commonListItemAdapter.resetIfreeFlow();
        }
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        super.initView();
        searchResultListView.setDividerHeight(0);

        searchGameAppAdapter = new SearchGameAppAdapter(mContext, searchResultList, mRoute);
        SearchActiveAdapter searchActiveAdapter = new SearchActiveAdapter(mContext, null, mRoute);
        SearchKuwanAdapter searchKuwanAdapter = new SearchKuwanAdapter(mContext, null, mRoute);

        mergeAdapter.addAdapter(searchGameAppAdapter);
        mergeAdapter.addAdapter(searchActiveAdapter);
        mergeAdapter.addAdapter(searchKuwanAdapter);

        adapterHashMap.put(TYPE_INDEX_APP, searchGameAppAdapter);
        adapterHashMap.put(TYPE_INDEX_STRATEGY, searchActiveAdapter);
        adapterHashMap.put(TYPE_INDEX_KUWAN, searchKuwanAdapter);
        searchResultListView.enableLoadingMore(true);
        searchResultListView.setLoadingListener(this);
        searchResultListView.setAdapter(mergeAdapter);
        searchResultListView.setOnItemClickListener(this);

        searchResultAdapter = new SearchGameAppAdapter(mContext, searchResultList, mRoute);

        mEmptyView = new EmptyView(mParentActivity, searchResultListView);
        mEmptyView.setErrorButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(searchText) && TextUtils.getTrimmedLength(searchText) > 0) {
                    search(searchText);
                }
            }
        });
    }


    public void setRoute(int[] route) {
        this.mRoute = route;
    }

    @Override
    public void search(String searchText) {
        this.searchText = searchText;
        clearResult();
        loadSearchResultTask(searchText);
    }

    @Override
    public void refreshRoute(int[] route) {
        setRoute(route);
        ((SearchGameAppAdapter) adapterHashMap.get(TYPE_INDEX_APP)).refreshRoute(route);
        ((SearchActiveAdapter) adapterHashMap.get(TYPE_INDEX_STRATEGY)).refreshRoute(route);
        ((SearchKuwanAdapter) adapterHashMap.get(TYPE_INDEX_KUWAN)).refreshRoute(route);
    }

    @Override
    public void clearResult() {
        synchronized (searchResultList) {
            searchResultList.clear();
        }
        SearchActiveAdapter searchActiveAdapter = (SearchActiveAdapter) adapterHashMap.get(TYPE_INDEX_STRATEGY);
        searchActiveAdapter.refreshData(null);
        searchActiveAdapter.notifyDataSetChanged();
        SearchKuwanAdapter searchKuwanAdapter = (SearchKuwanAdapter) adapterHashMap.get(TYPE_INDEX_KUWAN);
        searchKuwanAdapter.refreshData(null);
        searchKuwanAdapter.notifyDataSetChanged();

        if (searchResultAdapter != null) {
            searchResultAdapter.refreshData(null);
            searchResultAdapter.notifyDataSetChanged();
        }
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
        if (searchResultListView.getHeaderViewsCount() == 0) {//普通模式下，去掉头部位置
            position--;
        }
        if (position >= parent.getAdapter().getCount() || position < 0)
            return;
        if (parent.getAdapter().getItem(position) instanceof AppInfo) {
            AppInfo info = (AppInfo) parent.getAdapter().getItem(position);

            if (info == null)
                return;

            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
            startActivity(DetailActivity.newIntent(mContext, info.getAppId(), route));
        }
    }

    /**
     * 异步加载搜索结果
     *
     * @param searchText
     * @return 是否开始执行查询
     */
    private boolean loadSearchResultTask(String searchText) {
        if (!isNewLoaded) {
            isNewLoaded = true;
            mEmptyView.showLoading();
            String url = buildUrl(searchText, 0, 0, false);
            FSRequestHelper.newGetRequest(url, TAG,
                    SearchAllModel.class, new IFDResponse<SearchAllModel>() {
                        @Override
                        public void onSuccess(SearchAllModel model) {
                            binData(model);
                            isNewLoaded = false;
                        }

                        @Override
                        public void onNetWorkError() {
                            resetRefreshUi();
                            mEmptyView.showError();
                            isNewLoaded = false;
                        }

                        @Override
                        public void onServerError() {
                            resetRefreshUi();
                            mEmptyView.showError();
                            isNewLoaded = false;
                        }
                    }, false, true, new ExtendJsonUtil());
            return true;

        }
        return false;
    }

    @Override
    public void onLoadMore() {
        searchResultListView.onNoMoreData();
    }


    private void binData(SearchAllModel searchInfoModel) {
        if (searchInfoModel == null) {
            showGuess();
            return;
        }

        final List<SearchAllModel.ModelItem> searchList = searchInfoModel.getSearchInfos();

        resetRefreshUi();
        if (searchList == null || searchList.size() == 0) {
            showGuess();
            return;
        }
        int resultCount = 0;
        for (SearchAllModel.ModelItem modelItem : searchList) {
            if (!TextUtils.isEmpty(modelItem.getList())) {
                try {
                    if (TextUtils.equals(modelItem.getcClassify(), TYPE_INDEX_APP)) {
                        List<SearchInfo> searchInfos = JSON.parseArray(modelItem.getList(), SearchInfo.class);
                        if (!ListUtils.isEmpty(searchInfos)) {
                            resultCount += searchInfos.size();
                            updateSearchInfos(searchInfos);

                            AppInfoUtils.updatePatchInfo(mContext, searchResultList);

                            SearchGameAppAdapter searchGameAppAdapter = (SearchGameAppAdapter) adapterHashMap.get(TYPE_INDEX_APP);
                            if (AccountUtil.isAgentFree(mContext)) {
                                AppInfoUtils.updateAppFreeState(mContext, AppInfoUtils.getAllGameIds(searchResultList),
                                        searchGameAppAdapter, TAG);
                            }
                            searchGameAppAdapter.setShowHeader(true, modelItem.getiTotalRowCount());
                            searchGameAppAdapter.notifyDataSetChanged();
                        }
                    } else if (TextUtils.equals(modelItem.getcClassify(), TYPE_INDEX_STRATEGY)) {
                        List<AppNewsModel.ModelItem> searchInfos = JSON.parseArray(modelItem.getList(), AppNewsModel.ModelItem.class);
                        if (!ListUtils.isEmpty(searchInfos)) {
                            resultCount += searchInfos.size();
                            SearchActiveAdapter searchActiveAdapter = (SearchActiveAdapter) adapterHashMap.get(TYPE_INDEX_STRATEGY);
                            searchActiveAdapter.refreshData(searchInfos);
                            searchActiveAdapter.setShowHeader(true, modelItem.getiTotalRowCount());
                            searchActiveAdapter.notifyDataSetChanged();
                        }
                    } else if (TextUtils.equals(modelItem.getcClassify(), TYPE_INDEX_KUWAN)) {
                        List<SearchKuwanModel.ModelItem> searchInfos = JSON.parseArray(modelItem.getList(), SearchKuwanModel.ModelItem.class);
                        if (!ListUtils.isEmpty(searchInfos)) {
                            resultCount += searchInfos.size();
                            SearchKuwanAdapter searchKuwanAdapter = (SearchKuwanAdapter) adapterHashMap.get(TYPE_INDEX_KUWAN);
                            searchKuwanAdapter.refreshData(searchInfos);
                            searchKuwanAdapter.setShowHeader(true, modelItem.getiTotalRowCount());
                            searchKuwanAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("cannot parse modelitem to array");
                }
            }
        }
        if (resultCount == 0) {
            showGuess();
            return;
        }
        showHeader(false);
        searchResultListView.setDividerHeight(0);
        searchResultListView.setAdapter(mergeAdapter);
        mergeAdapter.notifyDataSetChanged();
        searchResultListView.onNoMoreData();
    }


    private void showGuess() {
        clearResult();
        mEmptyView.showEmpty();
        searchResultListView.setDivider(getResources().getDrawable(R.drawable.transparent));
        searchResultListView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.list_divider_height));
        showGuessYouFavourite();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}

