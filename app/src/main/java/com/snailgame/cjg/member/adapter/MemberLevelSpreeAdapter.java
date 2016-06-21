package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.member.model.MemberLevelArticle;
import com.snailgame.cjg.member.model.MemberPrivilege;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 会员等级礼包
 * Created by TAJ_C on 2015/12/22.
 */
public class MemberLevelSpreeAdapter extends MemberDetailBaseAdapter {
    private int currentLevel;
    private int numColumns;

    public MemberLevelSpreeAdapter(Context context, int currentLevel, List<MemberPrivilege.ModelItem.LevelPrivilege> list, int numColumns) {
        super(context, list);
        this.currentLevel = currentLevel;
        this.numColumns = numColumns;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_level_spree, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MemberPrivilege.ModelItem.LevelPrivilege item = getItem(position);
        if (item != null && item.getArticleInfo() != null) {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.iconView.setVisibility(View.VISIBLE);
            viewHolder.levelView.setVisibility(View.VISIBLE);
            viewHolder.getView.setVisibility(View.VISIBLE);

            MemberLevelArticle info = item.getArticleInfo();
            viewHolder.iconView.setImageUrlAndReUse(info.getLogo());
            viewHolder.titleView.setText(info.getArticleName());
            viewHolder.levelView.setText(item.getLevelName() + context.getString(R.string.member_level_spree_member_special));
            if (currentLevel >= item.getLevelId() && false == item.isReceive()) {
                viewHolder.getView.setBackgroundResource(R.drawable.btn_green_selector);
                viewHolder.getView.setText(R.string.not_get);
                viewHolder.getView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                            AccountUtil.userLogin(context);
                            return;
                        }

                        if (listener != null) {
                            try {
                                listener.getGoodsRequest(item.getArticleInfo().getArticeId(), item.getLevelId(), JSON.parseObject(item.getAwardConfig()).getIntValue("goodsId"));
                            } catch (Exception e) {
                            }
                        }
                    }
                });
            } else {
                if (item.isReceive()) {
                    viewHolder.getView.setText(R.string.have_get);
                }
                viewHolder.getView.setBackgroundResource(R.drawable.btn_grey_selector);
                viewHolder.getView.setOnClickListener(null);
            }

        } else {
            viewHolder.titleView.setVisibility(View.INVISIBLE);
            viewHolder.iconView.setVisibility(View.INVISIBLE);
            viewHolder.levelView.setVisibility(View.INVISIBLE);
            viewHolder.getView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.siv_icon)
        FSSimpleImageView iconView;

        @Bind(R.id.tv_title)
        TextView titleView;

        @Bind(R.id.tv_level)
        TextView levelView;

        @Bind(R.id.tv_get)
        TextView getView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


    private GoodsGetListener listener;

    public void setOnGoodsGetListener(GoodsGetListener listener) {
        this.listener = listener;
    }
}
