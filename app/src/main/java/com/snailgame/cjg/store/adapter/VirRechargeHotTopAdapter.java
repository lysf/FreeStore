package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.VirRechargeModel;
import com.snailgame.cjg.util.ComUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 虚拟充值 热门游戏 头部
 * Created by TAJ_C on 2015/12/4.
 */
public class VirRechargeHotTopAdapter extends BaseAdapter {
    private Context context;
    List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList;

    public VirRechargeHotTopAdapter(Context context, List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList) {
        this.context = context;
        this.gameGoodsList = gameGoodsList;
    }


    @Override
    public int getCount() {
        if (gameGoodsList == null) {
            return 0;
        } else {
            return gameGoodsList.size();
        }
    }

    @Override
    public VirRechargeModel.ModelItem.GameGoodsInfo getItem(int position) {
        return gameGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vir_recharge_hot_top, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final VirRechargeModel.ModelItem.GameGoodsInfo item = getItem(position);
        if (item != null) {
            viewHolder.nameView.setText(item.getGoodsName());
            viewHolder.desView.setText(item.getDiscount());
            viewHolder.goodsIconView.setImageUrlAndReUse(item.getImageUrl());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newIntent(context, item.getGoodsUrl()));
                }
            });

            boolean isTop = (position == 0 || position == 1);
            boolean isBottom = (position == getCount() - 1 || position == getCount() - 2);
            boolean isLeft = (position % 2 == 0);

            viewHolder.rechargeContainer.setPadding(isLeft ? ComUtil.dpToPx(16) : ComUtil.dpToPx(8), isTop ? ComUtil.dpToPx(20) : ComUtil.dpToPx(10),
                    isLeft ? ComUtil.dpToPx(8) : ComUtil.dpToPx(16), isBottom ? ComUtil.dpToPx(20) : ComUtil.dpToPx(10));
        }
        return convertView;

    }

    class ViewHolder {
        @Bind(R.id.siv_goods_icon)
        FSSimpleImageView goodsIconView;

        @Bind(R.id.tv_name)
        TextView nameView;
        @Bind(R.id.tv_des)
        TextView desView;


        @Bind(R.id.recharge_container)
        LinearLayout rechargeContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
