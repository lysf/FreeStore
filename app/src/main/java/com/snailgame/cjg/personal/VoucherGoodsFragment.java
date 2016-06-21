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
import com.snailgame.cjg.personal.model.MyVoucherGoodsModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人中心 -> 我的代金券 -> 商品代金券详情(合作游戏) fragment
 * Created by pancl on 2015/4/30.
 */
public class VoucherGoodsFragment extends FixedSupportV4BugFragment {

    private MyVoucherGoodsModel.ModelItem voucherGoods;

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
    @Bind(R.id.rl_voucher_amount)
    RelativeLayout rlVoucherAmount;
    @Bind(R.id.voucher_cooper_use_tip)
    TextView mVoucherCooperUseTip;

    /**
     * @param item 商品代金券项
     * @return
     */
    public static VoucherGoodsFragment create(MyVoucherGoodsModel.ModelItem item) {
        VoucherGoodsFragment fragment = new VoucherGoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_VOUCHER, item);
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
            voucherGoods = bundle.getParcelable(AppConstants.KEY_VOUCHER);
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
        if (voucherGoods != null) {
            voucher_cooper_fragment_header.setBackgroundResource(R.drawable.voucher_cooper_goods_header_bg);
            mVoucherName.setText(voucherGoods.getsVoucherName());
            mVoucherDesc.setVisibility(View.GONE);
            mVoucherAmount.setText(String.valueOf(voucherGoods.getiAmount()));
            mVoucherValidity.setText(getString(R.string.my_voucher_deadline, voucherGoods.getFormatteddEnd()));
            mVoucherCooperUseTip.setText(Html.fromHtml(voucherGoods.getsDesc()));
            rlVoucherAmount.setVisibility(View.GONE);
        }
    }
}
