package com.snailgame.cjg.util;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by yftx on 3/17/15.
 * UI相关操作类
 */
public class UIUtil {

    /**
     * viewPager切换时候不添加动画
     * @param viewPager
     * @param tabIndex
     */
    public static void changeViewPagerTabWithOutAnimation(ViewPager viewPager, int tabIndex) {
        if (viewPager == null) return;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null && adapter.getCount() >= tabIndex)
            viewPager.setCurrentItem(tabIndex, false);
    }
}
