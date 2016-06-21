package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.JumpUtil;

/**
 * 商品推荐
 * Created by sunxy on 2014/9/11.
 */
public class GoodsRecommendAdapter extends ModuleBaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;

    public GoodsRecommendAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return 2;
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
            case 1:
                convertView = getGoodsBanner();
                break;

        }
        return convertView;
    }

    private View getGoodsBanner() {
        View view = inflater.inflate(R.layout.home_goods_layout, null);
        FSSimpleImageView goodsLeft = (FSSimpleImageView) view.findViewById(R.id.goods_left);
        FSSimpleImageView goodsRight = (FSSimpleImageView) view.findViewById(R.id.goods_right);
        FSSimpleImageView goodsBottom = (FSSimpleImageView) view.findViewById(R.id.goods_bottom);
        ContentModel contentModel = getItem(0);
        if (contentModel != null) {
            goodsLeft.setImageUrl(contentModel.getsImageUrl());
            goodsLeft.setTag(R.id.tag_first, contentModel);
            goodsLeft.setTag(R.id.tag_second, 0);
            goodsLeft.setOnClickListener(goodsListener);
        }
        contentModel = getItem(1);
        if (contentModel != null) {
            goodsRight.setImageUrl(contentModel.getsImageUrl());
            goodsRight.setTag(R.id.tag_first, contentModel);
            goodsRight.setTag(R.id.tag_second, 1);
            goodsRight.setOnClickListener(goodsListener);
        }
        contentModel = getItem(2);
        if (contentModel != null) {
            goodsBottom.setVisibility(View.VISIBLE);
            goodsBottom.setImageUrl(TextUtils.isEmpty(contentModel.getsImageBig()) ?
                    contentModel.getsImageUrl() : contentModel.getsImageBig());
            goodsBottom.setTag(R.id.tag_first, contentModel);
            goodsBottom.setTag(R.id.tag_second, 2);
            goodsBottom.setOnClickListener(goodsListener);

        }

        return view;
    }

    View.OnClickListener goodsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentModel contentModel = (ContentModel) v.getTag(R.id.tag_first);
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) v.getTag(R.id.tag_second) + 1;

            JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);
        }
    };
}
