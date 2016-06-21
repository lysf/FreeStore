package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.member.model.MemberPrivilege;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/12/22.
 */
public class MemberHeadFrameAdapter extends MemberDetailBaseAdapter {
    private int numColumns;
    private String memberType;

    public MemberHeadFrameAdapter(Context context, List<MemberPrivilege.ModelItem.LevelPrivilege> list, int numColumns, String memberType) {
        super(context, list);
        this.numColumns = numColumns;
        this.memberType = memberType;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            int line = list.size() / numColumns;
            if (line * numColumns < list.size()) {
                return (line + 1) * numColumns;
            } else {
                return list.size();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_head_frame, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MemberPrivilege.ModelItem.LevelPrivilege item = getItem(position);
        if (item != null && item.getArticleInfo() != null) {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.iconView.setVisibility(View.VISIBLE);

            try {
                String iconUrl;

                iconUrl = JSON.parseObject(item.getAwardConfig()).getString("img");
                viewHolder.iconView.setImageUrlAndReUse(iconUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            viewHolder.titleView.setText(item.getsDesc());
        } else {
            viewHolder.titleView.setVisibility(View.INVISIBLE);
            viewHolder.iconView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.tv_level_title)
        TextView titleView;

        @Bind(R.id.siv_level_icon)
        FSSimpleImageView iconView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
