package com.snailgame.cjg.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.ResUtil;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by sunxy on 14-4-25.
 */
public class SettingAdapter extends BaseAdapter {
    private static final int[] sIcons = {
            R.drawable.ic_setting_wifi,
            R.drawable.ic_setting_dialog,
            R.drawable.ic_setting_root_auto_install,
            R.drawable.ic_setting_unroot_auto_install,
            R.drawable.ic_setting_delete,
            R.drawable.ic_setting_message,
            R.drawable.ic_setting_check_update,
            R.drawable.ic_setting_update,
            R.drawable.ic_setting_contact,
            R.drawable.ic_setting_about};

    private String titles[];

    private LayoutInflater inflater;

    private Map<Integer, Boolean> map;

    public SettingAdapter(Context context, Map<Integer, Boolean> map) {
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        titles = ResUtil.getStringArray(R.array.setting_array);
        this.map = map;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SettingHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.setting_list_layout, null);
            holder = new SettingHolder(view);
            view.setTag(holder);
        } else {
            holder = (SettingHolder) view.getTag();
        }

        if (map.containsKey(i)) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(map.get(i));
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }


        holder.icon.setImageResource(sIcons[i]);
        holder.title.setText(titles[i]);
        holder.lineView.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
        return view;
    }

    static class SettingHolder {
        @Bind(R.id.setting_icon)
        ImageView icon;
        @Bind(R.id.setting_title)
        TextView title;
        @Bind(R.id.setting_check)
        CheckBox checkBox;

        @Bind(R.id.line_view)
        View lineView;

        public SettingHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
