package com.snailgame.cjg.spree.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.SpreeDetailDialog;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 礼包 view holder
 * Created by TAJ_C on 2015/5/4.
 */
public class SpreeGiftItemHolder {

    @Bind(R.id.btn_spree_get)
    TextView spreeGetBtn;

    @Bind(R.id.tv_spree_title)
    TextView titleView;

    @Bind(R.id.tv_spree_content)
    TextView contentView;

    @Bind(R.id.tv_cd_key)
    TextView cdKeyView;

    @Bind(R.id.toggle_button)
    View toggleBtn;

    View containerView;
    Context mContext;

    HotLocalSpreeAdapter.SpreeInterfaceListener listener;

    public SpreeGiftItemHolder(Context context, View view, HotLocalSpreeAdapter.SpreeInterfaceListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.containerView = view;
        ButterKnife.bind(this, view);
    }

    public void inflatData(final Activity mActivity, final SpreeGiftInfo spreeGiftInfo) {
        if (spreeGiftInfo != null) {
            titleView.setText(ResUtil.getString(R.string.spree_title_frame, spreeGiftInfo.getsArticleName()));
            contentView.setText(String.valueOf(spreeGiftInfo.getContent()));
            toggleBtn.setVisibility(View.GONE);
            spreeGetBtn.setOnClickListener(new View.OnClickListener() {
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

            SpreeUtils.setExchangeBtn(mActivity, cdKeyView, spreeGetBtn, spreeGiftInfo, false);
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SpreeDetailDialog(mActivity, spreeGiftInfo).show();
                }
            });

        }

    }


}
