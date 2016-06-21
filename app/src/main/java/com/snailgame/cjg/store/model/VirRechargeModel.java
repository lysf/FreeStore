package com.snailgame.cjg.store.model;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 虚拟充值 热门游戏
 * Created by TAJ_C on 2015/11/27.
 */
public class VirRechargeModel {

    @JSONField(name = "code")
    private int code;

    @Nullable
    @JSONField(name = "page_total")
    private  int pageTotal;
    @JSONField(name = "datas")
    private ModelItem item;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

        @JSONField(name = "class_list")
        List<GameGoodsInfo> gameGoodsList;

        public List<GameGoodsInfo> getGameGoodsList() {
            return gameGoodsList;
        }

        public void setGameGoodsList(List<GameGoodsInfo> gameGoodsList) {
            this.gameGoodsList = gameGoodsList;
        }

        public static class GameGoodsInfo {
            @JSONField(name = "goods_app_icon")
            private String iconUrl;  //图片网址（正方形图片）
            @JSONField(name = "goods_image")
            private String imageUrl;  //图片网址（长方形图片）
            @JSONField(name = "goods_app_name")
            private String goodsName;
            @JSONField(name = "goods_discount")
            private String discount;  // 最低折扣
            @JSONField(name = "goods_url")
            private String goodsUrl; // 详情网址


            public String getIconUrl() {
                return iconUrl;
            }

            public void setIconUrl(String iconUrl) {
                this.iconUrl = iconUrl;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getGoodsName() {
                return goodsName;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public String getGoodsUrl() {
                return goodsUrl;
            }

            public void setGoodsUrl(String goodsUrl) {
                this.goodsUrl = goodsUrl;
            }
        }

    }

}
