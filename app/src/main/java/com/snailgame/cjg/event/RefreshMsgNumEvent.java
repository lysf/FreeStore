package com.snailgame.cjg.event;

/**
 * 刷新 未读消息数目
 * Created by TAJ_C on 2015/3/13.
 */
public class RefreshMsgNumEvent extends BaseEvent {
    int count;

    public RefreshMsgNumEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
