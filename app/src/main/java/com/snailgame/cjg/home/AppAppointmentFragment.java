package com.snailgame.cjg.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.BaseDataModel;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.model.TaskInfo;
import com.snailgame.cjg.event.AppAppointmentEvent;
import com.snailgame.cjg.event.DownloadInfoChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.AppAppointModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.AppInfoUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.DialogUtils;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;


/**
 * 预约
 * Created by TAJ_C on 2016/1/28.
 */
public class AppAppointmentFragment extends AbsBaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = AppAppointmentFragment.class.getSimpleName();

    @Bind(R.id.content)
    LoadMoreListView appListView;

    CommonListItemAdapter adapter;

    private QueryTaskListTask task;

    protected ArrayList<AppInfo> appInfoLists = new ArrayList<>();

    public static AppAppointmentFragment getIntent() {
        AppAppointmentFragment fragment = new AppAppointmentFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_more_listview_gapless_container;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        mRoute = createRoute();
        adapter = new CommonListItemAdapter(getActivity(), appInfoLists, AppConstants.FRAGMENT_APP_APPOINTMENT, mRoute);
        appListView.setAdapter(adapter);
        appListView.setOnItemClickListener(this);
        isLoadinUserVisibile = false;
    }


    @Override
    protected void loadData() {
        showLoading();
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_APP_APPOINTMENT, TAG, AppAppointModel.class, new IFDResponse<AppAppointModel>() {
            @Override
            public void onSuccess(AppAppointModel result) {
                if (result != null && result.getAppList() != null) {
                    synchronized (appInfoLists) {

                        for (AppAppointModel.ModelItem item : result.getAppList()) {
                            AppInfo appInfo = new AppInfo(item);
                            appInfo.setOriginCFlowFree(appInfo.getcFlowFree());
                            appInfo.setAppointmentStatus(item.getAppointment());
                            appInfo.setHasAppointment(item.isHasAppointment());
                            appInfo.setTestingStatus(item.getTestingStatus());
                            appInfo.setDelTestTime(item.getDelTestTime());
                            appInfoLists.add(appInfo);
                        }

                        AppInfoUtils.updateDownloadState(getActivity(), appInfoLists);

                        AppInfoUtils.updatePatchInfo(getActivity(), appInfoLists);
                        if (AccountUtil.isAgentFree(getActivity())) {
                            AppInfoUtils.updateAppFreeState(getActivity(), AppInfoUtils.getAllGameIds(appInfoLists), adapter, TAG);
                        } else if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    showEmpty();
                }

            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, false, true, new ExtendJsonUtil());
    }


    @Override
    public void onResume() {
        super.onResume();
        queryDb();
    }

    @Override
    protected LoadMoreListView getListView() {
        return appListView;
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void saveData(Bundle outState) {

    }

    private void queryDb() {
        if (task != null)
            task.cancel(true);
        task = new QueryTaskListTask();
        task.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
    }


    class QueryTaskListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (ListUtils.isEmpty(appInfoLists)) {
                return false;
            }

            synchronized (appInfoLists) {
                AppInfoUtils.updateDownloadState(mParentActivity, appInfoLists);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result && adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Subscribe
    public void appointmentApp(final AppAppointmentEvent event) {
        String parmas = "iId=" + event.getAppId();

        FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().JSON_URL_APP_APPOINTMENT, TAG, BaseDataModel.class, new IFDResponse<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel result) {

                for (AppInfo appInfo : appInfoLists) {
                    if (appInfo.getAppId() == event.getAppId()) {
                        appInfo.setHasAppointment(true);
                        adapter.refreshData(appInfoLists);
                        break;
                    }

                }
                DialogUtils.showOrderSuccessDialog(getActivity());
            }

            @Override
            public void onNetWorkError() {
                ToastUtils.showMsg(getActivity(), getString(R.string.order_fail));
            }

            @Override
            public void onServerError() {
                ToastUtils.showMsg(getActivity(), getString(R.string.order_fail));
            }
        }, parmas);
    }

    @Subscribe
    public void onDownloadInfoChange(DownloadInfoChangeEvent event) {
        ArrayList<TaskInfo> taskInfos = event.getTaskInfos(false);
        if (appListView != null && taskInfos != null) {
            synchronized (appInfoLists) {
                for (TaskInfo taskInfo : taskInfos) {
                    ComUtil.updateProgress(getActivity(), taskInfo, appInfoLists);
                }
            }

            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 首页
        int[] route = new int[]{
                AppConstants.STATISTCS_THIRD_SHORTCUT,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = adapter.getItem(position);
        if (appInfo != null) {
            getActivity().startActivity(DetailActivity.newIntent(getActivity(), appInfo.getAppId(), appInfo.getiFreeArea(), mRoute));
        }
    }
}
