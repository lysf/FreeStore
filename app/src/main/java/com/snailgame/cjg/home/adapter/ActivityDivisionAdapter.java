package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

/**
 * 活动模板
 * Created by sunxy on 2014/9/11.
 */
public class ActivityDivisionAdapter extends ModuleBaseAdapter implements View.OnClickListener {
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int PER_ROW_COUNT = 2;
    private int padding_16;
    private int width, height;

    public ActivityDivisionAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
        padding_16 = ResUtil.getDimensionPixelSize(R.dimen.dimen_16dp);
        height = ResUtil.getDimensionPixelSize(R.dimen.dimen_88dp);
        width = (int) (PhoneUtil.getScreenWidth() - padding_16 * 3) / 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        if (ListUtils.isEmpty(children))
            return 0;
        return children.size() / PER_ROW_COUNT + 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = getHeaderView(false);
        } else if (position == 1) {
            convertView = getBanner();
        } else {
            convertView = getItemView(position - 1);
        }

        return convertView;
    }

    private View getItemView(int position) {
        LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.home_activity_item, null);

        int childCount = itemView.getChildCount();


        for (int i = 0; i < childCount; i++) {
            ContentModel contentModel = getItem((2 * position + i) - 1);
            if (contentModel != null) {
                LinearLayout activityItemLayout = (LinearLayout) itemView.getChildAt(i);
                FSSimpleImageView banner = (FSSimpleImageView) activityItemLayout.getChildAt(0);
                banner.setImageUrl(contentModel.getsImageUrl());
                TextView title = (TextView) activityItemLayout.getChildAt(1);
                title.setText(contentModel.getsTitle());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
                LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) title.getLayoutParams();
                if (params != null && titleParams != null) {
                    titleParams.width = width;
                    params.leftMargin = padding_16;
                    params.width = width;
                    params.height = width * 9 / 16;
                    if (i == 1) {
                        params.rightMargin = padding_16;
                    } else {
                        params.rightMargin = 0;
                    }
                }
                banner.setTag(R.id.tag_first, contentModel);
                banner.setTag(R.id.tag_second, position);
                banner.setOnClickListener(this);
            }

        }
        return itemView;
    }

    private View getBanner() {
        View view = inflater.inflate(R.layout.home_banner, null);
        ContentModel contentModel = getItem(0);
        if (contentModel != null) {
            FSSimpleImageView banner = (FSSimpleImageView) view.findViewById(R.id.home_banner);
            banner.setImageUrl(contentModel.getsImageUrl());
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(contentModel.getsTitle());
            banner.setTag(R.id.tag_first, contentModel);
            banner.setTag(R.id.tag_second, 0);
            banner.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        ContentModel contentModel = (ContentModel) v.getTag(R.id.tag_first);
        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) v.getTag(R.id.tag_second) + 1;
        JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);
    }
}
