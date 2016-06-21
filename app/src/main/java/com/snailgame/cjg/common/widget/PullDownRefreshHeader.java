package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.snailgame.cjg.R;
import com.snailgame.cjg.util.DataTransUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by lic on 2016/3/9.
 * <p>
 * 下拉刷新的header
 */
public class PullDownRefreshHeader extends LinearLayout implements PtrUIHandler {
    private View contentView;
    private Context context;
    private AnimationDrawable refreshingDrawable;
    private ImageView mArrowImageView;
    private TextView mHintTextView;
    private View toolBarView, mBannerMask;
    public boolean isRefreshComplete = true;
    private boolean isTransparent;

    public PullDownRefreshHeader(Context context, View toolBarView, View mBannerMask) {
        super(context);
        this.context = context;
        this.toolBarView = toolBarView;
        this.mBannerMask = mBannerMask;
        initView();
    }

    public PullDownRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }


    private void initView() {
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView = LayoutInflater.from(context).inflate(R.layout.pull_down_refresh_header, null);
        addView(contentView, lp);
        setGravity(Gravity.BOTTOM);
        mArrowImageView = ButterKnife.findById(contentView, R.id.pull_refresh_header_arrow);
        mHintTextView = ButterKnife.findById(contentView, R.id.pull_refresh_header_hint_textview);
        refreshingDrawable = (AnimationDrawable) mArrowImageView.getBackground();
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        isRefreshComplete = false;
        mHintTextView.setText(DataTransUtil.getLastRefreshTime(SharedPreferencesUtil.getInstance().getLastRefreshTime()));
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        refreshingDrawable.start();
        mHintTextView.setText(context.getString(R.string.refreshing));
        SharedPreferencesUtil.getInstance().setLastRefreshTime(System.currentTimeMillis());
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        refreshingDrawable.stop();
        mHintTextView.setText(context.getString(R.string.refresh_done));
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if (mBannerMask != null && toolBarView != null) {
            if (ptrIndicator.getCurrentPosY() == 0) {
                isRefreshComplete = true;
                ObjectAnimator.ofFloat(toolBarView, "alpha", 0, 1).setDuration(150).start();
                ObjectAnimator.ofFloat(mBannerMask, "alpha", 0, 1).setDuration(150).start();
                isTransparent = false;
            } else {
                if (!isTransparent) {
                    isTransparent = true;
                    ObjectAnimator.ofFloat(toolBarView, "alpha", 1, 0).setDuration(1).start();
                    ObjectAnimator.ofFloat(mBannerMask, "alpha", 1, 0).setDuration(1).start();
                }
            }
//            ViewHelper.setTranslationY(searchView, ptrIndicator.getCurrentPosY());
        }
    }
}
