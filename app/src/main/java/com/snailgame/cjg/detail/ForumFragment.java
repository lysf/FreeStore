package com.snailgame.cjg.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.adapter.DetailFragmentAdapter;
import com.snailgame.cjg.detail.adapter.ForumAdapter;
import com.snailgame.cjg.detail.model.ForumModel;
import com.snailgame.cjg.detail.model.ScrollYEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import third.scrolltab.ScrollTabHolderFragment;

/**
 * 游戏详情 论坛标签页
 * Created by taj on 2014/11/20.
 */
public class ForumFragment extends ScrollTabHolderFragment implements LoadMoreListView.OnLoadMoreListener, AdapterView.OnItemClickListener, View.OnClickListener {
    static String TAG = ForumFragment.class.getName();

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.tv_post)
    TextView mPostView;
    private ForumAdapter mAdapter;
    private List<ForumModel.ModelItem> mForumList = new ArrayList<ForumModel.ModelItem>();

    private int forumId;
    private int mCurPage = 1;
    private int mTotalPage;
    private AbsListView.LayoutParams params;

    private int mHeaderHeight;
    private static final String HEIGHT_HEIGHT = "key_height";
    private static final String KEY_FORUM_ID = "key_forum_id";
    private boolean isEmpty = false;
    private int scrollHeight;

    private int headerHeight;
    private int currentAdjustScroll;

    public static ForumFragment getInstance(int mHeaderHeight, int forumId) {
        ForumFragment fragment = new ForumFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(HEIGHT_HEIGHT, mHeaderHeight);
        bundle.putInt(KEY_FORUM_ID, forumId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isLoadinUserVisibile = false;
        headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.detail_header_translate_height);//header可以滑动的最大高度
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.forum_layout;
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mHeaderHeight = bundle.getInt(HEIGHT_HEIGHT);
            forumId = bundle.getInt(KEY_FORUM_ID);
        }
    }

    @Override
    protected void initView() {
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setScrollHolder(mScrollTabHolder);
        loadMoreListView.setPagePosition(DetailFragmentAdapter.FRAGMENT_BBS);

        View placeHolder = new View(getActivity());
        placeHolder.setBackgroundColor(getResources().getColor(R.color.common_window_bg));
        params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight);
        placeHolder.setLayoutParams(params);
        loadMoreListView.addHeaderView(placeHolder);
        mPostView.setOnClickListener(this);
        mAdapter = new ForumAdapter(getActivity(), mForumList);
        loadMoreListView.setAdapter(mAdapter);
        loadMoreListView.setOnItemClickListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void loadData() {
        customAndShowLoading();
        fetchData();
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

    private void customAndShowLoading() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop((mHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.dimen_60dp)) / 2);
            showLoading();
        }
    }

    private void fetchData() {
        String url = JsonUrl.getJsonUrl().JSON_URL_FORM + "&fid=" + forumId + "&page=" + mCurPage;

        FSRequestHelper.newGetRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String msg) {
                ForumModel result = ForumJsonUtil.parseJsonToFormModel(msg);
                if (result == null || ListUtils.isEmpty(result.getItemList())) {
                    isEmpty = true;
                    showEmpty();
                    loadMoreListView.enableEmptyViewScrollable();
                    scrollToPositionWhenEmtpy(scrollHeight);
                    return;
                }

                mTotalPage = result.getPageInfo().getTotalPageCount();
                mForumList.addAll(result.getItemList());
                showView(mForumList);
                scrollToPosition(scrollHeight);
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
        }, false);
    }

    private void showView(List<ForumModel.ModelItem> itemList) {
        mAdapter.refreshDate(itemList);
        resetRefreshUi();
        if (itemList.size() >= 5) {
            loadMoreListView.resetEmptyFooterHeight();
        } else {
            //填充空白区域
            loadMoreListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight() - itemList.size() * ComUtil.dpToPx(100) - ComUtil.dpToPx(115) - ComUtil.dpToPx(35) - ComUtil.dpToPx(56));

            loadMoreListView.onNoMoreData();
        }
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
        if (mCurPage > mTotalPage) {
            loadMoreListView.onNoMoreData();
            return;
        }

        fetchData();
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (mAdapter != null && mAdapter.getCount() > 0)
            scrollToPosition(scrollHeight);
        else {
            scrollToPositionWhenEmtpy(scrollHeight);
        }

        this.scrollHeight = scrollHeight;
    }

    private void scrollToPosition(int scrollHeight) {
        if ((scrollHeight == 0 && loadMoreListView.getFirstVisiblePosition() >= 1)
                || (topScroll == -headerHeight && currentAdjustScroll == scrollHeight && scrollHeight == mHeaderHeight - headerHeight)) {
            return;
        }
        loadMoreListView.setSelectionFromTop(1, scrollHeight - ResUtil.getDimensionPixelSize(R.dimen.divider_height));
        currentAdjustScroll = scrollHeight;
    }

    private void scrollToPositionWhenEmtpy(int scrollHeight) {
        if (scrollHeight == 0) {
            return;
        }
        loadMoreListView.setSelectionFromTop(1, scrollHeight - ResUtil.getDimensionPixelSize(R.dimen.divider_height));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int clickPostion = position - 1; //减去 Header 站区的1
        if (clickPostion >= 0 && mAdapter.getCount() > 0) {
            ForumModel.ModelItem item = mAdapter.getItem(clickPostion);
            String url = JsonUrl.getJsonUrl().JSON_URL_FORUM_ITEM + "&tid=" + item.getTid();
            Intent intent = WebViewActivity.newIntent(getActivity(), url);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_post:
                String url = JsonUrl.getJsonUrl().JSON_URL_FORUM_POST + "&fid=" + forumId + "&cli=1";
                startActivity(WebViewActivity.newIntent(getActivity(), url));
                break;
            default:
                break;
        }
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
