package com.snailgame.cjg.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.server.ChannelAppInstallGetService;
import com.snailgame.cjg.guide.widget.GuideViewpager;
import com.snailgame.cjg.util.SharedPreferencesUtil;

import java.util.List;

import third.viewpagerindicator.CirclePageIndicatorCompat;

/**
 * Created by sunxy on 2014/12/10.
 */
public class GuideActivity extends Activity implements View.OnClickListener {
    private GuideViewpager mGuideViewPager;
    private CirclePageIndicatorCompat indicator;
    private GuidePagerAdapter adapter;
    private List<View> pagerViews;

    public static Intent newIntent(Context context) {
        return new Intent(context, GuideActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        indicator = (CirclePageIndicatorCompat) findViewById(R.id.guideCirclePageIndicator);
        mGuideViewPager = (GuideViewpager) findViewById(R.id.guideViewPager);
        pagerViews = mGuideViewPager.initPageView(this, this);
        adapter = new GuidePagerAdapter();
        mGuideViewPager.setAdapter(adapter);
        indicator.setViewPager(mGuideViewPager, adapter.getCount());

        // 判断是否是渠道安装
        if (!ChannelAppInstallGetService.isInWhiteList())
            startService(ChannelAppInstallGetService.newIntent(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goHomeBtn:
                SharedPreferencesUtil.getInstance().guideShowed();
                startActivity(MainActivity.newIntent(GuideActivity.this));
                finish();
                break;
        }
    }

    class GuidePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (pagerViews != null)
                return pagerViews.size();
            return 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pagerViews.get(position);
            container.addView(view);
            return view;
        }
    }
}
