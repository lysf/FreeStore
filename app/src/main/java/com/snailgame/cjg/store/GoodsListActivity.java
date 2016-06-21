package com.snailgame.cjg.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by TAJ_C on 2015/11/27.
 */
public class GoodsListActivity extends SwipeBackActivity {


    public static Intent newIntent(Context context, String title, String gcId) {
        Intent intent = new Intent(context, GoodsListActivity.class);
        intent.putExtra(AppConstants.GOODS_LIST_TITLE, title);
        intent.putExtra(AppConstants.GOODS_LIST_ID, gcId);
        return intent;
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
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String gcId = getIntent().getStringExtra(AppConstants.GOODS_LIST_ID);

        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getIntent().getStringExtra(AppConstants.GOODS_LIST_TITLE));
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fragmentContainer, GoodsListFragment.getInstance(gcId));
        ft.commitAllowingStateLoss();

    }
}
