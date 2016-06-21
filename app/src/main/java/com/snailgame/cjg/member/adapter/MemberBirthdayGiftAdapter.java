package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.member.model.MemberPrivilege;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/12/16.
 */
public class MemberBirthdayGiftAdapter extends MemberDetailBaseAdapter {
    public MemberBirthdayGiftAdapter(Context context, List<MemberPrivilege.ModelItem.LevelPrivilege> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_birthday_gift, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MemberPrivilege.ModelItem.LevelPrivilege item = getItem(position);
        if (item != null) {
            viewHolder.levelView.setText(item.getLevelName());
            if (item.getArticleInfo() != null) {
                viewHolder.birdthdayDesView.setText(item.getArticleInfo().getArticleName());
            }
        }
        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.tv_level)
        TextView levelView;

        @Bind(R.id.tv_birthday_des)
        TextView birdthdayDesView;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}