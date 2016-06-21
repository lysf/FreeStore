package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.personal.model.MyVoucherModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人中心 -> 我的代金券 -> 游戏代金券详情(合作游戏) fragment
 * Created by pancl on 2015/4/30.
 */
public class VoucherGameFragment extends FixedSupportV4BugFragment {

    private MyVoucherModel.ModelItem item;
    private boolean onceUse;    // 是否一次性可用

    @Bind(R.id.voucher_cooper_fragment_header)
    RelativeLayout voucher_cooper_fragment_header;
    @Bind(R.id.tv_voucher_name)
    TextView mVoucherName;
    @Bind(R.id.tv_voucher_desc)
    TextView mVoucherDesc;
    @Bind(R.id.tv_voucher_amount)
    TextView mVoucherAmount;
    @Bind(R.id.tv_voucher_validity)
    TextView mVoucherValidity;
    @Bind(R.id.tv_voucher_total)
    TextView mVoucherTotal;
    @Bind(R.id.tv_voucher_used)
    TextView mVoucherUsed;
    @Bind(R.id.tv_voucher_avilable)
    TextView mVoucherAvilable;
    @Bind(R.id.voucher_cooper_use_tip)
    TextView mVoucherCooperUseTip;
    @Bind(R.id.rl_voucher_amount)
    RelativeLayout mVoucherAmountlayout;

    /**
     * @param item 游戏代金券项
     * @return
     */
    public static VoucherGameFragment newIntent(MyVoucherModel.ModelItem item, boolean onceUse) {
        VoucherGameFragment fragment = new VoucherGameFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_VOUCHER, item);
        bundle.putBoolean(AppConstants.KEY_VOUCHER_ONCEUSR, onceUse);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            item = bundle.getParcelable(AppConstants.KEY_VOUCHER);
            onceUse = bundle.getBoolean(AppConstants.KEY_VOUCHER_ONCEUSR, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voucher_cooper_fragment, null);
        ButterKnife.bind(this, view);
        loadData();
        return view;
    }

    private void loadData() {
        if (item != null) {
            String remain = String.valueOf(item.getiAmount() - item.getiBalance());
            String yuan = getString(R.string.yuan_char) + " ";
            mVoucherName.setText(item.getsVoucherName());
            mVoucherDesc.setText(item.getsDesc());
            mVoucherAmount.setText(String.valueOf(item.getiBalance()));
            mVoucherValidity.setText(getString(R.string.my_voucher_deadline, item.getFormatteddEnd()));
            if (onceUse) {
                voucher_cooper_fragment_header.setBackgroundResource(R.drawable.voucher_cooper_goods_header_bg);
                mVoucherAmountlayout.setVisibility(View.GONE);
            } else {
                voucher_cooper_fragment_header.setBackgroundResource(R.drawable.voucher_cooper_game_header_bg);
                mVoucherAmountlayout.setVisibility(View.VISIBLE);
                mVoucherTotal.setText(yuan + String.valueOf(item.getiAmount()));
                mVoucherUsed.setText(yuan + remain);
                mVoucherAvilable.setText(yuan + String.valueOf(item.getiBalance()));
            }
            mVoucherCooperUseTip.setText(Html.fromHtml(item.getsUsage()));
        }
    }
}
