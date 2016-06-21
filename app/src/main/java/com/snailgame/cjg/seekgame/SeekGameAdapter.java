package com.snailgame.cjg.seekgame;

import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.seekgame.category.CategoryFragment;
import com.snailgame.cjg.seekgame.rank.RankFragment;
import com.snailgame.cjg.seekgame.recommend.RecommendFragment;
import com.snailgame.cjg.spree.AllSpreeFragment;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.util.ResUtil;

public class SeekGameAdapter extends FixedFragmentStatePagerAdapter {
    public static final int RECOMMEND = 0, SPREE = 1, SORT = 2, RANK = 3, BBS = 4;
    private String[] barTitles;
    private int[] mRoute;
    private RecommendFragment recommendFragment;

    public SeekGameAdapter(FragmentManager fragmentManager, int[] route) {
        super(fragmentManager);
        barTitles = ResUtil.getStringArray(R.array.seekgame_bar_titles);
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
            case RECOMMEND:
                if (recommendFragment == null) {
                    route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_RECOMMEND;
                    recommendFragment = RecommendFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND, false, route);
                }
                return recommendFragment;
            case SPREE:
                return new AllSpreeFragment();

            case SORT:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_SORT;
                return CategoryFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_GAME_CATEGORY, route);
            case RANK:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_RANK;
                return RankFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_GAME_RANK, false, route);
            case BBS:
                return ComUtil.getBBSFragment();
            default:
                return null;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return barTitles[position];
    }

}
