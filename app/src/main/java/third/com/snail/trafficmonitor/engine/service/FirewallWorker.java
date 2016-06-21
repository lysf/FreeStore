package third.com.snail.trafficmonitor.engine.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.firewall.IptableCommand;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

import static third.com.snail.trafficmonitor.engine.util.LogWrapper.makeTag;

/**
 * Created by kevin on 15/1/6.
 * <p/>
 * 处理Iptables命令相关事宜
 */
public class FirewallWorker extends IntentService {
    private final String TAG = makeTag(FirewallWorker.class);

    public static final String ACTION_FIRE_OFF_ALL = "action_fire_off_all";
    public static final String ACTION_FIRE_OFF_ONE = "action_fire_off_one";
    public static final String EXTRA_UID = "extra_uid";

    private PowerManager.WakeLock mProcessLock;
    private final int MAX_CLEAN = 10; //最多清除10次

    /**
     * 刷新指定UID的应用联网权限控制
     *
     * @param context Context
     * @param uid     应用UID
     */
    public static void handleOne(Context context, int uid) {
        Intent intent = new Intent(context, FirewallWorker.class);
        intent.setAction(ACTION_FIRE_OFF_ONE);
        intent.putExtra(EXTRA_UID, uid);
        context.startService(intent);
    }

    /**
     * 刷新所有应用联网权限控制
     *
     * @param context Context
     */
    public static void handleAll(Context context) {
        Intent intent = new Intent(context, FirewallWorker.class);
        intent.setAction(ACTION_FIRE_OFF_ALL);
        context.startService(intent);
    }

    public FirewallWorker() {
        super("FirewallWorker");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        mProcessLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        int uid;
        if (action.equals(ACTION_FIRE_OFF_ONE)) {
            uid = intent.getIntExtra(EXTRA_UID, -1);
            if (uid == -1) return;
            try {
                handleOneLocked(uid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (action.equals(ACTION_FIRE_OFF_ALL)) {
            try {
                handleAllLocked();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOneLocked(int uid) throws SQLException {
        mProcessLock.acquire();
        List<App> apps = new AppDao(this).query(App.COLUMN_UID, uid);
        if (apps == null || apps.size() <= 0) return;
        App app = apps.get(0);
        String mIface = SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_MOBILE_IFACE);
        String wIface = SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_WIFI_IFACE);
        ArrayList<String> cmds = new ArrayList<>();
        String cleanMobile = "";
        String cleanWifi = "";
        if (mIface != null && !mIface.equals(SpManager.SpData.INVALID_STRING)) {
            IptableCommand clean = new IptableCommand.Builder(IptableCommand.DEL).iface(mIface).uid(app.getUid()).build();
            if (clean != null) {
                cleanMobile = clean.getCmd();
            }
            IptableCommand.Builder builder = new IptableCommand.Builder(app.isMobileAccess() ? IptableCommand.DEL : IptableCommand.ADD);
            IptableCommand cmd = builder.iface(mIface).uid(app.getUid()).build();
            if (cmd != null) {
                cmds.add(cmd.getCmd());
            }
        }
        if (wIface != null && !wIface.equals(SpManager.SpData.INVALID_STRING)) {
            IptableCommand clean = new IptableCommand.Builder(IptableCommand.DEL).iface(wIface).uid(app.getUid()).build();
            if (clean != null) {
                cleanWifi = clean.getCmd();
            }
            IptableCommand.Builder builder = new IptableCommand.Builder(app.isWifiAccess() ? IptableCommand.DEL : IptableCommand.ADD);
            IptableCommand cmd = builder.iface(wIface).uid(app.getUid()).build();
            if (cmd != null) {
                cmds.add(cmd.getCmd());
            }
        }
        String[] cmdArray = new String[cmds.size()];
        StringBuilder sb = new StringBuilder();
        // clean firstly
        cleanChain(cleanMobile);
        cleanChain(cleanWifi);

        CommandHelper.runCmdsAsRoot(sb, cmds.toArray(cmdArray));
        LogWrapper.d(TAG, "handleOneLocked: " + sb.toString());
        mProcessLock.release();
    }

    private void handleAllLocked() throws SQLException {
        mProcessLock.acquire();
        List<App> apps = new AppDao(this).queryForAll();
        if (apps == null || apps.size() <= 0) return;
        ArrayList<String> cmds = new ArrayList<>();
        // Flush all firstly
        cmds.add("iptables -F");
        String mIface = SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_MOBILE_IFACE);
        String wIface = SpManager.getInstance(this).getString(SpManager.SpData.DEFAULT_WIFI_IFACE);
        for (App a : apps) {
            if (a.getUid() == -1) continue;
            if (mIface != null && !mIface.equals(SpManager.SpData.INVALID_STRING)) {
                IptableCommand.Builder builder = new IptableCommand.Builder(a.isMobileAccess() ? IptableCommand.DEL : IptableCommand.ADD);
                IptableCommand cmd = builder.iface(mIface).uid(a.getUid()).build();
                if (cmd != null) {
                    cmds.add(cmd.getCmd());
                }
            }
            if (wIface != null && !wIface.equals(SpManager.SpData.INVALID_STRING)) {
                IptableCommand.Builder builder = new IptableCommand.Builder(a.isWifiAccess() ? IptableCommand.DEL : IptableCommand.ADD);
                IptableCommand cmd = builder.iface(wIface).uid(a.getUid()).build();
                if (cmd != null) {
                    cmds.add(cmd.getCmd());
                }
            }
        }
        String[] cmdArray = new String[cmds.size()];
        StringBuilder sb = new StringBuilder();
        CommandHelper.runCmdsAsRoot(sb, cmds.toArray(cmdArray));
        LogWrapper.d(TAG, "handleAllLocked: " + sb.toString());
        mProcessLock.release();
    }

    private void cleanChain(String cmd) {
        if (!TextUtils.isEmpty(cmd)) {
            int cleanCount = MAX_CLEAN;
            StringBuilder cleanResult = new StringBuilder();
            //循环清除Iptables Chain，如果清除时找不到相关规则，则会有 iptables: No chain/target/match by that name. 提示
            //若无提示，可能还存在有相关规则，应该尝试继续删除，最大清除数量为MAX_CLEAN
            while (cleanCount > 0 && TextUtils.isEmpty(cleanResult.toString())) {
                CommandHelper.runCmdAsRoot(cmd, cleanResult);
                cleanCount--;
            }
        }
    }

}
