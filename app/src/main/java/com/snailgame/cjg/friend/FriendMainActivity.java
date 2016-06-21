package com.snailgame.cjg.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.event.FriendScreenChangeEvent;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.squareup.otto.Subscribe;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 朋友主界面
 * Created by TAJ_C on 2016/5/9.
 */
public class FriendMainActivity extends SwipeBackActivity implements FriendListFragment.EmptyListener {

    public static final String TAG = FriendMainActivity.class.getSimpleName();

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, FriendMainActivity.class);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getString(R.string.friend_title), R.drawable.ic_friend_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FriendAddActivity.newIntent(FriendMainActivity.this));
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FriendListFragment.getInstance()).commitAllowingStateLoss();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_friend_main;
    }

    @Override
    public void showRecommendView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FriendRecommendFragment.getInstance()).commitAllowingStateLoss();
    }

    @Subscribe
    public void showFriendListView(FriendScreenChangeEvent event) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FriendListFragment.getInstance()).commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }
}
