package com.reign.framework.jdbc.handlers;

import com.reign.framework.jdbc.*;
import java.util.*;
import java.sql.*;

public abstract class AbstractListHandler<T> implements ResultSetHandler<List<T>>
{
    @Override
    public List<T> handle(final ResultSet rs) throws SQLException {
        final List<T> rows = new ArrayList<T>();
        while (rs.next()) {
            rows.add(this.handleRow(rs));
        }
        return rows;
    }
    
    public abstract T handleRow(final ResultSet p0) throws SQLException;
}
