package com.snailgame.cjg.store.adapter;

import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lic on 2015/11/4.
 * 商城页面 钜惠代充的banner
 */
public class StoreFragmentRechargeAdapter extends RecyclingPagerAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<StoreChildContentModel> mInfos;
    private int[] mRoute;

    public StoreFragmentRechargeAdapter(Context context, List<StoreChildContentModel> infos, int[] route) {
        this.context = context;
        mInfos = infos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (!ListUtils.isEmpty(mInfos))
            return getRealListSize();
        return 0;
    }


    @Override
    public int getCircleCount() {
        if (mInfos != null)
            return getRealListSize();
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        StoreChildContentModel info1 = null;
        StoreChildContentModel info2 = null;
        StoreChildContentModel info3 = null;
        if (getRealListItem(position, 1) > -1) {
            info1 = mInfos.size() == 0 ? null : mInfos.get(getRealListItem(position, 1));
        }
        if (getRealListItem(position, 2) > -1) {
            info2 = mInfos.size() == 0 ? null : mInfos.get(getRealListItem(position, 2));
        }
        if (getRealListItem(position, 3) > -1) {
            info3 = mInfos.size() == 0 ? null : mInfos.get(getRealListItem(position, 3));
        }
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_store_recharge_item_view, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (info1 != null) {
            setImageView(holder.imageView1, info1);
        } else {
            holder.imageView1.setVisibility(View.INVISIBLE);
        }

        if (info2 != null) {
            setImageView(holder.imageView2, info2);
        } else {
            holder.imageView2.setVisibility(View.INVISIBLE);
        }

        if (info3 != null) {
            setImageView(holder.imageView3, info3);
        } else {
            holder.imageView3.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    /**
     * 设置图片和点击事件
     */
    private void setImageView(FSSimpleImageView FSSimpleImageView, final StoreChildContentModel storeChildContentModel) {
        FSSimpleImageView.setVisibility(View.VISIBLE);
        FSSimpleImageView.setImageUrlAndReUse(storeChildContentModel.getsImageUrl());
        FSSimpleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(WebViewActivity.newIntent(context, storeChildContentModel.getsJumpUrl()));

            }
        });
    }

    /**
     * 获取滑动页真正个数
     */
    private int getRealListSize() {
        if (mInfos.size() % 3 == 0) {
            return mInfos.size() / 3;
        } else {
            return mInfos.size() / 3 + 1;
        }

    }

    /**
     * 获取当前item在数据源minfo中的具体数据
     */
    private int getRealListItem(int pagerPosition, int imagePosition) {
        int position = 3 * (pagerPosition) + imagePosition - 1;
        if (position > mInfos.size() - 1) {
            return -1;
        }
        return position;

    }

    public static class ViewHolder {
        @Bind(R.id.image_view1)
        FSSimpleImageView imageView1;
        @Bind(R.id.image_view2)
        FSSimpleImageView imageView2;
        @Bind(R.id.image_view3)
        FSSimpleImageView imageView3;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
