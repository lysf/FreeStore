package com.snailgame.cjg.personal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import third.scrolltab.ScrollTabHolderFragment;

/**
 * Created by TAJ_C on 2015/4/27.
 */
public abstract class BaseHistoryFragment extends ScrollTabHolderFragment implements LoadMoreListView.OnLoadMoreListener {

    LoadMoreListView loadMoreListView;

    int headerHight;
    public final static int HISTORY_TYPE_GETED = 1; // 充值记录
    public final static int HISTORY_TYPE_USED = 2; // 使用记录
    public final static String VALUE_HISTORY_TYPE = "value_history_type";
    public static final String KEY_HISTORY_LIST = "key_history_list";
    protected abstract void fetchData();


    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gap_container;
    }

    @Override
    protected void initView() {
        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.content);
        loadMoreListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        addHeaderView();
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.setScrollHolder(mScrollTabHolder);
        loadMoreListView.setDivider(null);
    }

    private void addHeaderView() {
        FrameLayout view = new FrameLayout(getActivity());

        view.setBackgroundColor(ResUtil.getColor(R.color.common_window_bg));
        headerHight = getResources().getDimensionPixelSize(R.dimen.personal_score_history_header_height) + ComUtil.dpToPx(8);
        view.setPadding(0, headerHight, 0, 0);
        loadMoreListView.addHeaderView(view);
    }


    @Override
    protected void loadData() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop(ComUtil.dpToPx(120));
            showLoading();
        }

        fetchData();
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
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && loadMoreListView != null && loadMoreListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        if (loadMoreListView != null) {
            loadMoreListView.setSelectionFromTop(1, scrollHeight);
        }
    }

    public void moveEmptyView(float transY) {
        if (getEmptyView() != null) {
            moveView(getEmptyView().getEmptyView(), transY);
            moveView(getEmptyView().getErrorView(), transY);
            moveView(getEmptyView().getLoadingView(), transY);
        }
    }

    private void moveView(ViewGroup view, float transY) {
        if (view != null) {
            ViewHelper.setTranslationY(view, transY);
        }
    }


    @Override
    public void onLoadMore() {
        fetchData();
    }

}
