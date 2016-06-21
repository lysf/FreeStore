package com.snailgame.cjg.member;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.member.adapter.MemberBirthdayGiftAdapter;
import com.snailgame.cjg.member.adapter.MemberDiscountsAdapter;
import com.snailgame.cjg.member.adapter.MemberExclusiveSpreeAdapter;
import com.snailgame.cjg.member.adapter.MemberGameVoucherAdapter;
import com.snailgame.cjg.member.adapter.MemberHeadFrameAdapter;
import com.snailgame.cjg.member.adapter.MemberLevelSpreeAdapter;
import com.snailgame.cjg.member.adapter.MemberQualityServiceAdapter;
import com.snailgame.cjg.member.model.MemberPrivilege;
import com.snailgame.cjg.member.model.MemberSpreeResultModel;
import com.snailgame.cjg.member.widget.RLScrollView;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.GoodsGetListener;
import com.snailgame.cjg.personal.widget.FullListView;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 会员详情
 * Created by TAJ_C on 2015/12/10.
 */
public class MemberDetailActivity extends SwipeBackActivity {

    private static final String TAG = MemberDetailActivity.class.getSimpleName();
    @Bind(R.id.detail_actionbar_view)
    View actionbarView;

    @Bind(R.id.tv_detail_title)
    TextView actionbarTitleView;

    @Bind(R.id.content_container)
    RLScrollView contentContainer;


    @Bind(R.id.item_container)
    LinearLayout itemContainer;

    @Bind(R.id.lv_level_privilege)
    FullListView levelPrivilegeListView;


    @Bind(R.id.tv_privilege_name)
    TextView privilegeNameView;

    @Bind(R.id.tv_privilege_level)
    TextView privilegeLevelView;

    @Bind(R.id.tv_privilege_introuduce)
    TextView privilegeIntroView;

    @Bind(R.id.siv_privilege_icon)
    FSSimpleImageView iconView;

    @Bind(R.id.siv_privilege_bg)
    FSSimpleImageView bgView;

    @Bind(R.id.header_layout)
    RelativeLayout headerLayout;

    private String memberType;
    private int priliegeId;
    private int needLevelId;
    private int currentLevelId = 0;
    private String title;

    private static final int ALPHA_FILL = 255;
    private static final int MOVE_HEADER_HEIGHT = 200;

    private MemberExclusiveSpreeAdapter exclusiveSpreeAdapter;
    private MemberLevelSpreeAdapter levelSpreeAdatper;
    private MemberGameVoucherAdapter gameVourcherAdapter;
    public static final String TYPE_HEAD_FRAME = "HeadFrame";//头像边框
    public static final String TYPE_SPECIFIC_MEDAL = "SpecificMedal";//个性勋章 SpecificMedal
    public static final String TYPE_KUWAN_SPECIAL_BUY = "KuwanSpecialBuy";//酷玩专购 KuwanSpecialBuy
    public static final String TYPE_LEVEL_SPREE = "LevelSpree";    //等级礼包 LevelSpree
    public static final String TYPE_EXCLUSIVE_SPREE = "ExclusiveSpree";  //专享礼包 ExclusiveSpree
    public static final String TYPE_INTEGRAL_RAISE = "IntegralRaise"; //积分翻倍 IntegralRaise
    public static final String TYPE_BIRTHDAY_GIFT = "BirthdayGift"; //生日礼物, BirthdayGift
    public static final String TYPE_CUSTOMER_SERVICE = "CustomerService"; //店长服务 CustomerService
    public static final String TYPE_KUWAN_DISCOUNT_BUY = "KuwanDiscountBuy"; //优惠折扣 KuwanDiscountBuy
    public static final String TYPE_PASSWORD_RETRIEVE = "PasswordRetrieve"; //密码找回 PasswordRetrieve
    public static final String TYPE_GAME_PLAY = "GamePlay"; //游戏畅玩 GamePlay
    public static final String TYPE_QUALITY_SERVICE = "QualityService"; //优质服务 QualityService
    public static final String TYPE_CONNECTION_DIRECTOR = "ConnectionDirector"; //连线总监 ConnectionDirector

    public static Intent newIntent(Context context, String title, String memberType, int priliegeId) {
        Intent intent = new Intent(context, MemberDetailActivity.class);
        intent.putExtra(AppConstants.MEMBER_TYPE, memberType);
        intent.putExtra(AppConstants.MEMBER_TITLE, title);
        intent.putExtra(AppConstants.MEMBER_PRILIEGE_ID, priliegeId);
        return intent;
    }

    @Subscribe
    public void onUserLogin(UserLoginEvent event) {
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window mWindow = getWindow();
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
        }

        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }


    @Override
    protected void handleIntent() {
        memberType = getIntent().getStringExtra(AppConstants.MEMBER_TYPE);
        priliegeId = getIntent().getIntExtra(AppConstants.MEMBER_PRILIEGE_ID, -1);
        title = getIntent().getStringExtra(AppConstants.MEMBER_TITLE);
    }

    @Override
    protected void initView() {
        //Action bar
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) actionbarView.getLayoutParams();
        if (params != null) {
            params.topMargin = ComUtil.getStatesBarHeight();
        }
        actionbarView.getBackground().setAlpha(0);
        actionbarView.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) headerLayout.getLayoutParams();
        int headerHeight = ResUtil.getDimensionPixelOffset(R.dimen.member_detail_header_height);
        int statusBarHeight = ResUtil.getDimensionPixelOffset(R.dimen.status_bar_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layoutParams.height = headerHeight;
        } else {
            layoutParams.height = headerHeight - statusBarHeight;
        }
        headerLayout.setLayoutParams(layoutParams);

        contentContainer.setOnScrollChangedListener(new RLScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollchanged(int x, int y, int oldxX, int oldY) {
                setStatusAndActionbar(y, title);
            }
        });

        levelPrivilegeListView.setFocusable(false);
    }

    @Override
    protected void loadData() {
        String url = JsonUrl.getJsonUrl().JSON_URL_MEMBER_USER_PRIVILEGE + "?iPrivilegeId=" + priliegeId;
        FSRequestHelper.newGetRequest(url, TAG, MemberPrivilege.class, new IFDResponse<MemberPrivilege>() {
            @Override
            public void onSuccess(MemberPrivilege result) {
                if (result != null && result.getItem() != null) {

                    needLevelId = result.getItem().getNeedLevelId();
                    if (result.getItem().getLevel() != null) {
                        currentLevelId = result.getItem().getLevel().getLevelId();
                    }
                    setupView(result);
                } else if (result != null && result.getCode() == 1008) {
                    AccountUtil.usrInfoNoSuccess(MemberDetailActivity.this, AppConstants.USR_STATUS_EXPIRE_ERROR);
                }

            }

            @Override
            public void onNetWorkError() {

            }

            @Override
            public void onServerError() {

            }
        }, false);


        //如果是酷玩专购 则从里面取数据
        if (memberType.equals(TYPE_KUWAN_SPECIAL_BUY)) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.item_container, MemberKuwanSpecialBuyFragment.getInstance());
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_member_detail;
    }

    /**
     * 设置 根据数据 设置view
     *
     * @param result
     */
    private void setupView(final MemberPrivilege result) {
        MemberPrivilege.ModelItem item = result.getItem();
        privilegeNameView.setText(item.getPrivilegeName());
        privilegeLevelView.setText(item.getNeedLevel());
        privilegeIntroView.setText(item.getDesc());
        iconView.setImageUrl(item.getIconLargeUrl());
        bgView.setImageUrl(item.getBgUrl());

        switch (memberType) {
            case TYPE_HEAD_FRAME: //头像边框
            case TYPE_SPECIFIC_MEDAL:   //个性勋章
                itemContainer.removeAllViews();
                FullGridView headFrameGridView = new FullGridView(MemberDetailActivity.this);
                headFrameGridView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                headFrameGridView.setNumColumns(3);
                headFrameGridView.setHorizontalSpacing(ComUtil.dpToPx(1));
                headFrameGridView.setVerticalSpacing(ComUtil.dpToPx(1));

                MemberHeadFrameAdapter headFrameAdapter = new MemberHeadFrameAdapter(MemberDetailActivity.this, result.getItem().getLevelPrivilges(), 3, memberType);
                headFrameGridView.setAdapter(headFrameAdapter);
                itemContainer.addView(headFrameGridView);
                break;

            case TYPE_KUWAN_SPECIAL_BUY://酷玩专购

                break;

            case TYPE_LEVEL_SPREE:  //等级礼包
                itemContainer.removeAllViews();

                FullGridView gridView = new FullGridView(MemberDetailActivity.this);
                gridView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                gridView.setNumColumns(2);
                gridView.setHorizontalSpacing(ComUtil.dpToPx(1));
                gridView.setVerticalSpacing(ComUtil.dpToPx(1));
                levelSpreeAdatper = new MemberLevelSpreeAdapter(MemberDetailActivity.this, currentLevelId, result.getItem().getLevelPrivilges(), 2);
                levelSpreeAdatper.setOnGoodsGetListener(new GoodsGetActionListener());
                gridView.setAdapter(levelSpreeAdatper);
                itemContainer.addView(gridView);
                break;

            case TYPE_EXCLUSIVE_SPREE: //专享礼包
                exclusiveSpreeAdapter = new MemberExclusiveSpreeAdapter(MemberDetailActivity.this, currentLevelId, needLevelId, result.getItem().getConfig(), result.getItem().getArticleList());
                exclusiveSpreeAdapter.setOnGoodsGetListener(new GoodsGetActionListener());
                levelPrivilegeListView.setDividerHeight(0);
                levelPrivilegeListView.setAdapter(exclusiveSpreeAdapter);
                break;

            case TYPE_KUWAN_DISCOUNT_BUY: //优惠折扣
            case TYPE_INTEGRAL_RAISE:  //积分翻倍
                MemberDiscountsAdapter discountsAdapter = new MemberDiscountsAdapter(MemberDetailActivity.this, memberType, result.getItem().getLevelPrivilges());
                levelPrivilegeListView.setAdapter(discountsAdapter);
                break;
            case TYPE_BIRTHDAY_GIFT: //生日礼物
                MemberBirthdayGiftAdapter birthdayGiftAdapter = new MemberBirthdayGiftAdapter(MemberDetailActivity.this, result.getItem().getLevelPrivilges());
                levelPrivilegeListView.setAdapter(birthdayGiftAdapter);
                break;

            case TYPE_CUSTOMER_SERVICE:  //店长服务
            case TYPE_PASSWORD_RETRIEVE: //密码找回
                //获取 配置字段
                String copyStr;
                try {
                    JSONObject config = JSON.parseObject(result.getItem().getConfig());
                    copyStr = config.getString("qq");

                } catch (Exception e) {
                    copyStr = "";
                    e.printStackTrace();
                }
                //如果QQ 号码获取不到 则获取手机号码
                if (TextUtils.isEmpty(copyStr)) {
                    try {
                        JSONObject config = JSON.parseObject(result.getItem().getConfig());
                        copyStr = config.getString("phone");
                    } catch (Exception e) {

                    }

                }
                itemContainer.removeAllViews();
                View customerView = LayoutInflater.from(MemberDetailActivity.this).inflate(R.layout.item_member_customer_service, itemContainer, false);
                customerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_bottom);
                layout.setVisibility(View.VISIBLE);
                TextView copyView = (TextView) findViewById(R.id.tv_copy);
                copyView.setVisibility(View.VISIBLE);
                copyView.setTag(copyStr);
                if (currentLevelId >= needLevelId) {
                    copyView.setBackgroundResource(R.drawable.btn_green_selector);
                    copyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ComUtil.copyToClipBoard(MemberDetailActivity.this, (String) v.getTag());
                        }
                    });
                } else {
                    copyView.setBackgroundResource(R.drawable.btn_grey_selector);
                    copyView.setOnClickListener(null);
                }

                itemContainer.addView(customerView);
                break;

            case TYPE_GAME_PLAY://游戏畅玩
                List<MemberPrivilege.ModelItem.LevelPrivilege> list = new ArrayList<>();
                //刷选
                for (MemberPrivilege.ModelItem.LevelPrivilege privilege : result.getItem().getLevelPrivilges()) {
                    if (privilege.getLevelId() == currentLevelId) {
                        list.add(privilege);
                    }
                }
                gameVourcherAdapter = new MemberGameVoucherAdapter(MemberDetailActivity.this, currentLevelId, needLevelId, list);
                gameVourcherAdapter.setOnGoodsGetListener(new GoodsGetActionListener());
                levelPrivilegeListView.setDivider(new ColorDrawable(ResUtil.getColor(R.color.common_window_bg)));
                levelPrivilegeListView.setBackgroundColor(ResUtil.getColor(R.color.common_window_bg));
                levelPrivilegeListView.setDividerHeight(ComUtil.dpToPx(8));
                levelPrivilegeListView.setAdapter(gameVourcherAdapter);
                EmptyView emptyView = new EmptyView(this, levelPrivilegeListView);
                emptyView.setEmptyMessage(ResUtil.getString(R.string.member_game_voucher_emtpy_hint));

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemContainer.getLayoutParams();
                if (ListUtils.isEmpty(list)) {
                    emptyView.showEmpty();
                    itemContainer.setBackgroundColor(ResUtil.getColor(R.color.white));
                    params.setMargins(0, ComUtil.dpToPx(8), 0, ComUtil.dpToPx(8));
                    itemContainer.setLayoutParams(params);
                } else {
                    params.setMargins(ComUtil.dpToPx(8), ComUtil.dpToPx(8), ComUtil.dpToPx(8), 0);
                }

                itemContainer.setLayoutParams(params);
                break;

            case TYPE_QUALITY_SERVICE: //优质服务
                MemberQualityServiceAdapter qualityServiceAdatper = new MemberQualityServiceAdapter(MemberDetailActivity.this, item.getConfig());
                levelPrivilegeListView.setAdapter(qualityServiceAdatper);
                break;

            case TYPE_CONNECTION_DIRECTOR: //连线总监
                itemContainer.removeAllViews();
                View connectView = LayoutInflater.from(MemberDetailActivity.this).inflate(R.layout.item_member_connection_dirctor, null);
                ImageView lineTView = (ImageView) connectView.findViewById(R.id.iv_member_call_line1);
                ImageView lineBView = (ImageView) connectView.findViewById(R.id.iv_member_call_line2);
                ImageView btnView = (ImageView) connectView.findViewById(R.id.iv_member_call_btn);

                Animation animation = AnimationUtils.loadAnimation(this, R.anim.member_left_in);
                Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.member_right_out);
                Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.member_shake);
                lineTView.startAnimation(animation2);
                lineBView.startAnimation(animation);
                btnView.startAnimation(animation3);

                LinearLayout.LayoutParams paramsConn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsConn.gravity = Gravity.CENTER_VERTICAL;
                connectView.setLayoutParams(paramsConn);
                connectView.setBackgroundColor(ResUtil.getColor(R.color.white));

                try {
                    final String phone = JSON.parseObject(item.getConfig()).getString("phone");
                    btnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentLevelId < needLevelId) {
                                ToastUtils.showMsg(MemberDetailActivity.this,
                                        ResUtil.getString(R.string.member_needlevel_hint));
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemContainer.addView(connectView);

                break;


            default:
                break;

        }
    }


    /**
     * 根据移动 设置status bar 和Actionbar
     *
     * @param scrollY
     * @param title
     */
    public void setStatusAndActionbar(int scrollY, String title) {
        float currentAlpha = (Math.min((float) scrollY, ComUtil.dpToPx(MOVE_HEADER_HEIGHT)) / ComUtil.dpToPx(MOVE_HEADER_HEIGHT));
        if (currentAlpha < 0) {
            currentAlpha = 0;
        }

        int alpha = (int) (currentAlpha * ALPHA_FILL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alpha <= 30)
                getWindow().setStatusBarColor(ResUtil.getColor(R.color.translucent_30_black));
            else {
                getWindow().setStatusBarColor(Color.argb(alpha, 214, 69, 70));
            }
        }

        if (currentAlpha >= 0.9 && TextUtils.isEmpty(actionbarTitleView.getText().toString())) {
            actionbarTitleView.setText(title);
        } else if (currentAlpha < 0.9 && false == TextUtils.isEmpty(actionbarTitleView.getText().toString())) {
            actionbarTitleView.setText("");
        }
        actionbarView.getBackground().setAlpha(alpha);
    }


    /**
     * 领取物品
     */
    class GoodsGetActionListener implements GoodsGetListener {


        @Override
        public void getGoodsRequest(final int articeId, int levelId, int goodsId) {

            Map<String, String> parmas = new HashMap<>();
            parmas.put("iPrivilegeId", String.valueOf(priliegeId));
            if (!memberType.equals(TYPE_EXCLUSIVE_SPREE)) {
                parmas.put("iLevel", String.valueOf(levelId));
            }
            parmas.put("iGoodsId", String.valueOf(goodsId));
            FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_MEMBER_USER_DRAW, TAG, MemberSpreeResultModel.class, new IFDResponse<MemberSpreeResultModel>() {
                @Override
                public void onSuccess(MemberSpreeResultModel result) {
                    if (result != null) {
                        if (result.getCode() == 0) {

                            switch (memberType) {
                                case TYPE_LEVEL_SPREE:  //等级礼包
                                    if (levelSpreeAdatper != null) {
                                        levelSpreeAdatper.refreshData(articeId);
                                        ToastUtils.showMsg(MemberDetailActivity.this, ResUtil.getString(R.string.member_level_spree_get_success));
                                    }
                                    break;

                                case TYPE_EXCLUSIVE_SPREE: //专享礼包
                                    if (exclusiveSpreeAdapter != null) {
                                        ToastUtils.showMsg(MemberDetailActivity.this, ResUtil.getString(R.string.personal_task_receive_success));
                                        exclusiveSpreeAdapter.refreshData(articeId, result.getItem());
                                    }
                                    break;


                                case TYPE_GAME_PLAY://游戏畅玩
                                    if (gameVourcherAdapter != null) {
                                        gameVourcherAdapter.refreshData(articeId);
                                        ToastUtils.showMsg(MemberDetailActivity.this, ResUtil.getString(R.string.member_game_vourcher_get_success));
                                    }
                                    break;

                                default:
                                    break;

                            }

                        } else {
                            ToastUtils.showMsg(MemberDetailActivity.this, result.getMsg());
                        }
                    } else {
                        ToastUtils.showMsg(MemberDetailActivity.this, "UNKNOWN_ERROR");
                    }
                }

                @Override
                public void onNetWorkError() {
                    ToastUtils.showMsg(MemberDetailActivity.this, ResUtil.getString(R.string.spree_get_error));
                }

                @Override
                public void onServerError() {
                    ToastUtils.showMsg(MemberDetailActivity.this, ResUtil.getString(R.string.spree_get_error));
                }
            }, parmas);
        }


    }

    @OnClick(R.id.tv_detail_title)
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }
}
