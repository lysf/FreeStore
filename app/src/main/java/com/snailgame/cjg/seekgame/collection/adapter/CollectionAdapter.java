package com.snailgame.cjg.seekgame.collection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;

/**
 * 合集
 */
public class CollectionAdapter extends BaseAdapter {
    protected Context mContext;

    /**
     * 数据源
     */
    private List<AppInfo> sourceAppList;

    private boolean refreshAppListItem = false;
    private int[] mRoute;

    private static final int TYPE_COUNT = 2;

    public CollectionAdapter(Context context, List<AppInfo> appList, int[] route) {
        mContext = context;
        sourceAppList = appList;
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (ListUtils.isEmpty(sourceAppList))
            return 0;
        return (sourceAppList.size() - 1) / 3 * 3;
    }

    @Override
    public AppInfo getItem(int position) {
        if (sourceAppList != null && position < (sourceAppList.size() - 1) && position >= 0)
            return sourceAppList.get(position + 1);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AppInfo currAppInfo = getItem(position);
        CollectItemViewHolder holder;
        if (convertView != null) {
            holder = (CollectItemViewHolder) convertView.getTag();
            holder.setAppInfo(currAppInfo);
        } else {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.collection_item, parent, false);
            holder = new CollectItemViewHolder(mContext, convertView, currAppInfo);
            convertView.setTag(holder);
        }

        if (currAppInfo != null) {
            setupAppListView(position, holder, currAppInfo);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 2;
                    mContext.startActivity(DetailActivity.newIntent(mContext, currAppInfo.getAppId(), route));
                }
            });
        }


        return convertView;
    }


    private void setupAppListView(int position, final CollectItemViewHolder holder, AppInfo currAppInfo) {
        int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);
        holder.appIcon.setImageUrlAndReUse(currAppInfo.getIcon());
        if (TextUtils.isEmpty(currAppInfo.getCIconLabel()))
            holder.appIconLabel.setVisibility(View.GONE);
        else {
            holder.appIconLabel.setVisibility(View.VISIBLE);
            holder.appIconLabel.setImageUrlAndReUse(currAppInfo.getCIconLabel());
        }
        holder.appTitle.setText(currAppInfo.getAppName());
        holder.appSize.setText(FileUtil.formatFileSize(mContext, currAppInfo.getApkSize()));

        holder.setAppInfo(currAppInfo);

        holder.mDownloadStateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.button.performClick();
            }
        });


        //统计相关
        setStaticInfo(position, holder, currAppInfo);
        DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
        refreshDownloadBtn(currAppInfo, holder, downloadState);
    }

    /**
     * 更改按以及进度条钮状态
     *
     * @param appInfo
     * @param holder
     * @param downloadState
     */
    private void refreshDownloadBtn(AppInfo appInfo, CollectItemViewHolder holder, int downloadState) {
        int percent = (int) appInfo.getDownloadedPercent();

        if (downloadState == DownloadManager.STATUS_RUNNING) {
            //隐藏下载按钮
            holder.button.setVisibility(View.GONE);
            holder.mDownloadProgressBar.setVisibility(View.VISIBLE);
            holder.mDownloadStateView.setVisibility(View.VISIBLE);

            holder.mDownloadProgressBar.setProgress(percent);
            holder.mDownloadProgressBar.setBackgroundResource(R.drawable.detail_progress_bar_bg);
            holder.mDownloadProgressBar.setProgressDrawable(ResUtil.getDrawable(R.drawable.detail_progress_background));

            holder.mDownloadStateView.setBackgroundColor(ResUtil.getColor(R.color.translucent_full));
            holder.mDownloadStateView.setText(percent + "%");
        } else {
            //隐藏下载进度条
            holder.mDownloadProgressBar.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
            holder.mDownloadStateView.setVisibility(View.GONE);
        }

    }


    /**
     * 跟统计相关的
     *
     * @param position
     * @param holder
     * @param currAppInfo
     */
    private void setStaticInfo(int position, CollectItemViewHolder holder, AppInfo currAppInfo) {
        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            if (route[AppConstants.STATISTCS_DEPTH_SIX] == AppConstants.STATISTCS_DEFAULT_NULL) {
                route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 2;
            } else {
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 2;
            }
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
            route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
            currAppInfo.setRoute(route);
            holder.button.setTag(R.id.tag_first, currAppInfo);
            holder.button.setTag(R.id.tag_second, position);
        }

    }

    public void refreshAppListItem(List<FreeGameItem> itemLists) {
        refreshAppListItem = true;
        FreeGameItem freeGameItem;

        int itemLength = 0;
        if (itemLists != null) itemLength = itemLists.size();

        if (sourceAppList != null) {
            for (AppInfo appInfo : sourceAppList) {
                appInfo.setcFlowFree(AppInfo.FREE_NULL);
                for (int i = 0; i < itemLength; i++) {
                    freeGameItem = itemLists.get(i);
                    if (appInfo.getPkgName().equals(freeGameItem.getcPackage()))
                        appInfo.setcFlowFree(freeGameItem.getcFlowFree());
                }
            }
        }

        notifyDataSetChanged();
    }


    public void resetIfreeFlow() {
        if (refreshAppListItem) {
            for (AppInfo appInfo : sourceAppList)
                appInfo.setcFlowFree(appInfo.getOriginCFlowFree());
            notifyDataSetChanged();
        }
    }


    public void refreshData(List<AppInfo> appInfos) {
        sourceAppList = appInfos;
        notifyDataSetChanged();
    }


    class CollectItemViewHolder extends DownloadViewHolder {
        @Bind(R.id.app_icon)
        FSSimpleImageView appIcon;
        @Bind(R.id.app_icon_label)
        FSSimpleImageView appIconLabel;
        @Bind(R.id.app_title)
        TextView appTitle;
        @Bind(R.id.app_size)
        TextView appSize;

        @Bind(R.id.pb_detail_download)
        ProgressBar mDownloadProgressBar;
        @Bind(R.id.tv_state_download)
        TextView mDownloadStateView;
        public View itemView;

        public CollectItemViewHolder(Context context, View itemView, AppInfo appInfo) {
            super(context, itemView, appInfo);
            this.itemView = itemView;
        }
    }
}
