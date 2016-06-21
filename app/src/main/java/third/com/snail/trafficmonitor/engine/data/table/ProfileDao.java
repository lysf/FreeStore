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
 * profile类的持久数据操作类
 */
public class ProfileDao {

    Dao<Profile, Integer> dao;
    TrafficDataHelper trafficDataHelper;

    public ProfileDao(Context context) throws SQLException {
        trafficDataHelper = TrafficDataHelper.getHelper(context);
        dao = trafficDataHelper.getProfileDao();
    }

    public Dao<Profile, Integer> getDao() {
        return dao;
    }

    /**
     * 插入
     */
    public void insert(Profile data) throws SQLException {
        dao.createIfNotExists(data);
    }

    /**
     * 更新
     */
    public void update(Profile data) throws SQLException {
        dao.update(data);
    }

    /**
     * 查询
     */
    public List<Profile> query(String key, Object value) throws SQLException {
        List<Profile> profiles = dao.queryForEq(key, value);
        return profiles;
    }

    /**
     * 按照多条件查询
     * */
    public List<Profile> query(PreparedQuery<Profile> preparedQuery) throws SQLException {
        List<Profile> traffics = dao.query(preparedQuery);
        return traffics;
    }

    /**
     * 查询全部
     */
    public List<Profile> queryForAll() throws SQLException {
        List<Profile> profiles = dao.queryForAll();
        return profiles;
    }

    /**
     * 删除
     */
    public void delete() throws SQLException {
        List<Profile> profiles = queryForAll();
        if (!ListUtils.isEmpty(profiles))
            dao.delete(profiles);
    }
}
