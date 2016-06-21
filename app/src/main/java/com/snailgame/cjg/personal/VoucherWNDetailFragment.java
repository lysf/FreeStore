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
import com.snailgame.cjg.personal.adapter.VoucherWNDetailAdapter;
import com.snailgame.cjg.personal.model.VoucherWNDetailModel;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

/**
 * 个人中心 -> 我的代金券 -> 蜗牛代金券明细 fragment
 * Created by pancl on 2015/6/24.
 */
public class VoucherWNDetailFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, CustomLoadingView.RetryLoadListener {
    static final String TAG = VoucherWNDetailFragment.class.getName();
    private int voucherId;

    private LoadMoreListView loadMoreListView;
    private VoucherWNDetailAdapter voucherWNDetailAdapter;
    private EmptyView mEmptyView;

    private long totalPage = -1;
    private long currentPage = 1;

    /**
     * @param voucherId 代金券项id
     * @return
     */
    public static VoucherWNDetailFragment create(int voucherId) {
        VoucherWNDetailFragment fragment = new VoucherWNDetailFragment();
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
     * 获取 蜗牛代金券详情 数据
     */
    @Override
    protected void loadData() {
        mEmptyView.showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_QUERY_VOUCHER_WN_RECORD + "?iVoucherId=" + voucherId + "&number=10&currentPage=" + currentPage;
        FSRequestHelper.newGetRequest(url, TAG,
                VoucherWNDetailModel.class, new IFDResponse<VoucherWNDetailModel>() {
                    @Override
                    public void onSuccess(VoucherWNDetailModel model) {
                        resetRefreshUi();
                        if (model == null || ListUtils.isEmpty(model.getItemList())) {
                            mEmptyView.setEmptyMessage(getString(R.string.voucher_detail_records));
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getPageInfo() != null) {
                            totalPage = model.getPageInfo().getTotalPageCount();
                        }
                        if (voucherWNDetailAdapter == null) {
                            voucherWNDetailAdapter = new VoucherWNDetailAdapter(model.getItemList());
                            loadMoreListView.setAdapter(voucherWNDetailAdapter);
                        } else {
                            voucherWNDetailAdapter.addNewData(model.getItemList());
                            voucherWNDetailAdapter.notifyDataSetChanged();
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

