package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.ExchangeInfo;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/12/3.
 */
public class ExchangeAllAdapter extends BaseAdapter {
    private Context context;

    private List<ExchangeInfo> exchangeInfos;

    public ExchangeAllAdapter(Context context, List<ExchangeInfo> exchangeInfos) {
        this.context = context;
        this.exchangeInfos = exchangeInfos;
    }

    @Override
    public int getCount() {
        if (exchangeInfos == null) {
            return 0;
        } else {
            return exchangeInfos.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store_point_all, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ExchangeInfo exchangeInfo = getItem(position);
        if (exchangeInfo != null) {
            viewHolder.nameView.setText(exchangeInfo.getSName());
            viewHolder.pointView.setText(exchangeInfo.getIIntegral() + ResUtil.getString(R.string.score_point));
            if (!TextUtils.isEmpty(exchangeInfo.getCOriginalPrice())) {
                viewHolder.ordinaryPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.ordinaryPriceView.setText(ResUtil.getString(R.string.yuan_char) + exchangeInfo.getCOriginalPrice());
            } else {
                viewHolder.ordinaryPriceView.setText("");
            }

            viewHolder.iconView.setImageUrlAndReUse(exchangeInfo.getCIcon());
        }

        viewHolder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(WebViewActivity.newIntent(context, exchangeInfo.getSUrl()));
            }
        });
        return convertView;
    }

    public void addData(List<ExchangeInfo> exchangeInfos) {
        this.exchangeInfos.addAll(exchangeInfos);
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
