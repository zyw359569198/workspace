package com.reign.framework.jdbc;

import java.sql.*;
import java.util.*;

public interface RowProcessor
{
    Object[] toArray(final ResultSet p0) throws SQLException;
    
     <T> T toBean(final ResultSet p0, final Class<T> p1) throws SQLException;
    
     <T> List<T> toBeanList(final ResultSet p0, final Class<T> p1) throws SQLException;
    
    Map<String, Object> toMap(final ResultSet p0) throws SQLException;
}
