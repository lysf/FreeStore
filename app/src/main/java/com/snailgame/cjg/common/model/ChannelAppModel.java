package com.snailgame.cjg.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xixh on 2015/12/24.
 */
@Getter
@Setter
public class ChannelAppModel extends BaseDataModel {
    private ChannelAppModelItem item;

    @Getter
    @Setter
    public static class ChannelAppModelItem {
        public static final String TYPE_OPEN_APP = "1";
        public static final String TYPE_OPEN_URL = "2";
        public static final String TYPE_OPEN_APP_V = "3";
        private int nAppId;
        private int iChannelId;
        private String sUrl;
        private String cType;
        private String sExtend;
    }
}
