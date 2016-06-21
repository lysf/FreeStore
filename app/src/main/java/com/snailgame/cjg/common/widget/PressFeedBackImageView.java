package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by yftx on 7/3/14.
 * 点击变灰的ImageView
 */
public class PressFeedBackImageView extends ImageView {
    private boolean pressed = false;

    public PressFeedBackImageView(Context context) {
        super(context);
    }

    public PressFeedBackImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressFeedBackImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pressed) {
            canvas.drawColor(ResUtil.getColor(R.color.transparent_cover));
        }
    }
}
