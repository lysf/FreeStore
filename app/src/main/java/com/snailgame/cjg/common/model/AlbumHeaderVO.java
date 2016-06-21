package com.snailgame.cjg.common.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 专辑头部
 */
public class AlbumHeaderVO implements Serializable {
    private String albumIcon;
    private String albumDesc;
    private String albumType;
    private String albumJump;
    private String albumSubTitle;
    private String albumTitle;
    private String albumPic;

    public String getAlbumTitle() {
        return albumTitle;
    }

    @JSONField(name = "albumTitle")
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    @JSONField(name = "albumSubTitle")
    public void setAlbumSubTitle(String albumSubTitle) {
        this.albumSubTitle = albumSubTitle;
    }

    public String getAlbumSubTitle() {

        return albumSubTitle;
    }

    public String getAlbumIcon() {
        return albumIcon;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getAlbumPic() {
        return albumPic;
    }

    @JSONField(name = "albumIcon")
    public void setAlbumIcon(String albumIcon) {
        this.albumIcon = albumIcon;
    }

    public String getAlbumDesc() {
        return albumDesc;
    }

    @JSONField(name = "albumDesc")
    public void setAlbumDesc(String albumDesc) {
        this.albumDesc = albumDesc;
    }

    public String getAlbumType() {
        return albumType;
    }

    @JSONField(name = "albumType")
    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }
	
    public String getAlbumJump() {
        return albumJump;
    }

    @JSONField(name = "albumJump")
    public void setAlbumJump(String albumJump) {
        this.albumJump = albumJump;
    }
}
