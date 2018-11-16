package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.jdbc.listener.*;
import java.sql.*;
import java.util.*;

public class MongoConnection implements Connection
{
    private Connection connection;
    private static List<CommitListener> listeners;
    
    static {
        MongoConnection.listeners = new ArrayList<CommitListener>();
    }
    
    public static void registerListener(final CommitListener listener) {
        MongoConnection.listeners.add(listener);
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public MongoConnection(final Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.connection.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.connection.isWrapperFor(iface);
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return this.connection.createStatement();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return this.connection.prepareCall(sql);
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return this.connection.nativeSQL(sql);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.connection.setAutoCommit(autoCommit);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }
    
    @Override
    public void commit() throws SQLException {
        this.connection.commit();
        this.notiyListener(true);
    }
    
    @Override
    public void rollback() throws SQLException {
        this.connection.rollback();
        this.notiyListener(false);
    }
    
    @Override
    public void close() throws SQLException {
        this.connection.close();
        this.notiyListener(true);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.connection.setReadOnly(readOnly);
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return this.connection.isReadOnly();
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.connection.setCatalog(catalog);
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return this.connection.getCatalog();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.connection.setTransactionIsolation(level);
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.connection.getTransactionIsolation();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.connection.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.connection.clearWarnings();
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.connection.getTypeMap();
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this.connection.setTypeMap(map);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this.connection.setHoldability(holdability);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return this.connection.getHoldability();
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.connection.setSavepoint();
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return this.connection.setSavepoint(name);
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.connection.rollback(savepoint);
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.connection.releaseSavepoint(savepoint);
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return this.connection.prepareStatement(sql, autoGeneratedKeys);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return this.connection.prepareStatement(sql, columnIndexes);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return this.connection.createClob();
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return this.connection.createBlob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return this.connection.createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.connection.createSQLXML();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return this.connection.isValid(timeout);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this.connection.setClientInfo(name, value);
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        this.connection.setClientInfo(properties);
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return this.connection.getClientInfo(name);
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return this.connection.getClientInfo();
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return this.connection.createArrayOf(typeName, elements);
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return this.connection.createStruct(typeName, attributes);
    }
    
    private void notiyListener(final boolean succ) {
        for (final CommitListener listener : MongoConnection.listeners) {
            listener.commit(succ);
        }
    }
}
