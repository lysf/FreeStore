package com.snailgame.cjg.common.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.seekgame.collection.CollectionFragment;
import com.snailgame.cjg.seekgame.recommend.RecommendFragment;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.fastdev.FastDevFragment;


/**
 * Created by sunxy on 14-7-22.
 */
public abstract class AbsBaseFragment extends FastDevFragment {
    protected Activity mParentActivity;
    private EmptyView mEmptyView;
    public static final String KEY_SAVE = "key_save";
    public static final String KEY_NO_MORE = "key_no_more";        // LoadMoreListView是否已全部加载
    public static final String KEY_UID = "key_uid";
    protected int[] mRoute;
    protected boolean isLoadinUserVisibile = true;
    protected boolean mSaved = false;

    protected abstract LoadMoreListView getListView();

    /**
     * 恢复页面
     *
     * @param savedInstanceState 保存有状态的Bundle
     */
    protected abstract void restoreData(Bundle savedInstanceState);

    /**
     * 保存数据
     *
     * @param outState 保存状态的Bundle
     */
    protected abstract void saveData(Bundle outState);

    private NetworkChangeReceiver mNetworkChangeReceiver;
    private IntentFilter filter;


    private boolean lastConnected = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        lastConnected = NetworkUtils.isNetworkAvailable(mParentActivity);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmptyView = new EmptyView(mParentActivity, getListView());
        mEmptyView.setErrorButtonClickListener(mErrorClickListener);

        if (savedInstanceState != null)
            mSaved = savedInstanceState.getBoolean(KEY_SAVE, false);

        if (savedInstanceState != null && mSaved) {
            restoreData(savedInstanceState);        // 恢复页面
        } else {
            // AppListFragment是独立在Activity中的，不在Adapter中,不会执行setUserVisibleHint
            if (((this instanceof AppListFragment) && !(this instanceof RecommendFragment))
                    || (this instanceof CollectionFragment)) {
                loadData();     // 加载数据
            } else if (!this.isLoadinUserVisibile) {
                loadData();     // 加载数据
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (getView() != null && isLoadinUserVisibile && !mSaved) {
                loadData();     // 加载数据
            }

            isLoadinUserVisibile = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }

    protected View.OnClickListener mErrorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            loadData();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mParentActivity.registerReceiver(mNetworkChangeReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mParentActivity.unregisterReceiver(mNetworkChangeReceiver);
        } catch (IllegalArgumentException e) {

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void resetRefreshUi() {
        if (getListView() != null)
            getListView().onLoadMoreComplete();
    }

    protected void noMoreData() {
        getListView().onNoMoreData();
    }

    protected void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.showEmpty();
            if (getListView() != null)
                getListView().goneFooter();
        }
    }

    protected void showLoading() {
        if (mEmptyView != null) {
            mEmptyView.showLoading();
        }
    }

    protected void showError() {
        if (mEmptyView != null) {
            mEmptyView.showError();
        }
    }

    protected EmptyView getEmptyView() {
        return mEmptyView;
    }


    public class NetworkChangeReceiver extends BroadcastReceiver {
        public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION.equals(intent.getAction())) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm == null) return;
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.isConnected() && !lastConnected) {
                        loadData();
                        lastConnected = true;
                    }
                }
            }
        }
    }
}