package com.snailgame.cjg.search;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Uesr : Pancl
 * Date : 15-5-25
 * Time : 下午3:19
 * Description : 搜索 -> 搜索结果adapter
 */
public class SearchFragmentAdapter extends FragmentPagerAdapter {
    public final static int TAB_ALL = 0;
    public final static int TAB_GAME_APP = 1;
    public final static int TAB_ACTIVITY = 2;
    public final static int TAB_KUWAN = 3;

    public final static int[] TABS = new int[] {TAB_ALL, TAB_GAME_APP, TAB_ACTIVITY, TAB_KUWAN};

    private String[] string;
    private int[] mRoute;
    private IGuessFavourite guessFavourite;

    public SearchFragmentAdapter(FragmentManager fm, String[] string) {
        super(fm);
        this.string = string;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_ALL:
                SearchAllFragment searchAllFragment = new SearchAllFragment();
                searchAllFragment.setRoute(mRoute);
                searchAllFragment.setGuessFavourite(guessFavourite);
                return searchAllFragment;
            case TAB_GAME_APP:
                SearchGameAppFragment searchGameAppFragment = new SearchGameAppFragment();
                searchGameAppFragment.setGuessFavourite(guessFavourite);
                return searchGameAppFragment;
            case TAB_ACTIVITY:
                SearchActiveFragment searchActiveFragment = new SearchActiveFragment();
                searchActiveFragment.setGuessFavourite(guessFavourite);
                return searchActiveFragment;
            case TAB_KUWAN:
                SearchKuwanFragment searchKuwanFragment = new SearchKuwanFragment();
                searchKuwanFragment.setGuessFavourite(guessFavourite);
                return searchKuwanFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        if (string == null) return 0;
        return string.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (string != null && position < string.length)
            return string[position];
        return null;
    }

    public void setRoute(int[] route) {
        this.mRoute = route;
    }

    public void setGuessFavourite(IGuessFavourite guessFavourite) {
        this.guessFavourite = guessFavourite;
    }
}
