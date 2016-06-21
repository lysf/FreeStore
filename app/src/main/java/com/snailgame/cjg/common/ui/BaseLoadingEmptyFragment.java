package com.snailgame.cjg.common.ui;


import com.snailgame.fastdev.FastDevFragment;

import third.com.zhy.base.loadandretry.LoadingAndRetryManager;

/**
 * Created by TAJ_C on 2016/5/20.
 */
public abstract class BaseLoadingEmptyFragment extends FastDevFragment {

    protected LoadingAndRetryManager mLoadingAndRetryManager;


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

}
