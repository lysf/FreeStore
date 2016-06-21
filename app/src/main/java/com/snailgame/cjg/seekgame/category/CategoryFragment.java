package com.snailgame.cjg.seekgame.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekapp.SeekAppAdapter;
import com.snailgame.cjg.seekgame.SeekGameAdapter;
import com.snailgame.cjg.seekgame.category.adapter.CategoryListAdapter;
import com.snailgame.cjg.seekgame.category.model.CategoryListModel;
import com.snailgame.cjg.seekgame.category.model.CategoryListModel.ModelItem;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 分类 fragment
 */
public class CategoryFragment extends AbsBaseFragment {
    private final static String KEY_URL = "key_url";
    private final static String KEY_CATEGORY = "key_category";
    private static final String TAG = CategoryFragment.class.getSimpleName();

    private CategoryListAdapter mAdapter;
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    private ArrayList<ModelItem> mCategorys;

    private String url;


    public static CategoryFragment getInstance(String url, int[] route) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(url)) {
            if (url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_CATEGORY)) {
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_APP_SORT);
            } else if (url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_CATEGORY)) {
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_SORT);
            }
        }
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(url)) {
            if (url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_CATEGORY)) {
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_APP_SORT);
            } else if (url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_CATEGORY)) {
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_SORT);
            }
        }
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (loadMoreListView != null) {

            if (event.getTabPosition() == MainActivity.TAB_SEEK_GAME &&
                    url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_CATEGORY) &&
                    event.getPagePosition() == SeekGameAdapter.SORT) {
                loadMoreListView.setSelection(0);
            }

            if (event.getTabPosition() == MainActivity.TAB_COMMUNICATION &&
                    url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_CATEGORY) &&
                    event.getPagePosition() == SeekAppAdapter.PAGE_SORT) {
                loadMoreListView.setSelection(0);
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        handleArguments();

        mAdapter = new CategoryListAdapter(mParentActivity, mCategorys, mRoute);
        loadMoreListView.setAdapter(mAdapter);
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();
    }

    @Override
    protected void loadData() {

        FSRequestHelper.newGetRequest(url, TAG, CategoryListModel.class, new IFDResponse<CategoryListModel>() {
            @Override
            public void onSuccess(CategoryListModel result) {
                if (result != null) {
                    mCategorys = result.getInfos();
                    showView();
                }
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, true, true, new ExtendJsonUtil());
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        mCategorys = savedInstanceState.getParcelableArrayList(KEY_CATEGORY);
        showView();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (mCategorys != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putParcelableArrayList(KEY_CATEGORY, mCategorys);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    private void showView() {
        if (ListUtils.isEmpty(mCategorys)) {
            showEmpty();
        } else {
            mAdapter.refreshData(mCategorys);
        }

        resetRefreshUi();
    }


}
