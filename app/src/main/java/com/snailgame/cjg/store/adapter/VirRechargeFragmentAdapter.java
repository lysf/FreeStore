package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.store.VirRechargeAllFragment;
import com.snailgame.cjg.store.VirRechargeHotFragment;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by TAJ_C on 2015/11/27.
 */
public class VirRechargeFragmentAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] mTitles;


    public static final int TAB_HOT = 0;
    public static final int TAB_ALL = 1;

    public VirRechargeFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        mTitles = ResUtil.getStringArray(R.array.store_vir_recharge_title);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_HOT:
                return VirRechargeHotFragment.getInstance();
            case TAB_ALL:
                return VirRechargeAllFragment.getInstance();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mTitles[position];
    }
}
