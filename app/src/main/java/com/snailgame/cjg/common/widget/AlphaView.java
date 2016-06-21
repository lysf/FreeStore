package com.snailgame.cjg.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

import com.snailgame.cjg.global.AppConstants;

/*
 * created by xixh 09-22-2014
 */
@SuppressLint("NewApi")
public class AlphaView extends LinearLayout {
	public boolean onHideAnimation = false;
	public AlphaView(Context context) {
		super(context);
	}

	public AlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AlphaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public void setVisibility(int visibility) {
		if (visibility == VISIBLE) {
			onHideAnimation = false;
			super.setVisibility(visibility);
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
	        animation.setDuration(AppConstants.POP_WINDOW_ANIMATION_DURATION);
	        setAnimation(animation);
		} else if (visibility == GONE) {
			onHideAnimation = true;
			Animation animation = new AlphaAnimation(1.0f, 0.0f);
	        animation.setDuration(AppConstants.POP_WINDOW_ANIMATION_DURATION);
	        startAnimation(animation);
	        animation.setAnimationListener(new AnimationListener() {
	            
	            @Override
	            public void onAnimationStart(Animation animation) {

	            }
	            
	            @Override
	            public void onAnimationRepeat(Animation animation) {
	            }
	            
	            @Override
	            public void onAnimationEnd(Animation animation) {
					onHideAnimation = false;
	            	AlphaView.this.setVisibility(GONE, true);
	            }
	        });
		} else {
			onHideAnimation = false;
			super.setVisibility(visibility);
		}
	}
	
	public void setVisibility(int visibility, boolean bSuper) {
		if (bSuper) {
			super.setVisibility(visibility);
		} else {
			setVisibility(visibility);
		}
	}
}
