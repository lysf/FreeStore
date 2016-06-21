package com.snailgame.cjg.store.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.store.model.VirRechargeModel;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

/**
 * Created by TAJ_C on 2015/12/1.
 */
public class VirRechargeHotAdapter extends BaseAdapter {

    private Context context;
    private static final int TYPE_NUM = 2;
    private static final int TYPE_TOP = 0;
    private static final int TYPE_BOTTOM = 1;
    private static final int TOP_NUM = 4;
    private VirRechargeHotTopAdapter topAdapter;
    private VirRechargeHotBottomAdapter bottomAdapter;
    private List<VirRechargeModel.ModelItem.GameGoodsInfo> mGameGoodsList;

    public VirRechargeHotAdapter(Context context, List<VirRechargeModel.ModelItem.GameGoodsInfo> gameGoodsList) {
        this.context = context;

        if (gameGoodsList != null && gameGoodsList.size() > TOP_NUM) {
            topAdapter = new VirRechargeHotTopAdapter(context, gameGoodsList.subList(0, TOP_NUM));
            bottomAdapter = new VirRechargeHotBottomAdapter(context, gameGoodsList.subList(TOP_NUM, gameGoodsList.size()));
        } else {
            topAdapter = new VirRechargeHotTopAdapter(context, gameGoodsList);
        }
    }

    @Override
    public int getCount() {
        return TYPE_NUM;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*   @Override
       public int getItemViewType(int position) {
           return TYPE_NUM;
       }
   */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case TYPE_TOP:
                FullGridView topView = new FullGridView(context);
                topView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                topView.setNumColumns(2);
                if (topAdapter != null) {
                    topView.setAdapter(topAdapter);
                }
                convertView = topView;
                break;
            case TYPE_BOTTOM:
                FullGridView bottomView = new FullGridView(context);
                bottomView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                bottomView.setBackgroundColor(ResUtil.getColor(R.color.white));
                bottomView.setNumColumns(3);
                if (bottomAdapter != null) {
                    bottomView.setAdapter(bottomAdapter);
                }
                convertView = bottomView;
                break;
        }
        return convertView;
    }
}
