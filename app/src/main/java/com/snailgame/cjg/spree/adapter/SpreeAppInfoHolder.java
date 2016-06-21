package com.snailgame.cjg.spree.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.global.AppConstants;

import butterknife.Bind;

/**
 * 礼包 中的 app holder
 * Created by TAJ_C on 2015/5/4.
 */
public class SpreeAppInfoHolder extends DownloadViewHolder implements View.OnClickListener {
    @Bind(R.id.app_logo)
    FSSimpleImageView ivAppLogo;
    @Bind(R.id.app_title)
    TextView tvAppLabel;
    @Bind(R.id.app_info_layout)
    View appInfoLayout;
    @Nullable
    @Bind(R.id.lv_download_container)
    View buttonClick;

    private Context mContext;

    int[] mRoute;

    public SpreeAppInfoHolder(Context context, View parentView, int[] route) {
        super(context, parentView);
        this.mContext = context;
        if (buttonClick != null) {
            buttonClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.performClick();
                }
            });
        }
        mRoute = route;
    }


    public void bindAppInfoView(final AppInfo currAppInfo, final int position) {
        appInfo = currAppInfo;

        if (currAppInfo != null) {
            if (ivAppLogo != null && currAppInfo.getIcon() != null) {
                ivAppLogo.setImageUrlAndReUse(currAppInfo.getIcon());
            }

            if (tvAppLabel != null) {
                tvAppLabel.setText(currAppInfo.getAppName());
            }


            int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);


            if (button != null) {
                // 尝试下载统计
                int[] route = mRoute.clone();
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
                route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
                route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
                currAppInfo.setRoute(route);
                button.setTag(R.id.tag_first, currAppInfo);
            }
            DownloadWidgetHelper.getHelper().switchState(downloadState, this);
            appInfoLayout.setVisibility(View.VISIBLE);
        }

        appInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] route = mRoute.clone();
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
                mContext.startActivity(DetailActivity.newIntent(mContext, currAppInfo.getAppId(), route));
            }
        });
    }



}
