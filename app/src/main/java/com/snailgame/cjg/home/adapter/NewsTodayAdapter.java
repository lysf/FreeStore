package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.friend.utils.FriendTagUtil;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.news.model.NewsIgnoreModel;
import com.snailgame.cjg.news.widget.NewsIgnoreDialog;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 今日资讯
 * Created by TAJ_C on 2016/3/25.
 */
public class NewsTodayAdapter extends ModuleBaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int MAX_NUM = 3;
    private static final String NEWS_ITEM_TYPE_NORMAL = "1";//资讯类型: 1:为普通资讯 ; 2:为推广..
    private FriendTagUtil friendTagUtil = new FriendTagUtil();

    public NewsTodayAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        //+1模板顶部栏目
        if (ListUtils.isEmpty(children)) {
            return 1; // 显示头部
        } else {
            return 2; // 显示头部 + 下方GridView
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                convertView = getHeaderView(true);
                break;
            default:
                if (convertView == null || convertView.getTag() == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_home_news_today_container, parent, false);
                    convertView.setTag(children);
                }

                LinearLayout containerView = (LinearLayout) convertView.findViewById(R.id.news_today_container);
                if (containerView != null) {
                    containerView.removeAllViews();
                    for (int i = 0; i < children.size() && i < MAX_NUM; i++) {
                        final ContentModel item = children.get(i);
                        View itemView = LayoutInflater.from(context).inflate(R.layout.item_home_news_today, null);
                        final ViewHolder viewHolder = new ViewHolder(itemView);
                        if (item != null) {
                            viewHolder.picView.setImageUrlAndReUse(item.getsImageUrl());
                            viewHolder.titleView.setText(item.getsTitle());
                            viewHolder.timeView.setText(ComUtil.getFormatDate(item.getsSubtitle()));
                            itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(WebViewActivity.newNewsWebViewIntent(context, String.valueOf(item.getsRefId()),
                                            item.getsJumpUrl(), mRoute));
                                }
                            });

                            if (!TextUtils.isEmpty(item.getP3())) {
                                String[] tags = item.getP3().split(",");
                                if (tags != null) {
                                    for (String tag : tags) {
                                        if (viewHolder.tagContainer != null) {
                                            viewHolder.tagContainer.addView(friendTagUtil.getColorTextView(context, tag), viewHolder.tagContainer.getChildCount() - 1);
                                        }
                                    }
                                }
                            }

                            if (!TextUtils.isEmpty(item.getP2())) {
                                viewHolder.closeView.setVisibility(item.getP2().equals(NEWS_ITEM_TYPE_NORMAL) ? View.VISIBLE : View.GONE);
                            }

                            viewHolder.closeView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        List<NewsIgnoreModel> ignoreModelList = JSON.parseArray(item.getP1(), NewsIgnoreModel.class);

                                        new NewsIgnoreDialog(context, String.valueOf(item.getsRefId()), ignoreModelList)
                                                .setLocationByAttachedView(viewHolder.closeView)
                                                .setTouchOutsideDismiss(true)
                                                .setMatchParent(true)
                                                .show();
                                    } catch (Exception e) {
                                    }

                                }
                            });


                        }
                        containerView.addView(itemView);
                    }
                }

                break;
        }

        return convertView;
    }

    public void removeItem(String newsId) {
        for (ContentModel item : children) {
            if (item.getsRefId().equals(newsId)) {
                children.remove(item);
                notifyDataSetChanged();
                break;
            }
        }
    }

    class ViewHolder {
        @Bind(R.id.siv_news_pic)
        FSSimpleImageView picView;

        @Bind(R.id.tv_news_title)
        TextView titleView;

        @Bind(R.id.tv_news_time)
        TextView timeView;

        @Bind(R.id.iv_news_close)
        ImageView closeView;

        @Nullable
        @Bind(R.id.tag_container)
        LinearLayout tagContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
