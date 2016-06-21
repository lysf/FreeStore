package com.snailgame.cjg.personal;

import android.os.Bundle;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.VoucherGameDetailAdapter;
import com.snailgame.cjg.personal.model.VoucherGameDetailModel;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;


/**
 * 个人中心 -> 我的代金券 -> 游戏代金券明细 fragment
 * Created by pancl on 2015/4/20.
 */
public class VoucherGameDetailFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, CustomLoadingView.RetryLoadListener {

    static final String TAG = VoucherGameDetailFragment.class.getName();
    private int voucherId;

    private LoadMoreListView loadMoreListView;
    private VoucherGameDetailAdapter voucherGameDetailAdapter;
    private EmptyView mEmptyView;

    private long totalPage = -1;
    private long currentPage = 1;

    /**
     * @param voucherId 代金券项id
     * @return
     */
    public static VoucherGameDetailFragment create(int voucherId) {
        VoucherGameDetailFragment fragment = new VoucherGameDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_VOUCHER_ID, voucherId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.voucher_detail_fragment;
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            voucherId = bundle.getInt(AppConstants.KEY_VOUCHER_ID);
        }
    }

    @Override
    protected void initView() {
        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.voucher_cooper_list);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);

        mEmptyView = new EmptyView(mParentActivity, loadMoreListView);
        mEmptyView.setErrorButtonClickListener(mErrorClickListener);
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            loadData();
        }
    }

    /**
     * 获取 游戏代金券详情 数据
     */
    @Override
    protected void loadData() {
        mEmptyView.showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_QUERY_VOUCHER_RECORD + "?iVoucherId=" + voucherId + "&number=10&currentPage=" + currentPage;
        FSRequestHelper.newGetRequest(url, TAG,
                VoucherGameDetailModel.class, new IFDResponse<VoucherGameDetailModel>() {
                    @Override
                    public void onSuccess(VoucherGameDetailModel model) {
                        resetRefreshUi();
                        if (model == null || model.getItem() == null || model.getItem().getRecords() == null
                                || ListUtils.isEmpty(model.getItem().getRecords().getItemList())) {
                            mEmptyView.setEmptyMessage(getString(R.string.voucher_detail_records));
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getItem().getRecords().getPageInfo() != null) {
                            totalPage = model.getItem().getRecords().getPageInfo().getTotalPageCount();
                        }
                        if (voucherGameDetailAdapter == null) {
                            voucherGameDetailAdapter = new VoucherGameDetailAdapter(model.getItem().getRecords().getItemList());
                            loadMoreListView.setAdapter(voucherGameDetailAdapter);
                        } else {
                            voucherGameDetailAdapter.addNewData(model.getItem().getRecords().getItemList());
                            voucherGameDetailAdapter.notifyDataSetChanged();
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
                }, false);
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
            loadData();
        else {
            noMoreData();
        }
    }

    @Override
    public void retryLoad() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}

