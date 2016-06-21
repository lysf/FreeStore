package com.snailgame.cjg.seekgame.collection;

import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekgame.collection.adapter.CollectionListAdapter;
import com.snailgame.cjg.seekgame.collection.model.CollectionListModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 合集列表 fragment
 */
public class CollectionListFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {
    private final static String KEY_URL = "key_url";
    private final static String KEY_MODEL = "key_model";
    private final static String KEY_COLLECTION = "key_collection";
    private final static String KEY_PAGE = "key_page";
    private static final String MANUL_CHANGE_USER_VISIBLE = "key_change_user_visible";
    private static final String TAG = CollectionListFragment.class.getSimpleName();

    private CollectionListAdapter mAdapter;
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    private CollectionListModel mModel;
    private ArrayList<CollectionListModel.ModelItem> mCollectionList = new ArrayList<CollectionListModel.ModelItem>();
    private int nextPage = 1;


    private String url;


    /**
     * @param url   链接
     * @param route PV用路径记载
     * @return
     */
    public static CollectionListFragment getInstance(String url, boolean isManulChangeUserVisible, int[] route) {
        CollectionListFragment fragment = new CollectionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        bundle.putBoolean(MANUL_CHANGE_USER_VISIBLE, isManulChangeUserVisible);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        mAdapter = new CollectionListAdapter(mParentActivity, mCollectionList, mRoute);
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
            if (bundle.getBoolean(MANUL_CHANGE_USER_VISIBLE))    //只有需要手动控制isLoadinUserVisibile 才会改变isLoadinUserVisibile为false
                isLoadinUserVisibile = false;
        }
    }


    @Override
    protected void loadData() {
        createOrGetDataTask();
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        mCollectionList = savedInstanceState.getParcelableArrayList(KEY_COLLECTION);
        mModel = savedInstanceState.getParcelable(KEY_MODEL);
        nextPage = savedInstanceState.getInt(KEY_PAGE, 1);

        showView();

        if (savedInstanceState.getBoolean(KEY_NO_MORE, false))
            noMoreData();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (mCollectionList != null && mCollectionList.size() != 0 && mModel != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
            outState.putInt(KEY_PAGE, nextPage);
            outState.putParcelableArrayList(KEY_COLLECTION, mCollectionList);
            outState.putParcelable(KEY_MODEL, mModel);
        }
    }


    @Override
    public void onLoadMore() {
        if (mModel == null || mModel.getPageInfo() == null) {
            noMoreData();
            return;
        }

        if (nextPage > mModel.getPageInfo().getTotalPageCount()) {
            noMoreData();
            return;
        }

        createOrGetDataTask();


    }

    private void showView() {
        mAdapter.refreshData(mCollectionList);
        resetRefreshUi();
        if (ListUtils.isEmpty(mCollectionList)) {
            showEmpty();
        }
    }

    private void createOrGetDataTask() {


        FSRequestHelper.newGetRequest(url + nextPage + ".json", TAG, CollectionListModel.class,
                new IFDResponse<CollectionListModel>() {
                    @Override
                    public void onSuccess(CollectionListModel result) {
                        mModel = result;

                        if (mModel == null || ListUtils.isEmpty(mModel.getItemList())) {
                            showEmpty();
                            return;
                        }
                        if (mModel.getPageInfo() == null || mModel.getPageInfo().getTotalPageCount() == 1)
                            loadMoreListView.onNoMoreData();
                        for (CollectionListModel.ModelItem item : mModel.getItemList()) {
                            mCollectionList.add(item);
                        }
                        if (mModel.getItemList().size() <= 2) {
                            //填充空白区域
                            loadMoreListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight()
                                    - mModel.getItemList().size() * ComUtil.dpToPx(160) - ComUtil.dpToPx(160));
                        }
                        showView();
                        nextPage++;
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
    public void onDestroyView() {
        super.onDestroyView();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}
