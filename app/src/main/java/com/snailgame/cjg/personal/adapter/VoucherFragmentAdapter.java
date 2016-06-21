package com.snailgame.cjg.personal.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.personal.MyVoucherGameFragment;
import com.snailgame.cjg.personal.MyVoucherGoodsFragment;
import com.snailgame.cjg.personal.MyVoucherWNFragment;

import java.util.ArrayList;

/**
 * Uesr : Pancl
 * Date : 15-4-28
 * Time : 下午3:19
 * Description : 个人中心 -> 我的代金券（蜗牛、兔兔、商品、游戏）adapter
 */
public class VoucherFragmentAdapter extends FragmentPagerAdapter {
    public final static int TAB_WN = 0;
    public final static int TAB_TUTU = 1;
    public final static int TAB_GOODS = 2;
    public final static int TAB_GAME = 3;
    private ArrayList<String> titles;

    public VoucherFragmentAdapter(FragmentManager fm, ArrayList<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int i) {
        //如果不显示蜗牛代金卷 则
        if(false == PersistentVar.getInstance().getSystemConfig().isVoucherWoniuSwitch()){
            i++;
        }
        switch (i) {
            case TAB_WN:
                return new MyVoucherWNFragment();
            case TAB_TUTU:
                return MyVoucherGameFragment.getInstance(MyVoucherGameFragment.MYVOUCHER_TYPE_TUTU);
            case TAB_GOODS:
                return new MyVoucherGoodsFragment();
            case TAB_GAME:
                return MyVoucherGameFragment.getInstance(MyVoucherGameFragment.MYVOUCHER_TYPE_YOUXI);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        if (titles == null) return 0;
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.size())
            return titles.get(position);
        return null;
    }
}
