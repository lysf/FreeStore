package com.snailgame.cjg.guide;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.CustomLoadingView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.detail.DetailFragment;
import com.snailgame.cjg.detail.model.AppDetailModel;
import com.snailgame.cjg.detail.model.AppDetailResp;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 渠道应用下载
 * Created by yanHH on 2016/5/12.
 */
public class ChannelAppActivity extends BaseFSActivity implements CustomLoadingView.RetryLoadListener {
    public static final String KEY_SPREE_TITLE = "spree_title"; // 礼包标题
    public static final String KEY_SPREE_DESC = "spree_desc"; // 礼包描述
    private final String TAG = ChannelAppActivity.class.getSimpleName();

    @Bind(R.id.rl_detail_content)
    RelativeLayout rl_detail_content;
    //应用图片
    @Bind(R.id.appinfo_icon)
    FSSimpleImageView appinfo_icon;
    //应用名称
    @Bind(R.id.appinfo_title)
    TextView appinfo_title;

    //下载进度部分
    @Bind(R.id.view_channel_app_progress)
    View progressView;
    @Bind(R.id.pb_download_percent)
    ProgressBar mDownloadProgressBar;
    @Bind(R.id.tv_download_speed)
    TextView tvDownloadSpeed; // 下载速度
    @Bind(R.id.tv_download_size)
    TextView tvDownloadedSize; // 下载大小、文件大小
    @Bind(R.id.tv_install)
    TextView tv_install;//安装
    @Bind(R.id.tv_download_error)
    TextView tv_download_error;//下载失败提示

    //礼包
    @Bind(R.id.view_channel_app_gift)
    View view_channel_app_gift;
    @Bind(R.id.tv_spree_title)
    TextView tv_spree_title;
    @Bind(R.id.tv_spree_desc)
    TextView tv_spree_desc;


    @Bind(R.id.detailLoadingGif)// 加载信息提示
            CustomLoadingView loadingGif;

    // 推广活动用 渠道应用下载地址及MD5
    private int gameId;
    private int[] mRoute;
    private boolean autoDownload;
    private String downloadUrl;
    private String MD5;
    private String spreeTitle = null;//礼包标题
    private String spreeDesc = null;//礼包描述

    private AppInfo appInfo = new AppInfo();
    private AppDetailModel mDetailInfo;
    private QueryTaskListTask task;
    private CommonDetailViewHolder holder;

    @OnClick({R.id.tv_channel_app_go_home, R.id.tv_get_gift, R.id.iv_control, R.id.tv_install})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_channel_app_go_home://进入首页
                finish();
                break;
            case R.id.tv_get_gift://获取礼包
                startActivity(DetailActivity.newIntent(this, gameId, mRoute, DetailFragment.TAB_SPREE));
                finish();
                break;
            case R.id.iv_control://下载控制
                holder.iv_control.performClick();
                break;
            case R.id.tv_install://安装
                holder.iv_control.performClick();
                break;
        }
    }

    /**
     * @param context      上下文
     * @param appId        应用id
     * @param route        PV用路径记载
     * @param autoDownload 是否自动下载
     * @param isOutSideIn  是否外部跳转
     * @param downloadUrl  下载地址(活动推广)
     * @param MD5          MD5(活动推广)
     * @param spreeTitle   礼包标题
     * @param spreeDesc    礼包描述
     * @return
     */
    public static Intent newIntent(Context context, int appId, int[] route, boolean autoDownload, boolean isOutSideIn, String downloadUrl, String MD5, String spreeTitle, String spreeDesc) {
        Intent intent = new Intent(context, ChannelAppActivity.class);
        intent.putExtra(AppConstants.APP_DETAIL_APPID, appId);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        intent.putExtra(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, autoDownload);
        intent.putExtra(AppConstants.APP_DETAIL_URL, downloadUrl);
        intent.putExtra(AppConstants.APP_DETAIL_MD5, MD5);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        intent.putExtra(KEY_SPREE_TITLE, spreeTitle);
        intent.putExtra(KEY_SPREE_DESC, spreeDesc);
        return intent;
    }

    @Override
    protected void handleIntent() {
        gameId = getIntent().getIntExtra(AppConstants.APP_DETAIL_APPID, 0);
        mRoute = getIntent().getIntArrayExtra(AppConstants.KEY_ROUTE);
        autoDownload = getIntent().getBooleanExtra(AppConstants.APP_DETAIL_AUTO_DOWNLOAD, false);
        downloadUrl = getIntent().getStringExtra(AppConstants.APP_DETAIL_URL);
        MD5 = getIntent().getStringExtra(AppConstants.APP_DETAIL_MD5);
        spreeTitle = getIntent().getStringExtra(KEY_SPREE_TITLE);
        spreeDesc = getIntent().getStringExtra(KEY_SPREE_DESC);
    }

    @Override
    protected void initView() {
        if (TextUtils.isEmpty(spreeTitle) && TextUtils.isEmpty(spreeDesc)) {
            view_channel_app_gift.setVisibility(View.GONE);
        } else {
            view_channel_app_gift.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(spreeTitle)) {
                tv_spree_title.setText(Html.fromHtml(spreeTitle));
                tv_spree_title.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(spreeDesc)) {
                tv_spree_desc.setText(Html.fromHtml(spreeDesc));
                tv_spree_desc.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void loadData() {
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
                    showContentView();
                    // 未安装且为渠道自动安装的情况下，自动开启下载
                    if (autoDownload && !ComUtil.checkApkExist(FreeStoreApp.getContext(), appInfo.getPkgName())) {
                        DownloadHelper.startDownload(FreeStoreApp.getContext(), appInfo);
                    }
                } else {
                    ToastUtils.showMsg(ChannelAppActivity.this, getResources().getString(R.string.app_derail_json_parse_error));
                    finish();
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
    protected int getLayoutResId() {
        return R.layout.activity_channel_app;
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryTask();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_CHANNEL_APP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_CHANNEL_APP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFullScreen();
        // pv统计
        StaticsUtils.onPV(mRoute);
        loadingGif.setOnRetryLoad(this);
        MainThreadBus.getInstance().register(this);
    }

    //获取详情URL
    private String buildGameDetailUrl(long id) {
        String url = UrlUtils.getAppDetailJsonPath(id, JsonUrl.getJsonUrl().JSON_URL_APP_DETAIL, 4, 1000) + "/detail.json";

        return url;
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
        appInfo.setiFreeArea(AppConstants.FREE_AREA_OUT);
        appInfo.setcAppType(mDetailInfo.getcAppType());
        appInfo.setAppointmentStatus(mDetailInfo.getAppointment());
        queryTask();
    }


    private void showContentView() {
        hideLoading();
        setGameInfoUi();
        holder = new CommonDetailViewHolder(this, progressView);
        if (holder.iv_control != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_DETAIL;
            appInfo.setRoute(route);
            holder.iv_control.setTag(R.id.tag_first, appInfo);
        }
    }

    private void setGameInfoUi() {
        rl_detail_content.setVisibility(View.VISIBLE);
        appinfo_title.setText(mDetailInfo.getsAppName());
        appinfo_icon.setImageUrl(mDetailInfo.getcIcon());
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

    @Override
    public void retryLoad() {
        loadData();
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                setDownloadProgress(taskInfo);
            }
        }
    }

    /**
     * 下载更新进度条
     *
     * @param taskInfo
     */
    private void setDownloadProgress(TaskInfo taskInfo) {
        if (taskInfo != null && appInfo != null && taskInfo.getAppId() == appInfo.getAppId()) {
            int newState = taskInfo.getDownloadState();
            if (appInfo.getDownloadState() != newState) {
                if (holder != null) {
                    if (newState == DownloadManager.STATUS_SUCCESSFUL) {
                        PackageInfo packageInfo = PackageInfoUtil.getPackageInfoByName(this, appInfo.getPkgName());
                        if (packageInfo != null && packageInfo.versionCode == appInfo.getVersionCode()) {
                            newState = DownloadManager.STATUS_EXTRA_INSTALLED;

                        }
                    }
                    DownloadWidgetHelper.getHelper().switchState(newState, holder);
                }
                appInfo.setDownloadState(newState);
            }
            DownloadHelper.calcDownloadSpeed(this, appInfo, taskInfo);
            appInfo.setApkDownloadId(taskInfo.getTaskId());
            appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
            appInfo.setLocalUri(taskInfo.getApkLocalUri());
            appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
            int reason = taskInfo.getReason();
            DownloadHelper.handleMsgForPauseOrError(FreeStoreApp.getContext(), appInfo.getAppName(), newState, reason);
            setCenterBottomBtn((int) taskInfo.getTaskPercent());
            setupDownloadspeed(appInfo);
        }
    }

    private void setCenterBottomBtn(int percent) {
        if (holder != null) {
            DownloadWidgetHelper.getHelper().switchState(appInfo.getDownloadState(), holder);
        }
        int state = appInfo.getDownloadState();
        tv_download_error.setVisibility(View.INVISIBLE);
        boolean isHideProgressBar = state == DownloadManager.STATUS_SUCCESSFUL ||
                state == DownloadManager.STATUS_EXTRA_INSTALLED || state == DownloadManager.STATUS_FAILED ||
                state == DownloadManager.STATUS_INSTALLING || state == DownloadManager.STATUS_EXTRA_UPGRADABLE;
        switch (state) {
            case DownloadManager.STATUS_INSTALLING:
                tv_install.setText(R.string.btn_installing);//安装中
                tv_install.setBackgroundResource(R.drawable.btn_green_selector);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                tv_install.setText(R.string.btn_install);//安装
                tv_install.setBackgroundResource(R.drawable.btn_green_selector);
                break;
            case DownloadManager.STATUS_EXTRA_UPGRADABLE://更新
                tv_install.setText(R.string.btn_upgrade);
                tv_install.setBackgroundResource(R.drawable.btn_green_selector);
                break;
            case DownloadManager.STATUS_EXTRA_INSTALLED://打开
                tv_install.setText(R.string.btn_open);
                tv_install.setBackgroundResource(R.drawable.btn_blue_selector);
                break;
            case DownloadManager.STATUS_FAILED://下载失败
                tv_install.setText(R.string.btn_fail);
                tv_install.setBackgroundResource(R.drawable.btn_green_selector);
                tv_download_error.setVisibility(View.VISIBLE);
                break;
        }
        tv_install.setVisibility(isHideProgressBar ? View.VISIBLE : View.INVISIBLE);
        progressView.setVisibility(!isHideProgressBar ? View.VISIBLE : View.INVISIBLE);
//        mDownloadProgressBar.setVisibility(isShowProgressBar ? View.VISIBLE : View.GONE);
        mDownloadProgressBar.setProgress(percent);
//        mDownloadProgressBar.setBackgroundResource(R.drawable.detail_progress_pause_bg);
//        mDownloadProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_drawable));

    }

    /**
     * 设置下载速度
     *
     * @param currAppInfo
     */
    private void setupDownloadspeed(AppInfo currAppInfo) {
        if (tvDownloadSpeed != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String s = currAppInfo.getDownloadSpeed();
            stringBuilder.append(s).append("/S");
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDownloadSpeed.setText(stringBuilder);
        }

        if (tvDownloadedSize != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String s = FileUtil.formatFileSize(this, currAppInfo.getDownloadedSize());
            String s1 = FileUtil.formatFileSize(this, AppInfoUtils.getPatchApkSize(currAppInfo));
            stringBuilder.append(s).append("/").append(s1);
            stringBuilder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.have_updated_color)),
                    s.length() + 1, s.length() + s1.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDownloadedSize.setText(stringBuilder);
        }
    }

    public static class CommonDetailViewHolder extends DownloadViewHolder implements View.OnClickListener {
        public CommonDetailViewHolder(Context mContext, View parentView) {
            super(mContext, parentView);
        }
    }

    public void queryTask() {
        if (task != null)
            task.cancel(true);
        task = new QueryTaskListTask();
        task.execute();
    }

    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (appInfo == null) {
                return false;
            }
            List<AppInfo> appInfos = new ArrayList<AppInfo>();
            appInfos.add(appInfo);
            AppInfoUtils.updateDownloadState(ChannelAppActivity.this, appInfos);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                setCenterBottomBtn((int) appInfo.getDownloadedPercent());
                setupDownloadspeed(appInfo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null)
            task.cancel(true);
        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}
