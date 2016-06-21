package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.member.model.MemberArticle;
import com.snailgame.cjg.member.widget.MemberSpreeDetailDialog;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.fastdev.util.ResUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专享礼包
 * Created by TAJ_C on 2015/12/21.
 */
public class MemberExclusiveSpreeAdapter extends BaseAdapter {

    private Context mContext;
    private List<MemberArticle> spreeList;

    private String[] mGoodsIds;
    private int currentLevelId;
    private int needLevelId;
    private MemberSpreeDetailDialog dialog;

    public MemberExclusiveSpreeAdapter(Context context, int currentLevelId, int needLevelId, String goodsIds, List<MemberArticle> spreeList) {
        this.mContext = context;
        this.spreeList = spreeList;
        this.currentLevelId = currentLevelId;
        this.needLevelId = needLevelId;
        if (!TextUtils.isEmpty(goodsIds)) {
            try {
                mGoodsIds = new JSONObject(goodsIds).getString("goodsIds").split(",");
            } catch (JSONException e) {
                mGoodsIds = new String[]{""};
                e.printStackTrace();

            }
        } else {
            mGoodsIds = new String[]{""};
        }
        initInfoData(spreeList);
    }

    @Override
    public int getCount() {
        if (spreeList != null) {
            return spreeList.size();
        } else {
            return 0;
        }
    }

    @Override
    public MemberArticle getItem(int position) {
        return spreeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spree_item_all_spree, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MemberArticle item = getItem(position);

        if (item != null) {
            viewHolder.appNameView.setText(item.getAppName());
            viewHolder.spreeTitleView.setText(item.getArticleName());
            viewHolder.spreeTitleView.setTextColor(ResUtil.getColor(R.color.member_game_play_dies));
            viewHolder.spreeContentView.setText(item.getContent());
            viewHolder.iconView.setImageUrlAndReUse(item.getLogo());
            viewHolder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

            //没有领取显示剩余
            String headerTxt;
            SpannableStringBuilder currencyBuilder;

            headerTxt = ResUtil.getString(R.string.remain_spree);
            String remianNum = headerTxt + item.getRemianNum();
            currencyBuilder = new SpannableStringBuilder();
            currencyBuilder.append(remianNum);
            currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                    headerTxt.length(), remianNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.cdKeyView.setText(currencyBuilder);
            viewHolder.cdKeyView.setVisibility(View.VISIBLE);


            //有库存
            if (item.getRemianNum() > 0 && currentLevelId >= needLevelId) {
                //没有领取
                if (TextUtils.isEmpty(item.getCdKey())) {
                    //领取按钮
                    viewHolder.spreeGetBtn.setBackgroundResource(R.drawable.btn_green_selector);
                    viewHolder.spreeGetBtn.setText(R.string.not_get);
                    viewHolder.spreeGetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                                AccountUtil.userLogin(mContext);
                                return;
                            }

                            if (listener != null && position < mGoodsIds.length) {
                                try {
                                    listener.getGoodsRequest(item.getArticeId(), 0, Integer.parseInt(mGoodsIds[position]));
                                } catch (Exception e) {
                                }
                            }
                        }
                    });


                } else {
                    //领取过 显示拷贝
                    viewHolder.spreeGetBtn.setText(R.string.copy);
                    viewHolder.spreeGetBtn.setBackgroundResource(R.drawable.btn_blue_selector);
                    viewHolder.spreeGetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //复制
                            ComUtil.copyToClipBoard(mContext, item.getCdKey());
                        }
                    });
                    viewHolder.cdKeyView.setVisibility(View.VISIBLE);
                    headerTxt = ResUtil.getString(R.string.exchange_code);
                    String currency = headerTxt + item.getCdKey();
                    currencyBuilder = new SpannableStringBuilder();
                    currencyBuilder.append(currency);
                    currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)), headerTxt.length(), currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.cdKeyView.setText(currencyBuilder);
                }
            } else {
                //没有库存 显示灰色
                viewHolder.spreeGetBtn.setText(currentLevelId >= needLevelId ? R.string.none_get : R.string.not_get);
                viewHolder.spreeGetBtn.setBackgroundResource(R.drawable.btn_grey_selector);
                viewHolder.spreeGetBtn.setOnClickListener(null);
                viewHolder.cdKeyView.setVisibility(View.GONE);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog = new MemberSpreeDetailDialog(mContext, item, currentLevelId, needLevelId, Integer.parseInt(mGoodsIds[position]));
                    dialog.setOnGoodsGetListener(listener);
                    dialog.show();
                } catch (Exception e) {
                }
            }
        });

        return convertView;
    }

    public void refreshData(ArrayList<MemberArticle> spreeGiftInfoList) {
        this.spreeList = spreeGiftInfoList;
        initInfoData(spreeList);
        notifyDataSetChanged();
    }

    public void refreshData(int articeId, String cdKey) {
        if (spreeList != null) {
            for (MemberArticle info : spreeList) {
                if (info.getArticeId() == articeId) {
                    info.setCdKey(cdKey);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
        if (dialog != null) {
            dialog.spreeGetSuccess(articeId, cdKey);
        }
    }


    public List<MemberArticle> initInfoData(List<MemberArticle> listData) {
        if (listData == null)
            return null;

        for (MemberArticle item : listData) {
            String info = item.getIntro();
            try {
                JSONObject object = new JSONObject(info);
                if (object.has("content"))
                    item.setContent(object.getString("content"));
                if (object.has("useMethod"))
                    item.setUseMethod(object.getString("useMethod"));
                if (object.has("deadline"))
                    item.setDeadline(object.getString("deadline"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listData;
    }


    class ViewHolder {
        @Bind(R.id.iv_spree_logo)
        FSSimpleImageView iconView;
        @Bind(R.id.tv_app_name)
        TextView appNameView;
        @Bind(R.id.tv_spree_title)
        TextView spreeTitleView;
        @Bind(R.id.tv_spree_content)
        TextView spreeContentView;
        @Bind(R.id.btn_spree)
        TextView spreeGetBtn;
        @Bind(R.id.tv_cd_key)
        TextView cdKeyView;

        @Bind(R.id.line_view)
        View lineView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private GoodsGetListener listener;

    public void setOnGoodsGetListener(GoodsGetListener listener) {
        this.listener = listener;
    }
}
