package com.snailgame.cjg.search;

import android.text.TextUtils;
import android.view.View;

import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.search.adapter.SearchGameAppAdapter;
import com.snailgame.cjg.search.model.SearchInfo;
import com.snailgame.cjg.search.model.SearchInfoModel;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * 搜索 -> 游戏/软件fragment
 * Created by pancl on 2015/4/30.
 */
public class SearchGameAppFragment extends GuessFavouriteFragment implements ISearchTabController, LoadMoreListView.OnLoadMoreListener {
    static final String TAG = SearchGameAppFragment.class.getName();
    private SearchInfoModel searchInfoModel;

    private volatile boolean isNewLoaded = false;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        super.initView();
        searchResultListView.enableLoadingMore(true);
        searchResultListView.setLoadingListener(this);

        searchResultAdapter = new SearchGameAppAdapter(mContext, searchResultList, mRoute);
        searchResultListView.setAdapter(searchResultAdapter);
        super.setOnItemClickListener();

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

    @Override
    public void onLoadMore() {
        if (searchInfoModel != null && nextPage > searchInfoModel.getPageInfo().getTotalPageCount()) {
            searchResultListView.onNoMoreData();
            return;
        }

        if (!TextUtils.isEmpty(searchText) && TextUtils.getTrimmedLength(searchText) > 0) {
            loadSearchResultTask(searchText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public void search(String searchText) {
        this.searchText = searchText;
        nextPage = 1;
        clearResult();
        loadSearchResultTask(searchText);
    }

    @Override
    public void refreshRoute(int[] route) {
        mRoute = route;
        searchResultAdapter.refreshRoute(route);
    }

    @Override
    public void clearResult() {
        synchronized (searchResultList) {
            searchResultList.clear();
        }
        if (searchResultAdapter != null) {
            searchResultAdapter.refreshData(null);
            searchResultAdapter.notifyDataSetChanged();
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
            String url = buildUrl(searchText, nextPage, 0);//0:游戏/应用 1:动态 2:酷玩
            mEmptyView.showLoading();
            FSRequestHelper.newGetRequest(url, TAG,
                    SearchInfoModel.class, new IFDResponse<SearchInfoModel>() {
                        @Override
                        public void onSuccess(SearchInfoModel model) {
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

    private void binData(SearchInfoModel model) {
        searchInfoModel = model;
        if (searchInfoModel == null) {
            clearResult();
            mEmptyView.showEmpty();
            showGuessYouFavourite();
            return;
        }

        final List<SearchInfo> searchList = searchInfoModel.getSearchInfos();
        resetRefreshUi();
        if (searchList == null || searchList.size() == 0) {
            clearResult();
            mEmptyView.showEmpty();
            showGuessYouFavourite();
            return;
        }

        if (nextPage > searchInfoModel.getPageInfo().getTotalPageCount()) {
            return;
        }

        nextPage = searchInfoModel.getPageInfo().getRequestPageNum() + 1;
        fillItemData(searchList);
        showHeader(false);
        if (searchInfoModel.getPageInfo().getTotalPageCount() == 1) {
            searchResultListView.onNoMoreData();
        } else {
            searchResultListView.onLoadMoreComplete();
        }
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        super.onDownloadInfoChange(event);
    }

    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        super.userInfoChanged(event);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}

