package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/2/16.
 */
public class QuickReturnAdapter extends BaseAdapter {
    private int[] mRoute;
    private Activity activity;
    private List<ContentModel> contentList;

    public QuickReturnAdapter(int[] mRoute, Activity activity, List<ContentModel> contentList) {
        this.mRoute = mRoute;
        this.activity = activity;
        this.contentList = contentList;
    }

    @Override
    public int getCount() {
        if (contentList != null) {
            return contentList.size();
        } else {
            return 0;
        }
    }

    @Override
    public ContentModel getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.home_quick_return_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ContentModel item = getItem(position);
        if (item != null) {
            String url;
            String title;
            // 畅享宝图标设置
            if (PersistentVar.getInstance().getSystemConfig().isCxbStatus() &&
                    SharedPreferencesUtil.getInstance().isCxbPhoneNumber() &&
                    Integer.parseInt(item.getcSource()) == AppConstants.SOURCE_BBS) {
                url = PersistentVar.getInstance().getSystemConfig().getCxbIconUrl();
                title = PersistentVar.getInstance().getSystemConfig().getCxbTitle();
            } else {
                url = item.getsImageUrl();
                title = item.getsTitle();
            }

            viewHolder.iconView.setImageUrl(url);
            viewHolder.titleView.setText(title);
            if ((TextUtils.isEmpty(item.getcSource()) && TextUtils.isEmpty(item.getsJumpUrl())) ||
                    (item.getcSource().equals("1") && TextUtils.isEmpty(item.getsJumpUrl()))) {
                viewHolder.containerView.setBackgroundResource(R.color.translucent_full);
            } else {
                viewHolder.containerView.setBackgroundResource(R.drawable.ab_btn_selector);
            }
            viewHolder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = position;
                    jump(item, route);
                }
            });
        }

        return convertView;
    }

    private void jump(ContentModel contentModel, int[] route) {
        //顶部快捷入口
        if (activity != null) {
            JumpUtil.templateJump(activity, contentModel.getsJumpUrl(),
                    contentModel.getcSource(), contentModel.getsRefId(), contentModel.getsTitle(), route);
        }
    }

    class ViewHolder {
        @Bind(R.id.quickReturnIcon)
        FSSimpleImageView iconView;
        @Bind(R.id.quickReturnText)
        TextView titleView;

        @Bind(R.id.quickReturnRootLayout)
        View containerView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
