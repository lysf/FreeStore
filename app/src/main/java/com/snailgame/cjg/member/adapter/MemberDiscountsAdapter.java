package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.snailgame.cjg.R;
import com.snailgame.cjg.member.MemberDetailActivity;
import com.snailgame.cjg.member.model.MemberPrivilege;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 会员折扣
 * Created by TAJ_C on 2015/12/14.
 */
public class MemberDiscountsAdapter extends MemberDetailBaseAdapter {

    private String memberType;

    public MemberDiscountsAdapter(Context context, String memberType, List<MemberPrivilege.ModelItem.LevelPrivilege> list) {
        super(context, list);
        this.memberType = memberType;

        //添加头
        MemberPrivilege.ModelItem.LevelPrivilege header = new MemberPrivilege.ModelItem.LevelPrivilege();
        header.setLevelName(context.getString(R.string.member_discounts_level_));
        if (memberType.equals(MemberDetailActivity.TYPE_KUWAN_DISCOUNT_BUY)) {
            header.setAwardConfig(context.getString(R.string.member_discounts));
        } else if (memberType.equals(MemberDetailActivity.TYPE_INTEGRAL_RAISE)) {
            header.setAwardConfig(context.getString(R.string.member_beishu));
        }

        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.add(0, header);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_discounts, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        boolean isHeader = (position == 0);
        viewHolder.levelView.setTextSize(isHeader ? 16 : 14);
        viewHolder.levelView.setTextColor(ResUtil.getColor(isHeader ? R.color.primary_text_color : R.color.second_text_color));
        viewHolder.discountView.setTextSize(isHeader ? 16 : 14);
        viewHolder.discountView.setTextColor(ResUtil.getColor(isHeader ? R.color.primary_text_color : R.color.second_text_color));
        viewHolder.levelView.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        viewHolder.discountView.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);


        MemberPrivilege.ModelItem.LevelPrivilege item = getItem(position);
        if (item != null) {
            viewHolder.levelView.setText(item.getLevelName());
            if (item.getAwardConfig() != null) {
                try {
                    if (memberType.equals(MemberDetailActivity.TYPE_KUWAN_DISCOUNT_BUY)) {

                        if (isHeader) {
                            viewHolder.discountView.setText(item.getAwardConfig());
                        } else {
                            float discountPer = JSON.parseObject(item.getAwardConfig()).getFloatValue("discount");
                            viewHolder.discountView.setText((10 - (discountPer * 0.1)) + context.getString(R.string.member_discount_footer));
                        }

                    } else if (memberType.equals(MemberDetailActivity.TYPE_INTEGRAL_RAISE)) {

                        if (isHeader) {
                            viewHolder.discountView.setText(item.getAwardConfig());
                        } else {
                            float multiple = JSON.parseObject(item.getAwardConfig()).getFloatValue("multiple");
                            viewHolder.discountView.setText((1 + multiple / 100) + context.getString(R.string.member_bei_footer));
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.tv_level)
        TextView levelView;

        @Bind(R.id.tv_discounts)
        TextView discountView;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
