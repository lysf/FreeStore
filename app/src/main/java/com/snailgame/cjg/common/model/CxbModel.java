package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

public class CxbModel {
    protected int msgcode;
    protected String message;


    @JSONField(name = "msgcode")
    public int getMsgcode() {
        return msgcode;
    }

    @JSONField(name = "message")
    public String getMessage() {
        return message;
    }

    @JSONField(name = "msgcode")
    public void setMsgcode(int msgcode) {
        this.msgcode = msgcode;
    }

    @JSONField(name = "message")
    public void setMessage(String message) {
        this.message = message;
    }
}
