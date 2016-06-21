package com.snailgame.cjg.event;

import com.snailgame.cjg.common.model.UpdateModel;

/**
 * Created by TAJ_C on 2015/7/20.
 */
public class UpdateMySelfEvent extends BaseEvent{
    private UpdateModel.ModelItem updateItem;
    private String localUrl;

    public UpdateMySelfEvent(UpdateModel.ModelItem updateItem, String localUrl) {
        this.updateItem = updateItem;
        this.localUrl = localUrl;
    }

    public UpdateModel.ModelItem getUpdateItem() {
        return updateItem;
    }

    public void setUpdateItem(UpdateModel.ModelItem updateItem) {
        this.updateItem = updateItem;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }
}
