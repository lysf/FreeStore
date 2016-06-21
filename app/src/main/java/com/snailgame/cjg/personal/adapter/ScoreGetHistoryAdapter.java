package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.ScoreGetHistoryFragment;
import com.snailgame.cjg.personal.model.ScoreGroupModel;
import com.snailgame.cjg.personal.model.ScoreHistoryModel;
import com.snailgame.cjg.personal.model.ScoreHistoryModel.ModelItem;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * 积分历史Adapter
 * created by xixh 14-08-20
 */
public class ScoreGetHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<ScoreGroupModel.ModelItem> lists;
    private final static String SCORE_USED = "1";    // 使用
    private final static String SCORE_GOT = "0";    // 获得

    public ScoreGetHistoryAdapter(Context context, List<ScoreGroupModel.ModelItem> lists) {
        this.context = context;
        this.lists = lists;
    }


    @Override
    public int getCount() {
        if (lists == null)
            return 0;

        return lists.size();

    }

    @Override
    public ScoreGroupModel.ModelItem getItem(int position) {
        if (lists == null)
            return null;

        if (position < lists.size())
            return lists.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_score_get_history, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ScoreGroupModel.ModelItem item = getItem(position);
        updateIndicator(holder, item);
        holder.scoreTimeTitleView.setText(getFormatData(item.getMonth(), "yyyy-MM", "yyyy年MM月") +
                ResUtil.getString(R.string.score_total_num, item.getIntegral()));
        holder.scoreValidityView.setText(ResUtil.getString(R.string.score_effective_time) +
                getFormatData(item.getValidity(), "yyyy-MM-dd", "yyyy.MM.dd"));
        holder.lineView.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        holder.expandableContainer.setTag(R.id.tag_animation, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                item.setIsExtend(!item.isExtend());
                updateIndicator(holder, item);
                if (item.isExtend()) {
                    getMonthScoreHistory(item, holder);
                }


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (false == item.isExtend()) {
                    holder.expandableContainer.removeAllViews();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return convertView;
    }

    private void getMonthScoreHistory(final ScoreGroupModel.ModelItem item, final ViewHolder holder) {
        // 创建获取积分记录
        String url = JsonUrl.getJsonUrl().JSON_URL_USER_SCORE_HISTORY + "?iIntegralType=2&number=1000&cMonth=" + item.getMonth();
        FSRequestHelper.newGetRequest(url, ScoreGetHistoryFragment.TAG,
                ScoreHistoryModel.class, new IFDResponse<ScoreHistoryModel>() {
                    @Override
                    public void onSuccess(ScoreHistoryModel model) {
                        //移除Loading
                        if (model == null || model.getCode() != 0) {
                            // 异常

                            return;
                        }

                        if (ListUtils.isEmpty(model.getItemList())) {
                            // show empty
                            return;
                        }

                        for (ModelItem record : model.getItemList()) {
                            View expandView = LayoutInflater.from(context).inflate(R.layout.item_score_history_expand, null);
                            ExpandViewHolder expandViewHolder = new ExpandViewHolder(expandView);
                            if (item != null) {
                                expandViewHolder.contentView.setText(record.getDesc());
                                expandViewHolder.timeView.setText(getFormatData(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss"));

                                if (record.getType().equals(SCORE_USED)) {
                                    expandViewHolder.changedNumView.setText(String.format(context.getString(R.string.score_used), Math.abs(record.getIntegral())));
                                } else {
                                    expandViewHolder.changedNumView.setText(String.format(context.getString(R.string.score_got), Math.abs(record.getIntegral())));
                                }

                            }

                            holder.expandableContainer.addView(expandView);
                        }
                    }

                    @Override
                    public void onNetWorkError() {
                    }

                    @Override
                    public void onServerError() {
                    }
                }, false, true, new ExtendJsonUtil());
    }

    private void updateIndicator(ViewHolder viewHolder, ScoreGroupModel.ModelItem item) {
        if (item.isExtend()) {
            viewHolder.toggleView.setImageResource(R.drawable.ic_red_small_down);
        } else {
            viewHolder.toggleView.setImageResource(R.drawable.ic_more);
        }
    }

    private String getFormatData(String time, String inputFormat, String outputFormat) {
        SimpleDateFormat formatInput = new SimpleDateFormat(inputFormat);
        SimpleDateFormat formatOutput = new SimpleDateFormat(outputFormat);
        Date dataInput = formatInput.parse(time, new ParsePosition(0));
        return formatOutput.format(dataInput);
    }


    public void refreshData(List<ScoreGroupModel.ModelItem> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.tv_score_time_title)
        TextView scoreTimeTitleView;

        @Bind(R.id.tv_score_validity)
        TextView scoreValidityView;

        @Bind(R.id.iv_arrow)
        ImageView toggleView;

        @Bind(R.id.expandable)
        LinearLayout expandableContainer;

        @Bind(R.id.line_view)
        View lineView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    class ExpandViewHolder {

        @Bind(R.id.tv_content)
        TextView contentView;
        @Bind(R.id.tv_changed_num)
        TextView changedNumView;
        @Bind(R.id.tv_time)
        TextView timeView;

        @Bind(R.id.line_view)
        View lineView;


        public ExpandViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
