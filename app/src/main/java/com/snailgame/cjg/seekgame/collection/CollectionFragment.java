package com.snailgame.cjg.seekgame.collection;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.CollectionModel;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.ExpandableTextView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.FreeGameItemEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekgame.collection.adapter.CollectionAdapter;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.view.SimpleImageView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.commonsware.cwac.merge.MergeAdapter;
import third.scrolltab.ScrollTabHolder;

/**
 * 合集详情 fragment
 */
public class CollectionFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, ScrollTabHolder {
    private final static String KEY_URL = "key_url";
    private final static String KEY_MODEL = "key_model";
    private final static String KEY_PAGE = "key_page";

    private MergeAdapter mergeAdapter;

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.tv_detail_title)
    TextView tvTitle;
    @Bind(R.id.detail_actionbar_view)
    View actionbarView;
    private Drawable actionbarBgDrawable;

    private int nextPage = 1;
    private CollectionModel mCollectionModel = new CollectionModel();
    protected ArrayList<AppInfo> appInfoLists = new ArrayList<>();
    private String url;

    private View mHeaderView;
    private HeaderViewHolder headerViewHolder;
    private View mFirstAppView;
    private View mAppGridView;
    private CollectionAdapter mAdapter;
    private FirstAppViewHolder firstAppViewHolder;
    private QueryTaskListTask task;

    /**
     * @param url   链接
     * @param route PV用路径记载
     * @return
     */
    public static CollectionFragment getInstance(String url, int[] route) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (loadMoreListView != null && taskInfos != null) {
            synchronized (appInfoLists) {
                for (TaskInfo taskInfo : taskInfos) {
                    updateProgress(taskInfo, appInfoLists);
                }
            }

            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();

            if (firstAppViewHolder != null && !ListUtils.isEmpty(appInfoLists))
                setupFirstView(firstAppViewHolder, appInfoLists.get(0));
        }
    }

    @Subscribe
    public void appFreeStateChanged(UserInfoLoadEvent event) {
        if (!IdentityHelper.isLogined(getActivity()) && !ListUtils.isEmpty(appInfoLists)) {
            for (AppInfo appInfo : appInfoLists)
                appInfo.setcFlowFree(appInfo.getOriginCFlowFree());
        }

        //刷新list
        if (appInfoLists != null) {
            AppInfoUtils.updateAppFreeState(getActivity(), AppInfoUtils.getAllGameIds(appInfoLists), TAG);
        }
    }

    @Subscribe
    public void refreshAppListItem(FreeGameItemEvent event) {
        List<FreeGameItem> itemLists = event.getFreeGameItems();
        AppInfo appInfo;
        FreeGameItem freeGameItem;

        int length = appInfoLists.size();
        int itemLength = itemLists.size();

        for (int j = 0; j < length; j++) {
            appInfo = appInfoLists.get(j);
            appInfo.setcFlowFree(AppInfo.FREE_NULL);
            for (int i = 0; i < itemLength; i++) {
                freeGameItem = itemLists.get(i);
                if (appInfo.getPkgName().equals(freeGameItem.getcPackage()))
                    appInfo.setcFlowFree(freeGameItem.getcFlowFree());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_collection;
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(KEY_URL);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }

    @Override
    protected void initView() {
        initActionBar();

        mergeAdapter = new MergeAdapter();
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setScrollHolder(this);
        initHeaderView();
        initFirstAppView();
        initAppGridView();
    }

    /**
     * 根据返回游戏详情数据 设置title
     */
    private void initActionBar() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionbarView.getLayoutParams();
        if (params != null) {
            params.topMargin = ComUtil.getStatesBarHeight();
        }
        actionbarView.setVisibility(View.VISIBLE);
        actionbarBgDrawable = actionbarView.getBackground();
        actionbarBgDrawable.setAlpha(0);
    }

    @OnClick(R.id.tv_detail_title)
    protected void finish() {
        getActivity().finish();
    }


    /**
     * 初始化头部
     */
    private void initHeaderView() {
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.collection_header, null);
        headerViewHolder = new HeaderViewHolder(mHeaderView);

        mergeAdapter.addView(mHeaderView);
    }

    /**
     * 初始化头部
     */
    private void initFirstAppView() {
        mFirstAppView = LayoutInflater.from(getActivity()).inflate(R.layout.collection_first_item, null);
        firstAppViewHolder = new FirstAppViewHolder(getActivity(), mFirstAppView);
        mergeAdapter.addView(getTemplateDivider());
        mergeAdapter.addView(mFirstAppView);
    }

    /**
     * 初始化GridView
     */
    private void initAppGridView() {
        mAppGridView = LayoutInflater.from(getActivity()).inflate(R.layout.collection_grid, null);
        FullGridView fullGridView = (FullGridView) mAppGridView.findViewById(R.id.grid_view);
        mAdapter = new CollectionAdapter(getActivity(), appInfoLists, mRoute);
        fullGridView.setAdapter(mAdapter);

        mergeAdapter.addView(getTemplateDivider());
        mergeAdapter.addView(mAppGridView);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();

    }

    @Override
    public void onResume() {
        super.onResume();
        queryDb();
        MainThreadBus.getInstance().register(this);
    }

    private void queryDb() {
        if (task != null)
            task.cancel(true);
        task = new QueryTaskListTask();
        task.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }


    @Override
    protected void loadData() {
        createOrGetDataTask();
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        mCollectionModel = savedInstanceState.getParcelable(KEY_MODEL);
        nextPage = savedInstanceState.getInt(KEY_PAGE, 1);

        showView();

        if (savedInstanceState.getBoolean(KEY_NO_MORE, false))
            noMoreData();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (mCollectionModel != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
            outState.putInt(KEY_PAGE, nextPage);
            outState.putParcelable(KEY_MODEL, mCollectionModel);
        }
    }


    @Override
    public void onLoadMore() {
        // 防止用户第一次数据还未加载完成时，就上拉获取更多数据
        if (mCollectionModel == null || mCollectionModel.getPage() == null) {
            noMoreData();
            return;
        }

        if (nextPage > mCollectionModel.getPage().getTotalPageCount()) {
            noMoreData();
            return;
        }

        createOrGetDataTask();


    }

    private void showView() {
        showHeadView();
        showFirstView();
        showGridView();
        loadMoreListView.setAdapter(mergeAdapter);
    }

    /**
     * 显示顶部信息
     */
    private void showHeadView() {
        if (mCollectionModel != null && mCollectionModel.getAlbum() != null) {
            mHeaderView.setVisibility(View.VISIBLE);
            headerViewHolder.image.setImageUrl(mCollectionModel.getAlbum().getAlbumPic());
            headerViewHolder.title.setText(mCollectionModel.getAlbum().getAlbumTitle());
            if (TextUtils.isEmpty(mCollectionModel.getAlbum().getAlbumSubTitle()))
                headerViewHolder.title_second.setVisibility(View.GONE);
            else {
                headerViewHolder.title_second.setVisibility(View.VISIBLE);
                headerViewHolder.title_second.setText(mCollectionModel.getAlbum().getAlbumSubTitle());
            }

            headerViewHolder.desc.setText(mCollectionModel.getAlbum().getAlbumDesc());

        } else {
            mHeaderView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示第一条应用信息
     */
    private void showFirstView() {
        if (!ListUtils.isEmpty(appInfoLists)) {
            mFirstAppView.setVisibility(View.VISIBLE);
            firstAppViewHolder.setAppInfo(appInfoLists.get(0));
            firstAppViewHolder.setFlowFreeView(false);
            setupFirstView(firstAppViewHolder, appInfoLists.get(0));
            mFirstAppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ListUtils.isEmpty(appInfoLists))
                        return;

                    AppInfo appInfo = appInfoLists.get(0);
                    if (appInfo == null)
                        return;

                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_SEVEN] = 1;
                    getActivity().startActivity(DetailActivity.newIntent(getActivity(), appInfo.getAppId(), route));
                }
            });

        } else {
            mFirstAppView.setVisibility(View.GONE);
        }
    }

    private void setupFirstView(FirstAppViewHolder holder, AppInfo currAppInfo) {
        int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, FreeStoreApp.getContext());

        //图标
        if (!TextUtils.isEmpty(currAppInfo.getIcon())) {
            holder.ivAppLogo.setImageUrlAndReUse(currAppInfo.getIcon());
            holder.ivAppLogo.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(currAppInfo.getcIconLabel())) {
            holder.ivAppLogoLabel.setVisibility(View.GONE);
        } else {
            holder.ivAppLogoLabel.setImageUrlAndReUse(currAppInfo.getcIconLabel());
            holder.ivAppLogoLabel.setVisibility(View.VISIBLE);
        }

        //标题
        if (holder.tvAppLabel != null) {
            holder.tvAppLabel.setText(currAppInfo.getAppName());
            holder.tvAppLabel.setVisibility(View.VISIBLE);
        }
        if (holder.tvAppInfo != null) {
            holder.tvAppInfo.setText(currAppInfo.getsAppDesc().trim());
        }

        //统计相关
        setStaticInfo(0, holder, currAppInfo);
        DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
    }

    /**
     * 跟统计相关的
     *
     * @param position
     * @param holder
     * @param currAppInfo
     */
    private void setStaticInfo(int position, FirstAppViewHolder holder, AppInfo currAppInfo) {
        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            if (route[AppConstants.STATISTCS_DEPTH_SIX] == AppConstants.STATISTCS_DEFAULT_NULL) {
                route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
            } else {
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
            }
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
            route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
            currAppInfo.setRoute(route);
            holder.button.setTag(R.id.tag_first, currAppInfo);
            holder.button.setTag(R.id.tag_second, position);
        }
    }

    /**
     * 显示第一条应用信息
     */
    private void showGridView() {
        mAdapter.refreshData(appInfoLists);
    }

    private void createOrGetDataTask() {
        FSRequestHelper.newGetRequest(url + nextPage + ".json", TAG, CollectionModel.class,
                new IFDResponse<CollectionModel>() {
                    @Override
                    public void onSuccess(CollectionModel result) {
                        mCollectionModel = result;

                        if (mCollectionModel == null || mCollectionModel.getPage() == null) {
                            if (nextPage == 1) {
                                showEmpty();
                            }

                            return;
                        }

                        if (nextPage >= mCollectionModel.getPage().getTotalPageCount())
                            loadMoreListView.onNoMoreData();

                        if (ListUtils.isEmpty(mCollectionModel.getInfos())) {
                            if (ListUtils.isEmpty(appInfoLists)) {
                                showEmpty();
                            }
                            return;
                        }

                        nextPage = mCollectionModel.getPage().getRequestPageNum() + 1;

                        synchronized (appInfoLists) {
                            AppInfoUtils.bindData(getActivity(), mCollectionModel.getInfos(), appInfoLists, TAG);
                        }

                        showView();
                        resetRefreshUi();
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
     * 模板与模板之间带阴影的分割线
     *
     * @return
     */
    private View getTemplateDivider() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_modul_divider, loadMoreListView, false);
        return view;
    }

    /**
     * 更新应用列表进度
     *
     * @param taskInfo
     * @param appInfoList
     */
    private void updateProgress(TaskInfo taskInfo, List<AppInfo> appInfoList) {
        for (AppInfo appInfo : appInfoList) {
            if (taskInfo.getAppId() == appInfo.getAppId()) {
                DownloadHelper.calcDownloadSpeed(FreeStoreApp.getContext(), appInfo, taskInfo);
                appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                appInfo.setDownloadTotalSize(-1 == taskInfo.getApkTotalSize() ? AppInfoUtils.getPatchApkSize(appInfo) : taskInfo
                        .getApkTotalSize());
                appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                appInfo.setDownloadState(taskInfo.getDownloadState());
                appInfo.setLocalUri(taskInfo.getApkLocalUri());
                appInfo.setApkDownloadId(taskInfo.getTaskId());
                appInfo.setInDownloadDB(true);
                appInfo.setDownloadPatch(taskInfo.getApkTotalSize() < appInfo.getApkSize());
                int reason = taskInfo.getReason();
                DownloadHelper.handleMsgForPauseOrError(getActivity(), appInfo.getAppName(),
                        taskInfo.getDownloadState(), reason);
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (task != null)
            task.cancel(true);
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        setAlpha(loadMoreListView.getComputedScrollY());
    }


    //设置toolbar和状态栏透明度
    public void setAlpha(int mScrollY) {
        float minScrollHeight = getMinScrollHeight();
        float showScrollY = Math.min(mScrollY, minScrollHeight);
        changeActionbar(showScrollY / minScrollHeight);
    }


    private int getMinScrollHeight() {
        int minScrollHeight;
        int bannerHeight = getResources().getDimensionPixelOffset(R.dimen.collection_header_image_height);
        int statusBarHeight = getResources().getDimensionPixelOffset(R.dimen.status_bar_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            minScrollHeight = bannerHeight;
        } else {
            minScrollHeight = bannerHeight + statusBarHeight;
        }
        return minScrollHeight;
    }

    private boolean isRed = false;


    private void changeActionbar(float alpha) {
        int iAlpha = (int) (alpha * 255);
        actionbarBgDrawable.setAlpha(iAlpha);
        if (getActivity() instanceof CollectionActivity)
            ((CollectionActivity) getActivity()).setStatusBarColor(iAlpha);
        if (alpha >= 0.9 && !isRed) {
            isRed = true;
            if (mCollectionModel != null && mCollectionModel.getAlbum() != null) {
                tvTitle.setText(mCollectionModel.getAlbum().getAlbumTitle());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvTitle.setSelected(true);
                    }
                }, 1000);
            }
        } else if (alpha < 0.9 && isRed) {
            tvTitle.setText("");
            isRed = false;
        }
    }


    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (appInfoLists == null || appInfoLists.isEmpty()) {
                return false;
            }

            synchronized (appInfoLists) {
                AppInfoUtils.updateDownloadState(mParentActivity, appInfoLists);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                if (firstAppViewHolder != null && !ListUtils.isEmpty(appInfoLists))
                    setupFirstView(firstAppViewHolder, appInfoLists.get(0));
            }

        }
    }

    static class HeaderViewHolder {
        @Bind(R.id.image)
        SimpleImageView image;

        @Bind(R.id.desc)
        ExpandableTextView desc;

        @Bind(R.id.title)
        TextView title;

        @Bind(R.id.title_second)
        TextView title_second;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class FirstAppViewHolder extends DownloadViewHolder implements View.OnClickListener {
        @Bind(R.id.app_logo)
        FSSimpleImageView ivAppLogo;
        @Bind(R.id.app_logo_label)
        FSSimpleImageView ivAppLogoLabel;
        @Bind(R.id.app_title)
        TextView tvAppLabel;
        @Bind(R.id.app_info_layout)
        View viewContainer;
        @Bind(R.id.home_divider)
        public View divider;

        public FirstAppViewHolder(Context mContext, View parentView) {
            super(mContext, parentView);
        }

    }

}
