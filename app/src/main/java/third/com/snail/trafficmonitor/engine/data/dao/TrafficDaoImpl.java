package third.com.snail.trafficmonitor.engine.data.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

import third.com.snail.trafficmonitor.engine.data.table.Traffic;

/**
 * Created by kevin on 14/11/11.
 */
public class TrafficDaoImpl extends BaseDaoImpl<Traffic, Integer> {
    public TrafficDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<Traffic> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }
}
