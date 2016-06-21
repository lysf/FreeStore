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
import com.snailgame.cjg.util.ComUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/5/10.
 */
public class FriendListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Friend> mFriendList;
    private int tag;

    public FriendListAdapter(Context mContext, int tag, List<Friend> mFriendList) {
        this.mContext = mContext;
        this.tag = tag;
        this.mFriendList = mFriendList;
    }

    @Override
    public int getCount() {
        if (mFriendList == null) {
            return 0;
        } else {
            return mFriendList.size();
        }
    }

    @Override
    public Friend getItem(int position) {
        return mFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置按键显示隐藏
        boolean isApply = (tag == FriendListFragment.TAG_APPLY);
        viewHolder.friendReceiveView.setVisibility(isApply ? View.VISIBLE : View.GONE);
        viewHolder.friendRejectView.setVisibility(isApply ? View.VISIBLE : View.GONE);

        if (isApply) {
            viewHolder.friendNameView.setMaxWidth(ComUtil.dpToPx(138));
            viewHolder.friendGameNameView.setMaxWidth(ComUtil.dpToPx(120));
        }

        final Friend friend = getItem(position);
        if (friend != null) {
            viewHolder.friendPhotoView.setImageUrlAndReUse(friend.getPhoto());
            viewHolder.friendNameView.setText(friend.getNickName());
            viewHolder.friendGameNameView.setText(TextUtils.isEmpty(friend.getGameName()) ?
                    mContext.getString(R.string.friend_game_none_hint) : mContext.getString(R.string.friend_game_title, friend.getGameName()));
            viewHolder.friendGameIconView.setVisibility(TextUtils.isEmpty(friend.getGameName()) ? View.GONE : View.VISIBLE);
            viewHolder.friendReceiveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendHandleUtil.handleFriend(mContext, FriendListFragment.class.getSimpleName(),
                            friend, FriendHandleUtil.FRIEND_HANDLE_RECEIVE);
                }
            });
            viewHolder.friendRejectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendHandleUtil.handleFriend(mContext, FriendListFragment.class.getSimpleName(),
                            friend, FriendHandleUtil.FRIEND_HANDLE_REJECT);
                }
            });
        }
        return convertView;
    }

    public void refreshData(List<Friend> friendList) {
        this.mFriendList = friendList;
        notifyDataSetChanged();
    }

    public void addFriend(Friend friend) {
        if (mFriendList == null) {
            mFriendList = new ArrayList<>();
        }
        mFriendList.add(0, friend);
        notifyDataSetChanged();
    }

    public void removeFriend(Friend friend) {
        if (mFriendList != null && friend != null) {
            int index = -1;
            for (int i = 0; i < mFriendList.size(); i++) {
                if (friend.getUserId() == mFriendList.get(i).getUserId()) {
                    index = i;
                }
            }
            if (index != -1) {
                mFriendList.remove(index);
            }
            notifyDataSetChanged();
        }
    }


    static class ViewHolder {
        @Bind(R.id.siv_photo)
        FSSimpleImageView friendPhotoView;
        @Bind(R.id.tv_friend_name)
        TextView friendNameView;
        @Bind(R.id.tv_friend_game_name)
        TextView friendGameNameView;
        @Bind(R.id.tv_friend_reject)
        TextView friendRejectView;
        @Bind(R.id.tv_friend_receive)
        TextView friendReceiveView;

        @Bind(R.id.iv_friend_game)
        View friendGameIconView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
