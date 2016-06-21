package com.snailgame.cjg.news.adpter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelDragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 列表数据是否改变
     */
    private boolean isListChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<NewsChannel> channelList;


    /**
     * 要删除的position
     */
    public int remove_position = -1;
    /**
     * 移动过程中需要隐藏的position
     */
    private int hideViewPosition = -1;

    private boolean isShowDel = false;
    //当前资讯所在频道（字体颜色为绿色）
    private String currentItem;

    public ChannelDragAdapter(Context context, List<NewsChannel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public NewsChannel getItem(int position) {
        // TODO Auto-generated method stub
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        convertView = LayoutInflater.from(context).inflate(R.layout.item_news_channel, parent, false);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);

        final NewsChannel channel = getItem(position);

        if (channel != null) {
            viewHolder.itemView.setTextColor(ResUtil.getColor(channel.getChannelName().equals(currentItem) ?
                    R.color.btn_green_normal : R.color.primary_text_color));
            viewHolder.itemView.setText(channel.getChannelName());
            viewHolder.itemView.setVisibility(View.VISIBLE);
            if(position == 0){
                viewHolder.delView.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.delView.setVisibility(View.VISIBLE);
            }
            if (position < 3) {
                viewHolder.itemView.setEnabled(false);
            }
            if (isChanged && (position == holdPosition) && !isItemShow) {
//                viewHolder.itemView.setText("");
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.delView.setVisibility(View.INVISIBLE);
                viewHolder.itemView.setSelected(true);
                viewHolder.itemView.setEnabled(true);
                hideViewPosition = holdPosition;
                isChanged = false;
            }
            if (!isVisible && (position == -1 + channelList.size())) {
//                viewHolder.itemView.setText("");
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.delView.setVisibility(View.INVISIBLE);
                viewHolder.itemView.setSelected(true);
                viewHolder.itemView.setEnabled(true);
            }
            if (remove_position == position) {
//                viewHolder.itemView.setText("");
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.delView.setVisibility(View.INVISIBLE);
            }

            if (!isChanged && position == hideViewPosition) {
                convertView.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    /**
     * 添加频道列表
     */
    public void addItem(NewsChannel channel) {
        channelList.add(channel);
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        NewsChannel dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<NewsChannel> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        isListChanged = true;
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
     * 排序是否发生改变
     */
    public boolean isListChanged() {
        return isListChanged;
    }

    /**
     * 数据变化后，重置isListChanged
     */
    public void resetChanged() {
        isListChanged = false;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
        notifyDataSetChanged();
    }

    public void showDelView() {
        isShowDel = true;
        notifyDataSetChanged();
    }

    public void hideDelView() {
        isShowDel = false;
        notifyDataSetChanged();
    }

    public void hideMovingView(int position) {
        hideViewPosition = position;
        notifyDataSetChanged();
    }

    public void initHideViewPosition() {
        hideViewPosition = -1;
        notifyDataSetChanged();
    }
    //当前资讯所在频道
    public void setCurrentItem(String currentItem){
        this.currentItem = currentItem;
        notifyDataSetChanged();
    }
    class ViewHolder {
        @Bind(R.id.tv_text_item)
        TextView itemView;

        @Bind(R.id.iv_icon_del)
        ImageView delView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}