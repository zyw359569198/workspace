package com.reign.framework.jdbc.handlers;

import java.util.*;
import com.reign.framework.jdbc.*;
import java.sql.*;

public class BeanListHandler<T> implements ResultSetHandler<List<T>>
{
    private final RowProcessor convert;
    private final Class<T> type;
    
    public BeanListHandler(final Class<T> type) {
        this(type, ArrayHandler.ROW_PROCESSOR);
    }
    
    public BeanListHandler(final Class<T> type, final RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }
    
    @Override
    public List<T> handle(final ResultSet rs) throws SQLException {
        return this.convert.toBeanList(rs, this.type);
    }
}
