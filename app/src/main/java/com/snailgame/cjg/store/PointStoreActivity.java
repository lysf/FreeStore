package com.snailgame.cjg.store;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.event.StorePointSelectedEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.store.adapter.PointStoreFragmentAdapter;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 积分商城
 * Created by TAJ_C on 2015/12/2.
 */
public class PointStoreActivity extends SwipeBackActivity {

    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;


    private PointStoreFragmentAdapter mAdapter;

    private static final int PAGE_NUM = 2;

    int selectedPosition;
    ImageView selectedView;

    public static Intent newIntent(Context context) {
        return new Intent(context, PointStoreActivity.class);
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
        return R.layout.home_content_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStoreActionbar(getString(R.string.store_point_store_title));

        mAdapter = new PointStoreFragmentAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        tabStrip.setViewPager(mViewPager, PAGE_NUM, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
                switch (position) {
                    case PointStoreFragmentAdapter.TAB_HOT:
                        selectedView.setVisibility(View.INVISIBLE);
                        break;
                    case PointStoreFragmentAdapter.TAB_ALL:
                        selectedView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

    }


    public void makeStoreActionbar(String title) {
        View actionBarView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.actionbar_common, null);
        actionBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        actionBarView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        TextView tvTitle = ButterKnife.findById(actionBarView, R.id.tv_title);
        tvTitle.setText(title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        selectedView = ButterKnife.findById(actionBarView, R.id.iv_ab_search);
        selectedView.setVisibility(View.INVISIBLE);
        selectedView.setImageResource(R.drawable.ic_store_point_selector);
        selectedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStorePointSelectorDialog();
            }
        });

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarView);
    }


    /**
     * 弹出积分区间范围选择框
     */
    public void showStorePointSelectorDialog() {
        final List<TextView> selectorViews = new ArrayList<>();

        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.store_dialog_point_selecter);

        TextView selector1View = ButterKnife.findById(dialog, R.id.tv_point_selector_1);
        TextView selector2View = ButterKnife.findById(dialog, R.id.tv_point_selector_2);
        TextView selector3View = ButterKnife.findById(dialog, R.id.tv_point_selector_3);
        TextView selector4View = ButterKnife.findById(dialog, R.id.tv_point_selector_4);

        selectorViews.add(selector1View);
        selectorViews.add(selector2View);
        selectorViews.add(selector3View);
        selectorViews.add(selector4View);

        for (int i = 0; i < selectorViews.size(); i++) {
            final TextView view = selectorViews.get(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (TextView itemView : selectorViews) {
                        itemView.setCompoundDrawables(getDrawableLeft(PointStoreActivity.this, R.drawable.ic_store_point_unselected), null, null, null);
                    }

                    selectedPosition = (int) v.getTag();
                    ((TextView) v).setCompoundDrawables(getDrawableLeft(PointStoreActivity.this, R.drawable.ic_store_point_selected), null, null, null);
                }
            });

        }

        Button comfirmBtn = ButterKnife.findById(dialog, R.id.btn_ok);
        comfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainThreadBus.getInstance().post(new StorePointSelectedEvent(selectedPosition));

            }
        });

        ButterKnife.findById(dialog, R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static Drawable getDrawableLeft(Context context, int resourceId) {
        Drawable drawable = ResUtil.getDrawable(resourceId);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_STORE_POINT_SOTRE);

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_STORE_POINT_SOTRE);
    }
}
