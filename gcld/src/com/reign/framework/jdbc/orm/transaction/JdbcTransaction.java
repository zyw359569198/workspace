package com.reign.framework.jdbc.orm.transaction;

import com.reign.framework.jdbc.orm.*;
import org.apache.commons.logging.*;
import java.sql.*;

public class JdbcTransaction implements Transaction
{
    private static final Log log;
    private Connection connection;
    private TransactionListener jdbcSession;
    private boolean toggleAutoCommit;
    private boolean begin;
    private boolean rolledBack;
    private boolean committed;
    private boolean commitFailed;
    private JdbcFactory jdbcFactory;
    
    static {
        log = LogFactory.getLog("com.reign.framework.jdbc.transaction");
    }
    
    public JdbcTransaction(final TransactionListener jdbcSession, final Connection connection, final JdbcFactory jdbcFactory) {
        this.connection = connection;
        this.jdbcFactory = jdbcFactory;
        this.jdbcSession = jdbcSession;
    }
    
    @Override
    public void begin() {
        if (this.begin) {
            return;
        }
        if (this.commitFailed) {
            throw new RuntimeException("cannot re-start transaction after failed commit");
        }
        try {
            this.toggleAutoCommit = this.connection.getAutoCommit();
            if (this.toggleAutoCommit) {
                this.connection.setAutoCommit(false);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("JDBC begin failed: ", e);
        }
        this.begin = true;
        this.committed = false;
        this.rolledBack = false;
        this.jdbcSession.begin(this);
        this.jdbcFactory.notifyTransactionBegin(this);
        if (JdbcTransaction.log.isDebugEnabled()) {
            JdbcTransaction.log.debug("start new transaction [" + this.toString() + "] on session [" + this.jdbcSession.toString() + "] on connection [" + this.connection.toString() + "]");
        }
    }
    
    @Override
    public void commit() {
        if (!this.begin) {
            throw new RuntimeException("Transaction not successfully started");
        }
        try {
            if (JdbcTransaction.log.isDebugEnabled()) {
                JdbcTransaction.log.debug("before commit transaction [" + this.toString() + "]");
            }
            this.jdbcSession.beforeCommit(this, true);
            this.jdbcFactory.notifyTransactionBeforeCommit(this, true);
            this.commitAndResetAutoCommit();
            this.committed = true;
            this.jdbcSession.commit(this, true);
            this.jdbcFactory.notifyTransactionCommit(this, true);
            if (JdbcTransaction.log.isDebugEnabled()) {
                JdbcTransaction.log.debug("commit transaction [" + this.toString() + "] succ");
            }
        }
        catch (SQLException e) {
            this.commitFailed = true;
            throw new RuntimeException("JDBC commit failed", e);
        }
    }
    
    @Override
    public void rollback() {
        if (!this.begin && !this.commitFailed) {
            throw new RuntimeException("Transaction not successfully started");
        }
        if (!this.commitFailed) {
            try {
                if (JdbcTransaction.log.isDebugEnabled()) {
                    JdbcTransaction.log.debug("before rollback transaction [" + this.toString() + "]");
                }
                try {
                    this.jdbcSession.beforeCommit(this, false);
                    this.jdbcFactory.notifyTransactionBeforeCommit(this, false);
                }
                finally {
                    this.rollbackAndResetAutoCommit();
                    this.rolledBack = true;
                    if (JdbcTransaction.log.isDebugEnabled()) {
                        JdbcTransaction.log.debug("rollback transaction [" + this.toString() + "] succ");
                    }
                }
                this.rollbackAndResetAutoCommit();
                this.rolledBack = true;
                if (JdbcTransaction.log.isDebugEnabled()) {
                    JdbcTransaction.log.debug("rollback transaction [" + this.toString() + "] succ");
                }
            }
            catch (SQLException e) {
                throw new RuntimeException("JDBC rollback failed", e);
            }
            finally {
                this.jdbcSession.commit(this, false);
                this.jdbcFactory.notifyTransactionCommit(this, false);
            }
            this.jdbcSession.commit(this, false);
            this.jdbcFactory.notifyTransactionCommit(this, false);
        }
    }
    
    @Override
    public boolean isActive() {
        return this.begin && !this.rolledBack && !(this.committed | this.commitFailed);
    }
    
    private void rollbackAndResetAutoCommit() throws SQLException {
        try {
            this.connection.rollback();
        }
        finally {
            this.toggleAutoCommit();
        }
        this.toggleAutoCommit();
    }
    
    private void commitAndResetAutoCommit() throws SQLException {
        try {
            this.connection.commit();
        }
        finally {
            this.toggleAutoCommit();
        }
        this.toggleAutoCommit();
    }
    
    private void toggleAutoCommit() {
        try {
            if (this.toggleAutoCommit) {
                this.connection.setAutoCommit(true);
            }
        }
        catch (Exception ex) {}
    }
}
