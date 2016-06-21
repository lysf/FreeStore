package com.snailgame.cjg.news;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.cjg.common.db.daoHelper.NewsChannelDaoHelper;
import com.snailgame.cjg.common.db.daoHelper.NewsReadDaoHelper;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.server.SnailFreeStoreService;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.event.NewsChannelGetEvent;
import com.snailgame.cjg.news.util.NewsChannelListSortUtil;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 资讯
 * Created by TAJ_C on 2016/4/7.
 */
public class NewsActivity extends SwipeBackActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    @Bind(R.id.iv_channel)
    ImageView iv_channel;
    @Bind(R.id.my_category_text)
    TextView my_category_text;
    @Bind(R.id.ll_channel_container)
    LinearLayout ll_channel_container;

    private NewsFragmentPagerAdapter mAdapter;

    private static final float TAB_NUM = 5.5f;

    private ChannelFragment channelFragment;
    Animation animation_push_out;
    Animation animation_push_in;
    Animation animation_fade_in;
    Animation animation_fade_out;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NewsActivity.class);
        return intent;
    }

    @Override
    protected void handleIntent() {
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void initView() {
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.news_actionbar_title);
        channelFragment = new ChannelFragment();
//        mAdapter = new NewsFragmentPagerAdapter(NewsActivity.this, getSupportFragmentManager(), new ArrayList<NewsChannel>());
//        mViewPager.setAdapter(mAdapter);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_channel_container, channelFragment).commitAllowingStateLoss();
    }

    @Override
    protected void loadData() {
        List<NewsChannel> newsChannelList = NewsChannelDaoHelper.getInstance(this).queryShowChannel();
        if (ListUtils.isEmpty(newsChannelList)) {
            startService(SnailFreeStoreService.newIntent(this, SnailFreeStoreService.TYPE_GET_NEWS_CHANNEL));
        } else {
            setupView(newsChannelList);
        }
    }

    private void setupView(List<NewsChannel> newsChannelList) {
        if (newsChannelList == null) {
            return;
        }
        mViewPager.setAdapter(null);
        HashMap<String, Fragment.SavedState> fragmentStateMap = null;
        if(mAdapter != null){
            fragmentStateMap = mAdapter.getFragmentSavedStateMap();
        }
         mAdapter = new NewsFragmentPagerAdapter(NewsActivity.this, getSupportFragmentManager(), NewsChannelListSortUtil.getShowChannelList(newsChannelList));
        if(fragmentStateMap != null){
            mAdapter.setFragmentSavedStateMap(fragmentStateMap);
        }
        mViewPager.setAdapter(mAdapter);

        tabStrip.setViewPager(mViewPager, TAB_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {

            }
        });
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_news;
    }

    String currentTitle;

    @OnClick(R.id.iv_channel)
    void showChannelFragment() {
        if (iv_channel.isSelected()) {//显示
            hideChannel();
            iv_channel.setSelected(false);
        } else {
            currentTitle = (String) mAdapter.getPageTitle(mViewPager.getCurrentItem());
            showChannel();
            iv_channel.setSelected(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
        NewsReadDaoHelper.getInstance(this).deleteExtraMsg();
    }


    @Subscribe
    public void refreshChannelView(NewsChannelGetEvent event) {
        List<NewsChannel> newsChannelList = event.getNewsChannelList();
        setupView(newsChannelList);
    }

    //初始化Animation
    private void initAnimation() {
        if (animation_push_in == null) {
            animation_push_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.channel_push_in);
        }
        if (animation_push_out == null) {
            animation_push_out = AnimationUtils.loadAnimation(getBaseContext(), R.anim.channel_push_out);
        }
        if (animation_fade_in == null) {
            animation_fade_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.channel_fade_in);
        }
        if (animation_fade_out == null) {
            animation_fade_out = AnimationUtils.loadAnimation(getBaseContext(), R.anim.channel_fade_out);
        }
    }

    //显示频道操作
    private void showChannel() {
        initAnimation();
        if (channelFragment != null) {
            channelFragment.setCurrentChannelItem(currentTitle);
        }
        animation_push_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_channel_container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_channel_container.setVisibility(View.VISIBLE);
                my_category_text.setVisibility(View.VISIBLE);
                tabStrip.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ll_channel_container.startAnimation(animation_push_in);
        my_category_text.startAnimation(animation_fade_in);
        tabStrip.startAnimation(animation_fade_out);
    }

    //隐藏频道管理
    private void hideChannel() {
        initAnimation();
        animation_push_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_channel_container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_channel_container.setVisibility(View.GONE);
                my_category_text.setVisibility(View.INVISIBLE);
                tabStrip.setVisibility(View.VISIBLE);
                if (channelFragment != null && channelFragment.isUpdateChannel()) {//频道是否改变
                    if (ListUtils.isEmpty(channelFragment.userChannelList)) {
                        startService(SnailFreeStoreService.newIntent(getBaseContext(), SnailFreeStoreService.TYPE_GET_NEWS_CHANNEL));
                    } else {
                        setupView(channelFragment.userChannelList);
                    }
                    mViewPager.setCurrentItem(mAdapter.getTargetTitlePosition(currentTitle));
                    channelFragment.reSetChange();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        ll_channel_container.startAnimation(animation_push_out);
        my_category_text.startAnimation(animation_fade_out);
        tabStrip.startAnimation(animation_fade_in);
    }

    @Override
    public void onBackPressed() {
        if (iv_channel.isSelected()) {//显示
            hideChannel();
            iv_channel.setSelected(false);
        } else {
            super.onBackPressed();
        }
    }
}
