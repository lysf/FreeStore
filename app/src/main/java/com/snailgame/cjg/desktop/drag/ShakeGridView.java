package com.snailgame.cjg.desktop.drag;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunxy on 2015/4/22.
 */
public class ShakeGridView extends GridView {
    private boolean isEditModel=false;
    private List<ObjectAnimator> mWobbleAnimators = new LinkedList<>();

    public ShakeGridView(Context context) {
        super(context);
    }

    public ShakeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnScrollListener(scrollListener);
    }

    public ShakeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShakeGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Start edit mode without starting drag;
     */
    public void startEditMode() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isEditModel=true;
                startWobbleAnimation();
            }
        },400);

    }


    public void stopEditMode() {
        isEditModel=false;
        stopWobble(true);
    }

    private void startWobbleAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != null && Boolean.TRUE != v.getTag(R.id.dgv_wobble_tag)) {
                if (i % 2 == 0)
                    animateWobble(v);
                else
                    animateWobbleInverse(v);
                v.setTag(R.id.dgv_wobble_tag, true);
            }
        }
    }

    private void stopWobble(boolean resetRotation) {
        for (Animator wobbleAnimator : mWobbleAnimators) {
            wobbleAnimator.cancel();
        }
        mWobbleAnimators.clear();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != null) {
                if (resetRotation)
                    ViewHelper.setRotation(v, 0);
                v.setTag(R.id.dgv_wobble_tag, false);
            }
        }
    }

    private void animateWobble(View v) {
        ObjectAnimator animator = createBaseWobble(v);
        animator.setFloatValues(-2, 2);
        animator.start();
        mWobbleAnimators.add(animator);
    }

    private void animateWobbleInverse(View v) {
        ObjectAnimator animator = createBaseWobble(v);
        animator.setFloatValues(2, -2);
        animator.start();
        mWobbleAnimators.add(animator);
    }

    private ObjectAnimator createBaseWobble(final View v) {

        if (!isPreLollipop() && isPostHoneycomb())
            v.setLayerType(LAYER_TYPE_SOFTWARE, null);

        ObjectAnimator animator = new ObjectAnimator();
        animator.setDuration(180);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setPropertyName("rotation");
        animator.setTarget(v);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isPostHoneycomb())
                    v.setLayerType(LAYER_TYPE_NONE, null);
            }
        });
        return animator;
    }

    /**
     * The GridView from Android Lollipoop requires some different
     * setVisibility() logic when switching cells.
     *
     * @return true if OS version is less than Lollipop, false if not
     */
    public static boolean isPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void restartWobble() {
        stopWobble(false);
        startWobbleAnimation();
    }

    private boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
    OnScrollListener scrollListener=new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(isEditModel)
                startWobbleAnimation();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

}
