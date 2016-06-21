package com.snailgame.cjg.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yftx
 * on 2/22/14.
 */
public class CollectionModel extends BaseDataModel implements Parcelable{
    private List<BaseAppInfo> infos;
    private PageInfo page;

    public AlbumHeaderVO album;

    @JSONField(name = "list")
    public List<BaseAppInfo> getInfos() {
        return infos;
    }

    @JSONField(name = "list")
    public void setInfos(List<BaseAppInfo> infos) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.infos == null)
            this.infos = new ArrayList<BaseAppInfo>();
        dest.writeParcelableArray(this.infos.toArray(new BaseAppInfo[this.infos.size()]), flags);

        dest.writeParcelable(this.page, flags);
        dest.writeSerializable(this.album);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    public CollectionModel() {
    }

    private CollectionModel(Parcel in) {
        Parcelable[] list = in.readParcelableArray(BaseAppInfo.class.getClassLoader());
        if (list == null) {
            this.infos = new ArrayList<BaseAppInfo>();
        } else {
            this.infos = new ArrayList(Arrays.asList(list));
        }

        this.page = in.readParcelable(PageInfo.class.getClassLoader());
        this.album = (AlbumHeaderVO) in.readSerializable();
        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<CollectionModel> CREATOR = new Creator<CollectionModel>() {
        public CollectionModel createFromParcel(Parcel source) {
            return new CollectionModel(source);
        }

        public CollectionModel[] newArray(int size) {
            return new CollectionModel[size];
        }
    };
}
