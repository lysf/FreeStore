package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.MyVoucherGoodsAdapter;
import com.snailgame.cjg.personal.model.MyVoucherGoodsModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;


/**
 * 个人中心 -> 我的代金券 商品fragment
 * Created by pancl on 2015/4/28.
 */
public class MyVoucherGoodsFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, CustomLoadingView.RetryLoadListener {
    static final String TAG = MyVoucherGoodsFragment.class.getName();
    private LoadMoreListView loadMoreListView;
    private View headView;
    private TextView headTextView;

    private MyVoucherGoodsAdapter myVoucherGoodsAdapter;
    private EmptyView mEmptyView;
    private long totalPage = -1;
    private long currentPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.voucher_goods_fragment;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        this.headView = getActivity().getLayoutInflater().inflate(R.layout.my_voucher_header, null);
        this.headTextView = (TextView) headView.findViewById(R.id.headerTitle);

        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.voucher_goods_list);
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
     * 获取 商品代金券列表 数据
     */
    private void load() {
        mEmptyView.showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_KU_WAN_VOUCHER + "&eachNum=10&curpage=" + currentPage;
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            url += "&nUserId=" + IdentityHelper.getUid(FreeStoreApp.getContext()) + "&cIdentity=" + IdentityHelper.getIdentity(FreeStoreApp.getContext());
        }

        FSRequestHelper.newGetRequest(url, TAG,
                MyVoucherGoodsModel.class, new IFDResponse<MyVoucherGoodsModel>() {
                    @Override
                    public void onSuccess(MyVoucherGoodsModel model) {
                        resetRefreshUi();
                        if (model == null || ListUtils.isEmpty(model.getItemList())) {
                            mEmptyView.setEmptyMessage(getString(R.string.my_voucher_no_data));
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getPageInfo() != null) {
                            totalPage = model.getPageInfo().getTotalPageCount();
                        }
                        if (myVoucherGoodsAdapter == null) {
                            myVoucherGoodsAdapter = new MyVoucherGoodsAdapter(getActivity(), model.getItemList());
                            loadMoreListView.setAdapter(myVoucherGoodsAdapter);
                        } else {
                            myVoucherGoodsAdapter.addNewData(model.getItemList());
                            myVoucherGoodsAdapter.notifyDataSetChanged();
                        }
                        showHeaderView(model.getiExpireNum());
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
                }, false);
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
