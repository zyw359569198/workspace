package com.reign.gcld.phantom.common;

public class MyBatisSqlAdapter
{
    private String sql;
    
    public MyBatisSqlAdapter(final String sql) {
        this.sql = sql;
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public void setSql(final String sql) {
        this.sql = sql;
    }
}
