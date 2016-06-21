package com.snailgame.cjg.common.widget;


import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.common.model.NecessaryAppInfo;
import com.snailgame.cjg.common.model.NecessaryAppInfoModel;
import com.snailgame.cjg.download.DownloadHelper;
import com.snailgame.cjg.download.core.Downloads;
import com.snailgame.cjg.util.DownloadConfirm;
import com.snailgame.cjg.util.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;


/**
 * Created by lic on 2015/6/1.
 * 装机必备的dialog
 */
public class NecessaryAppDialog extends BottomSheet {

    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.title)
    TextView titleTV;
    @Bind(R.id.title_tips)
    TextView titleTipsTV;
    @Bind(R.id.btn_ok)
    Button confirmBtn;
    private NecessaryAppInfoModel model;
    private NecessaryAppDialogAdapter necessaryAppDialogAdapter;
    private List<NecessaryAppInfo> infos;
    private int selectedAppNum;

    public NecessaryAppDialog(Context context) {
        super(context);
    }

    public NecessaryAppDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.necessary_app_dialog, null);
        setContentDialogView(mDialogView);
        if (model == null)
            return;
        if (model.getAlbum() != null
                && !TextUtils.isEmpty(model.getAlbum().getAlbumTitle())
                && !TextUtils.isEmpty(model.getAlbum().getAlbumSubTitle())) {
            titleTV.setText(model.getAlbum().getAlbumTitle());
            titleTipsTV.setText(Html.fromHtml(model.getAlbum().getAlbumSubTitle()));
        }

        if (model.getInfos() != null) {
            infos = model.getInfos();
            necessaryAppDialogAdapter = new NecessaryAppDialogAdapter(getContext(), infos);
            gridView.setAdapter(necessaryAppDialogAdapter);
        }
        infos = model.getInfos();
        necessaryAppDialogAdapter = new NecessaryAppDialogAdapter(getContext(), infos);
        gridView.setAdapter(necessaryAppDialogAdapter);
        selectedAppNum = model.getInfos().size();
        confirmBtn.setText(getContext().getString(R.string.one_key_download, String.valueOf(selectedAppNum)));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (infos.get(position).isSelected()) {
                    infos.get(position).setIsSelected(false);
                    selectedAppNum--;
                } else {
                    infos.get(position).setIsSelected(true);
                    selectedAppNum++;
                }
                confirmBtn.setText(getContext().getString(R.string.one_key_download, String.valueOf(selectedAppNum)));
                necessaryAppDialogAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public AbsListView getAbsListView() {
        return gridView;
    }

    @OnClick(R.id.btn_cancel)
    void cancle() {
        dismiss();
    }

    @OnClick(R.id.btn_ok)
    void confirm() {
        DownloadConfirm.showDownloadConfirmDialog(getContext(), new DownloadConfirm.IConfirmResult() {
            @Override
            public void doDownload(boolean isDialogResult, boolean isUseFlowDownLoad) {
                boolean isAppSelected = false;
                for (NecessaryAppInfo necessaryAppInfo : infos) {
                    if (necessaryAppInfo.isSelected()) {
                        if (!isAppSelected)
                            isAppSelected = true;
                        final AppInfo appInfo = new AppInfo();
                        appInfo.setApkUrl(necessaryAppInfo.getcDownloadUrl());
                        appInfo.setAppName(necessaryAppInfo.getsAppName());
                        if (!isUseFlowDownLoad) {
                            appInfo.setDownloadState(Downloads.STATUS_PENDING_FOR_WIFI);
                        }
                        appInfo.setPkgName(necessaryAppInfo.getcPackage());
                        appInfo.setIcon(necessaryAppInfo.getcIcon());
                        appInfo.setVersionCode(Integer.parseInt(necessaryAppInfo.getiVersionCode()));
                        appInfo.setAppId(necessaryAppInfo.getnAppId());
                        appInfo.setVersionName(necessaryAppInfo.getcVersionName());
                        appInfo.setMd5(necessaryAppInfo.getcMd5());
                        appInfo.setApkSize(necessaryAppInfo.getiSize());
                        DownloadHelper.startDownload(getContext(), appInfo);
                    }
                }
                if (isAppSelected) {
                    dismiss();
                } else {
                    ToastUtils.showMsg(getContext(), getContext().getString(R.string.please_select_at_least_one_app));
                }
            }

            @Override
            public void doDismissDialog(boolean isDialogDismiss) {

            }
        });
    }

    public void setData(NecessaryAppInfoModel model) {
        this.model = model;
    }

}
