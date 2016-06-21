package com.snailgame.cjg.friend.model;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/5/9.
 */
@Getter
@Setter
public class Friend implements Serializable{
    @JSONField(name = "nUserId")
    private long userId; //用户id

    @JSONField(name = "sNickName")
    private String nickName; //昵称

    @Nullable
    @JSONField(name = "sGameName")
    private String gameName; //游戏名   ps:如果该用户并没有玩过游戏，则为""p

    @JSONField(name = "cPhoto")
    private String photo; // 头像

    @Nullable
    @JSONField(name = "cPhone")
    private String phone; // 绑定的手机号

}
