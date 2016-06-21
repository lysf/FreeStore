package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class AutoScrollViewPager extends ViewPager {
    public static final int DELAY_IN_MILLS = 4 * 1000;
    private long interval = DELAY_IN_MILLS;
    //是否循环滑动
    private boolean isCycle = true;
    private boolean isVerticalDraged = false;
    private Handler handler;
    private CustomDurationScroller scroller = null;
    public static final int SCROLL_WHAT = 0;
    private boolean isAutoScroll = true;//设置是否允许自动滚动的标志位

    public AutoScrollViewPager(Context paramContext) {
        super(paramContext);
        init();
    }

    public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }


    private void init() {
        handler = new MyHandler();
    }


    //view从window移除的时候将Handler置为null，解决引用该控件的Fragment的内存泄漏问题
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoScroll();
        if (handler != null)
            handler.removeMessages(SCROLL_WHAT);
        handler = null;
    }

    /**
     * 开始滑动
     */
    public void startAutoScroll() {
        if (!isAutoScroll)
            return;
        sendScrollMessage(DELAY_IN_MILLS);
    }

    /**
     * 设置是否允许滑动
     */
    public void setIsAutoScroll(boolean isAutoScroll) {
        this.isAutoScroll = isAutoScroll;
    }


    /**
     * 停止滑动
     */
    public void stopAutoScroll() {
        if (!isAutoScroll)
            return;
        if (handler == null) return;
        handler.removeMessages(SCROLL_WHAT);
    }


    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        if (handler == null) return;
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }


    /**
     * 滑动方法
     */
    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }

        int nextItem = ++currentItem;
        if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, false);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }


    private float startX;
    private float startY;

    /**
     * 触摸停止的处理方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getAdapter() != null && getAdapter().getCount() > 0) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = ev.getX();
                    startY = ev.getY();
                    //暂停滑动
                    stopAutoScroll();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    isVerticalDraged = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = ev.getX() - startX;
                    float deltaY = ev.getY() - startY;
                    //处理左右滑动
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        isVerticalDraged = false;
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        isVerticalDraged = true;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    startX = 0;
                    //开始滑动
                    startAutoScroll();
                    getParent().requestDisallowInterceptTouchEvent(false);
                    isVerticalDraged = false;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //解决华为手机上下刷新被拦截的问题
        if (isVerticalDraged)
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    scrollOnce();
                    sendScrollMessage(interval);
                default:
                    break;
            }
        }
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}
