package com.snailgame.cjg.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.download.core.DownloadReceiver;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.settings.AppAutoInstallSettingDialogActivity;
import com.snailgame.cjg.settings.AutoInstallAccessibilityService;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * Created by xiadi on 2014/4/16.
 */
public class ConfigAutoInstallActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private List<Intent> intentList = new ArrayList<Intent>(1);
    private boolean isRootAutoInstall = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_auto_install);
        mContext = this;

        intentList.add(getIntent());
        if (getIntent() != null) {
            isRootAutoInstall = getIntent().getBooleanExtra(AppConstants.KEY_IS_ROOT_AUTO_INSTALL, true);
        }

        configAutoInstall();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (false == isRootAutoInstall &&
                false == CommandHelper.hasRoot() &&
                AutoInstallAccessibilityService.isAccessibilitySettingsOn(this)) {
            ToastUtils.showMsg(mContext, getString(R.string.grant_superuser_access_succ));
            sendBroadcast();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) intentList.add(intent);
    }

    private void configAutoInstall() {
        TextView tvContent = (TextView) findViewById(R.id.header);
        tvContent.setText(R.string.dialog_superuser_access_title);

        tvContent = (TextView) findViewById(R.id.message);
        if (isRootAutoInstall) {

            tvContent.setText(getString(R.string.grant_superuser_access));
        } else {
            tvContent.setText(getString(R.string.grant_unroot_auto_install));
        }
        tvContent.setTextColor(ResUtil.getColor(R.color.primary_text_color));

        TextView sure = (TextView) findViewById(R.id.sure);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        cancel.setText(R.string.dialog_superuser_access_btn_cancel);
        sure.setText(R.string.dialog_superuser_access_btn_sure);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
        LinearLayout checkBoxArea = (LinearLayout) findViewById(R.id.checkbox_area);
        checkBoxArea.setVisibility(View.VISIBLE);
        checkBoxArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.sure:
                if (isRootAutoInstall) {
                    startConfigAutoInstall();
                } else {
                    Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(killIntent);

                    startActivity(AppAutoInstallSettingDialogActivity.newIntent(this));
                }
                break;
            case R.id.cancel:
                sendBroadcast();
                finish();
                break;
            case R.id.checkbox_area:
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.dialog_checkbox);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                if (checkBox instanceof CheckBox) {
                    SharedPreferencesUtil.getInstance().setAlertGrantRoot(!(checkBox).isChecked());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast();
    }

    private void sendBroadcast() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Intent i : intentList) {
                    if (i == null) continue;
                    i.setClass(mContext, DownloadReceiver.class);
                    i.setAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    sendBroadcast(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LogUtils.e(e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }

    private void startConfigAutoInstall() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params) {
                return CommandHelper.hasRoot();
            }

            @Override
            protected void onPostExecute(Boolean isAccessGiven) {
                super.onPostExecute(isAccessGiven);

                if (isAccessGiven) {
                    SharedPreferencesUtil.getInstance().setAutoInstall(true);
                    SharedPreferencesUtil.getInstance().setSuperuser(true);
                    ToastUtils.showMsg(mContext, getString(R.string.grant_superuser_access_succ));
                } else {
                    ToastUtils.showMsg(mContext, getString(R.string.grant_superuser_access_fail));
                }
                sendBroadcast();
                finish();
            }
        };
        task.execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Activity context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = context.getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }
}