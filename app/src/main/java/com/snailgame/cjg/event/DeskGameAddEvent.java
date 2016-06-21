package com.snailgame.cjg.event;

import com.snailgame.cjg.desktop.model.InstallGameInfo;

import java.util.ArrayList;

/**
 * Created by sunxy on 2015/3/13.
 */
public class DeskGameAddEvent  extends BaseEvent{
    private ArrayList<InstallGameInfo> selectGameLists;
    public DeskGameAddEvent(ArrayList<InstallGameInfo> selectGameLists){
        this.selectGameLists=selectGameLists;
    }

    public ArrayList<InstallGameInfo> getSelectGameLists() {
        return selectGameLists;
    }
}
