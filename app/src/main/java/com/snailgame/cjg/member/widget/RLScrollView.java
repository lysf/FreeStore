package com.snailgame.cjg.member.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 计算移动距离的 scroll view
 * Created by TAJ_C on 2015/12/10.
 */
public class RLScrollView extends ScrollView {
    public RLScrollView(Context context) {
        super(context);
    }

    public RLScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RLScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollchanged(l, t, oldl, oldt);
        }
    }

    public interface OnScrollChangedListener {
        void onScrollchanged(int x, int y, int oldxX, int oldY);
    }

    private OnScrollChangedListener onScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.onScrollChangedListener = listener;
    }
}
