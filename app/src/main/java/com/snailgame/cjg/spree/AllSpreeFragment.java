package com.snailgame.cjg.spree;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.common.widget.SpreeDetailDialog;
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.SpreeTaoSuccessEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.SpreeModel;
import com.snailgame.cjg.seekgame.SeekGameAdapter;
import com.snailgame.cjg.spree.adapter.AllSpreeAdapter;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 全部礼包 fragment
 * Created by TAJ_C on 2015/4/30.
 */
public class AllSpreeFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, AdapterView.OnItemClickListener {
    static String TAG = AllSpreeFragment.class.getName();
    protected LoadMoreListView mListView;
    private AllSpreeAdapter mAdapter;
    protected ArrayList<String> mKeyArray;
    public static String KEY_LIST_DATA = "key_list_data";

    private int totalPage = 0;
    private int currentPage = 1;

    protected ArrayList<SpreeGiftInfo> itemList;

    public static AllSpreeFragment getInstance() {
        AllSpreeFragment allSpreeFragment = new AllSpreeFragment();
        return allSpreeFragment;
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
        mListView = (LoadMoreListView) mContent.findViewById(R.id.content);
        mListView.enableLoadingMore(true);
        mListView.setLoadingListener(this);
        mListView.setOnItemClickListener(this);
        View spaceView = new View(getActivity());
        spaceView.setLayoutParams(new AbsListView.LayoutParams(ComUtil.dpToPx(1), ComUtil.dpToPx(8)));
        mListView.addHeaderView(spaceView);
        mAdapter = new AllSpreeAdapter(getActivity(), null);
        mAdapter.setOnSpreeGetListener(new AllSpreeAdapter.SpreeGetListener() {
            @Override
            public void spreeGetAction(SpreeGiftInfo spreeGiftInfo) {
                if (mKeyArray == null) {
                    mKeyArray = new ArrayList<String>();
                }
                SpreeUtils.getSpreeAction(getActivity(), mKeyArray, spreeGiftInfo, TAG);
            }
        });
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        showLoading();
        getAllSpreeData();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_SPREE);
    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_SPREE);
    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_SEEK_GAME &&
                event.getPagePosition() == SeekGameAdapter.SPREE &&
                mListView != null) {
            mListView.setSelection(0);
        }
    }

    @Override
    protected LoadMoreListView getListView() {
        return mListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        // onDestroyView 之后无法收到用户登录、登出信息，故在恢复数据时先判断用户信息是否改变
        if (!savedInstanceState.getString(KEY_UID, "").equals(IdentityHelper.getUid(FreeStoreApp.getContext()))) {
            loadData();
            return;
        }

        ArrayList<SpreeGiftInfo> itemList = savedInstanceState.getParcelableArrayList(KEY_LIST_DATA);
        showView(itemList);

        if (savedInstanceState.getBoolean(KEY_NO_MORE, false)) {
            noMoreData();
        }
    }

    @Override
    protected void saveData(Bundle outState) {
        if (mAdapter != null && mAdapter.getSpreeList() != null && mAdapter.getSpreeList().size() != 0) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
            outState.putString(KEY_UID, IdentityHelper.getUid(FreeStoreApp.getContext()));
            outState.putParcelableArrayList(KEY_LIST_DATA, mAdapter.getSpreeList());
        }
    }


    private void getAllSpreeData() {
        String url = JsonUrl.getJsonUrl().JSON_URL_ALL_SPREE_LIST + "?currentPage=" + currentPage + "&number=10";
        FSRequestHelper.newGetRequest(url, TAG, SpreeModel.class, new IFDResponse<SpreeModel>() {
            @Override
            public void onSuccess(SpreeModel result) {
                resetRefreshUi();

                if (result != null) {
                    if (itemList == null) {
                        itemList = result.getItemList();
                    } else {
                        itemList.addAll(result.getItemList());
                    }
                    showView(itemList);

                    if (result.getPageInfo() != null) {
                        totalPage = result.getPageInfo().getTotalPageCount();
                    }

                    if (totalPage == 1) {
                        getListView().inflateEmptyView(0);
                    }

                    if (currentPage >= totalPage) {
                        noMoreData();
                    }

                    currentPage++;
                } else {
                    showView(itemList);
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
        }, false, true, new ExtendJsonUtil());
    }


    private void showView(ArrayList<SpreeGiftInfo> itemList) {
        if (mAdapter == null || itemList == null) {
            showEmpty();
            return;
        }

        if (mAdapter != null) {
            mAdapter.refreshData(itemList);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public void onLoadMore() {
        getAllSpreeData();
    }


    @Subscribe
    public void getGiftSuccess(SpreeGetSuccessEvent event) {
        if (mAdapter != null) {
            mAdapter.refreshData(event.getSpreeGiftInfo());
        }
    }

    @Subscribe
    public void getGiftSuccess(SpreeTaoSuccessEvent event) {
        if (mAdapter != null) {
            mAdapter.refreshTaoData(event.getSpreeGiftInfo());
        }
    }

    @Subscribe
    public void onUserLogin(UserLoginEvent event) {
        loadData();
    }

    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        loadData();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            return;
        }
        SpreeGiftInfo spreeGiftInfo = mAdapter.getItem(position - 1);
        if (spreeGiftInfo != null) {
            new SpreeDetailDialog(getActivity(), spreeGiftInfo).show();
        }
    }
}
