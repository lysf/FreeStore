package com.snailgame.cjg.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.AppDetailActionBar;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.ButterKnife;

/**
 * Created by yftx
 * on 2/20/14.
 */
public class ActionBarUtils {

    public static void makeCommonActionbar(final Activity activity, ActionBar actionBar, int titleId) {

        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(titleId);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
    }

    public static void makeCommonActionbar(final Activity activity, ActionBar actionBar, String title) {

        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
    }

    public static void makeCommonActionbar(final Activity activity, ActionBar actionBar, String title, int iconId, View.OnClickListener onClickListener) {

        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        ImageView ivAbSearch = ButterKnife.findById(actionBarView, R.id.iv_ab_search);
        ivAbSearch.setVisibility(View.VISIBLE);
        ivAbSearch.setImageResource(iconId);
        ivAbSearch.setOnClickListener(onClickListener);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
    }


    public static ImageView makeWebViewActionbar(final WebView webView, final Activity activity, ActionBar actionBar, String title, View.OnClickListener listener, final String url) {
        View actionBarView = LayoutInflater.from(activity).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(title);
        ComUtil.setDrawableLeft(tvTitle, R.drawable.ic_close_webview);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof WebViewActivity) {
                    if (((WebViewActivity) activity).getFinishEnable()) {
                        activity.finish();
                    }
                } else {
                    activity.finish();
                }
            }
        });

        ImageView shareImage = ButterKnife.findById(actionBarView, R.id.iv_ab_search);
        shareImage.setOnClickListener(listener);
        shareImage.setVisibility(View.GONE);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
        return shareImage;
    }

    public static AppDetailActionBar makeDetailActionbarStyle(final Activity activity, String title,
                                                              final boolean isShowLeft, final boolean isDetail, final boolean showShare) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_ab_menu:
//                        ((SlidingFragmentActivity) activity).toggle();
                        break;
                    case R.id.tv_title:
                    case R.id.tv_collect_title:
                        activity.finish();
                        break;
                    case R.id.btn_shared:
                        ShareDialog shareDialog = new ShareDialog(activity);
                        ShareItemListener clickListener = new ShareItemListener(activity, shareDialog);
                        shareDialog.setOnItemClickListener(clickListener);
                        shareDialog.show();
                        break;

                    default:
                        break;
                }
            }
        };

        ActionBar mActionBar = ((ActionBarActivity) activity).getSupportActionBar();
        AppDetailActionBar mCustomView;
        if (isShowLeft) {
            if (isDetail) {
                mCustomView = (AppDetailActionBar)
                        activity.getLayoutInflater().inflate(R.layout.actionbar_app_detail, null);
            } else {
                mCustomView = (AppDetailActionBar)
                        activity.getLayoutInflater().inflate(R.layout.actionbar_app_collection, null);
            }
        } else {
            mCustomView = (AppDetailActionBar)
                    activity.getLayoutInflater().inflate(R.layout.actionbar_app_home, null);
        }
        mCustomView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(mCustomView);
        mCustomView.setTitle(title, isShowLeft);
        if (ButterKnife.findById(mCustomView, R.id.tv_title) != null) {
            ButterKnife.findById(mCustomView, R.id.tv_title).setOnClickListener(listener);
        }
        if (ButterKnife.findById(mCustomView, R.id.tv_collect_title) != null) {
            ButterKnife.findById(mCustomView, R.id.tv_collect_title).setOnClickListener(listener);
        }
        if (ButterKnife.findById(mCustomView, R.id.tv_ab_menu) != null) {
            ButterKnife.findById(mCustomView, R.id.tv_ab_menu).setOnClickListener(listener);
        }

        if (ButterKnife.findById(mCustomView, R.id.btn_shared) != null) {
            ButterKnife.findById(mCustomView, R.id.btn_shared).setOnClickListener(listener);
            if (!showShare)
                ButterKnife.findById(mCustomView, R.id.btn_shared).setVisibility(View.GONE);
        }

        return mCustomView;
    }

    public static void makeGameManageActionbar(final Activity activity, ActionBar actionBar, int titleId) {
        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(titleId);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameManageUtil.forwardToHomeOrCloseActivity(activity);
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
    }

    public static void makeStoreActionbar(final Activity activity, ActionBar actionBar, String title, View.OnClickListener onClickListener) {

        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        ImageView ivAbSearch = ButterKnife.findById(actionBarView, R.id.iv_ab_search);
        ivAbSearch.setVisibility(View.VISIBLE);
        ivAbSearch.setImageResource(R.drawable.voucher_detail_ic);
        ivAbSearch.setOnClickListener(onClickListener);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);
    }
}
