package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * Created by andy on 13-6-20.
 */
public class SearchKeyModel extends BaseDataModel {

    protected List<ModelItem> itemList;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }
    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem {
        private String sAppName; //搜索热词或者历史搜索

        public String getsAppName() {
            return sAppName;
        }

        @JSONField(name="sAppName")
        public void setsAppName(String sAppName) {
            this.sAppName = sAppName;
        }
    }

}
