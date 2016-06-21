package com.snailgame.cjg.personal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.VoucherGameRecordModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 代金券使用详情列表
 * Created by pancl on 2015/4/29.
 */
public class VoucherGameDetailAdapter extends BaseAdapter {

    private List<VoucherGameRecordModel.ModelItem> listData;

    public VoucherGameDetailAdapter(List<VoucherGameRecordModel.ModelItem> modelItems) {
        this.listData = modelItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.voucher_detail_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            final VoucherGameRecordModel.ModelItem item = getItem(position);
            viewHolder.tv_record_consume.setText(String.valueOf(-item.getiConsume()));
            viewHolder.tv_record_desc.setText(item.getsAppName());
            viewHolder.tv_record_create.setText(item.getdCreate());
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
    public VoucherGameRecordModel.ModelItem getItem(int position) {
        if (null != listData && position < getCount()) {
            return listData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addNewData(List<VoucherGameRecordModel.ModelItem> addListData) {
        if (listData != null) {
            listData.addAll(addListData);
            notifyDataSetChanged();
        }
    }


    class ViewHolder {

        @Bind(R.id.tv_record_consume)
        TextView tv_record_consume;
        @Bind(R.id.tv_record_desc)
        TextView tv_record_desc;
        @Bind(R.id.tv_record_create)
        TextView tv_record_create;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
