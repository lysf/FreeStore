package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.TrafficStaticInfo;
import com.snailgame.cjg.statistics.TrafficStatisticsUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * 流量统计数据库  增删改查
 * Created by chenping1 on 2014/9/18.
 */
public class TrafficStaticDaoHelper {


    public static final String TAG = TrafficStaticDaoHelper.class.getSimpleName();

    public static final String DB_NO_DATA = "-1";
    private volatile static TrafficStaticDaoHelper trafficStaticDBHelper = null;

    private Dao<TrafficStaticInfo, Integer> trafficStaticDao;

    public TrafficStaticDaoHelper(Context context) {
        try {
            trafficStaticDao = FreeStoreDataHelper.getHelper(context).getTrafficStaticDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TrafficStaticDaoHelper getInstance(Context context) {
        if (trafficStaticDBHelper == null) {
            synchronized (TrafficStaticDaoHelper.class) {
                if (trafficStaticDBHelper == null) {
                    trafficStaticDBHelper = new TrafficStaticDaoHelper(context);

                }
            }
        }
        return trafficStaticDBHelper;
    }


    /**
     * 查询
     *
     * @param networkType
     * @return
     */
    public synchronized TrafficStaticInfo query(int networkType, String actionType) {
        try {
            QueryBuilder<TrafficStaticInfo, Integer> queryBuilder = trafficStaticDao.queryBuilder();
            queryBuilder.where().eq(TrafficStaticInfo.COL_NETWORK_TYPE, networkType).and()
                    .eq(TrafficStaticInfo.COL_ACTION_TYPE, actionType);
            PreparedQuery<TrafficStaticInfo> preparedUpdate = queryBuilder.prepare();
            List<TrafficStaticInfo> infos = trafficStaticDao.query(preparedUpdate);
            if (infos != null && infos.size() > 0) {
                return infos.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送并备份到本地数据库
     *
     * @return
     */
    public synchronized boolean sendAndBackup() {
        try {
            QueryBuilder<TrafficStaticInfo, Integer> queryBuilder = trafficStaticDao.queryBuilder();
            PreparedQuery<TrafficStaticInfo> preparedUpdate = queryBuilder.prepare();
            String startTime = TrafficStatisticsUtil.getStartTime();
            String endTime = TrafficStatisticsUtil.getCurrentTime();
            List<TrafficStaticInfo> infos = trafficStaticDao.query(preparedUpdate);
            if (infos == null)
                return false;
            if (infos.size() == 0)
                return false;
            TrafficStaticInfo info = infos.get(0);
            info.setStartTime(startTime);
            info.setEndTime(endTime);
            //发送
            if (info.getnFlow() != 0) {
                TrafficStatisticsUtil.send(info);
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }


    /**
     * 插入到TrafficStaticInfo的表中
     *
     * @param info 流量信息
     * @return
     */
    public synchronized boolean insert(TrafficStaticInfo info) {
        if (info == null) {
            return false;
        }
        try {
            trafficStaticDao.create(info);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新数据库
     *
     * @param info
     * @return false 失败
     */
    public synchronized boolean update(TrafficStaticInfo info) {
        if (info == null) {
            return false;
        }
        try {
            UpdateBuilder<TrafficStaticInfo, Integer> updateBuilder = trafficStaticDao.updateBuilder();
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_PHONE_NUMBER, info.getnPhoneNum());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_VENDOR, info.getcVendor());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_GPS_PROVINCE, info.getsGPSProvince());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_CHANNEL_ID, info.getcChennelId());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_FLOW, info.getnFlow());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_PLATFORM, info.getnPlatform());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_IMSI, info.getcImsi());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_IMEI, info.getcImei());
            updateBuilder.updateColumnValue(TrafficStaticInfo.COL_TIME, info.getEndTime());
            updateBuilder.where().eq(TrafficStaticInfo.COL_NETWORK_TYPE, info.getcNetworkType()).and().eq(TrafficStaticInfo.COL_ACTION_TYPE, info.getcActionType());
            PreparedUpdate<TrafficStaticInfo> preparedUpdate = updateBuilder.prepare();
            trafficStaticDao.update(preparedUpdate);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送成功后， 重置统计的数据
     *
     * @return
     */
    public synchronized boolean delete() {
        try {
            trafficStaticDao.delete(trafficStaticDao.queryForAll());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
