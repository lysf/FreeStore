package third.com.snail.trafficmonitor.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.snailgame.cjg.R;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.AppWrapper;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.ui.widget.NetworkControlView;

/**
 * Created by kevin on 14/11/21.
 */
public class NetworkControlAdapter extends RecyclerView.Adapter<NetworkControlAdapter.ViewHolder> {
    private final String TAG = NetworkControlAdapter.class.getSimpleName();

    private Context context;
    private List<AppWrapper> mData;
//    private TableOperateTool<App> appTool;

    private boolean disableWifi;
    private boolean disableMobile;
    private String beginTag;

    public interface OnControlChangeListener {
        void onWifiChanged(ViewHolder holder, boolean checked) throws SQLException;
        void onMobileChanged(ViewHolder holder, boolean checked) throws SQLException;
    }

    private OnControlChangeListener listener;

    public NetworkControlAdapter(Context context, List<AppWrapper> data) {
        this.context = context;
//        appTool = new TableOperateTool<>(context, AppContract.CONTENT_URI);
        beginTag = context.getString(R.string.mobile_cost);
        updateData(data);
    }

    public void setOnControlChangeListener(OnControlChangeListener listener) {
        this.listener = listener;
    }

    public void updateData(List<AppWrapper> data) {
        mData = data;
        if (mData != null) {
            Collections.sort(mData, comparator);
        }
        notifyDataSetChanged();
    }

    public void updateSingleData(int position, boolean wifiChecked, boolean mobileChecked) throws SQLException {
        if (position == -1) return;
        AppWrapper wrapper = getItem(position);
        if (wrapper == null) return;
        wrapper.setWifiAccess(wifiChecked);
        wrapper.setMobileAccess(mobileChecked);
        mData.set(position, wrapper);
        new AppDao(context).update(wrapper.getApp());
    }

    public void disableController(boolean wifi, boolean mobile) {
        disableWifi = wifi;
        disableMobile = mobile;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder holder = new ViewHolder(new NetworkControlView(context));
        if (listener != null) {
            holder.view.setWifiListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        listener.onWifiChanged(holder, isChecked);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.view.setMobileListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        listener.onMobileChanged(holder, isChecked);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AppWrapper app = mData.get(position);
        if (disableWifi) {
            holder.view.disableWifi();
        }
        if (disableMobile) {
            holder.view.disableMobile();
        }
        holder.view.setTitle(app.getAppName());
        holder.view.setIcon(app.getIcon());
        holder.view.setWifiChecked(app.isWifiAccess());
        holder.view.setMobileChecked(app.isMobileAccess());
        long cost = app.getTotalRx() + app.getTotalTx() - app.getWifiTx() - app.getWifiRx();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(beginTag);
        sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_gray_light)),
                0, beginTag.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        String costStr = TrafficTool.getCost(cost);
        sb.append(costStr);
        sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black_r)),
                beginTag.length(), beginTag.length() + costStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.view.getMobileCostTv().setText(sb);
    }

    public AppWrapper getItem(int position) {
        if (mData == null) {
            return null;
        }
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NetworkControlView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (NetworkControlView) itemView;
        }

        public NetworkControlView getView() {
            return view;
        }
    }

    private Comparator<AppWrapper> comparator = new Comparator<AppWrapper>() {
        @Override
        public int compare(AppWrapper lhs, AppWrapper rhs) {
            long lcost;
            long rcost;
            lcost = lhs.getTotalRx() + lhs.getTotalTx() - lhs.getWifiRx() - lhs.getWifiTx();
            rcost = rhs.getTotalRx() + rhs.getTotalTx() - rhs.getWifiRx() - rhs.getWifiTx();
            if (lcost > rcost) {
                return -1;
            } else if (lcost < rcost) {
                return 1;
            }
            return 0;
        }
    };
}
