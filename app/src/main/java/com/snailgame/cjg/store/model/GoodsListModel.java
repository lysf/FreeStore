package com.snailgame.cjg.store.model;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 商品列表 Model
 * Created by TAJ_C on 2015/11/27.
 */
public class GoodsListModel {
    @JSONField(name = "code")
    int code;
    @Nullable
    @JSONField(name = "hasmore")
    boolean hasMore;
    @Nullable
    @JSONField(name = "page_total")
    int pageTotal;

    @JSONField(name = "datas")
    private ModelItem item;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public ModelItem getItem() {
        return item;
    }

    public void setItem(ModelItem item) {
        this.item = item;
    }

    public static class ModelItem {
        @JSONField(name = "goods_list")
        List<GoodsInfo> goodsList;

        public List<GoodsInfo> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<GoodsInfo> goodsList) {
            this.goodsList = goodsList;
        }
    }
}
