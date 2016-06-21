package com.snailgame.cjg.download.model;

import com.snailgame.cjg.common.model.BaseDataModel;

/**
 * Created by xixh on 2015/7/10.
 */
public class FreeDownloadModel extends BaseDataModel {
    FreeDownloadInfo item;

    public FreeDownloadInfo getItem() {
        return item;
    }

    public void setItem(FreeDownloadInfo item) {
        this.item = item;
    }

    public static class FreeDownloadInfo {
        String cDownloadUrl;
        String cMd5;

        public String getcDownloadUrl() {
            return cDownloadUrl;
        }

        public String getcMd5() {
            return cMd5;
        }

        public void setcDownloadUrl(String cDownloadUrl) {
            this.cDownloadUrl = cDownloadUrl;
        }

        public void setcMd5(String cMd5) {
            this.cMd5 = cMd5;
        }
    }
}
