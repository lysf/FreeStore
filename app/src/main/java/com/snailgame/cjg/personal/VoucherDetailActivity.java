package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.personal.adapter.VoucherFragmentAdapter;
import com.snailgame.cjg.personal.model.MyVoucherModel;
import com.snailgame.cjg.util.ActionBarUtils;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 个人中心 -> 我的、蜗牛代金券详情
 * Created by pancl on 2015/4/20.
 */
public class VoucherDetailActivity extends SwipeBackActivity {

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
        return R.layout.voucher_detail_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyVoucherModel.ModelItem item;
        int voucherType;
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        } else {
            item = intent.getParcelableExtra(AppConstants.KEY_VOUCHER);
            voucherType = intent.getIntExtra(AppConstants.KEY_VOUCHER_TYPE, VoucherFragmentAdapter.TAB_GAME);
        }

        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getString(R.string.voucher_detail_title));

        Fragment fragment = null;
        if (voucherType == VoucherFragmentAdapter.TAB_GAME) {
            fragment = VoucherGameDetailFragment.create(item.getiVoucherId());
        } else if (voucherType == VoucherFragmentAdapter.TAB_WN) {
            fragment = VoucherWNDetailFragment.create(item.getiVoucherId());
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.voucher_detail_content, fragment);
        ft.commitAllowingStateLoss();
    }

    public static Intent newIntent(Context context, MyVoucherModel.ModelItem item, int voucherType) {
        Intent intent = new Intent(context, VoucherDetailActivity.class);
        intent.putExtra(AppConstants.KEY_VOUCHER, item);
        intent.putExtra(AppConstants.KEY_VOUCHER_TYPE, voucherType);
        return intent;
    }
}
