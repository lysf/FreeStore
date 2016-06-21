package com.snailgame.cjg.event;

/**
 * Created by TAJ_C on 2015/7/14.
 */
public class ServiceStopEvent extends BaseEvent{
    private int serviceType;

    public ServiceStopEvent(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
