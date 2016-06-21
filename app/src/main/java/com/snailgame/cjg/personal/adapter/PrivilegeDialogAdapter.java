package com.snailgame.cjg.personal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.PrivilegeDialogModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 特权弹框内奖励列表
 * Created by pancl on 2015/5/21.
 */
public class PrivilegeDialogAdapter extends BaseAdapter {
    private List<PrivilegeDialogModel.ModelItem> listData;

    public PrivilegeDialogAdapter(List<PrivilegeDialogModel.ModelItem> modelItems) {
        this.listData = modelItems;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public PrivilegeDialogModel.ModelItem getItem(int position) {
        if (listData != null && position < getCount()) {
            return listData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.privilege_dialog_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            final PrivilegeDialogModel.ModelItem item = getItem(position);
            viewHolder.iv_award_icon.setImageUrlAndReUse(item.getIcon());
            viewHolder.tv_award_title.setText(item.getTitle());
            viewHolder.tv_award_desc.setText(item.getDesc());
        }
        return convertView;
    }

    class ViewHolder {

        @Bind(R.id.iv_award_icon)
        FSSimpleImageView iv_award_icon;
        @Bind(R.id.tv_award_title)
        TextView tv_award_title;
        @Bind(R.id.tv_award_desc)
        TextView tv_award_desc;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
