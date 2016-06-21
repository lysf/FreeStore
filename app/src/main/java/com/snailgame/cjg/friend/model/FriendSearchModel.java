package com.snailgame.cjg.friend.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 朋友搜索列表
 * Created by TAJ_C on 2016/5/17.
 */
@Getter
@Setter
public class FriendSearchModel extends BaseDataModel {
    @JSONField(name = "list")
    private List<ContactModel> recommendList;
}
