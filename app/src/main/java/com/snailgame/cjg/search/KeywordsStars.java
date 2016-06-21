package com.snailgame.cjg.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class KeywordsStars extends FrameLayout implements OnGlobalLayoutListener {
    public static final int IDX_X = 0;
    public static final int IDX_Y = 1;
    public static final int IDX_TXT_LENGTH = 2;
    public static final int IDX_TXT_HEIGHT = 3;

    /**
     * 由外至内的动画。
     */
    public static final int ANIMATION_IN = 1;

    /**
     * 由内至外的动画。
     */
    public static final int ANIMATION_OUT = 2;

    /**
     * 位移动画类型：从外围移动到坐标点。
     */
    public static final int OUTSIDE_TO_LOCATION = 1;

    /**
     * 位移动画类型：从坐标点移动到外围。
     */
    public static final int LOCATION_TO_OUTSIDE = 2;

    /**
     * 位移动画类型：从中心点移动到坐标点。
     */
    public static final int CENTER_TO_LOCATION = 3;

    /**
     * 位移动画类型：从坐标点移动到中心点。
     */
    public static final int LOCATION_TO_CENTER = 4;

    public static final long ANIM_DURATION = 800l;

    private OnClickListener itemClickListener;

    private static Interpolator interpolator;

    private static AlphaAnimation animAlpha2Opaque;

    private static AlphaAnimation animAlpha2Transparent;

    private static ScaleAnimation animScaleLarge2Normal, animScaleNormal2Large,
            animScaleZero2Normal, animScaleNormal2Zero;

    /**
     * 存储显示的关键字。
     */
    private Vector<String> vecKeywords;

    private int width, height;

    /**
     * go2Show()中被赋值为true，标识开发人员触发其开始动画显示。<br/>
     * 本标识的作用是防止在填充keywrods未完成的过程中获取到width和height后提前启动动画。<br/>
     * 在show()方法中其被赋值为false。<br/>
     * 真正能够动画显示的另一必要条件：width 和 height不为0。<br/>
     */
    private boolean enableShow;

    private Random random;

    /**
     * @see ANIMATION_IN
     * @see ANIMATION_OUT
     * @see OUTSIDE_TO_LOCATION
     * @see LOCATION_TO_OUTSIDE
     * @see LOCATION_TO_CENTER
     * @see CENTER_TO_LOCATION
     */
    private int txtAnimInType, txtAnimOutType;

    /**
     * 最近一次启动动画显示的时间。
     */
    private long lastStartAnimationTime;

    /**
     * 动画运行时间。
     */
    private long animDuration;

    /**
     * 随机颜色
     */
    private int[] colors;
    private List<Integer> fontColorList;
    /**
     * 随机字体大小
     */
    private float[] fontsizes;
    private List<Float> fontSizeList;
    private float scale;

    public KeywordsStars(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KeywordsStars(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeywordsStars(Context context) {
        super(context);
        init();
    }

    private void init() {
        lastStartAnimationTime = 0l;
        lastStartAnimationTime = 0l;
        animDuration = ANIM_DURATION;
        random = new Random();
        vecKeywords = new Vector<String>();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        interpolator = AnimationUtils.loadInterpolator(getContext(),
                android.R.anim.decelerate_interpolator);
        animAlpha2Opaque = new AlphaAnimation(0.0f, 1.0f);
        animAlpha2Transparent = new AlphaAnimation(1.0f, 0.0f);
        animScaleLarge2Normal = new ScaleAnimation(2, 1, 2, 1);
        animScaleNormal2Large = new ScaleAnimation(1, 2, 1, 2);
        animScaleZero2Normal = new ScaleAnimation(0, 1, 0, 1);
        animScaleNormal2Zero = new ScaleAnimation(1, 0, 1, 0);

        colors = new int[]{
                ResUtil.getColor(R.color.keyword_color_blue),
                ResUtil.getColor(R.color.btn_green_normal),
                ResUtil.getColor(R.color.btn_yellow_normal),
                ResUtil.getColor(R.color.common_oval_red)};
        fontsizes = new float[]{
                getResources().getDimension(R.dimen.text_size_little),
                getResources().getDimension(R.dimen.text_size_normal),
                getResources().getDimension(R.dimen.keyword_size_16),
                getResources().getDimension(R.dimen.keyword_size_20)};
        fontSizeList = new ArrayList<Float>();
        fontColorList = new ArrayList<Integer>();
        initFontColorList(0);
        initFontSizeList(0);
        scale = getResources().getDisplayMetrics().density;
    }

    private void initFontColorList(float filterFontColor) {
        fontColorList.clear();
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] != filterFontColor) {
                fontColorList.add(colors[i]);
            }
        }
    }

    private void initFontSizeList(float filterFontSize) {
        fontSizeList.clear();
        for (int i = 0; i < fontsizes.length; i++) {
            if (fontsizes[i] != filterFontSize) {
                fontSizeList.add(fontsizes[i]);
            }
        }
    }

    public long getDuration() {
        return animDuration;
    }

    public void setDuration(long duration) {
        animDuration = duration;
    }

    public boolean feedKeyword(String keyword) {
        boolean result = false;
        result = vecKeywords.add(keyword);
        return result;
    }

    /**
     * 开始动画显示。<br/>
     * 之前已经存在的TextView将会显示退出动画。<br/>
     *
     * @return 正常显示动画返回true；反之为false。返回false原因如下：<br/>
     * 1.时间上不允许，受lastStartAnimationTime的制约；<br/>
     * 2.未获取到width和height的值。<br/>
     */
    public boolean go2Show(int animType) {
        if (System.currentTimeMillis() - lastStartAnimationTime > animDuration) {
            enableShow = true;
            if (animType == ANIMATION_IN) {
                txtAnimInType = OUTSIDE_TO_LOCATION;
                txtAnimOutType = LOCATION_TO_CENTER;
            } else if (animType == ANIMATION_OUT) {
                txtAnimInType = CENTER_TO_LOCATION;
                txtAnimOutType = LOCATION_TO_OUTSIDE;
            }
            disapper();
            boolean result = show();
            return result;
        }
        return false;
    }

    private void disapper() {
        int size = getChildCount();
        for (int i = size - 1; i >= 0; i--) {
            final TextView txt = (TextView) getChildAt(i);
            if (txt.getVisibility() == View.GONE) {
                removeView(txt);
                continue;
            }
            FrameLayout.LayoutParams layParams = (LayoutParams) txt
                    .getLayoutParams();
            // Log.d("ANDROID_LAB", txt.getText() + " leftM=" +
            // layParams.leftMargin + " topM=" + layParams.topMargin
            // + " width=" + txt.getWidth());
            int[] xy = new int[]{layParams.leftMargin, layParams.topMargin,
                    txt.getWidth()};
            AnimationSet animSet = getAnimationSet(xy, (width >> 1),
                    (height >> 1), txtAnimOutType);
            txt.startAnimation(animSet);
            animSet.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    txt.setOnClickListener(null);
                    txt.setClickable(false);
                    txt.setVisibility(View.GONE);
                }
            });
        }
    }

//	public String getRandColor() {
//		String r, g, b, b1;
//		Random random = new Random();
//		r = Integer.toHexString(random.nextInt(256)).toLowerCase();
//		g = Integer.toHexString(random.nextInt(256)).toLowerCase();
//		b = Integer.toHexString(random.nextInt(256)).toLowerCase();
//		r = r.length() == 1 ? "0" + r : r;
//		g = g.length() == 1 ? "0" + g : g;
//		b = b.length() == 1 ? "0" + b : b;
//		return r + g + b;
//	}

    private boolean show() {
        if (width > 0 && height > 0 && vecKeywords != null
                && vecKeywords.size() > 0 && enableShow) {
            enableShow = false;
            lastStartAnimationTime = System.currentTimeMillis();// 找到中心点

            int currentLeftMargin = 0;
            int currentTopMargin = 0;
            int lastRowHeight = 0;
            int currentRowIndex = 0;
            List<TextView> currentRowView = new ArrayList<TextView>();
            initFontColorList(0);
            initFontSizeList(0);

            for (String keyword : vecKeywords) {
                // 随机颜色
                int ranFontColor = 0;
                if (fontColorList.size() > 0) {
                    ranFontColor = fontColorList.remove(random.nextInt(fontColorList.size()));
                } else {
                    ranFontColor = colors[random.nextInt(colors.length)];
                }
                // 随机字体大小
                float ranfontScaleSize = 0;
                if (fontSizeList.size() > 0) {
                    ranfontScaleSize = fontSizeList.remove(random.nextInt(fontSizeList.size()));
                } else {
                    ranfontScaleSize = fontsizes[random.nextInt(fontsizes.length)];
                }
                float ranfontSize = ranfontScaleSize / scale;

                // 实例化TextView
                final TextView txt = new TextView(getContext());
                txt.setOnClickListener(itemClickListener);
                txt.setText(keyword);
                txt.setTextColor(ranFontColor);
                txt.setGravity(Gravity.CENTER);
                txt.setSingleLine();
                txt.setEllipsize(TextUtils.TruncateAt.END);
                // 获取随机距左距顶长度值
                int ranLeft = (int) (getResources().getDimension(R.dimen.keyword_padding_width) / scale);
                int ranTop = (int) (getResources().getDimension(R.dimen.keyword_padding_height) / scale);
                int ran = (int) (getResources().getDimension(R.dimen.keyword_padding_random) / scale);
                ranLeft = random.nextInt(ran) + ranLeft;
                ranTop = random.nextInt(ran) + ranTop;

//				txt.setShadowLayer(1, 1, 1, 0xdd696969);

                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, ranfontSize);
                // 获取文本长度
                Paint paint = txt.getPaint();
                int strWidth = (int) Math.ceil(paint.measureText(keyword));
                FontMetrics fm = paint.getFontMetrics();
                int strHeight = (int) Math.ceil(fm.bottom - fm.top);

                if (currentRowView.size() + 1 > fontsizes.length || currentLeftMargin + strWidth + ranLeft > width) {
                    lastRowHeight = currentTopMargin;
                    adjustRowView(currentRowView, width - currentLeftMargin);
                    currentLeftMargin = 0;
                    currentRowView.clear();
                    currentRowIndex++;
                    initFontColorList(ranFontColor);
                    initFontSizeList(ranfontScaleSize);
                }
                if (lastRowHeight + strWidth + ranTop > height) {
                    continue;
                }

                FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                layParams.gravity = Gravity.LEFT | Gravity.TOP;
                layParams.leftMargin = currentLeftMargin + ranLeft;
                layParams.topMargin = lastRowHeight + ranTop;
                addView(txt, layParams);
                currentRowView.add(txt);

                int[] params = new int[4];
                params[IDX_TXT_LENGTH] = strWidth;
                params[IDX_TXT_HEIGHT] = strHeight;
                params[IDX_X] = layParams.leftMargin;
                params[IDX_Y] = layParams.topMargin;

                txt.setTag(R.id.tag_animation, params);
                txt.setTag(R.id.tag_row_index, currentRowIndex);

                currentLeftMargin += ranLeft + strWidth;
                currentTopMargin = Math.max(lastRowHeight + strHeight + ranTop, currentTopMargin);
            }
            adjustRowView(currentRowView, width - currentLeftMargin);
            currentRowView.clear();
            adjustRowHeightAndStartAnimation(currentTopMargin, currentRowIndex);
            return true;
        }
        return false;
    }

    /**
     * 调整每行行高，然后设置动画
     *
     * @param currentTopMargin 当前所有行最高高度
     * @param lastRowIndex     总行数减去一
     */
    private void adjustRowHeightAndStartAnimation(int currentTopMargin, int lastRowIndex) {
        if (height > currentTopMargin) {
            int heightMore = height - currentTopMargin;
            int heightSplit = lastRowIndex > 0 ? ((heightMore / 2) / lastRowIndex) : heightMore >> 1;
            int xCenter = width >> 1, yCenter = height >> 1;
            for (int i = 0; i < getChildCount(); i++) {
                TextView tv = (TextView) getChildAt(i);

                //check if it is new keyword textview, the old one who has no tag
                if (tv.getTag(R.id.tag_animation) != null) {
                    FrameLayout.LayoutParams fl = (LayoutParams) tv
                            .getLayoutParams();
                    int splitNum = (Integer) tv.getTag(R.id.tag_row_index);
                    fl.topMargin += lastRowIndex > 0 ? (heightSplit * splitNum + heightMore / 4) : heightSplit;

                    int[] params = (int[]) tv.getTag(R.id.tag_animation);
                    params[IDX_Y] = fl.topMargin;
                    // 动画
                    AnimationSet animSet = getAnimationSet(params, xCenter, yCenter,
                            txtAnimInType);
                    tv.startAnimation(animSet);
                    tv.setTag(R.id.tag_animation, null);
                }
            }
        }
    }

    /**
     * 调整每行TextView左右间距
     *
     * @param currentRowView 当前行集合
     * @param widthMore      多余的右侧宽度
     */
    private void adjustRowView(List<TextView> currentRowView, int widthMore) {
        if (currentRowView.size() > 0 && widthMore > 0) {
            int filterViewCount = currentRowView.size() - 1;
            if (filterViewCount > 0) {
                int widthSplit = (widthMore * 4 / 5) / filterViewCount;
                for (int i = 1; i <= filterViewCount; i++) {
                    FrameLayout.LayoutParams fl = (LayoutParams) currentRowView
                            .get(i).getLayoutParams();
                    fl.leftMargin += widthSplit * i;
                }
            } else {
                if (currentRowView.get(0) != null) {
                    FrameLayout.LayoutParams fl = (LayoutParams) currentRowView
                            .get(0).getLayoutParams();
                    fl.leftMargin += widthMore >> 1;
                }
            }
        }
    }

    public AnimationSet getAnimationSet(int[] xy, int xCenter, int yCenter,
                                        int type) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(interpolator);
        if (type == OUTSIDE_TO_LOCATION) {
            animSet.addAnimation(animAlpha2Opaque);
            animSet.addAnimation(animScaleLarge2Normal);
            TranslateAnimation translate = new TranslateAnimation((xy[IDX_X]
                    + (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0,
                    (xy[IDX_Y] - yCenter) << 1, 0);
            animSet.addAnimation(translate);
        } else if (type == LOCATION_TO_OUTSIDE) {
            animSet.addAnimation(animAlpha2Transparent);
            animSet.addAnimation(animScaleNormal2Large);
            TranslateAnimation translate = new TranslateAnimation(0, (xy[IDX_X]
                    + (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0,
                    (xy[IDX_Y] - yCenter) << 1);
            animSet.addAnimation(translate);
        } else if (type == LOCATION_TO_CENTER) {
            animSet.addAnimation(animAlpha2Transparent);
            animSet.addAnimation(animScaleNormal2Zero);
            TranslateAnimation translate = new TranslateAnimation(0,
                    (-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter));
            animSet.addAnimation(translate);
        } else if (type == CENTER_TO_LOCATION) {
            animSet.addAnimation(animAlpha2Opaque);
            animSet.addAnimation(animScaleZero2Normal);
            TranslateAnimation translate = new TranslateAnimation(
                    (-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter), 0);
            animSet.addAnimation(translate);
        }
        animSet.setDuration(animDuration);
        return animSet;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onGlobalLayout() {
        int tmpW = getWidth();
        int tmpH = getHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (width != tmpW || height != tmpH) {
            width = tmpW;
            height = tmpH;
            show();

        }
    }

    public void rubKeywords() {
        vecKeywords.clear();
    }

    /**
     * 直接清除所有的TextView。在清除之前不会显示动画。
     */
    public void rubAllViews() {
        removeAllViews();
    }

    public void setOnItemClickListener(OnClickListener listener) {
        itemClickListener = listener;
    }
}
