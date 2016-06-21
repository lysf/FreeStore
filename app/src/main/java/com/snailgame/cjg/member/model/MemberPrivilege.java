package com.snailgame.cjg.member.model;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAJ_C on 2015/12/15.
 */
public class MemberPrivilege extends BaseDataModel {


    private ModelItem item;

    @JSONField(name = "item")
    public ModelItem getItem() {
        return item;
    }

    @JSONField(name = "item")
    public void setItem(ModelItem item) {
        this.item = item;
    }

    public static class ModelItem {

        private int privilegeId;// 特权ID


        private String privilegeName; // 特权名称

        private String desc;// 描述


        private String iconUrl;


        private String iconGrayUrl;

        private String iconLargeUrl;

        private String bgUrl;


        private String needLevel;

        private int needLevelId;
        private String type;// 特权类型

        private String config;// 标准配置格式

        private String updateTime;// 更新时间

        private String createTime; // 生成时间

        private boolean isActive; //是否获得特权, true=是, false=否

        @Nullable
        private Level level;
        private List<LevelPrivilege> levelPrivilges;// 等级特权


        private ArrayList<MemberArticle> articleList;  //这个里面放特权礼包

        @JSONField(name = "iPrivilegeId")
        public int getPrivilegeId() {
            return privilegeId;
        }

        @JSONField(name = "iPrivilegeId")
        public void setPrivilegeId(int privilegeId) {
            this.privilegeId = privilegeId;
        }

        @JSONField(name = "sPrivilegeName")
        public String getPrivilegeName() {
            return privilegeName;
        }

        @JSONField(name = "sPrivilegeName")
        public void setPrivilegeName(String privilegeName) {
            this.privilegeName = privilegeName;
        }

        @JSONField(name = "sDesc")
        public String getDesc() {
            return desc;
        }

        @JSONField(name = "sDesc")
        public void setDesc(String desc) {
            this.desc = desc;
        }

        @JSONField(name = "cType")
        public String getType() {
            return type;
        }

        @JSONField(name = "cType")
        public void setType(String type) {
            this.type = type;
        }

        @JSONField(name = "cConfig")
        public String getConfig() {
            return config;
        }

        @JSONField(name = "cConfig")
        public void setConfig(String config) {
            this.config = config;
        }

        @JSONField(name = "dUpdate")
        public String getUpdateTime() {
            return updateTime;
        }

        @JSONField(name = "dUpdate")
        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        @JSONField(name = "dCreate")
        public String getCreateTime() {
            return createTime;
        }

        @JSONField(name = "dCreate")
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        @JSONField(name = "active ")
        public boolean isActive() {
            return isActive;
        }

        @JSONField(name = "active ")
        public void setIsActive(boolean isActive) {
            this.isActive = isActive;
        }

        @JSONField(name = "cIconLarger")
        public String getIconLargeUrl() {
            return iconLargeUrl;
        }
        @JSONField(name = "cIconLarger")
        public void setIconLargeUrl(String iconLargeUrl) {
            this.iconLargeUrl = iconLargeUrl;
        }

        @JSONField(name = "levelPrivilges")
        public List<LevelPrivilege> getLevelPrivilges() {
            return levelPrivilges;
        }

        @JSONField(name = "levelPrivilges")
        public void setLevelPrivilges(List<LevelPrivilege> levelPrivilges) {
            this.levelPrivilges = levelPrivilges;
        }

        @JSONField(name = "articles")
        public ArrayList<MemberArticle> getArticleList() {
            return articleList;
        }

        @JSONField(name = "articles")
        public void setArticleList(ArrayList<MemberArticle> articleList) {
            this.articleList = articleList;
        }

        @Nullable
        @JSONField(name = "level")
        public Level getLevel() {
            return level;
        }
        @Nullable
        @JSONField(name = "level")
        public void setLevel(Level level) {
            this.level = level;
        }

        @JSONField(name = "cIcon")
        public String getIconUrl() {
            return iconUrl;
        }

        @JSONField(name = "cIcon")
        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        @JSONField(name = "cIconGray")
        public String getIconGrayUrl() {
            return iconGrayUrl;
        }

        @JSONField(name = "cIconGray")
        public void setIconGrayUrl(String iconGrayUrl) {
            this.iconGrayUrl = iconGrayUrl;
        }

        @JSONField(name = "cIconBackground")
        public String getBgUrl() {
            return bgUrl;
        }

        @JSONField(name = "cIconBackground")
        public void setBgUrl(String bgUrl) {
            this.bgUrl = bgUrl;
        }

        @JSONField(name = "sNeedLevel")
        public String getNeedLevel() {
            return needLevel;
        }

        @JSONField(name = "sNeedLevel")
        public void setNeedLevel(String needLevel) {
            this.needLevel = needLevel;
        }

        @JSONField(name = "iNeedLevel")
        public int getNeedLevelId() {
            return needLevelId;
        }

        @JSONField(name = "iNeedLevel")
        public void setNeedLevelId(int needLevelId) {
            this.needLevelId = needLevelId;
        }

        public static class Level {
            @JSONField(name = "sLevelName")
            private String levelName;

            @JSONField(name = "iLevelId")
            private int levelId;

            public String getLevelName() {
                return levelName;
            }

            public void setLevelName(String levelName) {
                this.levelName = levelName;
            }

            public int getLevelId() {
                return levelId;
            }

            public void setLevelId(int levelId) {
                this.levelId = levelId;
            }
        }

        public static class LevelPrivilege {

            private boolean isReceive;// 是否领取, true=已领取, false=未领取

            private int privilegeId; // 特权ID

            private String sDesc; // 描述

            private int levelId; // 等级ID
            private String levelName; //等级名称

            private String awardConfig; // 标准配置格式

            private MemberLevelArticle articleInfo; // 物品


            String content;
            String useMethod;
            String deadline;

            @JSONField(name = "receive")
            public boolean isReceive() {
                return isReceive;
            }

            @JSONField(name = "receive")
            public void setIsReceive(boolean isReceive) {
                this.isReceive = isReceive;
            }

            @JSONField(name = "iPrivilegeId")
            public int getPrivilegeId() {
                return privilegeId;
            }

            @JSONField(name = "iPrivilegeId")
            public void setPrivilegeId(int privilegeId) {
                this.privilegeId = privilegeId;
            }

            @JSONField(name = "iLevelId")
            public int getLevelId() {
                return levelId;
            }

            @JSONField(name = "iLevelId")
            public void setLevelId(int levelId) {
                this.levelId = levelId;
            }

            @JSONField(name = "cAwardConfig")
            public String getAwardConfig() {
                return awardConfig;
            }

            @JSONField(name = "cAwardConfig")
            public void setAwardConfig(String awardConfig) {
                this.awardConfig = awardConfig;
            }

            @JSONField(name = "article")
            public MemberLevelArticle getArticleInfo() {
                return articleInfo;
            }

            @JSONField(name = "article")
            public void setArticleInfo(MemberLevelArticle articleInfo) {
                this.articleInfo = articleInfo;
            }

            @JSONField(name = "sLevelName")
            public String getLevelName() {
                return levelName;
            }

            @JSONField(name = "sLevelName")
            public void setLevelName(String levelName) {
                this.levelName = levelName;
            }
            @JSONField(name = "sDesc")
            public String getsDesc() {
                return sDesc;
            }
            @JSONField(name = "sDesc")
            public void setsDesc(String sDesc) {
                this.sDesc = sDesc;
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

        }
    }


}
