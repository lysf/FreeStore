
package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.snailgame.cjg.R;
import com.snailgame.cjg.personal.widget.AutofitTextView;
import com.snailgame.fastdev.util.ResUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MagicTextView extends AutofitTextView {

    // 递减/递增 的变量值
    private double mRate;
    // view 设置的值
    private double mValue;
    // 当前显示的值
    private double mCurValue;
    // 当前变化后最终状态的目标值
    private double mGalValue;
    // 控制加减法
    private int rate = 1;
    private boolean refreshing;
    private static final int REFRESH = 1;
    private DecimalFormat fnum = new DecimalFormat("0.00");
    private boolean isDouble = false;
    String galValueTxt;
    String curValeTxt;

    private Handler mHandler = new MsgHandler(this);


    public MagicTextView(Context context) {
        super(context);
    }

    public MagicTextView(Context context, AttributeSet set) {
        super(context, set);
    }

    public MagicTextView(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
    }


    public void setValue(int value) {
        mCurValue = 0;
        mGalValue = value;
        mValue = value;
        mRate = mValue / 20.00;
        BigDecimal b = new BigDecimal(mRate);
        mRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        mHandler.sendEmptyMessageAtTime(REFRESH, 500);
    }

    public void setValue(double value) {
        isDouble = true;
        mCurValue = 0.00;
        mGalValue = value;
        mValue = value;
        mRate = mValue / 20.00;
        BigDecimal b = new BigDecimal(mRate);
        mRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        mHandler.sendEmptyMessageAtTime(REFRESH, 500);
    }

    private void refreshView() {

        if (rate * mCurValue < mGalValue) {
            refreshing = true;

            if (isDouble) {
                galValueTxt = fnum.format(mGalValue);
                curValeTxt = fnum.format(mCurValue);
            } else {
                galValueTxt = String.valueOf((int) mGalValue);
                curValeTxt = String.valueOf((int) mCurValue);
            }


            StringBuffer lastTxt = new StringBuffer();
            for (int i = 0; i < (galValueTxt.length() - curValeTxt.length()); i++) {
                lastTxt.append(ResUtil.getString(R.string.spree_space));
            }
            lastTxt.append(curValeTxt);

            setText(lastTxt);
            mCurValue += mRate * rate;
            mHandler.sendEmptyMessageDelayed(REFRESH, 50);
        } else {
            refreshing = false;
            setText(isDouble ? fnum.format(mGalValue) : String.valueOf((int) mGalValue));
        }
    }

    static class MsgHandler extends Handler {
        private WeakReference<MagicTextView> mView;

        public MsgHandler(MagicTextView view) {
            this.mView = new WeakReference<MagicTextView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            MagicTextView view = mView.get();
            if (view != null) {
                switch (msg.what) {
                    case REFRESH:
                        view.refreshView();
                        break;
                    default:
                        break;
                }
            }

        }
    }
}
