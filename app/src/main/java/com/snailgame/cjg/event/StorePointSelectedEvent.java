package com.snailgame.cjg.event;

/**
 * Created by TAJ_C on 2015/12/3.
 */
public class StorePointSelectedEvent {
    int selectedPosition;

    public StorePointSelectedEvent(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
