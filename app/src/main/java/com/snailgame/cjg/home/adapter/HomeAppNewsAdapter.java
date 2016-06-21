package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.HomeAppNewsBackupItem;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.JumpUtil;

/**
 * 资讯模板
 * Created by sunxy on 2014/9/11.
 */
public class HomeAppNewsAdapter extends ModuleBaseAdapter implements View.OnClickListener {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final String VALUE_SMALL_PIC = "0";
    private static final String VALUE_LARGE_PIC = "1";

    public HomeAppNewsAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
        setBackupData(moduleModel);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = getHeaderView(true);
        } else {
            convertView = getItemView(position - 1, parent);
        }
        return convertView;
    }

    private View getItemView(int position, ViewGroup parent) {
        View itemView = null;
        ContentModel contentModel = getItem(position);


        if (contentModel != null) {

            itemView = inflater.inflate(R.layout.home_app_news_small_item, parent, false);

            FSSimpleImageView iconView = (FSSimpleImageView) itemView.findViewById(R.id.app_news_icon);
            View divider = itemView.findViewById(R.id.app_news_divider);
            //Icon
            String url = contentModel.getsImageUrl();
            if (url == null || TextUtils.isEmpty(url)) {
                iconView.setVisibility(View.GONE);
            } else {
                iconView.setVisibility(View.VISIBLE);
                iconView.setImageUrl(contentModel.getsImageUrl());
            }
            if (position >= getCount() - 2)
                divider.setVisibility(View.GONE);
            else {
                divider.setVisibility(View.VISIBLE);
            }


            View appNewsContainer = itemView.findViewById(R.id.app_news_container);
            appNewsContainer.setTag(R.id.tag_first, contentModel);
            appNewsContainer.setTag(R.id.tag_second, 0);
            appNewsContainer.setOnClickListener(this);

            ((TextView) itemView.findViewById(R.id.app_news_title)).setText(contentModel.getsTitle());
            ((TextView) itemView.findViewById(R.id.app_news_sub_title)).setText(contentModel.getsSubtitle());
        }
        return itemView;
    }

    @Override
    public void onClick(View v) {
        ContentModel contentModel = (ContentModel) v.getTag(R.id.tag_first);
        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) v.getTag(R.id.tag_second) + 1;
        route[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_NEWS;
        JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);
    }

    public void refreshData(ModuleModel moduleModel) {
        this.moduleModel = moduleModel;
        setBackupData(moduleModel);
        notifyDataSetChanged();
    }

    private void setBackupData(ModuleModel moduleModel) {
        if (moduleModel != null) {
            for (ContentModel item : moduleModel.getChilds()) {
                String backupTxt = item.getsBackup();
                try {
                    HomeAppNewsBackupItem backupItem = JSON.parseObject(backupTxt, HomeAppNewsBackupItem.class);
                    item.setP1(backupItem.getP1());
                    item.setP2(backupItem.getP2());
                    item.setP3(backupItem.getP3());
                } catch (Exception e) {
                    item.setP1(VALUE_SMALL_PIC);
                }
            }
        }

    }


}
