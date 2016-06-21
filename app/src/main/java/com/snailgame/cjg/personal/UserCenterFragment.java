package com.snailgame.cjg.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.SystemConfig;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.event.AvatarChangeEvent;
import com.snailgame.cjg.event.MemberChangeEvent;
import com.snailgame.cjg.event.ScratchInfoChangeEvent;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.event.UserInfoChangeEvent;
import com.snailgame.cjg.event.UserLoginEvent;
import com.snailgame.cjg.event.UserLogoutEvent;
import com.snailgame.cjg.event.VoucherUpdateEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.member.MemberCenterActivity;
import com.snailgame.cjg.member.model.MemberInfoModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.UserCenterAdapter;
import com.snailgame.cjg.personal.model.ScratchInfoModel;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.personal.model.UserStateModel;
import com.snailgame.cjg.personal.widget.SlidingScrollView;
import com.snailgame.cjg.settings.SettingActivity;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ChannelUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.skin.ISkinColor;
import com.snailgame.cjg.util.skin.ISkinDrawable;
import com.snailgame.cjg.util.skin.SkinManager;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sunxy on 2015/2/6.
 */
public class UserCenterFragment extends FixedSupportV4BugFragment implements SlidingScrollView.ScrollViewListener {
    static String TAG = UserCenterFragment.class.getName();

    @Bind(R.id.root_view)
    SlidingScrollView mRootView;

    @Bind(R.id.imageMenuAvatar)
    FSSimpleImageView mAvatarView;

    @Bind(R.id.imageMenuAvatar_bg)
    FSSimpleImageView bgAvatarView;

    @Bind(R.id.layout_avata)
    FrameLayout layoutAvata;

    @Bind(R.id.tv_personal_levelname)
    TextView mlevelView;

    @Bind(R.id.tv_account_name)
    TextView accountNameView;

    @Bind(R.id.userinfo_view_container)
    ViewGroup mUserInfoViewContainer;

    @Bind(R.id.tv_level)
    TextView tv_level;


    ImageView scratchImg;

    @Bind(R.id.tv_account_point)
    TextView mPointView;
    @Bind(R.id.tv_account_currency)
    TextView mCurrencyView;

    @Bind(R.id.tv_account_voucher)
    TextView mVoucherView;


    @Bind(R.id.not_login_layout)
    LinearLayout mNotLoginLayout;
    @Bind(R.id.my_wallet_desc)
    TextView mWalletDesc;

    @Bind(R.id.item_container)
    FullGridView item_container;

    @Bind(R.id.tv_personal_composition)
    TextView tv_personal_composition;//合约机用户

    private UserCenterAdapter userCenterAdapter;
    private MainActivity mMainActivity;
    private int topViewHeight;


    private static final int MEMBER_CENTER = 0, PERSONAL_CODE_EXCHANGE = 1,
            PERSONAL_TASK = 2, PERSONAL_SCRATCH = 3, MY_ORDER = 4,
            SHOPPING_CAR = 5, MY_ADDRESS = 6, SPREE = 7, FREE_CARD = 8, SETTING = 9;

    public static final int NONE = 0;
    private static final int TOP_NUM = 4;
    private UserInfo mUsrInfo;
    private UserStateModel.ModelItem mUserState;
    private ArrayList<ISkinDrawable> iSkinDrawables = new ArrayList<>();
    private ArrayList<ISkinColor> iSkinColors = new ArrayList<>();
    private MemberInfoModel memberInfoModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    /**
     * 头像变更
     */
    @Subscribe
    public void onAvatarChanged(AvatarChangeEvent event) {
        Bitmap bitmap = event.getAvatarBitmap();
        if (bitmap != null && mAvatarView != null) {
            mAvatarView.setImageBitmap(bitmap);
        }
    }

    /**
     * 积分变更
     */
    @Subscribe
    public void onScratchChanged(ScratchInfoChangeEvent event) {
        if (mUsrInfo == null)
            return;
        ScratchInfoModel scratchInfoModel = event.getScratchInfoModel();
        if (scratchImg == null) {
            return;
        }
        if (scratchInfoModel == null) {
            scratchImg.setVisibility(View.GONE);
            return;
        }
        ScratchInfoModel.ScratchInfo scratchInfo = scratchInfoModel.getItemModel();
        if (scratchInfoModel.getCode() == 0) {
            if (scratchInfo != null) {
                if (scratchInfo.getStatus() == ScratchInfoModel.ScratchInfo.STATUS_FREE) {
                    scratchImg.setVisibility(View.VISIBLE);
                } else {
                    scratchImg.setVisibility(View.GONE);
                }
            } else {
                scratchImg.setVisibility(View.GONE);
            }
        } else {
            scratchImg.setVisibility(View.GONE);
        }
    }

    /**
     * 代金券更新
     */
    @Subscribe
    public void onVoucherUpdate(VoucherUpdateEvent event) {
        mVoucherView.setText(String.valueOf(SharedPreferencesUtil.getInstance().getVoucherNum()));
    }

    /**
     * 个人信息变更（包括登录的时候）重新刷新个人信息的页签
     */
    @Subscribe
    public void onUserInfoChange(UserInfoChangeEvent event) {
        if (LoginSDKUtil.isLogined(getActivity())) {
            mUsrInfo = GlobalVar.getInstance().getUsrInfo();
            memberInfoModel = GlobalVar.getInstance().getMemberInfo();
            setupView(mUsrInfo, mUserState);
        }
    }

    /**
     * 用户登录
     */
    @Subscribe
    public void onUserLogin(UserLoginEvent event) {
        mUsrInfo = GlobalVar.getInstance().getUsrInfo();
        loadUserState();
    }

    @Subscribe
    public void onUserLogin(MemberChangeEvent event) {
        memberInfoModel = GlobalVar.getInstance().getMemberInfo();
        setupView(mUsrInfo, mUserState);
    }


    /**
     * 用户登出
     */
    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        mUsrInfo = null;
        mUserState = null;
        memberInfoModel = null;
        setupView(mUsrInfo, mUserState);
    }

    /**
     * 重复点击tab返回顶部
     *
     * @param event
     */
    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_MINE && mRootView != null) {
            mRootView.scrollTo(0, 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsrInfo = GlobalVar.getInstance().getUsrInfo();
        memberInfoModel = GlobalVar.getInstance().getMemberInfo();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_center_layout, null);
        ButterKnife.bind(this, view);
        userCenterAdapter = new UserCenterAdapter(mUsrInfo);
        item_container.setAdapter(userCenterAdapter);
        setupView(mUsrInfo, mUserState);
        mRootView.setScrollViewListener(this);
        item_container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 10) {
                    onItemClicked(view);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUsrInfo != null)
            loadUserState();
    }

    private void loadUserState() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_USER_STATE, TAG, UserStateModel.class, new IFDResponse<UserStateModel>() {
            @Override
            public void onSuccess(UserStateModel result) {
                if (result != null) {
                    mUserState = result.getModelItem();
                    userCenterAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNetWorkError() {

            }

            @Override
            public void onServerError() {

            }
        }, false, true, new ExtendJsonUtil());
    }


    private void setupView(UserInfo userInfo, UserStateModel.ModelItem userState) {
        setupUserInfoView(userInfo);
        userCenterAdapter.notifyDataSetChanged();
        initViewSkins();
    }


    private void setupUserInfoView(UserInfo info) {
        if (info == null) {
            tv_personal_composition.setVisibility(View.GONE);
            mUserInfoViewContainer.setVisibility(View.GONE);
            mNotLoginLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams mNotLoginLayoutLayoutParams = (LinearLayout.LayoutParams) mNotLoginLayout.getLayoutParams();
            topViewHeight = ResUtil.getDimensionPixelSize(R.dimen.dimen_180dp);
            mNotLoginLayoutLayoutParams.height += getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight();
            mNotLoginLayout.setPadding(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight(), 0, 0);
            mNotLoginLayout.setLayoutParams(mNotLoginLayoutLayoutParams);
            mWalletDesc.setText("");
            mPointView.setText("0");
            mCurrencyView.setText("0");
            mVoucherView.setText("0");
            return;
        } else {
            mUserInfoViewContainer.setVisibility(View.VISIBLE);
            mNotLoginLayout.setVisibility(View.GONE);
            mWalletDesc.setText(getString(R.string.personal_my_wallet_footer));
        }
        if (info.iscContractMachine()) {//合约机用户
            tv_personal_composition.setVisibility(View.VISIBLE);
        } else {
            tv_personal_composition.setVisibility(View.GONE);
        }
        String[] levels = getResources().getStringArray(R.array.personal_level_array);
        if (memberInfoModel != null) {
            tv_level.setText(String.format(ResUtil.getString(R.string.user_level), memberInfoModel.getCurrentlevel().getsLevelName()));
            if (Integer.parseInt(memberInfoModel.getCurrentPoint()) > 0) {
                tv_level.setVisibility(View.VISIBLE);
            } else {
                tv_level.setVisibility(View.GONE);
            }
        }

        if (info.getiLevel() <= 1) {
            mlevelView.setText(levels[0]);
        } else if (info.getiLevel() >= 10) {
            mlevelView.setText(levels[9]);
        } else {
            mlevelView.setText(levels[info.getiLevel() - 1]);
        }

        accountNameView.setText(mUsrInfo.getsNickName());
        mPointView.setText(String.valueOf(info.getiIntegral()));
        mCurrencyView.setText(String.valueOf(info.getiMoney()));
        mVoucherView.setText(String.valueOf(SharedPreferencesUtil.getInstance().getVoucherNum()));

        mlevelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebViewActivity.newIntent(getActivity(), PersistentVar.getInstance().getSystemConfig().getUserLevelInfoUrl()));
            }
        });

        mAvatarView.setImageUrlAutoRotateEnabled(info.getcPhoto(), true);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutAvata.getLayoutParams();
        LinearLayout.LayoutParams containerLayoutParams = (LinearLayout.LayoutParams) mUserInfoViewContainer.getLayoutParams();
        if (info.getsHeadFrame() != null && !TextUtils.isEmpty(info.getsHeadFrame())) {
            bgAvatarView.setVisibility(View.VISIBLE);
            bgAvatarView.setImageUrlAndReUse(info.getsHeadFrame());
            containerLayoutParams.height = getResources().getDimensionPixelOffset(R.dimen.personal_user_info_layout_height_large) + getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight();
            topViewHeight = ResUtil.getDimensionPixelSize(R.dimen.personal_user_info_layout_height_large);
        } else {
            bgAvatarView.setVisibility(View.GONE);
            containerLayoutParams.height = getResources().getDimensionPixelOffset(R.dimen.personal_user_info_layout_height_normal) + getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight();
            topViewHeight = ResUtil.getDimensionPixelSize(R.dimen.personal_user_info_layout_height_normal);
        }
        mUserInfoViewContainer.setPadding(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight(), 0, 0);
        mUserInfoViewContainer.setLayoutParams(containerLayoutParams);
        layoutAvata.setLayoutParams(layoutParams);
    }

    private void initViewSkins() {
        if (mUsrInfo == null)
            return;
        iSkinDrawables.add(new ISkinDrawable() {
            @Override
            public int getDrawableResId() {
                return R.drawable.score_wall_bg;
            }

            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (mUserInfoViewContainer != null) {
                    mUserInfoViewContainer.setBackgroundDrawable(drawable);

                }
            }

            @Override
            public String getTag() {
                return TAG;
            }
        });

        iSkinColors.add(new ISkinColor() {
            @Override
            public int getColorResId() {
                return R.color.personal_nickname_color;
            }

            @Override
            public void onColorChanged(int color) {
                if (mlevelView != null)
                    mlevelView.setTextColor(color);

                if (accountNameView != null)
                    accountNameView.setTextColor(color);
            }

            @Override
            public String getTag() {
                return TAG;
            }


        });
        for (ISkinDrawable iSkinDrawable : iSkinDrawables) {
            SkinManager.getInstance().registerSkinables(iSkinDrawable);
        }
        for (ISkinColor iSkinColor : iSkinColors) {
            SkinManager.getInstance().registerSkinables(iSkinColor);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!LoginSDKUtil.isLogined(getActivity())) {
            getActivity().finish();
        }

        // 此处要考虑 进入账户设置退出，所有不可放在前面
        if (resultCode == NONE) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.userinfo_view_container)
    void showViewHight1() {
        //TO Nothing
    }

    @OnClick(R.id.userinfo_view_container)
    void showUserInfo() {
        startActivityForResult(AccountSafeActivity.newIntent(getActivity()), 0);
    }


    @OnClick(R.id.rv_point_container)
    void showScoreHistoryScreen() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            if (AccountUtil.isUsrInfoGet()) {
                startActivity(ScoreHistoryActivity.newIntent(getActivity()));
            } else {
                AccountUtil.usrInfoNoSuccess(getActivity(), FreeStoreApp.statusOfUsr);
            }

        } else {
            AccountUtil.userLogin(getActivity());
        }
    }

    @OnClick(R.id.rv_currency_container)
    void showCurrencyHistoryScreen() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            startActivity(CurrencyHistoryActivity.newIntent(getActivity()));
        } else {
            AccountUtil.userLogin(getActivity());
        }
    }


    @OnClick(R.id.my_wallet_container)
    void showMyWalletScreen() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            startActivity(MyWalletActivity.newIntent(getActivity()));
        } else {
            AccountUtil.userLogin(getActivity());
        }
    }

    @OnClick(R.id.rv_voucher_container)
    void showVoucherScreen() {
        if (LoginSDKUtil.isLogined(getActivity())) {
            startActivity(MyVoucherActivity.newIntent(getActivity()));
        } else {
            AccountUtil.userLogin(getActivity());
        }
    }

    @OnClick(R.id.not_login_layout)
    void notLoginLayoutClick() {
        AccountUtil.userLogin(getActivity());
    }


    @Override
    public void onDestroyView() {
        SkinManager.getInstance().unregisterSkinables(TAG);
        iSkinDrawables.clear();
        iSkinColors.clear();

        FreeStoreApp.getRequestQueue().cancelAll(TAG);

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);

    }


    public void onItemClicked(View v) {
        if (!LoginSDKUtil.isLogined(getActivity()) && !v.getTag(R.id.tag_second).equals(SETTING)) {
            AccountUtil.userLogin(getActivity());
            return;
        }
        int value = (int) v.getTag(R.id.tag_second);
        switch (value) {
            case MY_ORDER://我的订单
                if (TextUtils.isEmpty(PersistentVar.getInstance().getSystemConfig().getMyOrderUrl())) {
                    startActivity(WebViewActivity.newIntent(getActivity(), JsonUrl.getJsonUrl().JSON_URL_ORDER));
                } else {
                    startActivity(WebViewActivity.newIntent(getActivity(), PersistentVar.getInstance().getSystemConfig().getMyOrderUrl()));
                }
                break;
            case MY_ADDRESS://收货地址
                if (TextUtils.isEmpty(PersistentVar.getInstance().getSystemConfig().getOrderAddressUrl())) {
                    startActivity(WebViewActivity.newIntent(getActivity(), JsonUrl.getJsonUrl().JSON_URL_ADDRESS));
                } else {
                    startActivity(WebViewActivity.newIntent(getActivity(), PersistentVar.getInstance().getSystemConfig().getOrderAddressUrl()));
                }
                break;
            case SPREE: // 礼包
                startActivity(MySpreeActivity.newIntent(getActivity()));
                break;
            case SHOPPING_CAR: // 购物车
                if (TextUtils.isEmpty(PersistentVar.getInstance().getSystemConfig().getShoppingCarUrl())) {
                    startActivity(WebViewActivity.newIntent(getActivity(), JsonUrl.getJsonUrl().JSON_URL_SHOPPING_CAR));
                } else {
                    startActivity(WebViewActivity.newIntent(getActivity(), PersistentVar.getInstance().getSystemConfig().getShoppingCarUrl()));
                }
                break;
            case FREE_CARD: // 免卡
                boolean isOpen = true;
                SystemConfig systemConfig = PersistentVar.getInstance().getSystemConfig();

                if (systemConfig.getStopFreeCardFunc() == 1) {
                    if (systemConfig.getScoreFreeCardStopVersion().contains(String.valueOf(ComUtil.getSelfVersionCode()))
                            || systemConfig.getHideFreeCardChannelIds().contains(ChannelUtil.getChannelID())) {
                        isOpen = false;
                    }
                }
                String url = JsonUrl.getJsonUrl().JSON_URL_CHECK_BALANCE;
                String stopText = systemConfig.getStopFreeCardDes();
                startActivity(WebViewActivity.newIntent(getActivity(), url,
                        isOpen, stopText, AppConstants.WEBVIEW_MODEL_ONLINE_SHOP));
                break;

            case PERSONAL_TASK: // 我的任务
                startActivity(UserTaskActivity.newIntent(getActivity()));
                break;

            case PERSONAL_SCRATCH: // 每日一抽
                startActivity(WebViewActivity.newIntent(getActivity(), JsonUrl.getJsonUrl().JSON_URL_SCORE_LUCKY));
                break;
            case MEMBER_CENTER: // 会员中心
                startActivity(MemberCenterActivity.newIntent(getActivity()));
                break;
            case PERSONAL_CODE_EXCHANGE: // 福利码
                MobclickAgent.onEvent(getActivity(), UmengAnalytics.EVENT_NOVICE_CARD);
                startActivity(WebViewActivity.newIntent(getActivity(), JsonUrl.getJsonUrl().JSON_URL_CODE_EXCHANGE));
                break;
            case SETTING:
                startActivity(SettingActivity.newIntent(getActivity()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrollChanged(SlidingScrollView scrollView, int x, int y, int oldX, int oldY) {
        changeToolBar(scrollView.getScrollY());
    }


    public void changeToolBar(int mScrollY) {
        int showScrollY = Math.min(mScrollY, topViewHeight);
        setToolbarStatusBarAlpha(showScrollY * 255 / topViewHeight);
    }

    public void setToolbarStatusBarAlpha(int alpha) {
        if (mMainActivity != null) {
            mMainActivity.setActionBarStatusBarAlpha(alpha);
        }
    }
}