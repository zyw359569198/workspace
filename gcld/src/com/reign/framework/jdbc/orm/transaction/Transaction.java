package com.reign.framework.jdbc.orm.transaction;

public interface Transaction
{
    void begin();
    
    void commit();
    
    void rollback();
    
    boolean isActive();
}
