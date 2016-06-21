package com.snailgame.cjg.personal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by yanHH on 2016/6/13.
 */
public class SlidingScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener;

    public SlidingScrollView(Context context) {
        super(context);
    }

    public SlidingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(SlidingScrollView scrollView, int x, int y, int oldX, int oldY);
    }

}
