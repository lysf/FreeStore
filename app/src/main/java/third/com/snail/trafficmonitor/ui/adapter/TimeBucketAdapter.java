package third.com.snail.trafficmonitor.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.snailgame.cjg.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.data.bean.TrafficInfo;
import third.com.snail.trafficmonitor.ui.widget.VerticalProgressBar;


/**
 * Created by lic on 2014/12/16.
 */
public class TimeBucketAdapter extends RecyclerView.Adapter<TimeBucketAdapter.ViewHolder> {

    private List<TrafficInfo> dataList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private long maxBytes = -1;
    private ArrayList<TrafficInfo> sortList = new ArrayList<>();
    private int currentDate;
    private int selsectPosition;

    public TimeBucketAdapter(ArrayList<TrafficInfo> list, Context context) {
        super();
        this.context = context;
        setDataList(list);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setDataList(ArrayList<TrafficInfo> list) {
        this.dataList = list;
        for (TrafficInfo trafficInfo : dataList) {
            sortList.add(trafficInfo);
        }
        Collections.sort(sortList);
        if (sortList != null && sortList.size() > 0) {
            maxBytes = sortList.get(0).getBytes();
        }
        currentDate = Calendar.getInstance().get(Calendar.DATE);
        selsectPosition = currentDate - 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.engine_item_recycleview, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (dataList.get(position).getDayOfWeek()) {
            case Calendar.SUNDAY:
                holder.weekTextView.setText("SUN");
                break;
            case Calendar.MONDAY:
                holder.weekTextView.setText("MON");
                break;
            case Calendar.TUESDAY:
                holder.weekTextView.setText("TUE");
                break;
            case Calendar.WEDNESDAY:
                holder.weekTextView.setText("WEN");
                break;
            case Calendar.THURSDAY:
                holder.weekTextView.setText("THU");
                break;
            case Calendar.FRIDAY:
                holder.weekTextView.setText("FRI");
                break;
            case Calendar.SATURDAY:
                holder.weekTextView.setText("SAT");
                break;
        }
        holder.MonthTextView.setText("" + dataList.get(position).getDay());
        long total = dataList.get(position).getBytes();
        DecimalFormat sDf = new DecimalFormat("0.00");

        if (position - currentDate >= 0) {
            holder.numTextView.setText("-");
        } else {
            if (total != 0) {
                holder.numTextView.setText(sDf.format((double) total / 1024 / 1024));
            } else {
                holder.numTextView.setText(total / 1024 / 1024 + "");
            }
        }
        int progress;
        if (maxBytes == 0 || total == 0) {
            progress = 0;
        } else {
            if (total == maxBytes) {
                progress = 100;
            } else {
                float scale = (float) ((double) total / (double) maxBytes);
                progress = (int) (100 * scale);
            }
            if (progress <= 1) {
                progress = 1;
            }
        }
        if (dataList.get(holder.getPosition()).isChecked()) {
            if (dataList.get(holder.getPosition()).isShowAnimation()) {
                ObjectAnimator animation = ObjectAnimator.ofInt(holder.verticalProgressBar, "Progress", progress);
                animation.setDuration(500);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
                dataList.get(holder.getPosition()).setShowAnimation(false);
            } else {
                holder.verticalProgressBar.setProgress(progress);
            }
            holder.verticalProgressBar.setSecondaryProgress(0);
            holder.weekTextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.MonthTextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.numTextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.numTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        } else {
            if (dataList.get(holder.getPosition()).isShowAnimation()) {
                ObjectAnimator animation = ObjectAnimator.ofInt(holder.verticalProgressBar, "secondaryProgress", progress);
                animation.setDuration(500);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
                dataList.get(holder.getPosition()).setShowAnimation(false);
            } else {
                holder.verticalProgressBar.setSecondaryProgress(progress);
            }
            holder.verticalProgressBar.setProgress(0);
            holder.weekTextView.setTextColor(context.getResources().getColor(R.color.progress_text_gray));
            holder.MonthTextView.setTextColor(context.getResources().getColor(R.color.black_r));
            holder.numTextView.setTextColor(context.getResources().getColor(R.color.black_r));
            holder.numTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getPosition() != selsectPosition) {
                    dataList.get(holder.getPosition()).setChecked(true);
                    dataList.get(selsectPosition).setChecked(false);
                    selsectPosition = holder.getPosition();
                    onItemClickListener.onItemClick(holder.getPosition());
                    notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_week)
        TextView weekTextView;
        @Bind(R.id.tv_month)
        TextView MonthTextView;
        @Bind(R.id.tv_traffic_num)
        TextView numTextView;
        @Bind(R.id.app_cost_progressbar)
        VerticalProgressBar verticalProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            numTextView.setSelected(true);
            verticalProgressBar.setMax(100);
            verticalProgressBar.setCurrMode(VerticalProgressBar.MODE_BOTTOM);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
