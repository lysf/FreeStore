package com.snailgame.cjg.seekgame.rank.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.seekgame.rank.RankFragment;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.SharedPreferencesHelper;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

/**
 * Created by sunxy on 2015/3/23.
 * 排行
 */
public class RankAdapter extends BaseAdapter implements View.OnClickListener {
    static String TAG = RankAdapter.class.getName();

    private Activity mContext;
    private LayoutInflater inflater;
    private List<AppInfo> sourceAppList;
    private int[] mRoute;
    private String orderIds;
    private String noRateString;
    private String orderString;
    private String orderedString;

    public RankAdapter(Activity context, List<AppInfo> appInfos, int[] route) {
        mContext = context;
        mRoute = route;
        sourceAppList = appInfos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        orderIds = SharedPreferencesHelper.getInstance().getValue(AppConstants.RANK_ORDER_ID, "");
        noRateString = ResUtil.getString(R.string.no_rate);
        orderString = ResUtil.getString(R.string.rank_order);
        orderedString = ResUtil.getString(R.string.rank_ordered);
    }

    @Override
    public int getCount() {
        if (ListUtils.isEmpty(sourceAppList))
            return 0;
        return sourceAppList.size() - RankFragment.TOP_3;
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
        View view;
        final RankViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.rank_list_item, parent, false);
            holder = new RankViewHolder(mContext, view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (RankViewHolder) convertView.getTag();
        }

        position += RankFragment.TOP_3;
        final AppInfo currAppInfo = getItem(position);

        refreshItemView(holder, currAppInfo, position);

        return view;
    }

    public void refreshItemView(RankViewHolder holder, AppInfo currAppInfo, int position) {

        if (currAppInfo != null) {
            int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);
            showContent(holder, currAppInfo, position);
            DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
            refreshDownloadBtn(currAppInfo, holder, downloadState);
            setStaticInfo(position, holder, currAppInfo);
            holder.itemView.setOnClickListener(this);

        }
    }

    private void showContent(final RankViewHolder holder, final AppInfo currAppInfo, int position) {
        holder.button.setTag(R.id.tag_first, currAppInfo);
        holder.rankAppIcon.setImageUrlAndReUse(currAppInfo.getIcon());
        holder.rankAppTitle.setText((position + 1) + "." + currAppInfo.getAppName());

        if (currAppInfo.getCommonLevel() == 0f) {
            holder.ratingBar.setVisibility(View.INVISIBLE);
            holder.noRate.setVisibility(View.VISIBLE);
            holder.noRate.setText(noRateString);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.noRate.setVisibility(View.INVISIBLE);
            holder.ratingBar.setRating(currAppInfo.getCommonLevel());
        }
        holder.mDownloadStateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currAppInfo.getiRefId() != 0)
                    holder.button.performClick();
                else {
                    order(currAppInfo.getRssId(), holder.mDownloadStateView);
                }
            }
        });

    }

    /**
     * 预约
     */
    private void order(String rssId, TextView orderView) {
        if (LoginSDKUtil.isLogined(mContext)) {
            SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
            if (TextUtils.isEmpty(orderIds)) {
                orderIds = rssId;
                showDialogAndSaveId(rssId, orderView, helper);
            } else {
                if (!orderIds.contains(rssId)) {
                    orderIds = orderIds + "," + rssId;
                    showDialogAndSaveId(rssId, orderView, helper);
                }
            }
        } else {
            AccountUtil.userLogin(mContext);
        }

    }

    private void showDialogAndSaveId(String rssId, TextView orderView, SharedPreferencesHelper helper) {
        helper.putValue(AppConstants.RANK_ORDER_ID, orderIds);
        helper.applyValue();
        orderView.setBackgroundResource(R.drawable.btn_grey_selector);
        orderView.setText(orderedString);
        DialogUtils.showOrderSuccessDialog(mContext);
        order(rssId);
    }

    /**
     * 更改按以及进度条钮状态
     *
     * @param appInfo
     * @param holder
     * @param downloadState
     */
    private void refreshDownloadBtn(AppInfo appInfo, RankViewHolder holder, int downloadState) {
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
            if (appInfo.getiRefId() == 0) {
                holder.button.setVisibility(View.GONE);
                holder.mDownloadStateView.setVisibility(View.VISIBLE);
                holder.mDownloadStateView.setText(ResUtil.getString(R.string.rank_order));
                if (orderIds.contains(appInfo.getRssId())) {
                    holder.mDownloadStateView.setText(orderedString);
                    holder.mDownloadStateView.setBackgroundResource(R.drawable.btn_grey_selector);
                } else {
                    holder.mDownloadStateView.setText(orderString);
                    holder.mDownloadStateView.setBackgroundResource(R.drawable.btn_green_selector);
                }
            } else {
                holder.button.setVisibility(View.VISIBLE);
                holder.mDownloadStateView.setVisibility(View.GONE);
            }
        }

    }


    /**
     * 跟统计相关的
     *
     * @param position
     * @param holder
     * @param currAppInfo
     */
    private void setStaticInfo(int position, RankViewHolder holder, AppInfo currAppInfo) {
        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
            route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
            route[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_RANK;
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_THIRD_LIST;
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
        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
        route[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_RANK;
        route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) view.getTag(R.id.tag_second) + 1;
        if (currAppInfo == null) return;
        if (currAppInfo.getiRefId() != 0 && currAppInfo.getAppId() != 0)
            mContext.startActivity(DetailActivity.newIntent(mContext, currAppInfo.getAppId(), route));
        else {
            ToastUtils.showMsgLong(mContext, mContext.getString(R.string.order_hint));
        }
    }

    private void order(String id) {
        String url = JsonUrl.getJsonUrl().JSON_URL_GAME_RANK_ORDER;
        String postBody = AccountUtil.getLoginParams().replace("?", "") + "&nItuneId=" + id + "&iPlatformId=" + AppConstants.APP_ID;

        FSRequestHelper.newPostRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, postBody);
    }
}
