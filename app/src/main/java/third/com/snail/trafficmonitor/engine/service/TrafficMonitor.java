package third.com.snail.trafficmonitor.engine.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snailgame.fastdev.util.ListUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;

import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;
import third.com.snail.trafficmonitor.engine.data.bean.AppRecord;
import third.com.snail.trafficmonitor.engine.data.bean.AppRecordCell;
import third.com.snail.trafficmonitor.engine.data.bean.FileTrafficObject;
import third.com.snail.trafficmonitor.engine.data.bean.SystemInsideUID;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.AppDao;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.NetworkDao;
import third.com.snail.trafficmonitor.engine.data.table.Profile;
import third.com.snail.trafficmonitor.engine.data.table.ProfileDao;
import third.com.snail.trafficmonitor.engine.data.table.Record;
import third.com.snail.trafficmonitor.engine.data.table.RecordDao;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.data.table.TrafficDao;
import third.com.snail.trafficmonitor.engine.manager.SpManager;
import third.com.snail.trafficmonitor.engine.util.CheckEndTimeStampTools;
import third.com.snail.trafficmonitor.engine.util.CheckNetTypeTools;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.StorageUtils;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

public class TrafficMonitor extends Service {
    private final String TAG = TrafficMonitor.class.getSimpleName();

    /**
     * TODO
     *
     * 1. 使用startForeground使service不容易被System kill掉
     */

    /**
     * 应用第一次启动，初始化应用和网络数据库，只会运行一次
     */
    public static final String ACTION_INIT_ALL_DATABASE = "init_all_database";
    /**
     * 开机启动后的初始化，需要记录一下当前所有拥有网络权限的应用的流量值
     */
    public static final String ACTION_BOOT_INIT = "boot_init";
    /**
     * 设备关机，将record表中的数据全部清空
     */
    public static final String ACTION_DEVICE_SHUTDOWN = "device_shutdown";
    /**
     * 网络状态发生变化，对网络数据表进行更新，并进行打点测量
     */
    public static final String ACTION_NETWORK_CHANGED = "network_changed";
    /**
     * 新安装或更新App，对App数据表进行更新，并进行打点测量
     */
    public static final String ACTION_APP_ADD = "app_add";
    /**
     * 有卸载的App，对App数据表进行更新
     */
    public static final String ACTION_APP_REMOVE = "app_remove";
    /**
     * 打点记录
     */
    public static final String ACTION_PICK_POINT = "pick_point";

    /**
     * 整点记录
     */
    public static final String ACTION_TIME_TICK = "time_tick";

    // 缓存信息处理队列，保存信息为缓存文件在cache中的文件名，以时间戳命名。
    private BlockingQueue<Long> mAppRecordQueue = new PriorityBlockingQueue<Long>();

    public static final String BROADCAST_TRAFFIC_UPDATE = "com.snail.trafficmanager.service.TrafficMonitor.trafficUpdate";

    public static final int PULL_PERIOD = 4;

    private SpManager mSpManager;
    /**
     * 用以保持缓存数据的处理过程
     */
    private PowerManager.WakeLock mDispatcherWakelock;
    private final String DISPATCHER_WAKELOCK_TAG = "dispatcher";

    /**
     * 保证记录能够完整进行完成
     */
    private PowerManager.WakeLock mRecorderWakelock;
    private final String HANDLER_WAKELOCK_TAG = "handler";

    /**
     * 记录当前网络对象
     */
    private Network mCurrentNetwork;

    private TrafficDispatcher mDispatcher;
    private boolean mState = false;

    /**
     * 记录Mobile和WIFI的iface，如果产生变化则触发防火墙重新配置操作
     */
    private String mMobileIface;
    private String mWifiIface;

    private AppDao appDao;
    private ProfileDao profileDao;
    private RecordDao recordDao;
    private NetworkDao networkDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogWrapper.d(TAG, "onCreate");

        mSpManager = SpManager.getInstance(this);
        try {
            appDao = new AppDao(this);
            profileDao = new ProfileDao(this);
            recordDao = new RecordDao(this);
            networkDao = new NetworkDao(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mDispatcher != null) {
            mState = false;
            mDispatcher.interrupt();
            mDispatcher = null;
        }
        mDispatcher = new TrafficDispatcher(this, mAppRecordQueue);
        mState = true;
        mDispatcher.start();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mDispatcherWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, DISPATCHER_WAKELOCK_TAG);
        mRecorderWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, HANDLER_WAKELOCK_TAG);

        mMobileIface = mSpManager.getString(SpManager.SpData.DEFAULT_MOBILE_IFACE);
        mWifiIface = mSpManager.getString(SpManager.SpData.DEFAULT_WIFI_IFACE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogWrapper.d(TAG, "onDestroy");
        mState = false;
        if (mDispatcher != null) {
            mDispatcher.interrupt();
            mDispatcher = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            LogWrapper.d(TAG, "Action:" + intent.getAction() + " Flag:" + intent.getFlags());
            String action = intent.getAction();
            if (action.equals(ACTION_INIT_ALL_DATABASE)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initDatabase();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (action.equals(ACTION_BOOT_INIT)) {
                // 若未曾初始化过，不做任何操作
                if (mSpManager.getBoolean(SpManager.SpData.DATABASE_ININTED)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                record(null);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                // 如果有防火墙设置，则处理防火墙设置
                try {
                    tryHandleFirewallChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(ACTION_DEVICE_SHUTDOWN)) {
                pickPoint();
            } else if (action.equals(ACTION_NETWORK_CHANGED)) {
                final Network network = CheckNetTypeTools.checkNet(this);
                // 检测是否需要重新配置防火墙
                try {
                    tryHandleFirewallChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                boolean abort = false;
                if (mCurrentNetwork == null) {
                    mCurrentNetwork = network;
                } else {
                    if (mCurrentNetwork.equals(network)) {
                        abort = true;
                        LogWrapper.d(TAG, "this network has handled.");
                    }
                    mCurrentNetwork = network;
                }
                LogWrapper.e(TAG, "Current: " + mCurrentNetwork.toString());
                if (!abort) {
                    if (network.getNetworkType() != Network.NetworkType.NONE) { //有网络
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    onNetworkChanged(network);
                                    pickPoint();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        // 无网络后也要打点，内部会清除record数据表，意义上相当于停止记录
                        pickPoint();
                    }
                }
            } else if (action.equals(ACTION_APP_ADD) && intent.getExtras() != null) {
                Object args = intent.getExtras().get("data");
                if (args instanceof App) {
                    final App app = (App) args;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                onAppChanged(app, true);
                                pickPoint();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } else if (action.equals(ACTION_APP_REMOVE) && intent.getExtras() != null) {
                Object args = intent.getExtras().get("data");
                if (args instanceof App) {
                    final App app = (App) args;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                onAppChanged(app, false);
                                pickPoint();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } else if (intent.getAction().equals(ACTION_PICK_POINT)) {
                pickPoint();
            } else if (intent.getAction().equals(ACTION_TIME_TICK)) {
                pickPoint(true);
            }
        }
        return START_STICKY;
    }

    private void tryHandleFirewallChanged() throws SQLException {
        String wifi = mSpManager.getString(SpManager.SpData.DEFAULT_WIFI_IFACE);
        String mobile = mSpManager.getString(SpManager.SpData.DEFAULT_MOBILE_IFACE);

        if (!wifi.equals(mWifiIface) || !mobile.equals(mMobileIface)) {
            mWifiIface = wifi;
            mMobileIface = mobile;
            List<App> notAccessWifiApps = appDao.query(App.COLUMN_ACCESS_WIFI, false);
            List<App> notAccessMobileApps = appDao.query(App.COLUMN_ACCESS_MOBILE, false);
            if (notAccessMobileApps.size() > 0 && notAccessWifiApps.size() > 0) {
                FirewallWorker.handleAll(this);
            }
        }
    }

    /**
     * 打点记录
     */
    private void pickPoint(boolean timepick) {
        new TrafficRecorder(this, mAppRecordQueue, timepick)
                .start();
    }

    /**
     * 打点记录
     */
    private void pickPoint() {
        pickPoint(false);
    }

    /**
     * 初始化应用和网络数据库
     */
    private void initDatabase() throws SQLException {
        LogWrapper.e(TAG, "initDatabase");
        if (!mSpManager.getBoolean(SpManager.SpData.DATABASE_ININTED)) {
            initAppDatabase();
            initNetDataBase();
            record(null);
            mSpManager.putBoolean(SpManager.SpData.DATABASE_ININTED, true);
        }
    }

    /**
     * 用于记录上一次的流量记录，可能发生在第一次记录，也可能发生在每次解析完请求后
     * 包含数据库操作，不要在主线程
     *
     * @param appRecord 若是第一次记录则为null
     */
    private void record(AppRecord appRecord) throws SQLException {
        boolean hasData = appRecord != null;
        LogWrapper.d(TAG, "Record DATA:" + hasData);
        Network network = null;
        if (hasData) {
            List<Network> result = networkDao.query(Network.COLUMN_ID, appRecord.getNetworkId());
            if (result != null && result.size() > 0) {
                network = result.get(0);
            }
            if (network == null) {
                return;
            }
        } else {
            network = CheckNetTypeTools.checkNet(this);
            if (network.getNetworkType() == Network.NetworkType.NONE) {
                return;
            }
            network = findOrCreateNetwork(network);
        }

        List<App> apps = appDao.queryForAll();
        if (ListUtils.isEmpty(apps)) {
            //UNLIKELY
            return;
        }
        // CLEANUP

        recordDao.delete();
        final List<Record> results = new ArrayList<Record>();

        if (hasData) {
            List<AppRecordCell> list = appRecord.getDetails();
            for (AppRecordCell cell : list) {
                App a = null;
                List<App> queryResult = appDao.query(App.COLUMN_ID, cell.getAppId());
                if (queryResult != null && queryResult.size() > 0) {
                    a = queryResult.get(0);
                }
                if (a == null) {
                    //UNLIKELY
                    continue;
                }
                Record record = new Record();
                record.setApp(a);
                record.setAppId(a.getId());
                record.setNetwork(network);
                record.setNetworkId(network.getId());
                record.setTimestamp(cell.getTimestamp());
                record.setRxBytes(cell.getRxBytes());
                record.setTxBytes(cell.getTxBytes());
                results.add(record);
            }
        } else {
            for (App a : apps) {
                if (!a.isMonitoring() || a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)
                        || a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                    continue;
                }
                Record record = new Record();
                record.setApp(a);
                record.setAppId(a.getId());
                record.setNetworkId(network.getId());
                record.setNetwork(network);
                record.setTimestamp(System.currentTimeMillis());
                record.setTxBytes(TrafficTool.getUidTxBytes(a.getUid()));
                record.setRxBytes(TrafficTool.getUidRxBytes(a.getUid()));
                results.add(record);
            }

            //记录WIFI和mobile的特殊流量
            App wifiApp = null;
            App mobileApp = null;
            for (App a : apps) {
                if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                    mobileApp = a;
                }
                if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                    wifiApp = a;
                }
                if (mobileApp != null && wifiApp != null) {
                    break;
                }
            }
            //UNLIKELY
            if (wifiApp == null || mobileApp == null) {
                /**add by licong begin 可能有某种情况，导致单独记录mobile和wifi的app为空，所以再插入一次，再查询一次，
                 如果还是为空就不管了，直接return，导致的结果是查询的数据基本为空。但是不会crash了**/
                App wifi = new App();
                wifi.setAppName("wifi_interface");
                wifi.setPackageName(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE);
                wifi.setUid(-1);
                wifi.setMonitoring(true);
                wifi.setDisplay(false/*DEBUG*/);
                wifi.setWifiAccess(true);
                wifi.setMobileAccess(true);
                appDao.insert(wifi);
                App mobile = new App();
                mobile.setAppName("mobile_interface");
                mobile.setPackageName(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE);
                mobile.setUid(-1);
                mobile.setMonitoring(true);
                mobile.setDisplay(false/*DEBUG*/);
                mobile.setWifiAccess(true);
                mobile.setMobileAccess(true);
                appDao.insert(mobile);
                List<App> queryApps = appDao.queryForAll();
                if (queryApps == null) return;
                for (App a : queryApps) {
                    if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                        mobileApp = a;
                    }
                    if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                        wifiApp = a;
                    }
                    if (mobileApp != null && wifiApp != null) {
                        break;
                    }
                }
                if (wifiApp == null || mobileApp == null) {
                    Log.e(TAG, "Wifi app and mobile app is all null, error occurred!");
                    return;
                }
                /**add by licong end **/
            }

            /**
             * 通过反射获取的wifi和mobile流量数据可能失败，在失败的情况下为-1，record中并不对此进行纠错，
             * 因为record所处的上下文并不具备足够的数据对其进行纠错，所以这种失败的-1数据依然丢进缓存队列中。
             * 当TrafficDispatcher对其进行calculate处理时，calculate所处的上下文中拥有足够多的数据对这样
             * 的错误（-1）数据进行（伪）矫正。
             */
            long[] mobileData = new long[2];
            long[] wifiData = new long[2];
            TrafficTool.getIfaceTotal(this, mobileData, wifiData);
            Record wifiRecord = new Record();
            wifiRecord.setApp(wifiApp);
            wifiRecord.setAppId(wifiApp.getId());
            wifiRecord.setNetworkId(network.getId());
            wifiRecord.setNetwork(network);
            wifiRecord.setTimestamp(System.currentTimeMillis());
            wifiRecord.setRxBytes(wifiData[0]);
            wifiRecord.setTxBytes(wifiData[1]);
            results.add(wifiRecord);

            Record mobileRecord = new Record();
            mobileRecord.setApp(mobileApp);
            mobileRecord.setAppId(mobileApp.getId());
            mobileRecord.setNetworkId(network.getId());
            mobileRecord.setNetwork(network);
            mobileRecord.setTimestamp(System.currentTimeMillis());
            mobileRecord.setRxBytes(mobileData[0]);
            mobileRecord.setTxBytes(mobileData[1]);
            results.add(mobileRecord);
        }
        for (Record record : results) {
            recordDao.insert(record);
        }
    }

    private Network findOrCreateNetwork(Network network) throws SQLException {
        Network result = null;
        List<Network> queryResult;
        if (network.getNetworkType() == Network.NetworkType.WIFI) {
            queryResult = networkDao.query(Network.getMasterKey(true), network.getWifiBSSID());
            if (queryResult != null && queryResult.size() > 0) {
                result = queryResult.get(0);
            }
        } else {
            queryResult = networkDao.query(Network.getMasterKey(false), network.getNetworkName());
            if (queryResult != null && queryResult.size() > 0) {
                result = queryResult.get(0);
            }
        }
        if (result != null) {
            return result;
        } else {
            networkDao.insert(network);
            return findOrCreateNetwork(network);
        }
    }

    /**
     * 初始化应用数据库，包含数据库操作，不要在主线程
     */
    public synchronized void initAppDatabase() throws SQLException {
        final ArrayList<App> apps = new ArrayList<App>();
        PackageManager manager = getPackageManager();

        ArrayList<Integer> addedUids = new ArrayList<Integer>();

        List<App> exist = appDao.queryForAll();
        if (exist != null && exist.size() > 0) {
            LogWrapper.e(TAG, "App database has been init.");
            return;
        }
        List<PackageInfo> list = manager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : list) {
            String[] permissions = info.requestedPermissions;
            if (permissions == null) {
                continue;
            }
            boolean monitor = false;
            for (String p : permissions) {
                if (p.equals(Manifest.permission.INTERNET)) {
                    monitor = true;
                    break;
                }
            }
            if (monitor) {
                App app = new App();
                app.setAppName(info.applicationInfo.loadLabel(manager).toString());
                app.setPackageName(info.packageName);
                app.setVersionCode(info.versionCode);
                app.setVersionName(info.versionName);
                app.setUid(info.applicationInfo.uid);
                //解决同一个UID不同packageName的问题，同时数据较MyDataManager多也是因为同一个UID数据重叠造成的
                if (addedUids.contains(app.getUid())) {
                    LogWrapper.e(TAG, "Abort " + app.getAppName());
                    continue;
                } else {
                    addedUids.add(app.getUid());
                }
                app.setMonitoring(true);
                // liukai added @20141124 begin
                app.setDisplay(true);
                app.setWifiAccess(true);
                app.setMobileAccess(true);
                // liukai added @20141124 end
                apps.add(app);
            }
        }

        //添加系统自带UID监控，目前已知有三个UID：0.1000.1013
        App root = new App();
        root.setAppName(SystemInsideUID.InsideUid.AID_ROOT.valueOf(this));
        root.setPackageName(SystemInsideUID.InsideUid.AID_ROOT.getFakePkgName());
        root.setUid(SystemInsideUID.InsideUid.AID_ROOT.value());
        root.setMonitoring(true);
        // liukai added @20141124 begin
        root.setDisplay(true);
        root.setWifiAccess(true);
        root.setMobileAccess(true);
        // liukai added @20141124 end
        apps.add(root);

        if (!addedUids.contains(SystemInsideUID.InsideUid.AID_SYSTEM.value())) {
            App system = new App();
            system.setAppName(SystemInsideUID.InsideUid.AID_SYSTEM.valueOf(this));
            system.setPackageName(SystemInsideUID.InsideUid.AID_SYSTEM.getFakePkgName());
            system.setUid(SystemInsideUID.InsideUid.AID_SYSTEM.value());
            system.setMonitoring(true);
            // liukai added @20141124 begin
            system.setDisplay(true);
            system.setWifiAccess(true);
            system.setMobileAccess(true);
            // liukai added @20141124 end
            apps.add(system);
        }

        App media = new App();
        media.setAppName(SystemInsideUID.InsideUid.AID_MEDIA.valueOf(this));
        media.setPackageName(SystemInsideUID.InsideUid.AID_MEDIA.getFakePkgName());
        media.setUid(SystemInsideUID.InsideUid.AID_MEDIA.value());
        media.setMonitoring(true);
        // liukai added @20141124 begin
        media.setDisplay(true);
        media.setWifiAccess(true);
        media.setMobileAccess(true);
        // liukai added @20141124 end
        apps.add(media);

        //添加WIFI和手机网络用户，为了更精准的计算总流量，同时也能够响应各个时段的功能
        App wifi = new App();
        wifi.setAppName("wifi_interface");
        wifi.setPackageName(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE);
        wifi.setUid(-1);
        wifi.setMonitoring(true);
        // liukai added @20141124 begin
        wifi.setDisplay(false/*DEBUG*/);
        wifi.setWifiAccess(true);
        wifi.setMobileAccess(true);
        // liukai added @20141124 end
        apps.add(wifi);

        App mobile = new App();
        mobile.setAppName("mobile_interface");
        mobile.setPackageName(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE);
        mobile.setUid(-1);
        mobile.setMonitoring(true);
        // liukai added @20141124 begin
        mobile.setDisplay(false/*DEBUG*/);
        mobile.setWifiAccess(true);
        mobile.setMobileAccess(true);
        // liukai added @20141124 end
        apps.add(mobile);

        LogWrapper.e(TAG, "Network application count " + apps.size());
        if (apps.size() <= 0) {
            return;
        }
        //启用数据库事务处理来提高插入效率
        TransactionManager.callInTransaction(TrafficDataHelper.getHelper(this).getConnectionSource(),
                new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        for (App app : apps) {
                            appDao.insert(app);
                        }
                        return null;
                    }
                });
    }

    public void initNetDataBase() {
        final Network network = CheckNetTypeTools.checkNet(this);
        if (network.getNetworkType() != Network.NetworkType.NONE) { //有网络
            try {
                onNetworkChanged(network);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void onNetworkChanged(Network network) throws SQLException {
        String key;
        String value;
        if (network.getNetworkType() == Network.NetworkType.WIFI) {
            key = Network.getMasterKey(true);
            value = network.getWifiBSSID();
        } else {
            key = Network.getMasterKey(false);
            value = network.getNetworkName();
        }

        List<Network> queryResult = networkDao.query(key, value);
        if (queryResult == null || queryResult.size() <= 0) {
            networkDao.insert(network);
        } else {
            Network oldNetwork = queryResult.get(0);
            oldNetwork.update(network);
            networkDao.update(oldNetwork);
        }
    }

    private void onAppChanged(App app, boolean isAdd) throws SQLException {
        LogWrapper.e(TAG, "" + isAdd + ":" + app.toString());
        App result = null;
        // liukai added @20141205 begin
        List<App> abort = appDao.query(App.COLUMN_UID, app.getUid());
        if (abort != null && abort.size() > 0) {
            LogWrapper.d(TAG, "Same UID, abort it (" + app.getPackageName() +
                    ":" + app.getAppName() + ")");
            return;
        }
        // liukai added @20141205 end
        List<App> queryResult = appDao.query(App.getMasterKey(), app.getPackageName());
        if (queryResult != null && queryResult.size() > 0) {
            result = queryResult.get(0);
        }
        if (result == null) {
            if (isAdd) {
                appDao.insert(app);
            }
        } else {
            result.update(app, isAdd);
            appDao.update(result);
        }
    }

    private class TrafficDispatcher extends Thread {
        private final String TAG = TrafficDispatcher.class.getSimpleName();
        BlockingQueue<Long> mQueue;
        Context mContext;

        public TrafficDispatcher(Context context, BlockingQueue<Long> queue) {
            mContext = context;
            mQueue = queue;
            setName(TAG + " " + System.currentTimeMillis() + "");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            LogWrapper.e(TAG, getName() + " Dispatcher interrupt.");
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            LogWrapper.e(TAG, "Dispatcher start working... " + getName());
            long timestamp;
            /**
             * 对于轮询处理过程不需要添加wakelock，因为缓存数据是以文件形式做持久化处理的，
             * 轮询过程中一旦处理了这个缓存数据会立即将其删除，所以如果轮询线程被Freeze，那么
             * 缓存文件也不会被删除，所以一旦手机唤醒(用户打开手机或App查看流量)，轮询过程会
             * 继续进行，缓存文件因为并没有丢失一样会被正常处理，所以无需添加wakelock进行守护，
             * 同时也避免了不必要的电量损耗
             */
            while (mState) {
                try {
                    timestamp = mQueue.take();
                    LogWrapper.d(TAG, "Got a request: " + timestamp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
//                    throw new RuntimeException("Interrupt dispatcher");
                    break;
                }

                File file = StorageUtils.getCacheFile(mContext, String.valueOf(timestamp));
                if (!file.exists()) {
                    LogWrapper.e(TAG, String.format("File doesn't exist %s, drop it.", file.getName()));
                    continue;
                }
                try {
                    mDispatcherWakelock.acquire();
                    InputStream is = new FileInputStream(file);
                    String result = convertStreamToString(is);
                    AppRecord record = JSON.parseObject(result, AppRecord.class);
                    LogWrapper.d(TAG, String.format("Parse DONE [ %s ]", file.getName()));
                    StorageUtils.deleteCacheFile(mContext, file.getName());
                    calculateTrafficDiff(record);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDispatcherWakelock.release();
                }
            }
            LogWrapper.e(TAG, "Dispatcher quit... " + this.getName());
        }
    }

    /**
     * 计算缓存cache文件中记录的流量与数据库中record表中数据的差分数据，然后
     * 存储到traffic（分表）中。
     *
     * @param record 缓存文件所解析出的记录数据
     */
    private void calculateTrafficDiff(AppRecord record) throws SQLException {
        int networkId = record.getNetworkId();
        LogWrapper.d(TAG, "Start calculate" + " network No." + networkId);
        List<AppRecordCell> cells = record.getDetails();

        List<Network> networkQueryResult = networkDao.query(Network.COLUMN_ID, networkId);
        Network newNetwork = null;
        if (networkQueryResult != null && networkQueryResult.size() > 0) {
            newNetwork = networkQueryResult.get(0);
        }
        // 保存差异数据用来上报给各个监听器
        List<Traffic> result = new ArrayList<Traffic>();
        // 临时存储除了mobileApp和wifiApp的其他app的上行和下行流量的和
        long tempTotalRx = 0;
        long tempTotalTx = 0;
        for (AppRecordCell cell : cells) {
            Record r = null;
            List<Record> recordList = recordDao.query(Record.COLUMN_APP_ID, cell.getAppId());
            if (recordList != null && recordList.size() > 0) {
                r = recordList.get(0);
            }
            // 数据库中并没有记录，这是一条新的App记录
            if (r == null)
                continue;
            Network network = r.getNetwork();
            App app = r.getApp();
            long startTimestamp = r.getTimestamp();
            long endTimestamp = cell.getTimestamp();
            long txBytes = cell.getTxBytes() - r.getTxBytes();
            long rxBytes = cell.getRxBytes() - r.getRxBytes();
            if (txBytes < 0) {
                txBytes = 0;
            }
            if (rxBytes < 0) {
                rxBytes = 0;
            }
            if (app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)
                    || app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                if (cell.getTxBytes() == -1 || cell.getRxBytes() == -1
                        || r.getTxBytes() == -1 || r.getRxBytes() == -1) {
                    // 若反射获取WIFI和mobile流量数据失败，则将数据流量设置为-1，在数据使用时做相应反应
                    txBytes = -1;
                    rxBytes = -1;
                }
            } else {
                tempTotalTx += txBytes;
                tempTotalRx += rxBytes;
            }
            Traffic traffic = new Traffic();
            traffic.setApp(app);
            traffic.setAppId(app.getId());
            //这块地方一定要当心，之所以用record表中的网络数据是因为在改变网络情况下会进行一次打点并且清空record表，所以record表中的网络格式肯定和现在我们要存的数据的网络格式是一致的
            traffic.setNetwork(network);
            traffic.setNetworkId(network.getId());
            traffic.setStartTimestamp(startTimestamp);
            traffic.setEndTimestamp(endTimestamp);
            traffic.setUploadBytes(txBytes);
            traffic.setDownloadBytes(rxBytes);
            result.add(traffic);
        }

        for (Traffic traffic : result) {
            App app = traffic.getApp();
            if (app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                if (traffic.getUploadBytes() == -1 || traffic.getDownloadBytes() == -1
                        || Math.abs(traffic.getUploadBytes() + traffic.getDownloadBytes() - tempTotalRx - tempTotalTx) > 600000) {
                    if (traffic.getNetwork().getNetworkType() != Network.NetworkType.WIFI) {
                        traffic.setDownloadBytes(tempTotalRx);
                        traffic.setUploadBytes(tempTotalTx);
                    }
                }
            } else if (app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                if (traffic.getUploadBytes() == -1 || traffic.getDownloadBytes() == -1
                        || Math.abs(traffic.getUploadBytes() + traffic.getDownloadBytes() - tempTotalRx - tempTotalTx) > 600000) {
                    if (traffic.getNetwork().getNetworkType() == Network.NetworkType.WIFI) {
                        traffic.setDownloadBytes(tempTotalRx);
                        traffic.setUploadBytes(tempTotalTx);
                    }
                }
            }
        }

        /**
         * 注册所有的当天可能时段 end_timestamps
         */
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        ArrayList<Long> timestamps = new ArrayList<>();
        for (int i = 0; i <= 24; i += PULL_PERIOD) {
            c.set(year, month, day, i, 5, 0); //Alarm自动对其，5分钟内
            timestamps.add(c.getTimeInMillis());
        }
        //检测endTimeStamp
        new CheckEndTimeStampTools(this, year, month, day, false).checkLong(timestamps);
        Collections.sort(timestamps);
        final TrafficDao trafficDao = new TrafficDao(this);
        final List<Traffic> trafficNew = new ArrayList<>();
        for (Traffic t : result) {
            App app = t.getApp();
            long endTimestamp = t.getEndTimestamp();
            /**
             * 找到记录中所描述的这个App在同一时段中是否存在数据，若存在则合并，若不存在则插入
             */
            long belongTimeEnd = -1L;
            for (Long lt : timestamps) {
                if (endTimestamp <= lt) {
                    belongTimeEnd = lt;
                    break;
                }
            }
            c.setTimeInMillis(belongTimeEnd);
            int belongTimeEndYear = c.get(Calendar.YEAR);
            int belongTimeEndMonth = c.get(Calendar.MONTH);
            int belongTimeEndHour = c.get(Calendar.HOUR_OF_DAY);
            int belongTimeEndDay = c.get(Calendar.DATE);
            c.set(belongTimeEndYear, belongTimeEndMonth, belongTimeEndDay, belongTimeEndHour, 0, 0);
            long belongTimeStart = c.getTimeInMillis() - 4 * 60 * 60 * 1000;

            QueryBuilder<Traffic, Integer> queryBuilder = trafficDao.getDao().queryBuilder();
            queryBuilder.where().eq(Traffic.COLUMN_APP_ID, app.getId()).and().rawComparison(Traffic.COLUMN_START_TIMESTAMP, ">=", belongTimeStart)
                    .and().rawComparison(Traffic.COLUMN_END_TIMESTAMP, "<=", belongTimeEnd);
            PreparedQuery<Traffic> preparedQuery = queryBuilder.prepare();
            List<Traffic> existT = trafficDao.query(preparedQuery);
            LogWrapper.d(TAG, timestampToString(t.getStartTimestamp()) + " --> " + timestampToString(endTimestamp)
                    + " :: " + timestampToString(belongTimeStart) + " --> " + timestampToString(belongTimeEnd));
            boolean update = false;
            for (Traffic e : existT) {
                if (e.getNetwork().getNetworkType() == t.getNetwork().getNetworkType()) {
                    trafficDao.update(e.update(t));
                    update = true;
                    LogWrapper.d(TAG, " " + e.getApp().getAppName() + " | " +
                            timestampToString(e.getStartTimestamp()) + " --> " + timestampToString(t.getEndTimestamp()));
                    break;
                }
            }
            if (!update && t.getDownloadBytes() + t.getUploadBytes() > 0) { //花费0流量的数据不计入数据库
                trafficNew.add(t);
            }
        }

        for (Traffic traffic : trafficNew) {
            trafficDao.insert(traffic);
        }
        LogWrapper.d(TAG, "Report result");
        sendBroadcast(new Intent(BROADCAST_TRAFFIC_UPDATE));

        if (newNetwork == null) {
            LogWrapper.d(TAG, "Network INVALID, cleanup");
            recordDao.delete();
        } else {
            LogWrapper.d(TAG, "Record new data");
            record(record);
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        is.close();
        return sb.toString().trim();
    }

    /**
     * Created by kevin on 14-10-8.
     * <p/>
     * Traffic recorder
     * 保存所有应用的当前记录，存储在cache中持久化数据，由wakelock保证单次操作完整进行
     */
    private class TrafficRecorder extends Thread {
        private final String TAG = TrafficRecorder.class.getSimpleName();

        public static final int INVALID_NETWORK = -1;

        private Context mContext;
        private BlockingQueue<Long> mQueue;
        private boolean mIsTimePick = false;

        public TrafficRecorder(Context context, BlockingQueue<Long> queue, boolean timetick) {
            mContext = context;
            mQueue = queue;
            mIsTimePick = timetick;
        }

        @Override
        public void run() {
            LogWrapper.d(TAG, TAG);
            try {
                mRecorderWakelock.acquire();
                Network network = CheckNetTypeTools.checkNet(mContext);
                AppRecord record = new AppRecord();
                int id;
                if (network.getNetworkType() != Network.NetworkType.NONE) {
                    network = findOrCreateNetwork(network);
                    id = network.getId();
                } else { //无网络
                    id = INVALID_NETWORK;
                }
                record.setNetworkId(id);

                List<App> allApps = appDao.queryForAll();
                List<AppRecordCell> cells = new ArrayList<AppRecordCell>();
                String iface = network.getIface();
                SparseArray<FileTrafficObject> fileTrafficObjectSparseArray = null;

                LogWrapper.d(TAG, "HasRoot: " + CommandHelper.hasRoot() + " Root function: " + mSpManager.getBoolean(SpManager.SpData.OPEN_ROOT_ADVANCED_FUNCTION));
                //读取文件获取流量的时候需要当前网络的iface，如果没有则按照读取系统接口获取
                if (iface != null) {
                    fileTrafficObjectSparseArray = TrafficTool.getFileTrafficData();
                }
                for (App app : allApps) {
                    // 如果应用已经设定为不再监控则直接跳过
                    if (!app.isMonitoring() || app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)
                            || app.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                        continue;
                    }
                    AppRecordCell cell = new AppRecordCell();
                    cell.setAppId(app.getId());
                    cell.setTimestamp(System.currentTimeMillis());
                    if (iface != null && fileTrafficObjectSparseArray != null
                            && fileTrafficObjectSparseArray.get(iface.hashCode() + String.valueOf(app.getUid()).hashCode()) != null) {
                        cell.setRxBytes(fileTrafficObjectSparseArray.get(iface.hashCode() + String.valueOf(app.getUid()).hashCode()).getRxBytes());
                        cell.setTxBytes(fileTrafficObjectSparseArray.get(iface.hashCode() + String.valueOf(app.getUid()).hashCode()).getTxBytes());
                    } else {
                        cell.setRxBytes(TrafficTool.getUidRxBytes(app.getUid()));
                        cell.setTxBytes(TrafficTool.getUidTxBytes(app.getUid()));
                    }
                    cells.add(cell);
                }

                //记录WIFI和mobile的特殊流量
                App wifiApp = null;
                App mobileApp = null;
                for (App a : allApps) {
                    if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_MOBILE_INTERFACE)) {
                        mobileApp = a;
                    }
                    if (a.getPackageName().equals(EngineEnvironment.PACKAGE_NAME_SPECIAL_WIFI_INTERFACE)) {
                        wifiApp = a;
                    }
                    if (mobileApp != null && wifiApp != null) {
                        break;
                    }
                }
                //UNLIKELY
//                if (wifiApp == null || mobileApp == null) {
//                    throw new RuntimeException("Wifi app or mobile app is null, error occurred!");
//                }

                long[] mobileData = new long[2];
                long[] wifiData = new long[2];
                TrafficTool.getIfaceTotal(mContext, mobileData, wifiData);

                if (mobileApp != null) {
                    AppRecordCell mobileCell = new AppRecordCell();
                    mobileCell.setAppId(mobileApp.getId());
                    mobileCell.setTimestamp(System.currentTimeMillis());
                    mobileCell.setRxBytes(mobileData[0]);
                    mobileCell.setTxBytes(mobileData[1]);
                    cells.add(mobileCell);
                }

                if (wifiApp != null) {
                    AppRecordCell wifiCell = new AppRecordCell();
                    wifiCell.setAppId(wifiApp.getId());
                    wifiCell.setTimestamp(System.currentTimeMillis());
                    wifiCell.setRxBytes(wifiData[0]);
                    wifiCell.setTxBytes(wifiData[1]);
                    cells.add(wifiCell);
                }

                record.setDetails(cells);

                long timestamp = System.currentTimeMillis();
                File file = StorageUtils.getCacheFile(mContext, "" + timestamp);
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
                writer.write(JSON.toJSONString(record));
                writer.close();

                if (mIsTimePick) {
                    profileDao.insert(new Profile(Profile.COLUMN_KEY_TIMESTAMP, "" + System.currentTimeMillis()));
                }
                LogWrapper.e(TAG, "new a request " + cells.size() + ":" + timestamp);
                mQueue.add(timestamp);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                mRecorderWakelock.release();
            }
        }
    }

    private static String timestampToString(long timestamp) {
        DateFormat formater = new SimpleDateFormat("HH:mm:ss");
        return formater.format(new Date(timestamp));
    }
}
