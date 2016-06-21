package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.detail.DetailFragment;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;

/**
 * 热门礼包
 * Created by sunxy on 2014/9/24.
 */
public class HotSpreeAdapter extends ModuleBaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;

    public HotSpreeAdapter(Activity context, ModuleModel moduleModel, int[] route) {
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
                convertView = getSpreeItemView(position - 1);
                break;

        }
        return convertView;
    }

    private View getSpreeItemView(int position) {
        View convertView = inflater.inflate(R.layout.home_hot_spree_layout, null);
        ContentModel contentModel = getItem(position);

        if (contentModel != null) {
            convertView.setTag(R.id.tag_first, contentModel.getsExtend());
            convertView.setTag(R.id.tag_second, position);
            convertView.setOnClickListener(spreeListener);
            TextView title = (TextView) convertView.findViewById(R.id.home_game_name);
            FSSimpleImageView logoImage = (FSSimpleImageView) convertView.findViewById(R.id.home_spree_logo);
            TextView spreeText = (TextView) convertView.findViewById(R.id.home_spree_title);
            TextView seeBtn = (TextView) convertView.findViewById(R.id.tv_see);
            title.setText(contentModel.getsTitle());
            logoImage.setImageUrl(contentModel.getsImageUrl());
            spreeText.setText(contentModel.getsSubtitle());
            seeBtn.setTag(R.id.tag_first, contentModel.getsExtend());
            seeBtn.setTag(R.id.tag_second, position);
            seeBtn.setOnClickListener(spreeListener);

        }
        return convertView;
    }

    View.OnClickListener spreeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String extend = (String) v.getTag(R.id.tag_first);
            if (extend != null && !TextUtils.isEmpty(extend)) {
                int gameId = parseGameId(extend);
                if (gameId != 0) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) v.getTag(R.id.tag_second) + 1;
                    context.startActivity(DetailActivity.newIntent(context, gameId, route, DetailFragment.TAB_SPREE));
                }
            }
        }
    };

    /**
     * 从礼包扩展字段解析出游戏id
     *
     * @param extend
     * @return
     */
    private int parseGameId(String extend) {
        try {
            JSONObject object = JSON.parseObject(extend);
            if (object.containsKey("nAppId"))
                return object.getIntValue("nAppId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
