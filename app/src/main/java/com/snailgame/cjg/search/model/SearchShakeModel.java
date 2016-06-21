package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.List;

public class SearchShakeModel extends BaseDataModel{
    public List<SearchInfo> searchShakeList = new ArrayList<SearchInfo>();

    @JSONField(name = "list")
    public List<SearchInfo> getSearchShakeList() {
		return searchShakeList;
	}
    @JSONField(name = "list")
	public void setSearchShakeList(List<SearchInfo> searchShakeList) {
		this.searchShakeList = searchShakeList;
	}

}
