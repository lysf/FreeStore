package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用推荐
 * Created by xixh on 14-12-25.
 */
public class AppRecommendModel extends BaseDataModel{
    public List<AppRecommend> infos = new ArrayList<AppRecommend>();
    public PageInfo pageInfo;
    public String val;


    @JSONField(name = "list")
    public List<AppRecommend> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(List<AppRecommend> infos) {
        this.infos = infos;
    }

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getVal() {
        return val;
    }

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }
}
