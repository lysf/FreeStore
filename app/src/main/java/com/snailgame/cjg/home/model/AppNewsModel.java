package com.snailgame.cjg.home.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * 资讯模型
 * @author pancl
 *
 */
public class AppNewsModel extends BaseDataModel {
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
    
    public static class ModelItem {
		private int iId;//ID
        private String dCreate;//生成时间
        private String cType;//cms类型 0：资讯 1：攻略
        private int iOrder;//排序
        private int nAppId;//应用id
        private String sAppName;//
        private String dUpdate;//更新时间
        private String cDelFlag;//删除标识: 0,已删除;1,未删除
        private String sTitle;//大标题
        private String sEditorName;//编辑名
        private String sInnerHtml;//正文文本
        private String sImageUrl;//图标
        private String sColumn;//栏目
        private String sSubTitle;//简介
        private String cOnlineFlag;//0：下线 1：上线
        private String sContentTypes;//内容类型
        private String sHtmlUrl;//静态页面地址
        int iPlatformId;//平台
        String cOs;//系统: 1,安卓; 2,IOS；3,WP
        String sDateDisplay;//
        
        private int topMargin;

        private int commentNum; //评论数量
        @JSONField(name = "iId")
		public int getiId() {
			return iId;
		}
        @JSONField(name = "iId")
		public void setiId(int iId) {
			this.iId = iId;
		}
        @JSONField(name = "dCreate")
		public String getdCreate() {
			return dCreate;
		}
        @JSONField(name = "dCreate")
		public void setdCreate(String dCreate) {
			this.dCreate = dCreate;
		}
        @JSONField(name = "cType")
		public String getcType() {
			return cType;
		}
        @JSONField(name = "cType")
		public void setcType(String cType) {
			this.cType = cType;
		}
        @JSONField(name = "iOrder")
		public int getiOrder() {
			return iOrder;
		}
        @JSONField(name = "iOrder")
		public void setiOrder(int iOrder) {
			this.iOrder = iOrder;
		}
        @JSONField(name = "nAppId")
		public int getnAppId() {
			return nAppId;
		}
        @JSONField(name = "nAppId")
		public void setnAppId(int nAppId) {
			this.nAppId = nAppId;
		}
        @JSONField(name = "sAppName")
		public String getsAppName() {
			return sAppName;
		}
        @JSONField(name = "sAppName")
		public void setsAppName(String sAppName) {
			this.sAppName = sAppName;
		}
        @JSONField(name = "dUpdate")
		public String getdUpdate() {
			return dUpdate;
		}
        @JSONField(name = "dUpdate")
		public void setdUpdate(String dUpdate) {
			this.dUpdate = dUpdate;
		}
        @JSONField(name = "cDelFlag")
		public String getcDelFlag() {
			return cDelFlag;
		}
        @JSONField(name = "cDelFlag")
		public void setcDelFlag(String cDelFlag) {
			this.cDelFlag = cDelFlag;
		}
        @JSONField(name = "sTitle")
		public String getsTitle() {
			return sTitle;
		}
        @JSONField(name = "sTitle")
		public void setsTitle(String sTitle) {
			this.sTitle = sTitle;
		}
        @JSONField(name = "sEditorName")
		public String getsEditorName() {
			return sEditorName;
		}
        @JSONField(name = "sEditorName")
		public void setsEditorName(String sEditorName) {
			this.sEditorName = sEditorName;
		}
        @JSONField(name = "sInnerHtml")
		public String getsInnerHtml() {
			return sInnerHtml;
		}
        @JSONField(name = "sInnerHtml")
		public void setsInnerHtml(String sInnerHtml) {
			this.sInnerHtml = sInnerHtml;
		}
        @JSONField(name = "sImageUrl")
		public String getsImageUrl() {
			return sImageUrl;
		}
        @JSONField(name = "sImageUrl")
		public void setsImageUrl(String sImageUrl) {
			this.sImageUrl = sImageUrl;
		}
        @JSONField(name = "sColumn")
		public String getsColumn() {
			return sColumn;
		}
        @JSONField(name = "sColumn")
		public void setsColumn(String sColumn) {
			this.sColumn = sColumn;
		}
        @JSONField(name = "sSubTitle")
		public String getsSubTitle() {
			return sSubTitle;
		}
        @JSONField(name = "sSubTitle")
		public void setsSubTitle(String sSubTitle) {
			this.sSubTitle = sSubTitle;
		}
        @JSONField(name = "cOnlineFlag")
		public String getcOnlineFlag() {
			return cOnlineFlag;
		}
        @JSONField(name = "cOnlineFlag")
		public void setcOnlineFlag(String cOnlineFlag) {
			this.cOnlineFlag = cOnlineFlag;
		}
        @JSONField(name = "sContentTypes")
		public String getsContentTypes() {
			return sContentTypes;
		}
        @JSONField(name = "sContentTypes")
		public void setsContentTypes(String sContentTypes) {
			this.sContentTypes = sContentTypes;
		}
        @JSONField(name = "sHtmlUrl")
		public String getsHtmlUrl() {
			return sHtmlUrl;
		}
        @JSONField(name = "sHtmlUrl")
		public void setsHtmlUrl(String sHtmlUrl) {
			this.sHtmlUrl = sHtmlUrl;
		}
        
		public int getTopMargin() {
			return topMargin;
		}
		public void setTopMargin(int topMargin) {
			this.topMargin = topMargin;
		}


        public int getiPlatformId() {
            return iPlatformId;
        }

        public String getcOs() {
            return cOs;
        }

        public String getsDateDisplay() {
            return sDateDisplay;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "cOs")
        public void setcOs(String cOs) {
            this.cOs = cOs;
        }

        @JSONField(name = "sDateDisplay")
        public void setsDateDisplay(String sDateDisplay) {
            this.sDateDisplay = sDateDisplay;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }
    }



}
