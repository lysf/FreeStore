package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义SwipeRefreshlayout 解决与EmptyView冲突问题
 * Created by sunxy on 2015/10/8.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private int width,height;
    private View emptyView;
    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void addView(View child, LayoutParams params) {
        super.addView(child, params);
        emptyView =child;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count=getChildCount();
        for(int i=0;i<count;i++) {
            if (getChildAt(i)== emptyView) {
                width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                        MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int count=getChildCount();
        for(int i=0;i<count;i++) {
            if (getChildAt(i)== emptyView) {
                getChildAt(i).layout(left, top, left + width, top + height);
            }
        }
    }
}
