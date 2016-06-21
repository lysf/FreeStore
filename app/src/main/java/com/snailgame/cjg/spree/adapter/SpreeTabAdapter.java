package com.snailgame.cjg.spree.adapter;

import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.snailgame.cjg.R;
import com.snailgame.cjg.member.MemberExclusiveFragment;
import com.snailgame.cjg.spree.AllSpreeFragment;
import com.snailgame.cjg.spree.HotSpreeFragment;
import com.snailgame.cjg.spree.LocalAppSpreeFragment;
import com.snailgame.fastdev.util.ResUtil;

/**
 * 礼包 Tab adapter
 * Created by TAJ_C on 2015/4/30.
 */
public class SpreeTabAdapter extends FixedFragmentStatePagerAdapter {

    private String[] barTitles;

    private static final int TAB_HOT_SPREE = 0;
    private static final int TAB_MEMBER_SPREE = 1;
    private static final int TAB_GUESS_LIKE_SPREE = 2;
    private static final int TAB_LOCAL_APP_SPREE = 3;
    private int[] mRoute;
    public SpreeTabAdapter(FragmentManager fm, int[] route) {
        super(fm);
        barTitles = ResUtil.getStringArray(R.array.spree_tab_array);
        mRoute = route;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_HOT_SPREE:
                return HotSpreeFragment.getInstance(mRoute);
            case TAB_MEMBER_SPREE:
                return MemberExclusiveFragment.getInstance();
            case TAB_GUESS_LIKE_SPREE:
                return AllSpreeFragment.getInstance();
            case TAB_LOCAL_APP_SPREE:
                return LocalAppSpreeFragment.getInstance(mRoute);
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return barTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return barTitles[position];
    }
}
