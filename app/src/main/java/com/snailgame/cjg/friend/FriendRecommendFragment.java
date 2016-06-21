package com.snailgame.cjg.friend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.GridView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.BaseLoadingEmptyFragment;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.adapter.RecommendFriendAdatper;
import com.snailgame.cjg.friend.model.FriendsRecommendModel;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import third.com.zhy.base.loadandretry.LoadingAndRetryManager;
import third.com.zhy.base.loadandretry.OnLoadingAndRetryListener;

/**
 * 朋友推荐页面
 * Created by TAJ_C on 2016/5/10.
 */
public class FriendRecommendFragment extends BaseLoadingEmptyFragment {

    @Bind(R.id.gv_recommend_friend)
    GridView recommendFriendView;

    @Bind(R.id.content_container)
    View contentContainer;

    RecommendFriendAdatper mAdapter;


    public static Fragment getInstance() {
        FriendRecommendFragment fragment = new FriendRecommendFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        mLoadingAndRetryManager = LoadingAndRetryManager.generate(contentContainer, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                loadData();
            }
        });
        mLoadingAndRetryManager.setEmptyMessage(getString(R.string.friend_recommend_none_hint));
        mAdapter = new RecommendFriendAdatper(getActivity(), null);
        recommendFriendView.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        showLoading();
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_FRIEND_RECOMMEND_FRIENDS, TAG, FriendsRecommendModel.class, new IFDResponse<FriendsRecommendModel>() {
            @Override
            public void onSuccess(FriendsRecommendModel friendsRecommendModel) {
                if (friendsRecommendModel != null && !ListUtils.isEmpty(friendsRecommendModel.getRecommendList()) && mAdapter != null) {
                    showContent();
                    mAdapter.refreshData(friendsRecommendModel.getRecommendList());
                } else {
                    showEmpty();
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


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_recommend;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void handleFriend(FriendHandleEvent event) {
        if (event != null && event.getResult() != null && event.getResult().getCode() == 0) {
            if (event.getHandle() == FriendHandleUtil.FRIEND_HANDLE_ADD) {
                mAdapter.removeFriend(event.getFriend());
                if (mAdapter.getCount() == 0) {
                    setEmptyMessage(getString(R.string.friend_recommend_add_none));
                    showEmpty();
                }
            }
        }
    }
}
