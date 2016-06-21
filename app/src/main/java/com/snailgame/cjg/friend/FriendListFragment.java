package com.snailgame.cjg.friend;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.BaseLoadingEmptyFragment;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.adapter.FriendListAdapter;
import com.snailgame.cjg.friend.model.FriendListModel;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.widget.FullListView;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.zhy.base.loadandretry.LoadingAndRetryManager;
import third.com.zhy.base.loadandretry.OnLoadingAndRetryListener;

/**
 * 朋友主界面
 * Created by TAJ_C on 2016/5/10.
 */
public class FriendListFragment extends BaseLoadingEmptyFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.lv_friend_apply)
    FullListView friendApplyView;
    @Bind(R.id.lv_friend_list)
    FullListView myfriendListView;
    @Bind(R.id.tv_friend_apply_title)
    View applyTitleView;
    @Bind(R.id.tv_my_friend_title)
    View myFriendTitleView;

    @Bind(R.id.content_container)
    View contentContainer;

    FriendListAdapter mFriendApplyAdapter;
    FriendListAdapter myFriendAdapter;

    private EmptyListener mListener;

    public static final int TAG_APPLY = 0;
    public static final int TAG_MYFRIEND = 1;

    public static Fragment getInstance() {
        FriendListFragment fragment = new FriendListFragment();
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


        mFriendApplyAdapter = new FriendListAdapter(getActivity(), TAG_APPLY, null);
        myFriendAdapter = new FriendListAdapter(getActivity(), TAG_MYFRIEND, null);

        friendApplyView.setAdapter(mFriendApplyAdapter);
        myfriendListView.setAdapter(myFriendAdapter);
        myfriendListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_main;
    }

    @Override
    protected void loadData() {
        showLoading();
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_FRIEND_MYFRIENDS, TAG, FriendListModel.class, new IFDResponse<FriendListModel>() {
            @Override
            public void onSuccess(FriendListModel friendListModel) {
                if (friendListModel != null && friendListModel.getItem() != null) {
                    FriendListModel.ModelItem item = friendListModel.getItem();

                    showContent();
                    mFriendApplyAdapter.refreshData(item.getApplyList());
                    myFriendAdapter.refreshData(item.getFriendList());
                    setTitleView();
                } else {
                    showEmpty();
                    if (mListener != null) {
                        mListener.showRecommendView();
                    }
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
        }, false);
    }

    private void setTitleView() {
        if (mFriendApplyAdapter.getCount() == 0 && myFriendAdapter.getCount() == 0) {
            if (mListener != null) {
                mListener.showRecommendView();
            }
        }

        myFriendTitleView.setVisibility(myFriendAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
        applyTitleView.setVisibility(mFriendApplyAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void handleFriend(FriendHandleEvent event) {
        if (event != null && event.getResult() != null && event.getResult().getCode() == 0) {
            if (mFriendApplyAdapter == null || myFriendAdapter == null) {
                return;
            }
            switch (event.getHandle()) {
                case FriendHandleUtil.FRIEND_HANDLE_RECEIVE:
                    mFriendApplyAdapter.removeFriend(event.getFriend());
                    myFriendAdapter.addFriend(event.getFriend());
                    break;

                case FriendHandleUtil.FRIEND_HANDLE_REJECT:
                    mFriendApplyAdapter.removeFriend(event.getFriend());
                    break;
                case FriendHandleUtil.FRIEND_HANDLE_DEL:
                    myFriendAdapter.removeFriend(event.getFriend());
                    break;

                case FriendHandleUtil.FRIEND_HANDLE_APPLY:
                    mFriendApplyAdapter.addFriend(event.getFriend());
                    break;

                default:
                    break;
            }

            setTitleView();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (EmptyListener) activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myFriendAdapter != null && myFriendAdapter.getItem(position) != null) {
            startActivity(FriendGamesActivity.newIntent(getActivity(), myFriendAdapter.getItem(position)));
//            getActivity().overridePendingTransition(R.anim.activity_slide_in_bottom, 0);
        }
    }

    public interface EmptyListener {
        public void showRecommendView();
    }

}
