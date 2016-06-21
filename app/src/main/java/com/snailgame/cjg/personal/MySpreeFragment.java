package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.MySpreeAdapter;
import com.snailgame.cjg.personal.model.MySpreeModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;


/**
 * 我的礼包
 */
public class MySpreeFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {
    static String TAG = MySpreeFragment.class.getName();
    private LoadMoreListView loadMoreListView;

    private MySpreeAdapter mySpreeAdapter;
    private EmptyView mEmptyView;
    private long totalPage = -1;
    private long currentPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.my_spree_fragment;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.my_spree_list);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);

        View headerView = new View(getActivity());
        loadMoreListView.addHeaderView(headerView);
        mEmptyView = new EmptyView(mParentActivity, loadMoreListView);
        mEmptyView.setErrorButtonClickListener(mErrorClickListener);
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            load();
        }
    }

    /**
     * 获取 我的礼包 数据
     */
    private void load() {
        mEmptyView.showLoading();
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_QUERY_USR_SPREE + "?number=10&currentPage=" + currentPage, TAG,
                MySpreeModel.class, new IFDResponse<MySpreeModel>() {
                    @Override
                    public void onSuccess(MySpreeModel model) {
                        resetRefreshUi();
                        if (model == null || ListUtils.isEmpty(model.getItemList())) {
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getPageInfo() != null) {
                            totalPage = model.getPageInfo().getTotalPageCount();
                        }
                        if (mySpreeAdapter == null) {
                            mySpreeAdapter = new MySpreeAdapter(getActivity(), model.getItemList());
                            loadMoreListView.setAdapter(mySpreeAdapter);
                        } else {
                            mySpreeAdapter.addNewData(model.getItemList());
                            mySpreeAdapter.notifyDataSetChanged();
                        }

                        if (model.getItemList().size() < 7 && currentPage == 1) {
                            loadMoreListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight() - model.getItemList().size() * ComUtil.dpToPx(110) - getResources().getDimensionPixelSize(R.dimen.actionbar_height));
                        }
                        if (totalPage == 1)
                            noMoreData();

                    }

                    @Override
                    public void onNetWorkError() {
                        mEmptyView.showError();
                    }

                    @Override
                    public void onServerError() {
                        mEmptyView.showError();
                    }
                }, false, true, new ExtendJsonUtil());
    }

    @Override
    protected void loadData() {
    }


    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
    }

    @Override
    protected void saveData(Bundle outState) {
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        if (totalPage > 0 && (currentPage <= totalPage))
            load();
        else {
            noMoreData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);

    }
}
