package com.snailgame.cjg.friend.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.util.ResUtil;


/**
 * Created by yanHH on 2016/5/17.
 */
public class SlidingFinishFrameLayout extends RelativeLayout {
    private final String TAG = SlidingFinishFrameLayout.class.getSimpleName();
    private Context context;
    /**
     * 屏幕高度
     */
    private int screenHeight;
    /**
     * 初始距离屏幕顶端高度
     */
    private int MARGIN_HEIGHT = ComUtil.dpToPx(180);
    private int MARGIN_PIC = ComUtil.dpToPx(36);
    /**
     * 需要超出的高度
     */
    private int TOP = MARGIN_PIC;
    /**
     * 滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * 按下点的X坐标
     */
    private int downX;
    /**
     * 按下点的Y坐标
     */
    private int downY;
    /**
     * 临时存储Y坐标
     */
    private int tempY;
    /**
     * 滑动类
     */
    private Scroller mScroller;
    /**
     * SlidingFinishFrameLayout的高度
     */
    private int viewHeight;
    /**
     * 记录是否正在滑动
     */
    private boolean isSliding;
    private View sliding_container;
    private boolean isFinish;
    private OnScrollListener onScrollListener;
    private RelativeLayout view_top;
    private RelativeLayout view_top_content;
    private LinearLayout view_bottom;
    private View detail_actionbar_view;
    private View space;
    private TextView tv_friend_name;
    private TextView tv_detail_title;
    private float disX, disY;

    private int topHeight;//初始top的高度
    private int emptyBottomHeight;//初始时ListView高度为0的bottom的高度
    private int ACTIONBAR_HEIGHT;
    private View emptyView;
    private int emptyViewStartY = 0;//EmptyView的初始位置
    private boolean isFirst = true;//是否第一次滑动
    private int statesBarHeight;
    private int appAreaHeiht;//应用区域高度

    public SlidingFinishFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public SlidingFinishFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public SlidingFinishFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        this.context = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        statesBarHeight = ComUtil.getStatesBarHeight();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        sliding_container = findViewById(R.id.sliding_container);
        view_bottom = (LinearLayout) findViewById(R.id.view_bottom);
        view_top = (RelativeLayout) findViewById(R.id.view_top);
        view_top_content = (RelativeLayout) findViewById(R.id.view_top_content);
        siv_friend_photo = findViewById(R.id.siv_friend_photo);
        space = findViewById(R.id.space);
        tv_friend_name = (TextView) findViewById(R.id.tv_friend_name);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        detail_actionbar_view = findViewById(R.id.detail_actionbar_view);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && isFirst) {
            ACTIONBAR_HEIGHT = detail_actionbar_view.getHeight();
            disY = (space.getBottom() - ACTIONBAR_HEIGHT / 2) - (tv_friend_name.getBottom() - tv_friend_name.getHeight() / 2);
            screenHeight = (int) PhoneUtil.getScreenHeight();
            appAreaHeiht = PhoneUtil.getAppAreaHeight((Activity) context);
            MARGIN_HEIGHT = screenHeight / 3;
            emptyBottomHeight = view_bottom.getHeight();
            viewHeight = sliding_container.getHeight();
            topHeight = viewHeight - emptyBottomHeight;
            TOP = view_top.getHeight() - ACTIONBAR_HEIGHT - statesBarHeight;
            emptyViewStartY = screenHeight - topHeight - MARGIN_HEIGHT;
            sliding_container.scrollTo(0, -screenHeight);
        }
    }

    public int getEmptyViewStartY() {
        return emptyViewStartY;
    }

    /**
     * 计算ListView高度，重新设置MARGIN_HEIGHT
     *
     * @param listViewHeight ListView高度
     */
    public void setMarginHeight(int listViewHeight, View emptyView) {
        this.emptyView = emptyView;
        if (topHeight + emptyBottomHeight + listViewHeight < screenHeight + TOP) {//小于屏幕高度 + TOP，填充高度（大于屏幕一定高度，ActionBar才会显示）
            LinearLayout.LayoutParams emptyParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight + TOP - topHeight);
            view_bottom.setLayoutParams(emptyParam);
            viewHeight = screenHeight + TOP;
        } else {
            viewHeight = topHeight + emptyBottomHeight + listViewHeight;
            LinearLayout.LayoutParams emptyParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight - topHeight);
            view_bottom.setLayoutParams(emptyParam);
        }
        if (isFirst) {
            mScroller.startScroll(0, -screenHeight, 0, screenHeight - MARGIN_HEIGHT, screenHeight - MARGIN_HEIGHT);
            isFirst = false;
        }
        postInvalidate();
    }

    /**
     * 滚动出界面
     */
    public void scrollDown() {
        isFinish = true;
        final int delta = (viewHeight + sliding_container.getScrollY());
        mScroller.startScroll(0, sliding_container.getScrollY(), 0, -delta,
                Math.abs(delta));
        postInvalidate();
    }

    /**
     * 滚动到起始位置
     */
    private void scrollOrigin() {
        int delta = sliding_container.getScrollY() + MARGIN_HEIGHT;
        mScroller.startScroll(0, sliding_container.getScrollY(), 0, -delta,
                Math.abs(delta));
        postInvalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = tempY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) event.getRawY();
                int deltaY = tempY - moveY;
                tempY = moveY;
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    isSliding = true;
                }
                if (sliding_container.getScrollY() + screenHeight <= viewHeight && isSliding) {
                    int scrollY = sliding_container.getScrollY() + screenHeight + deltaY <= viewHeight ? deltaY : viewHeight - (sliding_container.getScrollY() + screenHeight);
                    sliding_container.scrollBy(0, scrollY);
                    setAlpha(sliding_container.getScrollY());
                    setActionBarAlpha(sliding_container.getScrollY());
                    scrollEmptyView();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                downY = (int) event.getY();
                if (isSliding) {
                    if (TOP - sliding_container.getScrollY() < ACTIONBAR_HEIGHT * 3 && TOP - sliding_container.getScrollY() > 0) {
                        setTopScroll(sliding_container.getScrollY());
                        return true;
                    } else {
                        if (sliding_container.getScrollY() > -MARGIN_HEIGHT && sliding_container.getScrollY() < 0) {
                            scrollOrigin();
                            isFinish = false;
                            return true;
                        }
                    }
                    isSliding = false;
                    if (sliding_container.getScrollY() < -MARGIN_HEIGHT) {
                        if (Math.abs(sliding_container.getScrollY()) >= screenHeight / 2) {
                            isFinish = true;
                            scrollDown();
                        } else {
                            scrollOrigin();
                            isFinish = false;
                        }
                    }
                    return true;
                }
                if (sliding_container.getScrollY() == -MARGIN_HEIGHT && downY < MARGIN_HEIGHT) {
                    scrollDown();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 滑动到ActionBar出现
     *
     * @param scrollY
     */
    private void setTopScroll(int scrollY) {
        int delta = TOP - scrollY;
        mScroller.startScroll(0, scrollY, 0, delta, Math.abs(delta));
        postInvalidate();
    }

    /**
     * 动态设置背景透明度度
     *
     * @param scrollY
     */
    private void setAlpha(int scrollY) {
        setTopAlpha(scrollY);
        if (Math.abs(scrollY) < MARGIN_HEIGHT) {
            scrollY = MARGIN_HEIGHT;
        }
        int ap = 255 * (viewHeight - Math.abs(scrollY)) / viewHeight;
        String alpha = "#" + Integer.toHexString(ap < 20 ? 20 : ap);
        this.setBackgroundColor(Color.parseColor(alpha + "000000"));
    }

    private void setActionBarAlpha(int scrollY) {
        int alp = 255 * (screenHeight + scrollY) / (screenHeight + TOP);
        scroll(alp > 255 ? 255 : alp);
    }

    /**
     * 设置头部透明度
     *
     * @param scrollY
     */

    View siv_friend_photo;

    private void setTopAlpha(int scrollY) {
        if (scrollY >= TOP && isSliding) {
            detail_actionbar_view.setVisibility(VISIBLE);
            tv_friend_name.setVisibility(INVISIBLE);
        } else {
            detail_actionbar_view.setVisibility(INVISIBLE);
            tv_friend_name.setVisibility(VISIBLE);
        }
        if (scrollY > TOP || scrollY < -MARGIN_HEIGHT) return;

        int dis = MARGIN_HEIGHT + TOP - (scrollY - (-MARGIN_HEIGHT));//计算当前位置到初始-MARGIN_HEIGHT的距离
        float alp = dis * 1f / (MARGIN_HEIGHT + TOP);
        disX = tv_friend_name.getRight() - (tv_detail_title.getWidth() - ComUtil.dpToPx(32));
        ViewHelper.setTranslationX(tv_friend_name, -disX * (1 - alp));
        ViewHelper.setTranslationY(tv_friend_name, disY * (1 - alp));
        siv_friend_photo.setAlpha(alp);

        int alpha = (1 - alp) * 255 < 180 ? 0 : (int) ((1 - alp) * 255);
        if (alpha == 0) {
            view_top_content.setBackgroundColor(ResUtil.getColor(R.color.white));
            tv_friend_name.setTextColor(ResUtil.getColor(R.color.primary_text_color));
        } else {
            tv_friend_name.setTextColor(ResUtil.getColor(R.color.white));
            view_top_content.setBackgroundColor(Color.argb(alpha, 235, 65, 61));
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (Math.abs(sliding_container.getScrollY()) >= screenHeight - 1) {
                if (isFinish && onScrollListener != null) {
                    onScrollListener.onSlidingFinish();
                }
            }
            sliding_container.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            setAlpha(sliding_container.getScrollY());
            setActionBarAlpha(sliding_container.getScrollY());
            scrollEmptyView();
            postInvalidate();
            if (isSliding && mScroller.isFinished()) {
                isSliding = false;
            }
        }
    }

    /**
     * EmptyView,居中
     */
    private void scrollEmptyView() {
        if (emptyView != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, appAreaHeiht + statesBarHeight - topHeight + sliding_container.getScrollY());
            params.gravity = Gravity.CENTER;
            emptyView.setLayoutParams(params);
        }
    }

    /**
     * @param scrollY 滑动距离
     */
    private void scroll(int scrollY) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(scrollY);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        if (onScrollListener != null) {
            this.onScrollListener = onScrollListener;
        }
    }

    public interface OnScrollListener {
        /**
         * @param scrollY 距离顶部距离（滑动距离）
         */
        void onScroll(int scrollY);

        /**
         * 滑动到底部
         */
        void onSlidingFinish();
    }
}
