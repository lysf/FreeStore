package com.snailgame.cjg.event;

import com.snailgame.cjg.spree.model.SpreeGiftInfo;

/**
 * Created by xixh on 2015/12/01.
 */
public class SpreeTaoSuccessEvent {
    SpreeGiftInfo spreeGiftInfo;

    public SpreeTaoSuccessEvent(SpreeGiftInfo spreeGiftInfo) {
        this.spreeGiftInfo = spreeGiftInfo;
    }

    public SpreeGiftInfo getSpreeGiftInfo() {
        return spreeGiftInfo;
    }

    public void setSpreeGiftInfo(SpreeGiftInfo spreeGiftInfo) {
        this.spreeGiftInfo = spreeGiftInfo;
    }
}
