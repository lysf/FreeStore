package com.snailgame.cjg.seekapp;

import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.MainThreadBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * tab页-软件
 */
public class SeekAppFragment extends FixedSupportV4BugFragment {
    private FreeStoreApp mApp;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;

    private static final int TITLE_COUNT = 2;
    private int[] mRoute;
    private SeekAppAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoute = createRoute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_content_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApp = (FreeStoreApp) getActivity().getApplication();
        FragmentManager fragmentManager = getChildFragmentManager();
        mAdapter = new SeekAppAdapter(fragmentManager, mRoute);
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


    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 软件
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_APPPAGE,
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
