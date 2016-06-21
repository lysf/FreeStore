package third.com.snail.trafficmonitor.engine.data.table;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.TrafficDataHelper;

/**
 * Created by lic on 2015/7/14.
 * app表的持久数据的操作类
 */
public class AppDao {

    Dao<App, Integer> dao;
    TrafficDataHelper trafficDataHelper;

    public AppDao(Context context) throws SQLException {
        trafficDataHelper = TrafficDataHelper.getHelper(context);
        dao = trafficDataHelper.getAppDao();
    }

    public Dao<App, Integer> getDao() {
        return dao;
    }

    /**
     * 插入
     * */
    public void insert(App data) throws SQLException {
        dao.createIfNotExists(data);
    }

    /**
     * 更新
     * */
    public void update(App data) throws SQLException {
        dao.update(data);
    }

    /**
     * 查询
     * */
    public List<App> query(String key, Object value) throws SQLException {
        List<App> apps = dao.queryForEq(key, value);
        return apps;
    }

    /**
     * 查询全部
     * */
    public List<App> queryForAll() throws SQLException {
        List<App> apps = dao.queryForAll();
        return apps;
    }

    /**
     * 删除
     * */
    public void delete() throws SQLException {
        List<App> apps = queryForAll();
        if (!ListUtils.isEmpty(apps))
            dao.delete(apps);
    }

}
