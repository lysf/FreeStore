package com.snailgame.cjg.message.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息推送 实体类
 * Created by chenping1 on 2014/6/9.
 */
@Getter
@Setter
public class MessagePushExInfo {
    private int type;
    private String pageId;
    private String pageTitle;
    private String url;
    private String imgUrl;
    private String bigImgUrl;

    private String sGameName;
    private long nUserId;
    private String cPhoto;
    private String sNickName;
}
