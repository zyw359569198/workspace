package com.reign.framework.jdbc.orm.session;

import org.springframework.transaction.support.*;
import com.reign.framework.jdbc.orm.transaction.*;

public class JdbcSessionHolder extends ResourceHolderSupport
{
    private JdbcSession jdbcSession;
    private Transaction transaction;
    
    public JdbcSessionHolder(final JdbcSession session) {
        this.jdbcSession = session;
    }
    
    public JdbcSession getSession() {
        return this.jdbcSession;
    }
    
    public JdbcSession getValidatedSession() {
        if (this.jdbcSession != null && this.jdbcSession.isClosed()) {
            this.jdbcSession = null;
        }
        return this.jdbcSession;
    }
    
    public void setSession(final JdbcSession session) {
        this.jdbcSession = session;
    }
    
    public boolean containsSession(final JdbcSession session) {
        return session == this.jdbcSession;
    }
    
    public boolean isEmpty() {
        return this.jdbcSession == null;
    }
    
    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }
    
    public Transaction getTransaction() {
        return this.transaction;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.transaction = null;
    }
}
