package com.snailgame.cjg.spree.adapter;

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
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 全部礼包 adapter
 * Created by TAJ_C on 2015/5/5.
 */
public class AllSpreeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SpreeGiftInfo> spreeList;

    public AllSpreeAdapter(Context context, ArrayList<SpreeGiftInfo> spreeList) {
        this.mContext = context;
        this.spreeList = spreeList;
        SpreeUtils.initInfoData(spreeList);
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
    public SpreeGiftInfo getItem(int position) {
        return spreeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spree_item_all_spree, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SpreeGiftInfo spreeGiftInfo = getItem(position);
        viewHolder.appNameView.setText(spreeGiftInfo.getsAppName());
        viewHolder.spreeTitleView.setText(spreeGiftInfo.getsArticleName());
        viewHolder.spreeContentView.setText(spreeGiftInfo.getContent());
        viewHolder.iconView.setImageUrlAndReUse(spreeGiftInfo.getcLogo());


        viewHolder.spreeGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                    AccountUtil.userLogin(mContext);
                    return;
                }

                if (listener != null) {
                    listener.spreeGetAction(spreeGiftInfo);
                }
            }
        });
        SpreeUtils.setExchangeBtn(mContext, viewHolder.cdKeyView, viewHolder.spreeGetBtn, spreeGiftInfo, true);


        if (spreeGiftInfo.getiRemianNum() > 0) {
            if (TextUtils.isEmpty(spreeGiftInfo.getcCdkey())) {
                String headerTxt = ResUtil.getString(R.string.remain_spree);
                String remainNum = headerTxt + spreeGiftInfo.getiRemianNum();
                SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
                currencyBuilder.append(remainNum);
                currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                        headerTxt.length(), remainNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.cdKeyView.setText(currencyBuilder);
                viewHolder.cdKeyView.setVisibility(View.VISIBLE);
            }
        } else {
            String headerTxt = ResUtil.getString(R.string.tao_spree);
            String taoNum = headerTxt + spreeGiftInfo.getiTao();
            SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
            currencyBuilder.append(taoNum);
            currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                    headerTxt.length(), taoNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.cdKeyView.setText(currencyBuilder);
            viewHolder.cdKeyView.setVisibility(View.VISIBLE);
        }

        viewHolder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        return convertView;
    }

    public void refreshData(ArrayList<SpreeGiftInfo> spreeGiftInfoList) {
        this.spreeList = spreeGiftInfoList;
        SpreeUtils.initInfoData(spreeList);
        notifyDataSetChanged();
    }

    public void refreshData(SpreeGiftInfo spreeGiftInfo) {
        if (spreeList != null) {
            for (SpreeGiftInfo info : spreeList) {
                if (info.getiArticleId() == spreeGiftInfo.getiArticleId()) {
                    info.setcCdkey(spreeGiftInfo.getcCdkey());
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public void refreshTaoData(SpreeGiftInfo spreeGiftInfo) {
        if (spreeList != null) {
            for (SpreeGiftInfo info : spreeList) {
                if (info.getiArticleId() == spreeGiftInfo.getiArticleId()) {
                    info.setiTao(spreeGiftInfo.getiTao());
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public ArrayList<SpreeGiftInfo> getSpreeList() {
        return spreeList;
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


    private SpreeGetListener listener;

    public interface SpreeGetListener {
        void spreeGetAction(SpreeGiftInfo spreeGiftInfo);
    }

    public void setOnSpreeGetListener(SpreeGetListener listener) {
        this.listener = listener;
    }
}
