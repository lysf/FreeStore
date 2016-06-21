package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.event.TaskMsgChangedEvent;
import com.snailgame.cjg.event.UserTaskRefreshEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.UserTaskAdapter;
import com.snailgame.cjg.personal.model.TaskModel;
import com.snailgame.cjg.personal.model.TaskReceiveModel;
import com.snailgame.cjg.personal.model.UserTaskModel;
import com.snailgame.cjg.search.AppSearchActivity;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户任务列表
 * Created by sunxy on 2015/2/2.
 */
public class UserTaskFragment extends AbsBaseFragment implements AdapterView.OnItemClickListener {
    private LoadMoreListView loadMoreListView;
    private UserTaskAdapter adapter;

    private int mTaskTab;
    private boolean isEntry = true;
    public static final int TYPE_DOWNLOAD = 10; //下载某应用
    public static final int TYPE_PERSOANL_DATA = 11; //完善资料
    public static final int TYPE_SEARCH = 12; //搜索

    public static final int TASK_STATE_ACCEPT = 0, //可接受(未完成)
            TASK_STATE_GOING = 1, //进行中(未完成)
            TASK_STATE_COMPLETE = 2, // 已完成(已领取)
            TASK_STATE_UNRECEIVE = 3, //已完成(未领取)ua
            TASK_STATE_DROP = 4, //已放弃
            TASK_STATE_UNACCEPT = 5;//不可接受

    public static final int TAB_TASK_ONCE = 0, TAB_TASK_WEEK = 1;

    public static UserTaskFragment newInstance(int tab) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_TASK_TAB, tab);
        UserTaskFragment fragment = new UserTaskFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void onDownloadInfoChange(UserTaskRefreshEvent event) {
        if (event == null)
            return;

        switch (event.getResult()) {
            case UserTaskRefreshEvent.RESULT_SUCCESS:
                loadUserTaskSuccess(event.getModel());
                break;
            case UserTaskRefreshEvent.RESULT_NETWORK_ERROR:
                showBindError();
                break;
            case UserTaskRefreshEvent.RESULT_ERROR:
                showBindError();
                break;
            default:
                break;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new UserTaskAdapter(getActivity(), null, AccountUtil.isMember());
        isLoadinUserVisibile = false;
        MainThreadBus.getInstance().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.task_fragment;
    }

    @Override
    protected void handleIntent() {
        if (getArguments() == null)
            mTaskTab = TAB_TASK_ONCE;
        else
            mTaskTab = getArguments().getInt(AppConstants.KEY_TASK_TAB, TAB_TASK_ONCE);
    }

    @Override
    protected void initView() {
        loadMoreListView = (LoadMoreListView) mContent.findViewById(R.id.task_list);
        View headerView = new View(getActivity());
        View footerView = new View(getActivity());
        headerView.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(8)));
        footerView.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(8)));
        loadMoreListView.addHeaderView(headerView);
        loadMoreListView.addFooterView(footerView);
        loadMoreListView.setAdapter(adapter);
        loadMoreListView.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getEmptyView().setErrorButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                ((UserTaskActivity) getActivity()).loadTaskJson(JsonUrl.getJsonUrl().USER_TASK_URL);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();
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

    private void showBindError() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showError();
                }
            });
        }
    }

    private void handleResult(UserTaskModel userTaskModel) {
        UserTaskModel.ModelItem taskItem = userTaskModel.getItem();
        if (taskItem != null) {
            List<TaskModel> taskList = mTaskTab == TAB_TASK_ONCE ? taskItem.getOneTaskList() : taskItem.getWeekTaskList();
            if (!ListUtils.isEmpty(taskList)) {
                int unCompleteNum = 0;
                for (TaskModel item : taskList) {
                    if (item != null && !TextUtils.isEmpty(item.getcConfig())) {
                        item.setConfigItem(JSON.parseObject(item.getcConfig(), TaskModel.ConfigItem.class));
                    }
                    if (item.getcGroupStatus() == TASK_STATE_ACCEPT ||
                            item.getcGroupStatus() == TASK_STATE_GOING ||
                            item.getcGroupStatus() == TASK_STATE_UNRECEIVE) {
                        unCompleteNum++;
                    }
                }

                MainThreadBus.getInstance().post(new TaskMsgChangedEvent(mTaskTab, unCompleteNum, isEntry));

                adapter.refreshData(taskList);
            } else {
                MainThreadBus.getInstance().post(new TaskMsgChangedEvent(mTaskTab, 0, isEntry));
                showEmpty();
            }

        } else {
            MainThreadBus.getInstance().post(new TaskMsgChangedEvent(mTaskTab, 0, isEntry));
            showEmpty();
        }
        isEntry = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 软件
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_MYTASK,
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

        final TaskModel item = (TaskModel) getListView().getItemAtPosition(position);
        if (item == null) {
            return;
        }

        int groupStatus = item.getcGroupStatus();
        if (groupStatus == TASK_STATE_ACCEPT || groupStatus == TASK_STATE_GOING) {

            if (item.getcConfig() != null) {
                TaskModel.ConfigItem configItem = JSON.parseObject(item.getcConfig(), TaskModel.ConfigItem.class);
                if (configItem != null) {
                    switch (configItem.getType()) {
                        case TYPE_DOWNLOAD:
                            List<TaskModel.configItemContent> list = configItem.getContent();
                            if (list != null && list.get(0) != null) {
                                startActivity(DetailActivity.newIntent(
                                        getActivity(), list.get(0).getAppId(), createRoute()));
                            }

                            break;
                        case TYPE_PERSOANL_DATA:
                            startActivity(AccountSafeActivity.newIntent(getActivity()));
                            break;
                        case TYPE_SEARCH:
                            startActivity(AppSearchActivity.newIntent(getActivity()));
                            break;
                        default:
                            break;
                    }
                }

            }
        } else if (groupStatus == TASK_STATE_UNRECEIVE) {
            Map<String, String> parame = new HashMap<>();
            parame.put("iTaskId", String.valueOf(item.getiGroupId()));

            FSRequestHelper.newPostRequest(JsonUrl.getJsonUrl().USER_TASK_RECEIVE, TAG, TaskReceiveModel.class, new IFDResponse<TaskReceiveModel>() {
                @Override
                public void onSuccess(TaskReceiveModel result) {
                    if (result.getCode() == 0 && result.isVal()) {
                        ToastUtils.showMsg(getActivity(), ResUtil.getString(R.string.personal_task_receive_success));
                        ((UserTaskActivity) getActivity()).loadTaskJson(JsonUrl.getJsonUrl().USER_TASK_URL);
                    } else {
                        ToastUtils.showMsg(getActivity(), ResUtil.getString(R.string.personal_task_receive_failed));
                    }
                }

                @Override
                public void onNetWorkError() {
                    ToastUtils.showMsg(getActivity(), ResUtil.getString(R.string.personal_task_receive_failed));
                }

                @Override
                public void onServerError() {
                    ToastUtils.showMsg(getActivity(), ResUtil.getString(R.string.personal_task_receive_failed));
                }
            }, parame);
        }

    }

    public void loadUserTaskSuccess(UserTaskModel model) {
        resetRefreshUi();
        if (model != null && model.getItem() != null) {
            handleResult(model);
        } else {
            showError();
        }
    }

}