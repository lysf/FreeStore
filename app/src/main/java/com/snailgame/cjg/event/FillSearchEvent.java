package com.snailgame.cjg.event;

/**
 * 搜索 点击搜索结果列表 填满搜索项
 * Created by TAJ_C on 2015/3/13.
 */
public class FillSearchEvent extends BaseEvent {
    String keyword;

    public FillSearchEvent(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
