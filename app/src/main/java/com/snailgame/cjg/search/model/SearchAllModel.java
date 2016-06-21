package com.snailgame.cjg.search.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索 -> 全部Model
 * Created by pancl on 2015/6/2.
 */
public class SearchAllModel extends BaseDataModel{
    public List<ModelItem> searchInfos = new ArrayList<>();

    @JSONField(name = "list")
    public List<ModelItem> getSearchInfos() {
        return searchInfos;
    }

    @JSONField(name = "list")
    public void setSearchInfos(List<ModelItem> searchInfos) {
        this.searchInfos = searchInfos;
    }

    public static final class ModelItem {
        private String list;
        private int iTotalRowCount;
        private String cClassify;
        private int iPageRowCount;
        private int iRequestPageNum;
        private int iTotalPageCount;

        public String getList() {
            return list;
        }

        @JSONField(name="list")
        public void setList(String list) {
            this.list = list;
        }

        public String getcClassify() {
            return cClassify;
        }

        @JSONField(name="cClassify")
        public void setcClassify(String cClassify) {
            this.cClassify = cClassify;
        }

        public int getiPageRowCount() {
            return iPageRowCount;
        }

        @JSONField(name="iPageRowCount")
        public void setiPageRowCount(int iPageRowCount) {
            this.iPageRowCount = iPageRowCount;
        }

        public int getiRequestPageNum() {
            return iRequestPageNum;
        }

        @JSONField(name="iRequestPageNum")
        public void setiRequestPageNum(int iRequestPageNum) {
            this.iRequestPageNum = iRequestPageNum;
        }

        public int getiTotalPageCount() {
            return iTotalPageCount;
        }

        @JSONField(name="iTotalPageCount")
        public void setiTotalPageCount(int iTotalPageCount) {
            this.iTotalPageCount = iTotalPageCount;
        }

        public int getiTotalRowCount() {
            return iTotalRowCount;
        }

        @JSONField(name="iTotalRowCount")
        public void setiTotalRowCount(int iTotalRowCount) {
            this.iTotalRowCount = iTotalRowCount;
        }
    }
}
