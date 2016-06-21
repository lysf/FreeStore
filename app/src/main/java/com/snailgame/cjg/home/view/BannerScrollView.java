package com.snailgame.cjg.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.AutoScrollViewPager;
import com.snailgame.cjg.common.widget.RecyclingPagerAdapter;
import com.snailgame.cjg.global.FreeStoreApp;


/**
 * Created by TAJ_C on 2016/6/16.
 */
public class BannerScrollView extends RelativeLayout {
    private AutoScrollViewPager viewPager;
    private BannerCirclePageIndicator indicator;

    public BannerScrollView(Context context) {
        super(context);
        addView(context);
    }

    public BannerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(context);
    }

    private void addView(Context context) {

        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.home_banner_scrollview, null);

        viewPager = (AutoScrollViewPager) view.findViewById(R.id.autoScrollViewPager);
        viewPager.setCurrentItem(0);
        indicator = (BannerCirclePageIndicator) view.findViewById(R.id.circlePageIndicator);

        addView(view);
        setIsAutoScroll(false);
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
