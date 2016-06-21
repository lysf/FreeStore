package com.snailgame.cjg.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.downloadmanager.adapter.UpdateAppAdapter;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;

import java.util.ArrayList;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 忽略升级 Acitivty
 * Created by TAJ_C on 2015/3/18.
 */
public class UpgradeIgnoreActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;

    private UpdateAppAdapter mAdapter;

    private ArrayList<AppInfo> upgradeIgnoreList;
    private static final String KEY_UPGRADE_IGNORE_DATA = "key_upgrade_ignore_data";

    public static Intent newIntent(Context context, ArrayList<AppInfo> upgradeIgnoreList) {
        Intent intent = new Intent(context, UpgradeIgnoreActivity.class);
        intent.putParcelableArrayListExtra(KEY_UPGRADE_IGNORE_DATA, upgradeIgnoreList);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.load_more_listview_gap_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.ab_upgrade_ignore_title);

        upgradeIgnoreList = getIntent().getParcelableArrayListExtra(KEY_UPGRADE_IGNORE_DATA);
        mAdapter = new UpdateAppAdapter(this, upgradeIgnoreList, true);
        loadMoreListView.setPadding(0, ComUtil.dpToPx(8), 0, 0);
        loadMoreListView.setAdapter(mAdapter);
        loadMoreListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(position);
        if (appInfo != null) {
            startActivity(DetailActivity.newIntent(this, appInfo.getAppId(), createRoute()));
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 游戏管理
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_MANAGE,
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
}
