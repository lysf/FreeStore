package com.snailgame.cjg.seekgame;

import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * tab页-游戏
 */
public class SeekGameFragment extends FixedSupportV4BugFragment {
    private static final float TITLE_COUNT = 5f;//单屏最多显示title的个数
    private FreeStoreApp mApp;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    private int[] mRoute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoute = createRoute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_content_fragment, container, false);
        ButterKnife.bind(this, view);
        setMarginTop();
        return view;
    }

    private void setMarginTop() {
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)tabs.getLayoutParams();
        if(params!=null)
            params.topMargin=ComUtil.getStatesBarHeight()+getResources().getDimensionPixelSize(R.dimen.actionbar_height);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApp = (FreeStoreApp) getActivity().getApplication();
        FragmentManager fragmentManager = getChildFragmentManager();
        SeekGameAdapter mAdapter = new SeekGameAdapter(fragmentManager, mRoute);
        mViewPager.setAdapter(mAdapter);
        tabs.setViewPager(mViewPager, TITLE_COUNT, new ViewPagerSelectedEvent());
    }


    @Override
    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    private class ViewPagerSelectedEvent implements PagerSlideEventInterface {
        public void viewPagerPageSelected(int position) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.reset(this);
    }


    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_SEEK_GAME && event.getPagePosition() == -1) {
            MainThreadBus.getInstance().post(new TabClickedEvent(MainActivity.TAB_SEEK_GAME, mViewPager.getCurrentItem()));
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 游戏
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_GAMEPAGE,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }
}
