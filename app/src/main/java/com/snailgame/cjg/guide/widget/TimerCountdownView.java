package com.snailgame.cjg.guide.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yanHH on 2016/5/4.
 */
public class TimerCountdownView extends View {
    private final int ACTIONHEARTBEAT = 1;
    int mMaxSeconds = 0;//倒计时最大时间
    float mRateAngle = 0;//每100毫秒扫过的角度
    private float circleWidth = 5;//环形宽度
    private int circleColor = 0xfffb5052;//环形颜色
    private int defaultCircleColor = 0x66000000;//默认圆圈颜色（40%黑）
    private float startAngle = -90; //起始角度
    private float sweepAngle = 360; //扫描角度
    private int mDelayTime = 1 * 100;//间隔时间
    private CountdownTimerListener mListener;

    public TimerCountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ACTIONHEARTBEAT://每秒更新
                    startAngle += mRateAngle;
                    sweepAngle -= mRateAngle;
                    mMaxSeconds = mMaxSeconds - 100;
                    if (mMaxSeconds >= 0) {
                        invalidate();
                        mHandler.sendEmptyMessageDelayed(ACTIONHEARTBEAT, mDelayTime);
                        if (mListener != null) {
                            if (mMaxSeconds == 0) {
                                mListener.onTimeArrive(true);
                                mListener.onCountDown(0);
                            } else {
                                mListener.onTimeArrive(false);
                                mListener.onCountDown(mMaxSeconds / 1000 + 1);
                            }
                        }
                    }
                    break;
            }
        }
    };

    public void updateView() {
        invalidate();
        mHandler.sendEmptyMessageDelayed(ACTIONHEARTBEAT, mDelayTime);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDefaultCircle(canvas);
        drawCircle(canvas);
    }

    //绘制背景环
    public void drawDefaultCircle(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(defaultCircleColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(circleWidth);
        float left = circleWidth;
        float top = left;
        float right = getWidth() - left;
        float bottom = getHeight() - top;
        RectF oval = new RectF(left, top, right, bottom);
        canvas.drawArc(oval, 0, 360, false, paint);
    }

    //绘制环
    public void drawCircle(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(circleColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleWidth);
        float left = circleWidth;
        float top = left;
        float right = getWidth() - left;
        float bottom = getHeight() - top;
        RectF oval = new RectF(left, top, right, bottom);
        canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
    }

    /**
     * 设置初始最大时间
     *
     * @param seconds 单位秒
     */
    public void setMaxTime(int seconds) {
        mMaxSeconds = seconds * 1000;
        mRateAngle = 100 * sweepAngle / mMaxSeconds;
    }

    //销毁handler
    public void destroyHandler() {
        mHandler.removeMessages(ACTIONHEARTBEAT);
    }

    public void setCicleColor(int color) {
        circleColor = color;
    }

    public void setCicleWidth(int width) {
        circleWidth = width;
    }

    public interface CountdownTimerListener {
        void onCountDown(int seconds);//当前倒计时显示时间（秒）

        void onTimeArrive(boolean isArrive);//倒计时是否到达
    }

    public void addCountdownTimerListener(CountdownTimerListener listener) {
        mListener = listener;
    }
}
