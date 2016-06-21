package third.com.snail.trafficmonitor.engine.data.table;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.List;

import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;

/**
 * Created by lic on 2015/7/14.
 * traffic表的持久数据的操作类
 */
public class TrafficDao {

    Dao<Traffic, Integer> dao;
    TrafficDataHelper trafficDataHelper;

    public TrafficDao(Context context) throws SQLException {
        String tableName = EngineEnvironment.getInstance(context).getTrafficTableName();
        if (tableName == null) {
            throw new RuntimeException(String.format("Database traffic table haven't create yet."));
        }
        trafficDataHelper = TrafficDataHelper.getHelper(context);
        dao = trafficDataHelper.getTrafficDao(tableName);
    }

    public Dao<Traffic, Integer> getDao() {
        return dao;
    }

    /**
     * 插入
     */
    public void insert(Traffic data) throws SQLException {
        dao.createIfNotExists(data);
    }

    /**
     * 更新
     */
    public void update(Traffic data) throws SQLException {
        dao.update(data);
    }

    /**
     * 按照多条件查询
     */
    public List<Traffic> query(PreparedQuery<Traffic> preparedQuery) throws SQLException {
        List<Traffic> traffics = dao.query(preparedQuery);
        queryAssociate(traffics);
        return traffics;
    }

    /**
     * 由于traffic表里面的app_id和network_id字段关联的是id，需要重新进行在查询来填充里面的值
     */
    private void queryAssociate(List<Traffic> traffics) {
        for (int i = 0; i < traffics.size(); i++) {
            Traffic t = traffics.get(i);
            try {
                List<App> appList = trafficDataHelper.getAppDao().queryForEq(App.COLUMN_ID, t.getAppId());
                List<Network> networkList = trafficDataHelper.getNetWorkDao().queryForEq(Network.COLUMN_ID, t.getNetworkId());
                if (appList != null && appList.size() > 0)
                    traffics.get(i).setApp(appList.get(0));
                if (networkList != null && networkList.size() > 0)
                    traffics.get(i).setNetwork(networkList.get(0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
