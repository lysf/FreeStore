package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.NecessaryAppInfo;
import com.snailgame.cjg.util.FileUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lic on 2015/6/3.
 * 装机必备的dialog adapter
 */
public class NecessaryAppDialogAdapter extends BaseAdapter {

    private Context context;
    private List<NecessaryAppInfo> list;

    public NecessaryAppDialogAdapter(Context context, List<NecessaryAppInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public NecessaryAppInfo getItem(int position) {
        if (list != null) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.necessary_app_dialog_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NecessaryAppInfo necessaryAppInfo = list.get(position);
        if (necessaryAppInfo.isSelected()) {
            holder.iconSelect.setImageResource(R.drawable.necessary_app_item_s);
        } else {
            holder.iconSelect.setImageResource(R.drawable.necessary_app_item_n);
        }
        holder.iconApp.setImageUrlAndReUse(necessaryAppInfo.getcIcon());
        holder.nameTV.setText(necessaryAppInfo.getsAppName());
        holder.sizeTV.setText(FileUtil.formatFileSize(context, necessaryAppInfo.getiSize()));
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.tv_name)
        TextView nameTV;
        @Bind(R.id.tv_size)
        TextView sizeTV;
        @Bind(R.id.icon_app)
        FSSimpleImageView iconApp;
        @Bind(R.id.icon_select)
        ImageView iconSelect;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
