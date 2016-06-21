package com.snailgame.cjg.member.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.member.model.MemberArticle;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;

/**
 * 会员专享礼包详情
 * Created by TAJ_C on 2015/12/30.
 */
public class MemberSpreeDetailDialog extends BottomSheet {

    @Bind(R.id.spree_detail_container)
    RelativeLayout spreeContainer;
    @Bind(R.id.spree_logo)
    FSSimpleImageView logoView;
    @Bind(R.id.spree_title)
    TextView titleView;
    @Bind(R.id.spree_remain)
    TextView remainView;
    @Bind(R.id.spree_content)
    TextView contentView;
    @Bind(R.id.dead_time)
    TextView deadTimeView;
    @Bind(R.id.use_method)
    TextView useMethodView;
    @Bind(R.id.exchange)
    TextView exchangeView;

    private MemberArticle spree;
    private Context context;
    private int currentLevelId;
    private int needLevelId;
    private int goodsId;

    public MemberSpreeDetailDialog(Context context, MemberArticle spree, int currentLevelId, int needLevelId, int goodsId) {
        super(context);
        this.spree = spree;
        this.context = context;
        this.currentLevelId = currentLevelId;
        this.needLevelId = needLevelId;
        this.goodsId = goodsId;
    }

    public MemberSpreeDetailDialog(Context context, int theme, MemberArticle spree) {
        super(context, theme);
        this.spree = spree;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.dialog_spree_detail, null);
        setContentDialogView(mDialogView);
        showSpreeView(spree);
    }

    @Override
    public AbsListView getAbsListView() {
        return null;
    }

    private void showSpreeView(final MemberArticle item) {
        logoView.setImageUrl(item.getLogo());
        titleView.setText(item.getAppName() + item.getArticleName());

        contentView.setText(item.getContent());
        deadTimeView.setText(getSpannableString(context.getString(R.string.dead_line) + item.getDeadline(), 0, context.getString(R.string.dead_line).length(), R.color.child_text_color));
        useMethodView.setText(getSpannableString(item.getUseMethod(), 0, item.getUseMethod().length() - 1 < 5 ? item.getUseMethod().length() - 1 : 5, R.color.child_text_color));


        //有库存
        if (item.getRemianNum() > 0 && currentLevelId >= needLevelId) {
            //没有领取
            if (TextUtils.isEmpty(item.getCdKey())) {
                //领取按钮
                exchangeView.setBackgroundResource(R.drawable.btn_green_selector);
                exchangeView.setText(R.string.not_get);
                exchangeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                            AccountUtil.userLogin(context);
                            return;
                        }

                        if (listener != null) {
                            try {
                                listener.getGoodsRequest(item.getArticeId(),0, goodsId);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
                //没有领取显示剩余
                String headerTxt = ResUtil.getString(R.string.remain_spree);
                String remianNum = headerTxt + item.getRemianNum();
                SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
                currencyBuilder.append(remianNum);
                currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                        headerTxt.length(), remianNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                remainView.setText(currencyBuilder);
                remainView.setVisibility(View.VISIBLE);

            } else {
                //领取过 显示拷贝
                exchangeView.setText(R.string.copy);
                exchangeView.setBackgroundResource(R.drawable.btn_blue_selector);
                exchangeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //复制
                        ComUtil.copyToClipBoard(context, item.getCdKey());
                    }
                });
                remainView.setVisibility(View.VISIBLE);
                String headerTxt = ResUtil.getString(R.string.exchange_code);
                String currency = headerTxt + item.getCdKey();
                SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
                currencyBuilder.append(currency);
                currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)), headerTxt.length(), currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                remainView.setText(currencyBuilder);
            }
        } else {
            //没有库存 显示灰色
            exchangeView.setText(currentLevelId >= needLevelId ? R.string.none_get : R.string.not_get);
            exchangeView.setBackgroundResource(R.drawable.btn_grey_selector);
            exchangeView.setOnClickListener(null);
            remainView.setVisibility(View.GONE);
        }


    }


    private SpannableStringBuilder getSpannableString(String content, int begin, int end, int resColor) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
        if (stringBuilder.length() - 1 > end) {
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(resColor)),
                    begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return stringBuilder;
    }

    public void spreeGetSuccess(int articeId, String cdkey) {
        if (spree.getArticeId() == articeId) {
            spree.setCdKey(cdkey);
            showSpreeView(spree);
        }

    }

    private GoodsGetListener listener;

    public void setOnGoodsGetListener(GoodsGetListener listener) {
        this.listener = listener;
    }

}