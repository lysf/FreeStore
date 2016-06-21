package com.snailgame.cjg.event;

/**
 * Created by sunxy on 2015/3/26.
 */
public class RankFilterEvent  extends BaseEvent{
    private String type, country;
    private boolean dismiss = false;

    public void setDismiss(boolean dismiss) {
        this.dismiss = dismiss;
    }

    public RankFilterEvent(String type, String country) {
        this.type = type;
        this.country = country;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public boolean isDismiss() {
        return dismiss;
    }
}
