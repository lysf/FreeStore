package com.snailgame.cjg.event;

/**
 * Created by sunxy on 2015/3/13.
 */
public class DownloadRemoveEvent extends BaseEvent {
    private long downloadId;
    public DownloadRemoveEvent(long downloadId){
        this.downloadId=downloadId;
    }

    public long getDownloadId() {
        return downloadId;
    }
}
