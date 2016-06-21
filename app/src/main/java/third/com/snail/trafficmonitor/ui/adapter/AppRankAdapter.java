package third.com.snail.trafficmonitor.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.data.bean.AppTraffic;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.ui.TimeBucketActivity;

/**
 * Created by kevin on 14/12/22.
 */
public class AppRankAdapter extends BaseAdapter {
    private Activity activity;

    private ArrayList<AppTraffic> mData;

    public static final int TYPE_MOBILE = 0x01;
    public static final int TYPE_WIFI = 0x02;
    public int type;

    @IntDef({TYPE_MOBILE, TYPE_WIFI})
    public @interface Type {
    }

    public AppRankAdapter(Activity activity, List<AppTraffic> data, @Type int type) {
        this.activity = activity;
        if (data != null) {
            mData = new ArrayList<>(data);
            Collections.sort(mData, comparator);
        }
        this.type = type;
    }

    public void setData(List<AppTraffic> data) {
        if (data != null && data.size() > 0) {
            mData = new ArrayList<>(data);
            Iterator<AppTraffic> iterator = mData.iterator();
            while (iterator.hasNext()) {
                AppTraffic appTraffic = iterator.next();
                if (type == TYPE_WIFI) {
                    long wifiBytes = appTraffic.wifiTxBytes + appTraffic.wifiRxBytes;
                    if (wifiBytes < 20000) {
                        iterator.remove();
                    }
                } else {
                    long MobileBytes = appTraffic.rxBytes + appTraffic.txBytes - appTraffic.wifiRxBytes - appTraffic.wifiTxBytes;
                    if (MobileBytes < 20000) {
                        iterator.remove();
                    }
                }
            }
            Collections.sort(mData, comparator);
        } else {
            mData = null;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData == null) {
            return null;
        }
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mData == null) {
            return 0;
        }
        return mData.get(position).uid;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.engine_item_app_rank_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppTraffic app = mData.get(position);
        holder.image.setImageDrawable(app.icon);
        holder.clickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (type == TYPE_MOBILE) {
                    intent = TimeBucketActivity.newTimeBucket(activity, 0, "" + app.appId, "" + app.appName);
                } else {
                    intent = TimeBucketActivity.newTimeBucket(activity, 1, "" + app.appId, "" + app.appName);
                }
                activity.startActivity(intent);
            }
        });
        holder.name.setText(app.appName);
        holder.name.setSelected(true);
        long cost;
        if (type == TYPE_MOBILE) {
            cost = app.rxBytes + app.txBytes - app.wifiRxBytes - app.wifiTxBytes;
        } else {
            cost = app.wifiRxBytes + app.wifiTxBytes;
        }
        holder.cost.setText(TrafficTool.getCost(cost));
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.cost)
        TextView cost;
        @Bind(R.id.click_relativeLayout)
        RelativeLayout clickItem;

        ViewHolder(View item) {
            ButterKnife.bind(this, item);
        }
    }

    private Comparator<AppTraffic> comparator = new Comparator<AppTraffic>() {
        @Override
        public int compare(AppTraffic lhs, AppTraffic rhs) {
            long l;
            long r;
            if (type == TYPE_WIFI) {
                l = lhs.wifiRxBytes + lhs.wifiTxBytes;
                r = rhs.wifiRxBytes + rhs.wifiTxBytes;
            } else {
                l = lhs.rxBytes + lhs.txBytes - lhs.wifiRxBytes - lhs.wifiTxBytes;
                r = rhs.rxBytes + rhs.txBytes - rhs.wifiRxBytes - rhs.wifiTxBytes;
            }
            if (l > r) {
                return -1;
            } else if (l == r) {
                return 0;
            } else {
                return 1;
            }
        }
    };
}
