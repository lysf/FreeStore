package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.model.UpdateModel;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.DialogTitleLayout;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FlowFreeView;
import com.snailgame.cjg.common.widget.PopupDialog;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.event.RankFilterEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.navigation.BindPhoneActivity;
import com.snailgame.cjg.seekgame.rank.adapter.CountryFilterAdapter;
import com.snailgame.cjg.util.interfaces.DeleteDownloadTaskDialogListener;
import com.snailgame.cjg.util.interfaces.UpdateCompleteDialogListener;
import com.snailgame.fastdev.util.ResUtil;
import com.zbar.lib.CaptureActivity;

import butterknife.ButterKnife;

public class DialogUtils {
    /**
     * 首页 退出弹窗
     *
     * @param context
     * @param listener
     * @return
     */
    public static PopupDialog showExistDialog(final Activity context, View.OnClickListener listener) {
        if (context == null) return null;
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_exist, null);

        TextView tvExist = ButterKnife.findById(view, R.id.exist_text);
        tvExist.setText(Html.fromHtml(PersistentVar.getInstance().getSystemConfig().getExistDialogDes()));
        ButterKnife.findById(view, R.id.exist_anyway).setOnClickListener(listener);

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);

        ButterKnife.findById(view, R.id.resume_free_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * 首页 活动推广
     *
     * @param context
     * @param activityRecommend
     * @param route
     * @return
     */
    public static PopupDialog showBannerDialog(final Activity context, final ContentModel activityRecommend, final int[] route) {
        if (context == null) return null;

        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_recomment_activity, null);
        FSSimpleImageView banner = ButterKnife.findById(view, R.id.dialog_activity_banner);
        final TextView acitivityTime = ButterKnife.findById(view, R.id.activity_time);
        TextView activity_des = ButterKnife.findById(view, R.id.activity_des);

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        ButterKnife.findById(view, R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        switch (Integer.parseInt(activityRecommend.getcSource())) {
            case AppConstants.SOURCE_COLLECTION:
                //合集
                banner.setImageUrl(activityRecommend.getsImageUrl());
                acitivityTime.setText(activityRecommend.getdCreate());
                activity_des.setText(activityRecommend.getsTitle());
                banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtil.startCollection(context, activityRecommend.getsRefId(), activityRecommend.getsTitle(), route);
                        dialog.dismiss();
                    }
                });
                break;
            case AppConstants.SOURCE_ACTIVITY:
                //活动
                banner.setImageUrl(activityRecommend.getsImageUrl());
                acitivityTime.setText(activityRecommend.getdStart());
                activity_des.setText(activityRecommend.getsSummary());
                banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtil.startWebActivity(context, activityRecommend.getsJumpUrl());
                        dialog.dismiss();
                    }
                });

                break;
        }

        return dialog;
    }

    /**
     * 首页-游戏推荐
     *
     * @param context
     * @param appInfo
     * @param route
     * @return
     */
    public static PopupDialog showRecommendGame(final Activity context, final AppInfo appInfo, final int[] route) {
        if (context == null)
            return null;

        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_recoment_game, null);
        FSSimpleImageView icon = ButterKnife.findById(view, R.id.game_icon);
        icon.setImageUrl(appInfo.getIcon());
        TextView gameName = ButterKnife.findById(view, R.id.game_name);
        gameName.setText(appInfo.getAppName());
        TextView size = ButterKnife.findById(view, R.id.game_size);
        size.setText(FileUtil.formatFileSize(context, AppInfoUtils.getPatchApkSize(appInfo)));
        TextView gameDes = ButterKnife.findById(view, R.id.game_des);
        gameDes.setText(appInfo.getsAppDesc().trim());

        FlowFreeView FlowFreeView = ButterKnife.findById(view, R.id.flow_free_container);
        FlowFreeView.setFlowFreeUI(appInfo.getcFlowFree());

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);

        ButterKnife.findById(view, R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView downloadState = ButterKnife.findById(view, R.id.download_state_download);
        if (appInfo.getDownloadState() == DownloadManager.STATUS_EXTRA_INSTALLED) {
            downloadState.setText(context.getString(R.string.btn_open));
            downloadState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadWidgetHelper.openGame(context, appInfo);
                }
            });
        } else {
            downloadState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 尝试下载统计
                    int[] dRoute = route.clone();
                    dRoute[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_DIALOGAD;
                    dRoute[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
                    dRoute[AppConstants.STATISTCS_DEPTH_NINE] = appInfo.getAppId();
                    StaticsUtils.onDownload(dRoute);
                    DownloadConfirm.showDownloadConfirmDialog(context, new DownloadConfirm.IConfirmResult() {
                        @Override
                        public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                            if (!isUseFlowDownLoad) {
                                appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                            }
                            DownloadHelper.startDownload(context, appInfo);
                        }

                        @Override
                        public void doDismissDialog(boolean isDialogDismiss) {

                        }
                    });
                    dialog.dismiss();

                }
            });
        }
        dialog.setCancelable(false);

        ButterKnife.findById(view, R.id.rl_game_detail_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_DIALOGAD;
                context.startActivity(DetailActivity.newIntent(context, appInfo.getAppId(), route));
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 分享中
     *
     * @param context
     * @return
     */
    public static Dialog showShareDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_dialog, null);
        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);
        return dialog;
    }

    // 单个按钮Dialog
    public interface ConfirmClickedLister {
        void onClicked();
    }

    /**
     * 积分介绍
     *
     * @param activity
     * @param listener
     */
    public static void showScoreHistoryHintDialog(Activity activity, final ConfirmClickedLister listener) {
        if (activity.isFinishing()) return;

        final View mView = (activity).getLayoutInflater().inflate(
                R.layout.dialog_singel_button, null);

        final Dialog mDialog = new Dialog(activity, R.style.Dialog);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.setContentView(mView);
        TextView messageView = ButterKnife.findById(mView, R.id.message);

        String hint = ResUtil.getString(R.string.score_history_hint);
        String hintTime = ResUtil.getString(R.string.score_history_hint_time);
        String hintTimeFooter = ResUtil.getString(R.string.score_history_hint_time_footer);

        String msg = hint + hintTime + hintTimeFooter;
        SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
        currencyBuilder.append(msg);
        currencyBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#eb413d")), hint.length(), hint.length() + hintTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        messageView.setText(currencyBuilder);


        Button comfirmBtn = ButterKnife.findById(mView, R.id.btn_ok);
        comfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (listener != null) {
                    listener.onClicked();
                }

            }
        });
        mDialog.show();
    }


    /**
     * 取消，确认双按钮弹窗
     *
     * @param activity
     * @param title
     * @param msgShow
     * @param listener
     */
    public static void showTwoButtonDialog(Activity activity, String title, String cancelText, String msgShow, final ConfirmClickedLister listener) {
        if (activity.isFinishing()) return;

        final View mView = (activity).getLayoutInflater().inflate(
                R.layout.dialog_two_button, null);

        final AlertDialog mDialog = new AlertDialog.Builder(
                activity).setView(mView).create();
        TextView messageView = ButterKnife.findById(mView, R.id.message);
        DialogTitleLayout dialogTitleLayout = ButterKnife.findById(mView, R.id.tv_title);
        Button comfirmBtn = ButterKnife.findById(mView, R.id.btn_ok);
        Button cancelBtn = ButterKnife.findById(mView, R.id.btn_cancel);

        if (!TextUtils.isEmpty(cancelText)) {
            cancelBtn.setText(cancelText);
        }
        if (!TextUtils.isEmpty(title)) {
            dialogTitleLayout.setText(title);
        }
        messageView.setText(msgShow);
        comfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (listener != null) {
                    listener.onClicked();
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    /**
     * 取消，确认双按钮弹窗
     *
     * @param activity
     * @param title
     * @param msgShow
     * @param listener
     */
    public static void showTwoButtonDialog(Activity activity, String title, SpannableString msgShow, final ConfirmClickedLister listener) {
        if (activity.isFinishing()) return;

        final View mView = (activity).getLayoutInflater().inflate(
                R.layout.dialog_two_button, null);

        final AlertDialog mDialog = new AlertDialog.Builder(
                activity).setView(mView).create();
        TextView messageView = ButterKnife.findById(mView, R.id.message);
        DialogTitleLayout dialogTitleLayout = ButterKnife.findById(mView, R.id.tv_title);
        Button comfirmBtn = ButterKnife.findById(mView, R.id.btn_ok);
        Button cancelBtn = ButterKnife.findById(mView, R.id.btn_cancel);
        dialogTitleLayout.setText(title);
        messageView.setText(msgShow);
        comfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (listener != null) {
                    listener.onClicked();
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    //版本更新 Util
    public static AlertDialog createUpdateProgress(Context context, String message, boolean showProgress) {
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.check_update_layout, null);
        ProgressBar progressBar = ButterKnife.findById(view, R.id.check_progress);
        TextView textView = ButterKnife.findById(view, R.id.check_text);
        textView.setText(message);
        if (!showProgress)
            progressBar.setVisibility(View.GONE);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();

        return dialog;
    }

    /**
     * 用户退出账号确认窗口
     *
     * @param context
     * @param isFinishActivity
     * @return
     */
    public static PopupDialog showUserQuitDialog(final Activity context, final boolean isFinishActivity) {
        if (context == null) return null;
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_free_area, null);

        TextView tvMessage = ButterKnife.findById(view, R.id.message);
        tvMessage.setText(R.string.account_quit_confirm);
        Button btnSure = ButterKnife.findById(view, R.id.sure);
        btnSure.setText(R.string.btn_exit);

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);

        ButterKnife.findById(view, R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ButterKnife.findById(view, R.id.sure).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AccountUtil.userLogout(context);
                dialog.dismiss();
                if (isFinishActivity) {
                    context.finish();
                }
            }
        });

        return dialog;
    }

    /**
     * 开启自动安装提示窗
     *
     * @param context
     * @return
     */
    public static PopupDialog getAppAutoInstallSettingDialog(final Activity context) {
        if (context == null) return null;

        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_auto_install, null);

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);

        ButterKnife.findById(view, R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }


    /**
     * 排行榜过滤条件弹窗
     *
     * @param context
     * @param countrys
     */
    public static void showRankFilterDialog(final Activity context, final String countrys[]) {
        if (context == null) return;
        final SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
        final String feedTypeArray[] = ResUtil.getStringArray(R.array.feed_type_array);
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.rank_filter_dialog, null);
        TextView cancle = ButterKnife.findById(view, R.id.rank_cancle);
        TextView ok = ButterKnife.findById(view, R.id.rank_submit);
        GridView feedTypeGrid = ButterKnife.findById(view, R.id.feed_type_filter_grid);
        final RankFilterEvent event = new RankFilterEvent(helper.getValue(AppConstants.RANK_FEED_NAME,context.getString(R.string.rank_best_shell)), countrys[helper.getValue(AppConstants.RANK_COUNTRY_POSITION,0)]);
        final CountryFilterAdapter feedTypeAdapter = new CountryFilterAdapter(context, feedTypeArray, helper.getValue(AppConstants.RANK_FEED_POSITION, 0));
        feedTypeGrid.setAdapter(feedTypeAdapter);
        feedTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setType(feedTypeArray[position]);
                feedTypeAdapter.notifyDataChange(position);
                helper.putValue(AppConstants.RANK_FEED_POSITION, position);
                helper.putValue(AppConstants.RANK_FEED_NAME, event.getType());
                helper.applyValue();
            }
        });
        GridView filterCountryGrid = ButterKnife.findById(view, R.id.country_filter_grid);
        final CountryFilterAdapter adapter = new CountryFilterAdapter(context, countrys, helper.getValue(AppConstants.RANK_COUNTRY_POSITION, 0));
        filterCountryGrid.setAdapter(adapter);
        filterCountryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setCountry(countrys[position]);
                adapter.notifyDataChange(position);
                helper.putValue(AppConstants.RANK_COUNTRY_POSITION, position);
                helper.putValue(AppConstants.RANK_COUNTRY_NAME, event.getCountry());
                helper.applyValue();
            }
        });
        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                event.setDismiss(true);
                MainThreadBus.getInstance().post(event);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setDismiss(true);
                MainThreadBus.getInstance().post(event);
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainThreadBus.getInstance().post(event);
                dialog.dismiss();
            }
        });
    }


    /**
     * 排行榜-预约成功提示
     *
     * @param context
     */
    public static void showOrderSuccessDialog(final Activity context) {
        if (context == null || false == ComUtil.isTopActivity(context.getComponentName().getClassName()))
            return;
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_order_success, null);

        final AlertDialog dialog = new AlertDialog.Builder(
                context).setView(view).create();

        ButterKnife.findById(view, R.id.order_success_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 首页使用，下载完成后提示更新的dialog
     */
    public static Dialog showUpdateSuccessDialog(final Activity context, String localUrl, UpdateModel.ModelItem item,
                                                 final UpdateCompleteDialogListener updateCompleteDialogListener) {

        View dialog = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_myself_update, null);
        final Dialog mUpdateDialog = new Dialog(context, R.style.Dialog);
        mUpdateDialog.setContentView(dialog);

//        //设置大小
        WindowManager.LayoutParams layoutParams = mUpdateDialog.getWindow().getAttributes();
        layoutParams.width = ComUtil.dpToPx(354);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mUpdateDialog.getWindow().setAttributes(layoutParams);

        Button btnOK = ButterKnife.findById(dialog, R.id.btn_ok);
        ImageView btnCancel = ButterKnife.findById(dialog, R.id.btn_close);
        TextView tvMessage = ButterKnife.findById(dialog, R.id.message);
        TextView versionSize = ButterKnife.findById(dialog, R.id.version_and_size);
        TextView downloadTips = ButterKnife.findById(dialog, R.id.download_tips);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = (String) v.getTag();
                updateCompleteDialogListener.onSureBtnClick(downloadUrl);
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialog.setCanceledOnTouchOutside(true);

        tvMessage.setText(item.getsDesc());
        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        downloadTips.setText(context.getString(R.string.silence_download_finish));
        downloadTips.setTextColor(ResUtil.getColor(R.color.manage_score_high));
        versionSize.setText(item.getcVersion() + " " + FileUtil.formatFileSize(context, Long.parseLong(item.getcSize())));
        btnOK.setText(context.getString(R.string.silence_install));
        btnOK.setTag(localUrl);
        return mUpdateDialog;
    }

    /**
     * 首页使用，扫描二维码完成的提示
     */
    public static AlertDialog scanSuccessDialog(final Activity context, Intent data) {
        View dialog = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.dialog_scan_finsih, null);
        final AlertDialog aDialog = new AlertDialog.Builder(context).setView(dialog).create();
        aDialog.setCanceledOnTouchOutside(true);
        Button btnCancel = ButterKnife.findById(dialog, R.id.btn_cancel);
        final TextView textView = ButterKnife.findById(dialog, R.id.result);
        Button btnOK = ButterKnife.findById(dialog, R.id.btn_ok);
        String result = "";
        if (data != null) {
            result = data.getStringExtra(CaptureActivity.SCAN_RESULT);
        }
        textView.setText(result);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComUtil.copyToClipBoard(context, textView.getText().toString());
                aDialog.dismiss();
            }
        });
        return aDialog;
    }


    /**
     * ApplistFragment使用，开通免流量的提醒
     */
    public static AlertDialog flowFreeIgnoreDialog(final Activity context) {
        final View mView = LayoutInflater.from(context).inflate(R.layout.flow_free_ignore_layout, null);
        final AlertDialog mDialog = new AlertDialog.Builder(context).setView(mView).create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        //设置立即开通 字样
        String openTxt = ResUtil.getString(R.string.flow_free_open);
        String guide1 = ResUtil.getString(R.string.flow_free_guide);
        String guide2 = ResUtil.getString(R.string.flow_free_guide_2);

        SpannableString openSpanableTxt = new SpannableString(guide1 + openTxt + guide2);
        openSpanableTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mDialog.dismiss();
                context.startActivity(WebViewActivity.newIntent(context, PersistentVar.getInstance().getSystemConfig().getFlowPrivilegeUrl()));
            }
        }, guide1.length(), guide1.length() + openTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView messageView = ButterKnife.findById(mView, R.id.message);
        View comfirmBtn = ButterKnife.findById(mView, R.id.btn_ok);
        messageView.setText(openSpanableTxt);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        comfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        return mDialog;
    }

    /**
     * DownloadManageFragment，删除下载任务dialog
     */
    public static Dialog deleteDownLoadTaskDialog(final Activity context,
                                                  final DeleteDownloadTaskDialogListener deleteDownloadTaskDialogListener) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.download_manager_delete_dialog);
        TextView textView = (TextView) dialog.findViewById(R.id.message);
        textView.setText(R.string.manage_is_delete);
        textView.setTextColor(ResUtil.getColor(R.color.primary_text_color));
        TextView textsure = (TextView) dialog.findViewById(R.id.sure);
        textsure.setText(R.string.btn_delete);
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteDownloadTaskDialogListener.onDeleteDownloadTask();
            }
        });

        return dialog;
    }

    /**
     * UnBindPhoneActivity，解除绑定免流量手机号
     */
    public static PopupDialog unbindPhoneDialog(final Activity context) {
        View view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.push_detail_dialog, null);
        DialogTitleLayout dialogTitleLayout = (DialogTitleLayout) view.findViewById(R.id.push_detail_title);
        TextView contentText = (TextView) view.findViewById(R.id.push_detail_content);
        dialogTitleLayout.setText(context.getString(R.string.unbind_tips));
        contentText.setText(context.getString(R.string.unbind_success));

        final PopupDialog dialog = new PopupDialog(context, R.style.PopupDialog);
        dialog.setContentView(view);
        view.findViewById(R.id.push_detail_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                context.setResult(Activity.RESULT_OK);
                context.finish();
            }
        });
        return dialog;
    }

    /**
     * spreeUtils，获取礼包 结果的Dialog
     */
    public static Dialog spreeResultDialog(final Activity mActivity, String msgShow, final String cdKey, boolean isSucc, String title) {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_gift_package);
        dialog.setCanceledOnTouchOutside(false);
        TextView msgTextView = ButterKnife.findById(dialog, R.id.tv_msg);
        Button lookgiftBtn = ButterKnife.findById(dialog, R.id.btn_lookGift);
        Button sureBtn = ButterKnife.findById(dialog, R.id.btn_sure);
        TextView cdKeyView = ButterKnife.findById(dialog, R.id.tv_cdkey);
        msgTextView.setText(msgShow);
        DialogTitleLayout dialogTitleLayout = ButterKnife.findById(dialog, R.id.tip_icon_layout);

        lookgiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComUtil.copyToClipBoard(mActivity, cdKey);
                dialog.dismiss();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (isSucc) {
            String headerTxt = ResUtil.getString(R.string.exchange_code);
            String currency = headerTxt + cdKey;
//            SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
//            currencyBuilder.append(currency);
//            currencyBuilder.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.red)), headerTxt.length(), currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            cdKeyView.setText(currency);
        } else {
            dialogTitleLayout.setIsRed(true, ResUtil.getString(R.string.fail));
            lookgiftBtn.setVisibility(View.GONE);
            cdKeyView.setVisibility(View.GONE);

            // add by xixh
            sureBtn.setBackgroundResource(R.drawable.btn_scan_ok_selector);
            sureBtn.setTextColor(ResUtil.getColor(R.color.white));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sureBtn.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            sureBtn.setLayoutParams(params);
        }

        dialogTitleLayout.setText(title);
        return dialog;
    }


    /**
     * spreeUtils，获取礼包 用户未绑定手机号，提示绑定手机
     */
    public static Dialog spreeNotBindPhoneDialog(final Activity mActivity, String msgShow, String title) {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_gift_package);
        dialog.setCanceledOnTouchOutside(false);
        TextView msgTextView = ButterKnife.findById(dialog, R.id.tv_msg);
        Button lookgiftBtn = ButterKnife.findById(dialog, R.id.btn_lookGift);
        Button sureBtn = ButterKnife.findById(dialog, R.id.btn_sure);
        TextView cdKeyView = ButterKnife.findById(dialog, R.id.tv_cdkey);
        msgTextView.setText(msgShow);
        DialogTitleLayout dialogTitleLayout = ButterKnife.findById(dialog, R.id.tip_icon_layout);

        lookgiftBtn.setText(R.string.goto_bind_phone);
        lookgiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(BindPhoneActivity.newIntent(mActivity));
                dialog.dismiss();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogTitleLayout.setIsRed(true, ResUtil.getString(R.string.fail));
        cdKeyView.setVisibility(View.GONE);

        dialogTitleLayout.setText(title);
        return dialog;
    }

    /**
     * SplashActivity进度提示
     */
    public static Dialog splashProgressDialog(Activity activity) {
        Dialog splashDialog = new Dialog(activity, R.style.Dialog);
        splashDialog.setContentView(R.layout.view_material_progressbar);
        splashDialog.setCanceledOnTouchOutside(false);
        Window window = splashDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        return splashDialog;
    }
}
