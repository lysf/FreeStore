/*
 * Copyright 2011, 2015 Kai Liao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package third.BottomSheet;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.snailgame.cjg.R;

import java.lang.reflect.Method;

import butterknife.ButterKnife;


/**
 * One way to present a set of actions to a user is with bottom sheets, a sheet of paper that slides up from the bottom edge of the screen. Bottom sheets offer flexibility in the display of clear and simple actions that do not need explanation.
 * <p/>
 * https://www.google.com/design/spec/components/bottom-sheets.html
 * <p/>
 * Project: BottomSheet
 * Created by Kai Liao on 2014/9/21.
 */
@SuppressWarnings("unused")
public abstract class BottomSheet extends Dialog implements DialogInterface {

    private int mStatusBarHeight;

    private final SparseIntArray hidden = new SparseIntArray();

    // translucent support
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
    private boolean mInPortrait;
    private String sNavBarOverride;
    private boolean mNavBarAvailable;
    private float mSmallestWidthDp;

    private int limit = -1;
    private boolean cancelOnTouchOutside = true;
    private boolean cancelOnSwipeDown = true;

    private OnDismissListener dismissListener;
    private ClosableSlidingLayout mDialogView;

    public BottomSheet(Context context) {
        super(context, R.style.BottomSheet_Dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public BottomSheet(Context context, int theme) {
        super(context, theme);
        // https://github.com/jgilfelt/SystemBarTint/blob/master/library/src/com/readystatesoftware/systembartint/SystemBarTintManager.java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mInPortrait = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            try {
                Class c = Class.forName("android.os.SystemProperties");
                @SuppressWarnings("unchecked") Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }

            // check theme attrs
            int[] as = {android.R.attr.windowTranslucentNavigation};
            TypedArray a = context.obtainStyledAttributes(as);
            try {
                mNavBarAvailable = a.getBoolean(0, false);
            } finally {
                a.recycle();
            }

            // check window flags
            WindowManager.LayoutParams winParams = ((Activity) context).getWindow().getAttributes();

            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if ((winParams.flags & bits) != 0) {
                mNavBarAvailable = true;
            }

            mSmallestWidthDp = getSmallestWidthDp(wm);
            if (mNavBarAvailable)
                setTranslucentStatus(true);
            mStatusBarHeight = getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
        }
    }

    @SuppressLint("NewApi")
    private float getSmallestWidthDp(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wm.getDefaultDisplay().getRealMetrics(metrics);
        } else {
            //this is not correct, but we don't really care pre-kitkat
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    @TargetApi(14)
    private int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar(context)) {
                String key;
                if (mInPortrait) {
                    key = NAV_BAR_HEIGHT_RES_NAME;
                } else {
                    if (!isNavigationAtBottom())
                        return 0;
                    key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                }
                return getInternalDimensionSize(res, key);
            }
        }
        return result;
    }

    @TargetApi(14)
    private boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag (see static block)
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Should a navigation bar appear at the bottom of the screen in the current
     * device configuration? A navigation bar may appear on the right side of
     * the screen in certain configurations.
     *
     * @return True if navigation should appear at the bottom of the screen, False otherwise.
     */
    private boolean isNavigationAtBottom() {
        return (mSmallestWidthDp >= 600 || mInPortrait);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        cancelOnTouchOutside = cancel;
    }

    /**
     * Sets whether this dialog is canceled when swipe it down
     *
     * @param cancel whether this dialog is canceled when swipe it down
     */
    public void setCanceledOnSwipeDown(boolean cancel) {
        cancelOnSwipeDown = cancel;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("SameParameterValue")
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);
        // instance
        win.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    public void setContentDialogView(ClosableSlidingLayout mDialogView) {
        this.mDialogView = mDialogView;
        init();
    }

    public abstract AbsListView getAbsListView();

    public void init() {
        setContentView(mDialogView);
        ButterKnife.bind(this);
        if (!cancelOnSwipeDown)
            mDialogView.swipeable = cancelOnSwipeDown;
        mDialogView.setSlideListener(new ClosableSlidingLayout.SlideListener() {
            @Override
            public void onClosed() {
                BottomSheet.this.dismiss();
            }

            @Override
            public void onOpened() {

            }
        });
        int[] location = new int[2];
        mDialogView.getLocationOnScreen(location);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDialogView.setPadding(mDialogView.getPaddingLeft(), location[0] == 0 ? mStatusBarHeight + mDialogView.getPaddingTop() : mDialogView.getPaddingTop(),
                    mDialogView.getPaddingRight(), mDialogView.getPaddingBottom());
            mDialogView.getChildAt(0).setPadding(mDialogView.getChildAt(0).getPaddingLeft(), mDialogView.getChildAt(0).getPaddingTop(),
                    mDialogView.getChildAt(0).getPaddingRight(),
                    mNavBarAvailable ? getNavigationBarHeight(getContext()) + mDialogView.getChildAt(0).getPaddingBottom() : mDialogView.getChildAt(0).getPaddingBottom());
        }

        if (getAbsListView() != null) {
            mDialogView.mTarget = getAbsListView();
        }
        mDialogView.setCollapsible(false);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(cancelOnTouchOutside);
    }


}
