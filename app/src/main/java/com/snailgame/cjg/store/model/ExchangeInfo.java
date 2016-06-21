package com.snailgame.cjg.store.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 积分兑换物品信息
 * Created by xixh on 2016/04/14.
 */
@Getter
@Setter
public class ExchangeInfo {
    private int iId;
    private String sName;
    private String cIcon;
    private String cPicUrl;
    private int iIntegral;
    private String cAward;
    private String sDesc;
    private String sUrl;
    private String cOriginalPrice;
}
