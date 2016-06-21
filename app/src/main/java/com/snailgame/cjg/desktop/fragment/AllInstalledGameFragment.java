package com.snailgame.cjg.desktop.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.snailgame.cjg.R;
import com.snailgame.cjg.desktop.adapter.GameListAdapter;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.cjg.event.DeskGameAddEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 已安装列表
 * Created by sunxy on 2015/4/10.
 */
public class AllInstalledGameFragment extends BaseDeskFragment {
    private static final String KEY_DESK_GAME = "key_desk_game";
    private QueryInstallTask task;
    private ArrayList<InstallGameInfo> deskGameLists;
    private ArrayList<InstallGameInfo> selectGameLists;

    public static AllInstalledGameFragment getInstance(ArrayList<InstallGameInfo> deskGameLists) {
        AllInstalledGameFragment fragment = new AllInstalledGameFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_DESK_GAME, deskGameLists);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectGameLists = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleArguments();
        showDeskGames();
        showToolbar();
    }

    private void showToolbar() {
        title.setText(getString(R.string.add_game_title));
        ComUtil.setDrawableLeft(title, R.drawable.ic_back_normal);
        ic_search.setVisibility(View.GONE);
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            deskGameLists = bundle.getParcelableArrayList(KEY_DESK_GAME);
        }
    }

    View.OnClickListener itemClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InstallGameInfo installGameInfo = (InstallGameInfo) v.getTag();
            //添加游戏
            if (installGameInfo != null) {
                if (installGameInfo.isSelected()) {
                    installGameInfo.setSelected(false);
                    selectGameLists.remove(installGameInfo);
                } else {
                    installGameInfo.setSelected(true);
                    selectGameLists.add(installGameInfo);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };
    private void showDeskGames() {
        mEmptyView.showLoading();
        if (task != null && !task.isCancelled())
            task.cancel(true);
        task = new QueryInstallTask();
        task.execute();
    }

    class QueryInstallTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            return getAllInstalledGames();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (getActivity() == null) return;
            if (ListUtils.isEmpty(gameLists)) {
                mEmptyView.showEmpty();
                mEmptyView.getEmptyView().setClickable(true);
            }else {
                adapter = new GameListAdapter(getActivity(), gameLists, AppConstants.ALL_INSTALLED_GAME,itemClickListener,null);
                gridView.setAdapter(adapter);
                bottomLayout.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 读取已安装的应用列表
     *
     * @return
     */
    private boolean getAllInstalledGames() {
        if (getActivity() != null) {
            PackageManager packageManager = getActivity().getPackageManager();
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            int length = packageInfos.size();
            for (int i = 0; i < length; i++) {
                if (getActivity() == null) return false;
                PackageInfo packageInfo = packageInfos.get(i);
                InstallGameInfo tmpInfo = new InstallGameInfo();
                tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                tmpInfo.setPackageName(packageInfo.packageName);
                tmpInfo.setVersionName(packageInfo.versionName);
                tmpInfo.setVersionCode(packageInfo.versionCode);
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                if (icon instanceof BitmapDrawable) {
                    tmpInfo.setAppIcon(((BitmapDrawable) icon).getBitmap());
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !exist(packageInfo.packageName)) {
                        gameLists.add(tmpInfo);
                    }
                }
            }

            return true;
        }
        return false;
    }

    /**
     * 判断应用是否已经存在我的游戏列表中
     *
     * @param packageName
     * @return
     */
    private boolean exist(String packageName) {

        int length = deskGameLists.size();
        for (int i = 0; i < length; i++) {
            InstallGameInfo installGameInfo = deskGameLists.get(i);
            if (installGameInfo.getPackageName().equals(packageName))
                return true;
        }
        return false;
    }

    @OnClick(R.id.title)
    public void back() {
        cancle();
    }

    @OnClick(R.id.cancle_add)
    public void cancle() {
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.add_game)
    public void add() {
        if (!ListUtils.isEmpty(selectGameLists))
            MainThreadBus.getInstance().post(new DeskGameAddEvent(selectGameLists));
        cancle();
    }
}
