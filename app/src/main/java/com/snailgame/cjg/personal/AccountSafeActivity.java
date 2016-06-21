package com.snailgame.cjg.personal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.common.widget.AccountSafeItem;
import com.snailgame.cjg.event.AvatarChangeEvent;
import com.snailgame.cjg.event.UpdateUserInfoPhoneEvent;
import com.snailgame.cjg.event.UserPrivilegesEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.navigation.UnBindPhoneActivity;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.cjg.personal.widget.PrivilegeDialog;
import com.snailgame.cjg.personal.widget.UserBirthdaySelectorDialog;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by sunxy on 2014/12/30.
 */
public class AccountSafeActivity extends SwipeBackActivity {
    static String TAG = AccountSafeActivity.class.getName();

    private static final int UNBIND_REQUEST_CODE = 6;
    private UserInfo mUsrInfo;
    @Bind(R.id.personal_icon)
    AccountSafeItem personalIconItem;
    @Bind(R.id.personal_nickname)
    AccountSafeItem nickNameItem;
    @Bind(R.id.personal_account)
    AccountSafeItem accountItem;
    @Bind(R.id.personal_phone)
    AccountSafeItem telephoneItem;
    @Bind(R.id.snail_resetpwd_edit)
    AccountSafeItem passwordEdit;
    @Bind(R.id.account_protect)
    AccountSafeItem passwordProtect;

    @Bind(R.id.personal_birthday)
    AccountSafeItem ageItemView;

    @Bind(R.id.privilege_container)
    LinearLayout privilegeContainer;

    @Bind(R.id.privilege_item)
    ViewGroup privilegeItem;
    private ProgressDialog progressDialog;
    private String nameBuf;

    private boolean isBindPhoneNumber = true;

    private PrivilegeDialog mPrivilegeDialog;

    public static Intent newIntent(Context context) {
        return new Intent(context, AccountSafeActivity.class);
    }

    @Subscribe
    public void onAvatarChanged(AvatarChangeEvent event) {
        Bitmap bitmap = event.getAvatarBitmap();

        if (bitmap != null && personalIconItem != null) {
            bitmap = toOvalBitmap(bitmap);
            Drawable bitmapDrawable = new BitmapDrawable(bitmap);
            personalIconItem.setAvater(bitmapDrawable);
        }
    }


    @Subscribe
    public void onUserPrivilegesChange(UserPrivilegesEvent event) {
        if (LoginSDKUtil.isLogined(this)) {
            setupPrivilegeView();
        }
    }

    @Subscribe
    public void onBindSuccessed(UpdateUserInfoPhoneEvent event) {
        String phone = event.getPhone();
        mUsrInfo.setcPhone(phone);
        telephoneItem.initData(getString(R.string.free_phone_number), phone, null, true);
        isBindPhoneNumber = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.id.actionbar_account_safe);
        registerReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_ACCOUNT_SAFE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_ACCOUNT_SAFE);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        mUsrInfo = GlobalVar.getInstance().getUsrInfo();
        if (mUsrInfo != null) {
            personalIconItem.initData(getString(R.string.my_personla_icon), null, null, true);
            inflatePhotoData();

            nickNameItem.initData(getString(R.string.my_nickname), mUsrInfo.getsNickName(), null, true);

            ((TextView) accountItem.findViewById(R.id.accountSafeSubTitle)).setTextColor(ResUtil.getColor(R.color.black));
            accountItem.initData(IdentityHelper.getDisplayAccount(this), "", null, false);

            String telephone = mUsrInfo.getcPhone();
            if (TextUtils.isEmpty(telephone)) {
                isBindPhoneNumber = false;
                telephone = getString(R.string.no_free_telephone_number);
            }
            //生日
            ageItemView.initData(getString(R.string.personal_birthday_title), TextUtils.isEmpty(mUsrInfo.getUserBirthday()) ?
                    getString(R.string.personal_content_empty) : mUsrInfo.getUserBirthday(), null, true);

            telephoneItem.initData(getString(R.string.free_phone_number), telephone, null, true);

            passwordEdit.initData(getString(R.string.personal_option_password), null, null, true);

            passwordProtect.initData(getString(R.string.password_protect), null, null, true);
        }

        mPrivilegeDialog = new PrivilegeDialog(this);
        setupPrivilegeView();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_account_safe;
    }

    private void setupPrivilegeView() {
        privilegeContainer.removeAllViews();
        if (GlobalVar.getInstance().getUsrPrivileges() != null && GlobalVar.getInstance().getUsrPrivileges().getItemList() != null) {
            for (int i = 0; i < GlobalVar.getInstance().getUsrPrivileges().getItemList().size(); i++) {
                final UserPrivilegesModel.ModelItem item = GlobalVar.getInstance().getUsrPrivileges().getItemList().get(i);
                AccountSafeItem view = new AccountSafeItem(this);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ComUtil.dpToPx(48)));
                view.setBackgroundDrawable(ResUtil.getDrawable(R.drawable.list_item_selector));
                view.initData(item.getsPrivilegeName() + getString(R.string.personal_privileage),
                        item.isOpened() ? item.getcLightIcon() : item.getcGrayIcon(),
                        item.isOpened() ? "" : getString(R.string.no_free_telephone_number), null, true);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPrivilegeDialog != null) {
                            mPrivilegeDialog.showPopup(item);
                        }
                    }
                });
                privilegeContainer.addView(view);

                if (i != GlobalVar.getInstance().getUsrPrivileges().getItemList().size() - 1) {
                    View lineView = LayoutInflater.from(this).inflate(R.layout.common_horizontal_line, null);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    layoutParams.setMargins(ComUtil.dpToPx(16), 0, ComUtil.dpToPx(16), 0);
                    lineView.setLayoutParams(layoutParams);
                    privilegeContainer.addView(lineView);
                }

                privilegeItem.setVisibility(View.VISIBLE);
            }

        }
    }

    private void registerReceiver() {
        MainThreadBus.getInstance().register(this);

    }

    private void inflatePhotoData() {
        personalIconItem.initData(getString(R.string.my_personla_icon), null, mUsrInfo.getcPhoto(), true);
    }


    private CharSequence tempString;

    @OnClick(R.id.personal_nickname)
    void createNameEditDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setContentView(R.layout.personal_setting_name_dialog);
        dialog.setCanceledOnTouchOutside(false);
        final EditText nameEditView = (EditText) dialog.findViewById(R.id.et_name);


        nameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tempString = s;
            }

            @Override
            public void afterTextChanged(Editable s) {

                //最大32个字符
                if (tempString.length() > 32) {
                    ToastUtils.showMsgLong(AccountSafeActivity.this, R.string.limit_length);
                    int deleteLentht = tempString.length() - 32;
                    int selectionEnd = nameEditView.getSelectionEnd();
                    if (selectionEnd >= deleteLentht) {
                        s.delete(selectionEnd - deleteLentht, selectionEnd);
                        nameEditView.setText(s);
                    }
                }
            }
        });
        dialog.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameBuf = nameEditView.getText().toString().replaceAll("\r|\n", "");
                if (TextUtils.isEmpty(nameBuf)) {
                    ToastUtils.showMsg(AccountSafeActivity.this, R.string.personal_nick_null_hint);
                    return;
                }


                progressDialog = ProgressDialog.show(AccountSafeActivity.this, "", getString(R.string.personal_saving_tip), true);
                updateUserInfoDetail(nameBuf);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        nameEditView.setText("");
        nameEditView.setFocusableInTouchMode(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) nameEditView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(nameEditView, 0);
            }
        }, 500);
        dialog.show();
    }


    /**
     * 更新用户信息请求
     *
     * @param nickName
     */
    private void updateUserInfoDetail(final String nickName) {
        String url = JsonUrl.getJsonUrl().JSON_URL_PERSONAL_NICKNAME;
        String postBody = AccountUtil.getLoginParams().replace("?", "") + "&sNickName=" + nickName;

        FSRequestHelper.newPostRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        mUsrInfo.setsNickName(nameBuf);
                        nickNameItem.initData(getString(R.string.my_nickname), mUsrInfo.getsNickName(), null, true);
                        setResult(AppConstants.EDIT_NICKNAME_RESULT_CODE);
                        startService(UserInfoGetService.newIntent(AccountSafeActivity.this, AppConstants.ACTION_UPDATE_USR_INFO));
                    } else if (code == 5125 || code == 3020) {
                        ToastUtils.showMsg(AccountSafeActivity.this, ResUtil.getString(R.string.personal_name_invalid));
                    } else {
                        ToastUtils.showMsg(AccountSafeActivity.this, ResUtil.getString(R.string.unknown_error));
                    }
                } catch (Exception e) {
                    ToastUtils.showMsg(AccountSafeActivity.this, ResUtil.getString(R.string.unknown_error));
                }
            }

            @Override
            public void onNetWorkError() {
                updateUserInfoError();
            }

            @Override
            public void onServerError() {
                updateUserInfoError();
            }
        }, postBody);
    }

    /**
     * 更新用户信息失败
     */
    private void updateUserInfoError() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        ToastUtils.showMsg(AccountSafeActivity.this, ResUtil.getString(R.string.unknown_error));
    }

    @OnClick(R.id.account_protect)
    void showProtect() {
        LoginSDKUtil.snailBindMobile(this);
    }

    @OnClick(R.id.snail_resetpwd_edit)
    void setupPassword() {
        if (LoginSDKUtil.isLogined(this)) {
            LoginSDKUtil.snailChangePsw(this);
        }
    }

    @OnClick(R.id.personal_phone)
    void setupPhone() {
        if (isBindPhoneNumber) {
            startActivityForResult(UnBindPhoneActivity.newIntent(AccountSafeActivity.this, mUsrInfo.getcPhone(), mUsrInfo.getsNickName()), UNBIND_REQUEST_CODE);
        } else {
            AccountUtil.showNavigation(this, false);
        }
    }


    @OnClick(R.id.personal_icon)
    void setupAvatar() {
        startActivity(AvatarSetupActivity.newIntent(this));
    }


    @OnClick(R.id.personal_birthday)
    public void setupUserBirthday() {
        if (GlobalVar.getInstance().getUsrInfo() != null && false == TextUtils.isEmpty(GlobalVar.getInstance().getUsrInfo().getUserBirthday())) {
            ToastUtils.showMsg(this, getString(R.string.personal_birthday_invaild_changed));
            return;
        }

        final UserBirthdaySelectorDialog dialog = new UserBirthdaySelectorDialog(this);
        dialog.setBirthdayListener(new UserBirthdaySelectorDialog.OnBirthListener() {
            @Override
            public void onClick(final int year, final int month, final int day) {

                DialogUtils.showTwoButtonDialog(AccountSafeActivity.this, "", getString(R.string.personal_birthday_dialog_cancel),
                        getString(R.string.personal_birthday_changed_hint), new DialogUtils.ConfirmClickedLister() {
                            @Override
                            public void onClicked() {
                                final String birthday = year + "-" + month + "-" + day;
                                FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_USER_BIRTHDAY, TAG, BaseDataModel.class, new IFDResponse<BaseDataModel>() {
                                    @Override
                                    public void onSuccess(BaseDataModel result) {
                                        if (result != null && result.getCode() == 0) {
                                            mUsrInfo.setUserBirthday(birthday);
                                            ageItemView.initData(getString(R.string.personal_birthday_title), birthday, null, true);
                                        } else {
                                            if (result != null && false == TextUtils.isEmpty(result.getMsg())) {
                                                ToastUtils.showMsg(AccountSafeActivity.this, result.getMsg());
                                            } else {
                                                ToastUtils.showMsg(AccountSafeActivity.this, getString(R.string.personal_birthday_set_failed));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNetWorkError() {
                                        ToastUtils.showMsg(AccountSafeActivity.this, getString(R.string.personal_birthday_set_failed));
                                    }

                                    @Override
                                    public void onServerError() {
                                        ToastUtils.showMsg(AccountSafeActivity.this, getString(R.string.personal_birthday_set_failed));
                                    }
                                }, "sBirthday=" + birthday);
                            }
                        });
            }
        });
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
        mPrivilegeDialog.hidePopup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!LoginSDKUtil.isLogined(this)) {
            finish();
        }


        //解除绑定返回
        if (requestCode == UNBIND_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //刷新个人信息
                mUsrInfo.setcPhone("");
                startService(UserInfoGetService.newIntent(AccountSafeActivity.this, AppConstants.ACTION_UPDATE_USR_INFO));
                isBindPhoneNumber = false;
                telephoneItem.initData(getString(R.string.free_phone_number), getString(R.string.no_free_telephone_number), null, true);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap toOvalBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }
}