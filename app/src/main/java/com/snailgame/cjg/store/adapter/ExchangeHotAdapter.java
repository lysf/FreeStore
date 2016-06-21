package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.ExchangeInfo;
import com.snailgame.cjg.util.ComUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/12/2.
 */
public class ExchangeHotAdapter extends BaseAdapter {

    private Context context;
    private List<ExchangeInfo> exchangeInfos;
    private static final int LINE_COUNT = 4;

    public ExchangeHotAdapter(Context context, List<ExchangeInfo> exchangeInfos) {
        this.context = context;
        this.exchangeInfos = exchangeInfos;
    }

    @Override
    public int getCount() {
        if (exchangeInfos != null) {
            return exchangeInfos.size() / LINE_COUNT + (exchangeInfos.size() % LINE_COUNT == 0 ? 0 : 1);
        } else {
            return 0;
        }
    }

    @Override
    public ExchangeInfo getItem(int position) {
        return exchangeInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store_point_hot, parent, false);
            viewHolder = new ViewHolder(convertView);

            //图片下方三个 商品添加到 布局中
            for (int i = 0; i < LINE_COUNT - 1; i++) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_store_point_hot_content, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ComUtil.dpToPx(148), 1);
                itemView.setLayoutParams(params);

                viewHolder.itemContainer.addView(itemView);

                if (i != LINE_COUNT - 2) {
                    View spaceView = new View(context);
                    spaceView.setLayoutParams(new LinearLayout.LayoutParams(ComUtil.dpToPx(8), 1));
                    viewHolder.itemContainer.addView(spaceView);
                }
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置图片的 view
        int realPosition = position * LINE_COUNT;
        final ExchangeInfo picExchangeInfo = exchangeInfos.get(realPosition);
        if (picExchangeInfo != null) {
            viewHolder.picView.setImageUrlAndReUse(picExchangeInfo.getCPicUrl());
            viewHolder.picView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newIntent(context, picExchangeInfo.getSUrl()));
                }
            });
        }

        //设置图片商品下方三个商品view
        for (int i = 0; i < LINE_COUNT - 1; i++) {
            if ((2 * i) < viewHolder.itemContainer.getChildCount()) {
                int itemPosition = realPosition + i + 1;

                if (itemPosition < exchangeInfos.size()) {
                    final ExchangeInfo itemGoodsInfo = exchangeInfos.get(itemPosition);
                    View contentView = viewHolder.itemContainer.getChildAt(2 * i);
                    ContentViewHolder contentViewHolder = new ContentViewHolder(contentView);
                    contentViewHolder.goodsIconView.setImageUrlAndReUse(itemGoodsInfo.getCPicUrl());
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(WebViewActivity.newIntent(context, itemGoodsInfo.getSUrl()));
                        }
                    });

                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.siv_big_pic)
        FSSimpleImageView picView;

        @Bind(R.id.hor_item_container)
        LinearLayout itemContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    class ContentViewHolder {
        @Bind(R.id.siv_goods_icon)
        FSSimpleImageView goodsIconView;

        public ContentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
