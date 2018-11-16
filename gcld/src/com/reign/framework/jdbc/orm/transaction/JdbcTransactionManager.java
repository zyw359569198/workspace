package com.reign.framework.jdbc.orm.transaction;

import org.springframework.beans.factory.*;
import javax.sql.*;
import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.session.*;
import java.sql.*;
import org.springframework.transaction.support.*;
import org.springframework.transaction.*;
import org.springframework.jdbc.datasource.*;

public class JdbcTransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager, InitializingBean
{
    private DataSource dataSource;
    private JdbcFactory jdbcFactory;
    private static final long serialVersionUID = 3086285728484844417L;
    
    public JdbcFactory getJdbcFactory() {
        return this.jdbcFactory;
    }
    
    public void setJdbcFactory(final JdbcFactory jdbcFactory) {
        this.jdbcFactory = jdbcFactory;
    }
    
    public void setDataSource(final DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            this.dataSource = ((TransactionAwareDataSourceProxy)dataSource).getTargetDataSource();
        }
        else {
            this.dataSource = dataSource;
        }
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        if (this.getJdbcFactory() == null) {
            throw new IllegalArgumentException("Property 'jdbcContext' is required");
        }
        this.setDataSource(this.getJdbcFactory().getDataSource());
    }
    
    @Override
	public Object getResourceFactory() {
        return this.jdbcFactory;
    }
    
    @Override
	protected Object doGetTransaction() throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doGetTransaction call");
        }
        final JdbcTransactionObject txObject = new JdbcTransactionObject((JdbcTransactionObject)null);
        txObject.setSavepointAllowed(this.isNestedTransactionAllowed());
        final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.getResource(this.getResourceFactory());
        if (sessionHolder != null) {
            txObject.setSessionHolder(sessionHolder);
        }
        if (this.getDataSource() != null) {
            final ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(this.getDataSource());
            txObject.setConnectionHolder(conHolder);
        }
        return txObject;
    }
    
    @Override
	protected boolean isExistingTransaction(final Object transaction) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("isExistingTransaction call");
        }
        return ((JdbcTransactionObject)transaction).hasSpringManagedTransaction();
    }
    
    @Override
	protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doBegin call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)transaction;
        if (txObject.hasConnectionHolder() && !txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            throw new IllegalTransactionStateException("Pre-bound JDBC Connection found! JpaTransactionManager does not support running within DataSourceTransactionManager if told to manage the DataSource itself. It is recommended to use a single JpaTransactionManager for all transactions on a single DataSource, no matter whether JPA or JDBC access.");
        }
        JdbcSession session = null;
        try {
            if (txObject.getSessionHolder() == null || txObject.getSessionHolder().isSynchronizedWithTransaction()) {
                final JdbcSession newSession = this.getJdbcFactory().openSession();
                txObject.setSession(newSession);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Open New Session [" + newSession + "] for JDBC transaction");
                }
            }
            session = txObject.getSessionHolder().getSession();
            final Connection connection = session.getConnection();
            final Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(connection, definition);
            txObject.setPreviousIsolationLevel(previousIsolationLevel);
            final Transaction t = session.getTransaction();
            t.begin();
            txObject.getSessionHolder().setTransaction(t);
            final int timeout = this.determineTimeout(definition);
            if (timeout != -1) {
                txObject.getSessionHolder().setTimeoutInSeconds(timeout);
            }
            if (this.getDataSource() != null) {
                final Connection con = session.getConnection();
                final ConnectionHolder conHolder = new ConnectionHolder(con);
                if (timeout != -1) {
                    conHolder.setTimeoutInSeconds(timeout);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Exposing Wrapper JDBC transaction as JDBC transaction [" + con + "]");
                }
                TransactionSynchronizationManager.bindResource(this.getDataSource(), conHolder);
                txObject.setConnectionHolder(conHolder);
            }
            if (txObject.isNewSessionHolder()) {
                TransactionSynchronizationManager.bindResource(this.getResourceFactory(), txObject.getSessionHolder());
            }
            txObject.getSessionHolder().setSynchronizedWithTransaction(true);
        }
        catch (Exception ex) {
            if (txObject.isNewSession()) {
                try {
                    if (session.getTransaction().isActive()) {
                        session.getTransaction().rollback();
                    }
                }
                catch (Throwable ex2) {
                    this.logger.error("Could not rollback Session after failed transaction begin", ex);
                    throw new CannotCreateTransactionException("Could not open Session for transaction", ex);
                }
                finally {
                    JdbcSessionUtil.closeSession(session);
                }
                JdbcSessionUtil.closeSession(session);
            }
            throw new CannotCreateTransactionException("Could not open Session for transaction", ex);
        }
    }
    
    @Override
	protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doCommit call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)status.getTransaction();
        if (status.isDebug()) {
            this.logger.debug("Committing Wrpper Jdbc transaction on Session [" + txObject.getSessionHolder().getSession() + "]");
        }
        try {
            txObject.getSessionHolder().getTransaction().commit();
        }
        catch (Exception ex) {
            throw new TransactionSystemException("Could not commit Wrpper Jdbc transaction", ex);
        }
    }
    
    @Override
	protected Object doSuspend(final Object transaction) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doSuspend call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)transaction;
        txObject.setSessionHolder(null);
        txObject.setConnectionHolder((ConnectionHolder)null);
        final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.unbindResource(this.getResourceFactory());
        ConnectionHolder connectionHolder = null;
        if (this.getDataSource() != null) {
            connectionHolder = (ConnectionHolder)TransactionSynchronizationManager.unbindResource(this.getDataSource());
        }
        return new SuspendedResourcesHolder(sessionHolder, connectionHolder, null);
    }
    
    @Override
	protected void doResume(final Object transaction, final Object suspendedResources) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doResume call");
        }
        final SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder)suspendedResources;
        if (TransactionSynchronizationManager.hasResource(this.getResourceFactory())) {
            TransactionSynchronizationManager.unbindResource(this.getResourceFactory());
        }
        TransactionSynchronizationManager.bindResource(this.getResourceFactory(), resourcesHolder.getSessionHolder());
        if (this.getDataSource() != null) {
            TransactionSynchronizationManager.bindResource(this.getDataSource(), resourcesHolder.getConnectionHolder());
        }
    }
    
    @Override
	protected void doSetRollbackOnly(final DefaultTransactionStatus status) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doSetRollbackOnly call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)status.getTransaction();
        txObject.setRollbackOnly();
        if (status.isDebug()) {
            this.logger.debug("Setting Wrapper Jdbc transaction on Session [" + txObject.getSessionHolder().getSession() + "] rollback-only");
        }
    }
    
    @Override
	protected void doCleanupAfterCompletion(final Object transaction) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doCleanupAfterCompletion call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)transaction;
        if (txObject.isNewSessionHolder()) {
            TransactionSynchronizationManager.unbindResource(this.getResourceFactory());
        }
        if (this.getDataSource() != null) {
            TransactionSynchronizationManager.unbindResource(this.getDataSource());
        }
        final JdbcSession session = txObject.getSessionHolder().getSession();
        if (txObject.hasConnectionHolder()) {
            final Connection con = session.getConnection();
            DataSourceUtils.resetConnectionAfterTransaction(con, txObject.getPreviousIsolationLevel());
        }
        if (txObject.isNewSession()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Closing Wrapper Jdbc Session [" + session + "] after transaction");
            }
            JdbcSessionUtil.closeSession(session);
        }
        else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Not closing pre-bound Wrapper Jdbc Session [" + session + "] after transaction");
        }
        txObject.getSessionHolder().clear();
    }
    
    @Override
	protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("doRollback call");
        }
        final JdbcTransactionObject txObject = (JdbcTransactionObject)status.getTransaction();
        if (status.isDebug()) {
            this.logger.debug("Rolling back Wrapper Jdbc transaction on Session [" + txObject.getSessionHolder().getSession() + "]");
        }
        try {
            txObject.getSessionHolder().getTransaction().rollback();
        }
        catch (Exception ex) {
            throw new TransactionSystemException("Could not roll back Wrapper Jdbc transaction", ex);
        }
        finally {
            if (!txObject.isNewSession()) {
                txObject.getSessionHolder().getSession().clear();
            }
        }
        if (!txObject.isNewSession()) {
            txObject.getSessionHolder().getSession().clear();
        }
    }
    
    private class JdbcTransactionObject extends JdbcTransactionObjectSupport
    {
        private JdbcSessionHolder sessionHolder;
        private boolean newSessionHolder;
        private boolean newSession;
        
        public void setSession(final JdbcSession session) {
            this.sessionHolder = new JdbcSessionHolder(session);
            this.newSessionHolder = true;
            this.newSession = true;
        }
        
        public void setSessionHolder(final JdbcSessionHolder sessionHolder) {
            this.sessionHolder = sessionHolder;
            this.newSessionHolder = false;
            this.newSession = false;
        }
        
        public void setExistingSession(final JdbcSession session) {
            this.sessionHolder = new JdbcSessionHolder(session);
            this.newSessionHolder = true;
            this.newSession = false;
        }
        
        public JdbcSessionHolder getSessionHolder() {
            return this.sessionHolder;
        }
        
        public boolean isNewSessionHolder() {
            return this.newSessionHolder;
        }
        
        public boolean isNewSession() {
            return this.newSession;
        }
        
        public boolean hasSpringManagedTransaction() {
            return this.sessionHolder != null && this.sessionHolder.getTransaction() != null;
        }
        
        public void setRollbackOnly() {
            this.sessionHolder.setRollbackOnly();
            if (this.hasConnectionHolder()) {
                this.getConnectionHolder().setRollbackOnly();
            }
        }
        
        @Override
		public boolean isRollbackOnly() {
            return this.sessionHolder.isRollbackOnly() || (this.hasConnectionHolder() && this.getConnectionHolder().isRollbackOnly());
        }
    }
    
    private static class SuspendedResourcesHolder
    {
        private final JdbcSessionHolder sessionHolder;
        private final ConnectionHolder connectionHolder;
        
        private SuspendedResourcesHolder(final JdbcSessionHolder sessionHolder, final ConnectionHolder conHolder) {
            this.sessionHolder = sessionHolder;
            this.connectionHolder = conHolder;
        }
        
        private JdbcSessionHolder getSessionHolder() {
            return this.sessionHolder;
        }
        
        private ConnectionHolder getConnectionHolder() {
            return this.connectionHolder;
        }
    }
}
