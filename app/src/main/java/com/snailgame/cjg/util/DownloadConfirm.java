package com.snailgame.cjg.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.ButterKnife;

/*
 * 应用下载，弹窗确认
 * create by xixh 07-14-14
 */
public class DownloadConfirm {
    /**
     * 下载确认
     *
     * @param context        上下文
     * @param iConfirmResult 对话框回调对象，
     *                       当逻辑判断需要弹框提示时，则弹框。对话框内点击 普通下载 回调接口方法
     *                       不需要弹框时直接回调接口方法
     */
    public static void showDownloadConfirmDialog(Context context, IConfirmResult iConfirmResult) {
        if (NetworkUtils.isWifiEnabled(context)) {    // WIFI连接，直接下载
            iConfirmResult.doDownload(false, true);
        } else if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) { // 用户登录且用户为免流量用户，则直接下载
            iConfirmResult.doDownload(false, true);
        } else {
            if (SharedPreferencesUtil.getInstance().isDoNotAlertAnyMore1()) {
                iConfirmResult.doDownload(false, SharedPreferencesUtil.getInstance().isUseFlowDownLoad());
            } else {
                showDownloadNotLoginAndNotWifi(context, iConfirmResult);
            }
        }
    }

    /**
     * 下载确认
     *
     * @param context        上下文
     * @param iConfirmResult 对话框回调对象，
     *                       当逻辑判断需要弹框提示时，则弹框。对话框内点击 普通下载 回调接口方法
     *                       不需要弹框时直接回调接口方法
     * @param appInfo        应用信息
     */
    public static void showDownloadConfirmDialog(Context context, IConfirmResult iConfirmResult, AppInfo appInfo) {
        if (appInfo == null)
            return;

        if (appInfo.getiFreeArea() == AppConstants.FREE_AREA_IN) {
            if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                if (TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getPhoneNumber())) {
                    // 未绑定，提示绑定免流量手机
                    AccountUtil.showNavigation(context, false);
                } else {
                    if (AccountUtil.isFreeAreaFree()) {
                        // 免流量专区免开通（运营商免开通）
                        iConfirmResult.doDownload(false, true);
                    } else {
                        if (!AppConstants.account_type.equals(AppConstants.AGENT_TYPE) && TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getReplaceDomain())) {
                            // 免流量体验特权 不可开通
                            showNotSupportFreeArea(context, iConfirmResult, false);
                        } else {
                            // 免流量体验特权 可开通，未开通
                            showNotSupportFreeArea(context, iConfirmResult, true);
                        }
                    }
                }
            } else {
                // 未登录登录，提示用户登录
                AccountUtil.userLogin(context);
            }
        } else {
            if (NetworkUtils.isWifiEnabled(context)) {    // WIFI连接，直接下载
                iConfirmResult.doDownload(false, true);
            } else if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) { // 用户登录且用户为免流量用户，则直接下载
                iConfirmResult.doDownload(false, true);
            } else {
                if (SharedPreferencesUtil.getInstance().isDoNotAlertAnyMore1()) {
                    iConfirmResult.doDownload(false, SharedPreferencesUtil.getInstance().isUseFlowDownLoad());
                } else {
                    showDownloadNotLoginAndNotWifi(context, iConfirmResult);
                }
            }
        }
    }

    // 未登录且非WIFI网络连接下开启下载提示
    public static void showDownloadNotLoginAndNotWifi(final Context context, final IConfirmResult iConfirmResult) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.app_free_flow_dialog);
        TextView textView = ButterKnife.findById(dialog, R.id.message);
        String downloadNotLogin = PersistentVar.getInstance().getSystemConfig().getDownloadNotLogin();
        String tipStr = ResUtil.getString(R.string.not_lt_is_continue);
        String clickStr = ResUtil.getString(R.string.click_here);
        String url = JsonUrl.getJsonUrl().JSON_URL_USER_ACTIVITY;
        if (!TextUtils.isEmpty(downloadNotLogin)) {
            try {
                if (downloadNotLogin.contains("|")) {
                    String[] strings = downloadNotLogin.split("\\|");
                    tipStr = strings[0];
                    url = strings[1];
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }
//        textView.setText(UrlUtils.clearBlankLine(Html.fromHtml(PersistentVar.getInstance().getSystemConfig().getDownloadNotLogin())));
        SpannableString builder = new SpannableString(tipStr + clickStr);
        builder.setSpan(new NoUnderLineClickableSpan(context, dialog, url), tipStr.length(), tipStr.length() + clickStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Button sure = ButterKnife.findById(dialog, R.id.sure);
        Button cancel = ButterKnife.findById(dialog, R.id.cancel);
        cancel.setText(R.string.formal_download);
        sure.setText(R.string.auto_download_when_wifi);

        LinearLayout checkBoxArea = ButterKnife.findById(dialog, R.id.checkbox_area);
        checkBoxArea.setVisibility(View.VISIBLE);
        final CheckBox checkBox1 = ButterKnife.findById(checkBoxArea, R.id.dialog_checkbox);
        if (checkBox1 != null) {
            checkBoxArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox1.isChecked()) {
                        checkBox1.setChecked(false);
                    } else {
                        checkBox1.setChecked(true);
                    }
                    SharedPreferencesUtil.getInstance().setDoNotAlertAnyMore1(checkBox1.isChecked());
                }
            });
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                iConfirmResult.doDismissDialog(true);
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                iConfirmResult.doDownload(true, false);
                if (checkBox1 != null && checkBox1.isChecked()) {
                    SharedPreferencesUtil.getInstance().setUseFlowDownLoad(false);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                iConfirmResult.doDownload(true, true);
                if (checkBox1 != null && checkBox1.isChecked()) {
                    SharedPreferencesUtil.getInstance().setUseFlowDownLoad(true);
                }
            }
        });
        dialog.show();
    }

    static class NoUnderLineClickableSpan extends ClickableSpan {

        Context context;
        Dialog dialog;
        String url;

        public NoUnderLineClickableSpan(Context context, Dialog dialog, String url) {
            this.context = context;
            this.dialog = dialog;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            dialog.dismiss();
            context.startActivity(WebViewActivity.newIntent(context, url));
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(ResUtil.getColor(R.color.btn_green_click));
        }
    }

    // 不支持免流量下载
    private static void showNotSupportFreeArea(final Context context, final IConfirmResult iConfirmResult, boolean support) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_free_area);
        TextView textView = ButterKnife.findById(dialog, R.id.message);
        if (support) {
            if (!TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getFlowFreeEnd())) {
                // 已过期
                textView.setText(R.string.free_area_alert3);
            } else {
                // 未开通
                textView.setText(R.string.free_area_alert2);
            }
        } else {
            // 无法开通
            textView.setText(context.getString(R.string.free_area_alert, SharedPreferencesUtil.getInstance().getArea()));
        }

        TextView sure = ButterKnife.findById(dialog, R.id.sure);
        TextView cancel = ButterKnife.findById(dialog, R.id.cancel);
        cancel.setText(R.string.btn_cancel);
        sure.setText(R.string.continue_download);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                iConfirmResult.doDownload(true, SharedPreferencesUtil.getInstance().isUseFlowDownLoad());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 对话框回调接口
     */
    public interface IConfirmResult {
        /**
         * 当需要执行下载时，回调该接口
         * 下载动作请在回调内实现
         *
         * @param isDialogResult    是否是由对话框调用
         *                          （这个值目前只由 { DownloadWidgetHelper#handleState()}使用，
         *                          用于在下载新应用或者更新应用情况下，设置该应用在My_Game数据库中isUpdate的值，
         *                          参考旧逻辑。）
         * @param isUseFlowDownLoad 无网络或者移动网络情况下弹出的是否使用流量下载的对话框的选择情况，true为使用流量下载，false为wifi情况下自动下载
         */
        void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad);


        /**
         * 当取消对话框时实现
         *
         * @param isDialogDismiss 对话框是否取消
         */
        void doDismissDialog(boolean isDialogDismiss);
    }
}
