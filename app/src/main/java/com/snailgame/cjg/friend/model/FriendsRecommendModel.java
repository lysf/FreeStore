package com.snailgame.cjg.friend.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 朋友推荐列表
 * Created by TAJ_C on 2016/5/9.
 */
@Getter
@Setter
public class FriendsRecommendModel extends BaseDataModel{
    @JSONField(name = "list")
    private List<Friend> recommendList;
}
