package com.snailgame.cjg.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.SpreeTaoSuccessEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;

/**
 * Created by lic on 2015/6/4
 * 礼包详情的dialog
 */
public class SpreeDetailDialog extends BottomSheet {
    static String TAG = SpreeDetailDialog.class.getName();

    @Bind(R.id.spree_detail_container)
    RelativeLayout spreeContainer;
    @Bind(R.id.spree_logo)
    FSSimpleImageView logo;
    @Bind(R.id.spree_title)
    TextView title;
    @Bind(R.id.spree_remain)
    TextView remain;
    @Bind(R.id.spree_content)
    TextView content;
    @Bind(R.id.dead_time)
    TextView dead_time;
    @Bind(R.id.use_method)
    TextView use_method;
    @Bind(R.id.exchange)
    TextView exchange;
    private SpreeGiftInfo spree;
    private String get_spree, score_for_exchange;
    private String copy;
    private ArrayList<String> mKeyArray;
    private Context context;

    public SpreeDetailDialog(Context context, SpreeGiftInfo spree) {
        super(context);
        this.spree = spree;
        this.context = context;
    }

    public SpreeDetailDialog(Context context, int theme, SpreeGiftInfo spree) {
        super(context, theme);
        this.spree = spree;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.dialog_spree_detail, null);
        setContentDialogView(mDialogView);
        mKeyArray = new ArrayList<>();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MainThreadBus.getInstance().unregister(SpreeDetailDialog.this);
                FreeStoreApp.getRequestQueue().cancelAll(TAG);
            }
        });
        showSpreeView(spree);
     }

    @Override
    public AbsListView getAbsListView() {
        return null;
    }

    private void showSpreeView(final SpreeGiftInfo spree) {
        logo.setImageUrl(spree.getcLogo());
        title.setText(spree.getsAppName() + spree.getsArticleName());

        content.setText(spree.getContent());
        dead_time.setText(getSpannableString(context.getString(R.string.dead_line) + spree.getDeadline(), 0, context.getString(R.string.dead_line).length(), R.color.child_text_color));
        use_method.setText(getSpannableString(spree.getUseMethod(), 0, spree.getUseMethod().length() - 1 < 5 ? spree.getUseMethod().length() - 1 : 5, R.color.child_text_color));

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                    AccountUtil.userLogin(context);
                    return;
                }
                if (mKeyArray == null) {
                    mKeyArray = new ArrayList<>();
                }
                SpreeUtils.getSpreeAction((Activity) context, mKeyArray, spree, TAG);
            }
        });

        SpreeUtils.setExchangeBtn(context, remain, exchange, spree, true);

        if (spree.getiRemianNum() > 0) {
            if (TextUtils.isEmpty(spree.getcCdkey())) {
                String headerTxt = ResUtil.getString(R.string.remain_spree);
                String remianNum = headerTxt + spree.getiRemianNum();
                SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
                currencyBuilder.append(remianNum);
                currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                        headerTxt.length(), remianNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                remain.setText(currencyBuilder);
                remain.setVisibility(View.VISIBLE);
            }
        } else {
            String headerTxt = ResUtil.getString(R.string.tao_spree);
            String taoNum = headerTxt + spree.getiTao();
            SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
            currencyBuilder.append(taoNum);
            currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)),
                    headerTxt.length(), taoNum.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            remain.setText(currencyBuilder);
            remain.setVisibility(View.VISIBLE);
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

    @Subscribe
    public void spreeGetSuccess(SpreeGetSuccessEvent event) {
        String CdKey = event.getSpreeGiftInfo().getcCdkey();
        spree.setcCdkey(CdKey);
        showSpreeView(spree);
    }

    @Subscribe
    public void spreeGetSuccess(SpreeTaoSuccessEvent event) {
        spree.setiTao(event.getSpreeGiftInfo().getiTao());
        showSpreeView(spree);
    }
}
