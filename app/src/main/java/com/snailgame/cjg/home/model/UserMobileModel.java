package com.snailgame.cjg.home.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.snailgame.cjg.common.model.BaseDataModel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/6/12.
 */
@Getter
@Setter
public class UserMobileModel extends BaseDataModel {

    @JSONField(name = "item")
    private ModelItem item;


    @Getter
    @Setter
    public static class ModelItem {
        private String voiceRemainSize;//剩余语音时长，单位：分钟
        private String flowRemainSize;//剩余流量，单位：MB
        private String msgRemainSize;//剩余短信数
        private String city;//归属地
        private String balance;//余额

        private int status = MobileState.LOADING;//0.loading 1.success 2.failed  4.weihu
    }

    public interface MobileState {
        int LOADING = 0; //加载
        int SUCCESS = 1; //成功
        int FAILED = 2; //失败
        int MAINTENANCE = 3; //维护
    }
}
