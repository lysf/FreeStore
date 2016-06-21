package com.snailgame.cjg.event;

import com.snailgame.cjg.spree.model.SpreeGiftInfo;

/**
 * Created by TAJ_C on 2015/5/5.
 */
public class SpreeGetSuccessEvent {
    SpreeGiftInfo spreeGiftInfo;

    public SpreeGetSuccessEvent(SpreeGiftInfo spreeGiftInfo) {
        this.spreeGiftInfo = spreeGiftInfo;
    }

    public SpreeGiftInfo getSpreeGiftInfo() {
        return spreeGiftInfo;
    }

    public void setSpreeGiftInfo(SpreeGiftInfo spreeGiftInfo) {
        this.spreeGiftInfo = spreeGiftInfo;
    }
}
