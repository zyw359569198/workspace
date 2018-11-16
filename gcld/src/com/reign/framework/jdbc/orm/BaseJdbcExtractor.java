package com.reign.framework.jdbc.orm;

import java.util.*;
import com.reign.framework.jdbc.*;

public interface BaseJdbcExtractor extends JdbcExtractor
{
     <T, PK> T read(final PK p0, final JdbcEntity p1, final ResultSetHandler<T> p2);
    
     <T, PK> void insert(final T p0, final JdbcEntity p1, final String... p2);
    
     <T> void update(final T p0, final JdbcEntity p1);
    
     <PK> void delete(final PK p0, final JdbcEntity p1);
    
     <T> List<T> query(final String p0, final String p1, final List<Param> p2, final JdbcEntity p3, final ResultSetHandler<List<T>> p4);
    
     <PK> int update(final String p0, final List<Param> p1, final JdbcEntity p2, final PK p3, final String... p4);
    
     <PK> void updateDelay(final String p0, final List<Param> p1, final JdbcEntity p2, final PK p3, final String... p4);
    
    void batch(final String p0, final List<List<Param>> p1, final JdbcEntity p2, final String... p3);
    
    boolean callProcedure(final String p0, final List<Param> p1, final JdbcEntity p2, final String... p3);
    
    List<Object> callProcedureWithReturn(final String p0, final List<Param> p1, final JdbcEntity p2, final String... p3);
}
