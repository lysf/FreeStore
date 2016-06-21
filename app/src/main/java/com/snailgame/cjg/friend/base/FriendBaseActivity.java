package com.snailgame.cjg.friend.base;

import third.com.zhy.base.loadandretry.LoadingAndRetryManager;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by TAJ_C on 2016/5/20.
 */
public class FriendBaseActivity extends SwipeBackActivity {

    protected LoadingAndRetryManager mLoadingAndRetryManager;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    public void showEmpty() {
        if (mLoadingAndRetryManager != null) {
            mLoadingAndRetryManager.showEmpty();
        }
    }


    public void showError() {
        if (mLoadingAndRetryManager != null) {
            mLoadingAndRetryManager.showError();
        }
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

    public void setEmptyMessage(String emptyMessage) {
        if (mLoadingAndRetryManager != null) {
            mLoadingAndRetryManager.setEmptyMessage(emptyMessage);
        }
    }
    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }
}
