package com.snailgame.cjg.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.snailgame.cjg.R;
import com.snailgame.cjg.util.ActionBarUtils;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MySpreeActivity extends SwipeBackActivity {
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
		return R.layout.my_spree_activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.persion_gift_exchange);
		MySpreeFragment mySpreeFragment = new MySpreeFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.my_spree_content, mySpreeFragment);
		ft.commitAllowingStateLoss();
	}

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, MySpreeActivity.class);
        return intent;
    }
}
