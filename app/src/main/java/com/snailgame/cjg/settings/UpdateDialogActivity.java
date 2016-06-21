package com.snailgame.cjg.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.UpdateDialogReshowEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ApkInstaller;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lic on 2015/10/21
 * <p/>
 * 免商店自己提示自己更新的dialog
 */
public class UpdateDialogActivity extends Activity implements View.OnClickListener {

    public static Intent newIntent(Context context, boolean isNotificationTag, AppInfo appInfo, String description, int flagActivityNewTask, boolean bFouce) {
        Intent intent = new Intent(context, UpdateDialogActivity.class);
        intent.putExtra(AppConstants.IS_NOTIFICATION_TAG, isNotificationTag);
        intent.putExtra(AppConstants.UPDATE_MYSHELF, appInfo);
        intent.putExtra(AppConstants.UPDATE_MYSHELF_DESC, description);
        intent.putExtra(AppConstants.UPDATE_MYSHELF_FORCE, bFouce);
        intent.addFlags(flagActivityNewTask);
        return intent;
    }

    public static final String TAG = UpdateDialogActivity.class.getSimpleName();
    @Bind(R.id.btn_ok)
    Button btnOK;
    @Bind(R.id.message)
    TextView describe;
    @Bind(R.id.btn_close)
    ImageView closeBtn;
    @Bind(R.id.dialog_layout)
    View view;
    @Bind(R.id.version_and_size)
    TextView versionText;
    @Bind(R.id.download_tips)
    TextView downloadTips;

    //progress界面
    @Bind(R.id.tv_download_size)
    TextView tv_download_size;
    @Bind(R.id.tv_total_size)
    TextView tv_total_size;
    @Bind(R.id.tv_speed)
    TextView tv_speed;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.speed_layout)
    View speedLayout;
    private DownloadingTaskListTask mDownloadTask;
    private AppInfo appInfo;
    private long taskId;
    //是否在下载中的标志位
    private boolean inDownload;
    //是否在下载失败的标志位
    private boolean downloadFailed;
    private boolean bFouce = false;

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (taskInfos != null && inDownload) {
            for (TaskInfo taskInfo : taskInfos) {
                if (taskInfo.getAppPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                    updateProgress(taskInfo, taskInfos, true);
                    taskId = taskInfo.getTaskId();
                    break;
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        appInfo = getIntent().getParcelableExtra(AppConstants.UPDATE_MYSHELF);
        String desc = getIntent().getStringExtra(AppConstants.UPDATE_MYSHELF_DESC);
        bFouce = getIntent().getBooleanExtra(AppConstants.UPDATE_MYSHELF_FORCE, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_update);
        MainThreadBus.getInstance().register(this);
        ButterKnife.bind(this);
        describe.setText(desc);
        describe.setMovementMethod(ScrollingMovementMethod.getInstance());
        versionText.setText(appInfo.getVersionName() + " " + FileUtil.formatFileSize(this, appInfo.getApkSize()));
        btnOK.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (bFouce) {
            closeBtn.setVisibility(View.INVISIBLE);
        }
        initProgressLayout();
        queryTask(false);
    }

    // 初始化进度条数据
    private void initProgressLayout() {
        tv_speed.setText(DownloadHelper.DEFAULT_DOWNLOAD_SPEED + "/S");
        tv_download_size.setText("0");
        tv_total_size.setText(FileUtil.formatFileSize(this, appInfo.getApkSize()));
        progressBar.setMax((int) appInfo.getApkSize());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                inDownload = true;
                if (downloadFailed) {
                    downloadTips.setText(getString(R.string.update_self_dialog_tips));
                    downloadTips.setTextColor(ResUtil.getColor(R.color.manage_score_normal));
                    DownloadHelper.restartDownload(this, taskId);
                    initProgressLayout();
                    // 弹出进度框
                    btnOK.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    speedLayout.setVisibility(View.VISIBLE);

                } else {
                    queryTask(true);
                }

                break;
            case R.id.btn_close:
                if (inDownload) {
                    if (bFouce)
                        MainThreadBus.getInstance().post(new UpdateDialogReshowEvent());
                    DownloadHelper.cancelDownload(this, taskId);
                }
                finish();
                break;
        }

    }

    /**
     * 查询下载任务
     *
     * @param forUpdateProgress 是否用户点击了更新按钮进行正式下载
     */
    private void queryTask(boolean forUpdateProgress) {
        mDownloadTask = new DownloadingTaskListTask(forUpdateProgress);
        mDownloadTask.execute();
    }

    /**
     * 按照更新进度
     *
     * @param taskInfo          下载任务
     * @param taskInfos         下载任务列表
     * @param forUpdateProgress 是否用户点击了更新按钮进行正式下载
     */
    private void updateProgress(TaskInfo taskInfo, List<TaskInfo> taskInfos, boolean forUpdateProgress) {
        //如果仅仅查询当前下载是否完成则直接返回即可
        if (!forUpdateProgress) {
            if (taskInfo.getDownloadState() == DownloadManager.STATUS_SUCCESSFUL) {
                downloadTips.setText(getString(R.string.silence_download_finish));
                downloadTips.setTextColor(ResUtil.getColor(R.color.manage_score_high));
                btnOK.setText(getString(R.string.silence_install));
            }
            return;
        }
        //如果当前任务不是已经完成，则显示进度条
        if (taskInfo.getDownloadState() != DownloadManager.STATUS_SUCCESSFUL) {
            // 弹出进度框
            btnOK.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            speedLayout.setVisibility(View.VISIBLE);
        }
        switch (taskInfo.getDownloadState()) {
            case DownloadManager.STATUS_SUCCESSFUL:        // 下载完成
                try {
                    Uri uri = Uri.parse(taskInfo.getApkLocalUri());
                    ApkInstaller apkInstaller = new ApkInstaller(this, 0, uri.getPath(), taskInfo.getAppPkgName());
                    apkInstaller.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case DownloadManager.STATUS_FAILED:    // 下载失败
                inDownload = false;
                downloadFailed = true;
                btnOK.setText(getString(R.string.download_failed_click_to_retry));
                downloadTips.setText(getString(R.string.download_failed_click_to_retry));
                downloadTips.setTextColor(ResUtil.getColor(R.color.manage_score_low));
                btnOK.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                speedLayout.setVisibility(View.GONE);
                break;
            case DownloadManager.STATUS_PAUSED:     // 下载暂停
                DownloadHelper.resumeDownload(this, taskId);
                tv_speed.setText(DownloadHelper.DEFAULT_DOWNLOAD_SPEED + "/S");
                break;
            case DownloadManager.STATUS_RUNNING:   // 下载中
                tv_download_size.setText(FileUtil.formatFileSize(this, taskInfo.getDownloadedSize()));
                progressBar.setProgress((int) taskInfo.getDownloadedSize());

                DownloadHelper.calcDownloadSpeed(this, appInfo, taskInfo);
                tv_speed.setText(appInfo.getDownloadSpeed() + "/S");
                break;
            case DownloadManager.STATUS_PENDING:    // 等待中
                if (taskInfos != null && taskInfos.size() > 1) {
                    long[] temp = new long[taskInfos.size()];
                    int index = 0;
                    for (TaskInfo item : taskInfos) {
                        if (!item.getAppPkgName().equals(taskInfo.getAppPkgName())) {
                            if (item.getDownloadState() == DownloadManager.STATUS_RUNNING
                                    || item.getDownloadState() == DownloadManager.STATUS_PENDING) {
                                temp[index] = item.getTaskId();
                                index++;
                            }
                        }
                    }

                    long[] ids = new long[index];
                    System.arraycopy(temp, 0, ids, 0, index);
                    DownloadHelper.pauseDownload(getApplicationContext(), ids);
                    DownloadHelper.resumeDownload(getApplicationContext(), taskInfo.getTaskId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!bFouce) {
            if (inDownload) {
                DownloadHelper.cancelDownload(this, taskId);
            }
            finish();
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);

        if (mDownloadTask != null)
            mDownloadTask.cancel(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!bFouce) {
                if (inDownload) {
                    DownloadHelper.cancelDownload(this, taskId);
                }
                finish();
            }
        }
        return false;

    }

    class DownloadingTaskListTask extends AsyncTask<Void, Void, Boolean> {
        List<TaskInfo> taskInfoList;
        TaskInfo mTaskInfo;
        boolean forUpdateProgress;

        public DownloadingTaskListTask(boolean forUpdateProgress) {
            this.forUpdateProgress = forUpdateProgress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int queryFilter = DownloadManager.STATUS_RUNNING
                    | DownloadManager.STATUS_FAILED
                    | DownloadManager.STATUS_PAUSED
                    | DownloadManager.STATUS_PENDING
                    | DownloadManager.STATUS_PENDING_FOR_WIFI
                    | DownloadManager.STATUS_SUCCESSFUL
                    | DownloadManager.STATUS_INSTALLING
                    | DownloadManager.STATUS_PATCHING;

            taskInfoList = DownloadHelper.getDownloadTasks(
                    UpdateDialogActivity.this,
                    DownloadHelper.QUERY_TYPE_BY_STATUS,
                    queryFilter);

            if (ListUtils.isEmpty(taskInfoList)) {
                return false;
            }

            for (TaskInfo taskInfo : taskInfoList) {
                if (taskInfo.getAppPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                    taskId = taskInfo.getTaskId();
                    mTaskInfo = taskInfo;
                    return true;
                }
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //如果下载列表中没有免商店的下载任务，则进行下载，有的话就更新进度
            if (result) {
                updateProgress(mTaskInfo, taskInfoList, forUpdateProgress);
            } else {
                if (forUpdateProgress)
                    DownloadHelper.startDownload(UpdateDialogActivity.this, appInfo);
            }

        }
    }

}