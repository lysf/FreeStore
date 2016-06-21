package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;

import third.viewpagerindicator.CirclePageIndicator;

/**
 * Created by sunxy on 2014/10/16.
 */
public class AutoScrollView extends RelativeLayout {
    private AutoScrollViewPager viewPager;
    private CirclePageIndicator indicator;

    public AutoScrollView(Context context) {
        super(context);
        addView(context);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(context);
    }

    private void addView(Context context) {

        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.auto_scrollview_layout, null);

        viewPager = (AutoScrollViewPager) view.findViewById(R.id.autoScrollViewPager);
        viewPager.setCurrentItem(0);
        indicator = (CirclePageIndicator) view.findViewById(R.id.circlePageIndicator);

        addView(view);
    }

    public void hideCircleIndicator() {
        if (indicator != null)
            indicator.setVisibility(GONE);
    }

    public void setAdapter(RecyclingPagerAdapter adapter) {
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
            indicator.setViewPager(viewPager, adapter.getCircleCount());
        }
    }

    public void setCurrentItem(int index) {
        if (viewPager != null)
            viewPager.setCurrentItem(index);
    }


    public void startAutoScroll() {
        if (viewPager != null)
            viewPager.startAutoScroll();
    }

    public void stopAutoScroll() {
        if (viewPager != null)
            viewPager.stopAutoScroll();
    }

    public void setIsAutoScroll(boolean isAutoScroll) {
        if (viewPager != null)
            viewPager.setIsAutoScroll(isAutoScroll);
    }
}
