package com.snailgame.cjg.downloadmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.downloadmanager.adapter.InstalledAppAdapter;
import com.snailgame.cjg.downloadmanager.util.MultiSelectionUtil;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-11
 * Time : 下午3:48
 * Description :用户已安装的游戏fragment
 */
public class InstalledFragment extends AbsBaseFragment implements MyGameDaoHelper.MyGameCallback, AdapterView.OnItemClickListener, GameManageActivity.IMultiSelectableFragment {
    private InstalledAppAdapter mInstalledAppAdapter;
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;

    public ActionMode actionMode;
    private AsyncTask queryTask;

    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        refreshData(event.getAppInfoList());
    }

    @Override
    public void Callback(List<AppInfo> appInfos) {
        refreshData(appInfos);
    }

    private void refreshData(List<AppInfo> appInfos) {
        List<AppInfo> appInfoList = ComUtil.getInstalledApk(appInfos, getActivity());
        if (mInstalledAppAdapter != null) {
            mInstalledAppAdapter.refreshData(appInfoList);
            mInstalledAppAdapter.setActionMode(false);
            finishActionMode();
        }
        if (appInfoList == null || appInfoList.size() == 0) {
            showEmptyInstalledMsg();
            showEmpty();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.base_game_manager_fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryTask = MyGameDaoHelper.queryForAppInfoInThread(getActivity(), this);
        mParentActivity = getActivity();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        ActionModeHelper actionModeHelper = new ActionModeHelper();
        MultiSelectionUtil.attachMultiSelectionController(
                loadMoreListView, (ActionBarActivity) getActivity(), actionModeHelper);

        View view = new View(getActivity());

        view.setLayoutParams(new AbsListView.LayoutParams(1, ComUtil.dpToPx(4)));
        loadMoreListView.addHeaderView(view);

        mInstalledAppAdapter = new InstalledAppAdapter(mParentActivity, null);
        loadMoreListView.setAdapter(mInstalledAppAdapter);
        loadMoreListView.setOnItemClickListener(this);

    }

    @Override
    public void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public boolean isActionMode() {
        return mInstalledAppAdapter != null && mInstalledAppAdapter.isActionMode();
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
    public void onDestroyView() {
        if (queryTask != null)
            queryTask.cancel(true);
        super.onDestroyView();
    }

    protected void showEmptyInstalledMsg() {
        getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.empty_installed_msg));
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(i);
        if (appInfo == null) return;
        mParentActivity.startActivity(DetailActivity.newIntent(mParentActivity, appInfo.getAppId(), GameManageActivity.createRoute()));
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
            mInstalledAppAdapter.notifyDataSetChanged();
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
            mInstalledAppAdapter.setActionMode(true);
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
                    mInstalledAppAdapter.uninstallApps();
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            allCheckMode = false;
            mInstalledAppAdapter.setActionMode(false);
            mInstalledAppAdapter.clearCheckStatus();
            mInstalledAppAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }
}
