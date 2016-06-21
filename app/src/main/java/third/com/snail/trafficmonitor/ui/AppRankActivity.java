package third.com.snail.trafficmonitor.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;

import java.util.List;

import butterknife.Bind;
import third.com.snail.trafficmonitor.engine.data.bean.AppTraffic;
import third.com.snail.trafficmonitor.ui.helper.ActionBarHelper;
import third.com.snail.trafficmonitor.ui.loader.MonthAppTrafficLoader;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by kevin on 15/1/22.
 */
public class AppRankActivity extends SwipeBackActivity implements LoaderManager.LoaderCallbacks<List<AppTraffic>> {
    @Bind(R.id.tabs)
    PagerSlidingTabStrip mTab;
    @Bind(R.id.view_pager)
    ViewPager mPager;
    private PagerAdapter mAdapter;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.engine_activity_app_rank;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarHelper.makeCommonActionBar(this, R.string.traffic_month_app_detail);
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mTab.setViewPager(mPager, 2, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {

            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<List<AppTraffic>> onCreateLoader(int i, Bundle bundle) {
        return new MonthAppTrafficLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppTraffic>> loader, List<AppTraffic> appTraffics) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            AppRankFragment f = mAdapter.getChildAt(i);
            if (f != null)
                f.setDataWrapper(appTraffics);
        }
        /*long wifi = 0L;
        long mobile = 0L;
        for (AppTraffic t : appTraffics) {
            wifi += (t.wifiRxBytes + t.wifiTxBytes);
            mobile += (t.rxBytes + t.txBytes - t.wifiTxBytes - t.wifiRxBytes);
        }
        if (mobile <= 0) {
            mPager.setCurrentItem(1, true);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<List<AppTraffic>> loader) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private SparseArrayCompat<AppRankFragment> mChildren;

        private final String[] TITLES = {getString(R.string.mobile), "WIFI"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            mChildren = new SparseArrayCompat<>();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mChildren.remove(position);
        }

        public AppRankFragment getChildAt(int position) {
            return mChildren.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            ScrollFragment fragment = AppRankFragment.newInstance(position);
            mChildren.put(position, (AppRankFragment) fragment);
            return fragment;
        }
    }
}
