package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

public class NoviceCardModel extends BaseDataModel {
	
	protected List<ModelItem> itemList;

    @JSONField(name = "list")
    public List<ModelItem> getItemList() {
        return itemList;
    }
    @JSONField(name = "list")
    public void setItemList(List<ModelItem> itemList) {
        this.itemList = itemList;
    }

    public static final class ModelItem{
		private String name;
		private String quantity;
		private boolean status;
	
		@JSONField(name = "name")
		public String getName() {
			return name;
		}
		@JSONField(name = "name")
		public void setName(String name) {
			this.name = name;
		}
		@JSONField(name = "count")
		public String getQuantity() {
			return quantity;
		}
		@JSONField(name = "count")
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		@JSONField(name = "status")
		public boolean isStatus() {
			return status;
		}
		@JSONField(name = "status")
		public void setStatus(boolean status) {
			this.status = status;
		}
    }
}
