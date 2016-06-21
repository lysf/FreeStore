package com.snailgame.cjg.detail;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.common.widget.SpreeDetailDialog;
import com.snailgame.cjg.detail.adapter.DetailFragmentAdapter;
import com.snailgame.cjg.detail.adapter.GameSpreeAdapter;
import com.snailgame.cjg.detail.model.ScrollYEvent;
import com.snailgame.cjg.detail.model.SpreeState;
import com.snailgame.cjg.event.GetGiftPostEvent;
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.SpreeModel;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import third.scrolltab.ScrollTabHolderFragment;

/**
 * Created by sunxy on 2015/5/4.
 */
public class GameSpreeFragment extends ScrollTabHolderFragment implements AdapterView.OnItemClickListener {
    static String TAG = GameSpreeFragment.class.getName();
    private final int NO_ORDER = 0, ORDERED = 1;
    private static final String KEY_HEADER_HEIGHT = "header_height";
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.btn_order_spree)
    TextView mOrderSpree;
    private int appId;
    private int mHeaderHeight;
    private int scrollHeight;
    private ArrayList<String> mKeyArray;
    private ArrayList<SpreeGiftInfo> spreeLists;
    private GameSpreeAdapter adapter;
    private boolean isEmpty = false;
    private int orderState = NO_ORDER;
    private boolean ordering = false;
    private int headerHeight;
    private int currentAdjustScroll;

    public static GameSpreeFragment getInstance(int appId, int mHeaderHeight) {
        GameSpreeFragment fragment = new GameSpreeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_APP_ID, appId);
        bundle.putInt(KEY_HEADER_HEIGHT, mHeaderHeight);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.detail_header_translate_height);//header可以滑动的最大高度
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.game_spree_layout;
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            appId = bundle.getInt(AppConstants.KEY_APP_ID);
            mHeaderHeight = bundle.getInt(KEY_HEADER_HEIGHT);
            mKeyArray = new ArrayList<>();
        }
    }

    @Override
    protected void initView() {
        spreeLists = new ArrayList<>();
        adapter = new GameSpreeAdapter(spreeLists, getActivity());

        View placeHolder = new View(getActivity());
        placeHolder.setBackgroundColor(getResources().getColor(R.color.common_window_bg));
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight);

        placeHolder.setLayoutParams(params);

        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        loadMoreListView.onNoMoreData();
        loadMoreListView.addHeaderView(placeHolder);

        loadMoreListView.setScrollHolder(mScrollTabHolder);
        loadMoreListView.setOnItemClickListener(this);
        loadMoreListView.setPagePosition(DetailFragmentAdapter.FRAGMENT_SPREE);

        loadMoreListView.setAdapter(adapter);
        loadMoreListView.setDivider(null);
        initOrderState();
    }


    @OnClick(R.id.btn_order_spree)
    public void orderNewSpree() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            if (ordering) {
                ToastUtils.showMsg(getActivity(), R.string.double_click_alert);
                return;
            }

            if (orderState == ORDERED) {
                cancleOrderSpree();
            } else {
                MobclickAgent.onEvent(getActivity(), UmengAnalytics.EVENT_APPOINT_NEW_SPREE);
                order();
            }
        } else {
            AccountUtil.userLogin(getActivity());
        }
    }

    /**
     * 预约礼包
     */
    private void order() {
        ordering = true;
        HashMap<String, String> params = new HashMap<>(1);
        params.put("iGameId", String.valueOf(appId));
        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_ORDER_SPREE, TAG, SpreeState.class, new IFDResponse<SpreeState>() {
            @Override
            public void onSuccess(SpreeState result) {
                ordering = false;
                if (result != null && result.getCode() == 0) {
                    ToastUtils.showMsg(getActivity(), R.string.order_success);
                    orderState = ORDERED;
                    mOrderSpree.setText(getString(R.string.spree_ordered));
                    mOrderSpree.setBackgroundResource(R.drawable.btn_red_selector);
                } else {
                    ToastUtils.showMsg(getActivity(), R.string.order_fail);
                }
            }

            @Override
            public void onNetWorkError() {
                ordering = false;
            }

            @Override
            public void onServerError() {
                ordering = false;
            }
        }, params);

    }

    /**
     * 查询预约礼包
     */
    private void initOrderState() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            ordering = true;
            FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_QUERY_STATE + "?iGameId=" + appId, TAG, SpreeState.class, new IFDResponse<SpreeState>() {
                @Override
                public void onSuccess(SpreeState result) {
                    ordering = false;
                    if (result != null) {
                        if (result.isVal()) {
                            orderState = ORDERED;
                            mOrderSpree.setText(getString(R.string.spree_ordered));
                            mOrderSpree.setBackgroundResource(R.drawable.btn_red_selector);
                        } else {
                            orderState = NO_ORDER;
                            mOrderSpree.setText(getString(R.string.order_new_spree));
                            mOrderSpree.setBackgroundResource(R.drawable.btn_yellow_selector);
                        }
                    }
                }

                @Override
                public void onNetWorkError() {
                    ordering = false;
                }

                @Override
                public void onServerError() {
                    ordering = false;
                }
            }, false);
        } else {
//            AccountUtil.userLogin(getActivity());
        }

    }

    /**
     * 取消预约礼包
     */
    private void cancleOrderSpree() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            ordering = true;
            HashMap<String, String> params = new HashMap<>(1);
            params.put("iGameId", String.valueOf(appId));
            FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_CANCLEORDER_SPREE, TAG, BaseDataModel.class, new IFDResponse<BaseDataModel>() {
                @Override
                public void onSuccess(BaseDataModel result) {
                    ordering = false;
                    if (result != null && result.getCode() == 0) {
                        ToastUtils.showMsg(getActivity(), R.string.cancle_order_success);
                        orderState = NO_ORDER;
                        mOrderSpree.setText(getString(R.string.order_new_spree));
                        mOrderSpree.setBackgroundResource(R.drawable.btn_yellow_selector);
                    } else {
                        ToastUtils.showMsg(getActivity(), R.string.cancle_order_fail);
                    }
                }

                @Override
                public void onNetWorkError() {
                    ordering = false;
                }

                @Override
                public void onServerError() {
                    ordering = false;
                }
            }, params);
        }

    }

    @Override
    protected void loadData() {
        customAndShowLoading();
        getGameSpree();
    }

    private void showNothing() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop((mHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.dimen_60dp)) / 2);
            getEmptyView().showNothing();
            loadMoreListView.enableEmptyViewScrollable();
            scrollToPositionWhenEmtpy(scrollHeight);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showNothing();
    }

    private void customAndShowLoading() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop((mHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.dimen_60dp)) / 2);
            showLoading();
        }
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void saveData(Bundle outState) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onUserInfoChange(UserInfoChangeEvent event) {
        if (LoginSDKUtil.isLogined(getActivity())) {
            //刷新礼包
            getGameSpree();
            initOrderState();
        }
    }

    // 获取游戏礼包
    private void getGameSpree() {
        String url = JsonUrl.getJsonUrl().JSON_URL_APP_SPREE + "?iAppId=" + appId + "&number=" + Integer.MAX_VALUE;
        FSRequestHelper.newGetRequest(url, TAG, SpreeModel.class, new IFDResponse<SpreeModel>() {
            @Override
            public void onSuccess(SpreeModel result) {
                if (result == null || ListUtils.isEmpty(result.getItemList())) {
                    showGameSpreeEmpty();
                    scrollToPositionWhenEmtpy(scrollHeight);
                    return;
                }
                if (spreeLists != null && adapter != null) {
                    spreeLists.clear();
                    spreeLists.addAll(result.getItemList());
                    adapter.notifyDataChanged();
                    loadMoreListView.inflateEmptyView(getNeedInflateEmptyViewHeight(spreeLists.size()));
                    scrollToPosition(scrollHeight);
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

    /**
     * 获取需要填充的空白view高度
     */
    private int getNeedInflateEmptyViewHeight(int itemCount) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && itemCount <= 5) {
            Resources resources = getResources();
            return (int) PhoneUtil.getScreenHeight() - resources.getDimensionPixelSize(R.dimen.tab_height) - resources.getDimensionPixelSize(R.dimen.actionbar_height);
        }
        return 0;
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (adapter != null && adapter.getCount() > 0)
            scrollToPosition(scrollHeight);
        else {
            scrollToPositionWhenEmtpy(scrollHeight);
        }

        this.scrollHeight = scrollHeight;
    }

    private void scrollToPosition(int scrollHeight) {
        if ((scrollHeight == 0 && loadMoreListView.getFirstVisiblePosition() >= 1)
                || (topScroll == -headerHeight && currentAdjustScroll == scrollHeight && scrollHeight == mHeaderHeight - headerHeight)) {
            return;
        }
        loadMoreListView.setSelectionFromTop(1, scrollHeight);
        currentAdjustScroll = scrollHeight;
    }

    private void scrollToPositionWhenEmtpy(int scrollHeight) {
        if (scrollHeight == 0) {
            return;
        }
        loadMoreListView.setSelectionFromTop(1, scrollHeight);
    }

    private void showGameSpreeEmpty() {
        isEmpty = true;
        showEmpty();
        loadMoreListView.enableEmptyViewScrollable();
    }

    @Subscribe
    public void getGiftPost(GetGiftPostEvent event) {
        if (mKeyArray == null)
            mKeyArray = new ArrayList<>();
        SpreeUtils.getSpreeAction(getActivity(), mKeyArray, event.getSpreeInfo(), TAG);
    }


    @Subscribe
    public void spreeGetSuccess(SpreeGetSuccessEvent event) {
        adapter.changeSpreeState(event.getSpreeGiftInfo());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0 && adapter.getCount() > 0) {
            SpreeGiftInfo spreeGiftInfo = adapter.getItem(position - 1);
            if (spreeGiftInfo != null) {
                new SpreeDetailDialog(getActivity(), spreeGiftInfo).show();
            }
        }
    }

    int topScroll;

    @Subscribe
    public void scrollY(ScrollYEvent event) {
        topScroll = event.getScrollY();
        if (getUserVisibleHint()) {
            currentAdjustScroll = mHeaderHeight - Math.abs(topScroll);
        }
        EmptyView emptyView = getEmptyView();
        if (emptyView != null) {
            emptyView.setEmptyViewScroll(event.getScrollY() / 2);
        }
    }

}
