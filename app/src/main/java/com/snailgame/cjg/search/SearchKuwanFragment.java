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
import com.snailgame.cjg.search.adapter.SearchKuwanAdapter;
import com.snailgame.cjg.search.model.SearchKuwanModel;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * 搜索 -> 酷玩fragment
 * Created by pancl on 2015/4/30.
 */
public class SearchKuwanFragment extends GuessFavouriteFragment implements ISearchTabController, LoadMoreListView.OnLoadMoreListener {
    static final String TAG = SearchKuwanFragment.class.getName();
    private SearchKuwanAdapter searchKuwanAdapter;
    private SearchKuwanModel searchInfoModel;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        super.initView();
        searchResultListView.enableLoadingMore(true);
        searchResultListView.setLoadingListener(this);

        searchKuwanAdapter = new SearchKuwanAdapter(mContext, null, mRoute);
        searchResultListView.setAdapter(searchKuwanAdapter);

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
        searchKuwanAdapter.refreshRoute(route);
        searchResultAdapter.refreshRoute(route);
    }

    @Override
    public void clearResult() {
        searchKuwanAdapter.refreshData(null);
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
    private void loadSearchResultTask(String searchText) {
        String url = buildUrl(searchText, nextPage, 2);
        mEmptyView.showLoading();
        FSRequestHelper.newGetRequest(url, TAG,
                SearchKuwanModel.class, new IFDResponse<SearchKuwanModel>() {
                    @Override
                    public void onSuccess(SearchKuwanModel model) {
                        binData(model);
                    }

                    @Override
                    public void onNetWorkError() {
                        resetRefreshUi();
                        mEmptyView.showError();
                    }

                    @Override
                    public void onServerError() {
                        resetRefreshUi();
                        mEmptyView.showError();
                    }
                }, false, true, new ExtendJsonUtil());
    }

    public void setGuessFavourite(IGuessFavourite guessFavourite) {
        this.guessFavourite = guessFavourite;
    }

    private void binData(SearchKuwanModel model) {
        searchInfoModel = model;
        if (searchInfoModel == null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clearResult();
                    mEmptyView.showEmpty();
                    showGuessYouFavourite();
                }
            });
            return;
        }

        final List<SearchKuwanModel.ModelItem> searchList = searchInfoModel.getItemList();

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

                searchKuwanAdapter.addData(searchList);

                showHeader(false);
                searchResultListView.setAdapter(searchKuwanAdapter);

                if (searchInfoModel.getPageInfo().getTotalPageCount() == 1) {
                    searchResultListView.onNoMoreData();
                } else {
                    searchResultListView.onLoadMoreComplete();
                }
            }
        });
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

