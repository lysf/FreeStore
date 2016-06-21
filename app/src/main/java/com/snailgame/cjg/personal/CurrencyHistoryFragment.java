package com.snailgame.cjg.personal;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.CurrencyHistoryAdapter;
import com.snailgame.cjg.personal.model.CurrencyHistoryModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;

/**
 * 兔兔币记录(包含充值和使用) fragment
 * Created by TAJ_C on 2015/4/20.
 */
public class CurrencyHistoryFragment extends BaseHistoryFragment {
    static String TAG = CurrencyHistoryFragment.class.getName();
    private CurrencyHistoryAdapter mAdapter;
    private CurrencyHistoryModel mModel;

    private int mCurPage = 1;
    private int mTotalPage = 1;
    int mType;

    private static final String KEY_CUR_PAGE = "key_cur_page";
    private static final String KEY_TOTAL_PAGE = "key_total_page";
    private static final String KEY_HISTORY_LIST = "key_history_list";

    public static CurrencyHistoryFragment getInstance(int type) {
        CurrencyHistoryFragment fragment = new CurrencyHistoryFragment();
        Bundle arg = new Bundle();
        arg.putInt(VALUE_HISTORY_TYPE, type);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mType = bundle.getInt(VALUE_HISTORY_TYPE, HISTORY_TYPE_GETED);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new CurrencyHistoryAdapter(getActivity(), null);
        loadMoreListView.setAdapter(mAdapter);
        loadMoreListView.setPadding(0, 0, 0, ComUtil.dpToPx(50));
        loadMoreListView.setPagePosition(mType == HISTORY_TYPE_GETED ? 0 : 1);

    }


    @Override
    protected void fetchData() {
        if (mCurPage > mTotalPage) {    // 已加载完成
            loadMoreListView.onNoMoreData();
            return;
        }

        String url = JsonUrl.getJsonUrl().JSON_URL_USER_CURRENCY_HISTORY
                + "?currentPage=" + mCurPage + "&nUserId=" + IdentityHelper.getUid(getActivity());
        switch (mType) {
            case HISTORY_TYPE_GETED:
                url = url + "&cType=2,3";
                break;
            case HISTORY_TYPE_USED:
                url = url + "&cType=1";
                break;
            default:
                break;
        }

        createGetHistory(url);
    }


    // 创建获取积分记录
    private void createGetHistory(String url) {
        FSRequestHelper.newGetRequest(url, TAG, CurrencyHistoryModel.class, new IFDResponse<CurrencyHistoryModel>() {
            @Override
            public void onSuccess(CurrencyHistoryModel result) {
                mModel = result;
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
                        if (mType == HISTORY_TYPE_GETED) {
                            getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.currency_history_charge_null));
                        } else if (mType == HISTORY_TYPE_USED) {
                            getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.currency_history_used_null));
                        }
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

                inflateEmptyView(mModel.getItemList().size());
                mAdapter.addData(mModel.getItemList());
                mAdapter.notifyDataSetChanged();
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
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        mCurPage = savedInstanceState.getInt(KEY_CUR_PAGE);
        mTotalPage = savedInstanceState.getInt(KEY_TOTAL_PAGE);
        ArrayList<CurrencyHistoryModel.ModelItem> list = savedInstanceState.getParcelableArrayList(KEY_HISTORY_LIST);
        mAdapter.refreshData(list);
        if (savedInstanceState.getBoolean(KEY_NO_MORE, false)) {
            noMoreData();
        }

    }

    @Override
    protected void saveData(Bundle outState) {
        if (mAdapter != null && mAdapter.getCount() != 0) {
            outState.putInt(KEY_CUR_PAGE, mCurPage);
            outState.putInt(KEY_TOTAL_PAGE, mTotalPage);
            outState.putParcelableArrayList(KEY_HISTORY_LIST, mAdapter.getLists());
            outState.putBoolean(KEY_SAVE, true);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
        }

    }
}
