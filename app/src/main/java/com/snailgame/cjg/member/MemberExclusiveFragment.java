package com.snailgame.cjg.member;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.MemberChangeEvent;
import com.snailgame.cjg.member.adapter.MemberExclusiveSpreeAdapter;
import com.snailgame.cjg.member.model.MemberArticle;
import com.snailgame.cjg.member.model.MemberPrivilege;
import com.snailgame.cjg.member.model.MemberSpreeResultModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 会员专享
 * Created by TAJ_C on 2015/12/24.
 */
public class MemberExclusiveFragment extends AbsBaseFragment {
    private static final String TAG = MemberExclusiveFragment.class.getSimpleName();

    @Bind(R.id.content)
    LoadMoreListView listView;

    private ArrayList<MemberArticle> articleList;
    private MemberExclusiveSpreeAdapter exclusiveSpreeAdapter;

    private int currentLevelId = 0;
    private int needLevelId;
    private int privilegeId;

    public static final String KEY_SPREE_LIST = "key_spree_list";

    public static MemberExclusiveFragment getInstance() {
        MemberExclusiveFragment fragment = new MemberExclusiveFragment();
        return fragment;
    }


    @Subscribe
    public void memberInfoChanged(MemberChangeEvent event) {
        loadData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoadinUserVisibile = false;
        MainThreadBus.getInstance().register(this);
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
        View spaceView = new View(getActivity());
        spaceView.setLayoutParams(new AbsListView.LayoutParams(ComUtil.dpToPx(1), ComUtil.dpToPx(8)));
        listView.addHeaderView(spaceView);
    }

    @Override
    protected void loadData() {
        showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_MEMBER_USER_PRIVILEGE_TYPE + "?cType=" + MemberDetailActivity.TYPE_EXCLUSIVE_SPREE;
        FSRequestHelper.newGetRequest(url, TAG, MemberPrivilege.class, new IFDResponse<MemberPrivilege>() {
            @Override
            public void onSuccess(MemberPrivilege result) {
                resetRefreshUi();
                if (result != null && result.getItem() != null) {
                    privilegeId = result.getItem().getPrivilegeId();
                    needLevelId = result.getItem().getNeedLevelId();
                    if (result.getItem().getLevel() != null) {
                        currentLevelId = result.getItem().getLevel().getLevelId();
                    }
                    articleList = result.getItem().getArticleList();
                    exclusiveSpreeAdapter = new MemberExclusiveSpreeAdapter(getActivity(), currentLevelId, needLevelId, result.getItem().getConfig(), articleList);
                    exclusiveSpreeAdapter.setOnGoodsGetListener(new GoodsGetActionListener());
                    listView.setAdapter(exclusiveSpreeAdapter);

                    if (articleList == null || articleList.size() == 0) {
                        showEmpty();
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
    protected LoadMoreListView getListView() {
        return listView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        articleList = savedInstanceState.getParcelableArrayList(KEY_SPREE_LIST);
        if (exclusiveSpreeAdapter != null) {
            exclusiveSpreeAdapter.refreshData(articleList);
        }

    }

    @Override
    protected void saveData(Bundle outState) {
        if (articleList != null && articleList.size() != 0) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putParcelableArrayList(KEY_SPREE_LIST, articleList);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * 领取物品
     */
    class GoodsGetActionListener implements GoodsGetListener {


        @Override
        public void getGoodsRequest(final int articeId,int levelId, int goodsId) {

            Map<String, String> parmas = new HashMap<>();
            parmas.put("iPrivilegeId", String.valueOf(privilegeId));
            parmas.put("iGoodsId", String.valueOf(goodsId));
            FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_MEMBER_USER_DRAW, TAG, MemberSpreeResultModel.class, new IFDResponse<MemberSpreeResultModel>() {
                @Override
                public void onSuccess(MemberSpreeResultModel result) {
                    if (result != null) {
                        if (result.getCode() == 0) {
                            if (exclusiveSpreeAdapter != null) {
                                ToastUtils.showMsg(getActivity(), getResources().getString(R.string.personal_task_receive_success));
                                exclusiveSpreeAdapter.refreshData(articeId, result.getItem());
                            }

                        } else {
                            ToastUtils.showMsg(getActivity(), result.getMsg());
                        }
                    } else {
                        ToastUtils.showMsg(getActivity(), "UNKNOWN_ERROR");
                    }
                }

                @Override
                public void onNetWorkError() {
                    ToastUtils.showMsg(getActivity(), getResources().getString(R.string.spree_get_error));
                }

                @Override
                public void onServerError() {
                    ToastUtils.showMsg(getActivity(), getResources().getString(R.string.spree_get_error));
                }
            }, parmas);
        }
    }

}
