package com.snailgame.cjg.seekgame.rank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by sunxy on 2015/3/26.
 * 排行 地区过滤
 */
public class CountryFilterAdapter extends BaseAdapter {
    private int selectIndex = 0;
    private String countrys[];
    private LayoutInflater inflater;
    private int white, black;

    public CountryFilterAdapter(Context context, String itemArray[], int selectIndex) {
        countrys = itemArray;
        inflater = LayoutInflater.from(context);
        white = ResUtil.getColor(R.color.white);
        black = ResUtil.getColor(R.color.black);
        this.selectIndex = selectIndex;
    }

    @Override
    public int getCount() {
        if (countrys != null)
            return countrys.length;
        return 0;
    }

    @Override
    public String getItem(int position) {
        if (position < countrys.length)
            return countrys[position];
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rank_filter_country_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectIndex) {
            holder.country.setTextColor(white);
            holder.country.setBackgroundResource(R.drawable.rank_filter_item_select);
        } else {
            holder.country.setTextColor(black);
            holder.country.setBackgroundResource(R.drawable.rank_filter_item_normal);
        }

        String countryString = getItem(position);
        if (countryString != null)
            holder.country.setText(countryString);
        return convertView;
    }

    public void notifyDataChange(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.rank_filter_country)
        TextView country;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
