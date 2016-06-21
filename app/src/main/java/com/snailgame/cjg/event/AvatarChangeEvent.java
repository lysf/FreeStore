package com.snailgame.cjg.event;

import android.graphics.Bitmap;

/**
 * Created by sunxy on 2015/3/13.
 */
public class AvatarChangeEvent extends BaseEvent {
    private Bitmap avatarBitmap;
    public AvatarChangeEvent(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
    }
    public Bitmap getAvatarBitmap() {
        return avatarBitmap;
    }
}
