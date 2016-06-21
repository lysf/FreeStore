package com.snailgame.cjg.spree;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.snailgame.cjg.spree.adapter.SpreeTabAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 礼包Tab界面
 * Created by TAJ_C on 2015/4/30.
 */
public class SpreeTabFragment extends FixedSupportV4BugFragment {
    private FreeStoreApp mApp;

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;

    private SpreeTabAdapter spreeTabAdapter;
    private static final int TITLE_COUNT = 4;
    private int[] mRoute;

    public static SpreeTabFragment getInstance( int[] route) {
        SpreeTabFragment spreeTabFragment = new SpreeTabFragment();
        Bundle bundle=new Bundle();
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        spreeTabFragment.setArguments(bundle);
        return spreeTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_content_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApp = (FreeStoreApp) getActivity().getApplication();
        FragmentManager fragmentManager = getChildFragmentManager();
        spreeTabAdapter = new SpreeTabAdapter(fragmentManager,mRoute);
        mViewPager.setAdapter(spreeTabAdapter);
        mViewPager.setOffscreenPageLimit(3);
        tabs.setViewPager(mViewPager, TITLE_COUNT, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
            }
        });
    }


}