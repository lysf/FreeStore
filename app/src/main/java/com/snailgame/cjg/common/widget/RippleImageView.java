package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.snailgame.cjg.R;

/**
 * Created by kevin on 15/4/2.
 */
public class RippleImageView extends FrameLayout {
    private ImageView imgView;

    int[] extra = new int[]{android.R.attr.scaleType, android.R.attr.foreground};

    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    public RippleImageView(Context context) {
        this(context, null);
    }

    public RippleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("ResourceType")
    public RippleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imgView = new ImageView(context);
        addView(imgView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TypedArray a = context.obtainStyledAttributes(attrs, extra);
        int scaleType = a.getInt(0, -1);
        if (scaleType != -1) {
            imgView.setScaleType(sScaleTypeArray[scaleType]);
        }
        Drawable fg = a.getDrawable(1);
        if (fg == null) {
            setForeground(getResources().getDrawable(R.drawable.ab_btn_selector));
        } else {
            setForeground(fg);
        }
        a.recycle();

        //添加 布局设置默认 src
        TypedArray b = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        Drawable drawable = b.getDrawable(R.styleable.CustomImageView_src);
        if (drawable != null) {
            imgView.setImageDrawable(drawable);
        }
        setClickable(true);
    }

    public ImageView getImgView() {
        return imgView;
    }

    public void setImageBitmap(Bitmap bm) {
        imgView.setImageBitmap(bm);
    }

    public void setImageDrawable(Drawable d) {
        imgView.setImageDrawable(d);
    }

    public void setImageResource(int resId) {
        imgView.setImageResource(resId);
    }
}
