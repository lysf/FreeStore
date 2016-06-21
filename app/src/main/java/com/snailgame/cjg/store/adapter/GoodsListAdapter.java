package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.GoodsInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商品列表adapter
 * Created by TAJ_C on 2015/11/20.
 */
public class GoodsListAdapter extends BaseAdapter {

    private Context context;
    private List<GoodsInfo> goodsList;

    private boolean isShowListstyle;

    public GoodsListAdapter(Context context, boolean isShowListstyle, List<GoodsInfo> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
        this.isShowListstyle = isShowListstyle;
    }

    @Override
    public int getCount() {
        if (goodsList != null) {
            return goodsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public GoodsInfo getItem(int position) {
        return goodsList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(isShowListstyle ?
                    R.layout.item_store_game_brand_list_style : R.layout.item_store_game_brand_pic_style, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GoodsInfo goodsInfo = getItem(position);
        if (goodsInfo != null) {
            viewHolder.ordinaryPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.nameView.setText(goodsInfo.getGoodsName());
            if (AccountUtil.isMember()) {
                viewHolder.ordinaryPriceView.setText(context.getString(R.string.store_goods_mark_price, goodsInfo.getOrdinaryGoodsPrice()));
                viewHolder.priceView.setText(goodsInfo.getGoodsPrice());
                viewHolder.ordinaryPriceView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ordinaryPriceView.setVisibility(View.GONE);
                viewHolder.priceView.setText(goodsInfo.getOrdinaryGoodsPrice());
            }

            viewHolder.sockView.setText(context.getString(R.string.store_goods_storage, goodsInfo.getGoodsStorage()));
            viewHolder.discountsView.setVisibility(TextUtils.isEmpty(goodsInfo.getGoodsVipDiscountStr()) ? View.GONE : View.VISIBLE);
            viewHolder.discountsView.setText(goodsInfo.getGoodsVipDiscountStr());
            viewHolder.memberView.setVisibility((goodsInfo.getGoodsNeedVipLevel() <= 0) ? View.GONE : View.VISIBLE);
            viewHolder.memberView.setText(context.getString(R.string.store_goods_need_vip_level, goodsInfo.getGoodsNeedVipLevel()));

            if (isShowListstyle) {
                viewHolder.gameBrandIconView.setImageUrlAndReUse(goodsInfo.getGoodsImageUrl());
                RelativeLayout.LayoutParams priceParams = (RelativeLayout.LayoutParams) viewHolder.priceView.getLayoutParams();
                priceParams.topMargin = (AccountUtil.isMember() ? ComUtil.dpToPx(12) : ComUtil.dpToPx(34));

                RelativeLayout.LayoutParams headParams = (RelativeLayout.LayoutParams) viewHolder.priceHeaderView.getLayoutParams();
                headParams.topMargin = (AccountUtil.isMember() ? ComUtil.dpToPx(20) : ComUtil.dpToPx(42));
            } else {
                viewHolder.gameBrandPicView.setImageUrlAndReUse(goodsInfo.getBigListImage());
            }
        }
        return convertView;
    }

    public void refreshData(List<GoodsInfo> goodsList) {
        this.goodsList = goodsList;
        notifyDataSetChanged();
    }

    class ViewHolder {

        @Bind(R.id.tv_name)
        TextView nameView;
        @Bind(R.id.tv_price)
        TextView priceView;

        @Bind(R.id.tv_ordinary_price)
        TextView ordinaryPriceView;

        @Bind(R.id.tv_sock)
        TextView sockView;

        @Bind(R.id.tv_price_header)
        TextView priceHeaderView;

        @Bind(R.id.tv_member)
        TextView memberView;

        @Bind(R.id.tv_discounts)
        TextView discountsView;

        @Nullable
        @Bind(R.id.game_brand_icon)
        FSSimpleImageView gameBrandIconView;

        @Nullable
        @Bind(R.id.game_brand_pic)
        FSSimpleImageView gameBrandPicView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
