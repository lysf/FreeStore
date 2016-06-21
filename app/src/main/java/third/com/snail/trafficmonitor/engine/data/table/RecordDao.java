package third.com.snail.trafficmonitor.engine.data.table;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;

/**
 * Created by lic on 2015/7/14.
 * record表的持久数据操作类
 */
public class RecordDao {

    Dao<Record, Integer> dao;
    TrafficDataHelper trafficDataHelper;

    public RecordDao(Context context) throws SQLException {
        trafficDataHelper = TrafficDataHelper.getHelper(context);
        dao = trafficDataHelper.getRecordDao();
    }

    public Dao<Record, Integer> getDao() {
        return dao;
    }

    /**
     * 插入
     */
    public void insert(Record data) throws SQLException {
        dao.createIfNotExists(data);
    }

    /**
     * 更新
     */
    public void update(Record data) throws SQLException {
        dao.update(data);
    }

    /**
     * 查询
     */
    public List<Record> query(String key, Object value) throws SQLException {
        List<Record> records = dao.queryForEq(key, value);
        queryAssociate(records);
        return records;
    }

    /**
     * 查询全部
     */
    public List<Record> queryForAll() throws SQLException {
        List<Record> records = dao.queryForAll();
        queryAssociate(records);
        return records;
    }

    /**
     * 按照条件查询
     */
    public List<Record> queryAsCondition(PreparedQuery<Record> preparedQuery) throws SQLException {
        List<Record> records = dao.query(preparedQuery);
        queryAssociate(records);
        return records;
    }

    /**
     * 删除
     */
    public void delete() throws SQLException {
        List<Record> records = queryForAll();
        if (!ListUtils.isEmpty(records))
            dao.delete(records);
    }

    /**
     * 由于record表里面的app_id和network_id字段关联的是id，需要重新进行在查询来填充里面的值
     */
    private void queryAssociate(List<Record> records) {
        for (int i = 0; i < records.size(); i++) {
            Record r = records.get(i);
            try {
                List<App> appList = trafficDataHelper.getAppDao().queryForEq(App.COLUMN_ID, r.getAppId());
                List<Network> networkList = trafficDataHelper.getNetWorkDao().queryForEq(Network.COLUMN_ID, r.getNetworkId());
                if (appList != null && appList.size() > 0)
                    records.get(i).setApp(appList.get(0));
                if (networkList != null && networkList.size() > 0)
                    records.get(i).setNetwork(networkList.get(0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
