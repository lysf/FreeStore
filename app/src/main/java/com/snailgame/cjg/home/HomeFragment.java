package com.snailgame.cjg.home;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.ui.BaseToolbarFragment;
import com.snailgame.cjg.common.widget.ImagePageAdapter;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.common.widget.PopupDialog;
import com.snailgame.cjg.common.widget.PullDownRefreshHeader;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.CxbPhoneNumberChangeEvent;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.FreeGameItemEvent;
import com.snailgame.cjg.event.LoactionChangeEvent;
import com.snailgame.cjg.event.NecessaryDialogDismissEvent;
import com.snailgame.cjg.event.NewsIgnoreEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.event.UserInfoLoadEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.home.adapter.ActivityDivisionAdapter;
import com.snailgame.cjg.home.adapter.AdvertisementBannerAdapter;
import com.snailgame.cjg.home.adapter.CollectionAdapter;
import com.snailgame.cjg.home.adapter.GoodsRecommendAdapter;
import com.snailgame.cjg.home.adapter.HomeAppNewsAdapter;
import com.snailgame.cjg.home.adapter.HotGamesAdapter;
import com.snailgame.cjg.home.adapter.HotSpreeAdapter;
import com.snailgame.cjg.home.adapter.NewsHotAdapter;
import com.snailgame.cjg.home.adapter.NewsTodayAdapter;
import com.snailgame.cjg.home.adapter.QuickReturnAdapter;
import com.snailgame.cjg.home.adapter.RecommendGameAdapter;
import com.snailgame.cjg.home.adapter.ThreeContentGameAdapter;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.HomePageModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.home.model.UserMobileModel;
import com.snailgame.cjg.home.view.BannerScrollView;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.statistics.TrafficStatisticsUtil;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.cjg.util.QueryFreeGameUtils;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.interfaces.FreeGameRefrsh;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import third.commonsware.cwac.merge.MergeAdapter;
import third.scrolltab.ScrollTabHolder;

/**
 * Created by sunxy on 14-6-19.
 * 首页
 */
public class HomeFragment extends BaseToolbarFragment implements View.OnClickListener, ScrollTabHolder {
    static String TAG = HomeFragment.class.getName();

    private BannerScrollView bannerScrollView;
    private GridView quickReturnView;
    private View quickReturnDividerView;

    @Bind(R.id.homeContent)
    LoadMoreListView loadMoreListView;

    @Bind(R.id.refresh_header_container)
    PtrFrameLayout mPtrFrame;
    View mToolBarView;
    View mBannerMask;
    private ImagePageAdapter bannerPagerAdapter;
    private MergeAdapter mergeAdapter;


    private ArrayList<ContentModel> originHeaderLists;
    private List<ContentModel> filterHeaderLists;
    private List<ModuleModel> popupModuleLists;
    private PopupDialog dialog;
    private List<AppInfo> appInfoList;

    private String resultJson;  //保存原始的json数据
    private QueryTaskListTask task;
    private DelayHandler mDelayHandler;

    private boolean isNecessaryDialogShowed = false;

    private MainActivity mMainActivity;
    private List<ContentModel> mQuickBackLists;

    private int mSearchViewMarginTop;

    private boolean isSnailPhoneNumber = false;
    private boolean firstInHome = false;
    private boolean isRestoreData = false;
    private boolean theSecondTypePopupShowed = false;//限制弹出次数的弹窗
    private int popupTimes = 0;
    private int listviewPosition;
    private int listviewTop;

    private final Object syncObj = new Object();
    private static final String ALL = "0";
    private static final String FREE = "1";
    private static final String KEY_JSON = "key_json";
    private static final String KEY_LISTVIEW_POSITION = "scroll_position";
    private static final String KEY_LISTVIEW_TOP = "listview_top";
    private static final int INIT = 0, REFRESH = 1;
    private int refreshType = INIT;
    private static final long DELAY_TIME = 400;
    PullDownRefreshHeader pullDownRefreshHeader;

    // 下拉刷新
    private int iRefreshNum = 0;
    private NewsTodayAdapter newsTodayAdapter;
    private NewsHotAdapter newsHotAdapter;

    private UserMobileModel.ModelItem mUserMobile = new UserMobileModel.ModelItem();

    public static HomeFragment getInstance(boolean isFirstIn) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.KEY_FIRST_IN, isFirstIn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (loadMoreListView != null && taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                updateProgress(taskInfo);
            }
            if (mergeAdapter != null)
                mergeAdapter.notifyDataSetChanged();
        }
    }


    @Subscribe
    public void userInfoChanged(UserInfoLoadEvent event) {
        refreshAccountInfo();
        String uid = SharedPreferencesUtil.getInstance().getLastLoginID();
        //登录 退出 切换账号
        if (AppConstants.login || TextUtils.isEmpty(uid) || !uid.equals(IdentityHelper.getUid(getActivity()))) {
            refreshBanner();
            if (!IdentityHelper.isLogined(getActivity())) {
                resetIfreeFlow();
                mergeAdapter.notifyDataSetChanged();
            }
            //如果登陆且是免流量状态 刷新游戏列表
            if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) {
                mergeAdapter.notifyDataSetChanged();
            }
            refreshAgentFreeState();
            //保存上次登录uid
            SharedPreferencesUtil.getInstance().setLastLoginID(IdentityHelper.getUid(getActivity()));
        }
    }

    @Subscribe
    public void cxbPhoneNumberChange(CxbPhoneNumberChangeEvent event) {
        if (mQuickBackLists != null)
            showQuickReturnView(mQuickBackLists);
    }

    /**
     * 如果是广东代理则需要更新免状态
     */
    private void refreshAgentFreeState() {
        if (AccountUtil.isAgentFree(getActivity())) {
            AppInfoUtils.updateAppFreeState(getActivity(), AppInfoUtils.getAllGameIds(appInfoList), TAG);
        }
    }


    @Subscribe
    public void onLoactionChanged(LoactionChangeEvent event) {
        if (!ListUtils.isEmpty(filterHeaderLists)) {
            filter();
        }
    }

    @Subscribe
    public void onNecessaryDialogDismiss(NecessaryDialogDismissEvent event) {
        if (firstInHome && !theSecondTypePopupShowed) {
            isNecessaryDialogShowed = true;
            if (!ListUtils.isEmpty(popupModuleLists))
                showPopupDialog();
        }

    }

    /**
     * 刷新下载进度条
     *
     * @param taskInfo
     */
    private void updateProgress(TaskInfo taskInfo) {

        for (AppInfo appInfo : appInfoList) {
            if (appInfo != null) {
                if (taskInfo.getAppId() == appInfo.getAppId()) {
                    // calculate the task download speed
                    DownloadHelper.calcDownloadSpeed(getActivity(), appInfo, taskInfo);
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoute = createRoute();
        mergeAdapter = new MergeAdapter();
        appInfoList = new ArrayList<>();
        popupModuleLists = new ArrayList<>(2);//最多两个弹窗类型的Module
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mToolBarView = mMainActivity.getToolBarView();
        mBannerMask = mMainActivity.getBannerMask();
        setPullDownToRefresh();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void handleIntent() {
        if (getArguments() != null)
            firstInHome = getArguments().getBoolean(AppConstants.KEY_FIRST_IN, false);
    }

    @Override
    protected void initView() {
        loadMoreListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        loadMoreListView.enableLoadingMore(true);
        initheader();
    }

    private void initheader() {
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_fragment, loadMoreListView, false);
//        loadMoreListView.addHeaderView(headerView);
        mergeAdapter.addView(headerView);
        quickReturnView = (GridView) headerView.findViewById(R.id.quickEnter);
        quickReturnDividerView = headerView.findViewById(R.id.quick_enter_divider);
        bannerScrollView = (BannerScrollView) headerView.findViewById(R.id.autoScrollView);
        initScrollInfo();
    }

    private void setPullDownToRefresh() {
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, loadMoreListView, header);
            }
        });

        pullDownRefreshHeader = new PullDownRefreshHeader(getActivity(), mToolBarView, mBannerMask);
        pullDownRefreshHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrFrame.setHeaderView(pullDownRefreshHeader);
        mPtrFrame.addPtrUIHandler(pullDownRefreshHeader);
        mPtrFrame.disableWhenHorizontalMove(true);
//       mPtrFrame.autoRefresh();/*第一次进入页面是否自动刷新*/

    }

    /**
     * 初始searchView滚动需要的信息
     */
    private void initScrollInfo() {
        Resources resources = getResources();
        mSearchViewMarginTop = resources.getDimensionPixelSize(R.dimen.search_view_margin_top) - ComUtil.getStatesBarHeight();
        mDelayHandler = new DelayHandler();
    }

    private void refreshData() {
        iRefreshNum++;
        refreshType = REFRESH;
        mergeAdapter = new MergeAdapter();
        initheader();
        loadHomeJson(JsonUrl.getJsonUrl().JSON_URL_HOME, REFRESH);
    }


    @Override
    protected void loadData() {
        showLoading();
        mDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshAccountInfo();
                loadHomeJson(JsonUrl.getJsonUrl().JSON_URL_HOME, INIT);

            }
        }, DELAY_TIME);


    }

    // 获取首页数据
    private void loadHomeJson(String url, final int refreshType) {
        boolean cache = true;
        if (refreshType == REFRESH)
            cache = false;

        FSRequestHelper.newGetRequest(url + "?cImei=" + PhoneUtil.getIMEICode(getActivity()) + "&iNum=" + iRefreshNum, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    resultJson = result;
                    if (SharedPreferencesUtil.getInstance().isContractMachine()) {
                        setMobileStatus(UserMobileModel.MobileState.LOADING, null);
                        loadUserMobileData();
                    }
                    handleResult(result);
                }
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                }
            }

            @Override
            public void onNetWorkError() {
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                } else {
                    showBindError();
                }
            }

            @Override
            public void onServerError() {
                if (refreshType == REFRESH) {
                    mPtrFrame.refreshComplete();
                } else {
                    showBindError();
                }
            }
        }, cache);


    }

    /**
     * 加载合约机用户手机账单信息
     */
    private void loadUserMobileData() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_HOME_MOBILE, TAG, UserMobileModel.class, new IFDResponse<UserMobileModel>() {
            @Override
            public void onSuccess(UserMobileModel userMobileModel) {
                if (userMobileModel != null) {
                    if (userMobileModel.getCode() == 8888) {
                        setMobileStatus(UserMobileModel.MobileState.MAINTENANCE, null);
                    } else if (userMobileModel.getCode() == 0 && userMobileModel.getItem() != null) {
                        setMobileStatus(UserMobileModel.MobileState.SUCCESS, userMobileModel.getItem());
                    } else {
                        setMobileStatus(UserMobileModel.MobileState.FAILED, null);
                    }
                } else {
                    setMobileStatus(UserMobileModel.MobileState.FAILED, null);
                }
            }

            @Override
            public void onNetWorkError() {
                setMobileStatus(UserMobileModel.MobileState.FAILED, null);
            }

            @Override
            public void onServerError() {
                setMobileStatus(UserMobileModel.MobileState.FAILED, null);
            }
        }, false);
    }

    /**
     * 设置合约机手机状态
     *
     * @param status
     * @param userMobile
     */
    private void setMobileStatus(int status, UserMobileModel.ModelItem userMobile) {
        if (userMobile != null) {
            mUserMobile = userMobile;
        }

        if (mUserMobile == null) {
            mUserMobile = new UserMobileModel.ModelItem();
        }
        mUserMobile.setStatus(status);

        GlobalVar.getInstance().setUserMobile(mUserMobile);
        if (bannerPagerAdapter != null) {
            bannerPagerAdapter.refreshUserMobileView(mUserMobile);
        }
    }


    private void refreshAccountInfo() {
        popupTimes = SharedPreferencesUtil.getInstance().getPopupTimes();
        isSnailPhoneNumber = SharedPreferencesUtil.getInstance().isSnailPhoneNumber();
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        refreshAccountInfo();
        showLoading();
        resultJson = savedInstanceState.getString(KEY_JSON);
        listviewPosition = savedInstanceState.getInt(KEY_LISTVIEW_POSITION);
        listviewTop = savedInstanceState.getInt(KEY_LISTVIEW_TOP);
        if (SharedPreferencesUtil.getInstance().isContractMachine()) {
            mUserMobile = GlobalVar.getInstance().getUserMobile();
        }
        if (resultJson != null) {
            restoreInBackground();
        }

    }

    private void restoreInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRestoreData = true;
                handleResult(resultJson);
            }
        }).start();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (resultJson != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putString(KEY_JSON, resultJson);
            outState.putInt(KEY_LISTVIEW_POSITION, loadMoreListView.getFirstVisiblePosition());
            //保存Lisview 距离头部距离
            View view = loadMoreListView.getChildAt(0);
            int top = (view == null) ? 0 : (view.getTop() - loadMoreListView.getPaddingTop());
            outState.putInt(KEY_LISTVIEW_TOP, top);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_HOME);
        queryDb();
        setScrollView();
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
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_HOME);
        MainThreadBus.getInstance().unregister(this);
        stopAutoScroll();
    }


    /**
     * banner停止自动滚动
     */
    private void stopAutoScroll() {
        if (bannerScrollView != null) {
            bannerScrollView.stopAutoScroll();
        }
    }


    /**
     * 解析返回数据
     *
     * @param resultStr resultStr
     */
    private void handleResult(String resultStr) {
        HomePageModel homePageModel = ExtendJsonUtil.parseHomeJsonToModel(resultStr, HomePageModel.class);
        if (homePageModel != null && homePageModel.getList() != null) {
            clearData();
            List<ModuleModel> moduleModelList = homePageModel.getList();
            filterAndParseExtras(moduleModelList);
            showHomeView(moduleModelList);
        }
    }

    private void clearData() {
        appInfoList.clear();
        popupModuleLists.clear();
    }

    /**
     * 过滤顶部Banner数据解析游戏应用扩展字段
     */
    private void filterAndParseExtras(List<ModuleModel> moduleModelList) {
        for (ModuleModel moduleModel : moduleModelList) {
            int columnType = moduleModel.getcType();//栏目类型（1-广告位;2-快速入口；3-弹窗;4-内容栏"）
            String templateType = moduleModel.getcTemplateId();//模板类型（模板1-14）
            ArrayList<ContentModel> children = moduleModel.getChilds();
            if (children != null) {
                if (columnType == ModuleModel.TYPE_BANNER) {
                    //广告位过滤
                    originHeaderLists = children;//保存原始的广告位数据（用于登入登出等过滤操作）
                    filterHeaderLists = new ArrayList<>(originHeaderLists.size());
                    filterHeaderLists.addAll(originHeaderLists);
                    filter();
                } else {
                    //如果是游戏或者应用模板需要解析扩展字段
                    if (templateType.equals(ModuleModel.TEMPLATE_FOUR)
                            || templateType.equals(ModuleModel.TEMPLATE_TWL)
                            || templateType.equals(ModuleModel.TEMPLATE_FOURTEEN)) {
                        parseGameExtras(children, templateType, columnType == ModuleModel.TYPE_POPUP);
                    }
                }
            }
        }
    }

    /**
     * 根据解析出来的数据展示界面
     */
    private void showHomeView(final List<ModuleModel> moduleModelList) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int type;
                for (ModuleModel moduleModel : moduleModelList) {
                    type = moduleModel.getcType();
                    if (moduleModel.getChilds() != null) {
                        //类型:1-广告位;2-快速入口；3-弹窗;4-内容栏 5-快速入口代充值
                        switch (type) {
                            case ModuleModel.TYPE_BANNER:
                                showBannerHeader();
                                break;

                            case ModuleModel.TYPE_QUICK_ENTER:
                                showQuickReturnView(moduleModel.getChilds());
                                break;

                            case ModuleModel.TYPE_POPUP:
                                if (!isRestoreData && refreshType != REFRESH) {
                                    popupModuleLists.add(moduleModel);
                                }
                                break;

                            case ModuleModel.TYPE_CONTENT:
                                showTemplateContent(moduleModel);
                                break;
                        }
                    }
                }

                //for循环处理业务比较多可能消耗一定时间 activity有可能被销毁了
                if (getActivity() != null) {
                    loadMoreListView.setAdapter(mergeAdapter);
                    if (mMainActivity != null) {
                        mMainActivity.onHomeViewShowed();
                    }
                    loadMoreListView.setScrollHolder(HomeFragment.this);
                    loadMoreListView.onNoMoreData();
                    //由于弹出处理比较耗时，所以在首页界面显示之后再处理弹窗
                    if (isRestoreData) {
                        isRestoreData = false;
                        restoreHomeFragment();
                    } else {
                        SharedPreferencesUtil.getInstance().setLastRefreshTime(System.currentTimeMillis());
                    }

                    if (!firstInHome || isNecessaryDialogShowed)
                        showPopupDialog();
                }

            }
        });
    }


    private void restoreHomeFragment() {
        loadMoreListView.setSelectionFromTop(listviewPosition, listviewTop);
    }


    private void showPopupDialog() {
        if (firstInHome) {      // 首次进入
            firstInHome = false;

            if (ListUtils.isEmpty(popupModuleLists))
                return;

            // 取第一个
            showDialog(popupModuleLists.get(0), true);
            theSecondTypePopupShowed = true;
        } else {                // 非首次进入
            if (theSecondTypePopupShowed)   // 本次已显示过弹窗
                return;

            if (ListUtils.isEmpty(popupModuleLists) || popupModuleLists.size() < 2)
                return;

            if (popupModuleLists.get(1) == null || ListUtils.isEmpty(popupModuleLists.get(1).getChilds()))
                return;

            ContentModel model = popupModuleLists.get(1).getChilds().get(0);
            if (!checkTimesById(model.getiId() + model.getsRefId()))
                return;

            theSecondTypePopupShowed = true;

            // 取第二个
            showDialog(popupModuleLists.get(1), false);
        }
    }


    private void showDialog(ModuleModel popupModule, boolean firstIn) {
        String templateType = popupModule.getcTemplateId();
        switch (templateType) {
            case ModuleModel.TEMPLATE_FOUR:
                showGameDialog(popupModule);
                break;
            case ModuleModel.TEMPLATE_TEN:
                //合集或者活动弹窗
                showBannerDialog(popupModule);
                break;
        }
    }


    /**
     * 解析游戏扩展字段数据
     *
     * @param children children
     * @param isPopup  是否弹窗类型
     */
    private void parseGameExtras(List<ContentModel> children, String templateIdString, final boolean isPopup) {
        BaseAppInfo baseAppInfo;
        final List<BaseAppInfo> baseAppInfoList = new ArrayList<>(children.size());
        int templateId = -1;
        if (!TextUtils.isEmpty(templateIdString) && TextUtils.isDigitsOnly(templateIdString))
            templateId = Integer.parseInt(templateIdString);
        for (ContentModel contentModel : children) {
            String extend = contentModel.getsExtend();
            if (!TextUtils.isEmpty(extend)) {

                try {
                    baseAppInfo = JSON.parseObject(extend, BaseAppInfo.class);
                    baseAppInfo.setAppName(contentModel.getsTitle());
                    baseAppInfo.setIcon(contentModel.getsImageUrl());
                    baseAppInfo.setsAppDesc(contentModel.getsSummary());
                    if (isPopup)
                        templateId = AppConstants.SOUCE_POPUP;
                    baseAppInfo.setcMainType(templateId);
                    baseAppInfoList.add(baseAppInfo);

                } catch (JSONException e) {
                }
            }
        }
        synchronized (syncObj) {
            AppInfoUtils.bindHomeData(getActivity(), baseAppInfoList, appInfoList, TAG);
        }
    }


    private void showGameDialog(ModuleModel moduleModel) {
        if (moduleModel != null && !ListUtils.isEmpty(moduleModel.getChilds())) {
            List<ContentModel> children = moduleModel.getChilds();
            List<AppInfo> popupGameLists = new ArrayList<>(2);

            for (AppInfo appInfo : appInfoList) {
                if (appInfo.getcMainType() == AppConstants.SOUCE_POPUP)
                    popupGameLists.add(appInfo);
            }

            showDialog(children.get(0), popupGameLists.get(0));
        }
    }


    /**
     * 显示活动或者合集弹窗
     *
     * @param moduleModel moduleModel
     */
    private void showBannerDialog(ModuleModel moduleModel) {
        if (moduleModel.getChilds() != null && !moduleModel.getChilds().isEmpty()) {
            List<ContentModel> children = moduleModel.getChilds();

            showRealBanner(children.get(0));
        }
    }

    private void showRealBanner(ContentModel contentModel) {
        // 从弹出广告进入
        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_DIALOGAD;
        dialog = DialogUtils.showBannerDialog(getActivity(), contentModel, route);
        if (dialog != null) {
            dialog.show();
            changeTimesById(contentModel.getiId() + contentModel.getsRefId());
        }
    }


    /**
     * 刷新顶部banner
     */
    private void refreshBanner() {
        if (originHeaderLists != null) {
            filter();
            if (bannerPagerAdapter != null)
                bannerPagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据地区运营商过滤首页Banner内容
     */
    private void filter() {
        //省份编号，运营商信息
        String provinceId = ComUtil.getProvinceId("0");
        String operators = TrafficStatisticsUtil.getOperators();

        String target;
        String contentChannel;
        String myChannel = String.valueOf(ChannelUtil.getChannelID());//本机渠道

        filterHeaderLists.clear();
        for (ContentModel contentModel : originHeaderLists) {
            target = contentModel.getsTarget();
            contentChannel = contentModel.getcChannel();
            if (!TextUtils.isEmpty(target)) {
                //所有用户可见
                if (target.equals(ALL)) {
                    channelFilter(contentChannel, myChannel, contentModel);
                } else if (target.equals(FREE)) {
                    //170 免流量用户可见
                    if (isSnailPhoneNumber) {
                        channelFilter(contentChannel, myChannel, contentModel);
                    }
                } else if (!target.equals("_")) {

                    String array[] = target.split("_");

                    if (target.startsWith("_")) {
                        //只有省份编号
                        String province = array[1];
                        if (province.contains(provinceId)) {
                            channelFilter(contentChannel, myChannel, contentModel);
                        }
                    } else if (target.endsWith("_")) {
                        //只有运营商信息
                        String mobileType = array[0];
                        if (mobileType.contains(operators)) {
                            channelFilter(contentChannel, myChannel, contentModel);
                        }
                    } else {
                        //都有
                        String mobileType = array[0];
                        String province = array[1];
                        if (mobileType.contains(operators) && province.contains(provinceId)) {
                            channelFilter(contentChannel, myChannel, contentModel);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更具渠道过滤
     *
     * @param contentChannel contentChannel
     * @param myChannel      myChannel
     * @param contentModel   contentModel
     */
    private void channelFilter(String contentChannel, String myChannel, ContentModel contentModel) {
        //没有渠道过滤
        if (TextUtils.isEmpty(contentChannel))
            filterHeaderLists.add(contentModel);
        else if (isContainsMyChannel(contentChannel, myChannel)) {
            filterHeaderLists.add(contentModel);
        }
    }


    //要过滤渠道是否包含本机渠道
    public static boolean isContainsMyChannel(String contentChannel, String myChannel) {

        if (contentChannel.contains(",")) {
            //多渠道过滤
            String array[] = contentChannel.split(",");
            for (String channel : array) {
                if (channel.equals(myChannel))
                    return true;
            }
        } else if (contentChannel.equals(myChannel)) {
            return true;
        }
        return false;
    }


    /**
     * 展示模板3-14界面
     *
     * @param moduleModel moduleModel
     */
    private void showTemplateContent(final ModuleModel moduleModel) {
        String templateType = moduleModel.getcTemplateId();
        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
        RecommendGameAdapter recommendGameAdapter;
        //模板3-10
        switch (templateType) {
            case ModuleModel.TEMPLATE_THREE:
                if (getActivity() != null) {
                    mergeAdapter.addAdapter(new AdvertisementBannerAdapter(getActivity(), moduleModel, route));
                }
                break;
            case ModuleModel.TEMPLATE_FOUR:
                if (getActivity() != null) {
                    recommendGameAdapter = new RecommendGameAdapter(getActivity(), moduleModel, filterGameLists(moduleModel), route);
                    mergeAdapter.addAdapter(recommendGameAdapter);
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;
            case ModuleModel.TEMPLATE_FIVE:
                if (getActivity() != null) {
                    mergeAdapter.addAdapter(new HotSpreeAdapter(getActivity(), moduleModel, route));
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;

            case ModuleModel.TEMPLATE_SEVEN:
                if (getActivity() != null) {
                    mergeAdapter.addAdapter(new ActivityDivisionAdapter(getActivity(), moduleModel, route));
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;
            case ModuleModel.TEMPLATE_EIGHT:
                if (getActivity() != null) {
                    mergeAdapter.addAdapter(new GoodsRecommendAdapter(getActivity(), moduleModel, route));
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;
            case ModuleModel.TEMPLATE_NINE:
                if (getActivity() != null)
                    mergeAdapter.addAdapter(new HotGamesAdapter(getActivity(), moduleModel, route));
                break;
            case ModuleModel.TEMPLATE_TEN:
                if (getActivity() != null)
                    mergeAdapter.addAdapter(new CollectionAdapter(getActivity(), moduleModel, route));
                break;
            case ModuleModel.TEMPLATE_TWL:
                if (getActivity() != null) {
                    recommendGameAdapter = new RecommendGameAdapter(getActivity(), moduleModel, filterGameLists(moduleModel), route);
                    mergeAdapter.addAdapter(recommendGameAdapter);
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;

            case ModuleModel.TEMPLATE_FOURTEEN:
                if (getActivity() != null) {
                    recommendGameAdapter = new RecommendGameAdapter(getActivity(), moduleModel, null, route);
                    mergeAdapter.addAdapter(recommendGameAdapter);
                    mergeAdapter.addAdapter(new ThreeContentGameAdapter(getActivity(), moduleModel, appInfoList, route));
                    mergeAdapter.addView(getTemplateBigDivider());
                }
                break;

            case ModuleModel.TEMPLATE_FIFTEEN:
                if (getActivity() != null) {
                    HomeAppNewsAdapter mHomeAppNewsAdapter = new HomeAppNewsAdapter(getActivity(), moduleModel, route);
                    mergeAdapter.addAdapter(mHomeAppNewsAdapter);
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;

            case ModuleModel.TEMPLATE_SEVENTEEN:
                if (getActivity() != null) {
                    newsTodayAdapter = new NewsTodayAdapter(getActivity(), moduleModel, route);
                    mergeAdapter.addAdapter(newsTodayAdapter);
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;
            case ModuleModel.TEMPLATE_EIGHTEEM:
                if (getActivity() != null) {
                    newsHotAdapter = new NewsHotAdapter(getActivity(), moduleModel, route);
                    mergeAdapter.addAdapter(newsHotAdapter);
                    mergeAdapter.addView(getTemplateDivider());
                }
                break;
            default:
                break;

        }
    }

    private List<AppInfo> filterGameLists(ModuleModel moduleModel) {
        List<AppInfo> temAppInfoLists = new ArrayList<>();
        int templateId = -1;
        String templateIdString = moduleModel.getcTemplateId();
        if (!TextUtils.isEmpty(templateIdString) && TextUtils.isDigitsOnly(templateIdString))
            templateId = Integer.parseInt(templateIdString);

        for (AppInfo appInfo : appInfoList) {
            if (appInfo.getcMainType() == templateId)
                temAppInfoLists.add(appInfo);
        }
        return temAppInfoLists;
    }


    /**
     * 快速返回View
     *
     * @param lists lists
     */
    private void showQuickReturnView(final List<ContentModel> lists) {
        mQuickBackLists = lists;
        if (getActivity() != null) {
            quickReturnView.setVisibility(View.VISIBLE);
            quickReturnDividerView.setVisibility(View.VISIBLE);
            quickReturnView.setAdapter(new QuickReturnAdapter(mRoute, getActivity(), lists));
        }
    }

    /**
     * 首页幻灯片
     */
    private void showBannerHeader() {
        if (filterHeaderLists != null && getActivity() != null) {
            setScrollView();
            if (SharedPreferencesUtil.getInstance().isContractMachine()) {
                bannerPagerAdapter = new ImagePageAdapter(getActivity(), filterHeaderLists, mUserMobile, mRoute);
            } else {
                bannerPagerAdapter = new ImagePageAdapter(getActivity(), filterHeaderLists, mRoute);
            }
            bannerScrollView.setAdapter(bannerPagerAdapter);
        }
    }

    private void setScrollView() {
        if (!SharedPreferencesUtil.getInstance().isContractMachine()) {
            bannerScrollView.setIsAutoScroll(true);
            bannerScrollView.startAutoScroll();
        } else {
            bannerScrollView.stopAutoScroll();
            bannerScrollView.setIsAutoScroll(false);
        }
    }


    /**
     * 数据获取失败
     */
    private void showBindError() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showError();
            }
        });
    }

    /**
     * 根据弹窗id更新弹窗次数
     */
    private void changeTimesById(String collectionId) {
        if (SharedPreferencesUtil.getInstance().getPopupRecommentID().equals("-1") || !SharedPreferencesUtil.getInstance().getPopupRecommentID().equals(collectionId)) {
            SharedPreferencesUtil.getInstance().setPopupRecommentID(collectionId);
            SharedPreferencesUtil.getInstance().setPopupTimes(1);
        } else {
            popupTimes++;
            SharedPreferencesUtil.getInstance().setPopupTimes(popupTimes);
        }
    }

    /**
     * 根据弹窗id确认是否
     *
     * @param collectionId
     * @return
     */
    private boolean checkTimesById(String collectionId) {
        if (SharedPreferencesUtil.getInstance().getPopupRecommentID().equals("-1"))
            return true;

        if (SharedPreferencesUtil.getInstance().getPopupRecommentID().equals(collectionId)) {
            // 弹窗次数限制
            if (popupTimes >= PersistentVar.getInstance().getSystemConfig().getPopupTimes()) {
                return false;
            }
        }

        return true;
    }


    /**
     * 弹窗
     *
     * @param model
     * @param baseAppInfo
     */
    private void showDialog(final ContentModel model, final AppInfo baseAppInfo) {
        if (AccountUtil.isAgentFree(mParentActivity)) {
            baseAppInfo.setcFlowFree(AppInfo.FREE_NULL);
            QueryFreeGameUtils.queryFreeGame(String.valueOf(baseAppInfo.getAppId()), new FreeGameRefrsh() {
                @Override
                public void refresh(List<FreeGameItem> freeGameItems) {
                    if (freeGameItems == null || freeGameItems.size() == 0) {
                        showRealGameDialog(model, baseAppInfo, mRoute.clone());
                        return;
                    }
                    for (FreeGameItem freeGameItem : freeGameItems) {
                        if (baseAppInfo.getPkgName().equals(freeGameItem.getcPackage()))
                            baseAppInfo.setcFlowFree(freeGameItem.getcFlowFree());
                    }
                    showRealGameDialog(model, baseAppInfo, mRoute.clone());

                }
            }, TAG);
        } else {
            showRealGameDialog(model, baseAppInfo, mRoute.clone());
        }

    }

    // 首页
    private void showRealGameDialog(ContentModel contentModel, final AppInfo baseAppInfo, int[] route) {
        //没有下载或者下载失败 显示弹窗
        if (baseAppInfo.getDownloadState() == DownloadManager.STATUS_INITIAL || baseAppInfo.getDownloadState() == DownloadManager.STATUS_FAILED) {
            dialog = DialogUtils.showRecommendGame(getActivity(), baseAppInfo, route);
            if (dialog != null) {
                dialog.show();
                changeTimesById(contentModel.getiId() + contentModel.getsRefId());
            }
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDelayHandler != null)
            mDelayHandler.removeCallbacksAndMessages(null);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);

        if (task != null)
            task.cancel(true);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }


    /**
     * 重写onScroll控制快速返回View的显示与隐藏
     * 以及幻灯片是否自动滚动
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @param pagePosition
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        int mScrollY = 0;
        if (pullDownRefreshHeader != null && pullDownRefreshHeader.isRefreshComplete) {
            mScrollY = loadMoreListView.getComputedScrollY();
            changeToolBar(mScrollY);
        }
    }

    //更改搜索框位置
    public void changeToolBar(int mScrollY) {
        int showScrollY = Math.min(mScrollY, mSearchViewMarginTop);
        setToolbarStatusBarAlpha(showScrollY * 255 / mSearchViewMarginTop);
    }

    public void setToolbarStatusBarAlpha(int alpha) {
        if (mMainActivity != null) {
            mMainActivity.setToolbarStatusBarAlpha(alpha);
        }
    }

    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (ListUtils.isEmpty(appInfoList)) {
                return false;
            }

            synchronized (syncObj) {
                AppInfoUtils.updateDownloadState(mParentActivity, appInfoList);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (mergeAdapter != null)
                    mergeAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * 刷新游戏应用列表免状态
     */
    private boolean refreshAppListItem = false;

    @Subscribe
    public void refreshAppListItem(FreeGameItemEvent event) {
        refreshAppListItem = true;
        List<FreeGameItem> itemLists = event.getFreeGameItems();
        AppInfo appInfo;
        FreeGameItem freeGameItem;

        int length = appInfoList.size();
        int itemLength = itemLists.size();

        for (int j = 0; j < length; j++) {
            appInfo = appInfoList.get(j);
            appInfo.setcFlowFree(AppInfo.FREE_NULL);
            for (int i = 0; i < itemLength; i++) {
                freeGameItem = itemLists.get(i);
                if (appInfo.getPkgName().equals(freeGameItem.getcPackage()))
                    appInfo.setcFlowFree(freeGameItem.getcFlowFree());
            }
        }
        mergeAdapter.notifyDataSetChanged();
    }

    /**
     * 重置游戏应用列表免状态
     */
    public void resetIfreeFlow() {
        if (refreshAppListItem) {
            int length = appInfoList.size();
            AppInfo appInfo;
            for (int i = 0; i < length; i++) {
                appInfo = appInfoList.get(i);
                appInfo.setcFlowFree(appInfo.getOriginCFlowFree());
            }
            mergeAdapter.notifyDataSetChanged();
        }
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
     * 模板与模板之间带阴影的分割线 28dp
     *
     * @return
     */
    private View getTemplateBigDivider() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_modul_big_divider, loadMoreListView, false);
        return view;
    }

    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_HOME && loadMoreListView != null) {
            loadMoreListView.setSelection(0);
        }
    }


    @Subscribe
    public void refreshNewsView(NewsIgnoreEvent event) {
        if (newsHotAdapter != null) {
            newsHotAdapter.removeItem(event.getArticleId());
        }

        if (newsTodayAdapter != null) {
            newsTodayAdapter.removeItem(event.getArticleId());
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 首页
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_HOMEPAGE,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    class DelayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}