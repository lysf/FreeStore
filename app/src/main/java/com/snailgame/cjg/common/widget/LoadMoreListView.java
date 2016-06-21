package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.PhoneUtil;

import third.scrolltab.ScrollTabHolder;

/**
 * 可分页加载的ListView
 */
public class LoadMoreListView extends ListView implements OnScrollListener {
    public final static int FOOTER_RESET = 0, HEADER_RESET = 1;
    protected int resetType = FOOTER_RESET;
    public static final int SCROLL_DURATION = 400;
    public final static float OFFSET_RADIO = 1.8f;
    private int mTouchSlop;

    private OnLoadMoreListener listener;

    private boolean isLoading = false;
    private boolean noMore = false;

    private ImageView loadMoreProgress;
    private boolean enableLoadingMore = false;

    private View footView, emptyFooterView, emptyViewContent;//emptyFooterView 用来填充空白空间
    protected Scroller mScroller;
    private int mTotalItemCount;
    private View mContentView;

    private ScrollTabHolder mScrollTabHolder;

    private boolean isScrollHolder = false;
    private int pagePosition;
    private boolean updateFooterHeight = false;
    private float mLastY = -1;

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        emptyFooterView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.load_more_empty_footer, null);
        emptyViewContent = emptyFooterView.findViewById(R.id.empty_footer_content);
        footView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.load_more_footer, null);
        mContentView = footView.findViewById(R.id.contentView);
        loadMoreProgress = (ImageView) footView.findViewById(R.id.loadMoreProgress);
        setOnScrollListener(this);
        mScroller = new Scroller(context, new DecelerateInterpolator());
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
        if (isScrollHolder && mScrollTabHolder != null) {
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, pagePosition);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (enableLoadingMore) {
            if (view.getLastVisiblePosition() == view.getCount() - 1 && !isLoading && !noMore && listener != null) {
                showLoadingMoreProgress();
                isLoading = true;
                listener.onLoadMore();
            }
        }
    }

    /**
     * 显示底部Footer
     */
    private void showLoadingMoreProgress() {
        loadMoreProgress.setVisibility(VISIBLE);
    }

    /**
     * 隐藏底部Footer
     */
    private void hideLoadingMoreProgress() {
        loadMoreProgress.setVisibility(GONE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //只有没有更多数据才开启回弹效果
        if (mLastY == -1)
            mLastY = ev.getRawY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getRawY();
                float deltaY = mLastY - currentY;
                if (noMore && getLastVisiblePosition() == mTotalItemCount - 1
                        && (getBottomMargin() > 0 || deltaY > 0)) {
                    updateFooterHeight(deltaY);
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastY = -1;
                if (noMore && updateFooterHeight)
                    resetFooterHeight();
                break;
        }

        return super.onTouchEvent(ev);
    }


    private void updateFooterHeight(float delta) {
        updateFooterHeight = true;
        int height = getBottomMargin() + (int) (delta / OFFSET_RADIO);
        setBottomMargin(height);
    }

    private void setBottomMargin(int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    private void resetFooterHeight() {
        updateFooterHeight = false;
        int bottomMargin = getBottomMargin();
        int footerHeight = footView.getHeight();
        if (footerHeight > 0) {
            resetType = FOOTER_RESET;
            mScroller.startScroll(0, bottomMargin, 0, -footerHeight, SCROLL_DURATION);
        }
    }

    /**
     * 隐藏footer
     */
    private void hideFooter() {
        setBottomMargin(-ComUtil.dpToPx(70));
    }

    /**
     * 隐藏footer
     */
    public void goneFooter() {
        footView.setVisibility(GONE);
    }

    @Override
    public void computeScroll() {
        if (resetType == FOOTER_RESET) {
            if (mScroller.computeScrollOffset()) {
                //底部松手回弹效果
                setBottomMargin(mScroller.getCurrY());
                postInvalidate();
            }
        }
        super.computeScroll();
    }

    /**
     * 默认不开启加载更多功能
     *
     * @param enableLoadingMore
     */
    public void enableLoadingMore(boolean enableLoadingMore) {
        this.enableLoadingMore = enableLoadingMore;
        if (enableLoadingMore) {
            addFooterView(emptyFooterView);
            addFooterView(footView);
        }
    }


    /**
     * 加载更多完成
     */
    public void onLoadMoreComplete() {
        isLoading = false;
        noMore = false;
        hideLoadingMoreProgress();
    }

    /**
     * 全部加载数据完成
     */
    public void onNoMoreData() {
        hideFooter();
        isLoading = false;
        noMore = true;
        showLoadingMoreProgress();
        loadMoreProgress.setImageResource(R.drawable.load_end);

    }

    /**
     * 全部加载完 不需要兔子
     */
    public void onNoMoreDataWithoutEnd() {
        hideFooter();
        isLoading = false;
        noMore = true;
        showLoadingMoreProgress();
        hideLoadingMoreProgress();
    }

    /**
     * 用于ScrollTabHolderFragment
     *
     * @param mScrollTabHolder
     */
    public void setScrollHolder(ScrollTabHolder mScrollTabHolder) {
        isScrollHolder = true;
        this.mScrollTabHolder = mScrollTabHolder;
    }

    public int getComputedScrollY() {
        return (getFirstVisiblePosition() == 0) && getChildAt(0) != null ? -getChildAt(0).getTop() : Integer.MAX_VALUE;
    }

    public boolean getIsNoMore() {
        return noMore;
    }


    public void changeEmptyFooterHeight(int height) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = height;
        emptyViewContent.setLayoutParams(layoutParams);
    }

    public void resetEmptyFooterHeight() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 0;
        emptyViewContent.setLayoutParams(layoutParams);
    }

    /**
     * 允许tab页面的EmptyView可以滚动
     */
    public void enableEmptyViewScrollable() {
        inflateEmptyView(0);
        //使ListView 可见
        setVisibility(VISIBLE);
    }

    /**
     * 填充空白
     */
    public void inflateEmptyView(int height) {
        if (height <= 0) {
            //默认填充高度
            Resources resources = getResources();
            int emptyFooterHeight = (int) PhoneUtil.getScreenHeight() - getTotalListItemHeight()
                    - resources.getDimensionPixelSize(R.dimen.tab_height) - resources.getDimensionPixelSize(R.dimen.actionbar_height);
            if (emptyFooterHeight > 0) {
                changeEmptyFooterHeight(emptyFooterHeight);
            } else {
                changeEmptyFooterHeight(0);
            }
        } else {
            changeEmptyFooterHeight(height);
        }
    }

    public int getTotalListItemHeight() {
        ListAdapter listAdapter = getAdapter();
        int totalItemsHeight = 0;
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount() - 2;
            if (numberOfItems <= 1)
                return 0;
            ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            for (int itemPos = 1; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, this);
                item.setLayoutParams(layoutParams);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            int totalDividersHeight = getDividerHeight() * (numberOfItems - 1);
            totalItemsHeight = totalItemsHeight + totalDividersHeight;
        }
        return totalItemsHeight;
    }

    //    refresh load more interface

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadingListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

}
