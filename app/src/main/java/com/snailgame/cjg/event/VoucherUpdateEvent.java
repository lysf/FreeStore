package com.snailgame.cjg.event;

/**
 * 代金卷数量 变化刷新
 * Created by TAJ_C on 2015/6/30.
 */
public class VoucherUpdateEvent {
    int num;
    public VoucherUpdateEvent(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
