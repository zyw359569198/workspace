package com.reign.framework.jdbc.orm.session;

import java.sql.*;
import com.reign.framework.jdbc.orm.transaction.*;
import com.reign.framework.jdbc.orm.*;

public interface JdbcSession extends BaseJdbcExtractor
{
    Connection getConnection();
    
    Transaction getTransaction();
    
    boolean isClosed();
    
    void close();
    
    void evict(final String p0);
    
    void evictAll();
    
    void clear();
    
    JdbcFactory getJdbcFactory();
}
