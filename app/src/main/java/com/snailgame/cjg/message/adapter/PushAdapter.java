package com.snailgame.cjg.message.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.message.model.MessagePushExInfo;
import com.snailgame.cjg.util.MessageJumpUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 推送消息Adapter
 */
public class PushAdapter extends BaseAdapter implements ImpRefresh<List<PushModel>> {
    private final static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private List<PushModel> mPushModels;
    private Activity mActivity;
    private int[] mRoute;

    public PushAdapter(Activity activity, List<PushModel> pushModel) {
        mPushModels = pushModel;
        mActivity = activity;
        mRoute = createRoute();
    }


    @Override
    public int getCount() {
        if (mPushModels == null)
            return 0;
        return mPushModels.size();
    }

    @Override
    public PushModel getItem(int position) {
        if (mPushModels == null)
            return null;

        if (position < mPushModels.size())
            return mPushModels.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mActivity, R.layout.message_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.push_area.setBackgroundResource(R.drawable.list_item_selector);
        fillContent(position, viewHolder);
        return convertView;
    }

    @Override
    public void refreshData(List<PushModel> pushModels) {
        mPushModels = pushModels;
        notifyDataSetChanged();
    }

    private void fillContent(final int position, ViewHolder viewHolder) {
        final PushModel model = getItem(position);
        if (model == null || viewHolder == null)
            return;
        viewHolder.tv_title.setText(model.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        if (!TextUtils.isEmpty(model.getCreate_date()))
            viewHolder.tv_time.setText(simpleDateFormat.format(new Date(Long.parseLong(model.getCreate_date()))));

        //设置 Icon
        String extraContent = model.getExpandMsg();
        MessagePushExInfo messagePushExInfo = null;
        try {
            messagePushExInfo = JSON.parseObject(extraContent, MessagePushExInfo.class);
            String bigImgUrl = messagePushExInfo.getBigImgUrl();
            if (bigImgUrl.contains("|")) {
                String[] strings = bigImgUrl.split("\\|");
                bigImgUrl = strings[0];
            }
            model.setIconBigUrl(bigImgUrl);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }

        if (TextUtils.isEmpty(model.getIconBigUrl())) {
            viewHolder.ico.setVisibility(View.GONE);
        } else {
            viewHolder.ico.setVisibility(View.VISIBLE);
        }
        viewHolder.ico.setImageUrlAndReUse(model.getIconBigUrl());
        viewHolder.tv_content.setTextColor(ResUtil.getColor(R.color.primary_text_color));

        /*如果是公告则全部显示，如果不是则保留两行*/
        if (messagePushExInfo != null && (messagePushExInfo.getType() == 2 || messagePushExInfo.getType() == 8)) {
            viewHolder.tv_content.setMaxLines(9999);
        } else {
            viewHolder.tv_content.setMaxLines(2);
        }
        viewHolder.tv_content.setText(model.getContent());

        final MessagePushExInfo finalMessagePushExInfo = messagePushExInfo;
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 自定义事件 90003：用户点击消息列表
                StaticsUtils.sendGETUIEvent(mActivity, AppConstants.GETUI_ACTION_LIST_CLICK, model.getMsgId(), model.getTaskId());
                MessageJumpUtil.JumpActivity(mActivity, finalMessagePushExInfo, mRoute);
            }
        });
    }


    static class ViewHolder {
        View container;
        @Bind(R.id.ico)
        FSSimpleImageView ico;
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Bind(R.id.tv_time)
        TextView tv_time;
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.push_area)
        LinearLayout push_area;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            container = view;
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 推送
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_PUSH,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }
}
