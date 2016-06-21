package com.snailgame.cjg.store;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.VirRechargeHotAdapter;
import com.snailgame.cjg.store.model.VirRechargeModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import butterknife.Bind;

/**
 * 虚拟充值 热门游戏
 * Created by TAJ_C on 2015/11/27.
 */
public class VirRechargeHotFragment extends AbsBaseFragment {

    private static final String TAG = VirRechargeHotFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView listView;


    public static VirRechargeHotFragment getInstance() {
        VirRechargeHotFragment fragment = new VirRechargeHotFragment();
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
        View headerView = new View(getActivity());
        View footerView = new View(getActivity());
        footerView.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(12)));
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);

        showLoading();
    }

    @Override
    protected void loadData() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_STORE_HOT_VIRTUAL_LIST, TAG,
                VirRechargeModel.class, new IFDResponse<VirRechargeModel>() {
                    @Override
                    public void onSuccess(VirRechargeModel result) {
                        resetRefreshUi();
                        if (result != null && result.getCode() == 200 && result.getItem() != null) {
                            listView.setAdapter(new VirRechargeHotAdapter(getActivity(), result.getItem().getGameGoodsList()));
                        } else {
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
    public void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}
