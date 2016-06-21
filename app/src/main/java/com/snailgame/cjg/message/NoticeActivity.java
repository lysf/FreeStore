package com.snailgame.cjg.message;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.daoHelper.PushModelDaoHelper;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.UpdateNotificationEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.message.adapter.PushAdapter;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * Created by lic on 2015/09/17.
 * 通知中心
 * 只会显示通知
 */
public class NoticeActivity extends SwipeBackActivity {
    static String TAG = NoticeActivity.class.getName();

    public static Intent newIntent(Context context) {
        return new Intent(context, NoticeActivity.class);
    }

    public static Intent newIntent(Context context, int... flags) {
        Intent intent = newIntent(context);
        for (int i = 0; i < flags.length; i++) {
            intent.setFlags(flags[i]);
        }
        return intent;
    }

    @Subscribe
    public void onPushMsgReceived(UpdateNotificationEvent event) {
        if (!event.isAllHasReadUpdate)
            getMsgInfo();
    }

    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;

    //推送消息
    private PushAdapter mPushAdapter;
    private List<PushModel> mPushModels;
    private EmptyView mEmptyView;

    private boolean bPush = false;


    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeGameManageActionbar(this, getSupportActionBar(), R.string.notice_center_actionbar_title);

        MainThreadBus.getInstance().register(this);

        mEmptyView = new EmptyView(this, loadMoreListView);
        initData();
        showLoading();
        clearNotification();

    }

    private void clearNotification() {
        String ids = SharedPreferencesUtil.getInstance().getNotificationID();
        String idArray[] = ids.split(",");
        NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (idArray.length > 0) {
            for (String id : idArray) {
                if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id))
                    notiManager.cancel(Integer.parseInt(id));
            }
        }
        SharedPreferencesUtil.getInstance().setNotificationID("");
    }


    protected void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.setEmptyMessage(getString(R.string.no_data_now));
            mEmptyView.showEmpty();
        }
    }

    protected void showLoading() {
        if (mEmptyView != null) {
            mEmptyView.showLoading();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        bPush = false;
        getMsgInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_NOTICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_NOTICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

    /**
     * 获取推送消息
     */
    private void getMsgInfo() {
        Runnable readMsgRunnable = new Runnable() {
            @Override
            public void run() {
                List<PushModel> models = PushModelDaoHelper.queryForAll(NoticeActivity.this);
                if (models == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showEmpty();
                        }
                    });
                    return;
                }

                mPushModels = PushModelDaoHelper.deleteExtraMsgs(NoticeActivity.this, models);
                PushModelDaoHelper.updateAllHasRead(NoticeActivity.this);
                bPush = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPushAdapter.refreshData(mPushModels);
                        RefreshUI();
                    }
                });
                MainThreadBus.getInstance().post(new UpdateNotificationEvent(true));
            }
        };
        new Thread(readMsgRunnable).start();

    }


    private void initData() {
        mPushModels = new ArrayList<>();
        mPushAdapter = new PushAdapter(this, mPushModels);
        loadMoreListView.setAdapter(mPushAdapter);
    }


    private void RefreshUI() {
        if (bPush) {
            if (ListUtils.isEmpty(mPushModels)) {
                showEmpty();
            }
        }
    }


}
