package third.com.snail.trafficmonitor.ui;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.snailgame.cjg.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.data.bean.AppTraffic;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.ui.adapter.AppRankAdapter;
import third.com.snail.trafficmonitor.ui.widget.ScrollBackListView;

import static third.com.snail.trafficmonitor.engine.util.LogWrapper.makeTag;

/**
 * Created by kevin on 14/12/15.
 */
public class AppRankFragment extends ScrollFragment implements AbsListView.OnScrollListener {
    private final String TAG = makeTag(AppRankFragment.class);

    @Bind(R.id.list_view)
    ScrollBackListView mList;
    @Bind(R.id.progress_bar)
    View mProgress;
    @Bind(R.id.no_data_tv)
    TextView mNoDataHint;
    private int mPosition;
    private AppRankAdapter mAdapter;

    public static final int POSITION_MOBILE = 0;
    public static final int POSITION_WIFI = 1;

    private int mItemHeight;
    private int mListViewFullHeight;

    public static AppRankFragment newInstance(int position) {
        AppRankFragment fragment = new AppRankFragment();
        Bundle args = new Bundle();
        args.putInt("data", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("data");
        LogWrapper.d(TAG, "onCreate position: " + mPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.engine_app_rank_listview_layout, container, false);
        ButterKnife.bind(this, view);
        mList.setVerticalScrollBarEnabled(false);
        mList.setOnScrollListener(this);
        mList.showHideFooter(false);
        mItemHeight = getResources().getDimensionPixelSize(R.dimen.item_app_height);
        mList.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mListViewFullHeight = mList.getMeasuredHeight() - getResources().getDimensionPixelSize(R.dimen.pager_tab_height);
                mList.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        mAdapter = new AppRankAdapter(getActivity(), null, mPosition == 0 ? AppRankAdapter.TYPE_MOBILE : AppRankAdapter.TYPE_WIFI);
        mList.setAdapter(mAdapter);
        mAdapter.registerDataSetObserver(mObserver);

        mList.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        mNoDataHint.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.unregisterDataSetObserver(mObserver);
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            int count = mAdapter.getCount();

            if (count * mItemHeight < mListViewFullHeight) {
                int def = mListViewFullHeight - count * mItemHeight;
                mList.setEmptyHeight(def);
            } else {
                mList.resetEmptyHeight();
            }
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            int def = mListViewFullHeight;
            mList.setEmptyHeight(def);
        }
    };

    public void setDataWrapper(List<AppTraffic> data) {
        long wifi = 0L;
        long mobile = 0L;
        for (AppTraffic t : data) {
            wifi += (t.wifiRxBytes + t.wifiTxBytes);
            mobile += (t.rxBytes + t.txBytes - t.wifiTxBytes - t.wifiRxBytes);
        }

        mProgress.setVisibility(View.GONE);
        if (mPosition == POSITION_MOBILE) {
            if (mobile <= 0) {
                mNoDataHint.setText(String.format(getString(R.string.traffic_no_data_hint), getString(R.string.traffic_data_hint_mobile)));
                mNoDataHint.setVisibility(View.VISIBLE);
            } else {
                mList.setVisibility(View.VISIBLE);
                mAdapter.setData(data);
                mList.showHideFooter(true);
            }
        } else if (mPosition == POSITION_WIFI) {
            if (wifi <= 0) {
                mNoDataHint.setText(String.format(getString(R.string.traffic_no_data_hint), getString(R.string.traffic_data_hint_wifi)));
                mNoDataHint.setVisibility(View.VISIBLE);
            } else {
                mList.setVisibility(View.VISIBLE);
                mAdapter.setData(data);
                mList.showHideFooter(true);
            }
        }
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mList.getFirstVisiblePosition() >= 1) {
            return;
        }
        mList.setSelectionFromTop(1, scrollHeight);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null) {
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
        }
    }

}
