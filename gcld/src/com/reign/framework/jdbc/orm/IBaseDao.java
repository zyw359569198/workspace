package com.reign.framework.jdbc.orm;

import java.io.*;
import com.reign.framework.jdbc.orm.page.*;
import java.util.*;
import com.reign.framework.jdbc.*;

public interface IBaseDao<T extends JdbcModel, PK extends Serializable>
{
    public static final String ALL_QUERY_CACHE = "all";
    
    void create(final T p0);
    
    void create(final T p0, final String... p1);
    
    T read(final PK p0);
    
    T readForUpdate(final PK p0);
    
    void update(final T p0);
    
    void update(final T p0, final String... p1);
    
    void delete(final PK p0);
    
    List<T> getModels();
    
    Long getModelSize();
    
    T getFirstResultByHQLAndParam(final String p0);
    
    T getFirstResultByHQLAndParam(final String p0, final Params p1);
    
    List<T> getResultByHQLAndParam(final String p0);
    
    List<T> getResultByHQLAndParam(final String p0, final Params p1);
    
    List<T> getResultByHQLAndParam(final String p0, final PagingData p1, final Params p2);
    
    void update(final String p0, final Params p1);
    
    int update(final String p0, final Params p1, final boolean p2);
    
    void update(final String p0, final Params p1, final PK p2, final String... p3);
    
    int update(final String p0, final Params p1, final boolean p2, final PK p3, final String... p4);
    
    long count(final String p0, final Params p1);
    
    void batch(final String p0, final List<List<Param>> p1);
    
    void batch(final String p0, final List<List<Param>> p1, final String... p2);
    
    boolean callProcedure(final String p0, final List<Param> p1);
    
    boolean callProcedure(final String p0, final List<Param> p1, final String... p2);
    
    List<Object> callProcedureWithReturn(final String p0, final List<Param> p1);
    
    List<Object> callProcedureWithReturn(final String p0, final List<Param> p1, final String... p2);
    
    List<Map<String, Object>> callQueryProcedure(final String p0, final List<Param> p1);
    
    List<Map<String, Object>> query(final String p0, final List<Param> p1);
    
    List<Map<String, Object>> query(final String p0, final PagingData p1, final Params p2);
    
     <E> E query(final String p0, final List<Param> p1, final ResultSetHandler<E> p2);
}
