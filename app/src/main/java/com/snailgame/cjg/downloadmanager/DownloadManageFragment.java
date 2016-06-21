package com.snailgame.cjg.downloadmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.DownloadManager;
import com.snailgame.cjg.download.core.RealSystemFacade;
import com.snailgame.cjg.download.core.SystemFacade;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.downloadmanager.adapter.DownloadManageAppAdapter;
import com.snailgame.cjg.downloadmanager.util.MultiSelectionUtil;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.event.DownloadManageChangeEvent;
import com.snailgame.cjg.event.DownloadRemoveEvent;
import com.snailgame.cjg.event.DownloadTaskRemoveEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.PackageInfoUtil;
import com.snailgame.cjg.util.SharedPreferencesHelper;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.interfaces.DeleteDownloadTaskDialogListener;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-11
 * Time : 下午3:33
 * Description :下载管理
 */
public class DownloadManageFragment extends AbsBaseFragment implements
        AdapterView.OnItemClickListener, GameManageActivity.IMultiSelectableFragment {
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    public DownloadManageAppAdapter downloadingListAdapter;
    public List<AppInfo> downloadingList = new ArrayList<AppInfo>();
    private SystemFacade systemFacade;
    private DownloadingTaskListTask mDownloadTask;

    public ActionMode actionMode;

    private boolean isGuide = false;

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (loadMoreListView != null && taskInfos != null) {
            for (TaskInfo taskInfo : taskInfos) {
                updateProgress(taskInfo);
            }
            if (downloadingListAdapter != null)
                downloadingListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.base_game_manager_fragment;
    }

    @Subscribe
    public void onRemoveDownload(DownloadRemoveEvent event) {
        long downloadId = event.getDownloadId();

        if (ListUtils.isEmpty(downloadingList)) {
            showEmptyDownloadMsg();
            showEmpty();
            return;
        }


        for (AppInfo appInfo : downloadingList) {
            if (appInfo.getApkDownloadId() == downloadId) {
                downloadingList.remove(appInfo);
                break;
            }
        }
        downloadingListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        ActionModeHelper actionModeHelper = new ActionModeHelper();
        loadMoreListView.setOnItemClickListener(DownloadManageFragment.this);
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(4)));
        loadMoreListView.addHeaderView(view);
        MultiSelectionUtil.attachMultiSelectionController(
                loadMoreListView, (ActionBarActivity) getActivity(), actionModeHelper);
        downloadingListAdapter = new DownloadManageAppAdapter(mParentActivity, downloadingList);
        loadMoreListView.setAdapter(downloadingListAdapter);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
    }

    @Override
    protected void saveData(Bundle outState) {
    }


    @Override
    public void onResume() {
        super.onResume();
        queryTask();
        if (systemFacade == null) {
            systemFacade = new RealSystemFacade(getActivity());
        }
        MainThreadBus.getInstance().register(this);
    }

    public void queryTask() {
        downloadingList.clear();
        downloadingListAdapter.notifyDataSetChanged();

        if (mDownloadTask != null)
            mDownloadTask.cancel(true);
        mDownloadTask = new DownloadingTaskListTask();
        mDownloadTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        finishActionMode();
        MainThreadBus.getInstance().unregister(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDownloadTask != null)
            mDownloadTask.cancel(true);
    }

    @Override
    public void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public boolean isActionMode() {
        return downloadingListAdapter != null && downloadingListAdapter.isActionMode();
    }

    protected void showEmptyDownloadMsg() {
        getEmptyView().setEmptyMessage(FreeStoreApp.getContext().getString(R.string.empty_download_msg));
    }


    class DownloadingTaskListTask extends AsyncTask<Void, Void, Boolean> {
        List<TaskInfo> taskInfoList;
        PackageInfo packageInfo;
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showLoading();
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
                    mParentActivity,
                    DownloadHelper.QUERY_TYPE_BY_STATUS,
                    queryFilter);

            if (ListUtils.isEmpty(taskInfoList)) {
                return false;
            }

            for (TaskInfo taskInfo : taskInfoList) {
                AppInfo appInfo = getAppInfo(taskInfo);

                packageInfo = PackageInfoUtil.getPackageInfoByName(mParentActivity, appInfo.getPkgName());
                if (taskInfo.getDownloadState() == DownloadManager.STATUS_SUCCESSFUL
                        && packageInfo != null
                        && packageInfo.versionCode >= taskInfo.getAppVersionCode()) {
                    appInfo.setDownloadState(DownloadManager.STATUS_EXTRA_INSTALLED);
                }
                appInfo.setChecked(false);

                // 免商店自身已升级到最新版本后，不在下载管理中显示
                if ((appInfo.getDownloadState() == DownloadManager.STATUS_EXTRA_INSTALLED ||
                        SharedPreferencesUtil.getInstance().isSilenceUpdate()) //如果是静默更新
                        && appInfo.getPkgName() != null
                        && appInfo.getPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                    continue;
                }

                //如果是皮肤 则不显示
                if (appInfo.getcAppType() != null
                        && appInfo.getcAppType().equals(AppConstants.VALUE_TYPE_SKIN)) {
                    continue;
                }

                appInfoList.add(appInfo);
            }

            return !appInfoList.isEmpty();
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!isGuide) {
                guide(mParentActivity, AppConstants.USER_GUIDE_DOWNLOAD_TASKS_DELETE,
                        AppConstants.USER_GUIDE_COUNT_DOWNLOAD_TASKS_DELETE, R.string.user_guide_download_tasks_delete);
                isGuide = true;
            }
            if (!result) {
                showEmptyDownloadMsg();
                showEmpty();
            }
            downloadingList.addAll(appInfoList);
            downloadingListAdapter.refreshData(downloadingList);
        }
    }

    public void guide(Context context, String prefKey, int prefValue, int msgId) {

        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance();
        int actVal = helper.getValue(prefKey, 0);
        if (actVal < prefValue) {
            ToastUtils.showMsgLong(context, ResUtil
                    .getString(msgId));
            helper.putValue(prefKey, actVal + 1);
            helper.commitValue();
        }
    }

    private AppInfo getAppInfo(TaskInfo taskInfo) {
        AppInfo temp = new AppInfo();
        temp.setAppId(taskInfo.getAppId());
        temp.setAppName(taskInfo.getAppLabel());
        temp.setPkgName(taskInfo.getAppPkgName());
        temp.setIcon(taskInfo.getAppIconUrl());
        temp.setApkDownloadId(taskInfo.getTaskId());
        temp.setVersionCode(taskInfo.getAppVersionCode());

        long totalSize = taskInfo.getApkTotalSize();
        temp.setApkSize(totalSize < 0 ? temp.getApkSize() : taskInfo.getApkTotalSize());
        temp.setDownloadedPercent(taskInfo.getTaskPercent());
        temp.setDownloadedSize(taskInfo.getDownloadedSize());
        temp.setDownloadState(taskInfo.getDownloadState());
        temp.setLocalUri(taskInfo.getApkLocalUri());
        temp.setcFlowFree(taskInfo.getFlowFreeState());
        temp.setcAppType(taskInfo.getAppType());
        temp.setDownloadHint(taskInfo.getDownloadHint());
        return temp;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (view != null) {
            Object object = view.getTag();
            AppInfo appInfo;
            if (object != null && object instanceof DownloadViewHolder) {
                appInfo = (AppInfo) loadMoreListView.getItemAtPosition(i);
                if (downloadingListAdapter != null) {
                    if (appInfo != null) {
                        // 若应用为免商店自身，则点击不会跳转
                        if ((appInfo.getPkgName()) != null
                                && appInfo.getPkgName().equals(AppConstants.APP_PACKAGE_NAME)) {
                            return;
                        }
                        mParentActivity.startActivity(DetailActivity.newIntent(mParentActivity,
                                appInfo.getAppId(), GameManageActivity.createRoute()));
                    }
                }
            }
        }
    }

    long oldTime;

    private void updateProgress(TaskInfo taskInfo) {
        for (AppInfo appInfo : downloadingList) {
            if (taskInfo.getAppId() == appInfo.getAppId()) {
                // calculate the mDownloadTask download speed
                long newTime = System.currentTimeMillis();
                DownloadHelper.calcDownloadSpeed(getActivity(), appInfo, taskInfo);
                oldTime = newTime;
                appInfo.setDownloadedSize(taskInfo.getDownloadedSize());
                appInfo.setDownloadTotalSize(-1 == taskInfo.getApkTotalSize() ? appInfo.getApkSize() : taskInfo.getApkTotalSize());
                appInfo.setDownloadedPercent(taskInfo.getTaskPercent());
                appInfo.setDownloadState(taskInfo.getDownloadState());
                appInfo.setLocalUri(taskInfo.getApkLocalUri());
                appInfo.setApkSize(taskInfo.getApkTotalSize());
                appInfo.setApkDownloadId(taskInfo.getTaskId());
                int reason = taskInfo.getReason();
                DownloadHelper.handleMsgForPauseOrError(mParentActivity, appInfo.getAppName(), taskInfo.getDownloadState(), reason);
                break;
            }
        }
    }

    private class ActionModeHelper implements MultiSelectionUtil.MultiChoiceModeListener {
        private boolean allCheckMode;
        private View mMultiSelectActionBarView;

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(position);
            if (allCheckMode) {

            } else {
                appInfo.setChecked(checked);
            }
            downloadingListAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (mMultiSelectActionBarView == null) {
                mMultiSelectActionBarView = LayoutInflater.from(
                        FreeStoreApp.getContext()).inflate(
                        R.layout.list_multi_select_actionbar, null);
            }
            mode.setCustomView(mMultiSelectActionBarView);
            View view = (View) mMultiSelectActionBarView.getParent();
            if (view != null) {
                view.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
            }
            ((TextView) mMultiSelectActionBarView.findViewById(R.id.title))
                    .setText(getString(R.string.title_activity_down_load_manage));
            downloadingListAdapter.setActionMode(true);
            allCheckMode = false;
            actionMode = mode;
            MenuInflater inflater = mParentActivity.getMenuInflater();
            inflater.inflate(R.menu.multi_select_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    promptForDeleteTask();
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            allCheckMode = false;
            downloadingListAdapter.setActionMode(false);
            downloadingListAdapter.clearCheckStatus();
            downloadingListAdapter.notifyDataSetChanged();
        }
    }


    private void promptForDeleteTask() {
        final Dialog dialog = DialogUtils.deleteDownLoadTaskDialog(mParentActivity, new DeleteDownloadTaskDialogListener() {
            @Override
            public void onDeleteDownloadTask() {
                removeTasks();
            }
        });
        dialog.show();
    }

    private void removeTasks() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            final Dialog dialog = new Dialog(mParentActivity, R.style.Dialog);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setContentView(R.layout.dialog_progress);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                for (int index = downloadingList.size() - 1; index >= 0; index--) {
                    final AppInfo appInfo = downloadingList.get(index);
                    if (appInfo != null && appInfo.isChecked()) {
                        if (downloadingList.remove(index) != null) {

                            DownloadManager manager = DownloadHelper.getDownloadManager(mParentActivity);
                            if (manager.remove(appInfo.getApkDownloadId()) == 1) {
                                // delete apk
                                PackageInfoUtil.deleteApkFromDiskByUri(appInfo.getLocalUri());
                                // delete patch apk
                                if (!TextUtils.isEmpty(appInfo.getDownloadHint())) {
                                    PackageInfoUtil.deleteApkFromDiskByUri(Uri.parse(appInfo.getDownloadHint()).toString());
                                }

                                // cancel notification
                                if (systemFacade != null)
                                    systemFacade.cancelNotification(appInfo.getApkDownloadId());
                                MainThreadBus.getInstance().post(new DownloadTaskRemoveEvent(appInfo.getPkgName()));
                            }
                        }
                    }
                }


                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (result) {
                    actionMode.finish();
                    downloadingListAdapter.refreshData(downloadingList);
                    if (downloadingList.size() == 0) {
                        showEmptyDownloadMsg();
                        showEmpty();
                    }
                }

                MainThreadBus.getInstance().post(new DownloadManageChangeEvent());
            }
        };
        task.execute();
    }

}
