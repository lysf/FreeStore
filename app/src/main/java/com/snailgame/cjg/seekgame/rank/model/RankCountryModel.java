package com.snailgame.cjg.seekgame.rank.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;

/**
 * Created by sunxy on 2015/3/25.
 */
public class RankCountryModel extends BaseDataModel {
    private ArrayList<ModelItem> infos = new ArrayList<ModelItem>();

    @JSONField(name = "list")
    public ArrayList<ModelItem> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(ArrayList<ModelItem> infos) {
        this.infos = infos;
    }
    public static final class ModelItem {
        String country; //

        public String getCountry() {
            return country;
        }

        @JSONField(name = "cCountry")
        public void setCountry(String country) {
            this.country = country;
        }
    }
}
