package com.snailgame.cjg.personal.adapter;

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
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.MyVoucherGoodsCooperModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Uesr : Pancl
 * Date : 15-6-23
 * Time : 上午10:37
 * Description :商品代金券合作游戏adapter
 */
public class VoucherGamelistAdapter extends BaseAdapter {
    private Context context;
    private List<MyVoucherGoodsCooperModel.ModelItem> modelItems;

    public VoucherGamelistAdapter(Context context, List<MyVoucherGoodsCooperModel.ModelItem> modelItems) {
        this.context = context;
        this.modelItems = modelItems;
    }

    @Override
    public int getCount() {
        if (modelItems == null) {
            return 0;
        }
        return modelItems.size();
    }

    @Override
    public MyVoucherGoodsCooperModel.ModelItem getItem(int position) {
        if (modelItems == null)
            return null;
        if (position < modelItems.size())
            return modelItems.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.voucher_gamelist_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MyVoucherGoodsCooperModel.ModelItem item = getItem(position);
        if (item != null) {
            viewHolder.appTitle.setText(item.getAppName());
            viewHolder.appIcon.setImageUrlAndReUse(item.getAppIcon().replace("\n", ""));
            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newIntent(context, item.getUrl()));
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.appIcon)
        public FSSimpleImageView appIcon;
        @Bind(R.id.appTitle)
        public TextView appTitle;
        @Bind(R.id.main_layout)
        LinearLayout mainLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
