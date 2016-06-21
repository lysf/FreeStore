package third.com.snail.trafficmonitor.ui;

import android.widget.AbsListView;

/**
 * Created by kevin on 14/12/16.
 */
public interface ScrollTabHolder {
    void adjustScroll(int scrollHeight);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
