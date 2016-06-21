package third.com.snail.trafficmonitor.engine.data.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 14/11/11.
 */
public class MyTableUtil {
    private static Logger logger = LoggerFactory.getLogger(MyTableUtil.class);
    private static final FieldType[] noFieldTypes = new FieldType[0];

    public static <T> int createTable(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig)
            throws SQLException {
        return createTable(connectionSource, tableConfig, false);
    }

    public static <T> int createTableIfNotExists(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig)
            throws SQLException {
        return createTable(connectionSource, tableConfig, true);
    }

    private static <T, ID> int createTable(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig, boolean ifNotExists) throws SQLException {
        Dao<T, ID> dao = SimpleDaoManager.createDao(connectionSource, tableConfig);
        if (dao instanceof BaseDaoImpl<?, ?>) {
            return doCreateTable(connectionSource, ((BaseDaoImpl<?, ?>) dao).getTableInfo(), ifNotExists);
        } else {
            tableConfig.extractFieldTypes(connectionSource);
            TableInfo<T, ID> tableInfo = new TableInfo<T, ID>(connectionSource.getDatabaseType(), null, tableConfig);
            return doCreateTable(connectionSource, tableInfo, ifNotExists);
        }
    }

    private static <T, ID> int doCreateTable(ConnectionSource connectionSource, TableInfo<T, ID> tableInfo,
                                             boolean ifNotExists) throws SQLException {
        DatabaseType databaseType = connectionSource.getDatabaseType();
        logger.info("creating table '{}'", tableInfo.getTableName());
        List<String> statements = new ArrayList<String>();
        List<String> queriesAfter = new ArrayList<String>();
        addCreateTableStatements(databaseType, tableInfo, statements, queriesAfter, ifNotExists);
        DatabaseConnection connection = connectionSource.getReadWriteConnection();
        try {
            int stmtC =
                    doStatements(connection, "create", statements, false, databaseType.isCreateTableReturnsNegative(),
                            databaseType.isCreateTableReturnsZero());
            stmtC += doCreateTestQueries(connection, databaseType, queriesAfter);
            return stmtC;
        } finally {
            connectionSource.releaseConnection(connection);
        }
    }

    private static int doCreateTestQueries(DatabaseConnection connection, DatabaseType databaseType,
                                           List<String> queriesAfter) throws SQLException {
        int stmtC = 0;
        // now execute any test queries which test the newly created table
        for (String query : queriesAfter) {
            CompiledStatement compiledStmt = null;
            try {
                compiledStmt =
                        connection.compileStatement(query, StatementBuilder.StatementType.SELECT, noFieldTypes,
                                DatabaseConnection.DEFAULT_RESULT_FLAGS);
                // we don't care about an object cache here
                DatabaseResults results = compiledStmt.runQuery(null);
                int rowC = 0;
                // count the results
                for (boolean isThereMore = results.first(); isThereMore; isThereMore = results.next()) {
                    rowC++;
                }
                logger.info("executing create table after-query got {} results: {}", rowC, query);
            } catch (SQLException e) {
                // we do this to make sure that the statement is in the exception
                throw SqlExceptionUtil.create("executing create table after-query failed: " + query, e);
            } finally {
                // result set is closed by the statement being closed
                if (compiledStmt != null) {
                    compiledStmt.close();
                }
            }
            stmtC++;
        }
        return stmtC;
    }

    private static int doStatements(DatabaseConnection connection, String label, Collection<String> statements,
                                    boolean ignoreErrors, boolean returnsNegative, boolean expectingZero) throws SQLException {
        int stmtC = 0;
        for (String statement : statements) {
            int rowC = 0;
            CompiledStatement compiledStmt = null;
            try {
                compiledStmt =
                        connection.compileStatement(statement, StatementBuilder.StatementType.EXECUTE, noFieldTypes,
                                DatabaseConnection.DEFAULT_RESULT_FLAGS);
                rowC = compiledStmt.runExecute();
                logger.info("executed {} table statement changed {} rows: {}", label, rowC, statement);
            } catch (SQLException e) {
                if (ignoreErrors) {
                    logger.info("ignoring {} error '{}' for statement: {}", label, e, statement);
                } else {
                    throw SqlExceptionUtil.create("SQL statement failed: " + statement, e);
                }
            } finally {
                if (compiledStmt != null) {
                    compiledStmt.close();
                }
            }
            // sanity check
            if (rowC < 0) {
                if (!returnsNegative) {
                    throw new SQLException("SQL statement " + statement + " updated " + rowC
                            + " rows, we were expecting >= 0");
                }
            } else if (rowC > 0 && expectingZero) {
                throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting == 0: " + statement);
            }
            stmtC++;
        }
        return stmtC;
    }

    private static <T, ID> void addCreateTableStatements(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
                                                         List<String> statements, List<String> queriesAfter, boolean ifNotExists) throws SQLException {
        StringBuilder sb = new StringBuilder(256);
        sb.append("CREATE TABLE ");
        if (ifNotExists && databaseType.isCreateIfNotExistsSupported()) {
            sb.append("IF NOT EXISTS ");
        }
        databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
        sb.append(" (");
        List<String> additionalArgs = new ArrayList<String>();
        List<String> statementsBefore = new ArrayList<String>();
        List<String> statementsAfter = new ArrayList<String>();
        // our statement will be set here later
        boolean first = true;
        for (FieldType fieldType : tableInfo.getFieldTypes()) {
            // skip foreign collections
            if (fieldType.isForeignCollection()) {
                continue;
            } else if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String columnDefinition = fieldType.getColumnDefinition();
            if (columnDefinition == null) {
                // we have to call back to the database type for the specific create syntax
                databaseType.appendColumnArg(tableInfo.getTableName(), sb, fieldType, additionalArgs, statementsBefore,
                        statementsAfter, queriesAfter);
            } else {
                // hand defined field
                databaseType.appendEscapedEntityName(sb, fieldType.getColumnName());
                sb.append(' ').append(columnDefinition).append(' ');
            }
        }
        // add any sql that sets any primary key fields
        databaseType.addPrimaryKeySql(tableInfo.getFieldTypes(), additionalArgs, statementsBefore, statementsAfter,
                queriesAfter);
        // add any sql that sets any unique fields
        databaseType.addUniqueComboSql(tableInfo.getFieldTypes(), additionalArgs, statementsBefore, statementsAfter,
                queriesAfter);
        for (String arg : additionalArgs) {
            // we will have spat out one argument already so we don't have to do the first dance
            sb.append(", ").append(arg);
        }
        sb.append(") ");
        databaseType.appendCreateTableSuffix(sb);
        statements.addAll(statementsBefore);
        statements.add(sb.toString());
        statements.addAll(statementsAfter);
        addCreateIndexStatements(databaseType, tableInfo, statements, ifNotExists, false);
        addCreateIndexStatements(databaseType, tableInfo, statements, ifNotExists, true);
    }

    private static <T, ID> void addCreateIndexStatements(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
                                                         List<String> statements, boolean ifNotExists, boolean unique) {
        // run through and look for index annotations
        Map<String, List<String>> indexMap = new HashMap<String, List<String>>();
        for (FieldType fieldType : tableInfo.getFieldTypes()) {
            String indexName;
            if (unique) {
                indexName = fieldType.getUniqueIndexName();
            } else {
                indexName = fieldType.getIndexName();
            }
            if (indexName == null) {
                continue;
            }

            List<String> columnList = indexMap.get(indexName);
            if (columnList == null) {
                columnList = new ArrayList<String>();
                indexMap.put(indexName, columnList);
            }
            columnList.add(fieldType.getColumnName());
        }

        StringBuilder sb = new StringBuilder(128);
        for (Map.Entry<String, List<String>> indexEntry : indexMap.entrySet()) {
            logger.info("creating index '{}' for table '{}", indexEntry.getKey(), tableInfo.getTableName());
            sb.append("CREATE ");
            if (unique) {
                sb.append("UNIQUE ");
            }
            sb.append("INDEX ");
            if (ifNotExists && databaseType.isCreateIndexIfNotExistsSupported()) {
                sb.append("IF NOT EXISTS ");
            }
            databaseType.appendEscapedEntityName(sb, indexEntry.getKey());
            sb.append(" ON ");
            databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
            sb.append(" ( ");
            boolean first = true;
            for (String columnName : indexEntry.getValue()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                databaseType.appendEscapedEntityName(sb, columnName);
            }
            sb.append(" )");
            statements.add(sb.toString());
            sb.setLength(0);
        }
    }
}
