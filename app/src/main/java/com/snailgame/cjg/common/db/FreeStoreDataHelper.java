package com.snailgame.cjg.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.snailgame.cjg.common.db.dao.DeskGame;
import com.snailgame.cjg.common.db.dao.MyGame;
import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.cjg.common.db.dao.NewsReaded;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.dao.Skin;
import com.snailgame.cjg.common.db.dao.TrafficStaticInfo;
import com.snailgame.cjg.common.db.daoHelper.DeskGameDaoHelper;

import java.sql.SQLException;


/**
 * Created by lic on 2015/7/16.
 */
public class FreeStoreDataHelper extends OrmLiteSqliteOpenHelper {
    private final String TAG = FreeStoreDataHelper.class.getSimpleName();

    public static final String DB_NAME = "snail_app_store.db";
    public static final int DB_VERSION = 162;
    private volatile static FreeStoreDataHelper instance;

    private final Context mContext;
    private Dao<DeskGame, Integer> mDeskGameDao = null;
    private Dao<MyGame, Integer> mMyGameDao = null;
    private Dao<PushModel, Integer> mPushDao = null;
    private Dao<Skin, Integer> mSkinDao = null;
    private Dao<TrafficStaticInfo, Integer> mTrafficStaticDao = null;
    private Dao<NewsChannel, Integer> mNewsChannelDao = null;
    private Dao<NewsReaded, Integer> mNewsReadDao = null;

    private FreeStoreDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, DeskGame.class);
            TableUtils.createTableIfNotExists(connectionSource, MyGame.class);
            TableUtils.createTableIfNotExists(connectionSource, PushModel.class);
            TableUtils.createTableIfNotExists(connectionSource, Skin.class);
            TableUtils.createTableIfNotExists(connectionSource, TrafficStaticInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsChannel.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsReaded.class);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //在4.0新版本中需要在mygame表中增加一个totalInstallNum字段，并且需要在新版本数据库中删除之前的桌面游戏数据库中的重复数据
        if (oldVersion == 158) {
            if (mMyGameDao == null) {
                try {
                    mMyGameDao = getDao(MyGame.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                mMyGameDao.executeRaw("ALTER TABLE 'my_game_table' ADD COLUMN totalInstallNum INTEGER;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeskGameDaoHelper.deleteforDeskgameRepeat(mContext);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            addPushTableUserId();
        } else if (oldVersion == 159) {
            addPushTableUserId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeskGameDaoHelper.deleteforDeskgameRepeat(mContext);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (oldVersion == 160) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DeskGameDaoHelper.deleteforDeskgameRepeat(mContext);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            try {
                TableUtils.dropTable(connectionSource, DeskGame.class, true);
                TableUtils.dropTable(connectionSource, MyGame.class, true);
                TableUtils.dropTable(connectionSource, PushModel.class, true);
                TableUtils.dropTable(connectionSource, Skin.class, true);
                TableUtils.dropTable(connectionSource, TrafficStaticInfo.class, true);
                TableUtils.dropTable(connectionSource, NewsChannel.class, true);
                TableUtils.dropTable(connectionSource, NewsReaded.class, true);
                onCreate(database, connectionSource);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 增加push表中的userId字段
     */
    private void addPushTableUserId() {
        if (mPushDao == null) {
            try {
                mPushDao = getDao(PushModel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            mPushDao.executeRaw("ALTER TABLE 'push_table' ADD COLUMN user_id STRING DEFAULT '-1';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建AppDao来进行处理app表的操作
     */
    public Dao<DeskGame, Integer> getDeskGameDaoDao() throws SQLException {
        if (mDeskGameDao == null) {
            mDeskGameDao = getDao(DeskGame.class);
        }
        return mDeskGameDao;
    }


    /**
     * 创建MyGameDao来进行处理MyGame表的操作
     */
    public Dao<MyGame, Integer> getMyGameDao() throws SQLException {
        if (mMyGameDao == null) {
            mMyGameDao = getDao(MyGame.class);
        }
        return mMyGameDao;
    }

    /**
     * 创建PushDao来进行处理push表的操作
     */
    public Dao<PushModel, Integer> getPushDao() throws SQLException {
        if (mPushDao == null) {
            mPushDao = getDao(PushModel.class);
        }
        return mPushDao;
    }

    /**
     * 创建SkinDao来进行处理skin表的操作
     */
    public Dao<Skin, Integer> getSkinDao() throws SQLException {
        if (mSkinDao == null) {
            mSkinDao = getDao(Skin.class);
        }
        return mSkinDao;
    }

    /**
     * 创建TrafficStaticDao来进行处理TrafficStatic表的操作
     */
    public Dao<TrafficStaticInfo, Integer> getTrafficStaticDao() throws SQLException {
        if (mTrafficStaticDao == null) {
            mTrafficStaticDao = getDao(TrafficStaticInfo.class);
        }
        return mTrafficStaticDao;
    }

    public Dao<NewsChannel, Integer> getNewsChannelDao() throws SQLException {
        if (mNewsChannelDao == null) {
            mNewsChannelDao = getDao(NewsChannel.class);
        }
        return mNewsChannelDao;
    }

    public Dao<NewsReaded, Integer> getNewsReadDao() throws SQLException {
        if (mNewsReadDao == null) {
            mNewsReadDao = getDao(NewsReaded.class);
        }
        return mNewsReadDao;
    }

    @Override
    public void close() {
        super.close();
        mMyGameDao = null;
        mPushDao = null;
        mSkinDao = null;
        mTrafficStaticDao = null;
        mNewsChannelDao = null;
        mNewsReadDao = null;
        instance = null;
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static FreeStoreDataHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (FreeStoreDataHelper.class) {
                if (instance == null)
                    instance = new FreeStoreDataHelper(context);
            }
        }
        return instance;
    }

}
