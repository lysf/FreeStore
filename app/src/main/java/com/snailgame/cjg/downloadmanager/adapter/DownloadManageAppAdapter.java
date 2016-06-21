package com.snailgame.cjg.downloadmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 游戏管理 -> 下载管理 模块专用
 */
public class DownloadManageAppAdapter extends BaseAdapter implements ImpRefresh<List<AppInfo>> {
    private Activity mContext;
    private List<AppInfo> sourceAppList;
    private boolean isActionMode;

    public DownloadManageAppAdapter(Activity context, List<AppInfo> appInfos) {
        mContext = context;
        sourceAppList = appInfos;
        if(sourceAppList == null){
            sourceAppList = new ArrayList<AppInfo>();
        }
    }

    public boolean isActionMode() {
        return isActionMode;
    }

    public void setActionMode(boolean isActionMode) {
        this.isActionMode = isActionMode;
    }

    @Override
    public int getCount() {
        if(sourceAppList==null)return 0;
        return sourceAppList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        if(sourceAppList!=null&&position<sourceAppList.size())
            return sourceAppList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final DownloadListViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(FreeStoreApp.getContext()).inflate( R.layout.app_downloading_list_item, parent, false);
            holder = new DownloadListViewHolder(mContext, view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (DownloadListViewHolder) convertView.getTag();
        }

        AppInfo currAppInfo = getItem(position);

        if (currAppInfo != null) {
            if (holder.ivAppLogo != null) {
                holder.ivAppLogo.setImageUrlAndReUse(currAppInfo.getIcon());
            }

            if (holder.tvAppLabel != null) {
                holder.tvAppLabel.setText(currAppInfo.getAppName());
            }

            int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);

            DownloadWidgetHelper.getHelper().switchState(downloadState, holder);

            if (holder.button != null) {
                int[] route = createRoute();
                route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
                currAppInfo.setRoute(route);
                holder.button.setTag(R.id.tag_first, currAppInfo);
            }

            if (holder.pbDownload != null) {
                holder.pbDownload.setVisibility(View.GONE);
            }
            if (holder.tvDownloadedSize != null) {
                holder.tvDownloadedSize.setVisibility(View.GONE);
            }

            // refactor
            int colorRes = R.color.download_text_color;
            int txtRes = -1;
            int textSize = 10;

            switch (downloadState) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    textSize = 14;
                    colorRes = R.color.download_manage_item_version;
                    txtRes = R.string.download_state_not_install;
                    break;
                case DownloadManager.STATUS_INSTALLING:
                    textSize = 14;
                    colorRes = R.color.download_manage_item_version;
                    txtRes = R.string.download_state_installing;
                    break;
                case DownloadManager.STATUS_PATCHING:
                    textSize = 14;
                    colorRes = R.color.download_manage_item_version;
                    txtRes = R.string.download_state_patching;
                    break;
                case DownloadManager.STATUS_EXTRA_INSTALLED:
                    textSize = 14;
                    colorRes = R.color.download_text_color;
                    txtRes = R.string.download_state_installed;
                    break;
                case DownloadManager.STATUS_FAILED:
                    textSize = 14;
                    colorRes = R.color.alert;
                    txtRes = R.string.download_state_fail;
                    break;
                case DownloadManager.STATUS_PENDING_FOR_WIFI:
                    textSize = 14;
                    colorRes = R.color.download_manage_item_version;
                    txtRes = R.string.download_waitfor_wifi;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    colorRes = R.color.download_manage_item_version;
                    txtRes = R.string.download_pause;
                default:
                    if (holder.pbDownload != null) {
                        holder.pbDownload.setVisibility(View.VISIBLE);
                        holder.pbDownload.setProgress((int)currAppInfo.getDownloadedPercent());
                    }
                    if (holder.tvDownloadedSize != null) {
                        holder.tvDownloadedSize.setVisibility(View.VISIBLE);
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                        String s = FileUtil.formatFileSize(mContext, currAppInfo.getDownloadedSize());
                        String s1 = FileUtil.formatFileSize(mContext, currAppInfo.getApkSize());
                        stringBuilder.append(s).append("/").append(s1);
                        stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.download_manage_item_version)),
                                s.length() + 1, s.length() + s1.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.tvDownloadedSize.setText(stringBuilder);
                    }
                    break;
            }
            holder.tvDownloadSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, ComUtil.sp2px(textSize, mContext));
            if (holder.tvDownloadSpeed != null) {
                holder.tvDownloadSpeed.setTextColor(ResUtil.getColor(colorRes));
                if(txtRes==-1){
                    holder.tvDownloadSpeed.setText(currAppInfo.getDownloadSpeed() + "/S");
                    holder.tvDownloadSpeed.getPaint().setFakeBoldText(true);
                }else{
                    holder.tvDownloadSpeed.setText(ResUtil.getString(txtRes));
                    holder.tvDownloadSpeed.getPaint().setFakeBoldText(false);
                }
            }
            view.setId(currAppInfo.getAppId());

            if (isActionMode()) {
                holder.item_main.setBackgroundResource(currAppInfo.isChecked()
                        ? R.drawable.app_list_item_selected :R.drawable.list_item_selector);

                holder.button.setClickable(false);
                holder.buttonClick.setClickable(false);
            } else {
                currAppInfo.setChecked(false);
                holder.button.setClickable(true);
                holder.buttonClick.setClickable(true);
                holder.item_main.setBackgroundResource(R.drawable.list_item_selector);
            }

        }


        return view;
    }

    /**
     * 清理所有应用勾选状态为未勾选
     */
    public void clearCheckStatus() {
        if (sourceAppList != null) {
            for (AppInfo appInfo : sourceAppList) {
                appInfo.setChecked(false);
            }
        }
    }

    @Override
    public void refreshData(List<AppInfo> appInfos) {
        sourceAppList = appInfos;
        if(sourceAppList == null){
            sourceAppList = new ArrayList<AppInfo>();
        }
        notifyDataSetChanged();
    }

    public static class DownloadListViewHolder extends DownloadViewHolder implements View.OnClickListener {
        @Bind(R.id.app_logo)
        FSSimpleImageView ivAppLogo;
        @Bind(R.id.app_title)
        TextView tvAppLabel;
        @Bind(R.id.item_main)
        RelativeLayout item_main;
        @Bind(R.id.lv_download_container)
        View buttonClick;

        public DownloadListViewHolder(Context mContext, View parentView) {
            super(mContext, parentView);
            if (buttonClick != null) {
                buttonClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.performClick();
                    }
                });
            }
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 游戏管理
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_MANAGE,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_EIGHTH_LIST,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }
}
