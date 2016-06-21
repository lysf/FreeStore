package com.snailgame.cjg.downloadmanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.daoHelper.MyGameDaoHelper;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.downloadmanager.adapter.UpdateAppAdapter;
import com.snailgame.cjg.downloadmanager.util.GameManageUtil;
import com.snailgame.cjg.event.MyGameDBChangeEvent;
import com.snailgame.cjg.event.UpdateChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.DownloadConfirm.IConfirmResult;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-11
 * Time : 下午4:03
 * Description :用户手机需要更新的游戏fragment
 */
public class UpdateFragment extends AbsBaseFragment
        implements MyGameDaoHelper.MyGameCallback, AdapterView.OnItemClickListener {
    private static String TAG = UpdateFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView loadMoreListView;
    public UpdateAppAdapter mUpgradableAppAdapter;

    private List<AppInfo> appInfos = new ArrayList<AppInfo>();
    private TextView headTextView;
    private TextView footerTextView;
    private View footerView;
    @Bind(R.id.onekey_update)
    TextView OneKeyUpdate;
    @Bind(R.id.update_bottom_bar)
    View mBottomContainer;

    ArrayList<AppInfo> upgradeIgnoreList;
    private AsyncTask queryTask,updateChangeTask;


    @Subscribe
    public void onMyGameDbChanged(MyGameDBChangeEvent event) {
        if(isAdded())
            initUI(event.getAppInfoList());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        View headView = getActivity().getLayoutInflater().inflate(R.layout.update_head, null);
        this.headTextView = (TextView) headView.findViewById(R.id.updateTitle);
        mUpgradableAppAdapter = new UpdateAppAdapter(mParentActivity, appInfos, false);
        footerView = getActivity().getLayoutInflater().inflate(R.layout.update_footer, null);
        footerTextView = (TextView) footerView.findViewById(R.id.tv_ignore_footer);
        loadMoreListView.addHeaderView(headView);
        loadMoreListView.addFooterView(footerView);
        loadMoreListView.setAdapter(mUpgradableAppAdapter);
        loadMoreListView.setOnItemClickListener(this);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentActivity.startActivity(UpgradeIgnoreActivity.newIntent(mParentActivity, upgradeIgnoreList));
            }
        });
    }

    @Override
    public void loadData() {
        //showLoading();
        queryTask=MyGameDaoHelper.queryForAppInfoInThread(getActivity(), this);
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
        loadData();
    }

    protected void showEmptyUpdateMsg() {
        getEmptyView().setEmptyMessage(mParentActivity.getString(R.string.empty_update_msg));
    }


    @Override
    public void onDestroyView() {
        if(queryTask!=null)
            queryTask.cancel(true);
        if(updateChangeTask!=null)
            updateChangeTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void queryCallback(UpdateChangeEvent updateChangeEvent) {
        updateChangeTask=MyGameDaoHelper.queryForAppInfoInThread(getActivity(), UpdateFragment.this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo appInfo = (AppInfo) loadMoreListView.getItemAtPosition(position);
        if (appInfo == null) return;
        mParentActivity.startActivity(DetailActivity.newIntent(mParentActivity, appInfo.getAppId(), GameManageActivity.createRoute()));
    }

    @Override
    public void Callback(List<AppInfo> appInfos) {
        if(isAdded())
            initUI(appInfos);
    }

    private void initUI(List<AppInfo> appInfos) {
        List<AppInfo> infoList = GameManageUtil.getUpdateInfos(FreeStoreApp.getContext(), appInfos, false);
        mUpgradableAppAdapter.refreshData(infoList);
        refreshHead(infoList);
        if (infoList.size() == 0) {
            mBottomContainer.setVisibility(View.GONE);
        } else {
            mBottomContainer.setVisibility(View.VISIBLE);
        }

        upgradeIgnoreList = GameManageUtil.getUpgradeIgnoreList(FreeStoreApp.getContext(), appInfos);
        if (upgradeIgnoreList != null && upgradeIgnoreList.size() != 0) {
            footerTextView.setVisibility(View.VISIBLE);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

            String headerText = getString(R.string.upgrade_show_ignore);
            String contentText = " ( " + upgradeIgnoreList.size() + " )";

            stringBuilder.append(headerText + contentText);
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.green)),
                    headerText.length(), headerText.length() + contentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            footerTextView.setText(stringBuilder);
            footerView.setVisibility(View.VISIBLE);
        } else {
            footerView.setVisibility(View.GONE);
        }

        if (infoList.size() == 0 && upgradeIgnoreList.size() == 0) {
            showEmptyUpdateMsg();
            showEmpty();
        }
    }

    private void refreshHead(List<AppInfo> infoList) {
        long dataVolume = 0;
        long patchVolumeData = 0;
        for (AppInfo appInfo : infoList) {
            dataVolume += appInfo.getApkSize();
            patchVolumeData += appInfo.getIsPatch() == AppConstants.UPDATE_TYPE_PATCH ?
                    appInfo.getDiffSize() : appInfo.getApkSize();
        }
        headTextView.setText(GameManageUtil.getTextContent(dataVolume, patchVolumeData));
        if (IdentityHelper.isLogined(FreeStoreApp.getContext()) && AccountUtil.isFree()) {
            OneKeyUpdate.setText(mContent.getResources().getString(R.string.update_one_key_free));
        } else {
            OneKeyUpdate.setText(mContent.getResources().getString(R.string.update_one_key));
        }
    }

    @OnClick(R.id.onekey_update)
    public void onKeyUpdate() {
        final List<AppInfo> appInfoList = GameManageUtil.getUpdateInfos(getActivity(), mUpgradableAppAdapter.getUpdateList(), false);
        DownloadConfirm.showDownloadConfirmDialog(mParentActivity, new IConfirmResult() {
            @Override
            public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                if (appInfoList != null) {
                    for (AppInfo appInfo : appInfoList) {
                        if (!isUseFlowDownLoad) {
                            appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                        }
                        DownloadHelper.startDownload(mParentActivity, appInfo);
                    }
                }
                MainThreadBus.getInstance().post(new UpdateChangeEvent());
            }

            @Override
            public void doDismissDialog(boolean isDialogDismiss) {

            }
        });
    }


    public void showUpgradeIngradeList() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.base_game_manager_fragment;
    }

}