package com.reign.framework.jdbc;

import org.springframework.beans.factory.*;
import javax.sql.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.jdbc.handlers.*;
import org.apache.commons.logging.*;
import org.springframework.dao.*;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.jdbc.support.*;

public abstract class AbstractJdbcExtractor implements JdbcExtractor, InitializingBean
{
    private static final Log log;
    @Autowired
    private SqlFactory factory;
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;
    private int fetchsize;
    private int maxRows;
    private int batchSize;
    private SqlBuilder sqlBuilder;
    private SQLExceptionTranslator translator;
    private static final MapListHandler MAP_LIST_HANDLER;
    private static final String SQL_SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
    
    static {
        log = LogFactory.getLog("com.reign.framework.jdbc");
        MAP_LIST_HANDLER = new MapListHandler();
    }
    
    public AbstractJdbcExtractor() {
        this.fetchsize = 100;
        this.maxRows = 0;
        this.batchSize = 50;
        this.sqlBuilder = new SqlBuilder();
        this.translator = new SQLStateSQLExceptionTranslator();
    }
    
    public AbstractJdbcExtractor(final DataSource dataSource) {
        this.fetchsize = 100;
        this.maxRows = 0;
        this.batchSize = 50;
        this.sqlBuilder = new SqlBuilder();
        this.translator = new SQLStateSQLExceptionTranslator();
        this.dataSource = dataSource;
        this.afterPropertiesSet();
    }
    
    public void setSqlBuilder(final SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }
    
    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }
    
    public void setFetchsize(final int fetchsize) {
        this.fetchsize = fetchsize;
    }
    
    @Override
    public <T> T query(String sql, final List<Param> params, final ResultSetHandler<T> handler) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            pstmt = conn.prepareStatement(sql, 1003, 1007);
            this.applyStatementSettings(pstmt);
            this.sqlBuilder.buildParameters(pstmt, params);
            rs = pstmt.executeQuery();
            return handler.handle(rs);
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(pstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
    public List<Map<String, Object>> query(final String sql, final List<Param> params) {
        return this.query(sql, params, (ResultSetHandler<List<Map<String, Object>>>)AbstractJdbcExtractor.MAP_LIST_HANDLER);
    }
    
    @Override
    public int update(String sql, final List<Param> params) {
        PreparedStatement pstmt = null;
        final ResultSet rs = null;
        Connection conn = null;
        int result = 0;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            pstmt = conn.prepareStatement(sql);
            this.applyStatementSettings(pstmt);
            this.sqlBuilder.buildParameters(pstmt, params);
            result = pstmt.executeUpdate();
            return result;
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(pstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
    public int insert(String sql, final List<Param> params, final boolean autoGenerator) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        int result = 0;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            pstmt = conn.prepareStatement(sql);
            this.applyStatementSettings(pstmt);
            this.sqlBuilder.buildParameters(pstmt, params);
            result = pstmt.executeUpdate();
            if (result == 0 || !autoGenerator) {
                return result;
            }
            pstmt.close();
            pstmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return result;
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(pstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
    public void batch(String sql, final List<List<Param>> paramsList) {
        PreparedStatement pstmt = null;
        final ResultSet rs = null;
        Connection conn = null;
        int size = 0;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            pstmt = conn.prepareStatement(sql);
            this.applyStatementSettings(pstmt);
            for (final List<Param> params : paramsList) {
                this.sqlBuilder.buildParameters(pstmt, params);
                pstmt.addBatch();
                if (++size == this.batchSize) {
                    this.doExecuteBatch(pstmt, this.batchSize);
                    size = 0;
                }
            }
            if (size > 0) {
                this.doExecuteBatch(pstmt, size);
            }
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(pstmt, rs);
            this.releaseConnection(conn);
        }
        DbUtils.closeQuietly(pstmt, rs);
        this.releaseConnection(conn);
    }
    
    @Override
    public boolean callProcedure(String sql, final List<Param> params) {
        CallableStatement cstmt = null;
        final ResultSet rs = null;
        Connection conn = null;
        boolean result = false;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            cstmt = conn.prepareCall(sql);
            this.applyStatementSettings(cstmt);
            this.sqlBuilder.buildParameters(cstmt, params);
            result = cstmt.execute();
            return result;
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(cstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
    public List<Object> callProcedureWithReturn(String sql, final List<Param> params) {
        CallableStatement cstmt = null;
        final ResultSet rs = null;
        Connection conn = null;
        List<Object> resultList = null;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            cstmt = conn.prepareCall(sql);
            this.applyStatementSettings(cstmt);
            this.sqlBuilder.buildParameters(cstmt, params);
            cstmt.execute();
            int index = 1;
            resultList = new ArrayList<Object>();
            for (final Param param : params) {
                if (param.type.equals(Type.Out)) {
                    resultList.add(cstmt.getObject(index));
                }
                ++index;
            }
            return resultList;
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(cstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
    public List<Map<String, Object>> callQueryProcedure(String sql, final List<Param> params) {
        CallableStatement cstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        if (AbstractJdbcExtractor.log.isDebugEnabled()) {
            AbstractJdbcExtractor.log.debug("SQL: " + sql);
        }
        try {
            conn = this.getConnection();
            cstmt = conn.prepareCall(sql);
            this.applyStatementSettings(cstmt);
            this.sqlBuilder.buildParameters(cstmt, params);
            rs = cstmt.executeQuery();
            return AbstractJdbcExtractor.MAP_LIST_HANDLER.handle(rs);
        }
        catch (SQLException e) {
            final DataAccessException dae = this.translator.translate("execute sql: " + sql + "error, msg: " + e.toString(), sql, e);
            throw dae;
        }
        finally {
            DbUtils.closeQuietly(cstmt, rs);
            this.releaseConnection(conn);
        }
    }
    
    @Override
	public void afterPropertiesSet() {
        this.initSQLExceptionTranslator();
    }
    
    public void setDataSource(final DataSource ds) {
        this.dataSource = ds;
    }
    
    public void setSqlFactory(final SqlFactory factory) {
        this.factory = factory;
    }
    
    public int getMaxRows() {
        return this.maxRows;
    }
    
    public int getFetchsize() {
        return this.fetchsize;
    }
    
    protected void applyStatementSettings(final Statement stmt) throws SQLException {
        if (this.fetchsize > 0) {
            stmt.setFetchSize(this.fetchsize);
        }
        if (this.maxRows > 0) {
            stmt.setMaxRows(this.maxRows);
        }
    }
    
    protected Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(this.dataSource);
    }
    
    protected void releaseConnection(final Connection connection) {
        DataSourceUtils.releaseConnection(connection, this.dataSource);
    }
    
    protected DataSource getDataSource() {
        return this.dataSource;
    }
    
    private synchronized void initSQLExceptionTranslator() {
        if (this.translator == null) {
            if (this.dataSource != null) {
                this.translator = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            }
            else {
                this.translator = new SQLStateSQLExceptionTranslator();
            }
        }
    }
    
    private void doExecuteBatch(final Statement stmt, final int batchSize) throws SQLException {
        final int[] result = stmt.executeBatch();
        if (batchSize != result.length) {
            throw new RuntimeException("batch error, excepted batch result len: " + batchSize + ", but jdbc return len: " + result.length);
        }
        int[] array;
        for (int length = (array = result).length, j = 0; j < length; ++j) {
            final int i = array[j];
            if (i == -3) {
                throw new RuntimeException("batch update failed: " + i);
            }
        }
    }
}
