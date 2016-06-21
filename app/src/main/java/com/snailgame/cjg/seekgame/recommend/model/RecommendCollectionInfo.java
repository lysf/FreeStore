package com.snailgame.cjg.seekgame.recommend.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 推荐tab 中合集类型 扩展字段
 * Created by TAJ_C on 2015/7/28.
 */
public class RecommendCollectionInfo {
    @JSONField(name = "total")
    int total;
    @JSONField(name = "applist")
    List<ModelItem> applist;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ModelItem> getApplist() {
        return applist;
    }

    public void setApplist(List<ModelItem> applist) {
        this.applist = applist;
    }

    public static class ModelItem {
        @JSONField(name = "cIcon")
        String cIcon;
        @JSONField(name = "cAppName")
        String cAppName;

        public String getcIcon() {
            return cIcon;
        }

        public void setcIcon(String cIcon) {
            this.cIcon = cIcon;
        }

        public String getcAppName() {
            return cAppName;
        }

        public void setcAppName(String cAppName) {
            this.cAppName = cAppName;
        }
    }
}
