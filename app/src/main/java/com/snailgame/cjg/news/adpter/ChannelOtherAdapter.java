package com.snailgame.cjg.news.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.NewsChannel;

import java.util.List;

public class ChannelOtherAdapter extends BaseAdapter {
    private Context context;
    public List<NewsChannel> channelList;
    private TextView itemView;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public ChannelOtherAdapter(Context context, List<NewsChannel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public NewsChannel getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_channel, null);
        itemView = (TextView) view.findViewById(R.id.tv_text_item);
        itemView.setVisibility(View.VISIBLE);
        NewsChannel channel = getItem(position);
        itemView.setText(channel.getChannelName());
        if (!isVisible && (position == -1 + channelList.size())) {
            itemView.setText("");
            itemView.setVisibility(View.INVISIBLE);
        }
        if (remove_position == position) {
            itemView.setText("");
            itemView.setVisibility(View.INVISIBLE);
        }
        return view;
    }


    /**
     * 获取频道列表
     */
    public List<NewsChannel> getChannelLst() {
        return channelList;
    }

    /**
     * 添加频道列表
     */
    public void addItem(NewsChannel channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<NewsChannel> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}