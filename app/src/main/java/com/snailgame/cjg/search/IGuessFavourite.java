package com.snailgame.cjg.search;

import com.snailgame.cjg.search.model.SearchInfo;

import java.util.List;

/**
 * 搜索无结果时，猜你喜欢接口
 * Created by pancl on 2015/6/4.
 */
public interface IGuessFavourite {
    List<SearchInfo> getFavouriteList();
}
