package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * Created by sunxy on 2015/2/2.
 */
public class UserTaskModel extends BaseDataModel {

    public String val;
    @JSONField(name = "item")
    private ModelItem item;

    public static class ModelItem {

        @JSONField(name = "1")
        private List<TaskModel> oneTaskList;

        @JSONField(name = "3")
        private List<TaskModel> weekTaskList;

        public List<TaskModel> getOneTaskList() {
            return oneTaskList;
        }

        public void setOneTaskList(List<TaskModel> oneTaskList) {
            this.oneTaskList = oneTaskList;
        }

        public List<TaskModel> getWeekTaskList() {
            return weekTaskList;
        }

        public void setWeekTaskList(List<TaskModel> weekTaskList) {
            this.weekTaskList = weekTaskList;
        }
    }


    public String getVal() {
        return val;
    }

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }

    public ModelItem getItem() {
        return item;
    }

    public void setItem(ModelItem item) {
        this.item = item;
    }
}
