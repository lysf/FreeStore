package com.snailgame.cjg.personal.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by sunxy on 2015/2/2.
 */
public class TaskModel {
    private int iGroupId; // 任务ID
    private String cLogo; // 任务图标
    private String sGroupName; // 任务名称
    private String sGroupDescription; // 任务描述
    private int cCategory; // 任务类别，1:一次, 2:日常
    private int cGroupStatus; // 任务状态：0:可接受(未完成)， 1：进行中(未完成)，2：已完成(未领取)，3：已完成(已领取)，4：已放弃，5：不可接受
    private List<Award> awardList;
    private List<MemberAward> memberAwardList;

    public List<Award> getAwardList() {
        return awardList;
    }


    private String cConfig; // 引导界面配置, 10=下载某款应用、11=资料完善、12=搜索

    private ConfigItem configItem;// 引导界面配置 解析后数据


    @JSONField(name = "cConfig")
    public String getcConfig() {
        return cConfig;
    }

    @JSONField(name = "cConfig")
    public void setcConfig(String cConfig) {
        this.cConfig = cConfig;
    }

    @JSONField(name = "awardList")
    public void setAwardList(List<Award> awardList) {
        this.awardList = awardList;
    }

    public int getiGroupId() {
        return iGroupId;
    }

    @JSONField(name = "IGroupId")
    public void setiGroupId(int iGroupId) {
        this.iGroupId = iGroupId;
    }

    public String getcLogo() {
        return cLogo;
    }

    @JSONField(name = "CLogo")
    public void setcLogo(String cLogo) {
        this.cLogo = cLogo;
    }

    public String getsGroupName() {
        return sGroupName;
    }

    @JSONField(name = "SGroupName")
    public void setsGroupName(String sGroupName) {
        this.sGroupName = sGroupName;
    }

    public String getsGroupDescription() {
        return sGroupDescription;
    }

    @JSONField(name = "SGroupDescription")
    public void setsGroupDescription(String sGroupDescription) {
        this.sGroupDescription = sGroupDescription;
    }

    public int getcCategory() {
        return cCategory;
    }

    @JSONField(name = "CCategory")
    public void setcCategory(int cCategory) {
        this.cCategory = cCategory;
    }

    public int getcGroupStatus() {
        return cGroupStatus;
    }

    @JSONField(name = "CGroupStatus")
    public void setcGroupStatus(int cGroupStatus) {
        this.cGroupStatus = cGroupStatus;
    }

    public ConfigItem getConfigItem() {
        return configItem;
    }

    public void setConfigItem(ConfigItem configItem) {
        this.configItem = configItem;
    }

    public List<MemberAward> getMemberAwardList() {
        return memberAwardList;
    }

    @JSONField(name = "memberAwardList")
    public void setMemberAwardList(List<MemberAward> memberAwardList) {
        this.memberAwardList = memberAwardList;
    }

    public static class Award {
        String name;
        String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class MemberAward {
        String level;
        String name;
        String value;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ConfigItem {
        @JSONField(name = "type")
        int type;
        @JSONField(name = "content")
        List<configItemContent> content;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<configItemContent> getContent() {
            return content;
        }

        public void setContent(List<configItemContent> content) {
            this.content = content;
        }
    }

    public static class configItemContent {
        @JSONField(name = "appId")
        int appId;
        @JSONField(name = "another")
        String another;

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getAnother() {
            return another;
        }

        public void setAnother(String another) {
            this.another = another;
        }
    }

}
