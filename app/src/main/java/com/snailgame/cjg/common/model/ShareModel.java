package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by xixh on 2015/7/8.
 */
public class ShareModel extends BaseDataModel {
    ShareModelItem itemModel;

    @JSONField(name = "item")
    public ShareModelItem getItemModel() {
        return itemModel;
    }

    @JSONField(name = "item")
    public void setItemModel(ShareModelItem itemModel) {
        this.itemModel = itemModel;
    }


    public static class ShareModelItem {
        String shareTitle;
        String shareUrl;
        String sharePic;
        String shareDesc;

        public String getShareTitle() {
            return shareTitle;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public String getSharePic() {
            return sharePic;
        }

        public String getShareDesc() {
            return shareDesc;
        }

        @JSONField(name = "shareTitle")
        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        @JSONField(name = "shareUrl")
        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        @JSONField(name = "sharePic")
        public void setSharePic(String sharePic) {
            this.sharePic = sharePic;
        }

        public void setShareDesc(String shareDesc) {
            this.shareDesc = shareDesc;
        }
    }
}
