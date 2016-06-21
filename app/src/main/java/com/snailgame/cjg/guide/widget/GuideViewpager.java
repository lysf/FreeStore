package com.snailgame.cjg.guide.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.ViewPagerCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunxy on 2015/3/18.
 * 欢迎页Viewpager
 */
public class GuideViewpager extends ViewPagerCompat implements ViewPagerCompat.PageTransformer {
    private static final int PAGER_COUNT = 5;
    private FifthPageViewHolder fifthPageViewHolder;
    private View firstPageView, nextPageView, thirdPageView, fourthPageView, fifthPageView;
    private List<View> pagerViews;

    public GuideViewpager(Context context) {
        super(context);
    }

    public GuideViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public List<View> initPageView(Context context, OnClickListener btnClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);

        pagerViews = new ArrayList<>(PAGER_COUNT);
        //第一页
        firstPageView = inflater.inflate(R.layout.guide_page_one, null);
        pagerViews.add(firstPageView);
        //第二页
        nextPageView = inflater.inflate(R.layout.guide_page_two, null);
        pagerViews.add(nextPageView);
        //第三页
        thirdPageView = inflater.inflate(R.layout.guide_page_three, null);
        pagerViews.add(thirdPageView);
        //第四页
        fourthPageView = inflater.inflate(R.layout.guide_page_four, null);
        pagerViews.add(fourthPageView);
        //第五页
        fifthPageView = inflater.inflate(R.layout.guide_page_five, null);
        fifthPageViewHolder = new FifthPageViewHolder(fifthPageView);
        pagerViews.add(fifthPageView);
        fifthPageViewHolder.goHomeBtn.setOnClickListener(btnClickListener);

        setPageTransformer(true, this);
        return pagerViews;
    }

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {
            ViewHelper.setAlpha(view, 0);
        } else if (position <= 1) { // [-1,1]
            ViewHelper.setAlpha(view, 1);
        } else {
            ViewHelper.setAlpha(view, 0);
        }
    }


    static class FifthPageViewHolder {
        @Bind(R.id.goHomeBtn)
        TextView goHomeBtn;

        public FifthPageViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
