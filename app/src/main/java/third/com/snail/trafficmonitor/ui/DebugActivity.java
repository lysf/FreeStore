package third.com.snail.trafficmonitor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.snailgame.cjg.R;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.manager.TimeTickAlarmManager;
import third.com.snail.trafficmonitor.engine.service.FirewallWorker;
import third.com.snail.trafficmonitor.engine.service.TrafficMonitor;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.OperatorFlags;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * Created by kevin on 15/1/4.
 */
public class DebugActivity extends Activity {
    private final String TAG = DebugActivity.class.getSimpleName();

    @Bind(R.id.wifi_iface)
    TextView wifi;
    @Bind(R.id.mobile_iface)
    TextView mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engine_debug_layout);
        ButterKnife.bind(this);
        wifi.setText(SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_WIFI_IFACE));
        mobile.setText(SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_MOBILE_IFACE));
    }

    @OnClick(R.id.action_debug_database)
    void dataBaseClick() {
        StringBuilder sb = new StringBuilder();
        CommandHelper.runCmd("cp /data/data/com.snailgame.cjg/databases/traffic.db /sdcard/", sb);
        LogWrapper.e(TAG, "Debug database: " + sb.toString());
        makeToast("DONE");
    }

    @OnClick(R.id.action_debug_timetick)
    void timeTickClick() {
        Intent intent = new Intent(TimeTickAlarmManager.ACTION);
        sendBroadcast(intent);
        makeToast("DONE");
    }

    @OnClick(R.id.action_debug_refresh)
    void refreshClick() {
        Intent intent = new Intent(DebugActivity.this, TrafficMonitor.class);
        intent.setFlags(OperatorFlags.Debug);
        intent.setAction(TrafficMonitor.ACTION_PICK_POINT);
        startService(intent);
        makeToast("DONE");
    }

    @OnClick(R.id.action_debug_reset_chain)
    void refreshChainClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DebugActivity.this);
        builder.setTitle("WARNING!")
                .setMessage("Will flush all traffic control configurations in apps database.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                List<App> apps = null;
                                AppDao appDao = null;
                                try {
                                    appDao = new AppDao(DebugActivity.this);
                                    apps = appDao.queryForAll();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if (apps == null) {
                                    return null;
                                }
                                for (App a : apps) {
                                    a.setMobileAccess(true);
                                    a.setWifiAccess(true);
//                                    appTool.update(a);
                                    try {
                                        appDao.update(a);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                makeToast("Reset DONE");
                                FirewallWorker.handleAll(DebugActivity.this);
                            }
                        }.execute();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @OnClick(R.id.action_debug_handle_all_control)
    void controlClick() {
        FirewallWorker.handleAll(DebugActivity.this);
        makeToast("DONE");
    }

    private void makeToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
