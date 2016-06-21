package com.snailgame.cjg.downloadmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.MainThreadBus;

import java.util.ArrayList;

/**
 * Created by lic on 2015/6/11.
 * 如果在通知栏中点击更新按钮当前不是wifi或者免流量情况下，必须弹出提示框，此为提示框的承载activity
 */
public class UpdateInNotificationActivity extends Activity {

    public final static String LIST_DATA = "list_data";
    private ArrayList<AppInfo> infoList;

    public static Intent newIntent(Context context, ArrayList<AppInfo> infoList) {
        Intent intent = new Intent(context, UpdateInNotificationActivity.class);
        intent.putParcelableArrayListExtra(LIST_DATA, infoList);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoList = getIntent().getParcelableArrayListExtra(LIST_DATA);
        DownloadConfirm.showDownloadNotLoginAndNotWifi(UpdateInNotificationActivity.this, new DownloadConfirm.IConfirmResult() {
            @Override
            public void doDismissDialog(boolean isDialogDismiss) {
                UpdateInNotificationActivity.this.finish();
            }

            @Override
            public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                if (infoList != null) {
                    for (AppInfo appInfo : infoList) {
                        if (!isUseFlowDownLoad) {
                            appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                        }
                        DownloadHelper.startDownload(UpdateInNotificationActivity.this, appInfo);
                    }
                }
                MainThreadBus.getInstance().post(new UpdateChangeEvent());
            }
        });
    }

}
