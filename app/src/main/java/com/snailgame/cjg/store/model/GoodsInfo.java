package com.snailgame.cjg.store.model;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商品详细信息
 * Created by TAJ_C on 2015/11/27.
 */
public class GoodsInfo {
    @JSONField(name = "goods_id")
    private String goodsId;
    @JSONField(name = "goods_type")
    private String goodsType;
    @JSONField(name = "goods_commonid")
    private String goodsCommonId;
    @JSONField(name = "goods_name")
    private String goodsName;
    @JSONField(name = "goods_resume")
    private String goodsResume;
    @JSONField(name = "goods_price")
    private String goodsPrice;
    @JSONField(name = "goods_image")
    private String goodsImage;
    @JSONField(name = "goods_marketprice")
    private String goodsMarketPrice;   // 原价

    @Nullable
    @JSONField(name = "ordinary_goods_price")
    private String ordinaryGoodsPrice;    //普通用户价格

    @JSONField(name = "goods_salenum")
    private String saleNum;
    @JSONField(name = "goods_storage")
    private String goodsStorage;  // 库存

    @Nullable
    @JSONField(name = "big_list_image")
    private String bigListImage;    //  big_list_image
    @JSONField(name = "goods_image_url")
    private String goodsImageUrl; // 图片url （用于小图形式列表）

    @JSONField(name = "goods_url")
    private String goodsUrl;  //详情页url
    @Nullable
    @JSONField(name = "integral")
    private String integral; //积分

    @Nullable
    @JSONField(name = "goods_price_deduct_integral")
    private String goodsIntegralPrice;// 减免积分的 价格

    @Nullable
    @JSONField(name = "goods_vip_discount")
    private String goodsVipDiscount; // 优惠折扣  0.88 表示 88折

    @Nullable
    @JSONField(name = "goods_vip_discount_str")
    private String goodsVipDiscountStr;

    @Nullable
    @JSONField(name = "need_vip_lev")
    private int goodsNeedVipLevel;  //酷玩专购等级 2表示v2

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsCommonId() {
        return goodsCommonId;
    }

    public void setGoodsCommonId(String goodsCommonId) {
        this.goodsCommonId = goodsCommonId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsResume() {
        return goodsResume;
    }

    public void setGoodsResume(String goodsResume) {
        this.goodsResume = goodsResume;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsMarketPrice() {
        return goodsMarketPrice;
    }

    public void setGoodsMarketPrice(String goodsMarketPrice) {
        this.goodsMarketPrice = goodsMarketPrice;
    }

    public String getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(String saleNum) {
        this.saleNum = saleNum;
    }

    public String getGoodsStorage() {
        return goodsStorage;
    }

    public void setGoodsStorage(String goodsStorage) {
        this.goodsStorage = goodsStorage;
    }

    public String getBigListImage() {
        return bigListImage;
    }

    public void setBigListImage(String bigListImage) {
        this.bigListImage = bigListImage;
    }

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getGoodsIntegralPrice() {
        return goodsIntegralPrice;
    }

    public void setGoodsIntegralPrice(String goodsIntegralPrice) {
        this.goodsIntegralPrice = goodsIntegralPrice;
    }

    public String getGoodsVipDiscount() {
        return goodsVipDiscount;
    }

    public void setGoodsVipDiscount(String goodsVipDiscount) {
        this.goodsVipDiscount = goodsVipDiscount;
    }

    public String getGoodsVipDiscountStr() {
        return goodsVipDiscountStr;
    }

    public void setGoodsVipDiscountStr(String goodsVipDiscountStr) {
        this.goodsVipDiscountStr = goodsVipDiscountStr;
    }

    public int getGoodsNeedVipLevel() {
        return goodsNeedVipLevel;
    }

    public void setGoodsNeedVipLevel(int goodsNeedVipLevel) {
        this.goodsNeedVipLevel = goodsNeedVipLevel;
    }

    public String getOrdinaryGoodsPrice() {
        return ordinaryGoodsPrice;
    }

    public void setOrdinaryGoodsPrice(String ordinaryGoodsPrice) {
        this.ordinaryGoodsPrice = ordinaryGoodsPrice;
    }
}
