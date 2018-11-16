package com.reign.framework.jdbc.orm.transaction;

public interface TransactionListener
{
    void begin(final Transaction p0);
    
    void beforeCommit(final Transaction p0, final boolean p1);
    
    void commit(final Transaction p0, final boolean p1);
}
