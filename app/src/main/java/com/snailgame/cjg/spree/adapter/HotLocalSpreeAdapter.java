package com.snailgame.cjg.spree.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.cjg.spree.model.SpreesAppModel;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.SpreeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 热门 和 本地礼包 共用adapter
 * Created by TAJ_C on 2015/5/4.
 */
public class HotLocalSpreeAdapter extends BaseAdapter {

    private Activity mContext;

    private List<SpreesAppModel.ModelItem> spreeModelList;
    private List<AppInfo> appInfoList = new ArrayList<>();

    public static final int MAX_SPREE_ITEM_SHOW = 1;
    private int[] mRoute;

    public HotLocalSpreeAdapter(Activity context, List<SpreesAppModel.ModelItem> spreeModelList, int[] route) {
        this.mContext = context;
        this.spreeModelList = spreeModelList;
        initAppInfoList(spreeModelList);
        mRoute = route;
    }


    @Override
    public int getCount() {
        if (spreeModelList != null) {
            return spreeModelList.size();
        } else {
            return 0;
        }
    }

    @Override
    public SpreesAppModel.ModelItem getItem(int position) {
        return spreeModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spree_item_layout, null);
            viewHolder = new ViewHolder(mContext, convertView, mRoute);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //应用部分
        viewHolder.spreeAppInfoHolder.bindAppInfoView(appInfoList.get(position), position);

        //礼包部分
        final SpreesAppModel.ModelItem item = getItem(position);
        List<SpreeGiftInfo> spreeGiftInfoList = null;
        if (item != null) {
            spreeGiftInfoList = item.getSpreeGiftInfoList();
        }
        viewHolder.giftContainer.removeAllViews();
        viewHolder.spreeMoreContainer.removeAllViews();
        if (spreeGiftInfoList != null) {
            boolean isSpannable = spreeGiftInfoList.size() > MAX_SPREE_ITEM_SHOW;
            for (int i = 0; i < spreeGiftInfoList.size() && i < MAX_SPREE_ITEM_SHOW; i++) {
                View spreeGiftItemView = LayoutInflater.from(mContext).inflate(R.layout.spree_item_spree_info, null);
                SpreeGiftItemHolder itemGiftHolder = new SpreeGiftItemHolder(mContext, spreeGiftItemView, listener);
                itemGiftHolder.inflatData(mContext, spreeGiftInfoList.get(i));
                itemGiftHolder.toggleBtn.setVisibility(isSpannable && false == item.isShowExpand() ? View.VISIBLE : View.GONE);
                spreeGiftItemView.setTag(itemGiftHolder);
                viewHolder.giftContainer.addView(spreeGiftItemView);
            }

            for (int k = MAX_SPREE_ITEM_SHOW; k < spreeGiftInfoList.size(); k++) {
                View spreeGiftItemView = LayoutInflater.from(mContext).inflate(R.layout.spree_item_spree_info, null);
                SpreeGiftItemHolder spreeGiftItemHolder = new SpreeGiftItemHolder(mContext, spreeGiftItemView, listener);
                spreeGiftItemHolder.inflatData(mContext, spreeGiftInfoList.get(k));
                viewHolder.spreeMoreContainer.addView(spreeGiftItemView);
            }

        }

        viewHolder.spreeMoreContainer.setTag(R.id.tag_animation, new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                item.setShowExpand(!item.isShowExpand());
                View childView = viewHolder.giftContainer.getChildAt(0);
                if (childView != null) {
                    SpreeGiftItemHolder holder = (SpreeGiftItemHolder) childView.getTag();
                    holder.toggleBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return convertView;
    }


    public void refreshData(SpreeGiftInfo spreeGiftInfo) {

        if (spreeGiftInfo != null && spreeModelList != null) {
            for (SpreesAppModel.ModelItem item : spreeModelList) {
                if (item.getAppId() == spreeGiftInfo.getnAppId()) {
                    for (SpreeGiftInfo info : item.getSpreeGiftInfoList()) {
                        if (info.getiArticleId() == spreeGiftInfo.getiArticleId()) {
                            info.setcCdkey(spreeGiftInfo.getcCdkey());
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }
        }

    }

    public void refreshData(List<SpreesAppModel.ModelItem> modelItemList) {
        this.spreeModelList = modelItemList;
        initAppInfoList(modelItemList);
        notifyDataSetChanged();
    }

    public List<AppInfo> getAppInfoList() {
        return appInfoList;
    }


    public void refreshAppList(List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
        notifyDataSetChanged();
    }


    /**
     * 加 spree mode list 组装成 appInfo list
     *
     * @param spreeModelList
     */
    private void initAppInfoList(List<SpreesAppModel.ModelItem> spreeModelList) {
        appInfoList.clear();
        if (spreeModelList == null) {
            return;
        }

        for (SpreesAppModel.ModelItem item : spreeModelList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppId((int) item.getAppId());
            appInfo.setIcon(item.getIcon());
            appInfo.setVersionCode(item.getVersionCode());
            appInfo.setVersionName(item.getVersionName());
            appInfo.setAppName(item.getAppName());
            appInfo.setMd5(item.getMd5());
            appInfo.setApkSize(item.getApkSize());
            appInfo.setcAppType(item.getcAppType());
            appInfo.setPkgName(item.getPkgName());
            appInfo.setcFlowFree(item.getcFlowFree());
            appInfo.setApkUrl(item.getApkUrl());
            appInfo.setsAppDesc(item.getsAppDesc());
            appInfoList.add(appInfo);

            SpreeUtils.initInfoData(item.getSpreeGiftInfoList());
        }

        AppInfoUtils.updateDownloadState(mContext, appInfoList);
    }

    public static class ViewHolder {

        @Bind(R.id.expandable)
        LinearLayout spreeMoreContainer;

        @Bind(R.id.gift_container)
        LinearLayout giftContainer;


        SpreeAppInfoHolder spreeAppInfoHolder;

        public ViewHolder(Context context, View view, int[] route) {
            ButterKnife.bind(this, view);
            spreeAppInfoHolder = new SpreeAppInfoHolder(context, view, route);
        }
    }

    private SpreeInterfaceListener listener;

    public interface SpreeInterfaceListener {
        void spreeGetAction(SpreeGiftInfo spreeGiftInfo);

    }

    public void setOnSpreeGetListener(SpreeInterfaceListener listener) {
        this.listener = listener;
    }
}
