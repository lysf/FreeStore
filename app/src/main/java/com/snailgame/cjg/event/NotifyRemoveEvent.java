package com.snailgame.cjg.event;

/**
 * 移除通知消息
 * Created by xixh on 2016/2/24.
 */
public class NotifyRemoveEvent extends BaseEvent {
    private long notifyId;

    public NotifyRemoveEvent(long notifyId) {
        this.notifyId = notifyId;
    }

    public long getNotifyId() {
        return notifyId;
    }
}
