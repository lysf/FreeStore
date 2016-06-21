package com.snailgame.cjg.store;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.StorePointSelectedEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.ExchangeAllAdapter;
import com.snailgame.cjg.store.adapter.PointStoreAllAdapter;
import com.snailgame.cjg.store.model.ExchangeAllListModel;
import com.snailgame.cjg.store.model.GoodsListModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import third.commonsware.cwac.merge.MergeAdapter;

/**
 * Created by TAJ_C on 2015/12/2.
 */
public class PointStoreAllFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {
    private static final String TAG = PointStoreAllFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView listView;

    MergeAdapter mMergeAdapter;
    PointStoreAllAdapter mAdapter;
    ExchangeAllAdapter mExchangeAdapter;

    private int curPage = 1;
    private int sid = 0;

    private int totalPage = Integer.MAX_VALUE;

    private final static int STATUS_INIT = 0;
    private final static int STATUS_GET_SUCCESS = 1;
    private final static int STATUS_GET_FAIL = 2;
    int iPointAllGet = STATUS_INIT;
    int iExchangeAllGet = STATUS_INIT;

    public static PointStoreAllFragment getInstance() {
        PointStoreAllFragment fragment = new PointStoreAllFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(8)));
        listView.addHeaderView(view);
        listView.enableLoadingMore(true);
        listView.setLoadingListener(this);
        showLoading();
    }

    @Override
    protected void loadData() {
        loadPointList();
        loadExchangeList();
    }

    /**
     * 加载积分商城全部商品
     */
    private void loadPointList() {
        String loadUrl = JsonUrl.getJsonUrl().JSON_URL_STORE_ALL_POINT_LIST +
                "&curpage=" + curPage +
                (sid == 0 ? "" : "&sid=" + sid);
        FSRequestHelper.newGetRequest(loadUrl, TAG, GoodsListModel.class, new IFDResponse<GoodsListModel>() {
            @Override
            public void onSuccess(GoodsListModel result) {
                resetRefreshUi();
                if (result != null && result.getCode() == 200 && result.getItem() != null) {
                    curPage++;
                    totalPage = result.getPageTotal();

                    if (mAdapter == null) {
                        mAdapter = new PointStoreAllAdapter(getActivity(), result.getItem().getGoodsList());
                        pointHotDataGet(STATUS_GET_SUCCESS);
                    } else {
                        mAdapter.addData(result.getItem().getGoodsList());
                        if (curPage > totalPage) {
                            noMoreData();
                        }
                    }
                } else {
                    pointHotDataGet(STATUS_GET_FAIL);
                }
            }

            @Override
            public void onNetWorkError() {
                pointHotDataGet(STATUS_GET_FAIL);
            }

            @Override
            public void onServerError() {
                pointHotDataGet(STATUS_GET_FAIL);
            }
        }, false);
    }


    /**
     * 加载积分兑换全部商品
     */
    private void loadExchangeList() {
        String loadUrl = JsonUrl.getJsonUrl().JSON_URL_STORE_ALL_EXCHANGE_LIST +
                (sid == 0 ? "" : "&sid=" + sid);
        FSRequestHelper.newGetRequest(loadUrl, TAG, ExchangeAllListModel.class, new IFDResponse<ExchangeAllListModel>() {
            @Override
            public void onSuccess(ExchangeAllListModel result) {
                resetRefreshUi();
                if (result != null && result.getCode() == 200 && result.getItem() != null) {
                    mExchangeAdapter = new ExchangeAllAdapter(getActivity(), result.getItem().getGoodsList());

                    exchangeHotDataGet(STATUS_GET_SUCCESS);
                } else
                    exchangeHotDataGet(STATUS_GET_FAIL);
            }

            @Override
            public void onNetWorkError() {
                exchangeHotDataGet(STATUS_GET_FAIL);
            }

            @Override
            public void onServerError() {
                exchangeHotDataGet(STATUS_GET_FAIL);
            }
        }, false);
    }

    @Override
    protected LoadMoreListView getListView() {
        return listView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void saveData(Bundle outState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onLoadMore() {
        loadPointList();
    }

    private void pointHotDataGet(int status) {
        iPointAllGet = status;
        dataGet();
    }

    private void exchangeHotDataGet(int status) {
        iExchangeAllGet = status;
        dataGet();
    }


    private synchronized void dataGet() {
        if (iPointAllGet == STATUS_INIT ||
                iExchangeAllGet == STATUS_INIT)
            return;     // 只有两个请求都结束才会刷新界面
        else if (iPointAllGet == STATUS_GET_FAIL ||
                iExchangeAllGet == STATUS_GET_FAIL)
            showError();  // 两个请求都失败才会显示请求失败
        else {
            resetRefreshUi();

            if ((mExchangeAdapter == null || mExchangeAdapter.getCount() == 0) &&
                    (mAdapter == null || mAdapter.getCount() == 0))
                showEmpty();        // 两个请求数据都为空的时候，返回没有数据
            else {
                if (listView != null) {
                    if (mMergeAdapter == null)
                        mMergeAdapter = new MergeAdapter();

                    if (mExchangeAdapter != null)
                        mMergeAdapter.addAdapter(mExchangeAdapter);

                    if (mAdapter != null)
                        mMergeAdapter.addAdapter(mAdapter);

                    listView.setAdapter(mMergeAdapter);

                    if (curPage > totalPage) {
                        noMoreData();
                    }
                }
            }

        }
    }

    @Subscribe
    public void loadDataBySelector(StorePointSelectedEvent event) {
        sid = event.getSelectedPosition() + 1;
        curPage = 1;
        mMergeAdapter = null;
        mAdapter = null;
        mMergeAdapter = null;
        iPointAllGet = STATUS_INIT;
        iExchangeAllGet = STATUS_INIT;
        showLoading();
        loadData();
    }
}
