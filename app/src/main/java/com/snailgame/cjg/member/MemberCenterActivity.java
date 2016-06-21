package com.snailgame.cjg.member;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.MemberChangeEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.member.adapter.MemberCenterGridAdapter;
import com.snailgame.cjg.member.model.MemberInfoModel;
import com.snailgame.cjg.member.model.MemberLayoutChildContentModel;
import com.snailgame.cjg.member.model.MemberLayoutChildModel;
import com.snailgame.cjg.member.model.MemberLayoutModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.TransparentStatusBarActivity;
import com.snailgame.cjg.store.GoodsListActivity;
import com.snailgame.cjg.store.GoodsListFragment;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.commonsware.cwac.merge.MergeAdapter;
import third.scrolltab.ScrollTabHolder;

/**
 * 会员中心
 * Created by lic on 2015/12/17.
 */
public class MemberCenterActivity extends TransparentStatusBarActivity implements ScrollTabHolder {
    static String TAG = MemberCenterActivity.class.getName();

    MergeAdapter mergeAdapter;
    @Bind(R.id.list_view)
    LoadMoreListView loadMoreListView;
    private int listViewHeaderHeight;
    private EmptyView mEmptyView;
    private MemberCenterGridAdapter memberCenterGridAdapter;
    private View headView;

    @Subscribe
    public void onMemberInfoChanged(MemberChangeEvent event) {
        initHeader(true);
        if (memberCenterGridAdapter != null)
            memberCenterGridAdapter.refresh();

    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MemberCenterActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_member_center;
    }

    @Override
    protected void initView() {
        mEmptyView = new EmptyView(this, loadMoreListView);
        mEmptyView.setErrorButtonClickListener(mErrorClickListener);
        setupToolbar(getString(R.string.member_center), getString(R.string.introduce));
        mergeAdapter = new MergeAdapter();
        loadMoreListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        loadMoreListView.enableLoadingMore(true);
        loadMoreListView.setScrollHolder(this);
    }


    @Override
    protected void loadData() {
        loadMemberJson(JsonUrl.getJsonUrl().JSON_URL_MEMBER_PAGER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @OnClick(R.id.tv_right_action)
    void onToolBarRightBtnClick() {
        if (!TextUtils.isEmpty(PersistentVar.getInstance().getSystemConfig().getMemberIntroduce()))
            startWebView(PersistentVar.getInstance().getSystemConfig().getMemberIntroduce());
    }

    @OnClick(R.id.tv_title)
    void onToolBarTitleBtnClick() {
        finish();

    }

    protected View.OnClickListener mErrorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            loadMemberJson(JsonUrl.getJsonUrl().JSON_URL_MEMBER_PAGER);
        }
    };

    /**
     * 初始化header
     *
     * @param isRefresh 是否会员信息变更刷新
     */
    private void initHeader(boolean isRefresh) {
        MemberInfoModel memberInfoModel = GlobalVar.getInstance().getMemberInfo();
        if (headView == null)
            headView = LayoutInflater.from(this).inflate(R.layout.activity_member_center_header, loadMoreListView, false);
        TextView tv_current_point = ButterKnife.findById(headView, R.id.tv_current_point);
        FSSimpleImageView currentLevelImg = ButterKnife.findById(headView, R.id.iv_current_level_icon);
        FSSimpleImageView nextLevelImg = ButterKnife.findById(headView, R.id.iv_next_level_icon);
        TextView tvLevel = ButterKnife.findById(headView, R.id.tv_level);
        TextView tvUpgradeMemberLevel = ButterKnife.findById(headView, R.id.tv_upgrade_member_level);
        LinearLayout linearLayout = ButterKnife.findById(headView, R.id.content_layout);
        FSSimpleImageView overlayImageView = ButterKnife.findById(headView, R.id.overlay_img);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        FrameLayout.LayoutParams imgLayoutParams = (FrameLayout.LayoutParams) overlayImageView.getLayoutParams();
        int headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.member_center_header_height);
        int statusBarHeight = ResUtil.getDimensionPixelOffset(R.dimen.status_bar_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layoutParams.height = headerHeight;
            imgLayoutParams.height = headerHeight;
            listViewHeaderHeight = headerHeight;
        } else {
            layoutParams.height = headerHeight - statusBarHeight;
            imgLayoutParams.height = headerHeight - statusBarHeight;
            listViewHeaderHeight = headerHeight - statusBarHeight;
        }
        linearLayout.setLayoutParams(layoutParams);
        overlayImageView.setLayoutParams(imgLayoutParams);

        if (memberInfoModel != null && memberInfoModel.getCurrentlevel() != null && memberInfoModel.getCurrentlevel().getiLevelId() > 0) {
            String currentLevelUrl = memberInfoModel.getCurrentlevel().getsIconLarger();
            if (currentLevelUrl != null && !TextUtils.isEmpty(currentLevelUrl))
                currentLevelImg.setImageUrlAndReUse(currentLevelUrl);

            //是否有下一等级的会员，当前是否是最顶级会员
            if (memberInfoModel.getNextMemberLevel() != null && memberInfoModel.getNextMemberLevel().getsLevelName() != null &&
                    !TextUtils.isEmpty(memberInfoModel.getNextMemberLevel().getsLevelName())) {
                tvLevel.setText(memberInfoModel.getNextMemberLevel().getsLevelName());
                int upgradePoint = 0;
                if (memberInfoModel.getCurrentPoint() != null && !TextUtils.isEmpty(memberInfoModel.getCurrentPoint()))
                    upgradePoint = memberInfoModel.getCurrentlevel().getiPointEnd() - Integer.valueOf(memberInfoModel.getCurrentPoint()) + 1;
                String upgradePointString = getString(R.string.upgrade_member_level, String.valueOf(upgradePoint));
                SpannableString spannableString = new SpannableString(upgradePointString);
                spannableString.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.indicator_yellow)), 2,
                        2 + String.valueOf(upgradePoint).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvUpgradeMemberLevel.setText(spannableString);
                String nextLevelUrl = memberInfoModel.getNextMemberLevel().getsIconLarger();
                if (nextLevelUrl != null && !TextUtils.isEmpty(nextLevelUrl))
                    nextLevelImg.setImageUrlAndReUse(nextLevelUrl);
            } else {
                tvLevel.setText(getString(R.string.stay_tuned));
                tvUpgradeMemberLevel.setText(getString(R.string.current_level_is_top));
                nextLevelImg.setPlaceHolderImageRes(R.drawable.ic_member);
            }

            overlayImageView.setVisibility(View.GONE);
            if (toolbarTitle != null)
                toolbarTitle.setTextColor(ResUtil.getColor(R.color.actionbar_title_color));
            if (toolbarRight != null)
                toolbarRight.setTextColor(ResUtil.getColor(R.color.actionbar_title_color));
        } else {
            overlayImageView.setVisibility(View.VISIBLE);
            overlayImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(PersistentVar.getInstance().getSystemConfig().getMemberIntroduce()))
                        startWebView(PersistentVar.getInstance().getSystemConfig().getMemberIntroduce());
                }
            });
            if (toolbarTitle != null)
                toolbarTitle.setTextColor(ResUtil.getColor(R.color.white));
            if (toolbarRight != null)
                toolbarRight.setTextColor(ResUtil.getColor(R.color.white));
        }
        if (memberInfoModel != null && memberInfoModel.getCurrentPoint() != null) {
            tv_current_point.setText(memberInfoModel.getCurrentPoint());
        }
        if (!isRefresh)
            mergeAdapter.addView(headView);
    }

    /**
     * 初始化GridView的快速入口
     *
     * @param memberLayoutChildModel
     */
    private void initGridView(@NonNull MemberLayoutChildModel memberLayoutChildModel) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_member_center_grid, loadMoreListView, false);
        FullGridView fullGridView = (FullGridView) view.findViewById(R.id.grid_view);
        memberCenterGridAdapter = new MemberCenterGridAdapter(this, memberLayoutChildModel.getChilds());
        fullGridView.setAdapter(memberCenterGridAdapter);
        mergeAdapter.addView(view);
        mergeAdapter.addView(getTemplateDivider());
    }

    /**
     * 初始化酷玩专购
     *
     * @param memberLayoutChildModel
     */
    private void initSpecBuyView(@NonNull MemberLayoutChildModel memberLayoutChildModel) {
        View view = LayoutInflater.from(this).inflate(R.layout.general_purpose_spec_buy_view, loadMoreListView, false);
        setModelTitleView(view, memberLayoutChildModel);
        setAdImage(view, memberLayoutChildModel.getChilds(), true);
        mergeAdapter.addView(view);
        mergeAdapter.addView(getTemplateDivider());
    }

    /**
     * 两个左右两张图的广告
     *
     * @param memberLayoutChildModel
     */
    private void initTwoAdView(@NonNull MemberLayoutChildModel memberLayoutChildModel) {
        if (memberLayoutChildModel.getChilds() != null && memberLayoutChildModel.getChilds().size() > 1) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_member_two_ad_view, loadMoreListView, false);
            FSSimpleImageView imagLeft = ButterKnife.findById(view, R.id.image_left);
            FSSimpleImageView imageRight = ButterKnife.findById(view, R.id.image_right);
            setImageClick(imagLeft, memberLayoutChildModel.getChilds().get(0).getsImageUrl(), memberLayoutChildModel.getChilds().get(0).getsJumpUrl());
            setImageClick(imageRight, memberLayoutChildModel.getChilds().get(1).getsImageUrl(), memberLayoutChildModel.getChilds().get(1).getsJumpUrl());
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 初始化优惠折扣
     *
     * @param memberLayoutChildModel
     */
    private void initPanicBuyView(@NonNull MemberLayoutChildModel memberLayoutChildModel) {
        View view = LayoutInflater.from(this).inflate(R.layout.general_purpose_panic_buy_view, loadMoreListView, false);
        setModelTitleView(view, memberLayoutChildModel);
        setAdImage(view, memberLayoutChildModel.getChilds(), false);
        mergeAdapter.addView(view);
    }


    /**
     * 模板与模板之间带阴影的分割线
     *
     * @return
     */
    private View getTemplateDivider() {
        View view = LayoutInflater.from(this).inflate(R.layout.home_modul_divider, loadMoreListView, false);
        return view;
    }

    /**
     * 设置模块标题
     *
     * @param view
     * @param memberLayoutChildModel
     */
    private void setModelTitleView(View view, @NonNull final MemberLayoutChildModel memberLayoutChildModel) {
        TextView titleTextView = ButterKnife.findById(view, R.id.title);
        View titleView = ButterKnife.findById(view, R.id.title_view);
        TextView seeMoreView = ButterKnife.findById(view, R.id.see_more);
        FSSimpleImageView FSSimpleImageView = ButterKnife.findById(view, R.id.image_arrow);
        if (memberLayoutChildModel.getsTitle() != null)
            titleTextView.setText(Html.fromHtml(memberLayoutChildModel.getsTitle()));
        if (memberLayoutChildModel.getsPinText() != null)
            seeMoreView.setText(memberLayoutChildModel.getsPinText());
        if (memberLayoutChildModel.getsPinIcon() != null)
            FSSimpleImageView.setImageUrlAndReUse(memberLayoutChildModel.getsPinIcon());
        final String cSource = memberLayoutChildModel.getcSource();
        if (cSource != null && !TextUtils.isEmpty(cSource)) {
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cSource.equals(MemberLayoutChildModel.JUMP_TYPE_COOL_PLAY)) {
                        startActivity(GoodsListActivity.newIntent(MemberCenterActivity.this,
                                Html.fromHtml(memberLayoutChildModel.getsTitle()).toString(), GoodsListFragment.TYPE_GOODS_SPECIAL));

                    } else if (cSource.equals(MemberLayoutChildModel.JUMP_TYPE_DISCOUNT)) {
                        startActivity(GoodsListActivity.newIntent(MemberCenterActivity.this,
                                Html.fromHtml(memberLayoutChildModel.getsTitle()).toString(), GoodsListFragment.TYPE_GOODS_DISCOUNT));

                    }
                }
            });
        }
    }

    /**
     * 设置广告位图片和点击事件
     *
     * @param view
     * @param lists         广告内容的list
     * @param isContainFour 是否包含第四个广告位
     */
    private void setAdImage(View view, final ArrayList<MemberLayoutChildContentModel> lists, boolean isContainFour) {
        FSSimpleImageView imageVertical = ButterKnife.findById(view, R.id.image_vertical);
        FSSimpleImageView imageHorizonal1 = ButterKnife.findById(view, R.id.image_horizonal1);
        FSSimpleImageView imageHorizonal2 = ButterKnife.findById(view, R.id.image_horizonal2);
        FSSimpleImageView imageHorizonal3 = null;
        if (isContainFour) {
            imageHorizonal3 = ButterKnife.findById(view, R.id.image_horizonal3);
        }
        for (int i = 0; i < lists.size(); i++) {
            if (i == 0) {
                setImageClick(imageVertical, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 1) {
                setImageClick(imageHorizonal1, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 2) {
                setImageClick(imageHorizonal2, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else {
                if (isContainFour) {
                    setImageClick(imageHorizonal3, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
                }
            }
        }
    }

    /**
     * 设置图片点击
     *
     * @param image   显示的imageView
     * @param url     图片显示url
     * @param jumpUrl 点击跳转url
     */
    private void setImageClick(FSSimpleImageView image, String url, final String jumpUrl) {
        image.setImageUrlAndReUse(url);
        if (jumpUrl != null && !TextUtils.isEmpty(jumpUrl)) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jumpUrl != null && !TextUtils.isEmpty(jumpUrl))
                        startWebView(jumpUrl);
                }
            });
        }
    }

    /**
     * 跳转网页
     */
    private void startWebView(String url) {
        startActivity(WebViewActivity.newIntent(MemberCenterActivity.this, url));
    }


    @Override
    public void adjustScroll(int scrollHeight) {

    }

    /**
     * 重写onScroll控制actionbar和statusbar的透明度
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @param pagePosition
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        setAlpha(loadMoreListView.getComputedScrollY());
    }

    //设置toolbar和状态栏透明度
    public void setAlpha(int mScrollY) {
        int minScrollHeight = listViewHeaderHeight - ResUtil.getDimensionPixelOffset(R.dimen.actionbar_height) - ComUtil.getStatesBarHeight();
        int showScrollY = Math.min(mScrollY, minScrollHeight);
        setToolbarStatusBarAlpha(showScrollY * 255 / minScrollHeight);
    }

    /**
     * 展现界面
     */
    private void showUI(final List<MemberLayoutChildModel> moduleModelList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initHeader(false);
                String templateId;
                for (MemberLayoutChildModel memberLayoutChildModel : moduleModelList) {
                    if (memberLayoutChildModel != null && memberLayoutChildModel.getChilds() != null
                            && memberLayoutChildModel.getcTemplateId() != null) {
                        templateId = memberLayoutChildModel.getcTemplateId();
                        //类型:8-酷玩专购;9-优惠折扣；10-会员特权;11-两栏推荐位
                        switch (templateId) {
                            case MemberLayoutChildModel.TYPE_COOL_PLAY:
                                initSpecBuyView(memberLayoutChildModel);
                                break;
                            case MemberLayoutChildModel.TYPE_DISCOUNT:
                                initPanicBuyView(memberLayoutChildModel);
                                break;
                            case MemberLayoutChildModel.TYPE_PRIVILEGE:
                                initGridView(memberLayoutChildModel);
                                break;
                            case MemberLayoutChildModel.TYPE_TWO_AD:
                                initTwoAdView(memberLayoutChildModel);
                                break;
                        }
                    }
                }
                loadMoreListView.onNoMoreData();
                loadMoreListView.setAdapter(mergeAdapter);
            }
        });
    }


    /**
     * 解析返回数据
     *
     * @param resultStr resultStr
     */
    private void handleResult(String resultStr) {
        MemberLayoutModel memberLayoutModel = ExtendJsonUtil.parseHomeJsonToModel(resultStr, MemberLayoutModel.class);
        if (memberLayoutModel != null && memberLayoutModel.getList() != null) {
            List<MemberLayoutChildModel> moduleModelList = memberLayoutModel.getList();
            showUI(moduleModelList);
        }
    }

    /**
     * 获取会员信息和快速入口信息
     *
     * @param url
     */
    private void loadMemberJson(String url) {
        showLoading();
        FSRequestHelper.newGetRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    handleResult(result);
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, true);
    }

    protected void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.setEmptyMessage(getString(R.string.no_data_now));
            mEmptyView.showEmpty();
            loadMoreListView.goneFooter();
        }
    }

    protected void showLoading() {
        if (mEmptyView != null) {
            mEmptyView.showLoading();
        }
    }

    protected void showError() {
        if (mEmptyView != null) {
            mEmptyView.showError();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_PERSONAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_PERSONAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }


}