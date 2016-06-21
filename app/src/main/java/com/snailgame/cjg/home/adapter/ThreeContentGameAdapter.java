package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.holder.ThreeContentGameHolder;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxy on 2015/4/15.
 */
public class ThreeContentGameAdapter extends BaseAdapter implements View.OnClickListener {
    private static final int PER_ROW_COUNT = 3;//每行显示3个
    private Activity mContext;
    private LayoutInflater inflater;
    private List<AppInfo> sourceAppList;
    private int[] mRoute;

    public ThreeContentGameAdapter(Activity context, ModuleModel moduleModel, List<AppInfo> gameLists, int[] route) {
        mContext = context;
        mRoute = route;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());

        int templateId = -1;
        String templateIdString = moduleModel.getcTemplateId();
        if (!TextUtils.isEmpty(templateIdString) && TextUtils.isDigitsOnly(templateIdString))
            templateId = Integer.parseInt(templateIdString);

        sourceAppList = new ArrayList<>();
        for (AppInfo appInfo : gameLists) {
            if (appInfo.getcMainType() == templateId)
                sourceAppList.add(appInfo);
        }
    }

    @Override
    public int getCount() {
        if (ListUtils.isEmpty(sourceAppList))
            return 0;
        if (sourceAppList.size() % PER_ROW_COUNT == 0)
            return sourceAppList.size() / PER_ROW_COUNT;
        return sourceAppList.size() / PER_ROW_COUNT + 1;
    }

    @Override
    public AppInfo getItem(int position) {
        if (sourceAppList != null && position < sourceAppList.size())
            return sourceAppList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = getPosition(position);
        View view = inflater.inflate(R.layout.three_content_game_layout, parent, false);
        View contentView;
        //左边的应用
        final AppInfo currAppInfoLeft = getItem(position);
        if (currAppInfoLeft != null) {
            contentView = view.findViewById(R.id.three_content_game_lef);
            contentView.setVisibility(View.VISIBLE);
            refreshItemView(new ThreeContentGameHolder(mContext, contentView), currAppInfoLeft, position);
        }
        //中间的应用
        final AppInfo currAppInfoCenter = getItem(++position);
        if (currAppInfoCenter != null) {
            contentView = view.findViewById(R.id.three_content_game_center);
            contentView.setVisibility(View.VISIBLE);
            refreshItemView(new ThreeContentGameHolder(mContext, contentView), currAppInfoCenter, position);
        }
        //右边的应用
        final AppInfo currAppInfoRight = getItem(++position);
        if (currAppInfoRight != null) {
            contentView = view.findViewById(R.id.three_content_game_right);
            contentView.setVisibility(View.VISIBLE);
            refreshItemView(new ThreeContentGameHolder(mContext, contentView), currAppInfoRight, position);
        }
        return view;
    }

    private int getPosition(int position) {
        return position * PER_ROW_COUNT;
    }

    public void refreshItemView(ThreeContentGameHolder holder, AppInfo currAppInfo, int position) {
        int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);
        showContent(holder, currAppInfo);

        if (holder.viewBtn != null) {
            holder.viewBtn.setOnClickListener(this);
            holder.viewBtn.setTag(R.id.tag_first, currAppInfo);
            holder.viewBtn.setTag(R.id.tag_second, position);


            if (DownloadViewHolder.APP_APPOINTMENT_Y.equals(currAppInfo.getAppointmentStatus())) {
                holder.viewBtn.setVisibility(View.GONE);
                holder.button.setVisibility(View.VISIBLE);
            } else {
                boolean isReady = (downloadState != DownloadManager.STATUS_NOTREADY);
                holder.viewBtn.setVisibility(isReady ? View.GONE : View.VISIBLE);
                holder.button.setVisibility(isReady ? View.VISIBLE : View.GONE);
            }

        }
        DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
        refreshDownloadBtn(currAppInfo, holder, downloadState);
        setStaticInfo(position, holder, currAppInfo);
        holder.itemView.setOnClickListener(this);
    }

    private void showContent(final ThreeContentGameHolder holder, final AppInfo currAppInfo) {
        holder.button.setTag(R.id.tag_first, currAppInfo);
        holder.gameIcon.setImageUrlAndReUse(currAppInfo.getIcon());
        if (false == TextUtils.isEmpty(currAppInfo.getcIconLabel())) {
            holder.gameIconLabel.setVisibility(View.VISIBLE);
            holder.gameIconLabel.setImageUrlAndReUse(currAppInfo.getcIconLabel());
        } else {
            holder.gameIconLabel.setVisibility(View.GONE);
        }
        holder.gameppTitle.setText(currAppInfo.getAppName());
        holder.mDownloadStateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.button.performClick();
            }
        });

    }


    /**
     * 更改按以及进度条钮状态
     *
     * @param appInfo
     * @param holder
     * @param downloadState
     */
    private void refreshDownloadBtn(AppInfo appInfo, ThreeContentGameHolder holder, int downloadState) {
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
    private void setStaticInfo(int position, ThreeContentGameHolder holder, AppInfo currAppInfo) {
        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
            route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
            currAppInfo.setRoute(route);
            holder.button.setTag(R.id.tag_first, currAppInfo);
            holder.button.setTag(R.id.tag_second, position);
        }
        if (holder.itemView != null) {
            holder.itemView.setTag(R.id.tag_first, currAppInfo);
            holder.itemView.setTag(R.id.tag_second, position);
        }
    }


    @Override
    public void onClick(View view) {
        //进入游戏详情
        AppInfo currAppInfo = view.getTag(R.id.tag_first) == null ? null : (AppInfo) view.getTag(R.id.tag_first);
        int[] route = mRoute.clone();
        route[AppConstants.STATISTCS_DEPTH_SEVEN] = (Integer) view.getTag(R.id.tag_second) + 1;
        if (currAppInfo == null) return;
        if (currAppInfo.getAppId() != 0)
            mContext.startActivity(DetailActivity.newIntent(mContext, currAppInfo.getAppId(), mRoute));

    }

}