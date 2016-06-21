package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

public class BaseDataModel {
	protected int code;
    protected String msg;

	@JSONField(name = "code")
	public int getCode() {
		return code;
	}

	@JSONField(name = "code")
	public void setCode(int code) {
		this.code = code;
	}

	@JSONField(name = "msg")
	public String getMsg() {
		return msg;
	}

	@JSONField(name = "msg")
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
