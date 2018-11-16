package com.reign.framework.jdbc.handlers;

import java.util.*;
import com.reign.framework.jdbc.*;
import java.sql.*;

public class MapHandler implements ResultSetHandler<Map<String, Object>>
{
    private final RowProcessor convert;
    
    public MapHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }
    
    public MapHandler(final RowProcessor convert) {
        this.convert = convert;
    }
    
    @Override
    public Map<String, Object> handle(final ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toMap(rs) : null;
    }
}
