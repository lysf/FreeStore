package com.snailgame.cjg.home;

import android.content.res.Resources;
import android.os.Bundle;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.adapter.AppNewsAdapter;
import com.snailgame.cjg.home.model.AppNewsModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import butterknife.Bind;

/**
 * 资讯
 *
 * @author pancl
 */
public class AppNewsFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener {
    static String TAG = AppNewsFragment.class.getName();

    @Bind(R.id.lv_app_news)
    protected LoadMoreListView mListView;
    private AppNewsAdapter mAdapter;
    private long iRequestPageNum = 1;
    private long totalPage = 1;

    /**
     * @param route PV用路径记载
     * @return
     */
    public static AppNewsFragment getInstance(int[] route) {
        AppNewsFragment fragment = new AppNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoadinUserVisibile = false;
    }

      @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }

    @Override
    protected void initView() {
        mListView.enableLoadingMore(true);
        mListView.setLoadingListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_news_fragment;
    }

    /**
     * 获取资讯数据
     */
    private void getAppNewsInfo() {
        String url = JsonUrl.getJsonUrl().APP_NEWS_URL + iRequestPageNum + ".json";

        FSRequestHelper.newGetRequest(url, TAG, AppNewsModel.class, new IFDResponse<AppNewsModel>() {
            @Override
            public void onSuccess(AppNewsModel result) {
                resetRefreshUi();
                if (result == null || ListUtils.isEmpty(result.getItemList())) {
                    showEmpty();
                    return;
                }
                if (result.getPageInfo() != null) {
                    totalPage = result.getPageInfo().getTotalPageCount();
                }

                inflateEmptyView(result.getItemList().size());

                if (mAdapter == null) {
                    mAdapter = new AppNewsAdapter(getActivity(), result, mRoute);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.getListData().addAll(result.getItemList());
                    mAdapter.notifyDataSetChanged();
                }
                if (iRequestPageNum >= totalPage) {
                    noMoreData();
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
        }, true, true, new ExtendJsonUtil());
    }



    @Override
    protected void loadData() {
        if (getEmptyView() != null) {
            getEmptyView().setMarginTop(ComUtil.dpToPx(AppConstants.EMPTY_VIEW_MARGIN_TOP));
            showLoading();
        }
        getAppNewsInfo();
    }


    private void inflateEmptyView(int size) {
        //如果totalPage>1不需要考虑填充空白
        if (totalPage == 1) {
            Resources resources = getResources();
            int itemHeight = ComUtil.dpToPx(115);    //列表的高度固定的
            int actionbarHeight = (int) resources.getDimension(R.dimen.dimen_48dp); //actionbar的高度 高度固定
            int statusbarHeight = (int) resources.getDimension(R.dimen.dimen_25dp); //statusbar的高度 高度固定
            mListView.changeEmptyFooterHeight((int) PhoneUtil.getScreenHeight() - itemHeight * size - actionbarHeight - statusbarHeight);
        }
    }


    @Override
    protected LoadMoreListView getListView() {
        return mListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
    }

    @Override
    protected void saveData(Bundle outState) {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public void onLoadMore() {
        iRequestPageNum++;
        if (totalPage > 0 && (iRequestPageNum <= totalPage)) {
            getAppNewsInfo();
        } else {
            noMoreData();
        }
    }
}
