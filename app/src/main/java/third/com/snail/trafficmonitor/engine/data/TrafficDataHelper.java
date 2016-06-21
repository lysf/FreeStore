package third.com.snail.trafficmonitor.engine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Calendar;

import third.com.snail.trafficmonitor.engine.data.dao.MyTableUtil;
import third.com.snail.trafficmonitor.engine.data.table.App;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.Profile;
import third.com.snail.trafficmonitor.engine.data.table.Record;
import third.com.snail.trafficmonitor.engine.data.table.Traffic;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by lic on 15-7-13.
 * <p/>
 * ORMLite-Android Helper
 */
public class TrafficDataHelper extends OrmLiteSqliteOpenHelper {
    private final String TAG = TrafficDataHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "traffic.db";
    private static final int DATABASE_VERSION = 2;

    public static final String SPLIT_TABLE_FORMAT = "traffic_%1$s";
    private static TrafficDataHelper instance;
    private final Context mContext;
    /* dao which can throw exception */
    private Dao<Traffic, Integer> mTrafficDao = null;
    private Dao<App, Integer> mAppDao = null;
    private Dao<Network, Integer> mNetworkDao = null;
    private Dao<Profile, Integer> mProfileDao = null;
    private Dao<Record, Integer> mRecordDao = null;

    public TrafficDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * 创建或者删除traffic表
     *
     * @param year     年数
     * @param month    月份
     * @param isCreate 是否创建的标志位
     */
    public String dropOrCreateTrafficTable(int year, int month, boolean isCreate) throws SQLException {
        String yearStr = String.valueOf(year);
        String monthStr = month >= 10 ? "" + month : "0" + month;
        String table = String.format(SPLIT_TABLE_FORMAT, yearStr + monthStr);
        DatabaseTableConfig<Traffic> trafficConfig = DatabaseTableConfig.
                fromClass(getConnectionSource(), Traffic.class);
        trafficConfig.setTableName(table);
        if (isCreate) {
            MyTableUtil.createTableIfNotExists(getConnectionSource(), trafficConfig);
        } else {
            TableUtils.dropTable(getConnectionSource(), trafficConfig, true);
        }
        return table;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        LogWrapper.e(TAG, "TrafficDataHelper onCreate");
        try {
            TableUtils.createTableIfNotExists(connectionSource, App.class);
            TableUtils.createTableIfNotExists(connectionSource, Network.class);
            TableUtils.createTableIfNotExists(connectionSource, Profile.class);
            TableUtils.createTableIfNotExists(connectionSource, Record.class);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            //创建当前月份的Traffic表
            dropOrCreateTrafficTable(year, month, true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, App.class, true);
            TableUtils.dropTable(connectionSource, Network.class, true);
            TableUtils.dropTable(connectionSource, Profile.class, true);
            TableUtils.dropTable(connectionSource, Record.class, true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            //创建当前月份的Traffic表
            dropOrCreateTrafficTable(year, month, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建TrafficDao来进行处理traffic表的操作
     */
    public Dao<Traffic, Integer> getTrafficDao(String tableName) throws SQLException {
        if (mTrafficDao == null) {
            DatabaseTableConfig<Traffic> trafficConfig = DatabaseTableConfig.
                    fromClass(getConnectionSource(), Traffic.class);
            trafficConfig.setTableName(tableName);
            mTrafficDao = DaoManager.createDao(getConnectionSource(), trafficConfig);
        }
        return mTrafficDao;
    }


    /**
     * 创建AppDao来进行处理app表的操作
     */
    public Dao<App, Integer> getAppDao() throws SQLException {
        if (mAppDao == null) {
            mAppDao = getDao(App.class);
        }
        return mAppDao;
    }

    /**
     * 创建NetWorkDao来进行处理network表的操作
     */
    public Dao<Network, Integer> getNetWorkDao() throws SQLException {
        if (mNetworkDao == null) {
            mNetworkDao = getDao(Network.class);
        }
        return mNetworkDao;
    }

    /**
     * 创建ProfileDao来进行处理profile表的操作
     */
    public Dao<Profile, Integer> getProfileDao() throws SQLException {
        if (mProfileDao == null) {
            mProfileDao = getDao(Profile.class);
        }
        return mProfileDao;
    }

    /**
     * 创建RecordDao来进行处理record表的操作
     */
    public Dao<Record, Integer> getRecordDao() throws SQLException {
        if (mRecordDao == null) {
            mRecordDao = getDao(Record.class);
        }
        return mRecordDao;
    }

    @Override
    public void close() {
        super.close();
        mTrafficDao = null;
        mAppDao = null;
        mNetworkDao = null;
        mProfileDao = null;
        mRecordDao = null;
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized TrafficDataHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (TrafficDataHelper.class) {
                if (instance == null)
                    instance = new TrafficDataHelper(context);
            }
        }

        return instance;
    }
}
