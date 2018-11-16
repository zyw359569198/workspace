package com.reign.framework.jdbc;

import java.sql.*;
import java.util.*;

public class BasicRowProcessor implements RowProcessor
{
    private static final BeanProcessor convert;
    
    static {
        convert = new BeanProcessor();
    }
    
    @Override
    public Object[] toArray(final ResultSet rs) throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        final int cols = meta.getColumnCount();
        final Object[] result = new Object[cols];
        for (int i = 0; i < cols; ++i) {
            result[i] = rs.getObject(i + 1);
        }
        return result;
    }
    
    @Override
    public <T> T toBean(final ResultSet rs, final Class<T> type) throws SQLException {
        return BasicRowProcessor.convert.toBean(rs, type);
    }
    
    @Override
    public <T> List<T> toBeanList(final ResultSet rs, final Class<T> type) throws SQLException {
        return BasicRowProcessor.convert.toBeanList(rs, type);
    }
    
    @Override
    public Map<String, Object> toMap(final ResultSet rs) throws SQLException {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        final ResultSetMetaData rsmd = rs.getMetaData();
        for (int cols = rsmd.getColumnCount(), i = 1; i <= cols; ++i) {
            result.put(rsmd.getColumnName(i), rs.getObject(i));
        }
        return result;
    }
}
