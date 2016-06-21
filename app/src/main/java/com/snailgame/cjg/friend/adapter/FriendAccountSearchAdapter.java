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
import com.snailgame.cjg.friend.model.ContactModel;
import com.snailgame.cjg.friend.model.Friend;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/5/18.
 */
public class FriendAccountSearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContactModel> mFriendList;

    private static final String STATUS_FRIEND_YES = "0";
    private static final String STATUS_FRIEND_APPLY = "1";
    private static final String STATUS_FRIEND_NO = "2";

    public FriendAccountSearchAdapter(Context mContext, List<ContactModel> friendList) {
        this.mContext = mContext;
        this.mFriendList = friendList;
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
    public ContactModel getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_account_search, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ContactModel friend = getItem(position);
        if (friend != null) {
            viewHolder.friendPhotoView.setImageUrlAndReUse(friend.getPhoto());
            viewHolder.friendNameView.setText(friend.getNickName());
            viewHolder.friendAccountView.setText(friend.getAccountName());

            boolean isShowAdd = true;
            if (!TextUtils.isEmpty(friend.getIsFriend()) && (friend.getIsFriend().equals(STATUS_FRIEND_YES) || friend.getIsFriend().equals(STATUS_FRIEND_APPLY))) {
                isShowAdd = false;
            }
            viewHolder.addView.setVisibility(isShowAdd ? View.VISIBLE : View.GONE);
            viewHolder.hintView.setVisibility(isShowAdd ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(friend.getIsFriend())) {
                if (friend.getIsFriend().equals(STATUS_FRIEND_APPLY)) {
                    viewHolder.hintView.setText(mContext.getString(R.string.friend_add_sended));
                } else if (friend.getIsFriend().equals(STATUS_FRIEND_YES)) {
                    viewHolder.hintView.setText(mContext.getString(R.string.friend_added));
                }
            }

            viewHolder.addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendHandleUtil.handleFriend(mContext, FriendListFragment.class.getSimpleName(),
                            friend, FriendHandleUtil.FRIEND_HANDLE_ADD);
                }
            });

        }

        return convertView;
    }

    public void refreshData(List<ContactModel> friendList) {
        this.mFriendList = friendList;
        notifyDataSetChanged();
    }

    public void removeFriend(Friend friend) {
        if (mFriendList != null && friend != null) {
            mFriendList.remove(friend);
            notifyDataSetChanged();
        }
    }

    public void applyFriend(Friend friend) {
        if (mFriendList != null && friend != null) {
            for (ContactModel item : mFriendList) {
                if (item.getUserId() == friend.getUserId()) {
                    item.setIsFriend(STATUS_FRIEND_APPLY);
                    break;
                }
            }
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        @Bind(R.id.siv_photo)
        FSSimpleImageView friendPhotoView;
        @Bind(R.id.tv_friend_name)
        TextView friendNameView;
        @Bind(R.id.tv_friend_account_name)
        TextView friendAccountView;

        @Bind(R.id.tv_friend_add)
        TextView addView;

        @Bind(R.id.tv_friend_text_hint)
        TextView hintView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
