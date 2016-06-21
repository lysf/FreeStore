package com.snailgame.cjg.event;

import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.friend.model.Friend;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/5/11.
 */
@Getter
@Setter
public class FriendHandleEvent extends BaseEvent {
    BaseDataModel result;
    Friend friend;
    int handle;


    public FriendHandleEvent(int handle, Friend friend, BaseDataModel result) {
        this.result = result;
        this.friend = friend;
        this.handle = handle;
    }
}
