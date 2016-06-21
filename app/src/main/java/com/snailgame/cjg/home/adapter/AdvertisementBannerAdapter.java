package com.snailgame.cjg.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.JumpUtil;

import java.util.List;

/**
 * Created by sunxy on 2015/4/22.
 */
public class AdvertisementBannerAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ModuleModel moduleModel;
    private int[] mRoute;
    public AdvertisementBannerAdapter(Context context,ModuleModel moduleModel, int[] route){
        this.context=context;
        mRoute=route;
        this.moduleModel=moduleModel;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.home_page_ad_view,parent,false);
        inflateAdView(convertView,moduleModel);
        return convertView;
    }

    /**
     * 左右两个小广告位
     *
     * @param moduleModel moduleModel
     */
    private void inflateAdView(View adView,ModuleModel moduleModel) {
        List<ContentModel> childs = moduleModel.getChilds();
        if (childs != null && childs.size() >= 2) {
            ContentModel contentModel = childs.get(0);
            FSSimpleImageView leftBanner = (FSSimpleImageView) adView.findViewById(R.id.ad_left);
            leftBanner.setImageUrl(contentModel.getsImageBig());
            leftBanner.setTag(R.id.tag_first, contentModel);
            leftBanner.setTag(R.id.tag_second, 1);
            leftBanner.setOnClickListener(bannerListener);

            contentModel = childs.get(1);
            FSSimpleImageView rightBanner = (FSSimpleImageView) adView.findViewById(R.id.ad_right);
            rightBanner.setImageUrl(contentModel.getsImageBig());
            rightBanner.setTag(R.id.tag_first, contentModel);
            rightBanner.setTag(R.id.tag_second, 2);
            rightBanner.setOnClickListener(bannerListener);
        }
    }
    View.OnClickListener bannerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentModel contentModel = (ContentModel) v.getTag(R.id.tag_first);
            // 从小广告位进入
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_HALFAD;
            route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) v.getTag(R.id.tag_second);
            JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);

        }
    };
}
