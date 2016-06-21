package com.snailgame.cjg.event;

import com.snailgame.cjg.personal.model.ScratchInfoModel;

/**
 * 我的积分变更
 * Created by lic on 2015/3/9.
 */
public class ScratchInfoChangeEvent extends BaseEvent {

    private ScratchInfoModel scratchInfoModel;

    public ScratchInfoChangeEvent(ScratchInfoModel scratchInfoModel) {
        this.scratchInfoModel = scratchInfoModel;
    }

    public ScratchInfoModel getScratchInfoModel() {
        return scratchInfoModel;
    }
}
