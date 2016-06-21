package com.snailgame.cjg.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.event.FillSearchEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.search.model.SearchKeyModel;
import com.snailgame.cjg.util.MainThreadBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunxy on 2014/12/29.
 */
public class SearchTipsResultAdapter extends BaseAdapter {
    private Context mContext;
    private List<SearchKeyModel.ModelItem> resultLists;
    private LayoutInflater inflater;
    private View.OnClickListener onClickPlus;

    public SearchTipsResultAdapter(Context context, List<SearchKeyModel.ModelItem> resultLists) {
        mContext = context;
        this.resultLists = resultLists;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        this.onClickPlus = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = String.valueOf(v.getTag());
                MainThreadBus.getInstance().post(new FillSearchEvent(keyword));
            }
        };
    }

    @Override
    public int getCount() {
        if (resultLists != null)
            return resultLists.size();
        return 2;
    }

    @Override
    public SearchKeyModel.ModelItem getItem(int position) {
        if (resultLists != null && position < resultLists.size())
            return resultLists.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.auto_comp_listview_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final SearchKeyModel.ModelItem item = getItem(position);
        if (item != null) {
            viewHolder.appName.setText(item.getsAppName());
            viewHolder.search_history_add.setOnClickListener(onClickPlus);
            viewHolder.search_history_add.setTag(item.getsAppName());
        }

        return convertView;

    }

    class ViewHolder {
        @Bind(R.id.text)
        TextView appName;
        @Bind(R.id.search_history_add)
        ImageView search_history_add;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
