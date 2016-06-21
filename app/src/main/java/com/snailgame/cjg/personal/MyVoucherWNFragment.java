package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.MyVoucherAdapter;
import com.snailgame.cjg.personal.model.MyVoucherWNModel;
import com.snailgame.cjg.personal.model.VoucherWNModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;

/**
 * 个人中心 -> 蜗牛代金券 游戏fragment
 * Created by pancl on 2015/4/30.
 */
public class MyVoucherWNFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, CustomLoadingView.RetryLoadListener {
    static final String TAG = MyVoucherWNFragment.class.getName();
    private LoadMoreListView loadMoreListView;
    private View headView;
    private TextView headTextView;

    private MyVoucherAdapter myVoucherAdapter;
    private EmptyView mEmptyView;
    private long totalPage = -1;
    private long currentPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.my_voucher_fragment;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        this.headView = getActivity().getLayoutInflater().inflate(R.layout.my_voucher_header, null);
        this.headTextView = (TextView) headView.findViewById(R.id.headerTitle);

        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.my_voucher_list);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.addHeaderView(headView, null, true);
        loadMoreListView.setDivider(getResources().getDrawable(R.drawable.transparent));
        loadMoreListView.setDividerHeight(ComUtil.dpToPx(6));
        mEmptyView = new EmptyView(mParentActivity, loadMoreListView);
        mEmptyView.setErrorButtonClickListener(mErrorClickListener);
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            load();
        }
    }

    /**
     * 获取 蜗牛代金券 数据
     */
    private void load() {
        mEmptyView.showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_QUERY_USR_WN_VOUCHER + "?number=10&currentPage=" + currentPage;

        FSRequestHelper.newGetRequest(url, TAG,
                MyVoucherWNModel.class, new IFDResponse<MyVoucherWNModel>() {
                    @Override
                    public void onSuccess(MyVoucherWNModel model) {
                        resetRefreshUi();
                        if (model == null || model.getItem() == null
                                || ListUtils.isEmpty(model.getItem().getItemList())) {
                            mEmptyView.setEmptyMessage(getString(R.string.my_voucher_no_data));
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getItem().getPageInfo() != null) {
                            totalPage = model.getItem().getPageInfo().getTotalPageCount();
                        }
                        if (myVoucherAdapter == null) {
                            ArrayList<VoucherWNModel.ModelItem> gameList = model.getItem().getGameList() == null
                                    ? new ArrayList<VoucherWNModel.ModelItem>() : model.getItem().getGameList();
                            myVoucherAdapter = new MyVoucherAdapter(getActivity(), model.getItem().getItemList(), gameList);
                            loadMoreListView.setAdapter(myVoucherAdapter);
                        } else {
                            myVoucherAdapter.addNewData(model.getItem().getItemList());
                            myVoucherAdapter.notifyDataSetChanged();
                        }
                        showHeaderView(TextUtils.isEmpty(model.getItem().getVal()) ? 0 : Integer.parseInt(model.getItem().getVal()));
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
    public void retryLoad() {
        load();
    }

    /**
     * 您有X张代金券再不用就过期了
     *
     * @param overtimeItemsCount
     */
    private void showHeaderView(int overtimeItemsCount) {
        if (overtimeItemsCount > 0) {
            headTextView.setText(String.format(getString(R.string.my_voucher_overtime), String.valueOf(overtimeItemsCount)));
        } else {
            loadMoreListView.removeHeaderView(headView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}

