package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * 评论列表
 * Created by taj on 2014/11/12.
 */
public class CommentListModel extends BaseDataModel {
    protected List<ModelItem> itemList;
    protected PageInfo pageInfo;
    protected String val;

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

    @JSONField(name = "val")
    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static final class ModelItem {

        List<UserPlatformPrivilege> privilege;
        int iScore;
        String dCreate;
        String sContent;
        String nUserId;
        int iPlatformId;
        String cStatus; // 状态: 0,已删除; 1,未审核; 2,已审核, 3=已回复
        String dUpdate;
        String nAppId;
        String userIcon;
        String userNickName;
        String reply;  //回复
        String replyTime;
        String sHeadFrame;                // 头像边框
        String sSpecificMedal;                // 个性勋章
        int iLevel;// 等级
        int iMemberLevel;// 会员等级

        public List<UserPlatformPrivilege> getPrivilege() {
            return privilege;
        }

        public void setPrivilege(List<UserPlatformPrivilege> privilege) {
            this.privilege = privilege;
        }

        public int getiLevel() {
            return iLevel;
        }

        public void setiLevel(int iLevel) {
            this.iLevel = iLevel;
        }

        public int getiMemberLevel() {
            return iMemberLevel;
        }

        public void setiMemberLevel(int iMemberLevel) {
            this.iMemberLevel = iMemberLevel;
        }

        public int getiScore() {
            return iScore;
        }

        public String getdCreate() {
            return dCreate;
        }

        public String getsContent() {
            return sContent;
        }

        public String getnUserId() {
            return nUserId;
        }

        public int getiPlatformId() {
            return iPlatformId;
        }

        public String getcStatus() {
            return cStatus;
        }

        public String getdUpdate() {
            return dUpdate;
        }

        public String getnAppId() {
            return nAppId;
        }

        public String getUserIcon() {
            return userIcon;
        }

        public String getUserNickName() {
            return userNickName;
        }

        public String getsHeadFrame() {
            return sHeadFrame;
        }

        public String getsSpecificMedal() {
            return sSpecificMedal;
        }

        @JSONField(name = "iScore")
        public void setiScore(int iScore) {
            this.iScore = iScore;
        }

        @JSONField(name = "dCreate")
        public void setdCreate(String dCreate) {
            this.dCreate = dCreate;
        }

        @JSONField(name = "sContent")
        public void setsContent(String sContent) {
            this.sContent = sContent;
        }

        @JSONField(name = "nUserId")
        public void setnUserId(String nUserId) {
            this.nUserId = nUserId;
        }

        @JSONField(name = "iPlatformId")
        public void setiPlatformId(int iPlatformId) {
            this.iPlatformId = iPlatformId;
        }

        @JSONField(name = "cStatus")
        public void setcStatus(String cStatus) {
            this.cStatus = cStatus;
        }

        @JSONField(name = "dUpdate")
        public void setdUpdate(String dUpdate) {
            this.dUpdate = dUpdate;
        }

        @JSONField(name = "nAppId")
        public void setnAppId(String nAppId) {
            this.nAppId = nAppId;
        }

        @JSONField(name = "userIcon")
        public void setUserIcon(String userIcon) {
            this.userIcon = userIcon;
        }

        @JSONField(name = "userNickName")
        public void setUserNickName(String userNickName) {
            this.userNickName = userNickName;
        }

        @JSONField(name = "sReply")
        public String getReply() {
            return reply;
        }

        @JSONField(name = "sReply")
        public void setReply(String reply) {
            this.reply = reply;
        }

        @JSONField(name = "dReplyTime")
        public String getReplyTime() {
            return replyTime;
        }

        @JSONField(name = "dReplyTime")
        public void setReplyTime(String replyTime) {
            this.replyTime = replyTime;
        }

        @JSONField(name = "sHeadFrame")
        public void setsHeadFrame(String sHeadFrame) {
            this.sHeadFrame = sHeadFrame;
        }

        @JSONField(name = "sSpecificMedal")
        public void setsSpecificMedal(String sSpecificMedal) {
            this.sSpecificMedal = sSpecificMedal;
        }
    }

    public static final class UserPlatformPrivilege {
        private int IPrivilegeId; // 特权ID
        private String SPrivilegeName; // 特权名称
        private String CLightIcon; // 已点亮图标

        public int getIPrivilegeId() {
            return IPrivilegeId;
        }

        public void setIPrivilegeId(int IPrivilegeId) {
            this.IPrivilegeId = IPrivilegeId;
        }

        public String getSPrivilegeName() {
            return SPrivilegeName;
        }

        public void setSPrivilegeName(String SPrivilegeName) {
            this.SPrivilegeName = SPrivilegeName;
        }

        public String getCLightIcon() {
            return CLightIcon;
        }

        public void setCLightIcon(String CLightIcon) {
            this.CLightIcon = CLightIcon;
        }
    }
}
