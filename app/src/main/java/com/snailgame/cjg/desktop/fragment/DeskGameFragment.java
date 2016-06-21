package com.snailgame.cjg.desktop.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.DeskGame;
import com.snailgame.cjg.common.db.daoHelper.DeskGameDaoHelper;
import com.snailgame.cjg.desktop.adapter.GameListAdapter;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.cjg.event.DeskGameAddEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.search.AppSearchActivity;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;
import java.util.List;

import butterknife.OnClick;

/**
 * 我的游戏
 * Created by sunxy on 2015/4/10.
 */
public class DeskGameFragment extends BaseDeskFragment {
    private QueryDeskGameTask queryDeskGameTask;
    private boolean isEditEable = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDeskGames();
    }

    private void showDeskGames() {
        showLoading();
        if (queryDeskGameTask != null && !queryDeskGameTask.isCancelled())
            queryDeskGameTask.cancel(true);
        queryDeskGameTask = new QueryDeskGameTask();
        queryDeskGameTask.execute();
    }

    private void showLoading() {
        mEmptyView.showLoading();
    }

    @Subscribe
    public void deskGameAdded(DeskGameAddEvent event) throws SQLException {
        List<InstallGameInfo> selectedGameLists = event.getSelectGameLists();
        int length = selectedGameLists.size();
        InstallGameInfo installGameInfo;
        for (int i = 0; i < length; i++) {
            installGameInfo = selectedGameLists.get(i);
            installGameInfo.setSelected(false);
        }
        gameLists.addAll(0, selectedGameLists);
        adapter.notifyDataSetChanged();
        DeskGameDaoHelper.insertToDb(getActivity(), selectedGameLists);
    }


    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InstallGameInfo installGameInfo = (InstallGameInfo) v.getTag();
            if (installGameInfo != null) {
                if (isEditEable) {
                    gameLists.remove(installGameInfo);
                    adapter.notifyDataSetChanged();
                    try {
                       DeskGameDaoHelper.deleteFromDb(getActivity(), installGameInfo.getPackageName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (adapter.getCount() == 0) {
                        mEmptyView.showEmpty();
                        mEmptyView.getEmptyView().setClickable(true);
                    }
                } else {
                    if (TextUtils.isEmpty(installGameInfo.getPackageName())) {
                        if (!isEditEable) {
                            getFragmentManager().beginTransaction().add(R.id.myGameContainer, AllInstalledGameFragment.getInstance(gameLists)).addToBackStack(null).commitAllowingStateLoss();
                        }
                    } else {
                        try {
                            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(installGameInfo.getPackageName());
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    };

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!isEditEable && gameLists.size() > 1) {
                //全部显示删除按钮
                InstallGameInfo installGameInfo = (InstallGameInfo) v.getTag();
                if (installGameInfo != null && !TextUtils.isEmpty(installGameInfo.getPackageName())) {
                    startEditMode();
                }
            }
            return true;
        }
    };

    /**
     * 启动可编辑模式
     */
    private void startEditMode() {
        gridView.startEditMode();
        isEditEable = true;
        title.setText(getString(R.string.delete_mygame));
        ComUtil.setDrawableLeft(title, R.drawable.ic_back_normal);

        ic_search.setVisibility(View.GONE);
        gameLists.remove(gameLists.get(gameLists.size() - 1));
        int length = gameLists.size();
        for (int i = 0; i < length; i++) {
            InstallGameInfo installGameInfo = gameLists.get(i);
            installGameInfo.setSelected(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 取消可编辑模式
     */
    public void cancleEditMode() {
        isEditEable = false;
        title.setText(getString(R.string.mygame));
        ComUtil.setDrawableLeft(title, R.drawable.ic_desk_game_navigation_logo);
        ic_search.setVisibility(View.VISIBLE);
        int length = gameLists.size();
        for (int i = 0; i < length; i++) {
            InstallGameInfo installGameInfo = gameLists.get(i);
            installGameInfo.setSelected(false);
        }
        gameLists.add(new InstallGameInfo());
        adapter.notifyDataSetChanged();
        gridView.stopEditMode();
    }

    /**
     * 从我的游戏数据库读取数据TASK
     */
    class QueryDeskGameTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                getDeskGame();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (getActivity() == null) return;
            adapter = new GameListAdapter(getActivity(), gameLists, AppConstants.DESK_GAME, itemClickListener, longClickListener);
            gridView.setAdapter(adapter);
        }
    }

    /**
     * 从我的游戏数据库读取数据
     */
    private boolean getDeskGame() throws SQLException {
        if (getActivity() == null) return false;
        List<DeskGame> deskGameList = DeskGameDaoHelper.queryForAll(getActivity());
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo;

        for (DeskGame deskGame : deskGameList) {
            try {
                if (getActivity() != null) {
                    packageInfo = packageManager.getPackageInfo(deskGame.getPackageName(), 0);
                    InstallGameInfo tmpInfo = new InstallGameInfo();
                    tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                    tmpInfo.setPackageName(packageInfo.packageName);
                    tmpInfo.setVersionName(packageInfo.versionName);
                    tmpInfo.setVersionCode(packageInfo.versionCode);

                    BitmapDrawable bd = (BitmapDrawable) packageInfo.applicationInfo.loadIcon(packageManager);
                    tmpInfo.setAppIcon(bd.getBitmap());
                    gameLists.add(tmpInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        gameLists.add(new InstallGameInfo());
        return true;
    }

    /**
     * 是否处于可编辑状态
     *
     * @return
     */
    public boolean isEditEable() {
        return isEditEable;
    }


    @OnClick(R.id.mygame_search)
    public void search() {
        startActivity(AppSearchActivity.newIntent(getActivity()));
    }

    @OnClick(R.id.title)
    public void back() {
        if (isEditEable) {
            cancleEditMode();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queryDeskGameTask != null && !queryDeskGameTask.isCancelled())
            queryDeskGameTask.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }
}
