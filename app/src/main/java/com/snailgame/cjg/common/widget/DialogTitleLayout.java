package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

/**
 * 所有dialog的自定义title
 * Created by lic on 2015/5/7.
 */
public class DialogTitleLayout extends LinearLayout {

    private Context context;
    private boolean hasIcon;
    private boolean isRed;
    private String titleText;
    private TextView textView;

    public DialogTitleLayout(Context context) {
        super(context);
        this.context = context;
    }

    public DialogTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DialogTitle);
        hasIcon = a.getBoolean(R.styleable.DialogTitle_hasIcon, false);
        isRed = a.getBoolean(R.styleable.DialogTitle_isRed, false);
        titleText = a.getString(R.styleable.DialogTitle_titleText);
        a.recycle();
        setContent();
    }

    private void setContent() {
        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params;
        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (titleText != null) {
            textView.setText(titleText);
        }
        textView.setPadding((int) ResUtil.getDimension(R.dimen.dialog_title_padding), (int) ResUtil.getDimension(R.dimen.dialog_title_padding)
                , (int) ResUtil.getDimension(R.dimen.dialog_title_padding), (int) ResUtil.getDimension(R.dimen.dialog_title_padding));
        if (hasIcon) {
            Drawable drawable;
            if (isRed) {
                drawable = ResUtil.getDrawable(R.drawable.dialog_title_red);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setTextColor(ResUtil.getColor(R.color.red_light));
                textView.setCompoundDrawables(drawable, null, null, null);
                textView.setCompoundDrawablePadding((int) ResUtil.getDimension(R.dimen.dialog_icon_padding));
            } else {
                drawable = ResUtil.getDrawable(R.drawable.dialog_title_green);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setTextColor(ResUtil.getColor(R.color.green_light));
                textView.setCompoundDrawables(drawable, null, null, null);
                textView.setCompoundDrawablePadding((int) ResUtil.getDimension(R.dimen.dialog_icon_padding));
            }
        } else {
            if (isRed) {
                textView.setTextColor(ResUtil.getColor(R.color.red_light));
            } else {
                textView.setTextColor(ResUtil.getColor(R.color.green_light));
            }
        }
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(textView, params);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        View view = new View(context);
        if (isRed) {
            view.setBackgroundColor(ResUtil.getColor(R.color.red_light));
        } else {
            view.setBackgroundColor(ResUtil.getColor(R.color.green_light));
        }
        addView(view, params);
    }

    /**
     * 改变dialog title的显示文字
     */
    public void setText(String title) {
        textView.setText(title);
        invalidate();
    }

    /**
     * 改变dialog title的样式为绿色或者红色
     */
    public void setIsRed(boolean isRed, String titleText) {
        removeAllViews();
        this.isRed = isRed;
        this.titleText = titleText;
        setContent();
        invalidate();
    }

}
