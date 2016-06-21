package com.snailgame.cjg.event;

import com.snailgame.cjg.common.model.FreeGameItem;

import java.util.List;

/**
 * Created by sunxy on 2015/4/14.
 */
public class FreeGameItemEvent extends BaseEvent {
    List<FreeGameItem> freeGameItems;

    public List<FreeGameItem> getFreeGameItems() {
        return freeGameItems;
    }

    public void setFreeGameItems(List<FreeGameItem> freeGameItems) {
        this.freeGameItems = freeGameItems;
    }
}
