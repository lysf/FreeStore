package com.snailgame.cjg.event;

import android.text.TextUtils;

import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.global.AppConstants;

import java.util.ArrayList;

/**
 * 下载信息变更
 * Created by xixh on 2015/3/10.
 */
public class DownloadInfoChangeEvent extends BaseEvent {
    private ArrayList<TaskInfo> taskInfos;

    public DownloadInfoChangeEvent(ArrayList<TaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }

    public ArrayList<TaskInfo> getTaskInfos(boolean isSkinApp) {
        if (taskInfos != null) {
            ArrayList<TaskInfo> results = new ArrayList<>();
            for (TaskInfo taskInfo : taskInfos) {
                if (TextUtils.equals(taskInfo.getAppType(), AppConstants.VALUE_TYPE_SKIN) == isSkinApp) {
                    results.add(taskInfo);
                }
            }
            return results;
        }
        return null;
    }
}
