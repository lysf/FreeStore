package com.snailgame.cjg.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.DownloadWidgetHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.FreeGameItem;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;
import com.snailgame.cjg.seekgame.recommend.RecommendType;
import com.snailgame.cjg.seekgame.recommend.model.RecommendCollectionInfo;
import com.snailgame.cjg.seekgame.recommend.model.RecommendInfo;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 该类为所有应用列表子项目的通用适配器
 */
public class CommonListItemAdapter extends BaseAdapter implements OnClickListener {
    public static final int TAG_NORMAL_LIST = -2, TAG_COLLECTION_LIST = -3;
    protected LayoutInflater inflater;
    protected Activity mContext;

    private int mFragementType = AppConstants.VALUE_CATEGORY;
    /**
     * 数据源
     */
    private List<AppInfo> sourceAppList;


    private boolean refreshAppListItem = false;
    private int[] mRoute;


    private static final String TAG_APP_SORT = "1";  //表示游戏分类
    private static final String TAG_COLLECT_SORE = "2"; //表示合集分类
    private EmptyView emptyView;
    //    private boolean isShowHomeDivider = true;
    protected boolean isUseViewHolder = false;

    private static final int TYPE_COUNT = 2;
    private static final int MAX_NUM = 6;

    public CommonListItemAdapter(Activity context, List<AppInfo> appInfos, int fragementType, int[] route) {
        mContext = context;
        sourceAppList = appInfos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
        mFragementType = fragementType;
    }

    @Override
    public int getCount() {
        if (sourceAppList == null)
            return 0;
        return sourceAppList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        if (sourceAppList != null && position < sourceAppList.size() && position >= 0)
            return sourceAppList.get(position);
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
        if (mFragementType == AppConstants.VALUE_RECOMMEND
                && currAppInfo != null && currAppInfo.getcType().equals(TAG_COLLECT_SORE)) {
            //合集
            CollectItemViewHolder holder;
            if (convertView != null && convertView.getTag(TAG_COLLECTION_LIST) != null) {
                holder = (CollectItemViewHolder) convertView.getTag(TAG_COLLECTION_LIST);
            } else {
                convertView = inflater.inflate(R.layout.appinfo_list_collection_item, parent, false);
                holder = new CollectItemViewHolder(mContext, convertView);
                convertView.setTag(TAG_COLLECTION_LIST, holder);
            }

            setupCollectView(position, currAppInfo, holder);
        } else {
            //正常应用列表和 搜索里面的应用列表
            CommonListItemViewHolder holder;
            if (convertView != null && convertView.getTag(TAG_NORMAL_LIST) != null) {
                holder = (CommonListItemViewHolder) convertView.getTag(TAG_NORMAL_LIST);
                holder.setAppInfo(currAppInfo);
            } else {
                convertView = inflater.inflate(R.layout.appinfo_list_item, parent, false);
                holder = new CommonListItemViewHolder(mContext, convertView, currAppInfo);
                convertView.setTag(TAG_NORMAL_LIST, holder);
            }
            if (position >= getCount() - 1)
                holder.divider.setVisibility(View.GONE);
            else {
                holder.divider.setVisibility(View.VISIBLE);
            }

            if (currAppInfo != null) {
                setupAppListView(position, holder, currAppInfo);
            }
        }

        return convertView;
    }

    private void setupCollectView(int position, AppInfo currAppInfo, CollectItemViewHolder holder) {
        holder.appPicView.setImageUrlAndReUse(currAppInfo.getcPicUrl());
        if (TextUtils.isEmpty(currAppInfo.getIcon())) {
            holder.iconView.setVisibility(View.GONE);
        } else {
            holder.iconView.setVisibility(View.VISIBLE);
            holder.iconView.setImageUrl(currAppInfo.getIcon());
        }

        holder.titleView.setText(TextUtils.isEmpty(currAppInfo.getAppName()) ?
                ResUtil.getString(R.string.recommend_hot_collect) : currAppInfo.getAppName());


        if (holder.viewBtn != null) {
            holder.viewBtn.setTag(R.id.tag_first, currAppInfo);
            holder.viewBtn.setTag(R.id.tag_second, position);
        }

        if (holder.appPicView != null) {
            holder.appPicView.setTag(R.id.tag_first, currAppInfo);
            holder.appPicView.setTag(R.id.tag_second, position);
        }
        holder.appPicView.setOnClickListener(this);
        holder.viewBtn.setOnClickListener(this);
        RecommendCollectionInfo info = JSON.parseObject(currAppInfo.getsAppDesc(), RecommendCollectionInfo.class);
        setupCollectItemView(holder.itemContainer, info.getApplist());
    }


    /**
     * 设置 合集下面item view
     *
     * @param itemContainer
     * @param mAppRecommends
     */
    private void setupCollectItemView(LinearLayout itemContainer, List<RecommendCollectionInfo.ModelItem> mAppRecommends) {
        itemContainer.removeAllViews();
        if (mAppRecommends == null || mAppRecommends.size() == 0) {
            itemContainer.setVisibility(View.GONE);
            return;
        } else {
            itemContainer.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < mAppRecommends.size() && i < MAX_NUM; i++) {
            FSSimpleImageView imageView = new FSSimpleImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ComUtil.dpToPx(28), ComUtil.dpToPx(28));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            params.setMargins(ComUtil.dpToPx(4), 0, ComUtil.dpToPx(4), 0);
            imageView.setLayoutParams(params);
//            imageView.getHierarchy().setPlaceholderImage(R.drawable.pic_ragle_loading);
            imageView.setImageUrl(mAppRecommends.get(i).getcIcon());
            itemContainer.addView(imageView);
        }
    }

    private void setupAppListView(int position, CommonListItemViewHolder holder, AppInfo currAppInfo) {
        int downloadState = DownloadWidgetHelper.getHelper().checkDownloadState(currAppInfo, mContext);

        //图标
        if (holder.ivAppLogo != null && currAppInfo.getIcon() != null) {
            holder.ivAppLogo.setImageUrlAndReUse(currAppInfo.getIcon());
            holder.ivAppLogo.setVisibility(View.VISIBLE);
        }

        if (holder.ivAppLogoLabel != null) {
            if (TextUtils.isEmpty(currAppInfo.getcIconLabel())) {
                holder.ivAppLogoLabel.setVisibility(View.GONE);
            } else {
                holder.ivAppLogoLabel.setImageUrlAndReUse(currAppInfo.getcIconLabel());
                holder.ivAppLogoLabel.setVisibility(View.VISIBLE);
            }
        }

        //标题
        if (holder.tvAppLabel != null) {
            holder.tvAppLabel.setText(currAppInfo.getAppName());
            holder.tvAppLabel.setVisibility(View.VISIBLE);
        }
        if (holder.tvAppInfo != null) {
            holder.tvAppInfo.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(currAppInfo.getsInfo())) {
                holder.tvAppInfo.setText(currAppInfo.getsInfo().trim());
            } else if (!TextUtils.isEmpty(currAppInfo.getsAppDesc())) {
                holder.tvAppInfo.setText(currAppInfo.getsAppDesc().trim());
            } else {
                holder.tvAppInfo.setVisibility(View.GONE);
            }
        }
        //下载按钮隐藏
        if (mFragementType == AppConstants.VALUE_RECOMMEND) {
            holder.button.setVisibility(currAppInfo.getcType().equals(TAG_APP_SORT) ? View.VISIBLE : View.GONE);
            holder.loadingForBtn.setVisibility(currAppInfo.getcType().equals(TAG_APP_SORT) ? View.VISIBLE : View.GONE);
        }

        if (mFragementType == AppConstants.VALUE_FREEAREA) {
            holder.parentView.setBackgroundResource(R.drawable.list_item_selector);
            holder.viewContainer.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

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

        //统计相关
        setStaticInfo(position, holder, currAppInfo);
        DownloadWidgetHelper.getHelper().switchState(downloadState, holder);
        //如果是新测有 则显示删档时间和删档状态
        if (AppConstants.FRAGMENT_APP_APPOINTMENT == mFragementType) {
            holder.tvAppSize.setText(currAppInfo.getTestingStatus());
            holder.tvAppInfo.setText(currAppInfo.getDelTestTime());
        }

    }

    /**
     * 跟统计相关的
     *
     * @param position
     * @param holder
     * @param currAppInfo
     */
    private void setStaticInfo(int position, CommonListItemViewHolder holder, AppInfo currAppInfo) {
        if (holder.button != null) {
            // 尝试下载统计
            int[] route = mRoute.clone();
            if (route[AppConstants.STATISTCS_DEPTH_SIX] == AppConstants.STATISTCS_DEFAULT_NULL) {
                route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
            } else {
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
            }
            route[AppConstants.STATISTCS_DEPTH_EIGHT] = AppConstants.STATISTCS_EIGHTH_LIST;
            route[AppConstants.STATISTCS_DEPTH_NINE] = currAppInfo.getAppId();
            currAppInfo.setRoute(route);
            holder.button.setTag(R.id.tag_first, currAppInfo);
            holder.button.setTag(R.id.tag_second, position);
        }

    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        AppInfo currAppInfo = view.getTag(R.id.tag_first) == null ? null : (AppInfo) view.getTag(R.id.tag_first);
        int[] route = mRoute.clone();
        if (route[AppConstants.STATISTCS_DEPTH_SIX] == AppConstants.STATISTCS_DEFAULT_NULL) {
            route[AppConstants.STATISTCS_DEPTH_FOUR] = (Integer) view.getTag(R.id.tag_second) + 1;
        } else {
            route[AppConstants.STATISTCS_DEPTH_SEVEN] = (Integer) view.getTag(R.id.tag_second) + 1;
        }

        if (currAppInfo == null) return;
        switch (viewId) {

            case R.id.tv_view:
            case R.id.app_pic_item:
                if (mFragementType == AppConstants.VALUE_RECOMMEND) {
                    RecommendInfo recommendInfo = new RecommendInfo();
                    recommendInfo.setnParamId(currAppInfo.getAppId());
                    recommendInfo.setcType(currAppInfo.getcType());
                    recommendInfo.setcHtmlUrl(currAppInfo.getcHtmlUrl());
                    recommendInfo.setAppName(currAppInfo.getAppName());
                    startActivity(recommendInfo, route);
                } else {
                    mContext.startActivity(DetailActivity.newIntent(mContext, currAppInfo.getAppId(), route));
                }

                break;
            default:
                break;
        }
    }


    private void startActivity(RecommendInfo recommendInfo, int[] route) {
        Intent intent = new Intent();
        int linkParam = (int) recommendInfo.getnParamId();
        int type = Integer.parseInt(recommendInfo.getcType());
        switch (RecommendType.valueOf(type)) {
            case RECOMMEND_DETAIL:
                intent = DetailActivity.newIntent(mContext, linkParam, route);
                break;
            case RECOMMEND_COLLECTION:
                intent = CollectionActivity.newIntent(mContext, linkParam, route);
                break;
            case RECOMMEND_HTML:
                intent = WebViewActivity.newIntent(mContext,
                        recommendInfo.getcHtmlUrl());
                break;
            default:
                break;
        }
        mContext.startActivity(intent);
    }

    public void refreshAppListItem(List<FreeGameItem> itemLists) {
        refreshAppListItem = true;
        FreeGameItem freeGameItem;

        int itemLength = 0;
        if (itemLists != null) itemLength = itemLists.size();

        if (sourceAppList != null) {
            List<AppInfo> list = new ArrayList<>();
            for (AppInfo appInfo : sourceAppList) {
                appInfo.setcFlowFree(AppInfo.FREE_NULL);
                for (int i = 0; i < itemLength; i++) {
                    freeGameItem = itemLists.get(i);
                    if (appInfo.getPkgName().equals(freeGameItem.getcPackage()))
                        appInfo.setcFlowFree(freeGameItem.getcFlowFree());
                }

                // 免流量专区的非免应用
                if (appInfo.getiFreeArea() == AppConstants.FREE_AREA_IN && !AppInfo.isDownloadFree(appInfo.getcFlowFree()))
                    list.add(appInfo);
            }

            // 在免流量体验区去除不免的应用
            sourceAppList.removeAll(list);
        }

        if (ListUtils.isEmpty(sourceAppList) && emptyView != null)
            emptyView.showEmpty();
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

    public void setEmptyView(EmptyView emptyView) {
        this.emptyView = emptyView;
    }


    public static class CommonListItemViewHolder extends DownloadViewHolder implements View.OnClickListener {
        @Bind(R.id.app_logo)
        FSSimpleImageView ivAppLogo;
        @Bind(R.id.app_logo_label)
        FSSimpleImageView ivAppLogoLabel;
        @Bind(R.id.app_title)
        TextView tvAppLabel;
        @Bind(R.id.app_info_layout)
        View viewContainer;
        @Bind(R.id.home_divider)
        public View divider;

        @Bind(R.id.tv_view)
        View viewBtn;

        View parentView;

        public CommonListItemViewHolder(Context mContext, View parentView, AppInfo appInfo) {
            super(mContext, parentView, appInfo);
            this.parentView = parentView;
        }

    }

    class CollectItemViewHolder {
        @Bind(R.id.iv_collect_icon)
        FSSimpleImageView iconView;
        @Bind(R.id.tv_collect_title)
        TextView titleView;
        @Bind(R.id.app_pic_item)
        FSSimpleImageView appPicView;
        @Bind(R.id.tv_view)
        View viewBtn;
        @Bind(R.id.collection_item_container)
        LinearLayout itemContainer;
        Context mContext;

        public CollectItemViewHolder(Context context, View parentView) {
            ButterKnife.bind(this, parentView);
            mContext = context;

        }
    }

    public void refreshRoute(int[] route) {
        mRoute = route;
    }


}
