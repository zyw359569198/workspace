package com.reign.framework.jdbc.orm.session;

import com.reign.framework.jdbc.orm.*;
import org.springframework.transaction.support.*;

public final class JdbcSessionUtil
{
    public static boolean hasTransactionalSession(final JdbcFactory jdbcFactory) {
        if (jdbcFactory == null) {
            return false;
        }
        final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.getResource(jdbcFactory);
        return sessionHolder != null && !sessionHolder.isEmpty();
    }
    
    public static boolean isSessionTransactional(final JdbcSession session, final JdbcFactory jdbcFactory) {
        if (jdbcFactory == null) {
            return false;
        }
        final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.getResource(jdbcFactory);
        return sessionHolder != null && sessionHolder.containsSession(session);
    }
    
    public static JdbcSession getSession(final JdbcFactory jdbcFactory, final boolean allowCreate) {
        return doGetSession(jdbcFactory, allowCreate);
    }
    
    private static JdbcSession doGetSession(final JdbcFactory jdbcFactory, final boolean allowCreate) throws IllegalStateException {
        final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.getResource(jdbcFactory);
        JdbcSession session = null;
        if (sessionHolder != null && !sessionHolder.isEmpty()) {
            session = sessionHolder.getValidatedSession();
            if (session != null) {
                return session;
            }
        }
        if (allowCreate) {
            session = jdbcFactory.openSession();
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                JdbcSessionHolder holderToUse = sessionHolder;
                if (holderToUse == null) {
                    holderToUse = new JdbcSessionHolder(session);
                }
                else {
                    holderToUse.setSession(session);
                }
                if (holderToUse != sessionHolder) {
                    TransactionSynchronizationManager.bindResource(jdbcFactory, holderToUse);
                }
            }
        }
        return session;
    }
    
    public static void closeSession(final JdbcSession session) {
        session.close();
    }
}
