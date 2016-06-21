package com.snailgame.cjg.event;

/**
 * 皮肤下载成功事件
 * Created by xixh on 2015/3/10.
 */
public class SkinDownloadedEvent extends BaseEvent {
    private String localPath;
    private String appPkgName;

    public SkinDownloadedEvent(String localPath, String appPkgName) {
        this.localPath = localPath;
        this.appPkgName = appPkgName;
    }

    public String getAppPkgName() {
        return appPkgName;
    }

    public String getLocalPath() {
        return localPath;
    }
}
