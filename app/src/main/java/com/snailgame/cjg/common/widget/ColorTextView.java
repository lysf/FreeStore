package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.snailgame.cjg.util.ComUtil;

/**
 * Created by TAJ_C on 2016/5/24.
 */
public class ColorTextView extends TextView {
    private int textColor;
    private int borderColor;

    public ColorTextView(Context context) {
        super(context);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setColor(int color) {
        borderColor = color;
        setTextColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int stroke = 2;
        float half = 1;
        RectF rectF = new RectF(half, half, getWidth()-half, getHeight()-half);
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);

        canvas.drawRoundRect(rectF, ComUtil.dpToPx(2),ComUtil.dpToPx(2), paint);
    }


}
