package com.snailgame.cjg.detail.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.event.GetGiftPostEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SpreeUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 游戏详情礼包列表
 * Created by sunxy on 2015/5/4.
 */
public class GameSpreeAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<SpreeGiftInfo> spreeList;

    public GameSpreeAdapter( ArrayList<SpreeGiftInfo> spreeList, Activity mActivity) {
        this.mActivity = mActivity;
        this.spreeList = spreeList;
    }

    @Override
    public int getCount() {
        return spreeList == null ? 0 : spreeList.size();
    }

    @Override
    public SpreeGiftInfo getItem(int position) {
        if(spreeList!=null&&position<spreeList.size())
            return spreeList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<SpreeGiftInfo> getListData() {
        if (spreeList == null)
            spreeList = new ArrayList<SpreeGiftInfo>();
        return spreeList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SpreeViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.game_spree_item_layout, parent, false);
            holder = new SpreeViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SpreeViewHolder) convertView.getTag();
        }
        final SpreeGiftInfo item = getItem(position);

        if (item != null) {
            holder.title.setText("【"+item.getsArticleName()+"】");
            holder.content.setText(item.getContent());

            SpreeUtils.setExchangeBtn(mActivity, holder.exchangeCodeView, holder.exchangeBtn, item, true);

            if (TextUtils.isEmpty(item.getcCdkey()) && item.isAvaiable()) {
                holder.exchangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                            AccountUtil.userLogin(mActivity);
                            return;
                        }

                        MainThreadBus.getInstance().post(new GetGiftPostEvent(item));

                    }
                });
            }

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mActivity.startActivity(SpreeDetailActivity.newIntent(mActivity, item));
//                }
//            });
        }

        return convertView;
    }


    public void changeSpreeState(SpreeGiftInfo spreeInfo) {
        if (spreeList != null) {
            for (SpreeGiftInfo info : spreeList) {
                if (info.getiArticleId() == spreeInfo.getiArticleId()) {
                    if (info.getiRemianNum() != SpreeUtils.REMAIN_NUM_UNLIMITED) {
                        info.setiRemianNum(info.getiRemianNum() - 1);
                    }
                    info.setiGetNum(info.getiGetNum() - 1);
                    info.setcCdkey(spreeInfo.getcCdkey());
                    notifyDataSetChanged();
                }
            }

        }

    }

    static class SpreeViewHolder {
        @Bind(R.id.game_spree_title)
        TextView title;
        @Bind(R.id.game_spree_content)
        TextView content;
        @Bind(R.id.game_spree_exchange)
        TextView exchangeBtn;
        @Bind(R.id.tv_cd_key)
        TextView exchangeCodeView;

        public SpreeViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        SpreeUtils.initInfoData(this.spreeList);
    }

}
