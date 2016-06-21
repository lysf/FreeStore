package com.snailgame.cjg.friend.model;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/5/16.
 */
@Getter
@Setter
public class ContactModel extends Friend {
    @JSONField(name = "cAccount")
    private String accountName;

    private String contactName;//手机名片夹名字  在添加 手机联系人账号使用


    private String firstPinYin;
    private String pinYin;


    private String isFriend;//是否为好友关系：  0:好友关系;1：申请好友关系; 2:非好友关系
}
