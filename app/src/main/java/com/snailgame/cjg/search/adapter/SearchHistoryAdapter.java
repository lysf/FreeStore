package com.snailgame.cjg.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by pancl on 2014/11/5.
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> searchHistorys;

    public SearchHistoryAdapter(Context context, List<String> searchHistorys) {
        this.mContext = context;
        this.searchHistorys = searchHistorys == null ? new ArrayList<String>() : searchHistorys;

    }

    @Override
    public int getCount() {
        return searchHistorys.size();
    }

    @Override
    public String getItem(int position) {
        if (position < searchHistorys.size()) {
            return searchHistorys.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder = null;
        if (convertView == null) {
            view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.activity_app_search_history_item, null);
            if (view != null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        String searchKeyword = getItem(position);
        if (!TextUtils.isEmpty(searchKeyword)) {
            if (holder.searchKeyword != null) {
                holder.searchKeyword.setText(searchKeyword);
            }
        }
        return view;
    }

    class ViewHolder {

        @Bind(R.id.search_item_keyword)
        TextView searchKeyword;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
