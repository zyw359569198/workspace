package com.reign.framework.jdbc.handlers;

import com.reign.framework.jdbc.*;
import java.sql.*;

public class ArrayHandler implements ResultSetHandler<Object[]>
{
    static final RowProcessor ROW_PROCESSOR;
    private final RowProcessor convert;
    
    static {
        ROW_PROCESSOR = new BasicRowProcessor();
    }
    
    public ArrayHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }
    
    public ArrayHandler(final RowProcessor convert) {
        this.convert = convert;
    }
    
    @Override
    public Object[] handle(final ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toArray(rs) : null;
    }
}
