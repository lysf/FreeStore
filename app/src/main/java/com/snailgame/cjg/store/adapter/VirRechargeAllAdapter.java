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
 * 虚拟充值 全部游戏
 * Created by TAJ_C on 2015/12/1.
 */
public class VirRechargeAllAdapter extends BaseAdapter {
    private Context context;
    private List<VirRechargeModel.ModelItem.GameGoodsInfo> mGameGoodsList;

    public VirRechargeAllAdapter(Context context, List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList) {
        this.context = context;
        this.mGameGoodsList = gameGoodsList;
    }


    @Override
    public int getCount() {
        if (mGameGoodsList == null) {
            return 0;
        } else {
            return mGameGoodsList.size();
        }
    }

    @Override
    public VirRechargeModel.ModelItem.GameGoodsInfo getItem(int position) {
        return mGameGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vir_recharge_all, parent, false);
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
            viewHolder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            viewHolder.seeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newIntent(context, item.getGoodsUrl()));
                }
            });
        }
        return convertView;

    }

    public void addData(List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList) {
        this.mGameGoodsList.addAll(gameGoodsList);
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.siv_goods_icon)
        FSSimpleImageView goodsIconView;

        @Bind(R.id.tv_goods_name)
        TextView nameView;
        @Bind(R.id.tv_goods_des)
        TextView desView;
        @Bind(R.id.tv_see)
        TextView seeView;

        @Bind(R.id.line_view)
        View lineView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
