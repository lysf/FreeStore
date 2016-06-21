package com.snailgame.cjg.friend.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/5/12.
 */
@Getter
@Setter
public class FriendGamesModel extends BaseDataModel{

    @JSONField(name = "item")
    private ModelItem item;
    @Getter
    @Setter
    public static class ModelItem{
        @JSONField(name = "gameList")
        private List<BaseAppInfo> gameList;

        @JSONField(name = "nUserId")
        private Long userId; //用户id

        @JSONField(name = "sNickName")
        private String nickName; //昵称

        @JSONField(name = "cPhoto")
        private String photo; // 头像
    }

}
