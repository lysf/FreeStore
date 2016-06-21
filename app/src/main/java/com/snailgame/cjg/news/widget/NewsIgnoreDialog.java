package com.snailgame.cjg.news.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.event.NewsIgnoreEvent;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.news.adpter.NewsIgnoreTagAdapter;
import com.snailgame.cjg.news.model.NewsIgnoreModel;
import com.snailgame.cjg.news.widget.flowlayout.TagFlowLayout;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PhoneUtil;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by TAJ_C on 2016/3/25.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewsIgnoreDialog {
    private Context context;
    /**
     * 内容在三角形上面
     */
    public static final int GRAVITY_TOP = 0;
    /**
     * 内容在三角形下面
     */
    public static final int GRAVITY_BOTTOM = 1;

    /**
     * 对话框本身
     */
    private Dialog dialog;
    /**
     * 坐标
     */
    private int[] location;
    /**
     * 提醒框位置
     */
    private int gravity;


    @Bind(R.id.iv_triangle)
    ImageView triangleView;

    @Bind(R.id.rl_content)
    RelativeLayout contentContainer;


    @Bind(R.id.rlOutsideBackground)
    RelativeLayout rlOutsideBackground;

    @Bind(R.id.gv_ignore_content)
    TagFlowLayout contentView;

    @Bind(R.id.tv_choose_num)
    TextView numView;
    @Bind(R.id.tv_choose_tip)
    TextView tv_tip;

    @Bind(R.id.btn_sure)
    TextView sureView;

    private boolean touchOutsideDismiss;

    private NewsIgnoreTagAdapter ignoreAdatper;

    private String articleId; //资讯ID

    private static final int NUM_START = 2;

    public NewsIgnoreDialog(final Context context, String articleId, List<NewsIgnoreModel> ignoreList) {
        this.context = context;
        this.articleId = articleId;
        if (ignoreList == null) ignoreList = new ArrayList<>();
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_news_ignore, null);
        ButterKnife.bind(this, dialogView);

        ViewTreeObserver viewTreeObserver = dialogView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //当View可以获取宽高的时候，设置view的位置
                relocation(location);

            }
        });

        setTouchOutsideDismiss(true);

        ignoreAdatper = new NewsIgnoreTagAdapter(this.context, ignoreList, contentView);

        contentView.setAdapter(ignoreAdatper);
        ignoreAdatper.setOnRefreshNumLister(new NewsIgnoreTagAdapter.OnRefreshNumListener() {
            @Override
            public void refreshNumView(int num) {

                sureView.setText(num == 0 ? R.string.news_no_interest : R.string.btn_sure);
                sureView.setBackgroundResource(num == 0 ? R.drawable.btn_red_selector : R.drawable.btn_green_selector);
                tv_tip.setVisibility(num == 0 ? View.VISIBLE : View.GONE);
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(ResUtil.getString(R.string.news_ignore_num_title, num));
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#eb413d")), NUM_START, String.valueOf(num).length() + NUM_START, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                numView.setText(num == 0 ? context.getString(R.string.news_dialog_title) : builder);
            }
        });

        dialog = new Dialog(this.context, isFullScreen() ?
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen : android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(dialogView);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onEasyDialogDismissed != null) {
                    onEasyDialogDismissed.onDismissed();
                }
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (onEasyDialogShow != null) {
                    onEasyDialogShow.onShow();
                }
            }
        });

        animatorSetForDialogShow = new AnimatorSet();
        animatorSetForDialogDismiss = new AnimatorSet();

        objectAnimatorsForDialogShow = new ArrayList<>();
        objectAnimatorsForDialogDismiss = new ArrayList<>();
        ini();

    }


    @OnClick(R.id.btn_sure)
    public void uploadIgnoreResult() {
        String parameters = AccountUtil.getLoginParams().replace("?", "") + "&sTags=" + (ListUtils.isEmpty(ignoreAdatper.getSelectedData()) ? "" :
                JSON.toJSONString(ignoreAdatper.getSelectedData())) + "&cImei=" + PhoneUtil.getDeviceUUID(context) + "&iNewsId=" + articleId;

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_NEWS_NO_INTEREST, context.getClass().getSimpleName(), BaseDataModel.class, new IFDResponse<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel baseDataModel) {
                MainThreadBus.getInstance().post(new NewsIgnoreEvent(articleId));
                onDialogDismiss();
            }

            @Override
            public void onNetWorkError() {
                onDialogDismiss();
            }

            @Override
            public void onServerError() {
                onDialogDismiss();
            }
        }, parameters);
    }

    final View.OnTouchListener outsideBackgroundListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (touchOutsideDismiss && dialog != null) {
                onDialogDismiss();
            }
            return false;
        }
    };

    /**
     * The Dialog instance
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * 初始化默认值
     */
    private void ini() {
        //设置标题
        numView.setText(context.getString(R.string.news_dialog_title));

        sureView.setText(R.string.news_no_interest);
        this.setLocation(new int[]{0, 0})
                .setGravity(GRAVITY_BOTTOM)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(24, 24);
    }


    /**
     * 设置三角形所在的位置
     */
    public NewsIgnoreDialog setLocation(int[] location) {
        this.location = location;
        return this;
    }

    /**
     * 设置三角形所在的位置
     * location.x坐标值为attachedView所在屏幕位置的中心
     * location.y坐标值依据当前的gravity，如果gravity是top，则为控件上方的y值，如果是bottom，则为控件的下方的y值
     *
     * @param attachedView 在哪个View显示提示信息
     */
    public NewsIgnoreDialog setLocationByAttachedView(View attachedView) {
        if (attachedView != null) {
            this.attachedView = attachedView;
            int[] attachedViewLocation = new int[2];
            attachedView.getLocationOnScreen(attachedViewLocation);
            //手动设置 比300dp 小的就在上方显示
            gravity = attachedViewLocation[1] - ComUtil.dpToPx(300) > 0 ? GRAVITY_TOP : GRAVITY_BOTTOM;
            setGravity(gravity);
            switch (gravity) {
                case GRAVITY_BOTTOM:
                    attachedViewLocation[0] += attachedView.getWidth() / 2 - ComUtil.dpToPx(5);
                    attachedViewLocation[1] += attachedView.getHeight();
                    break;
                case GRAVITY_TOP:
                    attachedViewLocation[0] += attachedView.getWidth() / 2 - ComUtil.dpToPx(5);
                    break;

            }

            setLocation(attachedViewLocation);
        }
        return this;
    }


    //dp 转 px
    public int dpToPx(int dp) {
        DisplayMetrics metrics = ResUtil.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    /**
     * 对话框所依附的View
     */
    private View attachedView = null;

    public View getAttachedView() {
        return this.attachedView;
    }


    public void setView(View view) {
        int[] viewScreenPosition = new int[2];
        view.getLocationOnScreen(viewScreenPosition);


    }

    /**
     * 设置显示的内容在上方还是下方，如果设置错误，默认是在下方
     */
    private NewsIgnoreDialog setGravity(int gravity) {
        if (gravity != GRAVITY_BOTTOM && gravity != GRAVITY_TOP) {
            gravity = GRAVITY_BOTTOM;
        }
        this.gravity = gravity;
        switch (this.gravity) {
            case GRAVITY_BOTTOM:
                triangleView.setBackgroundResource(R.drawable.ic_news_ignore_top);
                break;
            case GRAVITY_TOP:
                triangleView.setBackgroundResource(R.drawable.ic_news_ignore_bottom);
                break;
        }


        contentContainer.setBackgroundResource(R.drawable.news_ignore_dialog_bg);
        return this;
    }

    /**
     * 设置是否填充屏幕，如果不填充就适应布局内容的宽度，显示内容的位置会尽量随着三角形的位置居中
     */
    public NewsIgnoreDialog setMatchParent(boolean matchParent) {
        ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
        layoutParams.width = matchParent ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        contentContainer.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 距离屏幕左右的边距
     */
    public NewsIgnoreDialog setMarginLeftAndRight(int left, int right) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();
        layoutParams.setMargins(left, 0, right, 0);
        contentContainer.setLayoutParams(layoutParams);
        return this;
    }

    public NewsIgnoreDialog setMarginTopAndBottom(int top, int bottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();
        layoutParams.setMargins(0, top, 0, bottom);
        contentContainer.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 设置触摸对话框外面，对话框是否消失
     */
    public NewsIgnoreDialog setTouchOutsideDismiss(boolean touchOutsideDismiss) {
        this.touchOutsideDismiss = touchOutsideDismiss;
        if (touchOutsideDismiss) {
            rlOutsideBackground.setOnTouchListener(outsideBackgroundListener);
        } else {
            rlOutsideBackground.setOnTouchListener(null);
        }
        return this;
    }


    /**
     * 显示提示框
     */
    public NewsIgnoreDialog show() {
        if (dialog != null) {

            dialog.show();
            onDialogShowing();
        }
        return this;
    }


    /**
     * 横向
     */
    public static final int DIRECTION_X = 0;
    /**
     * 纵向
     */
    public static final int DIRECTION_Y = 1;


    /**
     * 水平动画
     *
     * @param direction 动画的方向
     * @param duration  动画执行的时间长度
     * @param values    动画移动的位置
     */
    public NewsIgnoreDialog setAnimationTranslationShow(int direction, int duration, float... values) {
        return setAnimationTranslation(true, direction, duration, values);
    }

    /**
     * 水平动画
     *
     * @param direction 动画的方向
     * @param duration  动画执行的时间长度
     * @param values    动画移动的位置
     */
    public NewsIgnoreDialog setAnimationTranslationDismiss(int direction, int duration, float... values) {
        return setAnimationTranslation(false, direction, duration, values);
    }

    private NewsIgnoreDialog setAnimationTranslation(boolean isShow, int direction, int duration, float... values) {
        if (direction != DIRECTION_X && direction != DIRECTION_Y) {
            direction = DIRECTION_X;
        }
        String propertyName = "";
        switch (direction) {
            case DIRECTION_X:
                propertyName = "translationX";
                break;
            case DIRECTION_Y:
                propertyName = "translationY";
                break;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), propertyName, values)
                .setDuration(duration);
        if (isShow) {
            objectAnimatorsForDialogShow.add(animator);
        } else {
            objectAnimatorsForDialogDismiss.add(animator);
        }
        return this;
    }

    /**
     * 对话框出现时候的渐变动画
     *
     * @param duration 动画执行的时间长度
     * @param values   动画移动的位置
     */
    public NewsIgnoreDialog setAnimationAlphaShow(int duration, float... values) {
        return setAnimationAlpha(true, duration, values);
    }

    /**
     * 对话框消失时候的渐变动画
     *
     * @param duration 动画执行的时间长度
     * @param values   动画移动的位置
     */
    public NewsIgnoreDialog setAnimationAlphaDismiss(int duration, float... values) {
        return setAnimationAlpha(false, duration, values);
    }

    private NewsIgnoreDialog setAnimationAlpha(boolean isShow, int duration, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), "alpha", values).setDuration(duration);
        if (isShow) {
            objectAnimatorsForDialogShow.add(animator);
        } else {
            objectAnimatorsForDialogDismiss.add(animator);
        }
        return this;
    }

    private AnimatorSet animatorSetForDialogShow;
    private AnimatorSet animatorSetForDialogDismiss;
    private List<Animator> objectAnimatorsForDialogShow;
    private List<Animator> objectAnimatorsForDialogDismiss;


    private void onDialogShowing() {
        if (animatorSetForDialogShow != null && objectAnimatorsForDialogShow != null && objectAnimatorsForDialogShow.size() > 0) {
            animatorSetForDialogShow.playTogether(objectAnimatorsForDialogShow);
            animatorSetForDialogShow.start();
        }
        //TODO 缩放的动画效果不好，不能从控件所在的位置开始缩放
//        ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), "scaleX", 0.3f, 1.0f).setDuration(500).start();
//        ObjectAnimator.ofFloat(rlOutsideBackground.findViewById(R.id.rlParentForAnimate), "scaleY", 0.3f, 1.0f).setDuration(500).start();
    }

    @SuppressLint("NewApi")
    private void onDialogDismiss() {
        if (animatorSetForDialogDismiss.isRunning()) {
            return;
        }
        if (animatorSetForDialogDismiss != null && objectAnimatorsForDialogDismiss != null && objectAnimatorsForDialogDismiss.size() > 0) {
            animatorSetForDialogDismiss.playTogether(objectAnimatorsForDialogDismiss);
            animatorSetForDialogDismiss.start();
            animatorSetForDialogDismiss.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //这里有可能会有bug，当Dialog所依赖的Activity关闭的时候，如果这个时候，用户关闭对话框，由于对话框的动画关闭需要时间，当动画执行完毕后，对话框所依赖的Activity已经被销毁了，执行dismiss()就会报错
                    if (context != null && context instanceof Activity) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!((Activity) context).isDestroyed()) {
                                dialog.dismiss();
                            }
                        } else {
                            try {
                                dialog.dismiss();
                            } catch (final IllegalArgumentException e) {

                            } catch (final Exception e) {

                            } finally {
                                dialog = null;
                            }
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            dialog.dismiss();
        }
    }

    /**
     * 关闭提示框
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            onDialogDismiss();
        }
    }

    /**
     * 根据x，y，重新设置控件的位置
     * 因为setX setY为0的时候，都是在状态栏以下的，所以app不是全屏的话，需要扣掉状态栏的高度
     */

    private void relocation(int[] location) {
        float statusBarHeight = isFullScreen() ? 0.0f : getStatusBarHeight();

        triangleView.setX(location[0] - triangleView.getWidth() / 2);
        triangleView.setY(location[1] - triangleView.getHeight() / 2 - statusBarHeight);
        switch (gravity) {
            case GRAVITY_BOTTOM:
                contentContainer.setY(location[1] - triangleView.getHeight() / 2 - statusBarHeight + triangleView.getHeight());
                break;
            case GRAVITY_TOP:
                contentContainer.setY(location[1] - contentContainer.getHeight() - statusBarHeight - triangleView.getHeight() / 2);
                break;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();
        switch (gravity) {
            case GRAVITY_BOTTOM:
            case GRAVITY_TOP:
                int triangleCenterX = (int) (triangleView.getX() + triangleView.getWidth() / 2);
                int contentWidth = contentContainer.getWidth();
                int rightMargin = getScreenWidth() - triangleCenterX;
                int leftMargin = getScreenWidth() - rightMargin;
                int availableLeftMargin = leftMargin - layoutParams.leftMargin;
                int availableRightMargin = rightMargin - layoutParams.rightMargin;
                int x = 0;
                if (contentWidth / 2 <= availableLeftMargin && contentWidth / 2 <= availableRightMargin) {
                    x = triangleCenterX - contentWidth / 2;
                } else {
                    if (availableLeftMargin <= availableRightMargin) {
                        x = layoutParams.leftMargin;
                    } else {
                        x = getScreenWidth() - (contentWidth + layoutParams.rightMargin);
                    }
                }
                contentContainer.setX(x);
                break;

        }
    }

    /**
     * 获取屏幕的宽度
     */

    private int getScreenWidth() {
        DisplayMetrics metrics = ResUtil.getDisplayMetrics();
        return metrics.widthPixels;
    }


    /**
     * 获取状态栏的高度
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ResUtil.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断下当前要显示对话框的Activity是否是全屏
     */
    public boolean isFullScreen() {
        int flg = ((Activity) context).getWindow().getAttributes().flags;
        boolean flag = false;
        if ((flg & 1024) == 1024) {
            flag = true;
        }
        return flag;
    }

    /**
     * 设置是否可以按返回按钮取消
     */
    public NewsIgnoreDialog setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    private OnEasyDialogDismissed onEasyDialogDismissed;

    public NewsIgnoreDialog setOnEasyDialogDismissed(OnEasyDialogDismissed onEasyDialogDismissed) {
        this.onEasyDialogDismissed = onEasyDialogDismissed;
        return this;
    }

    /**
     * Dialog is dismissed
     */
    public interface OnEasyDialogDismissed {
        public void onDismissed();
    }

    private OnEasyDialogShow onEasyDialogShow;

    public NewsIgnoreDialog setOnEasyDialogShow(OnEasyDialogShow onEasyDialogShow) {
        this.onEasyDialogShow = onEasyDialogShow;
        return this;
    }

    /**
     * Dialog is showing
     */
    public interface OnEasyDialogShow {
        public void onShow();
    }
}
