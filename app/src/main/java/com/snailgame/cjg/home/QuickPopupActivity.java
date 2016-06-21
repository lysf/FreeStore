package com.snailgame.cjg.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.seekgame.collection.CollectionListFragment;
import com.snailgame.cjg.seekgame.recommend.RecommendFragment;
import com.snailgame.cjg.spree.SpreeTabFragment;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.umeng.analytics.MobclickAgent;

import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 首页快速弹出界面
 * Created by sunxy on 2015/3/17.
 */
public class QuickPopupActivity extends SwipeBackActivity {
    public static final String KEY_JUMP = "key_jump";
    public static final int TYPE_SPREE = 0, TYPE_BBS = 1, TYPE_APP_RECOMMEND = 2, TYPE_GAME_RECOMMEND = 3, TYPE_GAME_COLLECTION = 4;
    private int actionbarTitles[] = {R.string.game_spree, R.string.forum_title, R.string.app_recommend, R.string.game_recommend, R.string.game_collection};

    public static Intent newIntent(Context context, int jumpType, int[] route) {
        Intent intent = new Intent(context, QuickPopupActivity.class);
        intent.putExtra(QuickPopupActivity.KEY_JUMP, jumpType);
        intent.putExtra(AppConstants.KEY_ROUTE, route);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_QUICK_POPUP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_QUICK_POPUP);
    }

    @Override
    protected void handleIntent() {
        Intent intent = getIntent();
        int type = intent.getIntExtra(KEY_JUMP, -1);
        int[]route=intent.getIntArrayExtra(AppConstants.KEY_ROUTE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ActionBarUtils.makeGameManageActionbar(this, getSupportActionBar(), actionbarTitles[type]);

        switch (type) {
            case TYPE_SPREE:
                int[]newRoute=route.clone();
                newRoute[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_SHORTCUT;
                transaction.add(R.id.quick_popup_fragment_container, SpreeTabFragment.getInstance(newRoute))
                        .commitAllowingStateLoss();
                break;
            case TYPE_BBS:
                transaction.add(R.id.quick_popup_fragment_container, ComUtil.getBBSFragment())
                        .commitAllowingStateLoss();
                break;
            case TYPE_APP_RECOMMEND:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_RECOMMEND;
                transaction.add(R.id.quick_popup_fragment_container, RecommendFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_APP_RECOMMEND, true, route))
                        .commitAllowingStateLoss();
                break;
            case TYPE_GAME_RECOMMEND:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_RECOMMEND;
                transaction.add(R.id.quick_popup_fragment_container, RecommendFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_GAME_RECOMMEND, true, route))
                        .commitAllowingStateLoss();
                break;
            case TYPE_GAME_COLLECTION:
                route[AppConstants.STATISTCS_DEPTH_TWO] = AppConstants.STATISTCS_SECOND_THEME;
                transaction.add(R.id.quick_popup_fragment_container, CollectionListFragment.getInstance(JsonUrl.getJsonUrl().JSON_URL_GAME_THEME, true, route))
                        .commitAllowingStateLoss();
                break;

        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_quick_popup;
    }


}