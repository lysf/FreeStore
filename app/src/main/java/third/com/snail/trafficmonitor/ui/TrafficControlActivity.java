package third.com.snail.trafficmonitor.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.snailgame.cjg.R;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.AppWrapper;
import third.com.snail.trafficmonitor.engine.service.FirewallWorker;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;
import third.com.snail.trafficmonitor.ui.adapter.NetworkControlAdapter;
import third.com.snail.trafficmonitor.ui.helper.ActionBarHelper;
import third.com.snail.trafficmonitor.ui.loader.AppWrapperDataLoader;
import third.com.snail.trafficmonitor.ui.widget.LoadingRecyclerView;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by kevin on 14/11/20.
 */
public class TrafficControlActivity extends SwipeBackActivity implements LoaderManager.LoaderCallbacks<List<AppWrapper>> {
    private final String TAG = TrafficControlActivity.class.getSimpleName();

    private LoadingRecyclerView mRecyclerView;
    private NetworkControlAdapter mAdapter;

    private List<AppWrapper> mAllAppWrapper = null;

    private boolean canUse = true;

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
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NetworkControlAdapter(this, null);
        mRecyclerView = new LoadingRecyclerView(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setBackgroundColor(Color.parseColor("#ffffffff"));
        mRecyclerView.setAdapter(mAdapter);
        setContentView(mRecyclerView);
        mRecyclerView.showLoading();

        ActionBarHelper.makeCommonActionBar(this, getString(R.string.netowrk_control_title));
        if (!CommandHelper.hasRoot()) {
            canUse = false;
            mRecyclerView.showHint(getString(R.string.dont_have_root));
        }
        if (!CommandHelper.hasIptable()) {
            canUse = false;
            mRecyclerView.showHint(getString(R.string.dont_have_iptable));
        }
        if (canUse) {
            mRecyclerView.showHint(getString(R.string.traffic_control_hint));
        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    // 网络控制界面的搜索功能
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.engine_control_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogWrapper.e(TAG, newText);
                if (newText == null || TextUtils.isEmpty(newText)) {
                    mAdapter.updateData(mAllApp);
                    return true;
                }
                List<App> queryResult = new ArrayList<>();
                for (App a : mAllApp) {
                    if (a.getAppName().toLowerCase().contains(newText.toLowerCase())) {
                        queryResult.add(a);
                    }
                }
                mAdapter.updateData(queryResult);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public Loader<List<AppWrapper>> onCreateLoader(int id, Bundle args) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //获取当前月的所有应用消耗流量
        return new AppWrapperDataLoader(this, c.get(Calendar.YEAR), c.get(Calendar.MONTH));
    }

    @Override
    public void onLoadFinished(Loader<List<AppWrapper>> loader, List<AppWrapper> data) {
        if (mRecyclerView.isLoading()) {
            mRecyclerView.finishLoading();
        }
        if (!canUse) {
            mAdapter.disableController(true, true);
        }
        ((NetworkControlAdapter) mRecyclerView.getAdapter()).updateData(data);
        if (mAllAppWrapper == null) {
            mAllAppWrapper = data;
        }
        mAdapter.setOnControlChangeListener(controlChangeListener);
    }

    @Override
    public void onLoaderReset(Loader<List<AppWrapper>> loader) {
    }

    private NetworkControlAdapter.OnControlChangeListener controlChangeListener = new NetworkControlAdapter.OnControlChangeListener() {
        @Override
        public void onWifiChanged(NetworkControlAdapter.ViewHolder holder, boolean checked) throws SQLException {
            onControlChanged(true, checked, holder);
        }

        @Override
        public void onMobileChanged(NetworkControlAdapter.ViewHolder holder, boolean checked) throws SQLException {
            onControlChanged(false, checked, holder);
        }
    };

    private void onControlChanged(final boolean wifi, final boolean checked, final NetworkControlAdapter.ViewHolder holder) throws SQLException {
        final int position = holder.getPosition();
        final AppWrapper app = mAdapter.getItem(position);
        if (app == null) return;
        if (wifi) {
            if (app.isWifiAccess() == checked) return;
        } else {
            if (app.isMobileAccess() == checked) return;
        }
        //更新App数据表
        mAdapter.updateSingleData(position, holder.getView().isWifiChecked(), holder.getView().isMobileChecked());
        FirewallWorker.handleOne(this, app.getUid());
    }

}
