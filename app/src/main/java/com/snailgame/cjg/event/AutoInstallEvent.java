package com.snailgame.cjg.event;

/**
 * 设置 - 下载完自动安装通知
 * Created by pancl on 2015/3/13.
 */
public class AutoInstallEvent extends BaseEvent {
    private boolean success;

    public AutoInstallEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}