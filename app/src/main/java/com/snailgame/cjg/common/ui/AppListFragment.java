package com.snailgame.cjg.common.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AlbumHeaderVO;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.CollectionModel;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.VoucherCooperActivity;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 应用列表（应用分类/合集）
 * Created by sunxy on 14-3-14.
 */
public class AppListFragment extends AbsBaseFragment
        implements LoadMoreListView.OnLoadMoreListener, AdapterView.OnItemClickListener, VoucherCooperActivity.IHeaderSpace {
    static String TAG = AppListFragment.class.getName();

    @Nullable
    @Bind(R.id.content)
    protected LoadMoreListView loadMoreListView;
    private boolean isShowExtend;
    protected Activity mContext;
    private HeaderViewHolder headerViewHolder;
    private FlowAreaHeaderViewHolder flowAreaHeaderViewHolder;

    private View mHeaderView;
    protected List<BaseAppInfo> baseAppInfos;
    protected ArrayList<AppInfo> appInfoLists = new ArrayList<>();
    protected CommonListItemAdapter appInfoAdapter;
    private CollectionModel mCollectionModel = new CollectionModel();
    private QueryTaskListTask task;
    private int appModel;
    private String pageUrl;
    protected int nextPage = 1;
    private boolean isShowAlbum = false;
    private boolean isDescriptionClose = false;

    private final static String KEY_APP_INFO_LIST = "key_app_info_list";
    private final static String KEY_COLLECTION_MODEL = "key_collection_model";
    private final static String KEY_APP_MODEL = "key_app_model";
    private final static String KEY_URL = "key_url";
    private final static String KEY_NEXT_PAGE = "key_next_page";
    private final static String KEY_SHOW_ALBUM = "key_show_album";
    private final static String KEY_DES_CLOSE = "key_des_close";
    private AbsListView.OnScrollListener onScrollListener;

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (loadMoreListView != null && taskInfos != null) {
            synchronized (appInfoLists) {
                for (TaskInfo taskInfo : taskInfos) {
                    updateProgress(taskInfo, appInfoLists);
                }
            }

            if (appInfoAdapter != null)
                appInfoAdapter.notifyDataSetChanged();
        }
    }


    /**
     * @param url         接口地址
     * @param appModel    4：合集 其他:分类
     * @param isShowAlbum 是否显示顶部大图片
     * @param route       PV用路径记载
     * @return
     */
    public static AppListFragment create(String url, int appModel, boolean isShowAlbum, int[] route) {
        AppListFragment appListFragment = new AppListFragment();
        Bundle arg = new Bundle();
        arg.putString(AppConstants.APP_LIST_URL, url);
        arg.putInt(AppConstants.KEY_APP_MODEL, appModel);
        arg.putBoolean(AppConstants.APP_SORT_SHOW_ALBUM, isShowAlbum);
        arg.putIntArray(AppConstants.KEY_ROUTE, route);
        appListFragment.setArguments(arg);
        return appListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

    }

    protected void registerOtto() {
        MainThreadBus.getInstance().register(this);
    }

    @Subscribe
    public void appFreeStateChanged(UserInfoLoadEvent event) {
        //刷新list
        if (appInfoAdapter != null) {
            AppInfoUtils.updateAppFreeState(getActivity(), AppInfoUtils.getAllGameIds(appInfoLists), appInfoAdapter, TAG);
        }
        if (!IdentityHelper.isLogined(getActivity()) && appInfoAdapter != null) {
            appInfoAdapter.resetIfreeFlow();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 提供蜗牛代金券直接设置推荐游戏、应用信息
     *
     * @param baseAppInfos
     */
    public void setBaseAppInfos(List<BaseAppInfo> baseAppInfos) {
        this.baseAppInfos = baseAppInfos;
    }

    /**
     * 设置距顶高度，留出距离给其他可能的空间占据顶部
     *
     * @param paddingTop
     */
    @Override
    public void setHeaderSpace(int paddingTop) {
        this.loadMoreListView.removeHeaderView(mHeaderView);
        mHeaderView = new View(mContext);
        mHeaderView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paddingTop));
        this.loadMoreListView.addHeaderView(mHeaderView);
        if (getListView().getEmptyView() != null) {
            getListView().getEmptyView().setPadding(0, paddingTop, 0, 0);
        }
    }

    @Override
    protected void initView() {
        int[] route = mRoute.clone();
        appInfoAdapter = new CommonListItemAdapter(mContext, appInfoLists, appModel, route);
        loadMoreListView.enableLoadingMore(true);
        if (isShowAlbum) {
            initHeaderView();
        }
        loadMoreListView.setLoadingListener(this);
        loadMoreListView.setOnItemClickListener(this);
        loadMoreListView.setAdapter(appInfoAdapter);
        if (appModel == AppConstants.VALUE_VOUCHER || appModel == AppConstants.VALUE_FREEAREA) {
            loadMoreListView.setDividerHeight(0);
        }
        if (onScrollListener != null) {
            loadMoreListView.setOnScrollListener(onScrollListener);
        }
    }

    @Override
    protected void handleIntent() {
        if (getArguments() != null) {
            pageUrl = getArguments().getString(AppConstants.APP_LIST_URL);
            appModel = getArguments().getInt(AppConstants.KEY_APP_MODEL);
            isShowAlbum = getArguments().getBoolean(AppConstants.APP_SORT_SHOW_ALBUM);
            mRoute = getArguments().getIntArray(AppConstants.KEY_ROUTE);
        }
    }


    @Override
    protected void loadData() {
        if (TextUtils.isEmpty(pageUrl)) {
            if (baseAppInfos != null) {
                synchronized (appInfoLists) {
                    AppInfoUtils.bindData(mContext, baseAppInfos, appInfoLists, appInfoAdapter, TAG);
                }
            }
            return;
        }
        showLoading();
        String url = pageUrl;
        if (appModel == AppConstants.VALUE_CATEGORY) {
            url = url + "&currentPage=" + nextPage;
        } else {
            url = url + nextPage + ".json";
        }

        createAppListTask(url);
    }


    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        appInfoLists = savedInstanceState.getParcelableArrayList(KEY_APP_INFO_LIST);
        mCollectionModel = savedInstanceState.getParcelable(KEY_COLLECTION_MODEL);
        appModel = savedInstanceState.getInt(KEY_APP_MODEL);
        pageUrl = savedInstanceState.getString(KEY_URL);
        nextPage = savedInstanceState.getInt(KEY_NEXT_PAGE);
        isShowAlbum = savedInstanceState.getBoolean(KEY_SHOW_ALBUM);
        isDescriptionClose = savedInstanceState.getBoolean(KEY_DES_CLOSE);
        showView();

        if (savedInstanceState.getBoolean(KEY_NO_MORE, false))
            noMoreData();
    }

    private void showView() {
        appInfoAdapter.refreshData(appInfoLists);
        appInfoAdapter.notifyDataSetChanged();
        resetRefreshStatus();
    }


    @Override
    protected void saveData(Bundle outState) {
        if (appInfoLists != null && appInfoLists.size() != 0 && mCollectionModel != null) {
            outState.putParcelableArrayList(KEY_APP_INFO_LIST, appInfoLists);
            outState.putParcelable(KEY_COLLECTION_MODEL, mCollectionModel);
            outState.putInt(KEY_APP_MODEL, appModel);
            outState.putBoolean(KEY_SAVE, true);
            outState.putString(KEY_URL, pageUrl);
            outState.putInt(KEY_NEXT_PAGE, nextPage);
            outState.putBoolean(KEY_SHOW_ALBUM, isShowAlbum);
            outState.putBoolean(KEY_DES_CLOSE, isDescriptionClose);
            outState.putBoolean(KEY_NO_MORE, getListView().getIsNoMore());
        }

    }


    /**
     * 初始化头部
     */
    private void initHeaderView() {
        if (appModel == AppConstants.VALUE_FREEAREA) {
            mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.header_free_flow_exper, null);
            flowAreaHeaderViewHolder = new FlowAreaHeaderViewHolder(mContext, mHeaderView);
        } else {
            mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.company_detail_header, null);
            headerViewHolder = new HeaderViewHolder(mHeaderView);
        }

        mHeaderView.setVisibility(View.GONE);
        loadMoreListView.addHeaderView(mHeaderView);
    }


    /**
     * 刷新界面头部view
     *
     * @param album
     */
    private void refreshHeaderView(final AlbumHeaderVO album) {
        mHeaderView.setVisibility(View.VISIBLE);
        if (appModel == AppConstants.VALUE_FREEAREA) {
            if (GlobalVar.getInstance().getUsrInfo() != null) {
                flowAreaHeaderViewHolder.photoView.setImageUrlAutoRotateEnabled(GlobalVar.getInstance().getUsrInfo().getcPhoto(), true);
            }

            flowAreaHeaderViewHolder.phoneNumberView.setText(SharedPreferencesUtil.getInstance().getPhoneNumber());
            flowAreaHeaderViewHolder.totalFLowView.setText(SharedPreferencesUtil.getInstance().getFlowFreeSize() + "MB");
            flowAreaHeaderViewHolder.endTimeView.setText(formatDate(SharedPreferencesUtil.getInstance().getFlowFreeEnd()));
            flowAreaHeaderViewHolder.startTimeView.setText(formatDate(SharedPreferencesUtil.getInstance().getFlowFreeStart()));

        } else {
            final FSSimpleImageView banner = (FSSimpleImageView) mHeaderView.findViewById(R.id.companyCard);

            String desc = album.getAlbumDesc();
            headerViewHolder.mHeaderContainer.setText(desc.trim());
            if (appModel == AppConstants.VALUE_VOUCHER) {
                headerViewHolder.mCompanyDetailHeader.setVisibility(View.GONE);
            }

            banner.setImageUrl(album.getAlbumIcon());
        }

    }


    private String formatDate(String time) {
        DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat outFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            Date date = inFormat.parse(time);
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(position);
        if (appInfo == null) return;

        int[] route = mRoute.clone();
        if (isShowAlbum) {
            if (appModel == AppConstants.VALUE_FREEAREA) {
                route[AppConstants.STATISTCS_DEPTH_FOUR] = position;
            } else {
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position;
            }
        } else {
            if (appModel == AppConstants.VALUE_FREEAREA) {
                route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
            } else {
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
            }
        }
        getActivity().startActivity(DetailActivity.newIntent(mContext, appInfo.getAppId(), appInfo.getiFreeArea(), route));
    }


    @Override
    public void onResume() {
        super.onResume();
        queryDb();
        registerOtto();
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
        unRegisterOtto();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);

        if (task != null)
            task.cancel(true);
    }


    protected void unRegisterOtto() {
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * 分页加载更多回调
     */
    @Override
    public void onLoadMore() {

        int totalPage = 0;
        if (appModel == AppConstants.VALUE_CATEGORY ||
                appModel == AppConstants.VALUE_CATEGORY_TAG ||
                appModel == AppConstants.VALUE_FREEAREA ||
                appModel == AppConstants.VALUE_VOUCHER) {
            // 防止用户第一次数据还未加载完成时，就上拉获取更多数据
            if (mCollectionModel == null || mCollectionModel.getPage() == null) {
                noMoreData();
                return;
            }

            totalPage = mCollectionModel.getPage().getTotalPageCount();
        }
        if (nextPage > totalPage) {
            noMoreData();
            return;
        }

        String url = pageUrl;
        if (appModel == AppConstants.VALUE_CATEGORY) {
            url = url + "&currentPage=" + nextPage;
        } else {
            url = url + nextPage + ".json";
        }

        createAppListTask(url);
    }

    private void createAppListTask(String url) {
        FSRequestHelper.newGetRequest(url, TAG, CollectionModel.class, new IFDResponse<CollectionModel>() {
            @Override
            public void onSuccess(CollectionModel result) {
                mCollectionModel = result;

                if (appModel == AppConstants.VALUE_CATEGORY ||
                        appModel == AppConstants.VALUE_COLLECTION ||
                        appModel == AppConstants.VALUE_CATEGORY_TAG ||
                        appModel == AppConstants.VALUE_FREEAREA ||
                        appModel == AppConstants.VALUE_VOUCHER) {
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

                    // 免流量专区属性设置
                    if (appModel == AppConstants.VALUE_FREEAREA) {
                        for (BaseAppInfo info : mCollectionModel.getInfos()) {
                            info.setiFreeArea(AppConstants.FREE_AREA_IN);
                        }
                        appInfoAdapter.setEmptyView(getEmptyView());
                    }

                    // 隐藏分隔带
                    if (appModel == AppConstants.VALUE_VOUCHER || appModel == AppConstants.VALUE_FREEAREA) {
                        loadMoreListView.setDividerHeight(0);
                    }

                    nextPage = mCollectionModel.getPage().getRequestPageNum() + 1;

                    if (isShowAlbum) {
                        refreshHeaderView(mCollectionModel.album);
                    }

                    synchronized (appInfoLists) {
                        AppInfoUtils.bindData(mContext, mCollectionModel.getInfos(), appInfoLists, appInfoAdapter, TAG);
                    }
                }

                resetRefreshStatus();
            }

            @Override
            public void onNetWorkError() {
                showBindError();
            }

            @Override
            public void onServerError() {
                showBindError();
            }
        }, true, true, new ExtendJsonUtil());
    }

    protected void showBindError() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showError();
                resetRefreshStatus();
            }
        });
    }


    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ListUtils.isEmpty(appInfoLists)) {
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
                appInfoAdapter.notifyDataSetChanged();
            }

        }
    }


    protected void resetRefreshStatus() {
        resetRefreshUi();
    }


    /**
     * 更新应用列表进度
     *
     * @param taskInfo
     * @param collectionItemList
     */
    private void updateProgress(TaskInfo taskInfo, List<AppInfo> collectionItemList) {
        for (AppInfo appInfo : collectionItemList) {
            if (taskInfo.getAppId() == appInfo.getAppId()) {
                DownloadHelper.calcDownloadSpeed(mContext, appInfo, taskInfo);
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


    static class HeaderViewHolder {
        @Nullable
        @Bind(R.id.company_detail_header)
        LinearLayout mCompanyDetailHeader;

        @Nullable
        @Bind(R.id.lv_header_container)
        ExpandableTextView mHeaderContainer;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    static class FlowAreaHeaderViewHolder {
        @Bind(R.id.iv_photo)
        FSSimpleImageView photoView;

        @Bind(R.id.tv_phone_number)
        TextView phoneNumberView;

        @Bind(R.id.tv_total_flow)
        TextView totalFLowView;

        @Bind(R.id.tv_end_time)
        TextView endTimeView;

        @Bind(R.id.tv_open_time)
        TextView startTimeView;

        Activity context;

        public FlowAreaHeaderViewHolder(Activity context, View view) {
            ButterKnife.bind(this, view);
            this.context = context;
        }


        @OnClick(R.id.tv_open_btn)
        protected void openFlowFree() {
            context.startActivity(WebViewActivity.newIntent(context, PersistentVar.getInstance().getSystemConfig().getFlowPrivilegeUrl()));
        }

        @OnClick(R.id.iv_ignore)
        protected void showIgnore() {
            final AlertDialog mDialog = DialogUtils.flowFreeIgnoreDialog(context);
            mDialog.show();

        }


    }
}
