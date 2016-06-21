package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.JumpUtil;

/**
 * 热门合集
 * Created by sunxy on 2014/9/25.
 */
public class CollectionAdapter extends ModuleBaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;

    public CollectionAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                convertView = getHeaderView(true);
                break;
            default:
                convertView = getItemView(position - 1);
                break;
        }
        return convertView;
    }

    private View getItemView(final int i) {
        View banner = inflater.inflate(R.layout.home_collection_banner, null);
        FSSimpleImageView imageView = (FSSimpleImageView) banner.findViewById(R.id.home_banner);

        final ContentModel contentModel = getItem(i);
        if (contentModel != null) {
            imageView.setImageUrl(contentModel.getsImageUrl());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = i + 1;
                    JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);
                }
            });
        }


        return banner;
    }

}
