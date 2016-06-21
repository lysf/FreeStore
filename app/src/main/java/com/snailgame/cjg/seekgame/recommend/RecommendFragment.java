package com.snailgame.cjg.seekgame.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AppListFragment;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekapp.SeekAppAdapter;
import com.snailgame.cjg.seekgame.SeekGameAdapter;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;
import com.snailgame.cjg.seekgame.recommend.adapter.RecommendHeadAdapter;
import com.snailgame.cjg.seekgame.recommend.model.RecommendInfo;
import com.snailgame.cjg.seekgame.recommend.model.RecommendModel;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * 推荐Fragment 继承于AppListFragment
 * 父类主要处理 当应用 信息变化(如下载)，更新列表
 * Created by TAJ_C on 2014/10/27.
 */
public class RecommendFragment extends AppListFragment {
    private final static String KEY_URL = "key_url";
    private static final String TAG = RecommendFragment.class.getSimpleName();
    private RecommendModel mRecommendModel = new RecommendModel();

    private RecommendHeadAdapter mViewPagerAdapter;
    private LinearLayout mRecommendHeader;
    private ArrayList<RecommendInfo> infos = new ArrayList<>();

    private final static String KEY_RECOMMEND_MODEL = "key_recommend_model";
    private final static String KEY_RECOMMEND_INFO = "key_recommend_info";
    private static final String MANUL_CHANGE_USER_VISIBLE = "key_change_user_visible";
    private boolean headerRefreshed = false;

    private String url;
    private static final String TAG_COLLECT_SORE = "2"; //表示合集分类

    /**
     * @param url   链接
     * @param route PV用路径记载
     * @return
     */
    public static RecommendFragment getInstance(String url, boolean isManulChangeUserVisible, int[] route) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        bundle.putBoolean(MANUL_CHANGE_USER_VISIBLE, isManulChangeUserVisible);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        initRecommendHeader();

        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
        appInfoAdapter = new CommonListItemAdapter(mContext, appInfoLists, AppConstants.VALUE_RECOMMEND, route);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.setOnItemClickListener(this);
        loadMoreListView.setAdapter(appInfoAdapter);
        handleArguments();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
            if (bundle.getBoolean(MANUL_CHANGE_USER_VISIBLE))    //只有需要手动控制isLoadinUserVisibile 才会改变isLoadinUserVisibile为false
                isLoadinUserVisibile = false;
        }
    }


    @Subscribe
    public void onRecommentDownloadInfoChange(DownloadInfoChangeEvent event) {
        onDownloadInfoChange(event);
    }

    @Subscribe
    public void recommendAppFreeStateChanged(UserInfoLoadEvent event) {
        appFreeStateChanged(event);
    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {

        if (loadMoreListView != null) {

            if (event.getTabPosition() == MainActivity.TAB_SEEK_GAME &&
                    event.getPagePosition() == SeekGameAdapter.RECOMMEND &&
                    url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND)) {
                loadMoreListView.setSelection(0);
            }

            if (event.getTabPosition() == MainActivity.TAB_COMMUNICATION &&
                    event.getPagePosition() == SeekAppAdapter.PAGE_RECOMMEND &&
                    url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND)) {

                loadMoreListView.setSelection(0);
            }


        }

    }

    @Override
    protected void registerOtto() {
        //注册子类 解决父类无法收到otto事件的问题
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void unRegisterOtto() {
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected void loadData() {
        showLoading();
        loadRecommendData();
    }

    private void loadRecommendData() {

        FSRequestHelper.newGetRequest(url + nextPage + ".json", TAG, RecommendModel.class, new IFDResponse<RecommendModel>() {
            @Override
            public void onSuccess(RecommendModel result) {
                if (nextPage == 1) {
                    mRecommendModel = getRecommendModel(result);
                    if (mRecommendModel == null || ListUtils.isEmpty(mRecommendModel.listViewInfo)) {
                        showEmpty();
                    }
                } else {
                    if (nextPage > mRecommendModel.getPageInfo().getTotalPageCount()) {
                        return;
                    }

                    mRecommendModel.listViewInfo = getRecommendModel(result).listViewInfo;
                }

                if (!headerRefreshed) {
                    refreshRecommendHeadView();
                }
                synchronized (appInfoLists) {
                    AppInfoUtils.bindData(mContext, mRecommendModel.listViewInfo, appInfoLists, appInfoAdapter, TAG);
                }
                if (mRecommendModel.getPageInfo().getTotalPageCount() == 1) {
                    loadMoreListView.onNoMoreData();
                }
                nextPage++;
                resetRefreshStatus();
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

    /**
     * 初始化推荐头部， 一个viewpage 和 两个广告位
     */
    private void initRecommendHeader() {
        mRecommendHeader = (LinearLayout) View.inflate(FreeStoreApp.getContext(), R.layout.recommend_list_header, null);
        loadMoreListView.addHeaderView(mRecommendHeader);
    }

    @Override
    public void onLoadMore() {
        if (mRecommendModel == null || mRecommendModel.getPageInfo() == null) {
            noMoreData();
            return;
        }

        int totalPage = mRecommendModel.getPageInfo().getTotalPageCount();

        if (nextPage > totalPage) {
            noMoreData();
            return;
        }

        loadRecommendData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(url)) {
            if (url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND)) {
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_APP_RECOMMEND);
            } else if (url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND)) {
                MobclickAgent.onPageStart(UmengAnalytics.PAGE_GAME_RECOMMEND);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(url)) {
            if (url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND)) {
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_APP_RECOMMEND);
            } else if (url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND)) {
                MobclickAgent.onPageEnd(UmengAnalytics.PAGE_GAME_RECOMMEND);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    @Override
    protected void saveData(Bundle outState) {
        super.saveData(outState);
        if (mRecommendModel != null && infos != null && infos.size() != 0) {
            outState.putParcelable(KEY_RECOMMEND_MODEL, mRecommendModel);
            outState.putBoolean(KEY_SAVE, true);
            outState.putParcelableArrayList(KEY_RECOMMEND_INFO, infos);
        }

    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        super.restoreData(savedInstanceState);
        mRecommendModel = savedInstanceState.getParcelable(KEY_RECOMMEND_MODEL);
        infos = savedInstanceState.getParcelableArrayList(KEY_RECOMMEND_INFO);
        showView();
    }


    private void showView() {
        refreshRecommendHeadView();
        if (ListUtils.isEmpty(infos)) {
            showEmpty();
        }
    }

    public boolean isHasHeaderPagerData() {
        return mRecommendModel != null && mRecommendModel.viewPagerInfo != null && (mRecommendModel.viewPagerInfo.size() > 0);
    }


    public void startActivity(RecommendInfo recommendInfo, int[] route) {
        Intent intent = new Intent();
        int linkParam = (int) recommendInfo.getnParamId();
        int type = Integer.parseInt(recommendInfo.getcType());
        switch (RecommendType.valueOf(type)) {
            case RECOMMEND_DETAIL:
                intent = DetailActivity.newIntent(getActivity(), linkParam, route);
                break;
            case RECOMMEND_COLLECTION:
                intent = CollectionActivity.newIntent(getActivity(), linkParam, route);
                break;
            case RECOMMEND_HTML:
                intent = WebViewActivity.newIntent(getActivity(),
                        recommendInfo.getcHtmlUrl());
                break;
            default:
                break;
        }
        getActivity().startActivity(intent);
    }


    private void refreshRecommendHeadView() {
        if (mRecommendModel == null) return;

        if (isHasHeaderPagerData()) {
            infos = (ArrayList<RecommendInfo>) mRecommendModel.viewPagerInfo;
            int[] route = mRoute.clone();

            mViewPagerAdapter = new RecommendHeadAdapter(getActivity(), infos, url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND), route);
            GridView gridView = ButterKnife.findById(mRecommendHeader, R.id.recommend_header_gridview);
            gridView.setAdapter(mViewPagerAdapter);
        }

        headerRefreshed = true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(position);
        if (appInfo == null) return;


        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
        route[AppConstants.STATISTCS_DEPTH_FOUR] = position;
        RecommendInfo recommendInfo = new RecommendInfo();
        recommendInfo.setnParamId(appInfo.getAppId());
        recommendInfo.setcType(appInfo.getcType());
        recommendInfo.setcHtmlUrl(appInfo.getcHtmlUrl());
        recommendInfo.setAppName(appInfo.getAppName());
        startActivity(recommendInfo, route);
    }

    /**
     * RecommendModel 数据类
     *
     * @return
     */
    private RecommendModel getRecommendModel(RecommendModel model) {
        if (model != null) {

            //应用合集 将扩展字段赋值给描述
            for (RecommendInfo appInfo : model.allListInfo) {
                appInfo.setsAppDesc(appInfo.getsExtends());
                if (url.equals(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND)) {
                    appInfo.setcAppType(AppConstants.VALUE_TYPE_APP);
                } else if (url.equals(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND)) {
                    appInfo.setcAppType(AppConstants.VALUE_TYPE_GAME);
                }
            }
            model.setInfos(model.allListInfo);

        } else {
            model = new RecommendModel();
        }

        return model;
    }

}
