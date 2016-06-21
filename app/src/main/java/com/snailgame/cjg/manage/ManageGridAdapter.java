package com.snailgame.cjg.manage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.share.listener.ShareItemListener;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.downloadmanager.GameManageActivity;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.manage.model.TreasureBoxInfo;
import com.snailgame.cjg.personal.UserTaskActivity;
import com.snailgame.cjg.settings.SettingActivity;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.umeng.analytics.MobclickAgent;
import com.zbar.lib.CaptureActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.ui.SummaryActivity;
import third.com.snail.trafficmonitor.ui.TrafficControlActivity;

/**
 * create by lic
 * 管理页面百宝箱Adapter
 */
public class ManageGridAdapter extends BaseAdapter {
    private Activity activity;
    private List<TreasureBoxInfo> dataList;

    public ManageGridAdapter(Activity activity, List<TreasureBoxInfo> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        if (!EngineEnvironment.isTrafficEnable()) {
            Iterator<TreasureBoxInfo> iterator = this.dataList.iterator();
            while (iterator.hasNext()) {
                TreasureBoxInfo info = iterator.next();
                switch (Integer.valueOf(info.getcType())) {
                    case 1: //流量统计
                    case 5: //联网控制
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
    public TreasureBoxInfo getItem(int position) {
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

        TreasureBoxInfo info = getItem(position);
        if (info != null) {
            viewHolder.appIcon.setImageUrl(info.getcIcon());
            if (android.os.Build.VERSION.SDK_INT >= 21 ) {
                viewHolder.frameLayout.setBackground(ResUtil.getDrawable(R.drawable.manage_icon_bg));
            } else {
                viewHolder.frameLayout.setForeground(ResUtil.getDrawable(R.drawable.manage_icon_bg));
            }
            viewHolder.frameLayout.setOnClickListener(new ItemClickEvent(info));
            viewHolder.appName.setText(info.getsName());
        }

        return convertView;
    }

    private class ItemClickEvent implements View.OnClickListener {
        private TreasureBoxInfo info;

        public ItemClickEvent(TreasureBoxInfo info) {
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            switch (Integer.valueOf(info.getcType())) {
                case TreasureBoxInfo.TRAFFIC_STATISTICS_ID: //流量统计
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_FLOW_SUMMARY);
                    try {
                        activity.startActivity(new Intent(activity, SummaryActivity.class));
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showMsg(activity, activity.getString(R.string.msg_mobile_dont_support_function));
                    }
                    break;
                case TreasureBoxInfo.SCAN_ID: //扫一扫
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_CAPTURE);
                    activity.startActivityForResult(new Intent(activity, CaptureActivity.class), CaptureActivity.SCAN_REQUEST_CODE);
                    break;
                case TreasureBoxInfo.NOVICE_CARD_ID://新手卡兑换入口
                    // remove 由个人中心福利码代替
                    break;
                case TreasureBoxInfo.TRAFFIC_CONTROL_ID: //联网控制
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_TRAFFIC_CONTROL);
                    try {
                        activity.startActivity(new Intent(activity, TrafficControlActivity.class));
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showMsg(activity, activity.getString(R.string.msg_mobile_dont_support_function));
                    }
                    break;
                case TreasureBoxInfo.SCORE_ID: //赚话费
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_SCORE);
                    activity.startActivity(UserTaskActivity.newIntent(activity));
                    break;
                case TreasureBoxInfo.APP_MANAGE_ID: //应用管理
                    activity.startActivity(GameManageActivity.newIntent(activity));
                    break;
                case TreasureBoxInfo.RECOMMEND_ID: //推荐好友
                    ShareDialog shareDialog = new ShareDialog(activity);
                    ShareItemListener clickListener = new ShareItemListener(activity, shareDialog);
                    shareDialog.setOnItemClickListener(clickListener);
                    shareDialog.show();
                    break;
                case TreasureBoxInfo.SETTING_ID: //商店设置
                    activity.startActivity(SettingActivity.newIntent(activity));
                    break;
                case TreasureBoxInfo.WEB_ID: //网页
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("url", info.getcUrl());
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_BALANCE_AND_SPREE, map);
                    activity.startActivity(WebViewActivity.newIntent(activity,
                            info.getcUrl()));
                    break;
                default:
                    HashMap<String, String> defaultMap = new HashMap<String, String>();
                    defaultMap.put("url", info.getcUrl());
                    MobclickAgent.onEvent(activity, UmengAnalytics.EVENT_BALANCE_AND_SPREE, defaultMap);
                    activity.startActivity(WebViewActivity.newIntent(activity,
                            info.getcUrl()));
                    break;
            }
        }
    }

    final class ViewHolder {
        @Bind(R.id.appIcon)
        public FSSimpleImageView appIcon;
        @Bind(R.id.appName)
        public TextView appName;
        @Bind(R.id.layout)
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
