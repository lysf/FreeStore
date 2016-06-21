package third.com.snail.trafficmonitor.engine.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import third.com.snail.trafficmonitor.engine.service.TrafficMonitor;
import third.com.snail.trafficmonitor.engine.util.OperatorFlags;

/**
 * Created by kevin on 15/1/5.
 */
public class TrafficDataUpdater extends BroadcastReceiver {
    private final String TAG = TrafficDataUpdater.class.getSimpleName();

    public interface OnTrafficListener {
        void onDataUpdated();
    }
    private OnTrafficListener listener;

    public static IntentFilter getIntentFilter() {
        return new IntentFilter(TrafficMonitor.BROADCAST_TRAFFIC_UPDATE);
    }

    public static void requestUpdate(Activity activity) {
        Intent intent = new Intent(activity, TrafficMonitor.class);
        intent.setFlags(OperatorFlags.RequestInActivity);
        intent.setAction(TrafficMonitor.ACTION_PICK_POINT);
        activity.startService(intent);
    }

    public TrafficDataUpdater(OnTrafficListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            listener.onDataUpdated();
        }
    }
}
