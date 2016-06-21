package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * 应用授权
 */
public class AuthoModel extends BaseDataModel {
    private Content item;

    public Content getItem() {
        return item;
    }

    @JSONField(name = "item")
    public void setItem(Content item) {
        this.item = item;
    }

    public static final class Content {
        String cIdentity;

        public String getcIdentity() {
            return cIdentity;
        }

        public void setcIdentity(String cIdentity) {
            this.cIdentity = cIdentity;
        }
    }
}

