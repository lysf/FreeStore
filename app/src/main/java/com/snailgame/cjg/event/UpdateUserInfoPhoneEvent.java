package com.snailgame.cjg.event;

/**
 * Created by sunxy on 2015/3/13.
 */
public class UpdateUserInfoPhoneEvent  extends BaseEvent{
    private String phone;
    public UpdateUserInfoPhoneEvent(String phone){
        this.phone=phone;
    }

    public String getPhone() {
        return phone;
    }
}
