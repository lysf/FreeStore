package com.snailgame.cjg.manage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.downloadmanager.GameManageActivity;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.manage.model.TreasureBoxOfflineInfo;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.ui.SummaryActivity;
import third.com.snail.trafficmonitor.ui.TrafficControlActivity;

/**
 * create by lic
 * 管理页面百宝箱Adapter，在没有获取到系统配置的百宝箱时默认的功能设置。
 */
public class ManageGridOffLineAdapter extends BaseAdapter {
    private Activity activity;
    private List<TreasureBoxOfflineInfo> dataList;

    public ManageGridOffLineAdapter(Activity activity, List<TreasureBoxOfflineInfo> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        if (!EngineEnvironment.isTrafficEnable()) {
            Iterator<TreasureBoxOfflineInfo> iterator = this.dataList.iterator();
            while (iterator.hasNext()) {
                TreasureBoxOfflineInfo info = iterator.next();
                switch (Integer.valueOf(info.id)) {
                    case 0: //流量统计
                    case 1: //联网控制
                        iterator.remove();
                        break;
                }
            }
        }
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public TreasureBoxOfflineInfo getItem(int position) {
        if (dataList != null && position < dataList.size())
            return dataList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(
                    R.layout.manage_grid_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TreasureBoxOfflineInfo info = getItem(position);
        if (info != null) {
            viewHolder.appIcon.setImageResource(info.resId);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                viewHolder.frameLayout.setBackground(ResUtil.getDrawable(R.drawable.manage_icon_bg));
            } else {
                viewHolder.frameLayout.setForeground(ResUtil.getDrawable(R.drawable.manage_icon_bg));
            }
            viewHolder.frameLayout.setOnClickListener(new ItemClickEvent(info));
            viewHolder.appName.setText(info.name);
        }

        return convertView;
    }

    private class ItemClickEvent implements View.OnClickListener {
        private TreasureBoxOfflineInfo info;

        public ItemClickEvent(TreasureBoxOfflineInfo info) {
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            switch (Integer.valueOf(info.id)) {
                case TreasureBoxOfflineInfo.TRAFFIC_STATISTICS_ID: //流量统计
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_FLOW_SUMMARY);
                    try {
                        activity.startActivity(new Intent(activity, SummaryActivity.class));
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showMsg(activity, activity.getString(R.string.msg_mobile_dont_support_function));
                    }
                    break;
                case TreasureBoxOfflineInfo.TRAFFIC_CONTROL_ID: //联网控制
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_TRAFFIC_CONTROL);
                    try {
                        activity.startActivity(new Intent(activity, TrafficControlActivity.class));
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showMsg(activity, activity.getString(R.string.msg_mobile_dont_support_function));
                    }
                    break;
                case TreasureBoxOfflineInfo.APP_MANAGE_ID://应用管理
                    activity.startActivity(GameManageActivity.newIntent(activity));
                    break;
                case TreasureBoxOfflineInfo.RECOMMEND_ID: //推荐好友
                    ShareDialog shareDialog = new ShareDialog(activity);
                    ShareItemListener clickListener = new ShareItemListener(activity, shareDialog);
                    shareDialog.setOnItemClickListener(clickListener);
                    shareDialog.show();
                    break;
                default:
                    break;
            }
        }
    }

    final class ViewHolder {
        @Bind(R.id.appIcon)
        public ImageView appIcon;
        @Bind(R.id.appName)
        public TextView appName;
        @Bind(R.id.layout)
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
