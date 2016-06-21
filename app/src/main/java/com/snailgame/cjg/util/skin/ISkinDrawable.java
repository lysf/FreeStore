package com.snailgame.cjg.util.skin;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/**
 * Created by pancl on 2015/4/8.
 */
public interface ISkinDrawable {
    @DrawableRes int getDrawableResId();
    void onDrawableChanged(Drawable drawable);
    String getTag();
}
