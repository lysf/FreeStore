package com.snailgame.cjg.util.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 跳转信息 实体类
 * Created by xixh on 2016/4/14.
 */

@Getter
@Setter
public class JumpInfo {
    public static final String TASK_AUTO_INSTALL = "1";
    private int type;
    private String pageId;
    private String pageTitle;
    private String url;
    private String channel;
    private String task;
    private String md5;
}
