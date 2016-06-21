package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.personal.model.ScoreHistoryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/11/10.
 */
public class ScoreUsedHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<ScoreHistoryModel.ModelItem> recordList;
    private final static String SCORE_USED = "1";    // 使用
    private final static String SCORE_GOT = "0";    // 获得

    public ScoreUsedHistoryAdapter(Context context, ArrayList<ScoreHistoryModel.ModelItem> lists) {
        this.context = context;
        this.recordList = lists;
    }


    @Override
    public int getCount() {
        if (recordList == null)
            return 0;

        return recordList.size();

    }

    @Override
    public ScoreHistoryModel.ModelItem getItem(int position) {
        if (recordList == null)
            return null;

        if (position < recordList.size())
            return recordList.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.currency_history_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ScoreHistoryModel.ModelItem item = getItem(position);

        if (item != null) {
            holder.contentView.setText(item.getDesc());
            holder.timeView.setText(item.getCreateTime());

            if (item.getType().equals(SCORE_USED)) {
                holder.changedNumView.setText(String.format(context.getString(R.string.score_used), Math.abs(item.getIntegral())));
            } else {
                holder.changedNumView.setText(String.format(context.getString(R.string.score_got), Math.abs(item.getIntegral())));
            }
            holder.lineView.setVisibility(position == getCount() -1 ? View.GONE:View.VISIBLE);
        }

        return convertView;
    }


    public List<ScoreHistoryModel.ModelItem> getList() {
        return recordList;
    }

    public void refreshData(List<ScoreHistoryModel.ModelItem> lists) {
        this.recordList = lists;

        notifyDataSetChanged();
    }

    public void addData(List<ScoreHistoryModel.ModelItem> lists){
        if(recordList == null)
            recordList = new ArrayList<>();

        recordList.addAll(lists);
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


}
