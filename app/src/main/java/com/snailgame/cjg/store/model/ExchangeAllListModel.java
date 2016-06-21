package com.snailgame.cjg.store.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 积分兑换 商品列表 Model
 * Created by xixh on 2016/04/14.
 */
@Getter
@Setter
public class ExchangeAllListModel {
    int code;

    @JSONField(name = "datas")
    private ModelItem item;

    @Getter
    @Setter
    public static class ModelItem {
        @JSONField(name = "goods_list")
        List<ExchangeInfo> goodsList;
    }
}
