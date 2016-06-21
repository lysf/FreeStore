package com.snailgame.cjg.event;

/**
 * 取消下载任务
 * Created by xixh on 2016/2/24.
 */
public class DownloadTaskRemoveEvent extends BaseEvent {
    private String pkgName;

    public DownloadTaskRemoveEvent(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgName() {
        return pkgName;
    }
}
