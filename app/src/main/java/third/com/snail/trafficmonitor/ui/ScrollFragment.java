package third.com.snail.trafficmonitor.ui;

import android.widget.AbsListView;

/**
 * Created by kevin on 14/12/16.
 */
public abstract class ScrollFragment extends BaseFragment implements ScrollTabHolder {
    protected ScrollTabHolder mScrollTabHolder;

    public void setScrollTabHolder(ScrollTabHolder holder) {
        mScrollTabHolder = holder;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
    }
}
