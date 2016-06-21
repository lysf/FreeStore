package com.snailgame.cjg.detail.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.detail.CommentListFragment;
import com.snailgame.cjg.detail.ForumFragment;
import com.snailgame.cjg.detail.GameSpreeFragment;
import com.snailgame.cjg.detail.IntroduceFragment;
import com.snailgame.cjg.detail.model.AppDetailModel;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.ResUtil;

import third.scrolltab.ScrollTabHolder;
import third.scrolltab.ScrollTabHolderFragment;

public class DetailFragmentAdapter extends FragmentPagerAdapter {
    public static int FRAGMENT_INTRODUCE = 0, FRAGMENT_COMMENT = 1, FRAGMENT_SPREE = 2, FRAGMENT_BBS = 3;
    private String[] barTitles;
    private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
    private ScrollTabHolder mListener;
    private FragmentManager mFragmentManager;
    private AppInfo mAppInfo;
    private AppDetailModel mDetailInfo;
    private IntroduceFragment introduceFragment;
    private int mHeaderHeight;
    private int tabCount;
    private int[] mRoute;
    private String appType;

    public DetailFragmentAdapter(FragmentManager fragmentManager, ScrollTabHolder listener, AppInfo appInfo, AppDetailModel mDetailInfo
            , int mHeaderHeight, int tabCount, int[] route) {
        super(fragmentManager);
        mListener = listener;
        barTitles = ResUtil.getStringArray(R.array.detail_tab_titles);
        mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
        this.mFragmentManager = fragmentManager;
        mAppInfo = appInfo;
        this.mDetailInfo = mDetailInfo;
        this.mHeaderHeight = mHeaderHeight;
        this.tabCount = tabCount;
        mRoute = route;
        appType = mDetailInfo.getcAppType();
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public void setTabHolderScrollingContent(ScrollTabHolder listener) {
        mListener = listener;
    }

    @Override
    public ScrollTabHolderFragment getItem(int position) {
        ScrollTabHolderFragment fragment;
        switch (position) {
            case 0:
                if (introduceFragment == null) {
                    introduceFragment = IntroduceFragment.getInstance(mAppInfo, mDetailInfo, mHeaderHeight, mRoute);
                }
                fragment = introduceFragment;
                break;
            case 1:
                if (appType.equals(BaseAppInfo.APP_TYPE_GAME)) {
                    FRAGMENT_SPREE=1;
                    fragment = GameSpreeFragment.getInstance(mDetailInfo.getnAppId(), mHeaderHeight);
                }else {
                    FRAGMENT_COMMENT=1;
                    fragment = CommentListFragment.getInstance(mDetailInfo.getnAppId(), mHeaderHeight);
                }
                break;
            case 2:
                FRAGMENT_COMMENT=2;
                fragment = CommentListFragment.getInstance(mDetailInfo.getnAppId(), mHeaderHeight);
                break;
            case 3:
                fragment = ForumFragment.getInstance(mHeaderHeight, Integer.valueOf(mDetailInfo.getnFid()));
                break;
            default:
                throw new IllegalArgumentException(FreeStoreApp.getContext().getString(R.string.unknown_para));
        }
        mScrollTabHolders.put(position, fragment);
        if (mListener != null) {
            fragment.setScrollTabHolder(mListener);
        }
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), getItemId(position));
        ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);
            }
        }
        return object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return ResUtil.getString(R.string.detail_introduce);
            case 1:
                if(appType.equals(BaseAppInfo.APP_TYPE_GAME))
                    return ResUtil.getString(R.string.detail_spree);
                else{
                    return ResUtil.getString(R.string.detail_comment);
                }
            case 2:
                if(appType.equals(BaseAppInfo.APP_TYPE_GAME))
                    return ResUtil.getString(R.string.detail_comment);
            case 3:
                    return ResUtil.getString(R.string.detail_bbs);
        }
        return ResUtil.getString(R.string.detail_introduce);
    }

    public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
        return mScrollTabHolders;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

}
