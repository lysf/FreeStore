package com.snailgame.cjg.friend.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 我的朋友
 * Created by TAJ_C on 2016/5/9.
 */
@Getter
@Setter
public class FriendListModel extends BaseDataModel {

    @JSONField(name = "item")
    private ModelItem item;

    @Getter
    @Setter
    public static class ModelItem {
        @JSONField(name = "apply")
        private List<Friend> applyList;

        @JSONField(name = "friends")
        private List<Friend> friendList;
    }
}
