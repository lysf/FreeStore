package com.snailgame.cjg.event;

/**
 * Created by sunxy on 2015/3/13.
 */
public class UpdateNotificationEvent extends BaseEvent {

    /*进入通知中心，更新所有的hasRead字段时需要作出差异，然后发送广播，通知中心页面也会收到广播，此为标志位，防止通知中心页面进入死循环*/
    public boolean isAllHasReadUpdate;

    public UpdateNotificationEvent(boolean isAllHasReadUpdate) {
        this.isAllHasReadUpdate = isAllHasReadUpdate;

    }

    public UpdateNotificationEvent() {

    }
}
