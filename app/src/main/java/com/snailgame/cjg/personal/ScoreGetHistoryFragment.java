package com.snailgame.cjg.personal;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.ScoreGetHistoryAdapter;
import com.snailgame.cjg.personal.model.ScoreGroupModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;


/*
 * 积分历史 - 全部
 * created by xixh 14-08-25
 */
public class ScoreGetHistoryFragment extends BaseHistoryFragment {
    public static final String TAG = ScoreGetHistoryFragment.class.getName();
    private ScoreGetHistoryAdapter mAdapter;
    private ScoreGroupModel mModel;

    private ArrayList<ScoreGroupModel.ModelItem> scoreGroupList;

    public static ScoreGetHistoryFragment getInstance() {
        ScoreGetHistoryFragment fragment = new ScoreGetHistoryFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_score_history;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new ScoreGetHistoryAdapter(getActivity(), null);
        loadMoreListView.setAdapter(mAdapter);
        loadMoreListView.enableLoadingMore(false);
        loadMoreListView.setPagePosition(0);
    }

    @Override
    protected void loadData() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop(ComUtil.dpToPx(120));
            showLoading();
        }
        String url = JsonUrl.getJsonUrl().JSON_URL_USER_SCORE_GROUP;
        createGetHistory(url);
    }

    @Override
    protected void fetchData() {

    }


    // 创建获取积分记录
    private void createGetHistory(String url) {
        FSRequestHelper.newGetRequest(url, TAG,
                ScoreGroupModel.class, new IFDResponse<ScoreGroupModel>() {
                    @Override
                    public void onSuccess(ScoreGroupModel model) {
                        mModel = model;
                        resetRefreshUi();
                        if (mModel == null || mModel.getCode() != 0) {
                            // 异常
                            if (mAdapter.getCount() == 0)
                                showError();
                            else {
                                Toast.makeText(getActivity(), getString(R.string.data_loaded_fail), Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        if (ListUtils.isEmpty(mModel.getItemList())) {
                            if (mAdapter.getCount() == 0) {
                                getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.score_history_get_null));
                                showEmpty();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.data_loaded), Toast.LENGTH_LONG).show();
                            }

                            return;
                        }

                        if (scoreGroupList == null)
                            scoreGroupList = new ArrayList<ScoreGroupModel.ModelItem>();
                        scoreGroupList.addAll(model.getItemList());
                        mAdapter.refreshData(scoreGroupList);

                        inflateEmptyView(mAdapter.getCount());
                        loadMoreListView.onNoMoreData();
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

    void inflateEmptyView(int size) {
        //如果totalPage>1不需要考虑填充空白
            Resources resources = getResources();
            int itemHeight = (int) resources.getDimension(R.dimen.item_app_height);    //积分列表的高度固定的
            int actionbarHeight = (int) resources.getDimension(R.dimen.dimen_48dp); //actionbar的高度 高度固定
            int statusbarHeight = (int) resources.getDimension(R.dimen.dimen_25dp); //statusbar的高度 高度固定

            loadMoreListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight() -
                    itemHeight * size - actionbarHeight - statusbarHeight - headerHight - ComUtil.dpToPx(50));
    }
    @Override
    protected void saveData(Bundle outState) {
        super.saveData(outState);
        outState.putParcelableArrayList(KEY_HISTORY_LIST, scoreGroupList);
        outState.putBoolean(KEY_SAVE, true);
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        super.restoreData(savedInstanceState);
        ArrayList<ScoreGroupModel.ModelItem> list = savedInstanceState.getParcelableArrayList(KEY_HISTORY_LIST);
        mAdapter.refreshData(list);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }


}
