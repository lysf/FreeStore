package com.snailgame.cjg.personal;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.event.VoucherUpdateEvent;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by TAJ_C on 2015/6/24.
 */
public class MyWalletActivity extends SwipeBackActivity implements View.OnClickListener {
    @Bind(R.id.content_container)
    LinearLayout viewContainer;

    private static final int MY_SCORE = 2, MY_TUTU = 0, MY_VOUCHER = 1;

    private TextView vocherView;
    private TextView currencyView;
    private TextView pointView;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MyWalletActivity.class);
        return intent;
    }


    @Subscribe
    public void onUserInfoChange(UserInfoChangeEvent event) {
        if (LoginSDKUtil.isLogined(this)) {
            if (currencyView != null) {
                currencyView.setText(String.valueOf(GlobalVar.getInstance().getUsrInfo().getiMoney()));
            }
            if (pointView != null) {
                pointView.setText(String.valueOf(GlobalVar.getInstance().getUsrInfo().getiIntegral()));
            }
        }
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_my_wallet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.persional_my_wallet);

        String[] itemTitles = ResUtil.getStringArray(R.array.persional_mywallet_array);
        int[] itemIcons = new int[]{ R.drawable.person_tutu,
                R.drawable.person_voucher,R.drawable.person_score};


        for (int i = 0; i < itemTitles.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.personal_center_item_layout, null);
            itemView.setTag(i);
            itemView.setOnClickListener(this);

            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            itemViewHolder.myFunction.setText(itemTitles[i]);
            ComUtil.setDrawableLeft(itemViewHolder.myFunction, itemIcons[i]);
            itemViewHolder.personal_divider.setVisibility(i == (itemTitles.length - 1) ? View.GONE : View.VISIBLE);
            itemViewHolder.numberView.setVisibility(View.VISIBLE);
            if (GlobalVar.getInstance().getUsrInfo() != null) {
                switch (i) {
                    case MY_TUTU: // 兔兔币
                        currencyView = itemViewHolder.numberView;
                        itemViewHolder.numberView.setText(String.valueOf(GlobalVar.getInstance().getUsrInfo().getiMoney()));
                        break;
                    case MY_VOUCHER: // 我的代金卷
                        vocherView = itemViewHolder.numberView;
                        itemViewHolder.numberView.setText(String.valueOf(SharedPreferencesUtil.getInstance().getVoucherNum()));
                        break;
                    case MY_SCORE:
                        pointView = itemViewHolder.numberView;
                        itemViewHolder.numberView.setText(String.valueOf(GlobalVar.getInstance().getUsrInfo().getiIntegral()));
                        break;
                    default:
                        break;
                }
            }

            viewContainer.addView(itemView);
        }

        MainThreadBus.getInstance().register(this);
    }


    @Override
    public void onClick(View v) {
        int value = (int) v.getTag();
        switch (value) {
            case MY_TUTU: // 兔兔币
                if (LoginSDKUtil.isLogined(this)) {
                    startActivity(CurrencyHistoryActivity.newIntent(this));
                } else {
                    AccountUtil.userLogin(this);
                }

                break;
            case MY_VOUCHER: // 我的代金卷
                if (LoginSDKUtil.isLogined(this)) {
                    startActivity(MyVoucherActivity.newIntent(this));
                } else {
                    AccountUtil.userLogin(this);
                }
                break;
            case MY_SCORE:
                if (LoginSDKUtil.isLogined(this)) {
                    startActivity(ScoreHistoryActivity.newIntent(this));
                } else {
                    AccountUtil.userLogin(this);
                }

                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onVoucherUpdate(VoucherUpdateEvent event) {
        if (vocherView != null)
            vocherView.setText(String.valueOf(SharedPreferencesUtil.getInstance().getVoucherNum()));
    }

    protected class ItemViewHolder {
        @Bind(R.id.my_function)
        TextView myFunction;
        @Bind(R.id.personal_divider)
        View personal_divider;

        @Bind(R.id.tv_prompt)
        TextView numberView;

        public ItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
