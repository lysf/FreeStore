package com.snailgame.cjg.detail;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.commit451.nativestackblur.NativeStackBlur;
import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.share.sina.Constants;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FlowFreeView;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.detail.adapter.DetailFragmentAdapter;
import com.snailgame.cjg.detail.model.AppDetailModel;
import com.snailgame.cjg.detail.model.AppDetailResp;
import com.snailgame.cjg.detail.model.ScrollYEvent;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.RateChangeEvent;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.QueryFreeGameUtils;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.cjg.util.interfaces.FreeGameRefrsh;
import com.snailgame.cjg.wxapi.WechatShare;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.scrolltab.ScrollTabHolder;

/**
 * Created by sunxy on 2014/11/14.
 */
public class DetailFragment extends FixedSupportV4BugFragment implements View.OnClickListener, CustomLoadingView.RetryLoadListener, ScrollTabHolder {
    static String TAG = DetailFragment.class.getName();

    private static final int ACTIONBAR_SELECT = 0, HIDE_MARQUEEN = 2;
    @Bind(R.id.detailRootLayout)
    View mParentView;
    //头部信息部分
    @Bind(R.id.appinfo_icon)
    FSSimpleImageView iconView;
    @Bind(R.id.app_icon_label)
    FSSimpleImageView iconLabelView;
    @Bind(R.id.appinfo_title)
    TextView mGameTitle;
    @Bind(R.id.appinfo_version)
    TextView mGameVersionView;

    @Bind(R.id.appinfo_size)
    TextView mGameSizeView;
    @Bind(R.id.flow_free_container)
    FlowFreeView mFlowFreeView;
    @Bind(R.id.detailHeaderView)
    View mHeader;
    @Bind(R.id.innerHeaderView)
    View mInnerHeader;
    // 加载信息提示
    @Bind(R.id.detailLoadingGif)
    CustomLoadingView loadingGif;
    @Bind(R.id.game_rate)
    RatingBar game_rate;
    @Bind(R.id.versionLayout)
    LinearLayout versionLayout;
    @Bind(R.id.detailViewpager)
    ViewPager detailViewPager;
    @Bind(R.id.detailTabs)
    PagerSlidingTabStrip tabsScoreWall;
    @Bind(R.id.detail_content_layout)
    FrameLayout detailContentLayout;
    @Bind(R.id.game_detail_cover)
    ImageView cover;
    private AppInfo appInfo = new AppInfo();
    private AppDetailModel mDetailInfo;
    private boolean wechatShare = true;
    private Bitmap mSharedIcon;
    private ShareItemListener.LoadShareImageTask loadShareImageTask;
    private MarQueenHandler handler;

    private DetailFragmentAdapter mAdapter;
    private float mAlphaTranslation;
    private int mHeaderHeight, mMinHeaderTranslation;
    private int gameId;
    private int[] mRoute;
    private int iFreeArea = AppConstants.FREE_AREA_OUT;
    private View actionbarView;

    private Drawable actionbarBackgroundDrawable;
    private TextView actionbarTitle;
    private ImageView mSharedBtn;

    private float currentAlpha;
    private int tab;
    private boolean autoDownload;
    // 推广活动用 渠道应用下载地址及MD5
    private String downloadUrl;
    private String MD5;

    private ShareDialog shareDialog;
    private AsyncTask queryTask;

    public static final int TAB_DETAIL = 0;
    public static final int TAB_SPREE = 1;
    public static final int TAB_COMMENT = 2;
    private DetailActivity mDetailActivity;

    /**
     * @param gameId    应用id
     * @param iFreeArea 免流量专区
     * @param route     PV用路径记载
     * @return
     */

    public static DetailFragment getInstance(int gameId, int iFreeArea, int[] route) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.APP_KEY, gameId);
        bundle.putInt(AppConstants.KEY_FREEAREA, iFreeArea);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        bundle.putInt(AppConstants.APP_DETAIL_TAB, DetailFragment.TAB_DETAIL);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param gameId       应用id
     * @param iFreeArea    免流量专区
     * @param route        PV用路径记载
     * @param tab          刚进去是不是直接显示礼包标签
     * @param autoDownload 自动下载
     * @return
     */
    public static DetailFragment getInstance(int gameId, int iFreeArea, int[] route, int tab, boolean autoDownload, String downloadUrl, String MD5) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.APP_KEY, gameId);
        bundle.putInt(AppConstants.KEY_FREEAREA, iFreeArea);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        bundle.putInt(AppConstants.APP_DETAIL_TAB, tab);
        bundle.putBoolean(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, autoDownload);
        bundle.putString(AppConstants.APP_DETAIL_URL, downloadUrl);
        bundle.putString(AppConstants.APP_DETAIL_MD5, MD5);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        updateGameSize(event.getAppInfoList(), false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
        // pv统计
        StaticsUtils.onPV(mRoute);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState) {
        View container = inflater.inflate(R.layout.fragment_detail, containers, false);
        ButterKnife.bind(this, container);
        loadingGif.setOnRetryLoad(this);
        return container;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDetailActivity = (DetailActivity) getActivity();
        loadData();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            gameId = bundle.getInt(Constants.APP_KEY);
            iFreeArea = bundle.getInt(AppConstants.KEY_FREEAREA, AppConstants.FREE_AREA_OUT);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
            mRoute[AppConstants.STATISTCS_DEPTH_NINE] = gameId;
            tab = bundle.getInt(AppConstants.APP_DETAIL_TAB, TAB_DETAIL);
            autoDownload = bundle.getBoolean(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, false);
            downloadUrl = bundle.getString(AppConstants.APP_DETAIL_URL);
            MD5 = bundle.getString(AppConstants.APP_DETAIL_MD5);
        }
    }


    private void initBody() {
        RelativeLayout.LayoutParams iconViewParams = (RelativeLayout.LayoutParams) iconView.getLayoutParams();
        if (iconViewParams != null)
            iconViewParams.topMargin += ComUtil.getStatesBarHeight();
        RelativeLayout.LayoutParams iconLabelViewParams = (RelativeLayout.LayoutParams) iconLabelView.getLayoutParams();
        if (iconLabelViewParams != null)
            iconLabelViewParams.topMargin += ComUtil.getStatesBarHeight();
//        RelativeLayout.LayoutParams mTitleLayoutParams = (RelativeLayout.LayoutParams) mTitleLayout.getLayoutParams();
//        if (mTitleLayoutParams != null)
//            mTitleLayoutParams.topMargin += ComUtil.getStatesBarHeight();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mInnerHeader.getLayoutParams();
        if (params != null) {
            params.height += ComUtil.getStatesBarHeight();
        }
        mHeaderHeight = getHeaderHeight();
        mMinHeaderTranslation = -getHeaderTranslateHeight();
        mAlphaTranslation = -mMinHeaderTranslation;
        int tabCount = 2;
        if (mDetailInfo.getcAppType().equals(BaseAppInfo.APP_TYPE_GAME)) {
            tabCount = 3;
            //是否有论坛页
            if (!TextUtils.isEmpty(mDetailInfo.getnFid()) && Integer.valueOf(mDetailInfo.getnFid()) != 0) {
                tabCount = 4;
            }
        }
        mAdapter = new DetailFragmentAdapter(getChildFragmentManager(), this, appInfo, mDetailInfo, mHeaderHeight, tabCount, mRoute);
        detailViewPager.setOffscreenPageLimit(tabCount);
        detailViewPager.setAdapter(mAdapter);
        detailViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    setAllSelected();
                }
            }
        });
        tabsScoreWall.setViewPager(detailViewPager, tabCount, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
                setSelectedPosition(position);
            }
        });
        //如果第二页是礼包界面且当前需要首先显示的是礼包标签则进行以下操作
        if (tab == TAB_SPREE && mDetailInfo.getcAppType().equals(BaseAppInfo.APP_TYPE_GAME)) {
            detailViewPager.setCurrentItem(1);
        }

        if (tab == TAB_COMMENT) {
            detailViewPager.setCurrentItem(mDetailInfo.getcAppType().equals(BaseAppInfo.APP_TYPE_GAME) ? 2 : 1);
        }
    }

    private void setSelectedPosition(int position) {
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
        if (currentHolder != null && mHeader != null) {
            currentHolder.adjustScroll((int) (mHeaderHeight + ViewHelper.getTranslationY(mHeader)));
        }
    }

    private void setAllSelected() {
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mAdapter.getScrollTabHolders();
        for (int i = 0; i < scrollTabHolders.size(); i++) {
            if (i == detailViewPager.getCurrentItem()) continue;
            ScrollTabHolder currentHolder = scrollTabHolders.valueAt(i);
            if (currentHolder != null && mHeader != null) {
                currentHolder.adjustScroll((int) (mHeaderHeight + ViewHelper.getTranslationY(mHeader)));
            }
        }
    }


    /**
     * 顶部Header高度
     * 包含tab栏的高度
     *
     * @return
     */
    private int getHeaderHeight() {
        return getResources().getDimensionPixelSize(R.dimen.detail_header_with_tab_height) + ComUtil.getStatesBarHeight();
    }

    /**
     * 顶部Header高度 去除tab栏的高度
     * 即Header可以滑动的最大高度
     *
     * @return
     */
    private int getHeaderTranslateHeight() {
        return getResources().getDimensionPixelSize(R.dimen.detail_header_translate_height);
    }

    /**
     * 加载游戏详情数据
     */
    private void loadData() {
        showLoading();
        FSRequestHelper.newGetRequest(buildGameDetailUrl(gameId), TAG, AppDetailResp.class, new IFDResponse<AppDetailResp>() {
            @Override
            public void onSuccess(AppDetailResp result) {
                if (result != null && result.getItem() != null) {
                    mDetailInfo = result.getItem();

                    // 推广活动用 渠道应用下载地址及MD5
                    if (!TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(MD5)) {
                        mDetailInfo.setcDownloadUrl(downloadUrl);
                        mDetailInfo.setcMd5(MD5);
                    }
                    bindData(mDetailInfo);
                    fillPatchInfo();
                    showContentView();
                    updateFreeFlowState();

                    // 未安装且为渠道自动安装的情况下，自动开启下载
                    if (autoDownload && !ComUtil.checkApkExist(FreeStoreApp.getContext(), appInfo.getPkgName())) {
                        DownloadHelper.startDownload(FreeStoreApp.getContext(), appInfo);
                    }
                } else {
                    ToastUtils.showMsg(getActivity(), getResources().getString(R.string.app_derail_json_parse_error));
                    getActivity().finish();
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
        }, true);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //分享
            case R.id.btn_shared:
                String shareUrl = PersistentVar.getInstance().getSystemConfig().getShareUrl();
                GlobalVar.getInstance().setShareActivityUrl(shareUrl);
                if (mDetailInfo != null) {
                    shareDialog = new ShareDialog(getActivity());
                    String shareText = getString(R.string.share_good_app);
                    ShareItemListener shareItemListener =
                            new ShareItemListener(getActivity(), shareText + mDetailInfo.getsAppName() + getString(R.string.share_free_tip),
                                    mDetailInfo.getsAppDesc(), getShareUrl(gameId), mSharedIcon, shareDialog,
                                    mDetailInfo.getcIcon(), String.valueOf(gameId), mDetailInfo.getsAppName());
                    shareDialog.setOnItemClickListener(shareItemListener);
                    if (loadShareImageTask != null) {
                        loadShareImageTask.cancel(true);
                    }
                    loadShareImageTask = shareItemListener.getLoadImageTask();
                    shareDialog.show();
                }
                break;
            case R.id.tv_detail_title:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    /**
     * 显示加载界面
     */
    private void showLoading() {
        loadingGif.showLoading();
    }

    /**
     * 隐藏加载界面
     */
    private void hideLoading() {
        loadingGif.hide();
    }

    /**
     * 显示错误界面
     */
    private void showError() {
        loadingGif.showError();
    }

    private void showContentView() {
        initActionBar();
        hideLoading();
        setGameInfoUi();
        initBody();
    }


    private void setGameInfoUi() {
        mGameTitle.setText(mDetailInfo.getsAppName());
        mGameVersionView.setText("V" + mDetailInfo.getcVersionName());
        mGameSizeView.setText(FileUtil.formatFileSize(getActivity(), mDetailInfo.getiSize()));
//        if (PhoneUtilities.getTotalMemorySize(getActivity()) >= 1073741824) {
        BitmapManager.showImg(mDetailInfo.getcIcon(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            Bitmap bitmap = response.getBitmap();
                            iconView.setImageBitmap(response.getBitmap());
                            bitmap = ComUtil.addWhiteBackground(bitmap);
                            Bitmap bluredBitmap = NativeStackBlur.process(bitmap, 20);
//                                FastBlur.doBlur(bitmap, 20);
                            if (bluredBitmap != null) {
                                cover.setImageBitmap(bluredBitmap);
                            }

                            mSharedIcon = bitmap;
                            BitmapUtil.savePicNoCompress(bitmap, ComUtil.getShareImageFile(WechatShare.GAME_IMAGE_NAME).getAbsolutePath());
                        } else {
                            iconView.setImageResource(R.drawable.pic_ragle_loading);
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (getActivity() == null) return;
                        iconView.setImageResource(R.drawable.pic_ragle_loading);
                    }
                }
        );
//        }else{
//            iconView.setImageUrl(mDetailInfo.getcIcon());
//        }

        if (TextUtils.isEmpty(mDetailInfo.getcIconLabel())) {
            iconLabelView.setVisibility(View.GONE);
        } else {
            iconLabelView.setVisibility(View.VISIBLE);
            iconLabelView.setImageUrl(mDetailInfo.getcIconLabel());
        }
    }

    /**
     * 根据返回游戏详情数据 设置title
     */
    private void initActionBar() {
        actionbarView = getView().findViewById(R.id.detail_actionbar_view);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) actionbarView.getLayoutParams();
        if (params != null) {
            params.topMargin = ComUtil.getStatesBarHeight();
        }
        actionbarView.setVisibility(View.VISIBLE);
        actionbarTitle = ButterKnife.findById(actionbarView, R.id.tv_detail_title);
        actionbarTitle.setOnClickListener(this);
        actionbarTitle.setOnClickListener(this);
        actionbarBackgroundDrawable = actionbarView.getBackground();
        actionbarBackgroundDrawable.setAlpha(0);
        mSharedBtn = (ImageView) actionbarView.findViewById(R.id.btn_shared);
        mSharedBtn.setOnClickListener(this);

        handler = new MarQueenHandler();
        handler.sendEmptyMessageDelayed(ACTIONBAR_SELECT, 500);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queryTask != null)
            queryTask.cancel(true);
        if (loadShareImageTask != null)
            loadShareImageTask.cancel(true);

        if (handler != null) {
            handler.removeMessages(ACTIONBAR_SELECT);
            handler.removeMessages(HIDE_MARQUEEN);
        }

        FreeStoreApp.getRequestQueue().cancelAll(TAG);
//        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    private String buildGameDetailUrl(long id) {
        String url = UrlUtils.getAppDetailJsonPath(id, JsonUrl.getJsonUrl().JSON_URL_APP_DETAIL, 4, 1000) + "/detail.json";

        return url;
    }

    private String getShareUrl(long id) {
        String url = UrlUtils.getAppDetailJsonPath(id, JsonUrl.getJsonUrl().JSON_URL_APP_DETAIL, 4, 1000) + "/" + id
                + ".html";
        return url;
    }


    @Override
    public void retryLoad() {
        loadData();
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (detailViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            MainThreadBus.getInstance().post(new ScrollYEvent(Math.max(-scrollY, mMinHeaderTranslation)));
            ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
            currentAlpha = (Math.min(scrollY, mAlphaTranslation) / mAlphaTranslation);
            int alpha = (int) (currentAlpha * 255);
            actionbarBackgroundDrawable.setAlpha(alpha);
            if (mDetailActivity != null)
                mDetailActivity.setStatusbarColor(alpha);
            changeActionbarIcon(currentAlpha);
        }
    }

    private boolean isRed = false;

    private void changeActionbarIcon(float alpha) {
        if (alpha >= 0.9 && !isRed) {
            isRed = true;
            ComUtil.setDrawableLeft(actionbarTitle, R.drawable.detail_ab_back_gray);
//            actionbarBack.setImageResource(R.drawable.detail_ab_back_gray);
            mSharedBtn.setImageResource(R.drawable.detail_ab_share_gray);
            actionbarTitle.setText(mDetailInfo.getsAppName());
        } else if (alpha < 0.9 && isRed) {
            ComUtil.setDrawableLeft(actionbarTitle, R.drawable.ic_back_normal);
//            actionbarBack.setImageResource(R.drawable.ic_back_normal);
            mSharedBtn.setImageResource(R.drawable.detail_ab_share_white);
            actionbarTitle.setText("");
            isRed = false;
        }
    }


    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }
        //2400仅仅是一个站位的数字 其实也可以2400也可以改为c.getHeight() 不过c.getHeight有可能为0导致向下滑动有时会有跳动的感觉
        return -top + firstVisiblePosition * 2400 + headerHeight;
    }

    public void setGameRate(float rate) {
        if (game_rate != null)
            game_rate.setRating(rate);
    }


    /**
     * 根据AppDetailModel 填充AppInfo
     */
    private void bindData(AppDetailModel mDetailInfo) {
        appInfo.setAppId(mDetailInfo.getnAppId());
        appInfo.setAppName(mDetailInfo.getsAppName());
        appInfo.setApkUrl(mDetailInfo.getcDownloadUrl());
        appInfo.setPkgName(mDetailInfo.getcPackage());
        appInfo.setIcon(mDetailInfo.getcIcon());
        appInfo.setApkSize(mDetailInfo.getiSize());
        appInfo.setVersionName(mDetailInfo.getcVersionName());
        appInfo.setVersionCode(Integer.valueOf(mDetailInfo.getiVersionCode()));
        appInfo.setMd5(mDetailInfo.getcMd5());
        appInfo.setcFlowFree(mDetailInfo.getcFlowFree());
        appInfo.setCGameStatus(mDetailInfo.getcStatus());
        appInfo.setiFreeArea(iFreeArea);
        appInfo.setcAppType(mDetailInfo.getcAppType());
        appInfo.setAppointmentStatus(mDetailInfo.getAppointment());
    }

    /**
     * 填充差异更新信息
     */
    private void fillPatchInfo() {
        PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(getActivity(), appInfo.getPkgName());
        if (packageInfo != null && packageInfo.versionCode < appInfo.getVersionCode()) {
            queryTask = MyGameDaoHelper.queryForAppInfoInThread(getActivity(), new MyGameDaoHelper.MyGameCallback() {
                @Override
                public void Callback(List<AppInfo> appInfos) {
                    updateGameSize(appInfos, false);
                }
            });
        }
    }

    /**
     * 检查MyGameTable表是否版本数据与详情页返回数据一致，不一致则更新该表
     *
     * @param appInfos
     * @param isGetUpgradeApk 是否执行更新MyGameTable表，否则会死循环
     */
    private synchronized void updateGameSize(List<AppInfo> appInfos, boolean isGetUpgradeApk) {
        if (appInfo != null) {
            AppInfo updateInfo = null;
            List<AppInfo> updateInfos = GameManageUtil.getUpdateInfos(getActivity(), appInfos, true);
            for (AppInfo info : updateInfos) {
                if (TextUtils.equals(info.getPkgName(), appInfo.getPkgName())) {
                    updateInfo = info;
                    break;
                }
            }
            if (updateInfo == null || updateInfo.getVersionCode() < appInfo.getVersionCode()) {//数据库需要更新
                if (isGetUpgradeApk) {
                    MyGameDaoHelper.getUpgradeApk(getActivity());
                } else if (updateInfo != null) {
                    updateGameSizeView(updateInfo);
                }
            } else {//直接使用该数据库信息
                updateGameSizeView(updateInfo);
            }
        }
    }

    /**
     * 使用MyGameTable表中可更新应用数据更新视图
     *
     * @param updateInfo
     */
    private void updateGameSizeView(final AppInfo updateInfo) {
        final Activity activity = getActivity();
        if (updateInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH) {
            appInfo.setIsPatch(updateInfo.getIsPatch());
            appInfo.setDiffSize(updateInfo.getDiffSize());
            appInfo.setDiffUrl(updateInfo.getDiffUrl());
            appInfo.setDiffMd5(updateInfo.getDiffMd5());

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SpannableStringBuilder stringBuilder;
                    String apkSize = FileUtil.formatFileSize(activity, updateInfo.getApkSize());
                    stringBuilder = new SpannableStringBuilder(apkSize);
                    stringBuilder.setSpan(new StrikethroughSpan(),
                            0, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.append(" ").append(FileUtil.formatFileSize(activity, appInfo.getDiffSize()));
                    stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)),
                            apkSize.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mGameSizeView.setText(stringBuilder);
                }
            });
        }
    }


    private void updateFreeFlowState() {
        if (AccountUtil.isAgentFree(getActivity()) && mDetailInfo != null) {
            QueryFreeGameUtils.queryFreeGame(String.valueOf(appInfo.getAppId()), new FreeGameRefrsh() {
                @Override
                public void refresh(List<FreeGameItem> freeGameItems) {
                    if (freeGameItems == null || freeGameItems.size() == 0) {
                        appInfo.setcFlowFree(AppInfo.FREE_NULL);
                        mDetailInfo.setcFlowFree(AppInfo.FREE_NULL);
                    } else {
                        int length = freeGameItems.size();
                        FreeGameItem freeGameItem;
                        for (int i = 0; i < length; i++) {
                            freeGameItem = freeGameItems.get(i);
                            if (mDetailInfo.getcPackage().equals(freeGameItem.getcPackage())) {
                                appInfo.setcFlowFree(freeGameItem.getcFlowFree());
                                mDetailInfo.setcFlowFree(freeGameItem.getcFlowFree());
                            }
                        }
                    }

                    mFlowFreeView.setGameDetailFlowFreeUI(appInfo);
                    setLayoutMargin();

                    // 免流量状态刷新（免专区下载使用）
                    ((IntroduceFragment) mAdapter.getItem(0)).setAppInfo(appInfo);

                }
            }, TAG);
        } else {    // 显示应用免信息
            mFlowFreeView.setGameDetailFlowFreeUI(appInfo);
            setLayoutMargin();
        }
    }

    // 设置应用名称与版本号之间的间隔
    private void setLayoutMargin() {
        RelativeLayout.LayoutParams rateLP = (RelativeLayout.LayoutParams) game_rate.getLayoutParams();
        RelativeLayout.LayoutParams titleLP = (RelativeLayout.LayoutParams) mGameTitle.getLayoutParams();
        RelativeLayout.LayoutParams versionLP = (RelativeLayout.LayoutParams) versionLayout.getLayoutParams();
        RelativeLayout.LayoutParams ffLP = (RelativeLayout.LayoutParams) mFlowFreeView.getLayoutParams();
        if (mFlowFreeView.img_freestate.getVisibility() == View.VISIBLE) {
            rateLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(8), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            titleLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(8), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            versionLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(5), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            ffLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(5), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
        } else {
            rateLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(10), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            titleLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(10), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            versionLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(10), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
            ffLP.setMargins(ComUtil.dpToPx(0), ComUtil.dpToPx(0), ComUtil.dpToPx(0), ComUtil.dpToPx(0));
        }
        game_rate.setLayoutParams(rateLP);
        mGameTitle.setLayoutParams(titleLP);
        mGameTitle.setIncludeFontPadding(false);
        versionLayout.setLayoutParams(versionLP);
        mFlowFreeView.setLayoutParams(ffLP);
    }


    class MarQueenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTIONBAR_SELECT:
                    actionbarView.setSelected(true);
                    break;

            }

        }
    }


    @Subscribe
    public void onUserInfoChange(UserInfoChangeEvent event) {
        if (LoginSDKUtil.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) {
            //刷新免状态
            updateFreeFlowState();
        }
    }

    @Subscribe
    public void onRateChange(RateChangeEvent event) {
        setGameRate(event.getRate());
    }
}