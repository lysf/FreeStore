package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.personal.UserTaskFragment;
import com.snailgame.cjg.personal.model.TaskModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的任务列表adapter
 */
public class UserTaskAdapter extends BaseAdapter {


    private static final String EXPERIENCE = "experience";
    private static final String INTEGRAL = "integral";
    private static final String MONEY = "money";
    private List<TaskModel> taskModelList;

    private Context context;
    private LayoutInflater layoutInflater;
    private String exp, score, tutu;
    private boolean isMember;

    public UserTaskAdapter(Context context, List<TaskModel> taskModelList, boolean isMember) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.taskModelList = taskModelList;
        this.isMember = isMember;
        exp = ResUtil.getString(R.string.exp_string);
        score = ResUtil.getString(R.string.slide_menu_point);
        tutu = ResUtil.getString(R.string.slide_menu_currency);
    }

    @Override
    public int getCount() {
        if (taskModelList == null) {
            return 0;
        } else {
            return taskModelList.size();
        }
    }

    @Override
    public TaskModel getItem(int position) {
        return taskModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (isMember)
                convertView = layoutInflater.inflate(R.layout.personal_member_task_item, parent, false);
            else
                convertView = layoutInflater.inflate(R.layout.personal_task_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        initializeViews(getItem(position), position, convertView);

        return convertView;
    }

    private void initializeViews(final TaskModel taskModel, int position, View convertView) {
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        if (taskModel != null) {
            resetViews(holder);
            holder.taskName.setText(taskModel.getsGroupName());


            if (taskModel.getcGroupStatus() == UserTaskFragment.TASK_STATE_COMPLETE) { //已领取
                holder.taskStatus.setText(ResUtil.getString(R.string.complete));
                ComUtil.setDrawableTop(holder.taskStatus, R.drawable.task_comeplete);
            } else if (taskModel.getcGroupStatus() == UserTaskFragment.TASK_STATE_UNRECEIVE) {
                //已完成
                holder.taskStatus.setText(ResUtil.getString(R.string.task_receive_reward));
                ComUtil.setDrawableTop(holder.taskStatus, R.drawable.task_receive_gift);
            } else {
                TaskModel.ConfigItem configItem = taskModel.getConfigItem();
                if (configItem != null && (configItem.getType() == UserTaskFragment.TYPE_DOWNLOAD ||
                        configItem.getType() == UserTaskFragment.TYPE_PERSOANL_DATA ||
                        configItem.getType() == UserTaskFragment.TYPE_SEARCH)) {
                    //去完成
                    holder.taskStatus.setText(ResUtil.getString(R.string.task_go_work));
                    ComUtil.setDrawableTop(holder.taskStatus, R.drawable.task_go_work);
                } else {
                    //未完成
                    holder.taskStatus.setText(ResUtil.getString(R.string.begin_task));
                    ComUtil.setDrawableTop(holder.taskStatus, R.drawable.task_uncompelete);
                }

            }
            holder.taskIcon.setImageUrlAndReUse(taskModel.getcLogo());
            holder.taskStatus.setTag(taskModel.getcGroupStatus());
            holder.lineView.setVisibility(position <= getCount() - 2 ? View.VISIBLE : View.GONE);
            List<TaskModel.Award> awardList = taskModel.getAwardList();
            if (awardList != null) {
                for (TaskModel.Award award : awardList) {
                    String awardValue = "+" + award.getValue();
                    if (award.getName().equals(EXPERIENCE)) {
                        holder.expValue.setVisibility(View.VISIBLE);
                        holder.expValue.setText(getSpannableString(awardValue + exp, awardValue.length()));
                    }
                    if (award.getName().equals(INTEGRAL)) {
                        holder.scoreValue.setVisibility(View.VISIBLE);
                        holder.scoreValue.setText(getSpannableString(awardValue + score, awardValue.length()));

                    }

                    if (award.getName().equals(MONEY)) {
                        holder.tutuValue.setVisibility(View.VISIBLE);
                        holder.tutuValue.setText(getSpannableString(awardValue + tutu, awardValue.length()));
                    }

                }
                if (holder.expValue.getVisibility() == View.VISIBLE && (holder.scoreValue.getVisibility() == View.VISIBLE || holder.tutuValue.getVisibility() == View.VISIBLE)) {
                    holder.exp_divider.setVisibility(View.VISIBLE);
                }
                if ((holder.scoreValue.getVisibility() == View.VISIBLE) && (holder.tutuValue.getVisibility() == View.VISIBLE)) {
                    holder.score_divider.setVisibility(View.VISIBLE);
                }

                boolean isShowAll = false;
                if (holder.expValue.getVisibility() == View.VISIBLE && holder.scoreValue.getVisibility() == View.VISIBLE && holder.tutuValue.getVisibility() == View.VISIBLE) {
                    isShowAll = true;
                }
                //如果 只显示两个，则分割线两边边距为8， 三个则为2
                LinearLayout.LayoutParams expParams = (LinearLayout.LayoutParams) holder.exp_divider.getLayoutParams();
                expParams.setMargins(isShowAll ? ComUtil.dpToPx(2) : ComUtil.dpToPx(8), 0, isShowAll ? ComUtil.dpToPx(2) : ComUtil.dpToPx(8), 0);

                LinearLayout.LayoutParams scoreParams = (LinearLayout.LayoutParams) holder.score_divider.getLayoutParams();
                scoreParams.setMargins(isShowAll ? ComUtil.dpToPx(2) : ComUtil.dpToPx(8), 0, isShowAll ? ComUtil.dpToPx(2) : ComUtil.dpToPx(8), 0);
            }

            List<TaskModel.MemberAward> memberAwardList = taskModel.getMemberAwardList();
            if (memberAwardList != null && holder.member_exp != null) {
                String level = null;
                String value = null;
                for (TaskModel.MemberAward award : memberAwardList) {
                    if (award.getName().equals(INTEGRAL)) {
                        level = award.getLevel();
                        value = award.getValue();
                        break;
                    }
                }

                if (TextUtils.isEmpty(level) || TextUtils.isEmpty(value))
                    holder.member_exp.setVisibility(View.INVISIBLE);
                else {
                    holder.member_exp.setVisibility(View.VISIBLE);
                    holder.member_exp.setText(ResUtil.getString(R.string.member_task_exp, level, value));
                }
            }


        }
    }

    /**
     * 重置显示view
     *
     * @param holder
     */
    private void resetViews(ViewHolder holder) {
        holder.taskName.setText("");
        holder.taskStatus.setText("");
        if (holder.member_exp != null)
            holder.member_exp.setVisibility(View.INVISIBLE);
        holder.expValue.setVisibility(View.GONE);
        holder.scoreValue.setVisibility(View.GONE);
        holder.tutuValue.setVisibility(View.GONE);
        holder.exp_divider.setVisibility(View.GONE);
        holder.score_divider.setVisibility(View.GONE);

    }

    public void refreshData(List<TaskModel> taskModelList) {
        this.taskModelList = taskModelList;
        notifyDataSetChanged();
    }

    private SpannableStringBuilder getSpannableString(String content, int size) {
        SpannableStringBuilder stringBuilder =
                new SpannableStringBuilder(content);
        if (stringBuilder.length() > 0) {
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.search_history_hot_title_color)),
                    0, size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return stringBuilder;
    }

    protected class ViewHolder {
        @Bind(R.id.task_icon)
        FSSimpleImageView taskIcon;
        @Bind(R.id.task_name)
        TextView taskName;

        @Bind(R.id.task_status)
        TextView taskStatus;
        @Nullable
        @Bind(R.id.member_exp)
        TextView member_exp;
        @Bind(R.id.exp_value)
        TextView expValue;
        @Bind(R.id.score_value)
        TextView scoreValue;
        @Bind(R.id.tutu_value)
        TextView tutuValue;

        @Bind(R.id.exp_divider)
        View exp_divider;
        @Bind(R.id.score_divider)
        View score_divider;

        @Bind(R.id.line_view)
        View lineView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
