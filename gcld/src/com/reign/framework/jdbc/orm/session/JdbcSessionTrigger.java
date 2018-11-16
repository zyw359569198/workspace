package com.reign.framework.jdbc.orm.session;

public interface JdbcSessionTrigger
{
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    
    void trigger();
}
