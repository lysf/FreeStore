package com.snailgame.cjg.seekapp;

import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.seekgame.category.CategoryFragment;
import com.snailgame.cjg.seekgame.recommend.RecommendFragment;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.util.ResUtil;

public class SeekAppAdapter extends FixedFragmentStatePagerAdapter {
    private String[] barTitles ;
    private int[] mRoute;
    private RecommendFragment recommendFragment;
    public static final int PAGE_RECOMMEND = 0, PAGE_SORT = 1;

    public SeekAppAdapter(FragmentManager fragmentManager,int[] route) {
		super(fragmentManager);
		barTitles = ResUtil.getStringArray(R.array.seekapp_bar_titles);
        mRoute = route;
    }

    @Override
    public int getCount() {
        return barTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        int[] route = mRoute.clone();
        switch (position) {
            case 0:
                if (recommendFragment == null) {
                    route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_RECOMMEND;
                    recommendFragment = RecommendFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND,false, route);
                }
                return recommendFragment;

            case 1:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_SORT;
                return CategoryFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_APP_CATEGORY, route);
            default:
                return null;
        }
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return barTitles[position];
    }

}
