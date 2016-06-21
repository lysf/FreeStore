package com.snailgame.cjg.personal;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by lic on 2015/12/17.
 * <p/>
 * 5.0以上的机型设置statusbar透明，且statusbar仅仅浮在页面上方。
 */
public class TransparentStatusBarActivity extends SwipeBackActivity {

    private Window window;
    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    Drawable mToolbarDrawable;
    @Nullable
    @Bind(R.id.tv_title)
    protected TextView toolbarTitle;
    @Nullable
    @Bind(R.id.tv_right_action)
    protected TextView toolbarRight;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreenDisplay();
        super.onCreate(savedInstanceState);
    }


    /**
     * 设置页面的toolBar
     *
     * @param title     左侧标题显示文字
     * @param rightText 右侧按钮显示文字
     */
    protected void setupToolbar(@Nullable String title, @Nullable String rightText) {
        if (mToolBar == null)
            return;
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (toolbarTitle != null && title != null) {
            toolbarTitle.setText(title);
            toolbarTitle.setTextColor(ResUtil.getColor(R.color.white));
        }
        if (toolbarRight != null && rightText != null) {
            toolbarRight.setText(rightText);
            toolbarRight.setTextColor(ResUtil.getColor(R.color.white));
        }
        mToolbarDrawable = mToolBar.getBackground();
        setToolbarMarginTop();
        setToolbarStatusBarAlpha(0);
    }

    /**
     * 由于status是透明需要设置actionbar和顶部状态栏的距离
     */
    private void setToolbarMarginTop() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolBar.getLayoutParams();
        if (params != null)
            params.topMargin = ComUtil.getStatesBarHeight();
    }

    /**
     * 设置在5.0以上全屏显示
     */
    private void fullScreenDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * 设置actionbar和5.0以上的状态栏的透明度
     */
    public void setToolbarStatusBarAlpha(int alpha) {
        if (mToolbarDrawable != null)
            mToolbarDrawable.setAlpha(alpha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alpha <= 30)
                window.setStatusBarColor(ResUtil.getColor(R.color.translucent_15_black));
            else {
                window.setStatusBarColor(Color.argb(alpha, 214, 69, 70));
            }
        }
    }
}
