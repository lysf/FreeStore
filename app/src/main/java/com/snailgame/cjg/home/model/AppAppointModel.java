package com.snailgame.cjg.home.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.List;

/**
 * 首页 测新游
 * Created by TAJ_C on 2016/1/28.
 */
public class AppAppointModel extends BaseDataModel {

    @JSONField(name = "list")
    private List<ModelItem> appList;

    public List<ModelItem> getAppList() {
        return appList;
    }

    public void setAppList(List<ModelItem> appList) {
        this.appList = appList;
    }

    public static class ModelItem extends BaseAppInfo {
        @JSONField(name = "cTerminalType")
        private String terminalType; // 终端类型: 1,手机; 2, TV
        @JSONField(name = "cAppointment")
        private String appointment; // 是否显示预约，0-否 1-是
        @JSONField(name = "cPosterIcon")
        private String posterIcon;// 海报
        @JSONField(name = "cPosterPic")
        private String posterPic;// 海报
        @JSONField(name = "sCategoryName")
        private String categoryName; // 分类名称
        @JSONField(name = "cOs")
        private String os; // 系统: 1,安卓; 2,IOS；3,WP
        @JSONField(name = "cPlatforms")
        private String platforms; // 平台ID集合，多个平台以英文逗号分隔，最后以逗号结尾
        @JSONField(name = "cAppSource")
        private String appSource; // 应用来源: 0,自研;1,联运;2,其他;3,爬取,4.CPS,5 换量
        @JSONField(name = "cOfficialUrl")
        private String officialUrl; // 官方URL（Google、IOS或官方详情页）
        @JSONField(name = "sNotice")
        private String notice; // 应用通知，富文本编辑
        @JSONField(name = "sUpdateDesc")
        private String updateDesc; // 版本更新内容

        @JSONField(name = "hasAppointment")
        private boolean hasAppointment = false;//是否预约过了
        @JSONField(name = "sTestingStatus")
        private String testingStatus;// 公测状态
        @JSONField(name = "dDeleteFile")
        private String delTestTime; //删除测试数据时间

        public String getTerminalType() {
            return terminalType;
        }

        public void setTerminalType(String terminalType) {
            this.terminalType = terminalType;
        }

        public String getAppointment() {
            return appointment;
        }

        public void setAppointment(String appointment) {
            this.appointment = appointment;
        }


        public String getPosterIcon() {
            return posterIcon;
        }

        public void setPosterIcon(String posterIcon) {
            this.posterIcon = posterIcon;
        }

        public String getPosterPic() {
            return posterPic;
        }

        public void setPosterPic(String posterPic) {
            this.posterPic = posterPic;
        }


        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getPlatforms() {
            return platforms;
        }

        public void setPlatforms(String platforms) {
            this.platforms = platforms;
        }

        public String getAppSource() {
            return appSource;
        }

        public void setAppSource(String appSource) {
            this.appSource = appSource;
        }

        public String getOfficialUrl() {
            return officialUrl;
        }

        public void setOfficialUrl(String officialUrl) {
            this.officialUrl = officialUrl;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getUpdateDesc() {
            return updateDesc;
        }

        public void setUpdateDesc(String updateDesc) {
            this.updateDesc = updateDesc;
        }

        public boolean isHasAppointment() {
            return hasAppointment;
        }

        public void setHasAppointment(boolean hasAppointment) {
            this.hasAppointment = hasAppointment;
        }

        public String getTestingStatus() {
            return testingStatus;
        }

        public void setTestingStatus(String testingStatus) {
            this.testingStatus = testingStatus;
        }

        public String getDelTestTime() {
            return delTestTime;
        }

        public void setDelTestTime(String delTestTime) {
            this.delTestTime = delTestTime;
        }
    }

}
