package com.snailgame.cjg.search;

/**
 * 搜索结果，多页面切换接口
 * Created by pancl on 2015/5/26.
 */
public interface ISearchTabController {
    void search(String searchText);
    void refreshRoute(int[] route);
    void clearResult();
}
