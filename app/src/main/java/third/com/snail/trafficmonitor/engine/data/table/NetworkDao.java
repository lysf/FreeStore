package third.com.snail.trafficmonitor.engine.data.table;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;

/**
 * Created by lic on 2015/7/14.
 * network表的持久数据的操作类
 */
public class NetworkDao {

    Dao<Network, Integer> dao;
    TrafficDataHelper trafficDataHelper;

    public NetworkDao(Context context) throws SQLException {
        trafficDataHelper = TrafficDataHelper.getHelper(context);
        dao = trafficDataHelper.getNetWorkDao();
    }

    public Dao<Network, Integer> getDao() {
        return dao;
    }

    /**
     * 插入
     * */
    public void insert(Network data) throws SQLException {
        dao.createIfNotExists(data);
    }

    /**
     * 更新
     * */
    public void update(Network data) throws SQLException {
        dao.update(data);
    }

    /**
     * 查询
     * 按照键值对查询
     * */
    public List<Network> query(String key, Object value) throws SQLException {
        List<Network> networks = dao.queryForEq(key, value);
        return networks;
    }

    /**
     * 查询全部
     */
    public List<Network> queryForAll() throws SQLException {
        List<Network> networks = dao.queryForAll();
        return networks;
    }
}
