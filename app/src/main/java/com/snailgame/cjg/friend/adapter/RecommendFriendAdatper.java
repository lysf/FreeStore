package com.snailgame.cjg.friend.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.friend.FriendListFragment;
import com.snailgame.cjg.friend.model.Friend;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/5/10.
 */
public class RecommendFriendAdatper extends BaseAdapter {
    private Context mContext;
    private List<Friend> mRecommendList;

    private final int MAX_RECOMMEND_NUM = 6;

    public RecommendFriendAdatper(Context mContext, List<Friend> mRecommendList) {
        this.mContext = mContext;
        this.mRecommendList = mRecommendList;
    }

    @Override
    public int getCount() {
        if (mRecommendList == null) {
            return 0;
        } else {
            return mRecommendList.size() > MAX_RECOMMEND_NUM ? MAX_RECOMMEND_NUM : mRecommendList.size();
        }
    }

    @Override
    public Friend getItem(int position) {
        return mRecommendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_recommend, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Friend friend = getItem(position);
        if (friend != null) {
            String gameName = friend.getGameName();
            viewHolder.friendTitleView.setText(friend.getNickName());
            viewHolder.friendGameNameView.setText(TextUtils.isEmpty(gameName) ?
                    mContext.getString(R.string.friend_game_none_hint) : gameName);
            viewHolder.friendGameTitleView.setText(TextUtils.isEmpty(gameName) ?
                    R.string.friend_play_game_none_title : R.string.friend_play_game_title);

            viewHolder.friendPhotoView.setImageUrlAndReUse(friend.getPhoto());
            viewHolder.friendAddView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendHandleUtil.handleFriend(mContext, FriendListFragment.class.getSimpleName(),
                            friend, FriendHandleUtil.FRIEND_HANDLE_ADD);
                }
            });
        }
        return convertView;
    }

    public void refreshData(List<Friend> recommendList) {
        this.mRecommendList = recommendList;
        notifyDataSetChanged();
    }

    public void removeFriend(Friend friend) {
        if (mRecommendList != null && friend != null) {
            mRecommendList.remove(friend);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        @Bind(R.id.siv_friend_photo)
        FSSimpleImageView friendPhotoView;
        @Bind(R.id.tv_friend_title)
        TextView friendTitleView;
        @Bind(R.id.tv_friend_game_name)
        TextView friendGameNameView;
        @Bind(R.id.tv_friend_add)
        TextView friendAddView;

        @Bind(R.id.tv_friend_game_title)
        TextView friendGameTitleView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
