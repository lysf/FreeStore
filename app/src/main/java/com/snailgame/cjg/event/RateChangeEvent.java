package com.snailgame.cjg.event;

/**
 * Created by xixh on 2015/3/31.
 */
public class RateChangeEvent extends BaseEvent {
    private float rate;

    public RateChangeEvent(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }
}
