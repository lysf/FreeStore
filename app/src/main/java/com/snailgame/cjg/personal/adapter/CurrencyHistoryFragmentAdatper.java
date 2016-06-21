package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import com.snailgame.cjg.R;
import com.snailgame.cjg.personal.BaseHistoryFragment;
import com.snailgame.cjg.personal.CurrencyHistoryFragment;
import com.snailgame.fastdev.util.ResUtil;

import third.scrolltab.ScrollTabHolder;

/**
 * Created by TAJ_C on 2015/11/6.
 */
public class CurrencyHistoryFragmentAdatper extends FragmentPagerAdapter {
    private Context context;
    private String[] mHistoryTitles;
    private ScrollTabHolder mListener;

    public static final int TAB_HISTORY_GET = 0, TAB_HISTORY_USED = 1;
    private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;

    public CurrencyHistoryFragmentAdatper(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        mHistoryTitles = ResUtil.getStringArray(R.array.personal_currency_history_titles);
        mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
    }

    @Override
    public Fragment getItem(int position) {
        CurrencyHistoryFragment fragment = null;
        switch (position) {
            case TAB_HISTORY_GET:
                fragment = CurrencyHistoryFragment.getInstance(BaseHistoryFragment.HISTORY_TYPE_GETED);
                break;
            case TAB_HISTORY_USED:
                fragment = CurrencyHistoryFragment.getInstance(BaseHistoryFragment.HISTORY_TYPE_USED);
            default:
                break;
        }

        mScrollTabHolders.put(position, fragment);
        if (mListener != null) {
            fragment.setScrollTabHolder(mListener);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mHistoryTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mHistoryTitles[position];
    }

    public void setTabHolderScrollingContent(ScrollTabHolder listener) {
        mListener = listener;
    }

    public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
        return mScrollTabHolders;
    }
}
