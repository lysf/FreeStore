package com.snailgame.cjg.friend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.adapter.FriendAccountSearchAdapter;
import com.snailgame.cjg.friend.model.ContactModel;
import com.snailgame.cjg.friend.model.FriendSearchModel;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;

/**
 * Created by TAJ_C on 2016/5/18.
 */
public class FriendAccountSearchFragment extends AbsBaseFragment {

    public static final int FLAG_SEARCH_ACCOUNT = 0;

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;

    private List<ContactModel> friendList;
    private FriendAccountSearchAdapter mAdapter;

    private String accountName;
    private static final String KEY_INPUT_ACCOUNT = "key_input_account";


    public static Fragment getInstance(String accountName) {
        FriendAccountSearchFragment fragment = new FriendAccountSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_INPUT_ACCOUNT, accountName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
        isLoadinUserVisibile = false;
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
    protected void handleIntent() {
        accountName = getArguments().getString(KEY_INPUT_ACCOUNT);
    }

    @Override
    protected void initView() {
        loadMoreListView.enableLoadingMore(false);
        mAdapter = new FriendAccountSearchAdapter(getActivity(), friendList);
        loadMoreListView.setAdapter(mAdapter);

        View headerView = new View(getActivity());
        headerView.setLayoutParams(new AbsListView.LayoutParams(1,ComUtil.dpToPx(8)));
        loadMoreListView.addHeaderView(headerView);
    }


    @Override
    protected void loadData() {
        showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_FRIEND_SEARCH_LIST + "?searchFlag=" + FLAG_SEARCH_ACCOUNT + "&content=" + accountName;
        FSRequestHelper.newGetRequest(url, TAG, FriendSearchModel.class, new IFDResponse<FriendSearchModel>() {
            @Override
            public void onSuccess(FriendSearchModel friendSearchModel) {
                resetRefreshUi();
                getEmptyView().setEmptyMessage(getString(R.string.friend_search_none));

                if (friendSearchModel != null) {
                    if (ListUtils.isEmpty(friendSearchModel.getRecommendList())) {
                        showEmpty();
                    } else {
                        friendList = friendSearchModel.getRecommendList();
                        mAdapter.refreshData(friendList);
                    }


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
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void handleFriend(FriendHandleEvent event) {
        if (event != null && event.getResult() != null && event.getResult().getCode() == 0) {
            if (mAdapter != null && event.getHandle() == FriendHandleUtil.FRIEND_HANDLE_ADD) {
                mAdapter.applyFriend(event.getFriend());

            }
        }
    }
}
