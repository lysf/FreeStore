package com.snailgame.cjg.statistics;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.StaticsUtils;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

/**
 * Created by liuzl on 14-3-19.
 */
public class LivenessService extends IntentService {

    public static Intent newIntent(Context context) {
        return new Intent(context, LivenessService.class);
    }

    public static final String TAG = LivenessService.class.getSimpleName();

    public LivenessService() {
        super(LivenessService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<AppInfo> appInfos = MyGameDaoHelper.queryForAppInfo(this);
        if (appInfos == null || appInfos.size() == 0) return;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (ListUtils.isEmpty(tasks)) return;
        ActivityManager.RunningTaskInfo task = tasks.get(0);
        if (task == null) return;
        String lastPackage = SharedPreferencesUtil.getInstance().getLastPackage();
        if (lastPackage.equals(task.topActivity.getPackageName())) {
            return;
        } else {
            lastPackage = task.topActivity.getPackageName();
            SharedPreferencesUtil.getInstance().setLastPackage(lastPackage);
        }
        for (AppInfo info : appInfos) {
            if (info.getPkgName().equals(task.topActivity.getPackageName())) {
                StaticsUtils.openGame(info.getAppId());
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
