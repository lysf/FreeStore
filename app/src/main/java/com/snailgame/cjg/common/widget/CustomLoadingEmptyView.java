package com.snailgame.cjg.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义LoadingView View 不可见动画消失
 * Created by sunxy on 2015/1/16.
 */
@SuppressLint("NewApi")
public class CustomLoadingEmptyView extends LinearLayout {
    public CustomLoadingEmptyView(Context context) {
        super(context);
    }

    public CustomLoadingEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLoadingEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setVisibility(int visibility) {


        if((visibility==GONE||visibility==INVISIBLE)&&getChildCount()>1){
            LinearLayout linearLayout=(LinearLayout)getChildAt(1);
            if(linearLayout!=null) {
                LinearLayout innerLayout = (LinearLayout) linearLayout.getChildAt(0);
                if (innerLayout != null) {
                    ImageView imageView = (ImageView) innerLayout.getChildAt(0);
                    if (imageView != null) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
                        if (animationDrawable != null && animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                }
            }

        }
        super.setVisibility(visibility);
    }

}
