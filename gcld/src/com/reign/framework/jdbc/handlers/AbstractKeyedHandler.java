package com.reign.framework.jdbc.handlers;

import com.reign.framework.jdbc.*;
import java.util.*;
import java.sql.*;

public abstract class AbstractKeyedHandler<K, V> implements ResultSetHandler<Map<K, V>>
{
    @Override
    public Map<K, V> handle(final ResultSet rs) throws SQLException {
        final Map<K, V> result = new LinkedHashMap<K, V>();
        while (rs.next()) {
            result.put(this.createKey(rs), this.createRow(rs));
        }
        return result;
    }
    
    protected abstract K createKey(final ResultSet p0) throws SQLException;
    
    protected abstract V createRow(final ResultSet p0) throws SQLException;
}
