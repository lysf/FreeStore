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
 * 热门资讯
 * Created by TAJ_C on 2016/3/25.
 */
public class NewsHotAdapter extends ModuleBaseAdapter {

    private static final int VIEW_TYPE_COUNT = 2;

    private static final int MAX_NUM = 4;
    private FriendTagUtil friendTagUtil = new FriendTagUtil();

    public NewsHotAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        super(context, moduleModel, route);
    }

    @Override
    public int getCount() {
        //+1模板顶部栏目
        if (ListUtils.isEmpty(children))
            return 1;
        return (children.size() > MAX_NUM ? MAX_NUM : children.size()) + 1;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                convertView = getHeaderView(true);
                break;

            default:
                convertView = getItemView(position - 1);
                break;
        }
        return convertView;
    }

    private View getItemView(int position) {
        View convertView;
        final ViewHolder viewHolder;
        convertView = LayoutInflater.from(context).inflate(R.layout.item_news_style_left, null);
        viewHolder = new ViewHolder(convertView);


        final ContentModel contentModel = getItem(position);
        if (contentModel != null) {
            viewHolder.picView.setImageUrlAndReUse(contentModel.getsImageUrl());
            viewHolder.titleView.setText(contentModel.getsTitle());
            viewHolder.timeView.setText(ComUtil.getFormatDate(contentModel.getsSubtitle()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(WebViewActivity.newNewsWebViewIntent(context,
                            String.valueOf(contentModel.getsRefId()), contentModel.getsJumpUrl(), mRoute));
                }
            });
            if (!TextUtils.isEmpty(contentModel.getP3())) {
                String[] tags = contentModel.getP3().split(",");
                if (tags != null) {
                    for (String tag : tags) {
                        if (viewHolder.tagContainer != null) {
                            viewHolder.tagContainer.addView(friendTagUtil.getColorTextView(context, tag), viewHolder.tagContainer.getChildCount() - 1);
                        }
                    }
                }

            }
            if (!TextUtils.isEmpty(contentModel.getP2())) {
                viewHolder.closeView.setVisibility(contentModel.getP2().equals("1") ? View.VISIBLE : View.GONE);
            }
            viewHolder.closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        List<NewsIgnoreModel> ignoreModelList = JSON.parseArray(contentModel.getP1(), NewsIgnoreModel.class);

                        new NewsIgnoreDialog(context, String.valueOf(contentModel.getsRefId()), ignoreModelList)
                                .setLocationByAttachedView(viewHolder.closeView)
                                .setTouchOutsideDismiss(true)
                                .setMatchParent(true)
                                .show();
                    } catch (Exception e) {
                    }
                }
            });

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
