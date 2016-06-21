package com.snailgame.cjg.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by TAJ_C on 2015/11/5.
 */
public class TaskColorEggsActivity extends Activity {

    @Bind(R.id.iv_color_eggs)
    ImageView colorEggsView;
    @Bind(R.id.iv_color_eggs_light)
    ImageView colorEggsLightView;
    @Bind(R.id.iv_click_surprise)
    ImageView click_surprise;
    @Bind(R.id.eggs_container)
    RelativeLayout eggsContainer;
    @Bind(R.id.dialog_layout)
    FrameLayout dialogView;
    Handler mHander = new Handler();
    //是否停止彩蛋左右摇晃的标志位
    private boolean isStopRotate;
    private String content = "";
    //彩蛋是否点击标志位
    private boolean isClick = true;

    //dialog是否显示的标志位
    private boolean isShowDilog;

    public static Intent newIntent(Context context, String content) {
        Intent intent = new Intent(context, TaskColorEggsActivity.class);
        intent.putExtra(AppConstants.EGGS_CONTENT, content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_color_egges);
        ButterKnife.bind(this);
        content = getIntent().getStringExtra(AppConstants.EGGS_CONTENT);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(colorEggsView, "scaleX", 0, 1.5f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(colorEggsView, "scaleY", 0, 1.5f, 1);
        set.playTogether(scaleX, scaleY);
        set.setTarget(colorEggsView);
        set.setDuration(600).start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startRotate();
                click_surprise.setVisibility(View.VISIBLE);
                isClick = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        colorEggsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClick) {
                    isClick = true;
                    click_surprise.setVisibility(View.GONE);
                    isStopRotate = true;
                    startLightAnim();
                    colorEggsView.setImageResource(R.drawable.task_eggs_broken1);
                    mHander.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            colorEggsView.setImageResource(R.drawable.task_eggs_broken2);
                        }
                    }, 100);
                }
            }
        });

    }

    /**
     * 显示dialog
     */
    private void showEggsDialog(final String content) {
        isShowDilog = true;
        colorEggsView.setVisibility(View.GONE);
        colorEggsLightView.setVisibility(View.GONE);
        click_surprise.setVisibility(View.GONE);
        final View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_eggs_popup, null);
        dialogView.addView(view);
        TextView messageView = ButterKnife.findById(view, R.id.message);
        Button comfirmBtn = ButterKnife.findById(view, R.id.btn_ok);
        messageView.setText(Html.fromHtml(content));
        comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isShowDilog) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x < dialogView.getLeft() || x > dialogView.getRight() || y < dialogView.getTop()
                        || y > dialogView.getBottom()) {
                    finish();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 开启彩蛋后面光晕的放大和渐变动画
     */
    private void startLightAnim() {
        colorEggsLightView.setVisibility(View.VISIBLE);
        Animation smallToBigAnim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        smallToBigAnim.setInterpolator(new DecelerateInterpolator());
        smallToBigAnim.setDuration(1000);
        colorEggsLightView.startAnimation(smallToBigAnim);
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofFloat(colorEggsLightView, "alpha", 1, 0).setDuration(2000).start();
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(colorEggsView, "alpha", 1, 0);
                objectAnimator.setDuration(2000).start();
            }
        }, 600);
        mHander.postDelayed(new Runnable() {

            @Override
            public void run() {
                showEggsDialog(content);
            }
        }, 1800);
    }

    /**
     * 开启彩蛋的左右摇晃动画
     */
    private void startRotate() {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(colorEggsView, "rotation", 0, -15, 5, -15, 5, -15, 5, -15, 5, -15, 5, -15, 5, 0);
        ViewHelper.setPivotX(colorEggsView, colorEggsView.getWidth() / 2f);
        ViewHelper.setPivotY(colorEggsView, colorEggsView.getHeight() / 1.4f);
        rotateAnimator.setDuration(1200);
        rotateAnimator.setRepeatMode(Animation.REVERSE);
        rotateAnimator.setInterpolator(new DecelerateInterpolator());
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHander.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isStopRotate)
                            startRotate();
                    }
                }, 600);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        rotateAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHander != null) {
            mHander.removeCallbacksAndMessages(null);
        }
    }
}