package com.snailgame.cjg.store;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.VirRechargeAllAdapter;
import com.snailgame.cjg.store.model.VirRechargeModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import butterknife.Bind;

/**
 * 虚拟代充 全部游戏
 * Created by TAJ_C on 2015/12/1.
 */
public class VirRechargeAllFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {

    private static final String TAG = VirRechargeHotFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView listView;

    private VirRechargeAllAdapter mAdapter;
    private int curPage = 1;


    public static VirRechargeAllFragment getInstance() {
        VirRechargeAllFragment fragment = new VirRechargeAllFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
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
        String loadUrl = JsonUrl.getJsonUrl().JSON_URL_STORE_ALL_VIRTUAL_LIST +
                "&curpage=" + curPage;

        FSRequestHelper.newGetRequest(loadUrl, TAG,
                VirRechargeModel.class, new IFDResponse<VirRechargeModel>() {
                    @Override
                    public void onSuccess(VirRechargeModel result) {
                        resetRefreshUi();

                        if (result != null && result.getCode() == 200 && result.getItem() != null) {
                            if (mAdapter == null) {
                                mAdapter = new VirRechargeAllAdapter(getActivity(), result.getItem().getGameGoodsList());
                                listView.setAdapter(mAdapter);
                            } else {
                                mAdapter.addData(result.getItem().getGameGoodsList());
                            }

                            curPage++;

                            if (curPage > result.getPageTotal()) {
                                noMoreData();
                            }

                        }

                        if (mAdapter.getCount() <= 0) {
                            showEmpty();
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
                }, true);
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
    public void onLoadMore() {
        loadData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}
