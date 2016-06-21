package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.store.model.VirRechargeModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 虚拟充值 热门游戏下方
 * Created by TAJ_C on 2015/11/27.
 */
public class VirRechargeHotBottomAdapter extends BaseAdapter {
    private Context context;
    List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList;

    public VirRechargeHotBottomAdapter(Context context, List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vir_recharge_hot_bottom, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final VirRechargeModel.ModelItem.GameGoodsInfo item = getItem(position);
        if (item != null) {
            viewHolder.nameView.setText(item.getGoodsName());
            viewHolder.desView.setText(item.getDiscount());
            viewHolder.goodsIconView.setImageUrlAndReUse(item.getIconUrl());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newIntent(context, item.getGoodsUrl()));
                }
            });

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
        @Bind(R.id.tv_see)
        TextView seeView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
