package com.snailgame.cjg.downloadmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.FileUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * 已安装应用Adapter
 */
public class InstalledAppAdapter extends BaseAdapter implements ImpRefresh<List<AppInfo>> {
    private Context mContext;
    private List<AppInfo> mInstalledAppList;
    private boolean isActionMode;

    public InstalledAppAdapter(Context context, List<AppInfo> installedAppList) {
        mContext = context;
        mInstalledAppList = installedAppList;
    }

    public boolean isActionMode() {
        return isActionMode;
    }

    public void setActionMode(boolean isActionMode) {
        this.isActionMode = isActionMode;
    }

    @Override
    public int getCount() {

        if (mInstalledAppList == null) {
            return 0;
        }
        return mInstalledAppList.size();
    }

    @Override
    public AppInfo getItem(int position) {

        if (mInstalledAppList == null || position >= mInstalledAppList.size()) {
            return null;
        }
        return mInstalledAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup arg2) {

        View view;
        ViewHolder holder = null;
        if (convertview == null) {
            view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.installed_listview_item, null);
            if (view != null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
        } else {
            view = convertview;
            holder = (ViewHolder) convertview.getTag();
        }


        final AppInfo appInfo = getItem(position);
        if (appInfo != null) {
            if (holder.appIcon != null) {
                holder.appIcon.setImageUrlAndReUse(appInfo.getIcon());
            }
            if (holder.tvAppLabel != null) {
                holder.tvAppLabel.setText(appInfo.getAppName());
            }

            String versionStr = appInfo.getLocalAppVersion();
            if (versionStr != null && versionStr.length() > 6)
                versionStr = versionStr.substring(0, 6);

            if (holder.tvVersionSize != null) {
                holder.tvVersionSize.setText(String.valueOf(FileUtil.formatFileSize(mContext, appInfo.getApkSize())));
            }
            if (holder.deleteBtn != null) {
                holder.deleteBtn.setTag(appInfo.getPkgName());
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uninstallApp(appInfo.getPkgName());

                    }
                });
            }

            if (isActionMode()) {
                holder.lv_installed_listview_item.setBackgroundResource(appInfo.isChecked()
                        ? R.drawable.app_list_item_selected : R.drawable.list_item_selector);

                holder.deleteBtn.setClickable(false);
            } else {
                appInfo.setChecked(false);
                holder.deleteBtn.setClickable(true);
                holder.lv_installed_listview_item.setBackgroundResource(R.drawable.list_item_selector);
            }
        }

        return view;
    }

    private void uninstallApp(String url) {
        Uri packageURI = Uri.fromParts("package", url, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        mContext.startActivity(intent);
    }

    public void uninstallApps() {
        if (mInstalledAppList != null) {
            for (AppInfo appInfo : mInstalledAppList) {
                if (appInfo.isChecked()) {
                    uninstallApp(appInfo.getPkgName());
                }
            }
        }
    }

    /**
     * 清理所有应用勾选状态为未勾选
     */
    public void clearCheckStatus() {
        if (mInstalledAppList != null) {
            for (AppInfo appInfo : mInstalledAppList) {
                appInfo.setChecked(false);
            }
        }
    }

    @Override
    public void refreshData(List<AppInfo> appInfos) {
        mInstalledAppList = appInfos;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.lv_installed_listview_item)
        LinearLayout lv_installed_listview_item;
        @Bind(R.id.imgApp)
        FSSimpleImageView appIcon;
        @Bind(R.id.tvAppLabel)
        TextView tvAppLabel;
        @Bind(R.id.tvVersionSize)
        TextView tvVersionSize;
        @Bind(R.id.btn_delete)
        View deleteBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
