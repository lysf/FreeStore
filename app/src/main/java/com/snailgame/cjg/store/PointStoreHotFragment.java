package com.snailgame.cjg.store;

import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.ExchangeHotAdapter;
import com.snailgame.cjg.store.adapter.PointStoreHotAdapter;
import com.snailgame.cjg.store.model.ExchangeHotListModel;
import com.snailgame.cjg.store.model.GoodsListModel;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import butterknife.Bind;
import third.commonsware.cwac.merge.MergeAdapter;

/**
 * Created by TAJ_C on 2015/12/2.
 */
public class PointStoreHotFragment extends AbsBaseFragment {
    private static final String TAG = PointStoreHotFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView listView;

    MergeAdapter mMergeAdapter;
    PointStoreHotAdapter mAdapter;
    ExchangeHotAdapter mExchangeAdapter;

    private final static int STATUS_INIT = 0;
    private final static int STATUS_GET_SUCCESS = 1;
    private final static int STATUS_GET_FAIL = 2;
    int iPointHotGet = STATUS_INIT;
    int iExchangeHotGet = STATUS_INIT;

    public static PointStoreHotFragment getInstance() {
        PointStoreHotFragment fragment = new PointStoreHotFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gap_container;
    }


    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        listView.addHeaderView(new View(getActivity()));
        showLoading();
    }

    @Override
    protected void loadData() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_STORE_HOT_POINT_LIST, TAG, GoodsListModel.class, new IFDResponse<GoodsListModel>() {
            @Override
            public void onSuccess(GoodsListModel result) {
                if (result != null && result.getItem() != null && result.getItem().getGoodsList() != null) {
                    mAdapter = new PointStoreHotAdapter(getActivity(), result.getItem().getGoodsList());
                }

                pointHotDataGet(STATUS_GET_SUCCESS);
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


        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_STORE_HOT_EXCHANGE_LIST, TAG, ExchangeHotListModel.class, new IFDResponse<ExchangeHotListModel>() {
            @Override
            public void onSuccess(ExchangeHotListModel result) {
                if (result != null && result.getItems() != null) {
                    mExchangeAdapter = new ExchangeHotAdapter(getActivity(), result.getItems());
                }
                exchangeHotDataGet(STATUS_GET_SUCCESS);
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
    }

    private void pointHotDataGet(int status) {
        iPointHotGet = status;
        dataGet();
    }

    private void exchangeHotDataGet(int status) {
        iExchangeHotGet = status;
        dataGet();
    }


    private synchronized void dataGet() {
        if (iPointHotGet == STATUS_INIT ||
                iExchangeHotGet == STATUS_INIT)
            return;     // 只有两个请求都结束才会刷新界面
        else if (iPointHotGet == STATUS_GET_FAIL ||
                iExchangeHotGet == STATUS_GET_FAIL)
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
                }
            }

        }
    }
}
