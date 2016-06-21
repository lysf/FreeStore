package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * Created by sunxy on 2014/12/30.
 */
public class AccountSafeItem extends LinearLayout {
    private TextView title, subTitle;
    private ImageView subIcon;
    private FSSimpleImageView photoView;
    private FSSimpleImageView titleIcon;

    public AccountSafeItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AccountSafeItem(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.account_safe_item, null);
        title = (TextView) view.findViewById(R.id.accountSafeTitle);
        subTitle = (TextView) view.findViewById(R.id.accountSafeSubTitle);
        subIcon = (ImageView) view.findViewById(R.id.accountSafeSubIcon);
        photoView = (FSSimpleImageView) view.findViewById(R.id.photo_view);
        titleIcon = (FSSimpleImageView) view.findViewById(R.id.iv_title_icon);
        addView(view);
    }

    public void initData(String titleString, String subTitleString, String avatarUrl, boolean isShowMore) {
        title.setText(titleString);
        titleIcon.setVisibility(GONE);
        subIcon.setVisibility(isShowMore ? VISIBLE : GONE);
        if (subTitleString != null) {
            subTitle.setVisibility(VISIBLE);
            subTitle.setText(subTitleString);
        }

        if (avatarUrl != null) {
            photoView.setVisibility(VISIBLE);
            photoView.setImageUrlAutoRotateEnabled(avatarUrl, true);
        }

    }

    public void initData(String titleString, String titleIconUrl, String subTitleString, String avatarUrl, boolean isNeedScale) {
        this.initData(titleString, subTitleString, avatarUrl, isNeedScale);
        titleIcon.setVisibility(VISIBLE);
        titleIcon.setImageUrl(titleIconUrl);
    }


    /**
     * 设置头像
     * @param drawable
     */
    public void setAvater(Drawable drawable) {
        if (drawable != null) {
            photoView.setVisibility(VISIBLE);

            photoView.setImageDrawable(drawable);
        }

    }
}
