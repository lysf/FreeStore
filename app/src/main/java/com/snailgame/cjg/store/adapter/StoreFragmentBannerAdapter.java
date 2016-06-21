package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.RecyclingPagerAdapter;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.store.model.StoreChildContentModel;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

/**
 * Created by lic on 2015/11/4.
 * 商城页面banner
 */
public class StoreFragmentBannerAdapter extends RecyclingPagerAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<StoreChildContentModel> mInfos;
    private int[] mRoute;

    public StoreFragmentBannerAdapter(Context context, List<StoreChildContentModel> infos, int[] route) {
        this.context = context;
        mInfos = infos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (!ListUtils.isEmpty(mInfos))
            return Integer.MAX_VALUE;
        return 0;
    }


    @Override
    public int getCircleCount() {
        if (mInfos != null)
            return mInfos.size();
        return 0;
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return position % mInfos.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        final int index = getPosition(position);
        final StoreChildContentModel info = mInfos.size() == 0 ? null : mInfos.get(index);
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.simple_image_normal_overlay, null);
            holder.imageView = (FSSimpleImageView) view.findViewById(R.id.simple_image);
            holder.imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (info != null) {
            holder.imageView.setImageUrlAndReUse(info.getsImageUrl());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info.getsJumpUrl() != null && !TextUtils.isEmpty(info.getsJumpUrl()))
                        context.startActivity(WebViewActivity.newIntent(context, info.getsJumpUrl()));
                }
            });
        }
        return view;
    }

    private static class ViewHolder {
        FSSimpleImageView imageView;
    }


}
