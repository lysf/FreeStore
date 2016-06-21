package com.snailgame.cjg.event;

import com.snailgame.cjg.personal.model.UserTaskModel;

/**
 * 用户任务刷新
 * Created by xixh on 2016/3/23.
 */
public class UserTaskRefreshEvent extends BaseEvent {
    private int result;
    private UserTaskModel model;

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_NETWORK_ERROR = 1;
    public static final int RESULT_ERROR = 2;

    public UserTaskRefreshEvent(int result, UserTaskModel model) {
        this.result = result;
        this.model = model;
    }

    public int getResult() {
        return result;
    }

    public UserTaskModel getModel() {
        return model;
    }
}
