package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.member.model.MemberPrivilege;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 游戏畅玩
 * Created by TAJ_C on 2015/12/14.
 */
public class MemberGameVoucherAdapter extends MemberDetailBaseAdapter {
    private int currentLevelId;
    private int needLevelId;

    public MemberGameVoucherAdapter(Context context, int currentLevelId, int needLevelId, List<MemberPrivilege.ModelItem.LevelPrivilege> list) {
        super(context, list);
        initInfoData(this.list);
        this.currentLevelId = currentLevelId;
        this.needLevelId = needLevelId;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_game_voucher, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MemberPrivilege.ModelItem.LevelPrivilege item = getItem(position);
        if (item != null && item.getArticleInfo() != null) {
            viewHolder.iconView.setImageUrlAndReUse(item.getArticleInfo().getLogo());
            viewHolder.memberView.setText(item.getLevelName() + context.getString(R.string.member_level_spree_member_special));
            if (TextUtils.isEmpty(item.getDeadline())) {
                viewHolder.desView.setText("");
            } else {
                viewHolder.desView.setText(ResUtil.getString(R.string.member_valid_date) + item.getDeadline().substring(0, 10));
            }

            viewHolder.titleView.setText(item.getArticleInfo().getArticleName());
            if (item.isReceive() || currentLevelId < needLevelId) {
                viewHolder.getView.setText(currentLevelId < needLevelId ? R.string.not_get : R.string.have_get);
                viewHolder.getView.setOnClickListener(null);
                viewHolder.getView.setBackgroundResource(R.drawable.btn_grey_selector);
            } else {
                viewHolder.getView.setText(R.string.not_get);
                viewHolder.getView.setBackgroundResource(R.drawable.btn_green_selector);
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
            }

        }
        return convertView;
    }

    public List<MemberPrivilege.ModelItem.LevelPrivilege> initInfoData(List<MemberPrivilege.ModelItem.LevelPrivilege> listData) {
        if (listData == null)
            return null;

        for (MemberPrivilege.ModelItem.LevelPrivilege item : listData) {
            String info = item.getArticleInfo().getIntro();
            try {
                JSONObject object = JSON.parseObject(info);
                if (object.containsKey("content"))
                    item.setContent(object.getString("content"));
                if (object.containsKey("useMethod"))
                    item.setUseMethod(object.getString("useMethod"));
                if (object.containsKey("deadline"))
                    item.setDeadline(object.getString("deadline"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listData;
    }

    class ViewHolder {
        @Bind(R.id.siv_game_voucher_icon)
        FSSimpleImageView iconView;

        @Bind(R.id.tv_game_voucher_title)
        TextView titleView;

        @Bind(R.id.tv_game_voucher_des)
        TextView desView;

        @Bind(R.id.tv_game_voucher_member)
        TextView memberView;

        @Bind(R.id.btn_get)
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