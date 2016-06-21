package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.MagicTextView;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import third.scrolltab.ScrollTabHolder;

/**
 * Created by TAJ_C on 2015/11/6.
 */
public class BaseHistoryAcitvity extends SwipeBackActivity implements ScrollTabHolder {

    @Bind(R.id.header_title)
    TextView headerTitleView;

    @Bind(R.id.tv_total_num)
    MagicTextView numTextView;

    @Bind(R.id.tv_jump_get)
    TextView gainView;

    @Bind(R.id.header_view)
    View mHeaderView;


    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;

    @Bind(R.id.view_pager)
    ViewPager viewPager;


    @Bind(R.id.introduce_container)
    View introduceContainer;

    @Bind(R.id.tv_introduce)
    TextView introduceView;
    protected static final int TAB_NUM = 2;

    private int mMinHeaderHeight; // 滑动中最小显示的高度

    private int mHeaderHeight;  // 充值部分头部的高度，包括banner的高度以及tab的高度

    private int mMinHeaderTranslation;   // header最多可以移动多高。


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
        return R.layout.activity_score_currency_history;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LoginSDKUtil.isLogined(this)) {
            AccountUtil.userLogin(this);
            finish();
        } else {
            initData();
        }
    }

    private void initData() {
        mMinHeaderHeight = ResUtil.getDimensionPixelSize(R.dimen.tab_height);
        mHeaderHeight = ResUtil.getDimensionPixelSize(R.dimen.personal_score_history_header_height);
        mMinHeaderTranslation = mMinHeaderHeight - mHeaderHeight;
    }


    @Override
    public void adjustScroll(int scrollHeight) {
        // nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount, int pagePosition) {
        if (viewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            ViewHelper.setTranslationY(mHeaderView,
                    Math.max(-scrollY, mMinHeaderTranslation));
        }
    }

    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }
        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }


}