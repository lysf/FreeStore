package com.snailgame.cjg.detail.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/**
 * Created by taj on 2014/11/20.
 */
public class ForumModel {

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

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class ModelItem {
        private String tid;  //帖子ID
        private String readperm;
        private String author;     //作者名
        private String authorid;       //作者ID
        private String subject;  //帖子标题
        private String dateline;         //发帖时间
        private String lastpost;              //最后回复时间
        private String lastposter;      //最后回复人
        private String views;          //浏览量
        private String replies;        //回复数
        private String digest;
        private String attachment;       //附件数
        private String dbdateline;        //发帖时间戳
        private String dblastpost;       //回复时间戳
        private String displayorder;           //排序值, 3: 全局置顶， 2：区域置顶 1：本版置顶 0：正常

        @JSONField(name = "tid")
        public String getTid() {
            return tid;
        }

        @JSONField(name = "tid")
        public void setTid(String tid) {
            this.tid = tid;
        }

        @JSONField(name = "readperm")
        public String getReadperm() {
            return readperm;
        }

        @JSONField(name = "readperm")
        public void setReadperm(String readperm) {
            this.readperm = readperm;
        }

        @JSONField(name = "author")
        public String getAuthor() {
            return author;
        }

        @JSONField(name = "author")
        public void setAuthor(String author) {
            this.author = author;
        }

        @JSONField(name = "authorid")
        public String getAuthorid() {
            return authorid;
        }

        @JSONField(name = "authorid")
        public void setAuthorid(String authorid) {
            this.authorid = authorid;
        }

        @JSONField(name = "subject")
        public String getSubject() {
            return subject;
        }

        @JSONField(name = "subject")
        public void setSubject(String subject) {
            this.subject = subject;
        }

        @JSONField(name = "dateline")
        public String getDateline() {
            return dateline;
        }

        @JSONField(name = "dateline")
        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        @JSONField(name = "lastpost")
        public String getLastpost() {
            return lastpost;
        }

        @JSONField(name = "lastpost")
        public void setLastpost(String lastpost) {
            this.lastpost = lastpost;
        }

        @JSONField(name = "lastposter")
        public String getLastposter() {
            return lastposter;
        }

        @JSONField(name = "lastposter")
        public void setLastposter(String lastposter) {
            this.lastposter = lastposter;
        }

        @JSONField(name = "views")
        public String getViews() {
            return views;
        }

        @JSONField(name = "views")
        public void setViews(String views) {
            this.views = views;
        }

        @JSONField(name = "replies")
        public String getReplies() {
            return replies;
        }

        @JSONField(name = "replies")
        public void setReplies(String replies) {
            this.replies = replies;
        }

        @JSONField(name = "digest")
        public String getDigest() {
            return digest;
        }

        @JSONField(name = "digest")
        public void setDigest(String digest) {
            this.digest = digest;
        }

        @JSONField(name = "attachment")
        public String getAttachment() {
            return attachment;
        }

        @JSONField(name = "attachment")
        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        @JSONField(name = "dbdateline")
        public String getDbdateline() {
            return dbdateline;
        }

        @JSONField(name = "dbdateline")
        public void setDbdateline(String dbdateline) {
            this.dbdateline = dbdateline;
        }

        @JSONField(name = "dblastpost")
        public String getDblastpost() {
            return dblastpost;
        }

        @JSONField(name = "dblastpost")
        public void setDblastpost(String dblastpost) {
            this.dblastpost = dblastpost;
        }

        @JSONField(name = "displayorder")
        public String getDisplayorder() {
            return displayorder;
        }

        @JSONField(name = "displayorder")
        public void setDisplayorder(String displayorder) {
            this.displayorder = displayorder;
        }
    }
}
