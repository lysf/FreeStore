package com.snailgame.cjg.detail;


import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.CommentDialog;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.adapter.CommentAdapter;
import com.snailgame.cjg.detail.adapter.DetailFragmentAdapter;
import com.snailgame.cjg.detail.model.CommentListModel;
import com.snailgame.cjg.detail.model.ScrollYEvent;
import com.snailgame.cjg.event.CommentChangedEvent;
import com.snailgame.cjg.event.RateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import third.scrolltab.ScrollTabHolderFragment;


/**
 * 游戏详情 评论标签页
 * Created by taj on 2014/11/12.
 */
public class CommentListFragment extends ScrollTabHolderFragment implements LoadMoreListView.OnLoadMoreListener, View.OnClickListener {
    static String TAG = CommentListFragment.class.getName();

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.btn_comment)
    View mBtnComment;   //评论按钮
    TextView mCommentTitleView;
    private CommentAdapter mCommentAdapter;
    private List<CommentListModel.ModelItem> mCommentList = new ArrayList<CommentListModel.ModelItem>();
    private CommentListModel mModel;
    private int mCurPage = 1;
    private int mTotalPage;
    private int appId;
    private int commentNum;
    private float commentLevel;

    private int mHeaderHeight;
    private static final String KEY_HEADER_HEIGHT = "header_height";
    private int scrollHeight;
    private View headerView;
    private AbsListView.LayoutParams params;
    private View lineView;
    private boolean isEmpty = false;

    private int headerHeight;
    private int currentAdjustScroll;

    @Subscribe
    public void onCommentChanged(CommentChangedEvent event) {
        mCurPage = 1;
        mCommentList = new ArrayList<CommentListModel.ModelItem>();
        loadData();
    }

    public static CommentListFragment getInstance(int appId, int mHeaderHeight) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_APP_ID, appId);
        bundle.putInt(KEY_HEADER_HEIGHT, mHeaderHeight);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comment_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isLoadinUserVisibile = false;
        headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.detail_header_translate_height);//header可以滑动的最大高度
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            appId = bundle.getInt(AppConstants.KEY_APP_ID);
            mHeaderHeight = bundle.getInt(KEY_HEADER_HEIGHT);
        }
    }

    @Override
    protected void initView() {
        headerView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.comment_item_header, loadMoreListView, false);
        View space = headerView.findViewById(R.id.comment_space);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight);
        space.setLayoutParams(layoutParams);
        mCommentTitleView = (TextView) headerView.findViewById(R.id.tv_comment_title);

        lineView = headerView.findViewById(R.id.comment_line);
        mBtnComment.setOnClickListener(this);
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setScrollHolder(mScrollTabHolder);
        loadMoreListView.addHeaderView(headerView);
        mCommentAdapter = new CommentAdapter(getActivity(), mCommentList);
        loadMoreListView.setAdapter(mCommentAdapter);
        loadMoreListView.setPagePosition(DetailFragmentAdapter.FRAGMENT_COMMENT);
    }

    @Override
    protected void loadData() {
        customAndShowLoading();
        fetchData();
    }

    private void customAndShowLoading() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop((mHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.dimen_60dp)) / 2);
            showLoading();
        }
    }

    private void showNothing() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop((mHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.dimen_60dp)) / 2);
            getEmptyView().showNothing();
            loadMoreListView.enableEmptyViewScrollable();
            scrollToPositionWhenEmtpy(scrollHeight);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showNothing();
    }

    private void fetchData() {
        getCommentList();
    }


    private void getCommentList() {
        String url = JsonUrl.getJsonUrl().JSON_URL_COMMENT_LIST +
                "?iAppId=" + appId + "&currentPage=" + mCurPage + "&iPlatformId=" + AppConstants.PLATFORM_ID;
        FSRequestHelper.newGetRequest(url, TAG, CommentListModel.class, new IFDResponse<CommentListModel>() {
            @Override
            public void onSuccess(CommentListModel result) {
                if (result == null || result.getItemList() == null
                        || result.getItemList().isEmpty() || result.getCode() != 0) {
                    loadMoreListView.onNoMoreData();
                    showEmptyComment();
                    scrollToPositionWhenEmtpy(scrollHeight);
                    return;
                }

                if (!TextUtils.isEmpty(result.getVal())) {
                    try {
                        commentNum = (JSON.parseObject(result.getVal())).getIntValue("iCommentTimes");
                        commentLevel = (float) ((JSON.parseObject(result.getVal())).getDoubleValue("nScore"));

                        MainThreadBus.getInstance().post(new RateChangeEvent(commentLevel));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mTotalPage = result.getPageInfo().getTotalPageCount();

                if (mCurPage >= mTotalPage) {
                    loadMoreListView.onNoMoreData();
                }

                if (isAdded()) {
                    mCommentTitleView.setVisibility(View.VISIBLE);
                    mCommentTitleView.setText(getString(R.string.comment_title_header, commentNum));
                    lineView.setVisibility(View.VISIBLE);
                }
                showView(result.getItemList());
                mCurPage++;
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, false, true, new ExtendJsonUtil());
    }

    private void showView(List<CommentListModel.ModelItem> itemList) {
        mCommentList.addAll(itemList);
        mCommentAdapter.refreshData(mCommentList);
        resetRefreshUi();
        if (mCommentList.size() >= 5) {
            loadMoreListView.resetEmptyFooterHeight();
        }
        if (mTotalPage == 1) {
            //填充空白区域  48actionbar 高度 44 tab 高度
            int emptyFooterHeight = getNeedInflateEmptyViewHeight();
            if (emptyFooterHeight > 0)
                loadMoreListView.changeEmptyFooterHeight(emptyFooterHeight);
        }
        if (mCurPage == 1) {
            headerView.setVisibility(View.VISIBLE);
            scrollToPosition(scrollHeight);
        }

    }

    /**
     * 获取需要填充的空白view高度
     */
    private int getNeedInflateEmptyViewHeight() {
        Resources resources = getResources();
        return (int) PhoneUtil.getScreenHeight() - loadMoreListView.getTotalListItemHeight()
                - resources.getDimensionPixelSize(R.dimen.tab_height) - resources.getDimensionPixelSize(R.dimen.actionbar_height);
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
    public void onDestroyView() {
        super.onDestroyView();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onLoadMore() {

        if (mCurPage > mTotalPage) {
            loadMoreListView.onNoMoreData();
            return;
        }
        fetchData();
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (mCommentAdapter != null && mCommentAdapter.getCount() > 0)
            scrollToPosition(scrollHeight);
        else {
            scrollToPositionWhenEmtpy(scrollHeight);
        }
        this.scrollHeight = scrollHeight;
    }

    private void scrollToPosition(int scrollHeight) {
        if (scrollHeight == 0 && loadMoreListView.getFirstVisiblePosition() >= 1
                || (topScroll == -headerHeight && currentAdjustScroll == scrollHeight && currentAdjustScroll == mHeaderHeight - headerHeight)) {
            return;
        }
        loadMoreListView.setSelectionFromTop(1, scrollHeight + ComUtil.dpToPx(52));
        currentAdjustScroll = scrollHeight;
    }

    private void scrollToPositionWhenEmtpy(int scrollHeight) {
        if (scrollHeight == 0) {
            return;
        }
//        loadMoreListView.setSelectionFromTop(1, scrollHeight  + ComUtil.dpToPx(53)- ResUtil.getDimensionPixelSize(R.dimen.divider_height));
        loadMoreListView.setSelectionFromTop(1, scrollHeight - ResUtil.getDimensionPixelSize(R.dimen.divider_height));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                if (LoginSDKUtil.isLogined(getActivity())) {
                    new CommentDialog(getActivity()).setData(appId, commentNum, commentLevel).show();
                } else {
                    AccountUtil.userLogin(getActivity());
                }

                break;
            default:
                break;
        }
    }

    private void showEmptyComment() {
        if (isAdded())
            getEmptyView().setEmptyMessage(getString(R.string.comment_no_data_now));
        isEmpty = true;
        showEmpty();
        loadMoreListView.enableEmptyViewScrollable();
    }

    int topScroll;

    @Subscribe
    public void scrollY(ScrollYEvent event) {
        topScroll = event.getScrollY();
        if (getUserVisibleHint()) {
            currentAdjustScroll = mHeaderHeight - Math.abs(topScroll);
        }
        EmptyView emptyView = getEmptyView();
        if (emptyView != null) {
            emptyView.setEmptyViewScroll(event.getScrollY() / 2);
        }
    }
}
