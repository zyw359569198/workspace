package com.reign.framework.jdbc.handlers;

import com.reign.framework.jdbc.*;
import java.sql.*;

public class BeanHandler<T> implements ResultSetHandler<T>
{
    private final RowProcessor convert;
    private final Class<T> type;
    
    public BeanHandler(final Class<T> type) {
        this(type, ArrayHandler.ROW_PROCESSOR);
    }
    
    public BeanHandler(final Class<T> type, final RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }
    
    @Override
    public T handle(final ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toBean(rs, this.type) : null;
    }
}
