package com.snailgame.cjg.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.FriendHandleEvent;
import com.snailgame.cjg.friend.adapter.FriendGamesAdapter;
import com.snailgame.cjg.friend.model.Friend;
import com.snailgame.cjg.friend.model.FriendGamesModel;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;
import com.snailgame.cjg.friend.utils.MeasureListViewHeightUtil;
import com.snailgame.cjg.friend.widget.SlidingFinishFrameLayout;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.widget.FullListView;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.FastDevActivity;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import third.com.zhy.base.loadandretry.LoadingAndRetryManager;
import third.com.zhy.base.loadandretry.OnLoadingAndRetryListener;

/**
 * Created by TAJ_C on 2016/5/12.
 */
public class FriendGamesActivity extends FastDevActivity implements AdapterView.OnItemClickListener, SlidingFinishFrameLayout.OnScrollListener {
    @Bind(R.id.lv_friend_games)
    FullListView friendGamesListView;

    @Bind(R.id.siv_friend_photo)
    FSSimpleImageView photoView;

    @Bind(R.id.tv_friend_name)
    TextView friendNameView;
    @Bind(R.id.slidingFinishLayout)
    SlidingFinishFrameLayout slidingFinishLayout;
    @Bind(R.id.tv_detail_title)
    TextView tv_detail_title;
    @Bind(R.id.ll_container)
    LinearLayout content_view;
    @Bind(R.id.detail_actionbar_view)
    View actionbarView;

    protected LoadingAndRetryManager mLoadingAndRetryManager;

    private FriendGamesAdapter mAdapter;

    protected ArrayList<AppInfo> appInfoLists = new ArrayList<>();

    private long friendId;
    private Friend friend;

    protected int[] mRoute;
    private boolean isScrollFinish = false;
    private Window mWindow;
    private Drawable actionbarBgDrawable;

    public static Intent newIntent(Context context, Friend friend) {
        Intent intent = new Intent(context, FriendGamesActivity.class);
        intent.putExtra(AppConstants.KEY_FRIEND_USER, friend);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        translateStatusBar();
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_friend_games;
    }

    @Override
    protected void handleIntent() {
        friend = (Friend) getIntent().getSerializableExtra(AppConstants.KEY_FRIEND_USER);
        if (friend != null) {
            friendId = friend.getUserId();
        }
    }

    @Override
    protected void initView() {
        initActionBar();
        mRoute = createRoute();
        mAdapter = new FriendGamesAdapter(this, appInfoLists, mRoute);
        friendGamesListView.setAdapter(mAdapter);
        friendGamesListView.setOnItemClickListener(this);
        slidingFinishLayout.setOnScrollListener(this);
        photoView.setImageUrlAndReUse(friend.getPhoto());
        friendNameView.setText(friend.getNickName());
        tv_detail_title.setText(friend.getNickName());
        mLoadingAndRetryManager = LoadingAndRetryManager.generate(content_view, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                if (retryView == null) {
                    loadData();
                }
            }
        });
        mLoadingAndRetryManager.setEmptyMessage(getString(R.string.friend_game_none_hint));
    }
    @Override
    protected void loadData() {
        showLoading();
        String url = JsonUrl.getJsonUrl().JSON_URL_FRIEND_GAME_LIST + "?nUserId=" + friendId;
        FSRequestHelper.newGetRequest(url, TAG, FriendGamesModel.class, new IFDResponse<FriendGamesModel>() {
            @Override
            public void onSuccess(FriendGamesModel friendGamesModel) {
                if (friendGamesModel != null && friendGamesModel.getItem() != null) {
                    resetLoadingAndRetryLayout();
                    showContent();
                    FriendGamesModel.ModelItem item = friendGamesModel.getItem();
                    appInfoLists.clear();
                    AppInfoUtils.bindData(FriendGamesActivity.this,item.getGameList(), appInfoLists, mAdapter, TAG);
                    measureListViewHeight(null);
                    if (ListUtils.isEmpty(item.getGameList())) {
                        showEmpty();
                    }
                } else {
                    showEmpty();
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
        }, false);
    }


    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (friendGamesListView != null && taskInfos != null) {
            synchronized (appInfoLists) {
                for (TaskInfo taskInfo : taskInfos) {
                    updateProgress(taskInfo, appInfoLists);
                }
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
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
                DownloadHelper.calcDownloadSpeed(this, appInfo, taskInfo);
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
                DownloadHelper.handleMsgForPauseOrError(this, appInfo.getAppName(),
                        taskInfo.getDownloadState(), reason);
                break;
            }
        }
    }


    @OnClick({R.id.iv_close, R.id.tv_detail_title, R.id.iv_delete_friend})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                slidingFinishLayout.scrollDown();
                break;
            case R.id.tv_detail_title:
                finish();
                break;
            case R.id.iv_delete_friend://删除好友
                showDelDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 首页
        int[] route = new int[]{
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_FIFTH_VOUCHER,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = mAdapter.getItem(position);
        if (appInfo != null) {
            startActivity(DetailActivity.newIntent(this, appInfo.getAppId(), appInfo.getiFreeArea(), mRoute));
        }
    }

    /**
     * 测量friendGamesListView的高度
     */
    public void measureListViewHeight(View emptyView) {
            int height = MeasureListViewHeightUtil.getTotalHeightofListView(friendGamesListView);
            slidingFinishLayout.setMarginHeight(height, emptyView);
    }

    @Override
    public void onScroll(int scrollY) {
        setStatusBarColor(scrollY);
    }

    @Override
    public void onSlidingFinish() {
        isScrollFinish = true;
        finish();
    }


    public void showEmpty() {
        if (mLoadingAndRetryManager != null) {
            setEmptyViewLayout();
            mLoadingAndRetryManager.showEmpty();
            measureListViewHeight(mLoadingAndRetryManager.getLoadingAndRetryLayout());
        }
    }


    public void showError() {
        if (mLoadingAndRetryManager != null) {
            setEmptyViewLayout();
            mLoadingAndRetryManager.showError();
            measureListViewHeight(mLoadingAndRetryManager.getLoadingAndRetryLayout());
        }
    }

    /**
     * 根据滑动设置高度
     */
    private void setEmptyViewLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, slidingFinishLayout.getEmptyViewStartY());
        params.gravity = Gravity.CENTER;
        mLoadingAndRetryManager.getLoadingAndRetryLayout().setLayoutParams(params);
    }

    /**
     * 恢复LoadingAndRetryLayout的LayoutParams
     */
    private void resetLoadingAndRetryLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLoadingAndRetryManager.getLoadingAndRetryLayout().setLayoutParams(params);
    }

    public void showLoading() {
        if (mLoadingAndRetryManager != null) {
            mLoadingAndRetryManager.showLoading();
        }
    }

    public void showContent() {
        if (mLoadingAndRetryManager != null) {
            mLoadingAndRetryManager.showContent();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (!isScrollFinish) {
            overridePendingTransition(0, R.anim.activity_slide_out_bottom);
        }
    }

    @Subscribe
    public void deleteFriend(FriendHandleEvent event) {
        if (event != null && event.getResult() != null && event.getResult().getCode() == 0) {
            if (event.getHandle() == FriendHandleUtil.FRIEND_HANDLE_DEL) {
                finish();
            }
        }
    }

    private void showDelDialog() {
        DialogUtils.showTwoButtonDialog(this, ResUtil.getString(R.string.del_friend), ResUtil.getString(R.string.btn_cancel), ResUtil.getString(R.string.del_friend_tip), new DialogUtils.ConfirmClickedLister() {
            @Override
            public void onClicked() {
                FriendHandleUtil.handleFriend(FriendGamesActivity.this, TAG, friend, FriendHandleUtil.FRIEND_HANDLE_DEL);
            }
        });
    }

    private void translateStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindow = getWindow();
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(getResources().getColor(R.color.translucent_15_black));
        }
    }
    private void initActionBar() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionbarView.getLayoutParams();
        if (params != null) {
            params.topMargin = ComUtil.getStatesBarHeight();
        }
        actionbarBgDrawable = actionbarView.getBackground();
        actionbarBgDrawable.setAlpha(0);
    }

    public void setStatusBarColor(int alpha) {
        actionbarBgDrawable.setAlpha(alpha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(alpha >= 240){
               mWindow.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
            }else{
                mWindow.setStatusBarColor(getResources().getColor(R.color.translucent_15_black));
            }
        }
    }
}
