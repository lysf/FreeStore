package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * 热搜词
 * Created by xixh on 14-12-30.
 */
public class HotKeyModel extends BaseDataModel {

    protected List<ModelItem> itemList;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }
    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem {
        String nAppId;
        int iNum;
        String sKeyWord;

        public String getnAppId() {
            return nAppId;
        }

        public int getiNum() {
            return iNum;
        }

        public String getsKeyWord() {
            return sKeyWord;
        }

        @JSONField(name="nAppId")
        public void setnAppId(String nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name="iNum")
        public void setiNum(int iNum) {
            this.iNum = iNum;
        }

        @JSONField(name="sKeyWord")
        public void setsKeyWord(String sKeyWord) {
            this.sKeyWord = sKeyWord;
        }
    }

}
