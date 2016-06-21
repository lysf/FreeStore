package com.snailgame.cjg.personal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.model.PageInfo;

import java.util.List;

/*
 * 积分记录
 * created by xixh 14-08-26
 */
public class ScoreHistoryModel extends BaseDataModel {
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

    @JSONField(name = "page")
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    @JSONField(name = "page")
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class ModelItem implements Parcelable {
        @JSONField(name = "nUserId")
        private long userId; // 用户id
        @JSONField(name = "nSqNo")
        private long sqNo; // 物品序号
        @JSONField(name = "cSource")
        private String source; // 来源，例：兑换，礼包，抽奖
        @JSONField(name = "cRefId")
        private String refId; // 关联订单ID
        @JSONField(name = "cType")
        private String type; // 类型 1：消费，2：获取
        @JSONField(name = "iIntegral")
        private int integral; // 积分
        @JSONField(name = "sDesc")
        private String desc; // 描述
        @JSONField(name = "cChannel")
        private String channel; // 积分消费渠道，例：物品，语音，流量等
        @JSONField(name = "cChannelContext")
        private String channelContext; // 积分消费获取内容，按渠道JSON格式 {"cdkey":"333","goodsId":22}
        @JSONField(name = "iPlatformId")
        private int platformId; // 平台ID
        @JSONField(name = "dCreate")
        private String createTime; // 生成时间

        public ModelItem() {
        }

        protected ModelItem(Parcel in) {
            source = in.readString();
            refId = in.readString();
            type = in.readString();
            desc = in.readString();
            channel = in.readString();
            channelContext = in.readString();
            createTime = in.readString();
        }


        public static final Creator<ModelItem> CREATOR = new Creator<ModelItem>() {
            @Override
            public ModelItem createFromParcel(Parcel in) {
                return new ModelItem(in);
            }

            @Override
            public ModelItem[] newArray(int size) {
                return new ModelItem[size];
            }
        };

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public long getSqNo() {
            return sqNo;
        }

        public void setSqNo(long sqNo) {
            this.sqNo = sqNo;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getChannelContext() {
            return channelContext;
        }

        public void setChannelContext(String channelContext) {
            this.channelContext = channelContext;
        }

        public int getPlatformId() {
            return platformId;
        }

        public void setPlatformId(int platformId) {
            this.platformId = platformId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(source);
            dest.writeString(refId);
            dest.writeString(type);
            dest.writeString(desc);
            dest.writeString(channel);
            dest.writeString(channelContext);
            dest.writeString(createTime);
        }
    }
}
