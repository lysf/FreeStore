package com.snailgame.cjg.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.ui.ImageFullScreenActivity;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.ExpandableTextView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.model.AppAppointmentStateModel;
import com.snailgame.cjg.detail.model.AppDetailModel;
import com.snailgame.cjg.detail.model.AppRecommend;
import com.snailgame.cjg.detail.model.AppRecommendModel;
import com.snailgame.cjg.detail.model.InsteadCharge;
import com.snailgame.cjg.detail.model.ScrollYEvent;
import com.snailgame.cjg.detail.player.VideoPlayActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.AppAppointmentEvent;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.RateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.settings.FeedBackActivity;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.scrolltab.ScrollTabHolderFragment;

/**
 * 应用介绍
 * Created by sunxy on 2014/11/12.
 */
public class IntroduceFragment extends ScrollTabHolderFragment implements View.OnClickListener, CustomLoadingView.RetryLoadListener {
    static String TAG = IntroduceFragment.class.getName();

    private static final int TYPE_APP = 0;//0 代表应用 1代表游戏

    private HeaderViewHolder headerViewHolder;
    //底部Bar
    @Bind(R.id.detail_bottom_bar)
    RelativeLayout mBottomBarView;
    //底部中间按钮
    @Bind(R.id.pb_detail_download)
    ProgressBar mDownloadProgressBar;
    @Bind(R.id.tv_state_download)
    TextView mDownloadStateView;
    @Bind(R.id.game_remove)
    TextView mGameSoldOut;
    @Bind(R.id.game_addition_Container)
    View mGameAdditonContainer;

    private CommonDetailViewHolder holder;
    private String[] mPicUrls;
    private QueryTaskListTask task;

    private View mHeaderView;
    private Activity mContext;

    /*
     * 应用推荐
     */
    private LinearLayout mRecommendLayout;
    private View mRecommendContainer;
    private List<AppInfo> mAppRecommends = new ArrayList<AppInfo>();
    private static final String KEY_APP_INFO = "key_appinfo";
    private static final String KEY_DETAIL_INFO = "key_detail";
    private static final String KEY_HEADER_HEIGHT = "header_height";

    @Bind(R.id.detailListView)
    LoadMoreListView listView;
    private AppInfo mAppInfo;
    private AppDetailModel mDetailInfo;
    private int mHeaderHeight;
    private AbsListView.LayoutParams params;
    private View placeHolder;
    private TextView insteadChargeTitle, insteadChargeSubTitle, chargeBtn;
    private View insteadChargeLayout;
    private List<RecommendViewHolder> mRecommendViewHolders;
    private int headerHeight;
    private int currentAdjustScroll;

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                refreshAppInfos(taskInfo);
            }
            refreshRecommendProgress();
        }
    }

    @Subscribe
    public void appointmentApp(final AppAppointmentEvent event) {
        String parmas = "iId=" + event.getAppId();

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_APP_APPOINTMENT, TAG, BaseDataModel.class, new IFDResponse<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel result) {
                mAppInfo.setHasAppointment(true);
                setBottomBarUi();

                DialogUtils.showOrderSuccessDialog(getActivity());
            }

            @Override
            public void onNetWorkError() {
                ToastUtils.showMsg(getActivity(), getString(R.string.order_fail));
            }

            @Override
            public void onServerError() {
                ToastUtils.showMsg(getActivity(), getString(R.string.order_fail));
            }
        }, parmas);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.detail_header_translate_height);//header可以滑动的最大高度
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.detail_fragment;
    }

    public static IntroduceFragment getInstance(AppInfo appInfo, AppDetailModel mDetailInfo, int mHeaderHeight, int[] route) {
        IntroduceFragment fragment = new IntroduceFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_APP_INFO, appInfo);
        bundle.putParcelable(KEY_DETAIL_INFO, mDetailInfo);
        bundle.putInt(KEY_HEADER_HEIGHT, mHeaderHeight);
        bundle.putIntArray(AppConstants.KEY_ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void handleIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mAppInfo = bundle.getParcelable(KEY_APP_INFO);
            mDetailInfo = bundle.getParcelable(KEY_DETAIL_INFO);
            mHeaderHeight = bundle.getInt(KEY_HEADER_HEIGHT);
            mRoute = bundle.getIntArray(AppConstants.KEY_ROUTE);
        }
    }

    @Override
    protected void initView() {
        styleListView();
        addHeaderView();
        initHeaderUi();
        initBottomBarUi();
        showInsteadChargeView();
        initUi();
    }

    private void initUi() {
        if (mAppInfo != null) {
            setGameInfoUi();
            setWebPhoneBtn();
            refreshUpgradeStateBtn();
        }
    }

    private void showInsteadChargeView() {
        if (mAppInfo != null) {
            HashMap<Integer, InsteadCharge> insteadChargeHashMap = GlobalVar.getInstance().getInsteadChargeArrayMap();
            if (insteadChargeHashMap != null) {
                final InsteadCharge insteadCharge = insteadChargeHashMap.get(mAppInfo.getAppId());
                if (insteadCharge != null) {
                    insteadChargeLayout.setVisibility(View.VISIBLE);
                    insteadChargeTitle.setText(Html.fromHtml(insteadCharge.getsGuideTitle()));
                    insteadChargeSubTitle.setText(insteadCharge.getsGuideSubtitle());
                    chargeBtn.setText(insteadCharge.getsGuideButton());
                    chargeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobclickAgent.onEvent(getActivity(), UmengAnalytics.EVENT_APP_DETAIL_RECHARGE);
                            getActivity().startActivity(WebViewActivity.newIntent(getActivity(), insteadCharge.getsDesc()));
                        }
                    });
                }
            } else {

            }
        }
    }

    private void styleListView() {
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setPagePosition(0);
    }

    private void addHeaderView() {
        placeHolder = new View(getActivity());
        params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight);
        placeHolder.setLayoutParams(params);
        listView.addHeaderView(placeHolder);

        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.detail_fragment_header, null);
        headerViewHolder = new HeaderViewHolder(mHeaderView);
        listView.addHeaderView(mHeaderView);

        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.introduce_layout, R.id.introduce_text, new String[]{""}));
        listView.setScrollHolder(mScrollTabHolder);
    }

    private void initHeaderUi() {
        insteadChargeLayout = mHeaderView.findViewById(R.id.insteadChargeLayout);
        insteadChargeTitle = (TextView) mHeaderView.findViewById(R.id.charge_title);
        insteadChargeSubTitle = (TextView) mHeaderView.findViewById(R.id.charge_des);
        chargeBtn = (TextView) mHeaderView.findViewById(R.id.charge_btn);

        mRecommendLayout = (LinearLayout) mHeaderView.findViewById(R.id.layout_recommend);
        mRecommendContainer = mHeaderView.findViewById(R.id.recommend_container);
        headerViewHolder.mPhoneBtn.setOnClickListener(this);
        headerViewHolder.mFeedBackBtn.setOnClickListener(this);
    }


    private void initBottomBarUi() {
        mDownloadProgressBar.setOnClickListener(this);
        mDownloadStateView.setClickable(false);
        holder = new CommonDetailViewHolder(mContext, mBottomBarView);

        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_DETAIL;
            mAppInfo.setRoute(route);
            holder.setAppInfo(mAppInfo);
        }
        if (Integer.valueOf(mDetailInfo.getcAppType()) == TYPE_APP)
            mGameSoldOut.setText(getString(R.string.app_remove));
    }

    @Override
    protected void loadData() {
        loadDetailData();

        //等获取数据完毕后加载
        if (IdentityHelper.isLogined(getActivity()) &&
                !TextUtils.isEmpty(mAppInfo.getAppointmentStatus()) &&
                mAppInfo.getAppointmentStatus().equals(DownloadViewHolder.APP_APPOINTMENT_Y)) {

            loadAppointmentState();
        }
    }

    private void loadDetailData() {
        getFavouriteApp();
    }

    @Override
    protected LoadMoreListView getListView() {
        return listView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void saveData(Bundle outState) {

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            //下载按钮
            case R.id.pb_detail_download:
                holder.button.performClick();
                break;
            //客服
            case R.id.phone_btn:
                try {
                    String phoneNumber = mDetailInfo.getcServicePhone().trim();
                    if (TextUtils.isEmpty(phoneNumber)) {
                        ToastUtils.showMsg(mContext, getString(R.string.service_phone_lose));
                    } else {
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    }
                } catch (Exception e) {

                }

                break;
            //反馈
            case R.id.feedback_btn:
                startActivity(FeedBackActivity.newIntent(mContext, String.valueOf(mDetailInfo.getnAppId())));
                break;
            default:
                break;
        }
    }


    private void refreshUI(boolean isLoadEnd) {
        refreshRecommendProgress();
        if (mDetailInfo == null) {
            if (isLoadEnd)
                showError();
            return;
        }
        setBottomBarUi();
    }

    private void setGameInfoUi() {
        if (mDetailInfo.getcPicUrl() != null) {
            mPicUrls = mDetailInfo.getcPicUrl().split("\\|");
            setScreenShotView(mPicUrls);
        }
        String content = mDetailInfo.getsBroadcast();
        if (!TextUtils.isEmpty(content)) {
            //显示小编广播
            headerViewHolder.broadcast_text.setVisibility(View.VISIBLE);
            headerViewHolder.broadcast_text.setText(Html.fromHtml(content.trim()));
            headerViewHolder.broadcast_text.setMovementMethod(LinkMovementMethod.getInstance());
            addLinkClick(headerViewHolder.broadcast_text);
            headerViewHolder.tag_layout.setPadding(0, ComUtil.dpToPx(10), 0, 0);
        } else {
            headerViewHolder.broadcast_text.setVisibility(View.GONE);
            headerViewHolder.tag_layout.setPadding(0, ComUtil.dpToPx(16), 0, 0);
        }

        List<String> tags = parseTag(mDetailInfo.getcTags());
        if (!ListUtils.isEmpty(tags)) {
            headerViewHolder.tag_layout.setVisibility(View.VISIBLE);
            seTagView(tags);
        } else
            headerViewHolder.tag_layout.setVisibility(View.GONE);


        if (TextUtils.isEmpty(mDetailInfo.getsUpdateDesc())) {
            headerViewHolder.desc_layout.setVisibility(View.GONE);
            headerViewHolder.mUpgradeContent.setText(mDetailInfo.getsAppDesc().trim());
        } else {
            headerViewHolder.desc_layout.setVisibility(View.VISIBLE);
            headerViewHolder.mGameDescView.setText(mDetailInfo.getsAppDesc().trim());
            SpannableString spannableString = new SpannableString(
                    ResUtil.getString(R.string.app_upgrade) + mDetailInfo.getsUpdateDesc().trim());
            spannableString.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)), 0,
                    String.valueOf(ResUtil.getString(R.string.app_upgrade)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            headerViewHolder.mUpgradeContent.setText(spannableString);
        }
    }


    private void addLinkClick(TextView tv) {
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tv.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setText(style);
        }
    }


    private void setCenterBottomBtn(double percent) {
        boolean isShowProgressBar = mAppInfo.getDownloadState() == DownloadManager.STATUS_RUNNING ||
                mAppInfo.getDownloadState() == DownloadManager.STATUS_PAUSED;
        if (holder != null && holder.button != null) {
            holder.button.setVisibility(isShowProgressBar ? View.GONE : View.VISIBLE);
        }

        mDownloadProgressBar.setVisibility(isShowProgressBar ? View.VISIBLE : View.GONE);
        mDownloadStateView.setVisibility(isShowProgressBar ? View.VISIBLE : View.GONE);
        mDownloadProgressBar.setProgress((int) percent);
        mDownloadProgressBar.setBackgroundResource(R.drawable.detail_progress_bar_bg);
        mDownloadProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.detail_progress_background));

        if (mAppInfo.getDownloadState() == DownloadManager.STATUS_RUNNING) {
            mDownloadStateView.setBackgroundColor(ResUtil.getColor(R.color.translucent_full));
            mDownloadStateView.setText(getString(R.string.downloading_percent, percent));
        } else if (mAppInfo.getDownloadState() == DownloadManager.STATUS_PAUSED) {
            mDownloadStateView.setText(getResources().getString(R.string.list_item_continue));
            mDownloadStateView.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_yellow_selector));
            mDownloadProgressBar.setBackgroundResource(R.drawable.detail_progress_pause_bg);
            mDownloadProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.detail_progress_pause_background));
        }

    }


    /**
     * 电话 和 官网 按钮 显示
     */
    private void setWebPhoneBtn() {
        headerViewHolder.mPhoneBtn.setVisibility(View.VISIBLE);
        headerViewHolder.mFeedBackBtn.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(mDetailInfo.getcServicePhone())) {
            headerViewHolder.mPhoneNumView.setVisibility(View.VISIBLE);
            headerViewHolder.mPhoneNumView.setText(mDetailInfo.getcServicePhone());
        } else {
            headerViewHolder.mPhoneNumView.setVisibility(View.GONE);
        }
    }


    private void setBottomBarUi() {
        mBottomBarView.setVisibility(View.VISIBLE);

        if (Integer.valueOf(mAppInfo.getCGameStatus()) == 1) {
            if (holder != null) {
                holder.setAppInfo(mAppInfo);

                int downloadState = mAppInfo.getDownloadState();
                DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
                if (downloadState != DownloadManager.STATUS_EXTRA_INSTALLED
                        && downloadState != DownloadManager.STATUS_SUCCESSFUL) {
                }

            }
        } else {
            mGameAdditonContainer.setVisibility(View.GONE);
            mGameSoldOut.setVisibility(View.VISIBLE);
        }


        setCenterBottomBtn(mAppInfo.getDownloadedPercent());
    }

    /**
     * 设置应用截图显示
     *
     * @param picUrls
     */
    private void setScreenShotView(String[] picUrls) {
        headerViewHolder.mGameScreenShotView.removeAllViews();
        for (int i = 0; i < mPicUrls.length; i++) {
            FSSimpleImageView imageView = new FSSimpleImageView(FreeStoreApp.getContext());
            imageView.setControllerOverlayAndPlaceHolder();
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageUrl(picUrls[i]);
            imageView.setOnClickListener(new StartFullScreesImages(i));
            LinearLayout.LayoutParams paramas = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.game_detail_screenshot_width)
                    , getResources().getDimensionPixelSize(R.dimen.game_detail_screenshot_height));
            if (i + 1 == mPicUrls.length) {
                paramas.setMargins(ComUtil.dpToPx(8), 0, ComUtil.dpToPx(8), 0);
            } else {
                paramas.setMargins(ComUtil.dpToPx(8), 0, 0, 0);
            }
            headerViewHolder.mGameScreenShotView.addView(imageView, paramas);
        }
        if (!TextUtils.isEmpty(mDetailInfo.getcNatVideoPic())) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.video_thum, null);
            FSSimpleImageView video_thum = (FSSimpleImageView) view.findViewById(R.id.video_thum);
            video_thum.setImageUrl(mDetailInfo.getcNatVideoPic());
            headerViewHolder.mGameScreenShotView.addView(view, 0);
            video_thum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String video_uri = mDetailInfo.getcNatVideo();
                    Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                    intent.putExtra(AppConstants.VIDEO_URL, video_uri);
                    intent.putExtra(AppConstants.COMPANY_NAME, mDetailInfo.getsAppName());
                    startActivity(intent);
                }
            });
        }
    }


    /**
     * 设置应用标签显示
     *
     * @param tags
     */
    private void seTagView(List<String> tags) {
        headerViewHolder.tag_layout.removeAllViews();
        for (int i = 0; i < tags.size() && i < 3; i++) {
            TextView textView = new TextView(FreeStoreApp.getContext());
            textView.setText(tags.get(i));
            switch (i) {
                case 0:
                    textView.setBackgroundResource(R.drawable.app_tag_one);
                    break;
                case 1:
                    textView.setBackgroundResource(R.drawable.app_tag_two);
                    break;
                case 2:
                    textView.setBackgroundResource(R.drawable.app_tag_three);
                    break;
                default:
                    break;
            }
            LinearLayout.LayoutParams paramas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ComUtil.dpToPx(18));
            if (i == 0) {
                paramas.setMargins(ComUtil.dpToPx(0), 0, 0, 0);
            } else {
                paramas.setMargins(ComUtil.dpToPx(8), 0, 0, 0);
            }
            textView.setSingleLine();
            textView.setMaxEms(10);
            textView.setTextSize(10);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(ComUtil.dpToPx(8), 0, ComUtil.dpToPx(8), 0);
            headerViewHolder.tag_layout.addView(textView, paramas);
        }
    }

    /**
     * 更新跟升级相关的按钮状态
     */
    private void refreshUpgradeStateBtn() {
        if (holder != null && holder.button != null)
            holder.button.setVisibility(View.VISIBLE);
    }

    /**
     * 下载更新进度条
     *
     * @param taskInfo
     */
    private void setDownloadProgress(TaskInfo taskInfo, AppInfo appInfo) {
        if (taskInfo != null && appInfo != null && taskInfo.getAppId() == appInfo.getAppId()) {
            int newState = taskInfo.getDownloadState();
            if (appInfo.getDownloadState() != newState) {
                if (newState == DownloadManager.STATUS_SUCCESSFUL) {
                    PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(mContext, appInfo.getPkgName());
                    if (packageInfo != null && packageInfo.versionCode == appInfo.getVersionCode()) {
                        newState = DownloadManager.STATUS_EXTRA_INSTALLED;
                    }
                }
                appInfo.setDownloadState(newState);
            }
            appInfo.setApkDownloadId(taskInfo.getTaskId());
            appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
            appInfo.setLocalUri(taskInfo.getApkLocalUri());
            int reason = taskInfo.getReason();
            DownloadHelper.handleMsgForPauseOrError(FreeStoreApp.getContext(), appInfo.getAppName(), newState, reason);
            if (appInfo.getAppId() == mAppInfo.getAppId()) {
                if (holder != null)
                    DownloadWidgetHelper.getHelper().switchState(newState, holder);
                setCenterBottomBtn(taskInfo.getTaskPercent());
                refreshUpgradeStateBtn();
            }
        }
    }


    /**
     * 刷新页面内应用下载状态
     */
    private void refreshAppInfos(TaskInfo taskInfo) {
        setDownloadProgress(taskInfo, mAppInfo);

        if (ListUtils.isEmpty(mRecommendViewHolders))
            return;

        for (RecommendViewHolder holder : mRecommendViewHolders) {
            setDownloadProgress(taskInfo, holder.appInfo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryTask();
    }


    public void queryTask() {
        if (task != null)
            task.cancel(true);
        task = new QueryTaskListTask();
        task.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);

        if (headerViewHolder != null && headerViewHolder.mGameScreenShotView != null) {
            headerViewHolder.mGameScreenShotView.removeAllViews();
        }

        if (mRecommendLayout != null) {
            mRecommendLayout.removeAllViews();
        }

        if (task != null)
            task.cancel(true);
    }


    @Override
    public void retryLoad() {
        loadData();
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if ((scrollHeight == 0 && listView.getFirstVisiblePosition() >= 1)
                || (topScroll == -headerHeight && currentAdjustScroll == scrollHeight && scrollHeight == mHeaderHeight - headerHeight)) {
            return;
        }
        listView.setSelectionFromTop(1, scrollHeight - getResources().getDimensionPixelSize(R.dimen.divider_height));
        currentAdjustScroll = scrollHeight;
    }


    private class StartFullScreesImages implements View.OnClickListener {
        private int position;

        public StartFullScreesImages(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            startActivity(ImageFullScreenActivity.newIntent(mContext, position, mPicUrls));
        }
    }


    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mAppInfo == null && ListUtils.isEmpty(mRecommendViewHolders)) {
                return false;
            }
            List<AppInfo> appInfos = new ArrayList<AppInfo>();
            if (mAppInfo != null)
                appInfos.add(mAppInfo);

            if (!ListUtils.isEmpty(mRecommendViewHolders)) {
                for (RecommendViewHolder holder : mRecommendViewHolders) {
                    if (holder.appInfo != null)
                        appInfos.add(holder.appInfo);
                }
            }
            AppInfoUtils.updateDownloadState(mContext, appInfos);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                refreshUI(false);
            }
        }
    }


    // 刷新推荐列表
    private void refreshRecommendView() {
        if (mRecommendLayout != null) {
            mRecommendLayout.removeAllViews();
            if (mRecommendViewHolders == null)
                mRecommendViewHolders = new ArrayList<>();
            mRecommendViewHolders.clear();
            if (mAppRecommends == null || mAppRecommends.size() == 0) {
                mRecommendLayout.setVisibility(View.GONE);
                mRecommendContainer.setVisibility(View.GONE);
                return;
            } else {
                mRecommendLayout.setVisibility(View.VISIBLE);
                mRecommendContainer.setVisibility(View.VISIBLE);
            }

            int count = mAppRecommends.size();
            for (int i = 0; i < count; i++) {
                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.guess_what_you_like, null);
                RecommendViewHolder holder = new RecommendViewHolder(mContext, view);
                showRecommendContent(holder, mAppRecommends.get(i));
                mRecommendViewHolders.add(holder);
                view.setOnClickListener(new ClickFavouriteApp(i));
                LinearLayout.LayoutParams paramas = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramas.weight = 1;
                view.setLayoutParams(paramas);

                mRecommendLayout.addView(view);
            }

            refreshRecommendProgress();
        }
    }

    /**
     * 应用推荐显示
     *
     * @param holder
     * @param appInfo
     */
    private void showRecommendContent(final RecommendViewHolder holder, final AppInfo appInfo) {
        holder.appIcon.setImageUrl(appInfo.getIcon());
        holder.appTitle.setText(appInfo.getAppName());
        holder.appSize.setText(FileUtil.formatFileSize(getActivity(), appInfo.getApkSize()));
        holder.setAppInfo(appInfo);

        holder.mDownloadStateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.button.performClick();
            }
        });

    }

    /**
     * AppRecommend 填充AppInfo
     */
    private AppInfo bindData(AppRecommend appRecommend) {
        if (appRecommend == null)
            return null;

        AppInfo appInfo = new AppInfo();

        appInfo.setAppId(appRecommend.getnAppId());
        appInfo.setAppName(appRecommend.getsAppName());
        appInfo.setApkUrl(appRecommend.getcDownloadUrl());
        appInfo.setPkgName(appRecommend.getcPackage());
        appInfo.setIcon(appRecommend.getcIcon());
        appInfo.setApkSize(appRecommend.getiSize());
        appInfo.setVersionName(appRecommend.getcVersionName());
        appInfo.setVersionCode(appRecommend.getiVersionCode());
        appInfo.setMd5(appRecommend.getcMd5());
        appInfo.setcAppType(appRecommend.getcAppType());

        return appInfo;
    }

    // 刷新推荐应用进度条
    private void refreshRecommendProgress() {
        if (ListUtils.isEmpty(mRecommendViewHolders))
            return;

        for (RecommendViewHolder holder : mRecommendViewHolders) {
            if (holder.appInfo == null)
                continue;

            int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(holder.appInfo, mContext);
            DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
            refreshDownloadBtn(holder.appInfo, holder, downloadState);
        }
    }

    /**
     * 更改按以及进度条钮状态
     *
     * @param appInfo
     * @param holder
     * @param downloadState
     */
    private void refreshDownloadBtn(AppInfo appInfo, RecommendViewHolder holder, int downloadState) {
        int percent = (int) appInfo.getDownloadedPercent();

        if (downloadState == DownloadManager.STATUS_RUNNING) {
            //隐藏下载按钮
            holder.button.setVisibility(View.GONE);
            holder.mDownloadProgressBar.setVisibility(View.VISIBLE);
            holder.mDownloadStateView.setVisibility(View.VISIBLE);

            holder.mDownloadProgressBar.setProgress(percent);
            holder.mDownloadProgressBar.setBackgroundResource(R.drawable.detail_progress_bar_bg);
            holder.mDownloadProgressBar.setProgressDrawable(ResUtil.getDrawable(R.drawable.detail_progress_background));

            holder.mDownloadStateView.setBackgroundColor(ResUtil.getColor(R.color.translucent_full));
            holder.mDownloadStateView.setText(percent + "%");
        } else {
            //隐藏下载进度条
            holder.mDownloadProgressBar.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
            holder.mDownloadStateView.setVisibility(View.GONE);
        }

    }


    // 获取猜你喜欢应用
    private void getFavouriteApp() {
        String url = JsonUrl.getJsonUrl().JSON_RECOMMEND_GAME + "?iAppId=" + mDetailInfo.getnAppId()
                + "&iCategoryId=" + mDetailInfo.getiCategoryId() + "&iPlatformId=" + AppConstants.PLATFORM_ID;

        FSRequestHelper.newGetRequest(url, TAG, AppRecommendModel.class, new IFDResponse<AppRecommendModel>() {
            @Override
            public void onSuccess(AppRecommendModel result) {
                if (result == null)
                    return;

                getGameRate(result.getVal());

                if (result.getInfos() != null && result.getInfos().size() > 0) {
                    getRecommendList(result.getInfos());
                    refreshRecommendView();
                } else {
                    mRecommendLayout.setVisibility(View.GONE);
                    mRecommendContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNetWorkError() {
                showErrorView();
            }

            @Override
            public void onServerError() {
                showErrorView();
            }
        }, true, true, new ExtendJsonUtil());

    }


    private void loadAppointmentState() {
        String url = JsonUrl.getJsonUrl().JSON_URL_APPOINTMENT_STATE + "?iAppId=" + mDetailInfo.getnAppId();

        FSRequestHelper.newGetRequest(url, TAG, AppAppointmentStateModel.class, new IFDResponse<AppAppointmentStateModel>() {
            @Override
            public void onSuccess(AppAppointmentStateModel result) {
                if (result.getCode() == 0) {
                    try {
                        JSONObject json = JSON.parseObject(result.getItem());
                        mAppInfo.setHasAppointment(json.getBooleanValue(String.valueOf(mDetailInfo.getnAppId())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setBottomBarUi();
            }

            @Override
            public void onNetWorkError() {

            }

            @Override
            public void onServerError() {

            }
        }, false);
    }

    private void showErrorView() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecommendLayout.setVisibility(View.GONE);
                mRecommendContainer.setVisibility(View.GONE);
            }
        });
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            startActivityForResult(WebViewActivity.newIntent(getActivity(), mUrl), 0);
        }
    }

    private void getGameRate(String val) {
        if (TextUtils.isEmpty(val))
            return;

        try {
            float gameLevel = (float) (JSON.parseObject(val).getDoubleValue("nScore"));
            MainThreadBus.getInstance().post(new RateChangeEvent(gameLevel));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 从推荐列表中随机获取应用
    private void getRecommendList(List<AppRecommend> infos) {
        mAppRecommends.clear();
        int indexs[] = ComUtil.getRandomValue(infos.size(), 3);
        for (int index : indexs) {
            mAppRecommends.add(bindData(infos.get(index)));
        }
        AppInfoUtils.updateDownloadState(mContext, mAppRecommends);
    }

    private class ClickFavouriteApp implements View.OnClickListener {
        private int position;

        public ClickFavouriteApp(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            AppInfo info = mAppRecommends.get(position);
            if (info != null) {
                transFragment(Integer.valueOf(info.getAppId()), position);
            }
        }
    }

    private void transFragment(int gameId, int position) {
        int[] route = createRoute();
        route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
        DetailFragment fragment = DetailFragment.getInstance(gameId, mAppInfo.getiFreeArea(), route);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        transaction.add(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void setAppInfo(AppInfo appInfo) {
        this.mAppInfo = appInfo;
    }

    public static class CommonDetailViewHolder extends DownloadViewHolder implements View.OnClickListener {
        public CommonDetailViewHolder(Context mContext, View parentView) {
            super(mContext, parentView);
        }
    }

    class HeaderViewHolder {
        @Bind(R.id.broadcast_text)
        TextView broadcast_text;

        @Bind(R.id.tag_layout)
        LinearLayout tag_layout;
        //头部信息部分
        @Bind(R.id.layoutDetailImage)
        LinearLayout mGameScreenShotView;

        @Bind(R.id.layout_desc)
        LinearLayout desc_layout;
        //游戏摘要
        @Bind(R.id.game_des)
        ExpandableTextView mGameDescView;

        @Bind(R.id.upgrade_content)
        ExpandableTextView mUpgradeContent;

        //头部 官网 电话 反馈
        @Bind(R.id.phone_btn)
        View mPhoneBtn;
        @Bind(R.id.feedback_btn)
        View mFeedBackBtn;
        @Bind(R.id.tv_phone_number)
        TextView mPhoneNumView;

        public HeaderViewHolder(View headerView) {
            ButterKnife.bind(this, headerView);
//            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class RecommendViewHolder extends DownloadViewHolder {
        @Bind(R.id.app_icon)
        FSSimpleImageView appIcon;
        @Bind(R.id.app_title)
        TextView appTitle;
        @Bind(R.id.app_size)
        TextView appSize;

        @Bind(R.id.pb_detail_download)
        ProgressBar mDownloadProgressBar;
        @Bind(R.id.tv_state_download)
        TextView mDownloadStateView;
        public View itemView;

        public RecommendViewHolder(Context context, View itemView) {
            super(context, itemView);
            this.itemView = itemView;
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 游戏详情
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_DETAIL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_THIRD_RELATED,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    /**
     * 解析应用标签
     *
     * @param cTags
     * @return
     */
    private List<String> parseTag(String cTags) {
        List<String> tags = new ArrayList<>();
        if (TextUtils.isEmpty(cTags))
            return tags;

        String[] tag = cTags.split(",");
        if (tag != null && tag.length != 0) {
            for (int index = 0; index < tag.length && index < 3; index++) {
                if (TextUtils.isEmpty(tag[index]))
                    continue;

                String[] group = tag[index].split(":");
                if (group != null && group.length == 2 && !TextUtils.isEmpty(group[1]))
                    tags.add(group[1].replace("\"", "").replace("{", "").replace("}", ""));
            }
        }

        return tags;
    }

    int topScroll;

    @Subscribe
    public void scrollY(ScrollYEvent event) {
        topScroll = event.getScrollY();
        if (getUserVisibleHint()) {
            currentAdjustScroll = mHeaderHeight - Math.abs(topScroll);
        }
    }
}