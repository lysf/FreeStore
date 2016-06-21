package com.snailgame.cjg.friend.utils;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 测量ListView高度
 * Created by yanHH on 2016/5/19.
 */
public class MeasureListViewHeightUtil {
    public static int getTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }
        return totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
    }
}
