package com.snailgame.cjg.util.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by xixh on 2015/6/23.
 */
public class GameSdkDataModel {
    private List<BaseData> infos;

    @JSONField(name = "list")
    public List<BaseData> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(List<BaseData> infos) {
        this.infos = infos;
    }


    public static final class BaseData {
        private String CPackageName;
        private String CChannel;
        private String CPlatformVersion;
        private String IPlatformId;
        private String ITimeStamp;

        @JSONField(name = "CPackageName")
        public String getCPackageName() {
            return CPackageName;
        }

        @JSONField(name = "CChannel")
        public String getCChannel() {
            return CChannel;
        }

        @JSONField(name = "CPlatformVersion")
        public String getCPlatformVersion() {
            return CPlatformVersion;
        }

        @JSONField(name = "IPlatformId")
        public String getIPlatformId() {
            return IPlatformId;
        }

        @JSONField(name = "ITimeStamp")
        public String getITimeStamp() {
            return ITimeStamp;
        }

        @JSONField(name = "CPackageName")
        public void setCPackageName(String CPackageName) {
            this.CPackageName = CPackageName;
        }

        @JSONField(name = "CChannel")
        public void setCChannel(String CChannel) {
            this.CChannel = CChannel;
        }

        @JSONField(name = "CPlatformVersion")
        public void setCPlatformVersion(String CPlatformVersion) {
            this.CPlatformVersion = CPlatformVersion;
        }

        @JSONField(name = "IPlatformId")
        public void setIPlatformId(String IPlatformId) {
            this.IPlatformId = IPlatformId;
        }

        @JSONField(name = "ITimeStamp")
        public void setITimeStamp(String ITimeStamp) {
            this.ITimeStamp = ITimeStamp;
        }
    }
}
