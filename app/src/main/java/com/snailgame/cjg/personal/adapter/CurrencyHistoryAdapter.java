package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.CurrencyHistoryModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 兔兔币 历史记录 adapter
 * Created by TAJ_C on 2015/4/20.
 */
public class CurrencyHistoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CurrencyHistoryModel.ModelItem> lists;
    private final static String HISTORY_USED = "1";    // 使用
    private final static String HISTORY_GETED = "0";    // 获得

    public CurrencyHistoryAdapter(Context context, ArrayList<CurrencyHistoryModel.ModelItem> lists) {
        this.context = context;
        this.lists = lists;
    }


    @Override
    public int getCount() {
        if (lists == null)
            return 0;

        return lists.size();
    }

    @Override
    public CurrencyHistoryModel.ModelItem getItem(int position) {
        if (lists == null)
            return null;

        if (position < lists.size())
            return lists.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.currency_history_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CurrencyHistoryModel.ModelItem item = getItem(position);
        if (holder != null) {
            if (item != null) {
                holder.contentView.setText(item.getsDesc());
                holder.timeView.setText(item.getdCreate());

                if (item.getcType().equals(HISTORY_USED)) {
                    holder.changedNumView.setText(String.format(context.getString(R.string.score_used), Math.abs(item.getiMoney())));
                } else {
                    holder.changedNumView.setText(String.format(context.getString(R.string.score_got), Math.abs(item.getiMoney())));
                }

            }
        }

        holder.lineView.setVisibility(position < getCount() - 1 ? View.VISIBLE : View.GONE);
        return convertView;
    }

    class ViewHolder {

        @Bind(R.id.tv_content)
        TextView contentView;
        @Bind(R.id.tv_changed_num)
        TextView changedNumView;
        @Bind(R.id.tv_time)
        TextView timeView;

        @Bind(R.id.line_view)
        View lineView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void addData(ArrayList<CurrencyHistoryModel.ModelItem> lists) {
        if (lists == null || lists.size() == 0)
            return;

        if (this.lists == null || this.lists.size() == 0) {
            this.lists = lists;
            return;
        }
        this.lists.addAll(lists);
    }

    public ArrayList<CurrencyHistoryModel.ModelItem> getLists() {
        return lists;
    }

    public void refreshData(ArrayList<CurrencyHistoryModel.ModelItem> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

}
