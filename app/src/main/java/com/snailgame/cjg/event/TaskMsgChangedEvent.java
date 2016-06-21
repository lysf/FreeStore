package com.snailgame.cjg.event;

/**
 * Created by TAJ_C on 2015/11/5.
 */
public class TaskMsgChangedEvent {
    int type;
    int num;
    boolean isEntry;

    public TaskMsgChangedEvent(int type, int num, boolean isEntry) {
        this.type = type;
        this.num = num;
        this.isEntry = isEntry;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isEntry() {
        return isEntry;
    }

    public void setIsEntry(boolean isEntry) {
        this.isEntry = isEntry;
    }
}
