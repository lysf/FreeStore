/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snailgame.cjg.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FlowFreeView;
import com.snailgame.cjg.event.AppAppointmentEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 拆分DownloadViewHolder的UI和逻辑部分
 * 1. 原有UI部分共用Views独立成父类（即当前类），提供所有使用类似下载功能的视图继承
 * 2. 逻辑部分独立成{@link DownloadWidgetHelper}工具类，处理下载相关逻辑
 * <p>
 * Created by andy on 13-7-25.
 */
public class DownloadViewHolder implements View.OnClickListener, IDownloadStateSwitchable {
    private Context mContext;

    @Nullable
    @Bind(R.id.download_state_download)
    public TextView button; // 下载按钮
    @Nullable
    @Bind(R.id.download_state_download_image)
    public ImageView iv_control;//下载状态切换
    @Nullable
    @Bind(R.id.loading_for_btn)
    public ProgressBar loadingForBtn; // 下载按钮切换过程中的加载图片
    @Nullable
    @Bind(R.id.appinf_size)
    public TextView tvAppSize;
    @Nullable
    @Bind(R.id.appinfo_version)
    public TextView tvAppInfo; // 应用信息
    @Nullable
    @Bind(R.id.pb_download_percent)
    public ProgressBar pbDownload; // 下载进度
    @Nullable
    @Bind(R.id.tv_download_speed)
    public TextView tvDownloadSpeed; // 下载速度
    @Nullable
    @Bind(R.id.tv_download_size)
    public TextView tvDownloadedSize; // 下载大小、文件大小
    @Nullable
    @Bind(R.id.flow_free_container)
    public FlowFreeView flowFreeView; //免流量标志

    public AppInfo appInfo;

    public static final String APP_APPOINTMENT_N = "0";  //应用不显示预约
    public static final String APP_APPOINTMENT_Y = "1";  //应用显示预约

    /**
     * 初始化父类View内基本的子View，用于逻辑方法调用
     *
     * @param parentView 当前使用的View容器
     */
    public DownloadViewHolder(Context context, View parentView, AppInfo appInfo) {
        this.mContext = context;
        this.appInfo = appInfo;
        ButterKnife.bind(this, parentView);
        if (button != null) {
            button.setOnClickListener(this);
        }
        if (iv_control != null) {
            iv_control.setOnClickListener(this);
        }
        loadingForBtn.setOnClickListener(this);
    }


    /**
     * 初始化父类View内基本的子View，用于逻辑方法调用
     *
     * @param parentView 当前使用的View容器
     */
    public DownloadViewHolder(Context context, View parentView) {
        this.mContext = context;
        ButterKnife.bind(this, parentView);
        if (button != null) {
            button.setOnClickListener(this);
        }
        if (iv_control != null) {
            iv_control.setOnClickListener(this);
        }
        loadingForBtn.setOnClickListener(this);
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        if (button != null)
            button.setTag(R.id.tag_first, appInfo);
    }

    /**
     * 点击下载按钮的执行逻辑
     * 通过调用DownloadWidgetHelper处理
     *
     * @param view 当前下载的按钮
     */
    @Override
    public void onClick(View view) {
        AppInfo currAppInfo = view.getTag(R.id.tag_first) == null ? null : (AppInfo) view.getTag(R.id.tag_first);
        if (currAppInfo != null) {
            if (!TextUtils.isEmpty(currAppInfo.getAppointmentStatus()) && currAppInfo.getAppointmentStatus().equals(APP_APPOINTMENT_Y)) {
                if (!currAppInfo.isHasAppointment()) {

                    if (!IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                        AccountUtil.userLogin(mContext);
                        return;
                    }
                    MainThreadBus.getInstance().post(new AppAppointmentEvent(currAppInfo.getAppId()));
                }
            } else {
                DownloadWidgetHelper.getHelper().handleState(mContext, this, currAppInfo);
            }
        }
    }

    /**
     * 显示App介绍信息等，隐藏进度栏、下载速度及大小
     */
    private void showAppVer() {
        if (tvAppSize != null && pbDownload != null) {
            tvAppSize.setVisibility(View.VISIBLE);
            tvDownloadSpeed.setVisibility(View.GONE);
            tvDownloadedSize.setVisibility(View.GONE);
            pbDownload.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏App大小等，显示进度栏、下载速度及大小
     */
    private void hideAppVer() {
        if (tvAppSize != null && pbDownload != null) {
            tvAppSize.setVisibility(View.GONE);
            tvDownloadSpeed.setVisibility(View.VISIBLE);
            tvDownloadSpeed.setTextColor(ResUtil.getColor(R.color.download_text_color));
            tvDownloadSpeed.getPaint().setFakeBoldText(true);
            tvDownloadedSize.setVisibility(View.VISIBLE);
            pbDownload.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏App大小、示进度栏、下载速度及大小，
     */
    private void showWaitingForWifi() {
        if (tvAppSize != null && pbDownload != null) {
            tvAppSize.setVisibility(View.GONE);
            tvDownloadSpeed.setVisibility(View.VISIBLE);
            tvDownloadSpeed.setText(R.string.download_waitfor_wifi);
            tvDownloadSpeed.setTextColor(ResUtil.getColor(R.color.download_manage_item_version));
            tvDownloadSpeed.getPaint().setFakeBoldText(false);
            tvDownloadedSize.setVisibility(View.GONE);
            pbDownload.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏App大小、示进度栏、下载速度及大小，显示已暂停
     */
    private void showPause() {
        if (tvAppSize != null && pbDownload != null) {
            tvAppSize.setVisibility(View.GONE);
            tvDownloadSpeed.setVisibility(View.VISIBLE);
            tvDownloadSpeed.setTextColor(ResUtil.getColor(R.color.download_manage_item_version));
            tvDownloadSpeed.setText(R.string.download_pause);
            tvDownloadSpeed.getPaint().setFakeBoldText(false);
            tvDownloadedSize.setVisibility(View.VISIBLE);
            pbDownload.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设定下载按钮的显示状态
     *
     * @param textRes 文本资源值
     * @param btnRes  按钮背景资源值
     */
    private void setDownloadButtonState(int textRes, int btnRes) {
        if (button != null) {
            button.setText(textRes);
            button.setBackgroundResource(btnRes);
            button.setClickable(true);
            button.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下载图片切换
     *
     * @param imageRes 图片资源（根据状态设置）
     */
    private void setDownloadButtonState(int imageRes) {
        if (iv_control != null) {
            iv_control.setImageResource(imageRes);
            iv_control.setClickable(true);
            iv_control.setVisibility(View.VISIBLE);
        }
    }

    private boolean isHideAppSize(AppInfo currAppInfo) {
        // 免流量专区不显示
        if (currAppInfo.getiFreeArea() == AppConstants.FREE_AREA_IN) {
            return true;
        }
        if (IdentityHelper.isLogined(mContext) && AccountUtil.isFree() && AppInfo.isFree(currAppInfo.getcFlowFree())) {
            return !(SharedPreferencesUtil.getInstance().isSnailPhoneNumber() &&
                    (currAppInfo.getcFlowFree().equals(AppInfo.FREE_PLAY) ||
                            currAppInfo.getcFlowFree().equals(AppInfo.FREE_PLAY_AREA)));
        }
        return false;
    }


    /**
     * 设置下载速度
     *
     * @param currAppInfo
     */
    private void setupDownloadspeed(AppInfo currAppInfo) {
        if (currAppInfo == null)
            return;
        if (pbDownload != null) {
            pbDownload.setProgress((int) currAppInfo.getDownloadedPercent());
        }

        if (tvDownloadSpeed != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String s = currAppInfo.getDownloadSpeed();
            stringBuilder.append(s).append("/S");
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                    0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDownloadSpeed.setText(stringBuilder);
        }

        if (tvDownloadedSize != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String s = FileUtil.formatFileSize(mContext, currAppInfo.getDownloadedSize());
            String s1 = FileUtil.formatFileSize(mContext, AppInfoUtils.getPatchApkSize(currAppInfo));
            stringBuilder.append(s).append("/").append(s1);
//            stringBuilder.setSpan(new ForegroundColorSpan(Typeface.BOLD),
//                    0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.have_updated_color)),
                    s.length() + 1, s.length() + s1.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDownloadedSize.setText(stringBuilder);
        }
    }


    @Override
    public void setFlowFreeView(boolean isShow) {
        if (isShow) {
            if (flowFreeView != null) {
                //免标志
                flowFreeView.setFlowFreeUI(appInfo);
                flowFreeView.setVisibility(View.VISIBLE);
            }
            if (tvDownloadSpeed != null)
                tvDownloadSpeed.setVisibility(View.GONE);
            if (tvDownloadedSize != null)
                tvDownloadedSize.setVisibility(View.GONE);
            if (pbDownload != null)
                pbDownload.setVisibility(View.GONE);
            //应用大小
            if (tvAppSize != null) {
                tvAppSize.setText(FileUtil.formatFileSize(mContext, AppInfoUtils.getPatchApkSize(appInfo)));
                boolean isHideAppSize = isHideAppSize(appInfo) && flowFreeView != null;
                tvAppSize.setVisibility(isHideAppSize ? View.GONE : View.VISIBLE);
            }
        } else {
            if (flowFreeView != null)
                flowFreeView.setVisibility(View.GONE);
            if (tvAppSize != null)
                tvAppSize.setVisibility(View.GONE);
            if (tvDownloadSpeed != null)
                tvDownloadSpeed.setVisibility(View.VISIBLE);
            if (tvDownloadedSize != null)
                tvDownloadedSize.setVisibility(View.VISIBLE);
            if (pbDownload != null)
                pbDownload.setVisibility(View.VISIBLE);
            setupDownloadspeed(appInfo);
        }
    }


    @Override
    public void setAppointmentState() {
        if (appInfo != null && APP_APPOINTMENT_Y.equals(appInfo.getAppointmentStatus())) {
            if (!appInfo.isHasAppointment()) {
                setDownloadButtonState(R.string.rank_order, R.drawable.btn_green_selector);
            } else {
                setDownloadButtonState(R.string.rank_ordered, R.drawable.btn_grey_selector);
            }
            showAppVer();
        }


    }

    @Override
    public void switchToDownload() {
        if (button != null) {
            setDownloadButtonState(R.string.btn_download, R.drawable.btn_green_selector);
            showAppVer();
        }
    }

    @Override
    public void switchToWaiting() {
        setDownloadButtonState(R.string.btn_waiting, R.drawable.btn_grey_selector);
        setDownloadButtonState(R.drawable.iv_down_pause);
        hideAppVer();
    }

    @Override
    public void switchToWaitingForWifi() {
        setDownloadButtonState(R.string.btn_continue, R.drawable.btn_yellow_selector);
        setDownloadButtonState(R.drawable.iv_down_continue);
        showWaitingForWifi();
    }

    @Override
    public void switchToPause() {
        setDownloadButtonState(R.string.btn_pause, R.drawable.btn_grey_selector);
        setDownloadButtonState(R.drawable.iv_down_pause);
        hideAppVer();
    }

    @Override
    public void switchToContinue() {
        setDownloadButtonState(R.string.btn_continue, R.drawable.btn_yellow_selector);
        setDownloadButtonState(R.drawable.iv_down_continue);
        showPause();
    }

    @Override
    public void switchToInstall() {
        setDownloadButtonState(R.string.btn_install, R.drawable.btn_green_selector);
        showAppVer();
    }

    @Override
    public void switchToOpen() {
        setDownloadButtonState(R.string.btn_open, R.drawable.btn_blue_selector);
        showAppVer();
    }

    @Override
    public void switchToUpgrade() {
        setDownloadButtonState(R.string.btn_upgrade, R.drawable.btn_green_selector);
        showAppVer();
    }

    @Override
    public void switchToRedownload() {
        setDownloadButtonState(R.string.btn_fail, R.drawable.btn_green_selector);
        showAppVer();
    }

    @Override
    public void switchToInstalling() {
        setDownloadButtonState(R.string.btn_installing, R.drawable.btn_green_selector);
        showAppVer();
    }

    @Override
    public void switchToPatching() {
        setDownloadButtonState(R.string.btn_patching, R.drawable.btn_grey_selector);
        showAppVer();

    }

    @Override
    public void switchToNotReady() {
        setDownloadButtonState(R.string.btn_notready, R.drawable.btn_grey_selector);
        showAppVer();
    }

    @Override
    public void switching() {
        if (button != null) {
            button.setClickable(false);
            button.setVisibility(View.GONE);
        }
        if (iv_control != null) {
            iv_control.setClickable(false);
//            iv_control.setVisibility(View.GONE);
        }
        if (loadingForBtn != null) loadingForBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void switched() {
        if (loadingForBtn != null) loadingForBtn.setVisibility(View.GONE);
    }

    @Override
    public void setState(int state) {
        if (button != null) {
            button.setId(state);
        }
        if (iv_control != null) {
            iv_control.setId(state);
        }
    }

    @Override
    public int getState() {
        return button != null ? button.getId() : iv_control.getId();
    }
}

