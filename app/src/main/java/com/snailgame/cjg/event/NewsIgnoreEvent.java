package com.snailgame.cjg.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/15.
 */
@Getter
@Setter
public class NewsIgnoreEvent {
    private String articleId;

    public NewsIgnoreEvent(String articleId) {
        this.articleId = articleId;
    }
}
