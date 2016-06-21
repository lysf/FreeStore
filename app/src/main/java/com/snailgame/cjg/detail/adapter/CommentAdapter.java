package com.snailgame.cjg.detail.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.model.CommentListModel;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 游戏详情 评论的adapter
 * Created by taj on 2014/11/12.
 */
public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommentListModel.ModelItem> mCommentList;


    public CommentAdapter(Context mContext, List<CommentListModel.ModelItem> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }


    @Override
    public int getCount() {
        if (mCommentList != null) {
            return mCommentList.size();
        } else {
            return 0;
        }
    }

    @Override
    public CommentListModel.ModelItem getItem(int position) {
        if (mCommentList != null && position < mCommentList.size()) {
            return mCommentList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommentListModel.ModelItem item = getItem(position);
        if (viewHolder != null) {
            if (item != null && false == TextUtils.isEmpty(item.getsContent())) {
                viewHolder.nameView.setText(item.getUserNickName());
                viewHolder.timeView.setText(item.getdUpdate());
                viewHolder.contentView.setText(item.getsContent());
                viewHolder.ratingBarView.setRating(item.getiScore());

                StringBuffer reply = new StringBuffer();
                for (int i = 0; i < 18; i++) {
                    reply.append(ResUtil.getString(R.string.space));
                }
                reply.append(item.getReply());
                viewHolder.replyContentView.setText(Html.fromHtml(reply.toString()));
                viewHolder.replyContentView.setMovementMethod(LinkMovementMethod.getInstance());
                addLinkClick(viewHolder.replyContentView);

                viewHolder.photoView.setVisibility(View.VISIBLE);
                viewHolder.ratingBarView.setVisibility(View.VISIBLE);
                viewHolder.lineView.setVisibility(View.VISIBLE);
                if ((GlobalVar.getInstance().getUsrInfo() != null) && item.getnUserId().equals(GlobalVar.getInstance().getUsrInfo().getnUserId()))
                    viewHolder.photoView.setImageUrlAutoRotateEnabled(GlobalVar.getInstance().getUsrInfo().getcPhoto(), true);
                else
                    viewHolder.photoView.setImageUrlAutoRotateEnabled(item.getUserIcon(), true);

                if (item.getsHeadFrame() != null && !TextUtils.isEmpty(item.getsHeadFrame())) {
                    viewHolder.photoViewBg.setImageUrlAndReUse(item.getsHeadFrame());
                    viewHolder.photoViewBg.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.photoViewBg.setVisibility(View.GONE);
                }

                if (item.getiMemberLevel() > 0) {
                    viewHolder.levelView.setText("V" + item.getiMemberLevel());
                    viewHolder.levelView.setVisibility(View.VISIBLE);
                } else
                    viewHolder.levelView.setVisibility(View.GONE);


                boolean isReply = item.getcStatus().equals("3");
                viewHolder.replyContentHeaderView.setVisibility(isReply ? View.VISIBLE : View.GONE);
                viewHolder.replyContentView.setVisibility(isReply ? View.VISIBLE : View.GONE);
                viewHolder.replyContainer.setVisibility(isReply ? View.VISIBLE : View.GONE);
            } else {
                viewHolder.photoView.setVisibility(View.GONE);
                viewHolder.photoViewBg.setVisibility(View.GONE);
                viewHolder.ratingBarView.setVisibility(View.GONE);
                viewHolder.lineView.setVisibility(View.GONE);
                viewHolder.replyContentView.setVisibility(View.GONE);
                viewHolder.replyContentHeaderView.setVisibility(View.GONE);
                viewHolder.replyContainer.setVisibility(View.GONE);
                viewHolder.levelView.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    private void addLinkClick(TextView tv) {
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tv.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setText(style);
        }
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            mContext.startActivity(WebViewActivity.newIntent(mContext, mUrl));
        }
    }


    class ViewHolder {
        @Bind(R.id.tv_account_name)
        TextView nameView;
        @Bind(R.id.tv_create_time)
        TextView timeView;
        @Bind(R.id.tv_comment_content)
        TextView contentView;
        @Bind(R.id.iv_account_photo)
        FSSimpleImageView photoView;
        @Bind(R.id.iv_account_photo_bg)
        FSSimpleImageView photoViewBg;
        @Bind(R.id.rb_comment)
        RatingBar ratingBarView;
        @Bind(R.id.comment_line)
        View lineView;
        @Bind(R.id.tv_level)
        TextView levelView;

        @Bind(R.id.reply_container)
        View replyContainer;
        @Bind(R.id.tv_reply_content)
        TextView replyContentView;
        @Bind(R.id.tv_reply_content_header)
        TextView replyContentHeaderView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

    public void refreshData(List<CommentListModel.ModelItem> commentList) {
        this.mCommentList = commentList;
        notifyDataSetChanged();
    }

    public void release() {

    }
}
