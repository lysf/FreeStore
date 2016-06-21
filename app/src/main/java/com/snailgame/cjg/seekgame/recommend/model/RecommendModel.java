package com.snailgame.cjg.seekgame.recommend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseAppInfo;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 推荐页面 model 类
 * Created by yftx
 * on 2/21/14.
 */
public class RecommendModel extends BaseDataModel implements Parcelable{
    //所有数据
    public List<RecommendInfo> allListInfo = new ArrayList<RecommendInfo>();

    //推荐头 viewpage 的数据
    public List<RecommendInfo> viewPagerInfo = new ArrayList<RecommendInfo>();

    //推荐下方列表的 数据
    public List<BaseAppInfo> listViewInfo = new ArrayList<BaseAppInfo>();

    //推荐view page 两个广告为的数据
    public List<RecommendInfo> advertisementInfo = new ArrayList<RecommendInfo>();


    public PageInfo pageInfo = new PageInfo();


    private static final int TYPE_VIEWPAGER = 1;
    private static final int TYPE_ADVERTISEMENT = 2;
    private static final int TYPE_LISTVIEW = 3;

    @JSONField(name = "list")
    public List<RecommendInfo> getAllListInfo() {
        return allListInfo;
    }

    @JSONField(name = "list")
    public void setAllListInfo(List<RecommendInfo> allListInfo) {
        this.allListInfo = allListInfo;
    }

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }


    public void setInfos(List<RecommendInfo> infos) {
        resetData();
        for (RecommendInfo info : infos) {
            int type = Integer.valueOf(info.getcPostion());
            switch (type) {
                case TYPE_VIEWPAGER:
                    viewPagerInfo.add(info);
                    break;
                case TYPE_ADVERTISEMENT:
                    advertisementInfo.add(info);
                    break;
                default:
                    listViewInfo.add(info);
                    break;
            }
        }
    }

    private void resetData() {
        viewPagerInfo.clear();
        listViewInfo.clear();
        advertisementInfo.clear();
    }


    public RecommendModel() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (allListInfo == null)
            allListInfo = new ArrayList<>();
        if (viewPagerInfo == null)
            viewPagerInfo = new ArrayList<>();
        if (listViewInfo == null)
            listViewInfo = new ArrayList<>();
        if (advertisementInfo == null)
            advertisementInfo = new ArrayList<>();

        dest.writeParcelableArray(allListInfo.toArray(new RecommendInfo[this.allListInfo.size()]), flags);
        dest.writeParcelableArray(viewPagerInfo.toArray(new RecommendInfo[this.viewPagerInfo.size()]), flags);
        dest.writeParcelableArray(advertisementInfo.toArray(new RecommendInfo[this.advertisementInfo.size()]), flags);
        dest.writeParcelableArray(listViewInfo.toArray(new BaseAppInfo[this.advertisementInfo.size()]), flags);

        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
    }

    private RecommendModel(Parcel in) {
        Parcelable[] allList = in.readParcelableArray(RecommendInfo.class.getClassLoader());
        if (allList == null) {
            allListInfo = new ArrayList<>();
        } else {
            allListInfo = new ArrayList(Arrays.asList(allList));
        }

        Parcelable[] viewPagerList = in.readParcelableArray(RecommendInfo.class.getClassLoader());
        if (viewPagerList == null) {
            viewPagerInfo = new ArrayList<>();
        } else {
            viewPagerInfo = new ArrayList(Arrays.asList(viewPagerList));
        }

        Parcelable[] advertisementList = in.readParcelableArray(RecommendInfo.class.getClassLoader());
        if (advertisementList == null) {
            advertisementInfo = new ArrayList<>();
        } else {
            advertisementInfo = new ArrayList(Arrays.asList(advertisementList));
        }

        Parcelable[] list = in.readParcelableArray(BaseAppInfo.class.getClassLoader());
        if (list == null) {
            listViewInfo = new ArrayList<>();
        } else {
            listViewInfo = new ArrayList(Arrays.asList(list));
        }

        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.code = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<RecommendModel> CREATOR = new Creator<RecommendModel>() {
        public RecommendModel createFromParcel(Parcel source) {
            return new RecommendModel(source);
        }

        public RecommendModel[] newArray(int size) {
            return new RecommendModel[size];
        }
    };
}
