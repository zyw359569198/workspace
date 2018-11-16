package com.reign.framework.jdbc;

import java.util.*;

public interface JdbcExtractor
{
     <T> T query(final String p0, final List<Param> p1, final ResultSetHandler<T> p2);
    
    List<Map<String, Object>> query(final String p0, final List<Param> p1);
    
    int update(final String p0, final List<Param> p1);
    
    int insert(final String p0, final List<Param> p1, final boolean p2);
    
    void batch(final String p0, final List<List<Param>> p1);
    
    boolean callProcedure(final String p0, final List<Param> p1);
    
    List<Object> callProcedureWithReturn(final String p0, final List<Param> p1);
    
    List<Map<String, Object>> callQueryProcedure(final String p0, final List<Param> p1);
}
