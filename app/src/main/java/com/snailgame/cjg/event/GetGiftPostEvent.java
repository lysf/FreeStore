package com.snailgame.cjg.event;

import com.snailgame.cjg.spree.model.SpreeGiftInfo;

/**
 * 获取礼包请求
 * Created by TAJ_C on 2015/3/12.
 */
public class GetGiftPostEvent extends BaseEvent{
    private SpreeGiftInfo spreeInfo;


    public GetGiftPostEvent( SpreeGiftInfo spreeInfo) {
        this.spreeInfo = spreeInfo;
    }


    public SpreeGiftInfo getSpreeInfo() {
        return spreeInfo;
    }
}
