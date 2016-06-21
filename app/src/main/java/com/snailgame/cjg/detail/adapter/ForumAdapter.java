package com.snailgame.cjg.detail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.detail.model.ForumModel;
import com.snailgame.cjg.global.FreeStoreApp;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taj on 2014/11/20.
 */
public class ForumAdapter extends BaseAdapter {

    private Context mContext;
    private List<ForumModel.ModelItem> mForumList;

    public ForumAdapter(Context context, List<ForumModel.ModelItem> forumList) {
        this.mContext = context;
        this.mForumList = forumList;
    }

    @Override
    public int getCount() {
        if (mForumList != null) {
            return mForumList.size();
        } else {
            return 0;
        }
    }

    @Override
    public ForumModel.ModelItem getItem(int position) {
        if(mForumList!=null&&position<mForumList.size())
            return mForumList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.forum_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ForumModel.ModelItem item = getItem(position);
        holder.titleView.setText(item.getSubject());
        holder.repliesView.setText(item.getReplies());
        holder.authorView.setText(item.getAuthor());
        holder.headerLabelView.setVisibility(item.getDisplayorder() != null && item.getDisplayorder().equals("0") ? View.GONE : View.VISIBLE);
        return convertView;
    }

    public void refreshDate(List<ForumModel.ModelItem> forumList) {
        this.mForumList = forumList;
        notifyDataSetChanged();
    }


    class ViewHolder {
        @Bind(R.id.tv_header_label)
        TextView headerLabelView;
        @Bind(R.id.tv_title)
        TextView titleView;
        @Bind(R.id.tv_replies)
        TextView repliesView;
        @Bind(R.id.tv_author)
        TextView authorView;
        public ViewHolder(View itemView){
            ButterKnife.bind(this,itemView);
        }
    }
}
