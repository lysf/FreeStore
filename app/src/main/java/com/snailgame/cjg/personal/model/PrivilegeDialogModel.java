package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 特权弹出框实体
 * Created by pancl on 2015/4/29.
 */
public class PrivilegeDialogModel {
    protected String grayTitle;
    protected String lightTitle;
    protected String buttonText;
    protected List<ModelItem> itemList;

    @JSONField(name = "grayTitle")
    public String getGrayTitle() {
        return grayTitle;
    }

    @JSONField(name = "grayTitle")
    public void setGrayTitle(String grayTitle) {
        this.grayTitle = grayTitle;
    }

    @JSONField(name = "lightTitle")
    public String getLightTitle() {
        return lightTitle;
    }

    @JSONField(name = "lightTitle")
    public void setLightTitle(String lightTitle) {
        this.lightTitle = lightTitle;
    }

    @JSONField(name = "buttonText")
    public String getButtonText() {
        return buttonText;
    }

    @JSONField(name = "buttonText")
    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    @JSONField(name = "awards")
    public List<ModelItem> getItemList() {
        return itemList;
    }

    @JSONField(name = "awards")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem {
        private String icon;
        private String title;
        private String desc;

        @JSONField(name = "desc")
        public String getDesc() {
            return desc;
        }

        @JSONField(name = "desc")
        public void setDesc(String desc) {
            this.desc = desc;
        }

        @JSONField(name = "icon")
        public String getIcon() {
            return icon;
        }

        @JSONField(name = "icon")
        public void setIcon(String icon) {
            this.icon = icon;
        }

        @JSONField(name = "title")
        public String getTitle() {
            return title;
        }

        @JSONField(name = "title")
        public void setTitle(String title) {
            this.title = title;
        }
    }
}
