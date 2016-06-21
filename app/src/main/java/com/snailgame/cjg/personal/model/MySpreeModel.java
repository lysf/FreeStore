package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * Created by sunxy on 14-4-8.
 */
public class MySpreeModel extends BaseDataModel {

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

    public static final class ModelItem {
        private long nAppId; //应用id
        private String sArticleName; //物品名称
        private String cLogo;//logo
        private String sAppName;
        private String sIntro;
        private String cShelvesStatus;//物品上下架状态
        private String nUserId;//用户id
        private String nSqNo;
        private String cStatus;
        private String dUpdate;
        private String dCreate;
        private String cType;//物品类型
        private int iArticleId;//物品id
        private String cClassify;//物品类别
        private String cSource;//物品来源
        private String cCdkey;//cdkey

        private boolean showDetail = false;

        private String content;
        private String useMethod;
        private String deadline;

        @JSONField(name = "nAppId")
        public long getnAppId() {
            return nAppId;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(long nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name = "sArticleName")
        public String getsArticleName() {
            return sArticleName;
        }

        @JSONField(name = "sArticleName")
        public void setsArticleName(String sArticleName) {
            this.sArticleName = sArticleName;
        }

        @JSONField(name = "cLogo")
        public String getcLogo() {
            return cLogo;
        }

        @JSONField(name = "cLogo")
        public void setcLogo(String cLogo) {
            this.cLogo = cLogo;
        }

        @JSONField(name = "sAppName")
        public String getsAppName() {
            return sAppName;
        }

        @JSONField(name = "sAppName")
        public void setsAppName(String sAppName) {
            this.sAppName = sAppName;
        }

        @JSONField(name = "sIntro")
        public String getsIntro() {
            return sIntro;
        }

        @JSONField(name = "sIntro")
        public void setsIntro(String sIntro) {
            this.sIntro = sIntro;
        }

        @JSONField(name = "cShelvesStatus")
        public String getcShelvesStatus() {
            return cShelvesStatus;
        }

        @JSONField(name = "cShelvesStatus")
        public void setcShelvesStatus(String cShelvesStatus) {
            this.cShelvesStatus = cShelvesStatus;
        }

        @JSONField(name = "nUserId")
        public String getnUserId() {
            return nUserId;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(String nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "nSqNo")
        public String getnSqNo() {
            return nSqNo;
        }

        @JSONField(name = "nSqNo")
        public void setnSqNo(String nSqNo) {
            this.nSqNo = nSqNo;
        }

        @JSONField(name = "cStatus")
        public String getcStatus() {
            return cStatus;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
        }

        @JSONField(name = "dUpdate")
        public String getdUpdate() {
            return dUpdate;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
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

        @JSONField(name = "iArticleId")
        public int getiArticleId() {
            return iArticleId;
        }

        @JSONField(name = "iArticleId")
        public void setiArticleId(int iArticleId) {
            this.iArticleId = iArticleId;
        }

        @JSONField(name = "cClassify")
        public String getcClassify() {
            return cClassify;
        }

        @JSONField(name = "cClassify")
        public void setcClassify(String cClassify) {
            this.cClassify = cClassify;
        }

        @JSONField(name = "cSource")
        public String getcSource() {
            return cSource;
        }

        @JSONField(name = "cSource")
        public void setcSource(String cSource) {
            this.cSource = cSource;
        }

        @JSONField(name = "cCdkey")
        public String getcCdkey() {
            return cCdkey;
        }

        @JSONField(name = "cCdkey")
        public void setcCdkey(String cCdkey) {
            this.cCdkey = cCdkey;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUseMethod() {
            return useMethod;
        }

        public void setUseMethod(String useMethod) {
            this.useMethod = useMethod;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }


        public boolean isShowDetail() {
            return showDetail;
        }

        public void setShowDetail(boolean showDetail) {
            this.showDetail = showDetail;
        }
    }

}
