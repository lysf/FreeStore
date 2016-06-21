package com.snailgame.cjg.event;

/**
 * Created by TAJ_C on 2015/8/31.
 */
public class TabClickedEvent {
    int tabPosition;
    int pagePosition;

    public TabClickedEvent(int tabPosition, int pagePosition) {
        this.tabPosition = tabPosition;
        this.pagePosition = pagePosition;
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public int getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }
}
