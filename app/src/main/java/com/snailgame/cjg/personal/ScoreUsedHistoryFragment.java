package com.snailgame.cjg.personal;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.ScoreUsedHistoryAdapter;
import com.snailgame.cjg.personal.model.ScoreHistoryModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;

/**
 * Created by TAJ_C on 2015/11/10.
 */
public class ScoreUsedHistoryFragment extends BaseHistoryFragment {
    static final String TAG = ScoreGetHistoryFragment.class.getName();
    private ScoreUsedHistoryAdapter mAdapter;
    private ScoreHistoryModel mModel;

    private int mCurPage = 1;
    private int mTotalPage = 1;
    private ArrayList<ScoreHistoryModel.ModelItem> scoreHistoryList;

    public static ScoreUsedHistoryFragment getInstance() {
        ScoreUsedHistoryFragment fragment = new ScoreUsedHistoryFragment();
        return fragment;
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
        mAdapter = new ScoreUsedHistoryAdapter(getActivity(), scoreHistoryList);
        loadMoreListView.setAdapter(mAdapter);
        loadMoreListView.setPadding(0, 0, 0, ComUtil.dpToPx(50));
        loadMoreListView.setBackgroundColor(ResUtil.getColor(R.color.white));
        loadMoreListView.enableLoadingMore(false);
        loadMoreListView.setPagePosition(1);
    }


    // 获取数据
    @Override
    protected void fetchData() {
        if (mCurPage > mTotalPage) {    // 已加载完成
            loadMoreListView.onNoMoreData();
            return;
        }


        String url = JsonUrl.getJsonUrl().JSON_URL_USER_SCORE_HISTORY + "?iIntegralType=1"
                + "&currentPage=" + mCurPage;
        createGetHistory(url);
    }


    // 创建获取积分记录
    private void createGetHistory(String url) {
        FSRequestHelper.newGetRequest(url, TAG,
                ScoreHistoryModel.class, new IFDResponse<ScoreHistoryModel>() {
                    @Override
                    public void onSuccess(ScoreHistoryModel model) {
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
                                getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.score_history_used_null));
                                showEmpty();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.data_loaded), Toast.LENGTH_LONG).show();
                            }
                            mTotalPage = mCurPage;
                            return;
                        }

                        if (mModel.getPageInfo() != null) {
                            mTotalPage = mModel.getPageInfo().getTotalPageCount();
                            if (mTotalPage == 1)
                                loadMoreListView.onNoMoreData();
                        }

                        if (scoreHistoryList == null) {
                            scoreHistoryList = new ArrayList<ScoreHistoryModel.ModelItem>();
                        }

                        scoreHistoryList.addAll(model.getItemList());
                        mAdapter.refreshData(scoreHistoryList);

                        inflateEmptyView(mAdapter.getCount());
                        mCurPage++;
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
        if (mTotalPage == 1) {
            Resources resources = getResources();
            int itemHeight = (int) resources.getDimension(R.dimen.item_app_height);    //积分列表的高度固定的
            int actionbarHeight = (int) resources.getDimension(R.dimen.dimen_48dp); //actionbar的高度 高度固定
            int statusbarHeight = (int) resources.getDimension(R.dimen.dimen_25dp); //statusbar的高度 高度固定

            loadMoreListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight() -
                    itemHeight * size - actionbarHeight - statusbarHeight - headerHight - ComUtil.dpToPx(50));
        }
    }

    @Override
    protected void saveData(Bundle outState) {
        super.saveData(outState);
        outState.putParcelableArrayList(KEY_HISTORY_LIST, scoreHistoryList);
        outState.putBoolean(KEY_SAVE, true);
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        super.restoreData(savedInstanceState);
        ArrayList<ScoreHistoryModel.ModelItem> list = savedInstanceState.getParcelableArrayList(KEY_HISTORY_LIST);
        mAdapter.refreshData(list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }


}
