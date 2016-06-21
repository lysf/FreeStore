package com.snailgame.cjg.event;

import com.snailgame.cjg.common.db.dao.NewsChannel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/19.
 */
@Getter
@Setter
public class NewsChannelGetEvent {
    List<NewsChannel> newsChannelList;

    public NewsChannelGetEvent(List<NewsChannel> newsChannelList) {
        this.newsChannelList = newsChannelList;
    }
}
