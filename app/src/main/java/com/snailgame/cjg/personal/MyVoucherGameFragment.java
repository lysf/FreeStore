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
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.MyVoucherAdapter;
import com.snailgame.cjg.personal.model.MyVoucherModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

/**
 * 个人中心 -> 我的代金券 游戏fragment
 * Created by pancl on 2015/4/30.
 */
public class MyVoucherGameFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, CustomLoadingView.RetryLoadListener {
    static String TAG = MyVoucherGameFragment.class.getName();
    private LoadMoreListView loadMoreListView;
    private View headView;
    private TextView headTextView;

    private MyVoucherAdapter myVoucherAdapter;
    private EmptyView mEmptyView;
    private long totalPage = -1;
    private long currentPage = 1;
    private int mType = MYVOUCHER_TYPE_TUTU;

    public static final int MYVOUCHER_TYPE_TUTU = 0;        // 兔兔券
    public static final int MYVOUCHER_TYPE_YOUXI = 2;       // 游戏代金券


    public static MyVoucherGameFragment getInstance(int type) {
        MyVoucherGameFragment fragment = new MyVoucherGameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_MYVOUCHER_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_voucher_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null)
            mType = bundle.getInt(AppConstants.KEY_MYVOUCHER_TYPE, MYVOUCHER_TYPE_TUTU);

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
     * 获取 我的代金券 数据
     */
    private void load() {
        mEmptyView.showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_QUERY_USR_VOUCHER + "?number=10&currentPage=" + currentPage + "&cType=" + mType;
        FSRequestHelper.newGetRequest(url, TAG,
                MyVoucherModel.class, new IFDResponse<MyVoucherModel>() {
                    @Override
                    public void onSuccess(MyVoucherModel model) {
                        resetRefreshUi();
                        if (model == null || ListUtils.isEmpty(model.getItemList())){
                            mEmptyView.setEmptyMessage(getString(R.string.my_voucher_no_data));
                            mEmptyView.showEmpty();
                            return;
                        }

                        if (model.getPageInfo() != null) {
                            totalPage = model.getPageInfo().getTotalPageCount();
                        }
                        if (myVoucherAdapter == null) {
                            myVoucherAdapter = new MyVoucherAdapter(getActivity(), model.getItemList(), mType == MYVOUCHER_TYPE_YOUXI);
                            loadMoreListView.setAdapter(myVoucherAdapter);
                        } else {
                            myVoucherAdapter.addNewData(model.getItemList());
                            myVoucherAdapter.notifyDataSetChanged();
                        }
                        showHeaderView(TextUtils.isEmpty(model.getVal()) ? 0 : Integer.parseInt(model.getVal()));
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

