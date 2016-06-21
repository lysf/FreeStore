package com.snailgame.cjg.news.adpter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.NewsReaded;
import com.snailgame.cjg.common.db.daoHelper.NewsReadDaoHelper;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.friend.utils.FriendTagUtil;
import com.snailgame.cjg.news.model.NewsListModel;
import com.snailgame.cjg.news.widget.NewsIgnoreDialog;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2016/4/13.
 */
public class NewsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<NewsListModel.ModelItem.DataBean> newsList;

    private static final int NEWS_STYLE_NUM = 4;

    private static final String NEWS_STYLE_NONE_PIC = "1";
    private static final String NEWS_STYLE_LEFT_PIC = "2";
    private static final String NEWS_STYLE_THREE_PIC = "3";
    private static final String NEWS_STYLE_BIG_PIC = "4";

    private static final String NEWS_ITEM_TYPE_NORMAL = "1";//资讯类型: 1:为普通资讯 ; 2:为推广..
    protected int[] mRoute;

    FriendTagUtil friendTagUtil = new FriendTagUtil();

    public NewsListAdapter(Context mContext, List<NewsListModel.ModelItem.DataBean> newsList, int[] route) {
        this.mContext = mContext;
        this.newsList = newsList;
        this.mRoute = route;

        List<NewsReaded> newsReadedList = NewsReadDaoHelper.getInstance(mContext).queryReadData();
        if (newsReadedList != null && newsList != null) {
            for (NewsReaded newsReaded : newsReadedList) {
                for (NewsListModel.ModelItem.DataBean item : newsList) {
                    if (item != null && newsReaded.getNewsID().equals(item.getNArticleId())) {
                        item.setRead(true);
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        if (newsList != null) {
            return newsList.size();
        } else {
            return 0;
        }
    }


    @Override
    public int getViewTypeCount() {
        return NEWS_STYLE_NUM;
    }

    @Override
    public NewsListModel.ModelItem.DataBean getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final NewsListModel.ModelItem.DataBean item = getItem(position);
        if (item == null) {
            return LayoutInflater.from(mContext).inflate(R.layout.item_news_style_none_pic, parent, false);
        }

        final ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            switch (item.getCStyleType()) {
                case NEWS_STYLE_LEFT_PIC:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_style_left, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(R.id.tag_news_style_left_pic, viewHolder);
                    break;
                case NEWS_STYLE_THREE_PIC:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_style_three, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(R.id.tag_news_style_three_pic, viewHolder);
                    break;
                case NEWS_STYLE_BIG_PIC:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_style_big_pic, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(R.id.tag_news_style_big_pic, viewHolder);
                    break;
                default:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_style_none_pic, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(R.id.tag_news_style_none, viewHolder);
                    break;
            }

        } else {
            switch (item.getCStyleType()) {
                case NEWS_STYLE_LEFT_PIC:
                    viewHolder = (ViewHolder) convertView.getTag(R.id.tag_news_style_left_pic);
                    break;
                case NEWS_STYLE_THREE_PIC:
                    viewHolder = (ViewHolder) convertView.getTag(R.id.tag_news_style_big_pic);
                    break;
                case NEWS_STYLE_BIG_PIC:
                    viewHolder = (ViewHolder) convertView.getTag(R.id.tag_news_style_big_pic);
                    break;
                default:
                    viewHolder = (ViewHolder) convertView.getTag(R.id.tag_news_style_none);
                    break;
            }
        }

        //设置图片
        if (item.getImages() != null && item.getImages().size() > 0) {
            //三幅图
            if (item.getCStyleType().equals(NEWS_STYLE_THREE_PIC)) {
                for (int i = 0; i < item.getImages().size() && i < viewHolder.smallPicViews.size(); i++) {
                    NewsListModel.ModelItem.DataBean.ImagesBean imageItem = item.getImages().get(i);
                    if (imageItem != null) {
                        viewHolder.smallPicViews.get(i).setImageUrlAndReUse(imageItem.getCUrl());
                    }
                }
            } else if (!item.getCStyleType().equals(NEWS_STYLE_NONE_PIC)) { //剩下的 除了不显示图片的
                if (item.getImages().get(0) != null) {
                    if (viewHolder.picView != null) {
                        viewHolder.picView.setImageUrlAndReUse(item.getImages().get(0).getCUrl());
                    }
                }
            }
        }

        viewHolder.titleView.setText(item.getSTitle());


        viewHolder.titleView.setTextColor(ResUtil.getColor(item.isRead() ?
                R.color.news_readed_color : R.color.primary_text_color));

        viewHolder.timeView.setText(ComUtil.getFormatDate(item.getDCreate()));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item.setRead(true);

                NewsReaded newsReaded = new NewsReaded();
                newsReaded.setNewsID(item.getNArticleId());
                NewsReadDaoHelper.getInstance(mContext).insert(newsReaded);

                viewHolder.titleView.setTextColor(ResUtil.getColor(R.color.news_readed_color));

                mContext.startActivity(WebViewActivity.newNewsWebViewIntent(mContext, item.getNArticleId(), item.getCUrl(), mRoute));
            }
        });

        if (!TextUtils.isEmpty(item.getTags())) {
            String[] tags = item.getTags().split(",");
            if (tags != null) {
                for (String tag : tags) {
                    if (viewHolder.tagContainer != null) {
                        viewHolder.tagContainer.addView(friendTagUtil.getColorTextView(mContext,tag), viewHolder.tagContainer.getChildCount() - 1);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(item.getItemType())) {
            viewHolder.closeView.setVisibility(item.getItemType().equals(NEWS_ITEM_TYPE_NORMAL) ? View.VISIBLE : View.GONE);
        }
        viewHolder.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewsIgnoreDialog(mContext, item.getNArticleId(), item.getNoInterestTags())
                        .setLocationByAttachedView(viewHolder.closeView)
                        .setTouchOutsideDismiss(true)
                        .setMatchParent(true)
                        .show();
            }
        });

        return convertView;
    }


    public void refreshData(List<NewsListModel.ModelItem.DataBean> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Nullable
        @Bind(R.id.siv_news_pic)
        FSSimpleImageView picView;

        @Bind(R.id.tv_news_title)
        TextView titleView;

        @Bind(R.id.tv_news_time)
        TextView timeView;

        @Bind(R.id.iv_news_close)
        ImageView closeView;

        @Nullable
        @Bind(R.id.siv_news_pic1)
        FSSimpleImageView smallpicView1;

        @Nullable
        @Bind(R.id.siv_news_pic2)
        FSSimpleImageView smallpicView2;

        @Nullable
        @Bind(R.id.siv_news_pic3)
        FSSimpleImageView smallpicView3;

        @Nullable
        @Bind(R.id.tag_container)
        LinearLayout tagContainer;

        List<FSSimpleImageView> smallPicViews;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            smallPicViews = new ArrayList<>();
            smallPicViews.add(smallpicView1);
            smallPicViews.add(smallpicView2);
            smallPicViews.add(smallpicView3);
        }
    }
}