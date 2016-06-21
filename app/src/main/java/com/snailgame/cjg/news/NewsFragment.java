package com.snailgame.cjg.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.common.widget.PullDownRefreshHeader;
import com.snailgame.cjg.event.NewsIgnoreEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.news.adpter.NewsListAdapter;
import com.snailgame.cjg.news.model.NewsListModel;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by TAJ_C on 2016/4/12.
 */
public class NewsFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {

    private static final String KEY_CHANNEL_ID = "key_channel_id";

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;

    @Bind(R.id.refresh_header_container)
    PtrFrameLayout mPtrFrame;

    PullDownRefreshHeader pullDownRefreshHeader;

    private NewsListAdapter mAdapter;
    private int channelId;
    private int currentPage = 1;
    private int totalPage = 1;

    private static final int INIT = 0, REFRESH = 1;
    private int refreshType = INIT;
    public static String KEY_LIST_DATA = "key_list_data";
    private ArrayList<NewsListModel.ModelItem.DataBean> newsList;

    public static Fragment getInstance(int channelId) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CHANNEL_ID, channelId);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        newsList = savedInstanceState.getParcelableArrayList(KEY_LIST_DATA);
        if (mAdapter != null && newsList != null && newsList.size() > 0) {
            mAdapter.refreshData(newsList);
        } else {
            showEmpty();
        }

        if (savedInstanceState.getBoolean(KEY_NO_MORE, false)) {
            noMoreData();
        }
    }

    @Override
    protected void saveData(Bundle outState) {
        if (newsList != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putParcelableArrayList(KEY_LIST_DATA, newsList);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
        }
    }

    @Override
    protected void setTAG() {
        TAG = this.getClass().getSimpleName() + channelId;
    }

    @Override
    protected void handleIntent() {
        channelId = getArguments().getInt(KEY_CHANNEL_ID);
        MainThreadBus.getInstance().register(this);
        mRoute = createRoute();
        setTAG();
    }

    @Override
    protected void initView() {
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);
        mAdapter = new NewsListAdapter(getActivity(), newsList, mRoute);
        loadMoreListView.setAdapter(mAdapter);

        setPullDownToRefresh();

    }

    private void setPullDownToRefresh() {
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, loadMoreListView, header);
            }
        });

        pullDownRefreshHeader = new PullDownRefreshHeader(getActivity(), null, null);
        pullDownRefreshHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrFrame.setHeaderView(pullDownRefreshHeader);
        mPtrFrame.addPtrUIHandler(pullDownRefreshHeader);
        mPtrFrame.disableWhenHorizontalMove(true);

    }


    private void refreshData() {
        refreshType = REFRESH;
        loadData();
    }

    @Override
    protected void loadData() {
        newsList = null;
        showLoading();
        getNewsData();
    }

    private void getNewsData() {

        String url = JsonUrl.getJsonUrl().JSON_URL_NEWS_CHANNEL_NEWS_CONTENT +
                "?nChannelId=" + channelId +
                ((newsList == null) ? "" : ("&nArticleId=" + newsList.get(newsList.size() - 1).getNArticleId())) +
                "&cImei=" + PhoneUtil.getDeviceUUID(getActivity());

        FSRequestHelper.newGetRequest(url, TAG, NewsListModel.class, new IFDResponse<NewsListModel>() {
            @Override
            public void onSuccess(NewsListModel newsListModel) {
                resetRefreshUi();
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                }

                if (newsListModel != null && newsListModel.getItem() != null) {
                    List<NewsListModel.ModelItem.DataBean> data = newsListModel.getItem().getData();

                    if (newsList == null) {
                        newsList = new ArrayList<NewsListModel.ModelItem.DataBean>();
                    }

                    newsList.addAll(data);
                    mAdapter.refreshData(newsList);

                    if (newsListModel.getItem().getPage() != null) {
                        totalPage = newsListModel.getItem().getPage().getTotalPageCount();
                        currentPage = newsListModel.getItem().getPage().getRequestPageNum();
                    }

                }

                if (newsList == null) {
                    showEmpty();
                }

                if (currentPage >= totalPage) {
                    noMoreData();
                }

            }

            @Override
            public void onNetWorkError() {
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                } else {
                    showError();
                }
            }

            @Override
            public void onServerError() {
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                } else {
                    showError();
                }
            }
        }, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onLoadMore() {
        getNewsData();
    }


    @Subscribe
    public void ignoreNews(NewsIgnoreEvent event) {
        if (newsList != null) {
            for (NewsListModel.ModelItem.DataBean item : newsList) {
                if (item.getNArticleId() == event.getArticleId()) {
                    newsList.remove(item);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 搜索
        int[] route = new int[]{
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }


}
