package com.snailgame.cjg.event;

/**
 * Created by TAJ_C on 2016/2/14.
 */
public class AppAppointmentEvent extends BaseEvent{
    private int appId;

    public AppAppointmentEvent(int appId) {
        this.appId = appId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
