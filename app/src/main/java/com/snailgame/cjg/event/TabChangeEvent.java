package com.snailgame.cjg.event;

/**
 * 搜索 页间切换事件
 * Created by pancl on 2015/6/2.
 */
public class TabChangeEvent extends BaseEvent {
    int tabPosition;

    public TabChangeEvent(int position) {
        this.tabPosition = position;
    }

    public int getTabPosition() {
        return tabPosition;
    }
}
