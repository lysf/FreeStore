package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-24
 * Time : 上午11:05
 * Description :
 */
public class SearchInfoModel extends BaseDataModel{
    public List<SearchInfo> searchInfos = new ArrayList<SearchInfo>();
    public PageInfo pageInfo;

    @JSONField(name = "list")
    public List<SearchInfo> getSearchInfos() {
        return searchInfos;
    }

    @JSONField(name = "list")
    public void setSearchInfos(List<SearchInfo> searchInfos) {
        this.searchInfos = searchInfos;
    }

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
