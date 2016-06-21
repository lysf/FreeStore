package com.snailgame.cjg.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.friend.FriendListFragment;
import com.snailgame.cjg.friend.model.ContactModel;
import com.snailgame.cjg.friend.utils.FriendHandleUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/5/16.
 */
public class FriendContactAdapter extends BaseAdapter {
    private Context context;

    private List<ContactModel> friendContactList;

    public FriendContactAdapter(Context context, List<ContactModel> contactList) {
        this.context = context;
        this.friendContactList = contactList;
    }

    @Override
    public int getCount() {
        if (friendContactList == null) {
            return 0;
        } else {
            return friendContactList.size();
        }
    }

    @Override
    public ContactModel getItem(int position) {
        return friendContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_contact, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final ContactModel item = getItem(position);
        if (item != null) {
            int selection = item.getFirstPinYin().charAt(0);
            int positonForSelection = getPositionForSection(selection);
            if (position == positonForSelection) {
                viewHolder.contactTitleView.setVisibility(View.VISIBLE);
                viewHolder.contactTitleView.setText(item.getFirstPinYin());
            } else {
                viewHolder.contactTitleView.setVisibility(View.GONE);
            }

            viewHolder.accountNameView.setText(item.getNickName());
            viewHolder.contactNameView.setText(item.getContactName());
            viewHolder.photoView.setImageUrlAndReUse(item.getPhoto());
            viewHolder.friendAddView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendHandleUtil.handleFriend(context, FriendListFragment.class.getSimpleName(),
                            item, FriendHandleUtil.FRIEND_HANDLE_ADD);
                }
            });
        }
        return convertView;
    }

    public void refreshData(List<ContactModel> friendList) {
        this.friendContactList = friendList;
        notifyDataSetChanged();
    }


    @SuppressLint("DefaultLocale")
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = friendContactList.get(i).getFirstPinYin();

            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    static class ViewHolder {
        @Bind(R.id.tv_contact_title)
        TextView contactTitleView;
        @Bind(R.id.tv_account_name)
        TextView accountNameView;
        @Bind(R.id.tv_contact_name)
        TextView contactNameView;
        @Bind(R.id.tv_friend_add)
        TextView friendAddView;

        @Bind(R.id.siv_friend_photo)
        FSSimpleImageView photoView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }
}
