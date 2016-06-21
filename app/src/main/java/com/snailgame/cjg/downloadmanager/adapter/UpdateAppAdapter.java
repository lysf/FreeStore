package com.snailgame.cjg.downloadmanager.adapter;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.event.IgnoreUpgradeEvent;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.DownloadConfirm.IConfirmResult;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SpreeUtils;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.slideexpandable.ExpandCollapseAnimation;

/**
 * Created by shenzaih on 14-2-15.
 */
public class UpdateAppAdapter extends BaseAdapter implements ImpRefresh<List<AppInfo>> {

    public Activity mContext;
    public List<AppInfo> mUpgradableAppList;
    private String sizeText;
    private boolean isIgnore;

    public UpdateAppAdapter(Activity mContext, List<AppInfo> upgradableAppList, boolean isIgnore) {
        this.mContext = mContext;
        this.mUpgradableAppList = upgradableAppList;
        sizeText = ResUtil.getString(R.string.detail_upgrade_size);
        this.isIgnore = isIgnore;
    }

    @Override
    public int getCount() {
        if (mUpgradableAppList == null) {
            return 0;
        }
        return mUpgradableAppList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        if (mUpgradableAppList == null || mUpgradableAppList.size() <= position) {
            return null;
        }
        return mUpgradableAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder = null;
        if (convertView == null) {
            view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.update_listview_item, parent, false);
            if (view != null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        final AppInfo appInfo = getItem(position);
        if (appInfo != null) {
            setupAppInfo(holder, appInfo);
            if (isIgnore) {
                setIgnoreView(holder, appInfo, position);
            } else {
                setupDownloadView(holder, appInfo);
            }
            setupUpgradeView(holder, appInfo, position);
        }

        return view;
    }

    private void setupAppInfo(ViewHolder holder, AppInfo appInfo) {
        if (holder.appIcon != null) {
            holder.appIcon.setImageUrlAndReUse(appInfo.getIcon());
        }
        if (holder.tvAppLabel != null) {
            holder.tvAppLabel.setText(appInfo.getAppName());
        }

        if (holder.tvVersionSize != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sizeText);
            if (appInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH) {
                String apkSize = FileUtil.formatFileSize(mContext, appInfo.getApkSize());
                stringBuilder.append(apkSize);
                stringBuilder.setSpan(new StrikethroughSpan(),
                        sizeText.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(" ").append(FileUtil.formatFileSize(mContext, appInfo.getDiffSize()));
                stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)),
                        sizeText.length() + apkSize.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                stringBuilder.append(FileUtil.formatFileSize(mContext, appInfo.getApkSize()));
                if (stringBuilder.length() > sizeText.length()) {
                    stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)),
                            sizeText.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            holder.tvVersionSize.setText(stringBuilder);

            String arrowText = " --> ";
            stringBuilder = new SpannableStringBuilder(AppConstants.APP_VERSION_FLAG +
                    appInfo.getInstalledApkVersionName() + arrowText + AppConstants.APP_VERSION_FLAG + appInfo.getVersionName());
            if (appInfo.getVersionName() != null && !TextUtils.isEmpty(appInfo.getVersionName())) {
                stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)),
                        appInfo.getInstalledApkVersionName().length() + arrowText.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.tvNewVersion.setText(stringBuilder);
        }
    }

    private void setIgnoreView(ViewHolder holder, final AppInfo appInfo, final int position) {
        holder.updateText.setText(mContext.getString(R.string.list_item_ignore_cancel));
        holder.updateText.setBackgroundResource(R.drawable.btn_green_selector);
        holder.updateText.setClickable(true);

        if (holder.updateText != null) {
            holder.updateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpgradableAppList.remove(position);
                    notifyDataSetChanged();
                    MyGameDaoHelper.updateUpgradeIgnoreCode(mContext, appInfo.getPkgName(), AppConstants.INVALID_UPDATE_IGNORE);
                    if (ListUtils.isEmpty(mUpgradableAppList)) {
                        mContext.finish();
                    }
                    MainThreadBus.getInstance().post(new UpdateChangeEvent());
                }
            });
        }
    }

    private void setupDownloadView(ViewHolder holder, final AppInfo appInfo) {
        if (!appInfo.isDownloading()) {
            holder.updateText.setText(mContext.getString(R.string.list_item_update));
            holder.updateText.setBackgroundResource(R.drawable.btn_green_selector);
            holder.updateText.setClickable(true);
        } else {
            holder.updateText.setText(mContext.getString(R.string.updating_text));
            holder.updateText.setBackgroundResource(R.drawable.btn_grey_selector);
            holder.updateText.setClickable(false);
        }
        if (holder.updateText != null) {
            holder.updateText.setTag(appInfo.getPkgName());
            holder.updateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadConfirm.showDownloadConfirmDialog(mContext, new IConfirmResult() {
                        @Override
                        public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                            if (!isUseFlowDownLoad) {
                                appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                            }
                            DownloadHelper.startDownload(mContext, appInfo);
                            MainThreadBus.getInstance().post(new UpdateChangeEvent());
                        }

                        @Override
                        public void doDismissDialog(boolean isDialogDismiss) {

                        }
                    });
                }
            });
        }
    }

    /**
     * ���� ����� ��Ϣ
     *
     * @param holder
     * @param appInfo
     * @param position
     */
    private void setupUpgradeView(final ViewHolder holder, final AppInfo appInfo, final int position) {
        holder.upgradeInfoView.setText(TextUtils.isEmpty(appInfo.getUpgradeDesc()) ?
                ResUtil.getString(R.string.upgrade_des_empty) : appInfo.getUpgradeDesc());

        boolean isShowExpand = appInfo.isShowExpand();
        holder.expandableContainer.setVisibility(isShowExpand ? View.VISIBLE : View.GONE);
        showUpgradeTitleView(appInfo, holder);
        holder.upgradeContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpreeUtils.animateView(holder.expandableContainer, appInfo.isShowExpand() ?
                        ExpandCollapseAnimation.COLLAPSE : ExpandCollapseAnimation.EXPAND);
                appInfo.setShowExpand(!appInfo.isShowExpand());
            }
        });


        holder.expandableContainer.setTag(R.id.tag_animation, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.upgradeTiTleView.setText(ResUtil.getString(R.string.upgrade_content_title));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showUpgradeTitleView(appInfo, holder);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (isIgnore) {
            holder.ignoreView.setVisibility(View.GONE);
        } else {
            holder.ignoreView.setVisibility(View.VISIBLE);
            holder.ignoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //������ version code ������ݿ��� ���Ƴ�
                    mUpgradableAppList.remove(position);
                    notifyDataSetChanged();
                    MyGameDaoHelper.updateUpgradeIgnoreCode(mContext, appInfo.getPkgName(), appInfo.getVersionCode());
                    MainThreadBus.getInstance().post(new IgnoreUpgradeEvent());
                }
            });
        }

    }

    private void showUpgradeTitleView(AppInfo appInfo, ViewHolder holder) {
        if (appInfo.isShowExpand()) {
            holder.upgradeTiTleView.setText(ResUtil.getString(R.string.upgrade_content_title));
            holder.arrowView.setImageResource(R.drawable.ic_extend_up);
        } else {
            holder.upgradeTiTleView.setText(ResUtil.getString(R.string.upgrade_content_title) +
                    (TextUtils.isEmpty(appInfo.getUpgradeDesc()) ? ResUtil.getString(R.string.upgrade_des_empty) : appInfo.getUpgradeDesc()));
            holder.arrowView.setImageResource(R.drawable.ic_extend_down);
        }
    }

    public List<AppInfo> getUpdateList() {
        return mUpgradableAppList;
    }

    @Override
    public void refreshData(List<AppInfo> appInfos) {
        mUpgradableAppList = appInfos;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.imgApp)
        FSSimpleImageView appIcon;
        @Bind(R.id.tvAppLabel)
        TextView tvAppLabel;
        @Bind(R.id.tvVersionSize)
        TextView tvVersionSize;
        @Bind(R.id.tvNewVersion)
        TextView tvNewVersion;
        @Bind(R.id.text_update)
        TextView updateText;
        @Bind(R.id.arrows_img)
        ImageView arrowView;

        @Bind(R.id.upgrade_container)
        View upgradeContainerView;

        @Bind(R.id.expandableContainer)
        View expandableContainer;

        @Bind(R.id.tv_upgrade_info)
        TextView upgradeInfoView;

        @Bind(R.id.tv_upgrade_title)
        TextView upgradeTiTleView;
        @Bind(R.id.tv_ignore)
        TextView ignoreView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
