package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * 搜索 -> 酷玩Model
 * Created by pancl on 2015/6/2.
 */
public class SearchKuwanModel extends BaseDataModel{
    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }
    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }
    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }
    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static final class ModelItem {
        private int iGoodsId;  //商品id
        private String sGoodsName;//商品名称
        private double iGoodsPrice;//商品售价
        private double iMarketPrice;//商品市场价
        private String sGoodsImageUrl;//商品图片
        private String sGoodsPurchaseUrl;//商品购买链接
        private int iGoodsStorage;//商品可卖库存
        private String cGoodsStatus;//物品状态  1：上架0：下架 10：违规（只有1是可卖状态)

        public String getcGoodsStatus() {
            return cGoodsStatus;
        }

        @JSONField(name="cGoodsStatus")
        public void setcGoodsStatus(String cGoodsStatus) {
            this.cGoodsStatus = cGoodsStatus;
        }

        public int getiGoodsId() {
            return iGoodsId;
        }

        @JSONField(name="iGoodsId")
        public void setiGoodsId(int iGoodsId) {
            this.iGoodsId = iGoodsId;
        }

        public double getiGoodsPrice() {
            return iGoodsPrice;
        }

        @JSONField(name="iGoodsPrice")
        public void setiGoodsPrice(double iGoodsPrice) {
            this.iGoodsPrice = iGoodsPrice;
        }

        public int getiGoodsStorage() {
            return iGoodsStorage;
        }

        @JSONField(name="iGoodsStorage")
        public void setiGoodsStorage(int iGoodsStorage) {
            this.iGoodsStorage = iGoodsStorage;
        }

        public double getiMarketPrice() {
            return iMarketPrice;
        }

        @JSONField(name="iMarketPrice")
        public void setiMarketPrice(double iMarketPrice) {
            this.iMarketPrice = iMarketPrice;
        }

        public String getsGoodsImageUrl() {
            return sGoodsImageUrl;
        }

        @JSONField(name="sGoodsImageUrl")
        public void setsGoodsImageUrl(String sGoodsImageUrl) {
            this.sGoodsImageUrl = sGoodsImageUrl;
        }

        public String getsGoodsName() {
            return sGoodsName;
        }

        @JSONField(name="sGoodsName")
        public void setsGoodsName(String sGoodsName) {
            this.sGoodsName = sGoodsName;
        }

        public String getsGoodsPurchaseUrl() {
            return sGoodsPurchaseUrl;
        }

        @JSONField(name="sGoodsPurchaseUrl")
        public void setsGoodsPurchaseUrl(String sGoodsPurchaseUrl) {
            this.sGoodsPurchaseUrl = sGoodsPurchaseUrl;
        }
    }
}
