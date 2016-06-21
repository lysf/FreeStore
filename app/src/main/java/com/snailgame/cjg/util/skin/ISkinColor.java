package com.snailgame.cjg.util.skin;

import android.support.annotation.ColorRes;

/**
 * Created by pancl on 2015/4/15.
 */
public interface ISkinColor {
    @ColorRes
    int getColorResId();
    void onColorChanged(int color);
    String getTag();
}
