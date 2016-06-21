package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.GoodsInfo;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/12/3.
 */
public class PointStoreAllAdapter extends BaseAdapter {
    private Context context;

    private List<GoodsInfo> goodsInfoList;

    public PointStoreAllAdapter(Context context, List<GoodsInfo> goodsInfoList) {
        this.context = context;
        this.goodsInfoList = goodsInfoList;
    }

    @Override
    public int getCount() {
        if (goodsInfoList == null) {
            return 0;
        } else {
            return goodsInfoList.size();
        }
    }

    @Override
    public GoodsInfo getItem(int position) {
        return goodsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store_point_all, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final GoodsInfo goodsInfo = getItem(position);
        if (goodsInfo != null) {
            viewHolder.nameView.setText(goodsInfo.getGoodsName());
            viewHolder.pointView.setText(goodsInfo.getIntegral() + ResUtil.getString(R.string.score_point));
            viewHolder.ordinaryPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.ordinaryPriceView.setText(ResUtil.getString(R.string.yuan_char) + goodsInfo.getGoodsPrice());
            viewHolder.reducePriceView.setText("+" + goodsInfo.getGoodsIntegralPrice() + ResUtil.getString(R.string.yuan));
            viewHolder.saleView.setText(ResUtil.getString(R.string.store_goods_sales, goodsInfo.getSaleNum()));
            viewHolder.iconView.setImageUrlAndReUse(goodsInfo.getGoodsImageUrl());
        }

        viewHolder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(WebViewActivity.newIntent(context, goodsInfo.getGoodsUrl()));
            }
        });
        return convertView;
    }

    public void addData(List<GoodsInfo> goodsInfoList) {
        this.goodsInfoList.addAll(goodsInfoList);
        notifyDataSetChanged();
    }


    class ViewHolder {
        @Bind(R.id.siv_goods_icon)
        FSSimpleImageView iconView;
        @Bind(R.id.tv_goods_name)
        TextView nameView;

        @Bind(R.id.tv_goods_point)
        TextView pointView;
        @Bind(R.id.tv_goods_reduce_price)
        TextView reducePriceView;
        @Bind(R.id.tv_goods_ordinary_price)
        TextView ordinaryPriceView;

        @Bind(R.id.tv_goods_sale)
        TextView saleView;

        @Bind(R.id.line_view)
        View lineView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
