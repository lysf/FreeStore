package com.snailgame.cjg.personal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.VoucherCooperActivity;
import com.snailgame.cjg.personal.model.MyVoucherGoodsModel;
import com.snailgame.fastdev.util.ResUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的商品代金券 adapter
 * Created by pancl on 2015/4/30.
 */
public class MyVoucherGoodsAdapter extends BaseAdapter {

    private List<MyVoucherGoodsModel.ModelItem> listData;

    private Activity context;

    public MyVoucherGoodsAdapter(Activity context, List<MyVoucherGoodsModel.ModelItem> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.my_voucher_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            final MyVoucherGoodsModel.ModelItem item = getItem(position);
            viewHolder.iv_voucher_icon.setImageUrlAndReUse(item.getcIcon());
            viewHolder.tv_voucher_name.setText(item.getiAmount() + context.getString(R.string.yuan) + item.getsVoucherName());
            viewHolder.tv_voucher_name.setTextColor(ResUtil.getColor(R.color.red));
            viewHolder.tv_voucher_desc.setText(item.getsDesc());
            viewHolder.tv_voucher_validity.setText(context.getString(R.string.my_voucher_deadline, item.getFormatteddEnd()));

            if (item.getdEndDate() == null) {
                viewHolder.tv_voucher_validity.setTextColor(ResUtil.getColor(R.color.voucher_deadline_color));
            } else {
                Date now = new Date();
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(item.getdEndDate());
                rightNow.add(Calendar.DAY_OF_YEAR, -4);//日期减4天
                if (now.before(item.getdEndDate()) && now.after(rightNow.getTime())) {
                    viewHolder.tv_voucher_validity.setTextColor(ResUtil.getColor(R.color.red));
                } else {
                    viewHolder.tv_voucher_validity.setTextColor(ResUtil.getColor(R.color.voucher_deadline_color));
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(VoucherCooperActivity.newIntent(context, item));
                }
            });
        }
        return convertView;
    }


    @Override
    public int getCount() {
        if (null != listData) {
            return listData.size();
        }
        return 0;
    }

    @Override
    public MyVoucherGoodsModel.ModelItem getItem(int position) {
        if (null != listData && position < getCount()) {
            return listData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addNewData(List<MyVoucherGoodsModel.ModelItem> addListData) {
        if (listData != null) {
            listData.addAll(addListData);
            notifyDataSetChanged();
        }
    }


    class ViewHolder {

        @Bind(R.id.iv_voucher_icon)
        FSSimpleImageView iv_voucher_icon;
        @Bind(R.id.tv_voucher_name)
        TextView tv_voucher_name;
        @Bind(R.id.tv_voucher_desc)
        TextView tv_voucher_desc;
        @Bind(R.id.tv_voucher_validity)
        TextView tv_voucher_validity;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
