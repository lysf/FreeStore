package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lic
 */
public class NecessaryAppInfoModel extends BaseDataModel implements Serializable {
    private List<NecessaryAppInfo> infos;
    private PageInfo page;
    private AlbumHeaderVO album;

    @JSONField(name = "list")
    public List<NecessaryAppInfo> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(List<NecessaryAppInfo> infos) {
        this.infos = infos;
    }

    @JSONField(name = "page")
    public PageInfo getPage() {
        return page;
    }

    @JSONField(name = "page")
    public void setPage(PageInfo page) {
        this.page = page;
    }

    public AlbumHeaderVO getAlbum() {
        return album;
    }

    @JSONField(name = "albumHeader")
    public void setAlbum(AlbumHeaderVO album) {
        this.album = album;
    }
}
